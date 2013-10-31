
package br.com.infowaypi.ecare.services.cadastros;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.hibernate.SQLQuery;
import org.hibernate.Transaction;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.segurados.Dependente;
import br.com.infowaypi.ecare.segurados.Empresa;
import br.com.infowaypi.ecare.segurados.Matricula;
import br.com.infowaypi.ecare.segurados.Pensionista;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.enums.TipoSeguradoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service do Fluxo Atualizar Dependentes (MigracaoDependentes.jhm.xml) da aba Cadastro do role Central de Servicos que tem a função de realizar a migração de 
 * DEPENDENTES para DEPENDENTES SUPLEMENTARES e de DEPENDENTES para PENSIONISTAS.
 * @author Thiago Franco
 * @changes Josino Rodrigues
 *
 */
public class MigrarDependentes {
	
	public static final Integer TIPO_DEPENDENTE_SUPLEMENTAR = 1;
	public static final Integer TIPO_PENSIONISTA = 2;
	
	/**
	 * Método padrão de busca de Segurados no sistema.
	 * @param cpf
	 * @param numeroCartao
	 * @return
	 * @throws ValidateException
	 */
	public Dependente buscarDependente(String cpf, String numeroCartao) throws ValidateException{
		SearchAgent sa = new SearchAgent();
		
		if(Utils.isStringVazia(numeroCartao) && Utils.isStringVazia(cpf))
			throw new ValidateException(MensagemErroEnum.AUSENCIA_DE_PARAMETROS.getMessage());

		if(!Utils.isStringVazia(numeroCartao))
			sa.addParameter(new Equals("numeroDoCartao", numeroCartao));
		
		if(!Utils.isStringVazia(cpf))
			sa.addParameter(new Equals("pessoaFisica.cpf", cpf));
		
		Dependente dependente  = sa.uniqueResult(Dependente.class);
		
		if(dependente == null || !dependente.getTipoDeSegurado().equals(TipoSeguradoEnum.DEPENDENTE.descricao()))
			throw new ValidateException(MensagemErroEnum.BENEFICIARIO_NAO_ENCONTRADO.getMessage());
		dependente.getTitular().getDependentes().size();
		dependente.getTitular().getPensionistas().size();
		dependente.getTitular().tocarObjetos();
		return dependente;
	}
	
	/**
	 * 2ºPasso - Nesse passo é feita a escolha do tipo (DEPENDENTE SUPLEMENTAR ou PENSIONISTA) para qual o beneficiário irá migrar.
	 *	É realizado também os validates para a migração seguindo as regras do saúde recife.  
	 * @param tipoBeneficiario
	 * @param dependente
	 * @return
	 * @throws ValidateException
	 */
	public ResumoMigracaoDependentes selecionarTipo(Integer tipoBeneficiario,String cpf, 
			Empresa empresa, String codigo,Integer tipoPagamento, BigDecimal salario,
			Dependente dependente,UsuarioInterface usuario) throws ValidateException{
		
		boolean isDependenteAtivo = dependente.isSituacaoAtual(SituacaoEnum.ATIVO.descricao());
		boolean isDependenteSuspenso = dependente.isSituacaoAtual(SituacaoEnum.SUSPENSO.descricao());
		boolean isDependenteSemGrauDeDependencia = dependente.getTipoDeDependencia() == 0;
		boolean isUltimoDependenteAtivo = (dependente.getTitular().getDependentes().size() == 1 && (isDependenteAtivo || isDependenteSuspenso));
		boolean isTitularCancelado = dependente.getTitular().isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());

		boolean isBeneficiarioPensionsita = tipoBeneficiario.equals(this.TIPO_PENSIONISTA);
		if(isBeneficiarioPensionsita && isDependenteSemGrauDeDependencia)
			throw new ValidateException(MensagemErroEnum.DEPENDENTE_SEM_GRAU_DE_PARENTESCO.getMessage());

		if(isBeneficiarioPensionsita && isUltimoDependenteAtivo){
				if(!dependente.getTipoDeDependencia().equals(Constantes.TIPO_CONJUGUE) && 
						!dependente.getTipoDeDependencia().equals(Constantes.TIPO_FILHO_EXCEPCIONAL_INVALIDO) &&
						!dependente.getTipoDeDependencia().equals(Constantes.TIPO_FILHO_MENOR_25_ANOS) &&
						!dependente.getTipoDeDependencia().equals(Constantes.TIPO_FILHO_MENOR_Q_21))
					throw new ValidateException(MensagemErroEnum.DEPENDENTE_NAO_APTO_A_SER_PENSIONISTA.getMessage());
				
				if(!isTitularCancelado)
					dependente.getTitular().mudarSituacao(usuario, SituacaoEnum.CANCELADO.descricao(), MotivoEnum.CANCELADO_OBITO.getMessage(), new Date());
		}
		
		if(cpf != null)
			dependente.getPessoaFisica().setCpf(cpf);
			
		Matricula matricula = new Matricula();
		matricula.setAtiva(true);
		matricula.setEmpresa(empresa);
		matricula.setDescricao(codigo);
		matricula.setTipoPagamento(tipoPagamento);
		matricula.setSalario(salario);
		
		ResumoMigracaoDependentes resumo = new ResumoMigracaoDependentes();
		resumo.setDependente(dependente);
		resumo.setMatricula(matricula);
		resumo.setTipoBeneficiarioSelecionado(tipoBeneficiario);
		return resumo;
	}
	
	/**
	 * Método temporário para migração de dependentes (MIGRA APENAS SUPLEMENTARES)
	 * @param tipoBeneficiario
	 * @param cpf
	 * @param dependente
	 * @param usuario
	 * @return
	 * @throws ValidateException
	 */
	public ResumoMigracaoDependentes selecionarTipo(Integer tipoBeneficiario,String cpf, 
			Dependente dependente,UsuarioInterface usuario) throws ValidateException{
		return selecionarTipo(tipoBeneficiario, cpf, null, null, null, null, dependente, usuario);
		
	}
	
	/**
	 * 3º Passo - Mudança de tipo do beneficiário. Muda-se o discriminator via SQL e é adicionada uma situação na coleção de situações
	 * do beneficiário. Nesse passo são exibidas informações do beneficiário.
	 * @param dependente
	 * @return
	 * @throws Exception
	 */
	public Segurado conferirDados(ResumoMigracaoDependentes resumo, UsuarioInterface usuario) throws Exception{
		Transaction transaction = HibernateUtil.currentSession().beginTransaction();
		Dependente dependente = resumo.getDependente(); 
		
		String update = "";
		if(resumo.getTipoBeneficiarioSelecionado().equals(this.TIPO_DEPENDENTE_SUPLEMENTAR)){
			update = "update segurados_segurado set tiposegurado = 'DS', idTitularSuplementar = ?,idTitular = null, diaBase = 1  where numerodocartao = ?";
		}else if (resumo.getTipoBeneficiarioSelecionado().equals(this.TIPO_PENSIONISTA)){
			update = "update segurados_segurado set tiposegurado = 'P', idTitular = null, idTitularOrigem=?, diaBase = 1 where numerodocartao = ?";
		}
		
		SQLQuery query = HibernateUtil.currentSession().createSQLQuery(update);
		query.setLong(0, dependente.getTitular().getIdSegurado());
		query.setString(1, dependente.getNumeroDoCartao());
		query.executeUpdate();
		
		Segurado segurado = ImplDAO.findById(dependente.getIdSegurado(), Segurado.class);
		
		//atualiza-se a carencia do segurado migrado.
		segurado.getPessoaFisica().setCpf(dependente.getPessoaFisica().getCpf());
		
		if(resumo.getTipoBeneficiarioSelecionado().equals(this.TIPO_DEPENDENTE_SUPLEMENTAR)){
			Calendar dataSituacao = Calendar.getInstance();
			dataSituacao.setTime(segurado.getSituacao().getDataSituacao());
			
			boolean isSeguradoSuspenso = segurado.isSituacaoAtual(SituacaoEnum.SUSPENSO.descricao());
			boolean isSuspensoAMais30Dias = Utils.diferencaEmDias(dataSituacao,Calendar.getInstance()) > 30;
			
			if(isSeguradoSuspenso && isSuspensoAMais30Dias){
				segurado.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), MotivoEnum.MIGRACAO_PARA_DEPENDENTE_SUPLEMENTAR.getMessage(". Dependente Suspenso há mais de 30 dias"), new Date());
				segurado.setInicioDaCarencia(null);
			}else segurado.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), MotivoEnum.MIGRACAO_PARA_DEPENDENTE_SUPLEMENTAR.getMessage(". Carencias Migradas."), new Date());
		}
		else if(resumo.getTipoBeneficiarioSelecionado().equals(this.TIPO_PENSIONISTA)){
			Pensionista pensionista = (Pensionista) segurado;
			pensionista.getMatriculas().add(resumo.getMatricula());
			resumo.getMatricula().setSegurado(pensionista);
			pensionista.setOrdem(0);
			pensionista.gerarCartao();
			pensionista.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), MotivoEnum.MIGRACAO_PARA_PENSIONISTA.getMessage(), new Date());
		}
		ImplDAO.save(segurado);
		
		transaction.commit();
		return dependente;
	}
	
	public void finalizar(){
		
	}
	
}
