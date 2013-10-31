package br.com.infowaypi.ecarebc.promocao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.Observacao;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @literal Classe que representa uma autorização para a<br>
 * realização de uma <b>consulta sem a geração de co-participação</b>.
 * 
 * @author Marcus Boolean
 *
 */
public class PromocaoConsulta extends ImplColecaoSituacoesComponent{
	
	private static final long serialVersionUID = 1L;
	
	public static final String LIBERADO = "Liberado(a)";
	public static final String UTILIZADO = "Utilizado(a)";
	public static final String VENCIDO = "Vencido(a)";
	public static final int TIPO_ELETIVA = 1;
	public static final int TIPO_URGENCIA = 2;
	public static final int TIPO_TODAS = 3;
	public static final int TIPO_NULO = 0;
	
	private Long idConsultaPromocional;
	private Date dataCriacao;
	private Especialidade especialidade;
	private SeguradoInterface segurado;
	private GuiaSimples guia;
	private Observacao observacao;
	private int tipo;
	
	
	public PromocaoConsulta() {}
	
	public String getTipoFormatado(){
		
		if(this.tipo == PromocaoConsulta.TIPO_ELETIVA){
			return "Eletiva";
		} else {
			return "Urgência";
		}
	}
	
	public PromocaoConsulta(UsuarioInterface usuario, SeguradoInterface segurado, Especialidade especialidade) {
		this.especialidade 	= especialidade;
		this.segurado 		= segurado;
		this.dataCriacao 	= new Date();
		this.mudarSituacao(usuario, LIBERADO, MotivoEnum.LIBERACAO_CONSULTA_PROMOCIONAL.getMessage(), new Date());
	}
	
	public PromocaoConsulta(UsuarioInterface usuario, SeguradoInterface segurado) {
		this.segurado 		= segurado;
		this.dataCriacao 	= new Date();
		this.mudarSituacao(usuario, LIBERADO, MotivoEnum.LIBERACAO_CONSULTA_PROMOCIONAL.getMessage(), new Date());
	}
	
	public Boolean validate(UsuarioInterface usuario){
		if(this.isUtilizado()){
			throw new RuntimeException("Não é permitido alterar uma consulta promocional que já foi utilizada.");
		}
		
		return true;
	}
	public boolean isVencido() {
		Calendar diaDeHoje 						= Calendar.getInstance();
		boolean isSituacaoAtualVencida 			= this.getSituacao().getDescricao().equals(VENCIDO); 
		boolean isConsultaPromocionalVenceu 	= Utils.getDiferencaEmDias(this.getDataDeVencimento(), diaDeHoje.getTime()) > 0 ;
		
		if (!isSituacaoAtualVencida && isConsultaPromocionalVenceu) {
			this.mudarSituacao(null, VENCIDO, MotivoEnum.VENCIMENTO_CONSULTA_PROMOCIONAL.getMessage(), this.getDataDeVencimento());
			return true;
		} else {
			return isSituacaoAtualVencida;
		}
		
	}
	
	public boolean isUtilizado() {
		if(this.isSituacaoAtual(UTILIZADO)) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean isLiberado() {
		if(this.isSituacaoAtual(LIBERADO)) {
			return true;
		}else {
			return false;
		}
	}
	
	public void utilizarConsultaPromocional(GuiaConsulta guia, UsuarioInterface usuario) {
		SimpleDateFormat formatador = new SimpleDateFormat("dd/mm/YYYY");

		if(isVencido()) {
			throw new RuntimeException("Consulta Promocional Vencida em "+ formatador.format(this.getSituacao().getData()));
		}else if(isLiberado()) {
			if(this.especialidade.equals(guia.getEspecialidade())) {
				if(this.segurado.equals(guia.getSegurado())){
					this.guia = guia;
					this.mudarSituacao(usuario, UTILIZADO, MotivoEnum.CONSULTA_PROMOCIONAL_UTILIZADA.getMessage(), new Date());
				}else{
					throw new RuntimeException("O Segurado informado na guia não confere com o discriminado na Consulta Promocional");
				}
			}else {
				throw new RuntimeException("A especialidade informada na guia não confere com a discriminada na Consulta Promocional");
			}
		}else{
			throw new RuntimeException("Consulta Promocional Utilizada em "+ formatador.format(this.getSituacao().getData()));
		}

	}

	public Especialidade getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}

	public GuiaSimples getGuia() {
		return guia;
	}

	public void setGuia(GuiaSimples guia) {
		this.guia = guia;
	}

	public SeguradoInterface getSegurado() {
		return segurado;
	}

	public void setSegurado(SeguradoInterface segurado) {
		this.segurado = segurado;
	}

	public Long getIdConsultaPromocional() {
		return idConsultaPromocional;
	}

	public void setIdConsultaPromocional(Long idConsultaPromocional) {
		this.idConsultaPromocional = idConsultaPromocional;
	}
	
	public Date getDataCriacao() {
		return this.dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	
	public Date getDataDeVencimento() {
		Calendar diaAVencer 	= Calendar.getInstance();
		Calendar diaDeCriacao 	= Calendar.getInstance();
		
		diaDeCriacao.setTime(this.dataCriacao);
		diaAVencer.setTime(diaDeCriacao.getTime());
		
		if(this.tipo == TIPO_ELETIVA){
			diaAVencer.add(Calendar.DAY_OF_MONTH, 30);
		}else if(this.tipo == TIPO_URGENCIA){
			diaAVencer.add(Calendar.DAY_OF_MONTH, 2);
		}
		
		return diaAVencer.getTime();
	}
	
	public void tocarObjetos() {
		if(this.especialidade != null) {
			this.especialidade.getDescricao();
			this.especialidade.getSexo();
		}
		
		if(this.guia != null) {
			this.guia.getAutorizacao();
		}
		
		if(this.segurado != null) {
			this.segurado.getPessoaFisica().getNome();
		}
	}

	public Observacao getObservacao() {
		return observacao;
	}

	public void setObservacao(Observacao observacao) {
		this.observacao = observacao;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	
	public String getDescricaoDoTipo() {
		if (this.tipo == TIPO_ELETIVA) {
			return "Eletiva";
		} else if (this.tipo == TIPO_URGENCIA) {
			return "Urgência";
		} else {
			return "Erro na determinação";
		}
	}			
	
	public boolean isUrgencia() {
		if (this.tipo == this.TIPO_URGENCIA) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean isEletiva() {
		if (this.tipo == this.TIPO_ELETIVA) {
			return true;
		} else {
			return false;
		}
	}
}
