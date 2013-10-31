package br.com.infowaypi.ecare.arquivos;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.FetchMode;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.segurados.Cartao;
import br.com.infowaypi.ecare.segurados.DependenteInterface;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.segurados.Titular;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus bOolean
 * @changes Thiago vøID
 */
public class ArquivoCartoes {
	
	public static final String TITULAR = "T";
	public static final String DEPENDENTE = "D";
	public static final String SEGUNDA_VIA = "S";
	public static final String DEPENDENTE_ORFAO = "P";
	
	private String path;
	private StringBuffer fileBody = new StringBuffer();
	Set<Cartao> cartoes = new HashSet<Cartao>(); 
	List<Cartao> cartoesTitulares;
	
	public ArquivoCartoes(List<Cartao> cartaoTitulares, String path) {
		this.cartoesTitulares = cartoesTitulares;
		this.path = path;
	}
	
	public ArquivoCartoes(List<Cartao> cartoesTitulares) {
		this.cartoesTitulares = cartoesTitulares;
		
	}

	public byte[] fileGenerate() throws Exception {
		int seguradosProcessados = 0;
		int titularesProcessados = cartoesTitulares.size();
		int dependentesProcessados = 0;
		
		
//		System.out.println("Inicio do processamento de segurados...");
		
		for (Cartao cartaoTitular : this.cartoesTitulares) {
			
			if(cartaoTitular.getSegurado().getDataAdesao() == null) {
//				System.out.println("Titular de CPF: " + titular.getPessoaFisica().getCpf()+ " possui data de adesão nula!");
				seguradosProcessados++;
				continue;
			}		
			
			seguradosProcessados++;

				
			
			cartaoTitular.getSegurado().setDataGeracaoDoCartao(new Date());
			cartoes.add(cartaoTitular.getSegurado().getCartaoAtual());
//			ImplDAO.save(cartaoTitular);
			
			fileBody.append(this.TITULAR);
			fileBody.append(";");
			fileBody.append(cartaoTitular.getNumeroDoCartao());
			fileBody.append(";");
			fileBody.append(cartaoTitular.getSegurado().getPessoaFisica().getNome().toUpperCase());
			fileBody.append(";");
			fileBody.append(Utils.format(cartaoTitular.getSegurado().getDataAdesao()));
			fileBody.append(";");
			fileBody.append(cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getLogradouro() == null ?"" :cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getLogradouro().toUpperCase());
			fileBody.append(";");
			fileBody.append(cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getNumero() == null ?"" :cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getNumero());
			fileBody.append(";");
			fileBody.append(cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getComplemento() == null ?"" :cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getComplemento().toUpperCase());
			fileBody.append(";");
			fileBody.append(cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getPontoDeReferencia() == null ?"" :cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getPontoDeReferencia().toUpperCase());
			fileBody.append(";");
			fileBody.append(cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getBairro() == null ?"" :cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getBairro().toUpperCase());
			fileBody.append(";");
			fileBody.append(cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getCep() == null ?"" :cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getCep());
			fileBody.append(";");
			fileBody.append(cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getMunicipio() == null ?"":cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getMunicipio().getDescricao().toUpperCase());
			fileBody.append(";");
			fileBody.append(System.getProperty("line.separator"));
			
			seguradosProcessados++;
			

			if(cartaoTitular.getSegurado().isSeguradoTitular() && !((Titular)cartaoTitular.getSegurado()).getDependentes().isEmpty()) {
				for (DependenteInterface dependente : ((Titular)cartaoTitular.getSegurado()).getDependentes()) {
					dependentesProcessados++;
					if(!dependente.isRecadastrado() || !dependente.isSituacaoAtual(SituacaoEnum.ATIVO.descricao())){
						seguradosProcessados++;
						continue;
					}
					
//					Assert.isNotNull(dependente.getCartaoAtual(), "ACHEI!!!!! CARTÂO DO TITULAR: "+cartaoTitular.getNumeroDoCartao());
					cartoes.add(dependente.getCartaoAtual());
					cartaoTitular.getSegurado().setDataGeracaoDoCartao(new Date());
//					ImplDAO.save(dependente);
					
					fileBody.append(this.DEPENDENTE);
					fileBody.append(";");
					fileBody.append(dependente.getNumeroDoCartao());
					fileBody.append(";");
					fileBody.append(dependente.getPessoaFisica().getNome());
					fileBody.append(";");
					if(dependente.getDataAdesao() != null) {
						fileBody.append(Utils.format(dependente.getDataAdesao()));
						fileBody.append(";");
//						System.out.println("Dependente de CPF: " + dependente.getPessoaFisica().getCpf()+ " possui data de adesão nula!");
					}
					else{
						fileBody.append(dependente.getTitular().getDataAdesao());
						fileBody.append(";");
					}
					fileBody.append(dependente.getTitular().getPessoaFisica().getEndereco().getLogradouro() == null? "": dependente.getTitular().getPessoaFisica().getEndereco().getLogradouro().toUpperCase());
					fileBody.append(";");
					fileBody.append(dependente.getTitular().getPessoaFisica().getEndereco().getNumero()== null ? "" : dependente.getTitular().getPessoaFisica().getEndereco().getNumero());
					fileBody.append(";");
					fileBody.append(dependente.getTitular().getPessoaFisica().getEndereco().getComplemento() == null ?"" :dependente.getTitular().getPessoaFisica().getEndereco().getComplemento().toUpperCase());
					fileBody.append(";");
					fileBody.append(dependente.getTitular().getPessoaFisica().getEndereco().getPontoDeReferencia() == null ?"" :dependente.getTitular().getPessoaFisica().getEndereco().getPontoDeReferencia().toUpperCase());
					fileBody.append(";");
					fileBody.append(dependente.getTitular().getPessoaFisica().getEndereco().getBairro()==null? "": dependente.getTitular().getPessoaFisica().getEndereco().getBairro().toUpperCase());
					fileBody.append(";");
					fileBody.append(dependente.getTitular().getPessoaFisica().getEndereco().getCep()==null ? "": dependente.getTitular().getPessoaFisica().getEndereco().getCep().toUpperCase());
					fileBody.append(";");
					fileBody.append(dependente.getTitular().getPessoaFisica().getEndereco().getMunicipio()== null?"": dependente.getTitular().getPessoaFisica().getEndereco().getMunicipio().getDescricao().toUpperCase());
					fileBody.append(";");
					fileBody.append(System.getProperty("line.separator"));
					seguradosProcessados++;
				}
			}
			
		}//for
		
		
		List<Cartao> cartoesSegundaVia = HibernateUtil.currentSession().createCriteria(Cartao.class)
		.add(Expression.gt("viaDoCartao", 1))
		.add(Expression.isNull("remessa"))
		.setFetchSize(100)
		.list();
		
		if(!cartoesSegundaVia.isEmpty()){
			for (Cartao cartaoTitular : cartoesSegundaVia) {
				cartaoTitular.getSegurado().setDataGeracaoDoCartao(new Date());
				cartoes.add(cartaoTitular.getSegurado().getCartaoAtual());
				fileBody.append(this.SEGUNDA_VIA);
				fileBody.append(";");
				fileBody.append(cartaoTitular.getNumeroDoCartao());
				fileBody.append(";");
				fileBody.append(cartaoTitular.getSegurado().getPessoaFisica().getNome().toUpperCase());
				fileBody.append(";");
				fileBody.append(Utils.format(cartaoTitular.getSegurado().getDataAdesao()));
				fileBody.append(";");
				fileBody.append(cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getLogradouro() == null ?"" :cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getLogradouro().toUpperCase());
				fileBody.append(";");
				fileBody.append(cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getNumero() == null ?"" :cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getNumero());
				fileBody.append(";");
				fileBody.append(cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getComplemento() == null ?"" :cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getComplemento().toUpperCase());
				fileBody.append(";");
				fileBody.append(cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getPontoDeReferencia() == null ?"" :cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getPontoDeReferencia().toUpperCase());
				fileBody.append(";");
				fileBody.append(cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getBairro() == null ?"" :cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getBairro().toUpperCase());
				fileBody.append(";");
				fileBody.append(cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getCep() == null ?"" :cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getCep());
				fileBody.append(";");
				fileBody.append(cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getMunicipio() == null ?"":cartaoTitular.getSegurado().getPessoaFisica().getEndereco().getMunicipio().getDescricao().toUpperCase());
				fileBody.append(";");
				fileBody.append(System.getProperty("line.separator"));
			}
		}
		
		List<Cartao> cartoesParaGeracao = HibernateUtil.currentSession().createCriteria(Cartao.class)
		.add(Expression.eq("viaDoCartao", 1))
		.add(Expression.isNull("remessa"))
		.setFetchMode("segurado", FetchMode.SELECT)
		.setFetchSize(100)
		.list();
		
		Set<Segurado> dependentesSemCartao = new HashSet<Segurado>();
		for (Cartao cartao : cartoesParaGeracao) {
			if(cartao.getSegurado().isSeguradoDependente() && cartao.getSegurado().getTitular().isSeguradoTitular() 
					&& cartao.getSegurado().getTitular().isSituacaoAtual(SituacaoEnum.ATIVO.descricao())
					&& cartao.getSegurado().isSituacaoAtual(SituacaoEnum.ATIVO.descricao())
					&& cartao.getSegurado().isRecadastrado()){
				Titular titular = cartao.getSegurado().getTitular();
				if(titular.getCartaoAtual() != null && titular.getCartaoAtual().isEnviado() && !cartao.isEnviado()){
					dependentesSemCartao.add(cartao.getSegurado());
					cartao.getSegurado().setDataGeracaoDoCartao(new Date());
					cartoes.add(cartao);
				}
			}
		}
		
		for (Segurado seguradoDependente : dependentesSemCartao) {
			fileBody.append(this.DEPENDENTE_ORFAO);
			fileBody.append(";");
			fileBody.append(seguradoDependente.getNumeroDoCartao());
			fileBody.append(";");
			fileBody.append(seguradoDependente.getPessoaFisica().getNome().toUpperCase());
			fileBody.append(";");
			fileBody.append(Utils.format(seguradoDependente.getDataAdesao()));
			fileBody.append(";");
			fileBody.append(seguradoDependente.getTitular().getPessoaFisica().getEndereco().getLogradouro() == null ?"" :seguradoDependente.getTitular().getPessoaFisica().getEndereco().getLogradouro().toUpperCase());
			fileBody.append(";");
			fileBody.append(seguradoDependente.getTitular().getPessoaFisica().getEndereco().getNumero() == null ?"" :seguradoDependente.getTitular().getPessoaFisica().getEndereco().getNumero());
			fileBody.append(";");
			fileBody.append(seguradoDependente.getTitular().getPessoaFisica().getEndereco().getComplemento() == null ?"" :seguradoDependente.getTitular().getPessoaFisica().getEndereco().getComplemento().toUpperCase());
			fileBody.append(";");
			fileBody.append(seguradoDependente.getTitular().getPessoaFisica().getEndereco().getPontoDeReferencia() == null ?"" :seguradoDependente.getTitular().getPessoaFisica().getEndereco().getPontoDeReferencia().toUpperCase());
			fileBody.append(";");
			fileBody.append(seguradoDependente.getTitular().getPessoaFisica().getEndereco().getBairro() == null ?"" :seguradoDependente.getTitular().getPessoaFisica().getEndereco().getBairro().toUpperCase());
			fileBody.append(";");
			fileBody.append(seguradoDependente.getTitular().getPessoaFisica().getEndereco().getCep() == null ?"" :seguradoDependente.getTitular().getPessoaFisica().getEndereco().getCep());
			fileBody.append(";");
			fileBody.append(seguradoDependente.getTitular().getPessoaFisica().getEndereco().getMunicipio() == null ?"":seguradoDependente.getTitular().getPessoaFisica().getEndereco().getMunicipio().getDescricao().toUpperCase());
			fileBody.append(";");
			fileBody.append(System.getProperty("line.separator"));
		}
		return this.fileBody.toString().getBytes();
	}
	
	public RemessaDeCartao getRemessaDeCartao() throws Exception{
		RemessaDeCartao remessa = new RemessaDeCartao();
		remessa.setConteudoArquivo(fileGenerate());
		remessa.setDataGeracao(new Date());
		remessa.setEnviado(false);
		remessa.addCartoes(cartoes);
		return remessa;
	}
	
	public Set<Cartao> getCartoes() {
		return cartoes;
	}
	
	public byte[] getConteudo(){
		return this.fileBody.toString().getBytes();
	}

}
