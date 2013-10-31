/**
 * 
 */
package br.com.infowaypi.ecare.services;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.associados.ResumoProfissionais;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * @author Marcus Vinicius
 *
 */
public class ImportarArquivoCREMEPEService {
	public static final String CARACTERE_INVALIDO_1= " - ";
	public static final String CARACTERE_INVALIDO_2= ",";
	public static final String PONTO_E_VIRGULA = ";";
	public static final String ATIVO = "Ativo";
	private static final Object QUEBRA_DE_LINHA = System.getProperty("line.separator");
	
	public ResumoProfissionais informarArquivo(byte [] conteudoArquivo, UsuarioInterface usuario) throws Exception {
		if(conteudoArquivo.length == 0)
			throw new ValidateException("Selecione um arquivo!");
		
		Scanner sc = new Scanner(new ByteArrayInputStream(conteudoArquivo));
		
		List<Profissional> profissionaisCriados = new ArrayList<Profissional>();
		try {
			profissionaisCriados = processaValores(processaArquivo(formataArquivo(sc)), usuario);
		} catch (Exception e) {
			throw new ValidateException ("Arquivo Inválido.");
		}
		
		Assert.isNotEmpty(profissionaisCriados, "Não há profissionais a serem importados.");
		ResumoProfissionais resumo  = new ResumoProfissionais();
		resumo.setProfissionaisEncontrados(new HashSet<Profissional>(profissionaisCriados));
		return resumo;
	}
	
	public ResumoProfissionais salvar(ResumoProfissionais resumo) throws Exception {
		for (Profissional profissional : resumo.getProfissionaisEncontrados()) {
			ImplDAO.save(profissional);
		}
		
		return resumo;
	}
	
	public void finalizar (ResumoProfissionais resumo) {};

	private List<Profissional> processaValores(Set<Valor> valores, UsuarioInterface usuario) {
		
		Set<String> crmsDoArquivo = new HashSet<String>();
		
		for (Valor valor : valores) {
			crmsDoArquivo.add(valor.getCrm());
		}
		
		List<Profissional> profissionais = new ArrayList<Profissional>();
		
		SearchAgent sa= new SearchAgent();
		sa.addParameter(new In("crm",crmsDoArquivo));
		profissionais = sa.list(Profissional.class);
		
		for (Profissional profissional : profissionais) {
			crmsDoArquivo.remove(profissional.getCrm());
		}
		
		//limpa a coleçao para reuso
		profissionais.clear();
		
		for (Valor valor : valores) {
			if(crmsDoArquivo.contains(valor.getCrm())) {
				Profissional profissional = new Profissional();
				profissional.getPessoaFisica().setNome(valor.getNome());
				profissional.setCrm(valor.getCrm());
				profissional.setConselho("CRM");
				profissional.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), "Profissional cadastrado AUTOMATICAMENTE via importação do arquivo do CREMEPE.", new Date());
				profissional.setCrmNome(profissional.getPessoaFisica().getNome() + " - " + profissional.getCrm());
				
				if(!valor.getEspecialidades().isEmpty()) {
					sa.clearAllParameters();
					sa.addParameter(new In("descricao", valor.getEspecialidades()));
					List<Especialidade> especialidades = sa.list(Especialidade.class);
					profissional.setEspecialidades(new HashSet<Especialidade>(especialidades));
				}
				
				profissionais.add(profissional);
				
			}
		}
		
		return profissionais;
	}

	private Set<Valor> processaArquivo(StringBuffer buffer) throws ValidateException {
		Scanner sc = new Scanner(buffer.toString());
		Set<Valor> valores = new HashSet<Valor>();
		
		while (sc.hasNext()) {
			
			StringTokenizer tokenizer = new StringTokenizer(sc.nextLine(),";");
			Valor valor = new Valor();
			valor.setCrm(tokenizer.nextToken());
			
			//pula o estado
			tokenizer.nextToken();
			
			valor.setNome(tokenizer.nextToken());
			
			//pula inscricao
			tokenizer.nextToken();
			//pula situacao
			tokenizer.nextToken();
			
			while (tokenizer.hasMoreTokens()) {
				valor.addEspecialidade(tokenizer.nextToken());
			}
			
			valores.add(valor);
		}
	
		return valores;
	}

	private StringBuffer formataArquivo(Scanner sc) {
		StringBuffer buffer = new StringBuffer();
		while(sc.hasNext()) {
			String linha = sc.nextLine();
			
			if(linha.contains(ATIVO)) {
				String linhaSemHifem = linha.replace(CARACTERE_INVALIDO_1, PONTO_E_VIRGULA);
				String linhaSemVirgulas = linhaSemHifem.replace(CARACTERE_INVALIDO_2, PONTO_E_VIRGULA);
				
				buffer.append(linhaSemVirgulas);
				buffer.append(QUEBRA_DE_LINHA);
			}
		}
		return buffer;
	}
	
	public class Valor {
		String crm;
		String nome;
		Set<String> especialidades = new HashSet<String>();
		
		public String getCrm() {
			return crm;
		}
		public void setCrm(String crm) {
			this.crm = crm;
		}
		public String getNome() {
			return nome;
		}
		public void setNome(String nome) {
			this.nome = nome;
		}
		public Set<String> getEspecialidades() {
			return especialidades;
		}
		public void setEspecialidades(Set<String> especialidades) {
			this.especialidades = especialidades;
		}
		
		public void addEspecialidade (String especialidade) {
			this.especialidades.add(especialidade);
		}
		
	}

}
