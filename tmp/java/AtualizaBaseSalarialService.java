package br.com.infowaypi.ecare.financeiro.consignacao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.hibernate.FlushMode;
import org.hibernate.Query;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.segurados.Empresa;
import br.com.infowaypi.ecare.segurados.Matricula;
import br.com.infowaypi.ecare.segurados.tuning.MatriculaTuning;
import br.com.infowaypi.ecare.segurados.tuning.TitularConsignacaoTuning;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;

/**
 * @author Marcus bOolean
 *
 */
public class AtualizaBaseSalarialService {

	public static byte[] conteudoErradoBytes;

	@SuppressWarnings("unchecked")
	public ResumoBaseSalarial informaArquivo(byte[] conteudo, Date competencia) throws Exception {

		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		
		//Inativa todas as matriculas do tipo folha, depois ativa somente as que encontram-se no arquivo.
		HibernateUtil.currentSession().createSQLQuery("update segurados_matricula set ativa = false where tipopagamento = 2").executeUpdate();

		Map<String, Set<AgrupamentoValores>> map = getValoresAgrupadosPorCpf(conteudo);

		String sql = "Select idSegurado, pessoaFisica.cpf from TitularConsignacaoTuning t";
		Query query = HibernateUtil.currentSession().createQuery(sql);
		
		List<Object[]> titulares = query.list();
		
		String sqlUpdate = "update segurados_matricula set ativa = true, salario = ? where idsegurado = ? and descricao = ? and idgrupo = ?";

		Map<String,Empresa> mapEmpresas = getMapEmpresas();
		int matriculasAlteradas = 0;
		int titularesProcessados = 0;

		int registrosAtualizados = 0;
		int matriculasInseridas = 0;
		try{
			for (Object[] titular : titulares) {
				Set<AgrupamentoValores> agrupamentos = map.get(titular[1]);
				if (agrupamentos != null){
					titularesProcessados++;
					for (AgrupamentoValores agrupamentoValores : agrupamentos) {
						matriculasAlteradas++;
						BigDecimal salario 		= agrupamentoValores.getSalario();
						Long idSegurado 		= (Long) titular[0];
						String codigoMatricula 	= agrupamentoValores.getCodigoMatricula();
						Long idGrupo 			= mapEmpresas.get(agrupamentoValores.getCodigoEmpresa()).getIdGrupo();
						
						registrosAtualizados = HibernateUtil.currentSession()
								.createSQLQuery(sqlUpdate)
								.setBigDecimal(0, salario)
								.setLong(1, idSegurado)
								.setString(2, codigoMatricula)
								.setLong(3, idGrupo).executeUpdate();
						
						if(registrosAtualizados == 0){
							TitularConsignacaoTuning segurado = ImplDAO.findById(idSegurado, TitularConsignacaoTuning.class);;
							MatriculaTuning  matricula = new MatriculaTuning();
							matricula.setEmpresa(mapEmpresas.get(agrupamentoValores.getCodigoEmpresa()));
							matricula.setDescricao(codigoMatricula);
							matricula.setSalario(salario);
							matricula.setAtiva(true);
							matricula.setTipoPagamento(Matricula.FORMA_PAGAMENTO_FOLHA);
							matricula.setSeguradoConsignacao(segurado);
							matriculasInseridas++;
							ImplDAO.save(matricula);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(MensagemErroEnumSR.ERRO_ATUALIZACAO_BASE_SALARIAL.getMessage());
		}

		ResumoBaseSalarial resumo = new ResumoBaseSalarial();
		resumo.setNumeroMatriculas(matriculasAlteradas);
		resumo.setNumeroTitulares(titularesProcessados);
		resumo.setNumeroMatriculasInseridas(matriculasInseridas);
		resumo.setConteudoErrado(conteudoErradoBytes);

		ArquivosConsignacoes arquivos = new ArquivosConsignacoes();
		arquivos.setArquivoDeBaseSalarial(conteudo);
		arquivos.setArquivoDelinhasErradasBaseSalarial(conteudoErradoBytes);
		arquivos.setCompetencia(competencia);
		ImplDAO.save(arquivos);

		return resumo;
	}

	private Map<String,Empresa> getMapEmpresas(){
		List<Empresa> empresas = HibernateUtil.currentSession().createCriteria(Empresa.class).list();
		Map<String, Empresa> mapEmpresas = new HashMap<String, Empresa>();

		for (Empresa empresa : empresas) {
			mapEmpresas.put(empresa.getCodigoLegado(), empresa);
		}

		return mapEmpresas;
	}

	public void conferirDados(ResumoBaseSalarial resumo) { }

	public static byte[] getConteudo(String path) throws Exception {
		InputStream out = new FileInputStream(path);
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

		while (out.available() > 0) {
			byteArray.write(out.read());
		}

		return byteArray.toByteArray();
	}

	public static Map<String, Set<AgrupamentoValores>> getValoresAgrupadosPorCpf(byte[] conteudo) throws Exception {

		Scanner sc = new Scanner(new ByteArrayInputStream(conteudo)); 
		AgrupamentoValores valoresPorCPF;
		Map<String, Set<AgrupamentoValores>> map = new HashMap<String, Set<AgrupamentoValores>>();
		StringBuffer conteudoErrado = new StringBuffer();

		while(sc.hasNext()) {
			String linhaIntacta = sc.nextLine();
			linhaIntacta = linhaIntacta.replace(" ", "");
			StringTokenizer linha = new StringTokenizer(linhaIntacta,";");
			String empresa = linha.nextToken().trim();
			String codigoMatricula = linha.nextToken().trim();
			String cpf = linha.nextToken().trim();
			cpf = aplicarMascaraCPF(cpf);
			String salario = linha.nextToken().trim();


			if(isLinhaArquivoBaseValida(linhaIntacta)) {
				valoresPorCPF = new AgrupamentoValores();
				valoresPorCPF.setCodigoEmpresa(empresa);
				valoresPorCPF.setCodigoMatricula(codigoMatricula);
				valoresPorCPF.setSalario(salario);
				valoresPorCPF.setCpf(cpf);

				if(map.keySet().contains(cpf)) {
					map.get(cpf).add(valoresPorCPF);
				}else{
					Set<AgrupamentoValores> set = new HashSet<AgrupamentoValores>();
					set.add(valoresPorCPF);
					map.put(cpf, set);
				}

			}else{
				conteudoErrado.append(linhaIntacta);
				conteudoErrado.append(System.getProperty("line.separator"));
			}
		}
		conteudoErradoBytes = conteudoErrado.toString().getBytes();
		return map;
	}

	public static String aplicarMascaraCPF(String string) {
		StringBuffer buff = new StringBuffer(string);
		StringBuffer stringReturn = new StringBuffer(buff.substring(0, 3));
		stringReturn.append(".");
		stringReturn.append(buff.substring(3, 6));
		stringReturn.append(".");
		stringReturn.append(buff.substring(6, 9));
		stringReturn.append("-");
		stringReturn.append(buff.substring(9));
		return stringReturn.toString();

	}

	private static boolean isLinhaArquivoBaseValida(String linha) throws Exception {

		if(!StringUtils.isNumeric(linha.replace(";", "").trim())){
			return false;
		}else{
			return true;
		}
	}
}
