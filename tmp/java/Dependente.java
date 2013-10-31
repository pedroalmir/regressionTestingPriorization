package br.com.infowaypi.ecare.segurados;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TreeSet;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.enums.MotivoEnumSR;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.msr.enums.SituacaoEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus bOolean
 *
 */
public class Dependente extends DependenteSR {
	

	private static final long serialVersionUID = 6370131338968570190L;
	
	//Atributo utilizado no fluxo de Atualizar Dependentes pelo role relacionamento. Este atributo guarda o valordo tipo de beneficário escolhido
	//no passo anterior para ser utilizado no passo seguinte.
	private Integer tipoBeneficiario;

	private Set<Regularizacao> regularizacoes;

	public Dependente() {
		super();
		this.regularizacoes = new TreeSet<Regularizacao>();
	}
	
	public Dependente(UsuarioInterface usuario) {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public Titular getTitular() {
		return super.getTitular();
	}
	
	@Override
	public void reativar(String motivo, Date dataAdesao, UsuarioInterface usuario) throws Exception {
		super.reativar(motivo, dataAdesao,usuario);
	}
	
	@Override
	public void suspender(String motivo, UsuarioInterface usuario) throws Exception {
		super.suspender(motivo, usuario);
	}
	
	@Override
	public void cancelar(String motivo, UsuarioInterface usuario) throws Exception {
		super.cancelar(motivo, usuario);
	}

	public int compareTo(DependenteInterface dependente2) {
		int  mesmaAdesao = this.getDataAdesao().compareTo(dependente2.getDataAdesao());
		if(mesmaAdesao == 0){
			return this.getPessoaFisica().getDataNascimento().compareTo(dependente2.getPessoaFisica().getDataNascimento());
		} else {
			return mesmaAdesao;
		}
	}

	public Integer getTipoBeneficiario() {
		return tipoBeneficiario;
	}

	public void setTipoBeneficiario(Integer tipoBeneficiario) {
		this.tipoBeneficiario = tipoBeneficiario;
	}

	@Override
	public boolean isSeguradoDependenteSuplementar() {
		return false;
	}

	public Set<Regularizacao> getRegularizacoes() {
		return regularizacoes;
	}

	public void setRegularizacoes(Set<Regularizacao> regularizacoes) {
		this.regularizacoes = regularizacoes;
	}

	@SuppressWarnings("unchecked")
	// o se o dep ja estiver suspenso ele nao pode mudar de estado
	public void regularizar(UsuarioInterface usuario){

		Assert.isEquals(this.getTipoDeDependencia(), SeguradoBasico.TIPO_FILHO_MENOR_25_ANOS, 
				"Caro usuário, a regularizacao só é permitida para dependentes com grau de parentesco "+SeguradoBasico.DESCRICAO_TIPO_FILHO_MENOR_25_ANOS.toUpperCase()+".");
		Assert.isNotNull(usuario, "Usuário Nulo");
		Assert.isFalse(this.isSituacaoAtual(SituacaoEnum.CANCELADO.getDescricao()),
				"Caro Usuário, dependentes cancelados não podem ser regularizados.");
		
		if (getRegularizacoes().isEmpty()) {
			Regularizacao regularizado = new Regularizacao(usuario, this);
			this.getRegularizacoes().add(regularizado);
		} else {
			Regularizacao ultimaRegularizacao = getUltimaRegularizacao();
			Calendar dataProximaRegularixacao = new GregorianCalendar();
			dataProximaRegularixacao.setTime(ultimaRegularizacao.getDataRegularizacao());
			dataProximaRegularixacao.add(Calendar.MONTH, 4);
						
			int resultado = Utils.compareData(dataProximaRegularixacao.getTime(), new Date());	
	
			if (resultado <= 0) {
				
				Regularizacao regularizado = new Regularizacao(usuario, this);
				this.getRegularizacoes().add(regularizado);
				
			} else {
				throw new RuntimeException(MensagemErroEnumSR.REGULARIZACAO_PRECOCE.getMessage(Utils.format(dataProximaRegularixacao.getTime())));
			}
		}
		
		if (this.isSituacaoAtual(SituacaoEnum.SUSPENSO.getDescricao()) && this.getSituacao().getMotivo().equalsIgnoreCase(MotivoEnumSR.REGULARIZACAO_DEPENDENTE.getMessage())) {
			this.mudarSituacao(usuario, Constantes.SITUACAO_ATIVO, MotivoEnumSR.REGULARIZACAO_DEPENDENTE.getMessage(), new Date());
		}	
		
	}

	public Regularizacao getUltimaRegularizacao() {
		Set<Regularizacao> regs = getRegularizacoes();
		Regularizacao reg = null;
		for (Regularizacao regularizacao : regs) {
			if (reg == null) {
				reg = regularizacao;
				continue;
			}
			if (Utils.compareData(reg.getDataRegularizacao(), regularizacao
					.getDataRegularizacao()) == -1) {
				reg = regularizacao;
			}

		}
		return reg;
	}
	
	@Override
	public void updateDataInicioCarencia() {
		Calendar dataAdesao = Calendar.getInstance();
		dataAdesao.setLenient(true);
		
		if(this.getDataAdesao() == null){
			return;
		}
		
		dataAdesao.setTime(this.getDataAdesao());
		
		if (isAderiuNoPrimeiroMesDeVida()) {
			dataAdesao.set(Calendar.DAY_OF_MONTH, -800);
			this.setInicioDaCarencia(dataAdesao.getTime());
		} else if(Utils.compareData(this.dataAdesao, diaD) < 0) {
			this.setInicioDaCarencia(dataAdesao.getTime());
		} else {
			this.setInicioDaCarencia(this.getDataPrimeiroPagamentoAposAdesao());
		}
	}
	
	public boolean isAderiuNoPrimeiroMesDeVida() {
		Calendar primeiroMesDeVida = Calendar.getInstance();
		primeiroMesDeVida.setTime(pessoaFisica.getDataNascimento());
		primeiroMesDeVida.add(Calendar.MONTH, 1);
		
		if(dataAdesao.compareTo(primeiroMesDeVida.getTime()) <= 0){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public Boolean validate(UsuarioInterface usuario) throws ValidateException {
		if(this.getInicioDaCarencia() == null){
			this.updateDataInicioCarencia();
		}
		
		Boolean retorno = super.validate(usuario); 

		//Cria usuário que dá acesso ao segurado 'dependente' ao portal do beneficiário.
		this.criarUsuarioSegurado(Role.DEPENDENTE.getValor());
		
		return retorno;
	}
}
