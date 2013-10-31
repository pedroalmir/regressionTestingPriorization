package br.com.infowaypi.ecarebc.financeiro.faturamento.ordenador;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.financeiro.faturamento.TetoPrestadorFaturamento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe que representa um ordenamento no sistema.
 * @author <a href="mailto:mquixaba@gmail.com">Marcus Quixabeira</a>
 * @since 2010-07-19 15:01
 *
 */
public class Ordenador implements Serializable,Cloneable{
	
	private static final long serialVersionUID = 1L;
	private Long idOrdenador;
	private Date competencia;
	private Date dataRecebimento;
	private int identificador;
	private Date dataGeracao;
	
	private UsuarioInterface usuario;
	
	private BigDecimal limiteAFaturarNormal;
	private BigDecimal limiteAFaturarPassivo;
	
	private BigDecimal valorAFaturarNormal;
	private BigDecimal valorAFaturarPassivo;
	
	private Set<GuiaFaturavel> guiasFaturamentoNormal;

	private Set<GuiaFaturavel> guiasFaturamentoPassivo;
	
	private Set<InformacaoOrdenador> informacoesOrdenador;
	
	private BigDecimal valorHospital = BigDecimal.ZERO;
	private BigDecimal valorLaboratorio = BigDecimal.ZERO;
	private BigDecimal valorMedicosCredenciados = BigDecimal.ZERO;
	private BigDecimal valorDentista= BigDecimal.ZERO;
	private BigDecimal valorOutrosProfissionais= BigDecimal.ZERO;
	private BigDecimal valorClinicaDeExames= BigDecimal.ZERO;
	private BigDecimal valorClinicaDeOdontologia= BigDecimal.ZERO;
	private BigDecimal valorClinicaAmbulatorial= BigDecimal.ZERO;
	private BigDecimal valorAnestesista= BigDecimal.ZERO;
	private BigDecimal valorOutrasCategorias= BigDecimal.ZERO;
	
	// Atributo transiente para repetição do fluxo de geracao de ordenador
	private boolean flag = false;
	
	private List<InformacaoOrdenador> informacaoOrdenadorPessoaFisica;
	private List<InformacaoOrdenador> informacaoOrdenadorPessoaJuridica;
	
	public Ordenador() {
		this.informacoesOrdenador = new HashSet<InformacaoOrdenador>();
		this.valorAFaturarNormal = BigDecimal.ZERO;
		this.valorAFaturarPassivo = BigDecimal.ZERO;
		this.limiteAFaturarNormal 	= BigDecimal.ZERO;
		this.limiteAFaturarPassivo 	= BigDecimal.ZERO;
		this.informacaoOrdenadorPessoaFisica = null;
		this.informacaoOrdenadorPessoaJuridica = null;
		this.guiasFaturamentoNormal = new HashSet<GuiaFaturavel>();
		this.guiasFaturamentoPassivo = new HashSet<GuiaFaturavel>();
	}
	
	public Long getIdOrdenador() {
		return idOrdenador;
	}
	
	public void setIdOrdenador(Long idOrdenador) {
		this.idOrdenador = idOrdenador;
	}
	
	public Date getCompetencia() {
		return competencia;
	}
	
	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}
	
	public Date getDataRecebimento() {
		return dataRecebimento;
	}
	
	public void setDataRecebimento(Date dataRecebimento) {
		this.dataRecebimento = dataRecebimento;
	}
	
	public int getIdentificador() {
		return identificador;
	}
	public void setIdentificador(int identificador) {
		this.identificador = identificador;
	}
	public Date getDataGeracao() {
		return dataGeracao;
	}
	
	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}
	
	public UsuarioInterface getUsuario() {
		return usuario;
	}
	public void setUsuario(UsuarioInterface usuario) {
		this.usuario = usuario;
	}
	public Set<InformacaoOrdenador> getInformacoesOrdenador() {
		return informacoesOrdenador;
	}
	public void setInformacoesOrdenador(Set<InformacaoOrdenador> informacoesOrdenador) {
		this.informacoesOrdenador = informacoesOrdenador;
	}
	
	public BigDecimal getTotal() {
		return this.valorAFaturarNormal.add(this.valorAFaturarPassivo);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Ordenador clone = new Ordenador();
		clone.setCompetencia(this.getCompetencia());
		clone.setDataGeracao(this.getDataGeracao());
		clone.setDataRecebimento(this.getDataRecebimento());
		
		clone.setValorAnestesista(this.getValorAnestesista());
		clone.setValorClinicaAmbulatorial(this.getValorClinicaAmbulatorial());
		clone.setValorClinicaDeExames(this.getValorClinicaDeExames());
		clone.setValorClinicaDeOdontologia(this.getValorClinicaDeOdontologia());
		clone.setValorDentista(this.getValorDentista());
		clone.setValorHospital(this.getValorHospital());
		clone.setValorLaboratorio(this.getValorLaboratorio());
		clone.setValorMedicosCredenciados(this.getValorMedicosCredenciados());
		clone.setValorOutrasCategorias(this.getValorOutrasCategorias());
		clone.setValorOutrosProfissionais(this.getValorOutrosProfissionais());
		clone.setLimiteAFaturarNormal(this.getLimiteAFaturarNormal());
		clone.setLimiteAFaturarPassivo(this.getLimiteAFaturarPassivo());
		
		for (InformacaoOrdenador informacaoOrdenador : this.getInformacoesOrdenador()) {
			InformacaoOrdenador informacaoClone = (InformacaoOrdenador) informacaoOrdenador.clone();
			clone.getInformacoesOrdenador().add(informacaoClone);
			informacaoClone.setOrdenador(clone);
		}
		
		clone.updateValoresAFaturar();
		clone.setUsuario(this.getUsuario());
		
		clone.setGuiasFaturamentoNormal(this.getGuiasFaturamentoNormal());
		clone.setGuiasFaturamentoPassivo(this.getGuiasFaturamentoPassivo());
		
		return clone;
	}
	
	public BigDecimal getValorHospital() {
		return valorHospital;
	}

	public void setValorHospital(BigDecimal valorHospital) {
		this.valorHospital = valorHospital;
	}

	public BigDecimal getValorLaboratorio() {
		return valorLaboratorio;
	}

	public void setValorLaboratorio(BigDecimal valorLaboratorio) {
		this.valorLaboratorio = valorLaboratorio;
	}

	public BigDecimal getValorMedicosCredenciados() {
		return valorMedicosCredenciados;
	}

	public void setValorMedicosCredenciados(BigDecimal valorMedicosCredenciados) {
		this.valorMedicosCredenciados = valorMedicosCredenciados;
	}

	public BigDecimal getValorDentista() {
		return valorDentista;
	}

	public void setValorDentista(BigDecimal valorDentista) {
		this.valorDentista = valorDentista;
	}

	public BigDecimal getValorOutrosProfissionais() {
		return valorOutrosProfissionais;
	}

	public void setValorOutrosProfissionais(BigDecimal valorOutrosProfissionais) {
		this.valorOutrosProfissionais = valorOutrosProfissionais;
	}

	public BigDecimal getValorClinicaDeExames() {
		return valorClinicaDeExames;
	}

	public void setValorClinicaDeExames(BigDecimal valorClinicaDeExames) {
		this.valorClinicaDeExames = valorClinicaDeExames;
	}

	public BigDecimal getValorClinicaDeOdontologia() {
		return valorClinicaDeOdontologia;
	}

	public void setValorClinicaDeOdontologia(BigDecimal valorClinicaDeOdontologia) {
		this.valorClinicaDeOdontologia = valorClinicaDeOdontologia;
	}

	public BigDecimal getValorClinicaAmbulatorial() {
		return valorClinicaAmbulatorial;
	}

	public void setValorClinicaAmbulatorial(BigDecimal valorClinicaAmbulatorial) {
		this.valorClinicaAmbulatorial = valorClinicaAmbulatorial;
	}

	public BigDecimal getValorAnestesista() {
		return valorAnestesista;
	}

	public void setValorAnestesista(BigDecimal valorAnestesista) {
		this.valorAnestesista = valorAnestesista;
	}

	public BigDecimal getValorOutrasCategorias() {
		return valorOutrasCategorias;
	}

	public void setValorOutrasCategorias(BigDecimal valorOutrasCategorias) {
		this.valorOutrasCategorias = valorOutrasCategorias;
	}

	public void update() {
		this.updateResumoPorCategoria();
		this.updateValoresAFaturar();
	}
	
	public void updateResumoPorCategoria() {
		valorHospital 				= BigDecimal.ZERO; 
		valorLaboratorio 			= BigDecimal.ZERO; 
		valorMedicosCredenciados 	= BigDecimal.ZERO; 
		valorDentista 				= BigDecimal.ZERO; 
		valorClinicaDeExames 		= BigDecimal.ZERO; 
		valorClinicaDeOdontologia 	= BigDecimal.ZERO; 
		valorClinicaAmbulatorial 	= BigDecimal.ZERO; 
		valorAnestesista 			= BigDecimal.ZERO; 
		valorOutrosProfissionais 	= BigDecimal.ZERO; 
		valorOutrasCategorias 		= BigDecimal.ZERO; 

		for (InformacaoOrdenador informacao : informacoesOrdenador) {
			Integer tipoPrestador = informacao.getPrestador().getTipoPrestador();
			
			switch (tipoPrestador) {
			case Prestador.TIPO_PRESTADOR_HOSPITAL:
				 valorHospital = valorHospital.add(informacao.getValorTotal());
				break;
			case Prestador.TIPO_PRESTADOR_LABORATORIO:
				 valorLaboratorio = valorLaboratorio.add(informacao.getValorTotal());
				break;
			case Prestador.TIPO_PRESTADOR_MEDICOS:
				 valorMedicosCredenciados = valorMedicosCredenciados.add(informacao.getValorTotal());
				break;
			case Prestador.TIPO_PRESTADOR_DENTISTAS:
				 valorDentista = valorDentista.add(informacao.getValorTotal());
				break;
			case Prestador.TIPO_PRESTADOR_CLINICAS_DE_EXAMES:
				 valorClinicaDeExames = valorClinicaDeExames.add(informacao.getValorTotal());
				break;
			case Prestador.TIPO_PRESTADOR_CLINICAS_DE_ODONTOLOGIA:
				 valorClinicaDeOdontologia = valorClinicaDeOdontologia.add(informacao.getValorTotal());
				break;
			case Prestador.TIPO_PRESTADOR_CLINICAS_AMBULATORIAIS:
				 valorClinicaAmbulatorial = valorClinicaAmbulatorial.add(informacao.getValorTotal());
				break;
			case Prestador.TIPO_PRESTADOR_ANESTESISTA:
				 valorAnestesista = valorAnestesista.add(informacao.getValorTotal());
				break;
			case Prestador.TIPO_PRESTADOR_OUTROS:
				 valorOutrosProfissionais = valorOutrosProfissionais.add(informacao.getValorTotal());
				break;				
			default:
				valorOutrasCategorias = valorOutrasCategorias.add(informacao.getValorTotal());
				break;
			}
		}
	}
	
	public void updateValoresAFaturar() {
		this.valorAFaturarNormal = BigDecimal.ZERO;
		this.valorAFaturarPassivo = BigDecimal.ZERO;
		
		for (InformacaoOrdenador informacaoOrdenador : this.informacoesOrdenador) {
			this.valorAFaturarNormal = this.valorAFaturarNormal.add(informacaoOrdenador.getValorAFaturarNormal());
			this.valorAFaturarPassivo = this.valorAFaturarPassivo.add(informacaoOrdenador.getValorAFaturarPassivo());
		}
		
	}
	
	public boolean validateTetos() throws Exception {
		
		InformacaoOrdenador tabelaTotal = this.getTabelaSubTotal().get(2);
		
		BigDecimal valorNormal = tabelaTotal.getValorNormal();
		BigDecimal valorPassivo = tabelaTotal.getValorPassivo();
		if(this.getLimiteAFaturarNormal() != null && valorNormal.compareTo(this.getLimiteAFaturarNormal()) < 0) {
			throw new ValidateException("O Limite a Faturar Normal informado supera o Valor Normal.");
		}
		
		if(this.getLimiteAFaturarPassivo() != null && valorPassivo.compareTo(this.getLimiteAFaturarPassivo()) < 0) {
			throw new ValidateException("O Limite a Faturar Passivo informado supera o Valor Passivo.");
		}
		
		return true;
	}

	private boolean validarValores(BigDecimal valorNormal, BigDecimal valorPassivo ) throws ValidateException, Exception {
		String limiteAFaturar 	= null;
		String valorTeto 		= null;

		for (InformacaoOrdenador informacao : this.getInformacoesOrdenador()) {
			informacao.validate();
		}
		
		if(limiteAFaturarNormal != null && MoneyCalculation.compare(limiteAFaturarNormal, BigDecimal.ZERO) > 0 && MoneyCalculation.compare(valorNormal, limiteAFaturarNormal) > 0) {
			limiteAFaturar 	= Utils.applyMoneyMask(this.limiteAFaturarNormal);
			valorTeto 		= Utils.applyMoneyMask(valorNormal);
			throw new ValidateException(MensagemErroEnum.VALOR_TETO_MAIOR_QUE_LIMITE_INFORMADO.getMessage(valorTeto, "normal", limiteAFaturar));
		}
		
		if(limiteAFaturarPassivo != null && MoneyCalculation.compare(limiteAFaturarPassivo, BigDecimal.ZERO) > 0 && MoneyCalculation.compare(valorPassivo, limiteAFaturarPassivo) > 0) {
			limiteAFaturar 	= Utils.applyMoneyMask(this.limiteAFaturarPassivo);
			valorTeto 		= Utils.applyMoneyMask(valorPassivo);
			throw new ValidateException(MensagemErroEnum.VALOR_TETO_MAIOR_QUE_LIMITE_INFORMADO.getMessage(valorTeto, "passivo", limiteAFaturar));
		}
		
		this.refreshInformacoes();
		
		return true;
	}
	
	public boolean validarValoresAFaturar() throws ValidateException, Exception{
		BigDecimal valorAFaturarNormal = this.getTabelaSubTotal().get(2).getValorAFaturarNormal();
		BigDecimal valorAFaturarPassivo = this.getTabelaSubTotal().get(2).getValorAFaturarPassivo();
		return validarValores(valorAFaturarNormal, valorAFaturarPassivo);
	}

	private void refreshInformacoes() {
		Set<InformacaoOrdenador> informacoes = new HashSet<InformacaoOrdenador>();
		informacoes.addAll(this.informacaoOrdenadorPessoaFisica);
		informacoes.addAll(this.informacaoOrdenadorPessoaJuridica);
		this.setInformacoesOrdenador(informacoes);
	}
	
	public void atualizaIdentificador(Ordenador ordenadorAnterior) throws Exception {
		if(ordenadorAnterior != null) {
			int identificadorAnterior = ordenadorAnterior.getIdentificador();
			String prefixo = String.valueOf(identificadorAnterior).substring(0,6);
			String sufixo = String.valueOf(identificadorAnterior).substring(6,8);
			int sufixoNumerico = Integer.valueOf(sufixo);
			sufixoNumerico++;
			if(sufixoNumerico > 99) {
				throw new ValidateException("O Número de ordenadores possiveis para esta competencia já foi atingido.");
			}else {
				if(String.valueOf(sufixoNumerico).length() <= 1) {
					String sufixoAjustado = "0" + String.valueOf(sufixoNumerico);
					String novoIdentificador = prefixo+String.valueOf(sufixoAjustado);
					this.setIdentificador(Integer.valueOf(novoIdentificador));
				}else {
					String novoIdentificador = prefixo+String.valueOf(sufixoNumerico);
					this.setIdentificador(Integer.valueOf(novoIdentificador));
				}	
			}
		}else {
			String prefixo = Utils.format(this.competencia, "yyyyMM");
			String sufixo = "01";
			String identificador = prefixo+ sufixo;
			this.setIdentificador(Integer.valueOf(identificador));
		}
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public List<InformacaoOrdenador> getInformacoesOrdenadorPessoaFisica() {
		if (this.informacaoOrdenadorPessoaFisica == null) {
			this.informacaoOrdenadorPessoaFisica = this.getInformacoesOrdenadorPorTipoDePrestador(true);
		}
		return this.informacaoOrdenadorPessoaFisica;
	}
	
	public List<InformacaoOrdenador> getInformacoesOrdenadorPessoaJuridica() {
		if (this.informacaoOrdenadorPessoaJuridica == null) {
			this.informacaoOrdenadorPessoaJuridica = this.getInformacoesOrdenadorPorTipoDePrestador(false);
		}
		return this.informacaoOrdenadorPessoaJuridica;
	}
	
	public List<InformacaoOrdenador> getInformacoesOrdenadorOrdenado() {
		ArrayList<InformacaoOrdenador> result = new ArrayList<InformacaoOrdenador>(this.informacoesOrdenador);
		Collections.sort(result, new InformacaoOrdenadorComparator());
		return result;
	}
	
	private List<InformacaoOrdenador> getInformacoesOrdenadorPorTipoDePrestador(boolean isPessoaFisica) {
		List<InformacaoOrdenador> result = new ArrayList<InformacaoOrdenador>(this.informacoesOrdenador);

		Iterator<InformacaoOrdenador> iterator = result.iterator();
		while (iterator.hasNext()) {
			InformacaoOrdenador informacaoOrdenador = iterator.next();
			boolean isPrestadorPessoaFisica = informacaoOrdenador.getPrestador().isPessoaFisica();
			
			if (isPrestadorPessoaFisica != isPessoaFisica) {
				iterator.remove();
			}
		}
		Collections.sort(result, new InformacaoOrdenadorComparator());
		return result;
	}

	public List<InformacaoOrdenador> getTabelaSubTotal() {
		List<InformacaoOrdenador> result = new ArrayList<InformacaoOrdenador>();
		
		InformacaoOrdenador subtotalPF = this.createSubTotal("SUBTOTAL (PESSOA FÍSICA)", this.getInformacoesOrdenadorPessoaFisica());
		result.add(subtotalPF);
		
		InformacaoOrdenador subtotalPJ = this.createSubTotal("SUBTOTAL (PESSOA JURÍDICA)", this.getInformacoesOrdenadorPessoaJuridica());
		result.add(subtotalPJ);
		
		InformacaoOrdenador total = this.createTotal("TOTAL", subtotalPF, subtotalPJ); 
		result.add(total);
		
		return result;
	}
	
	private InformacaoOrdenador createTotal(String fantasia, InformacaoOrdenador subtotalPF, InformacaoOrdenador subtotalPJ) {
		InformacaoOrdenador result = new InformacaoOrdenador();
		
		this.setPrestador(fantasia, result);
		
		BigDecimal somatorioValorNormal = subtotalPF.getValorNormal().add(subtotalPJ.getValorNormal());
		result.setValorNormal(somatorioValorNormal);
		
		BigDecimal somatorioValorAFaturarNormal = subtotalPF.getValorAFaturarNormal().add(subtotalPJ.getValorAFaturarNormal());
		result.setValorAFaturarNormal(somatorioValorAFaturarNormal);
		
		BigDecimal somatorioValorPassivo = subtotalPF.getValorPassivo().add(subtotalPJ.getValorPassivo());
		result.setValorPassivo(somatorioValorPassivo);
		
		BigDecimal somatorioValorAFaturarPassivo = subtotalPF.getValorAFaturarPassivo().add(subtotalPJ.getValorAFaturarPassivo());
		result.setValorAFaturarPassivo(somatorioValorAFaturarPassivo);
		
		BigDecimal somatorioTetoNormal = subtotalPF.getTetoNormal().add(subtotalPJ.getTetoNormal());
		result.setTetoNormal(somatorioTetoNormal);
		
		BigDecimal somatorioTetoPassivo = subtotalPF.getTetoPassivo().add(subtotalPJ.getTetoPassivo());
		result.setTetoPassivo(somatorioTetoPassivo);
		
		return result;
	}

	private void setPrestador(String fantasia, InformacaoOrdenador result) {
		Prestador prestador = new Prestador();
		prestador.getPessoaJuridica().setFantasia(fantasia);
		result.setPrestador(prestador);
	}

	private InformacaoOrdenador createSubTotal(String fantasia, List<InformacaoOrdenador> infoOrdenador) {
		InformacaoOrdenador subTotal = new InformacaoOrdenador();

		this.setPrestador(fantasia, subTotal);
		
		BigDecimal ValorNormal 			= BigDecimal.ZERO;
		BigDecimal valorAFaturarNormal 	= BigDecimal.ZERO;
		BigDecimal ValorPassivo 		= BigDecimal.ZERO;
		BigDecimal valorAFaturarPassivo = BigDecimal.ZERO;
		BigDecimal tetoNormal 			= BigDecimal.ZERO;
		BigDecimal tetoPassivo 			= BigDecimal.ZERO;
		
		for (InformacaoOrdenador informacaoOrdenador : infoOrdenador) {
			ValorNormal = ValorNormal.add(informacaoOrdenador.getValorNormal());
			valorAFaturarNormal = valorAFaturarNormal.add(informacaoOrdenador.getValorAFaturarNormal());
			ValorPassivo = ValorPassivo.add(informacaoOrdenador.getValorPassivo());
			valorAFaturarPassivo = valorAFaturarPassivo.add( informacaoOrdenador.getValorAFaturarPassivo());
			
			BigDecimal valorTetoNormal = informacaoOrdenador.getTetoNormal();
			if(valorTetoNormal != null){
				tetoNormal = tetoNormal.add( valorTetoNormal);
			}else{
				tetoNormal = tetoNormal.add(informacaoOrdenador.getValorNormal());
			}
			
			BigDecimal valorTetoPassivo = informacaoOrdenador.getTetoPassivo();
			if(valorTetoPassivo != null){
				tetoPassivo = tetoPassivo.add(valorTetoPassivo);
			}else{
				tetoPassivo = tetoPassivo.add(informacaoOrdenador.getValorPassivo());
			}
			
		}
		
		subTotal.setValorNormal(ValorNormal);
		subTotal.setValorAFaturarNormal(valorAFaturarNormal);
		subTotal.setValorPassivo(ValorPassivo);
		subTotal.setValorAFaturarPassivo(valorAFaturarPassivo);
		subTotal.setTetoNormal(tetoNormal);
		subTotal.setTetoPassivo(tetoPassivo);
		
		return subTotal;
	}

	
	private class InformacaoOrdenadorComparator implements Comparator<InformacaoOrdenador> {

		@Override
		public int compare(InformacaoOrdenador o1, InformacaoOrdenador o2) {
			String prestador1 = getFantasia(o1);
			String prestador2 = getFantasia(o2);
			return prestador1.compareTo(prestador2);
		}
		
		private String getFantasia(InformacaoOrdenador o) {
			return o.getPrestador().getPessoaJuridica().getFantasia();
		}
	}

	public BigDecimal getLimiteAFaturarNormal() {
		return limiteAFaturarNormal;
	}

	public void setLimiteAFaturarNormal(BigDecimal limiteAFaturarNormal) {
		this.limiteAFaturarNormal = limiteAFaturarNormal;
	}

	public BigDecimal getLimiteAFaturarPassivo() {
		return limiteAFaturarPassivo;
	}

	public void setLimiteAFaturarPassivo(BigDecimal limiteAFaturarPassivo) {
		this.limiteAFaturarPassivo = limiteAFaturarPassivo;
	}
	
	public Set<GuiaFaturavel> getGuiasFaturamentoNormal() {
		return guiasFaturamentoNormal;
	}

	public void setGuiasFaturamentoNormal(Set<GuiaFaturavel> guiasFaturamentoNormal) {
		this.guiasFaturamentoNormal = guiasFaturamentoNormal;
	}

	public Set<GuiaFaturavel> getGuiasFaturamentoPassivo() {
		return guiasFaturamentoPassivo;
	}
	
	public Set<GuiaFaturavel> getGuias() {
		HashSet<GuiaFaturavel> guias = new HashSet<GuiaFaturavel>();
		guias.addAll(getGuiasFaturamentoNormal());
		guias.addAll(getGuiasFaturamentoPassivo());
		return guias;
	}

	public void setGuiasFaturamentoPassivo(Set<GuiaFaturavel> guiasFaturamentoPassivo) {
		this.guiasFaturamentoPassivo = guiasFaturamentoPassivo;
	}

	public BigDecimal getValorAFaturarNormal() {
		return valorAFaturarNormal;
	}

	public void setValorAFaturarNormal(BigDecimal valorAFaturarNormal) {
		this.valorAFaturarNormal = valorAFaturarNormal;
	}

	public BigDecimal getValorAFaturarPassivo() {
		return valorAFaturarPassivo;
	}

	public void setValorAFaturarPassivo(BigDecimal valorAFaturarPassivo) {
		this.valorAFaturarPassivo = valorAFaturarPassivo;
	}
	
	public Map<Prestador, InformacaoOrdenador> getMapInformacaoOrdenador(){
		Map<Prestador, InformacaoOrdenador> map = new HashMap<Prestador, InformacaoOrdenador>();
		
		for (InformacaoOrdenador informacaoOrdenador : getInformacoesOrdenador()) {
			map.put(informacaoOrdenador.getPrestador(), informacaoOrdenador);
		}
		
		return map;
	}
	
	public Set<TetoPrestadorFaturamento> getTetos(boolean isNormal) {
		
		Set<TetoPrestadorFaturamento> tetos = new HashSet<TetoPrestadorFaturamento>();
		
		for (InformacaoOrdenador informacao : this.getInformacoesOrdenador()) {
			if(isNormal) {
				if(informacao.getTetoNormal() != null) {
					TetoPrestadorFaturamento teto = new TetoPrestadorFaturamento();
					teto.setPrestador(informacao.getPrestador());
					teto.setTeto(informacao.getTetoNormal());
					tetos.add(teto);
				}
			}else {
				if(informacao.getTetoPassivo() != null) {
					TetoPrestadorFaturamento teto = new TetoPrestadorFaturamento();
					teto.setPrestador(informacao.getPrestador());
					teto.setTeto(informacao.getTetoPassivo());
					tetos.add(teto);
				}
			}
		}
		
		return tetos;
	}
	
	public Set<Prestador> getPrestadoresTetoZerado(boolean isNormal){
		Set<Prestador> prestadoresIgnorados = new HashSet<Prestador>();
		for (TetoPrestadorFaturamento teto : this.getTetos(isNormal)) {
			if (MoneyCalculation.compare(teto.getTeto(), BigDecimal.ZERO) == 0) {
				prestadoresIgnorados.add(teto.getPrestador());
			}
		}
		
		return prestadoresIgnorados;
	}
}
