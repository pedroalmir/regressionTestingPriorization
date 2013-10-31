/**
 * 
 */
package br.com.infowaypi.ecare.segurados;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.FlushMode;

import br.com.infowaypi.ecarebc.financeiro.DependenteFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.ecarebc.planos.Plano;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus bOolean
 *
 */
public class Pensionista extends TitularFinanceiroSR {
	
	private static final long serialVersionUID = 2966316738132423117L;
	public static final int TIPO_SERVIDOR = 1;
	public static final int TIPO_PENSIONISTA = 2;
	private TitularFinanceiroSR titularOrigem;
	
	private CargoInterface cargo;

	public Pensionista() {
		super();
	}

	public CargoInterface getCargo() {
		return cargo;
	}

	public void setCargo(CargoInterface cargo) {
		this.cargo = cargo;
	}


	@Override
	public String getTipoPlano() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean isSeguradoDependenteSuplementar() {
		return false;
	}
	
	@Override
	public boolean isSeguradoTitular() {
		return false;
	}

	@Override
	public boolean isBeneficiario() {
		return true;
	}


	public Pensionista getTitular() {
		return this;
	}
	
	public Boolean validate(UsuarioInterface usuario) throws ValidateException {
		
		if (!Utils.isStringVazia(getPessoaFisica().getNomeDaMae())){
			recadastrar(usuario);
		}
		
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		if(!Utils.isStringVazia(this.pessoaFisica.getCpf()) && !Utils.isCpfValido(this.pessoaFisica.getCpf())){
			throw new ValidateException("CPF inválido");
		}
		
		boolean cpfDuplicado = Utils.isCampoDuplicado(this, "pessoaFisica.cpf", this.getPessoaFisica().getCpf());		
		if(cpfDuplicado){
			throw new ValidateException("Este CPF já foi cadastrado para outro Pensionista.");
		}
		
		return super.validate(usuario);
	}

	@Override
	public boolean isSeguradoPensionista() {
		return true;
	}

	@Override
	public boolean isSeguradoDependente() {
		return false;
	}

	public Plano getPlano() {
		return null;
	}

	public Set<DependenteFinanceiroInterface> getDependentesFinanceiro() {
		return new HashSet<DependenteFinanceiroInterface>();
	}

	public Set<FluxoFinanceiroInterface> getFluxosFinanceiros() {
		Set<FluxoFinanceiroInterface> fluxos = new HashSet<FluxoFinanceiroInterface>();
		for(Consignacao consignacao : this.getConsignacoes())
			fluxos.add(consignacao);
		return fluxos;
	}

	public Set<DependenteSR> getDependentes() {
		return new HashSet<DependenteSR>();
	}

	public void setPlano(Plano plano) {
	}
	
	public BigDecimal getValorContrato() {
		// TODO Auto-generated method stub
		return null;
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

	public TitularFinanceiroSR getTitularOrigem() {
		return titularOrigem;
	}

	public void setTitularOrigem(TitularFinanceiroSR titularOrigem) {
		this.titularOrigem = titularOrigem;
	}
	
	@Override
	public void updateDataInicioCarencia() {
		Date dataDoPrimeiroPagamento = this.getDataDoPrimeiroPagamento();
		
		if(Utils.compareData(this.dataAdesao, diaD) < 0) {
			this.setInicioDaCarencia(dataAdesao);
		} else if(dataDoPrimeiroPagamento != null) {
			this.setInicioDaCarencia(dataDoPrimeiroPagamento);			
		}
	}
}