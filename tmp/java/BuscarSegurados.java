package br.com.infowaypi.ecare.services;

import static br.com.infowaypi.msr.utils.Utils.isStringVazia;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.enums.MotivoEnumSR;
import br.com.infowaypi.ecare.segurados.DependenteSR;
import br.com.infowaypi.ecare.segurados.DependenteSuplementar;
import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.segurados.SeguradoBasico;
import br.com.infowaypi.ecare.segurados.Titular;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.NotEquals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings("unchecked")
public class BuscarSegurados {

	public static ResumoSegurados buscar(String cpfDoTitular,String numeroDoCartao, Class<? extends Segurado> tipoSegurado, boolean liberarSuspensos) {

		if(Utils.isStringVazia(numeroDoCartao) && Utils.isStringVazia(cpfDoTitular)){
			throw new RuntimeException(MensagemErroEnum.AUSENCIA_DE_PARAMETROS.getMessage());
		} else if(!Utils.isStringVazia(cpfDoTitular) && !Utils.isCpfValido(cpfDoTitular)){
			throw new RuntimeException(MensagemErroEnum.PESSOA_FISICA_COM_CPF_INVALIDO.getMessage());
		}
	
		List<Segurado> segurados = new ArrayList<Segurado>(getSegurados(numeroDoCartao, cpfDoTitular, tipoSegurado, liberarSuspensos));
		
		if ((segurados == null || segurados.isEmpty()) && ((isStringVazia(cpfDoTitular)) && (!isStringVazia(numeroDoCartao)))){
			throw new RuntimeException(MensagemErroEnum.CARTAO_SEGURADO_NAO_ENCONTRADO.getMessage(numeroDoCartao));
		} else if((segurados == null || segurados.isEmpty()) && ((!isStringVazia(cpfDoTitular)) && (isStringVazia(numeroDoCartao)))){
			throw new RuntimeException(MensagemErroEnum.CPF_SEGURADO_NAO_ENCONTRADO.getMessage(cpfDoTitular));
		} else if((segurados == null || segurados.isEmpty()) && ((!isStringVazia(cpfDoTitular)) && (!isStringVazia(numeroDoCartao)))){
			throw new RuntimeException(MensagemErroEnum.SEGURADO_NAO_ENCONTRADO.getMessage(numeroDoCartao, cpfDoTitular));
		}
		
		updateInicioCarencia(segurados);
		
		return new ResumoSegurados(segurados);
	}
	
	public static ResumoSegurados buscarReimpressao(String cpfDoTitular,String numeroDoCartao, 
			Class<? extends Segurado> tipoSegurado) {
		
		if(Utils.isStringVazia(numeroDoCartao) && Utils.isStringVazia(cpfDoTitular)){
			throw new RuntimeException(MensagemErroEnum.AUSENCIA_DE_PARAMETROS.getMessage());
		} else if(!Utils.isStringVazia(cpfDoTitular) && !Utils.isCpfValido(cpfDoTitular)){
			throw new RuntimeException(MensagemErroEnum.PESSOA_FISICA_COM_CPF_INVALIDO.getMessage());
		}
	
		List<Segurado> segurados = new ArrayList<Segurado>(getSeguradosReimpressao(numeroDoCartao, cpfDoTitular, tipoSegurado));
		
		if((segurados == null || segurados.isEmpty()) && ((isStringVazia(cpfDoTitular)) && (!isStringVazia(numeroDoCartao)))){
			throw new RuntimeException(MensagemErroEnum.CARTAO_SEGURADO_NAO_ENCONTRADO.getMessage(numeroDoCartao));
		} else if((segurados == null || segurados.isEmpty()) && ((!isStringVazia(cpfDoTitular)) && (isStringVazia(numeroDoCartao)))){
			throw new RuntimeException(MensagemErroEnum.CPF_SEGURADO_NAO_ENCONTRADO.getMessage(cpfDoTitular));
		} else if((segurados == null || segurados.isEmpty()) && ((!isStringVazia(cpfDoTitular)) && (!isStringVazia(numeroDoCartao)))){
			throw new RuntimeException(MensagemErroEnum.SEGURADO_NAO_ENCONTRADO.getMessage(numeroDoCartao, cpfDoTitular));
		}
		
		updateInicioCarencia(segurados);
		
		return new ResumoSegurados(segurados);
	}

	private static void updateInicioCarencia(List<Segurado> segurados) {
		
		/* if_not[ATUALIZAR_CARENCIA_NO_ATENDIMENTO]*/
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		tx.begin();
		for (Segurado segurado : segurados) {
			if(segurado.getInicioDaCarencia() == null){
				segurado.updateDataInicioCarencia();
				HibernateUtil.currentSession().update(segurado);
			}
		}
//		try{
//			tx.commit();
//		}catch (Exception e) {
//			tx.rollback();
//		}
		/* end[ATUALIZAR_CARENCIA_NO_ATENDIMENTO]*/
	}
	/**
	 * Método utilizado na busca de segurados pelo CPF ou cartão do titular. Também busca segurados suspensos. 
	 * @param <S>
	 * @param cartao
	 * @param cpf
	 * @param tipoSegurado
	 * @param inclueSuspenso
	 * @return
	 */
	public static <S extends Segurado> List<S> getSegurados(String cartao,String cpf,Class<S> tipoSegurado, boolean inclueSuspenso) {

		if (!Utils.isStringVazia(cartao)){
			List<S> segurados = new ArrayList<S>();
			segurados.add((S) buscarPorCartao(cartao,tipoSegurado, inclueSuspenso));
			return segurados;
		}
		
		List<S> segurados = buscaPorCpf(cpf, tipoSegurado, inclueSuspenso, false);
		
		for(Segurado segurado : segurados){
			inativaDependentesForaDaIdadePermitida(segurado);
		}
		
		return segurados;
	}
	
	public static <S extends Segurado> S getSegurado(String cartao,String cpf,Class<S> tipoSegurado, boolean liberarSuspensos) throws ValidateException {
		
		if (!Utils.isStringVazia(cartao)){
			return (S) buscarPorCartao(cartao,tipoSegurado, liberarSuspensos);
		}
		
		return buscaPorCpfSegurado(cpf, tipoSegurado, liberarSuspensos);
	}
	
	protected static <S extends Segurado> List<S> getSeguradosReimpressao(String cartao,String cpf,
			Class<S> tipoSegurado) {

		if (!Utils.isStringVazia(cartao)){
			return buscarPorCartaoReimpressao(cartao,tipoSegurado);
		}
		
		List<S> segurados = buscaPorCpf(cpf, tipoSegurado, true, true);
		
		for(Segurado segurado : segurados){
			inativaDependentesForaDaIdadePermitida(segurado);
		}
		
		return segurados;
	}
	
	/**
	 * Buscar o segurado titular e todos os seus dependentes
	 */
	public static <S extends Segurado> List<S> buscaPorCpf(String cpfDoTitular, Class<S> tipoSegurado, boolean inclueSuspensos, boolean inclueCancelados) {
		
		SearchAgent sa = new SearchAgent();
		sa.clearAllParameters();
		sa.addParameter(new Equals("pessoaFisica.cpf", cpfDoTitular));
		
		List<S> segurados = sa.list(tipoSegurado);
		carregaDependentes(segurados);

		boolean incluirApenasAtivo = !inclueSuspensos && !inclueCancelados;
		
		segurados = filtraSegurados(segurados, incluirApenasAtivo, inclueSuspensos,inclueCancelados);
		
		
		return segurados;
	}

	/**
	 * Filtra os segurados de acordo com as flags passadas.
	 * @param <S>
	 * @param segurados
	 * @param incluirApenasAtivo
	 * @param incluirSuspenso
	 * @return
	 */
	private static <S extends Segurado> List<S>  filtraSegurados(List<S> segurados, boolean incluirApenasAtivo, boolean incluirSuspenso,boolean inclueCancelados) {
		List<S> seguradosFiltrados = new ArrayList<S>();
		
		for (S segurado : segurados) {

			ImplColecaoSituacoesComponent colecaoSituacao = null;
			colecaoSituacao = segurado;
			/* if[ATENDIMENTO_BASEADO_NA_SITUACAO_CONTRATO]
			colecaoSituacao = segurado.getContratoAtual();
			end[ATENDIMENTO_BASEADO_NA_SITUACAO_CONTRATO]*/
			
			boolean isSeguradoAtivo = colecaoSituacao.isSituacaoAtual(SituacaoEnum.ATIVO.descricao());
			boolean isSeguradoSuspensoPorRecadastramento = colecaoSituacao.isSituacaoAtual(SituacaoEnum.SUSPENSO.descricao()) &&
				colecaoSituacao.getSituacao().getMotivo().equals(MotivoEnum.BENEFICIARIO_NAO_RECADASTRADO.getMessage());
			
			boolean isSeguradoSuspensoPorOutroMotivo = colecaoSituacao.isSituacaoAtual(SituacaoEnum.SUSPENSO.descricao()) &&
				!colecaoSituacao.getSituacao().getMotivo().equals(MotivoEnum.BENEFICIARIO_NAO_RECADASTRADO.getMessage());
			
			
			boolean isSeguradoCancelado = colecaoSituacao.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
			
			if(incluirApenasAtivo && (isSeguradoAtivo || isSeguradoSuspensoPorRecadastramento)){
				seguradosFiltrados.add(segurado);
				
			}else{
				if(incluirSuspenso && !inclueCancelados ){
					 if (isSeguradoAtivo || isSeguradoSuspensoPorRecadastramento ||isSeguradoSuspensoPorOutroMotivo){
						 seguradosFiltrados.add(segurado);
					 }
				}
				
				if(incluirSuspenso && inclueCancelados ){
					 if (isSeguradoAtivo || isSeguradoSuspensoPorRecadastramento ||isSeguradoSuspensoPorOutroMotivo || isSeguradoCancelado){
						 seguradosFiltrados.add(segurado);
					 }
				}
				
				if(!incluirSuspenso && inclueCancelados ){
					 if (isSeguradoAtivo || isSeguradoSuspensoPorRecadastramento || isSeguradoCancelado){
						 seguradosFiltrados.add(segurado);
					 }
				}
				
			}
		}
		return seguradosFiltrados;
	}
	
	private static <S extends Segurado> void carregaDependentes(List<S> segurados) {
		Titular titular;
		List<S> dependetes = new ArrayList<S>();
		for (S s : segurados) {
			if (!segurados.isEmpty() && Titular.class.isInstance(s)) {
				titular = (Titular) s;
				
				Set<DependenteSR> dependentes = titular.getDependentes();
				for (DependenteSR dependente : dependentes) {
					dependetes.add((S) dependente);
				}
				
				for (DependenteSuplementar dependente : titular.getDependentesSuplementares()) {
					dependetes.add((S) dependente);
				}
			}
		}
		segurados.addAll(dependetes);
	}
	
	/**
	 * Buscar um único segurado ativo com o CPF.
	 * @throws ValidateException 
	 */
	private static <S extends Segurado> S buscaPorCpfSegurado(String cpf,
			Class<S> tipoSegurado, boolean liberarSuspensos) throws ValidateException {
		
		List<S> segurados;
		SearchAgent sa = new SearchAgent();
		sa.clearAllParameters();
		sa.addParameter(new Equals("pessoaFisica.cpf", cpf));
		sa.addParameter(new NotEquals("situacao.descricao", SituacaoEnum.CANCELADO.descricao()));
		
		segurados = sa.list(tipoSegurado);
		
		if(segurados.isEmpty()){
			throw new ValidateException("Não foi encontrado segurado com o CPF informado.");
		}
		
		for (S segurado : segurados) {
			validaSituacao(segurado, liberarSuspensos, false);
		}
		
		return segurados.get(0);
	}
	
	private static <S extends Segurado> List<S> buscarPorCartaoReimpressao(String numeroCartao,Class tipoSegurado) {
		
		S segurado = buscarSeguradoPorCartao(numeroCartao, tipoSegurado);
		
		Assert.isNotNull(segurado, MensagemErroEnum.CARTAO_SEGURADO_NAO_ENCONTRADO.getMessage(numeroCartao));
		
		inativaDependentesForaDaIdadePermitida(segurado);
		
		List<S> segurados = new ArrayList<S>();
		segurados.add(segurado);
		
		return segurados;
	}

	private static <S extends Segurado> S buscarPorCartao(String numeroCartao, Class tipoSegurado, boolean liberarSuspensos) {

		S segurado = buscarSeguradoPorCartao(numeroCartao, tipoSegurado);
		
		Assert.isNotNull(segurado, MensagemErroEnum.CARTAO_SEGURADO_NAO_ENCONTRADO.getMessage(numeroCartao));
		
		validaSituacao(segurado, liberarSuspensos, false);
		
		inativaDependentesForaDaIdadePermitida(segurado);
		
		return segurado;
	}

	private static <S> S buscarSeguradoPorCartao(String numeroCartao, Class tipoSegurado) {
		
		S segurado = null;
		/* if[VERIFICAR_NUMERO_CARTAO_BASEADO_NO_CONTRATO]
		segurado = buscarPorNumeroCartaoContratoSR(numeroCartao, tipoSegurado);
		else[VERIFICAR_NUMERO_CARTAO_BASEADO_NO_CONTRATO]*/
		
		segurado = buscarPorNumeroCartaoDoSegurado(numeroCartao, tipoSegurado);
		/* end[VERIFICAR_NUMERO_CARTAO_BASEADO_NO_CONTRATO]*/
		return segurado;
	}
	
	/**
	 * Buscar um segurado a partir do numero do cartão do segurado.
	 * @param tipoSegurado 
	 * @return Segurado
	 */
	private static <S> S buscarPorNumeroCartaoDoSegurado(String numeroCartao, Class tipoSegurado) {
		
		Criteria criteria = HibernateUtil.currentSession().createCriteria(tipoSegurado)
							.add(Expression.eq("numeroDoCartao", numeroCartao));
		
		S segurado = (S) criteria.uniqueResult();
		return segurado;
	}

	/**
	 * Buscar um segurado a partir do numero do cartão do contrato.
	 * @param tipoSegurado 
	 * @return Segurado
	 */
	private static <S> S buscarPorNumeroCartaoContratoSR(String numeroCartao, Class tipoSegurado) {
		String cartaoSemMascara = numeroCartao.replace(".", "").replace("-", "");
		
		String hql = "Select idSegurado from ContratoSR c where c.numeroCartaoAtual = :cartao ";
		
		Query query = HibernateUtil.currentSession().createQuery(hql).setString("cartao", cartaoSemMascara);
		
		Long idSegurado = (Long) query.uniqueResult();
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("idSegurado", idSegurado));

		return (S) sa.uniqueResult(tipoSegurado);
	}
	
	public static void validaSituacao(Segurado segurado, boolean liberarSuspensos, boolean liberarCancelados) {

		ImplColecaoSituacoesComponent colecaoSituacoes = null;
		colecaoSituacoes = segurado;
		
		/* if[ATENDIMENTO_BASEADO_NA_SITUACAO_CONTRATO]
		colecaoSituacoes = segurado.getContratoAtual();
		end[ATENDIMENTO_BASEADO_NA_SITUACAO_CONTRATO]*/
		
		boolean isSeguradoAtivo = colecaoSituacoes.isSituacaoAtual(SituacaoEnum.ATIVO.descricao());
		boolean isSeguradoSuspensoPorRecadastramento = colecaoSituacoes.isSituacaoAtual(SituacaoEnum.SUSPENSO.descricao()) &&
			colecaoSituacoes.getSituacao().getMotivo().equals(MotivoEnum.BENEFICIARIO_NAO_RECADASTRADO.getMessage());
		if(!liberarCancelados){
			if(liberarSuspensos){
				if(colecaoSituacoes.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())){
					throw new RuntimeException(MensagemErroEnum.SEGURADO_INATIVO_NO_SISTEMA.getMessage());
				}
			} else {
				if(!isSeguradoAtivo && !isSeguradoSuspensoPorRecadastramento){
					throw new RuntimeException(MensagemErroEnum.SEGURADO_INATIVO_NO_SISTEMA.getMessage());
				}
			}
		}
	}

	private static void inativaDependentesForaDaIdadePermitida(Segurado segurado){
		/* if_not[IDADE_DEPENDENTE_ILIMITADA]*/
		if(segurado.isSeguradoDependente() && !segurado.isSeguradoDependenteSuplementar() && segurado.isSituacaoAtual(SituacaoEnum.ATIVO.descricao())){
			
			DependenteSR dependente = ImplDAO.findById(segurado.getIdSegurado(), DependenteSR.class);
			int idade = dependente.getPessoaFisica().getIdade();
			
			boolean idadeSuperiorA21Anos = dependente.getTipoDeDependencia().equals(SeguradoBasico.TIPO_FILHO_MENOR_Q_21) && idade >= 21;
			boolean idadeSuperiorA25Anos = dependente.getTipoDeDependencia().equals(SeguradoBasico.TIPO_FILHO_MENOR_25_ANOS) && idade >= 25;
			
			if(idadeSuperiorA21Anos || idadeSuperiorA25Anos){
				dependente.mudarSituacao(null, SituacaoEnum.SUSPENSO.descricao(),MotivoEnumSR.DEPENDENTE_COM_IDADE_SUPERIOR_A_PERMITIDA.getMessage(), new Date());
			}
			
			Transaction tx = HibernateUtil.currentSession().beginTransaction();
			try{
				tx.begin();
				ImplDAO.save(dependente);
				tx.commit();
			}catch (Exception e) {
				tx.rollback();
			}
		}
		/* end[IDADE_DEPENDENTE_ILIMITADA]*/
	}

	public static ResumoSegurados buscar(String cpfDoTitular, String numeroDoCartao, Class<? extends Segurado> tipoSegurado) {
		return buscar(cpfDoTitular, numeroDoCartao, tipoSegurado, false);
	}
}
