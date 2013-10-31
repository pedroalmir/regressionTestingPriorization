package br.com.infowaypi.ecare.financeiro.ordenador;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.utils.CompetenciaUtils;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings("unchecked")
public class ResumoPreviaOrdenador {
	
	public Date competencia;
	
	public Prestador prestador;
	public Set<GuiaSimples> guiasMontanteAtual;
	public Set<GuiaSimples> guiasMontanteFuturo;
	
	public int quantidadeFechadasAtuais = 0;
	public BigDecimal valorFechadasAtuais = BigDecimal.ZERO;	
	public int quantidadeEmAuditoriaAtuais = 0;
	public BigDecimal valorEmAuditoriaAtuais = BigDecimal.ZERO;	
	public int quantidadeAuditadasAtuais = 0;
	public BigDecimal valorAuditadasAtuais = BigDecimal.ZERO;	
	public int quantidadeConfirmadasAtuais = 0;
	public BigDecimal valorConfirmadasAtuais = BigDecimal.ZERO;
	
	public int quantidadeFechadasFuturas = 0;
	public BigDecimal valorFechadasFuturas = BigDecimal.ZERO;	
	public int quantidadeEmAuditoriaFuturas = 0;
	public BigDecimal valorEmAuditoriaFuturas = BigDecimal.ZERO;	
	public int quantidadeAuditadasFuturas = 0;
	public BigDecimal valorAuditadasFuturas = BigDecimal.ZERO;	
	public int quantidadeConfirmadasFuturas = 0;
	public BigDecimal valorConfirmadasFuturas = BigDecimal.ZERO;
	
	public ResumoPreviaOrdenador(Date competencia, Prestador prestador,Set<GuiaSimples> guias) {
		this.competencia = competencia;
		this.prestador = prestador;
		guiasMontanteAtual = new HashSet<GuiaSimples>();
		guiasMontanteFuturo = new HashSet<GuiaSimples>();
		
		buscarGuias(guias);
		processarAtual();
		processarFuturo();
	}

	private void buscarGuias(Set<GuiaSimples> guias) {

		for (GuiaSimples guia : guias) {
			boolean isGuiaMontanteAtual = Utils.compareData(guia.getDataTerminoAtendimento(), CompetenciaUtils.getFimCompetencia(competencia)) <= 0; 
			if (isGuiaMontanteAtual) {
				this.guiasMontanteAtual.add(guia);
			}else {
				this.guiasMontanteFuturo.add(guia);
			}
		}
	}
	
	private void processarAtual() {
		for (GuiaSimples guia : this.guiasMontanteAtual) {
			boolean isFechada = guia.isSituacaoAtual(SituacaoEnum.FECHADO.descricao());
			boolean isEmAuditoria = guia.isSituacaoAtual(SituacaoEnum.ENVIADO.descricao()) || guia.isSituacaoAtual(SituacaoEnum.RECEBIDO.descricao()) || guia.isSituacaoAtual(SituacaoEnum.DEVOLVIDO.descricao());
			boolean isAuditadas = guia.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao());
			boolean isConfirmadas = guia.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao());

			if (isFechada) {
				this.quantidadeFechadasAtuais ++;
				this.valorFechadasAtuais = this.valorFechadasAtuais.add(guia.getValorTotal());
			}else if (isEmAuditoria) {
				this.quantidadeEmAuditoriaAtuais++;
				this.valorEmAuditoriaAtuais = this.valorEmAuditoriaAtuais.add(guia.getValorTotal());
			}else if (isAuditadas) {
				this.quantidadeAuditadasAtuais ++;
				this.valorAuditadasAtuais = this.valorAuditadasAtuais.add(guia.getValorTotal());
			}else if (isConfirmadas) {
				this.quantidadeConfirmadasAtuais ++;
				this.valorConfirmadasAtuais = this.valorConfirmadasAtuais.add(guia.getValorTotal());
			}
		}
	}
	
	private void processarFuturo() {
		for (GuiaSimples guia : this.guiasMontanteFuturo) {
			boolean isFechada = guia.isSituacaoAtual(SituacaoEnum.FECHADO.descricao());
			boolean isEmAuditoria = guia.isSituacaoAtual(SituacaoEnum.ENVIADO.descricao()) || guia.isSituacaoAtual(SituacaoEnum.RECEBIDO.descricao()) || guia.isSituacaoAtual(SituacaoEnum.DEVOLVIDO.descricao());
			boolean isAuditadas = guia.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao());
			boolean isConfirmadas = guia.isSituacaoAtual(SituacaoEnum.CONFIRMADO.descricao());

			if (isFechada) {
				this.quantidadeFechadasFuturas ++;
				this.valorFechadasFuturas = this.valorFechadasFuturas.add(guia.getValorTotal());
			}else if (isEmAuditoria) {
				this.quantidadeEmAuditoriaFuturas++;
				this.valorEmAuditoriaFuturas = this.valorEmAuditoriaFuturas.add(guia.getValorTotal());
			}else if (isAuditadas) {
				this.quantidadeAuditadasFuturas++;
				this.valorAuditadasFuturas = this.valorAuditadasFuturas.add(guia.getValorTotal());
			}else if (isConfirmadas) {
				this.quantidadeConfirmadasFuturas++;
				this.valorConfirmadasFuturas = this.valorConfirmadasFuturas.add(guia.getValorTotal());
			}
		}
	}
	

	public BigDecimal getValorTotalGeralAtual(){
		return valorFechadasAtuais.add(valorEmAuditoriaAtuais).add(valorAuditadasAtuais).add(valorConfirmadasAtuais);
	}
	
	public BigDecimal getValorTotalGeralFuturo(){
		return valorFechadasFuturas.add(valorEmAuditoriaFuturas).add(valorAuditadasFuturas).add(valorConfirmadasFuturas);
	}
	
	public int getQuantidadeTotalGeralAtual(){
		return quantidadeFechadasAtuais+quantidadeEmAuditoriaAtuais+quantidadeAuditadasAtuais+quantidadeConfirmadasAtuais;
	}
	
	public int getQuantidadeTotalGeralFuturo(){
		return quantidadeFechadasFuturas+quantidadeEmAuditoriaFuturas+quantidadeAuditadasFuturas+quantidadeConfirmadasFuturas;
	}
	
	public BigDecimal getValorTotalApuradoAtual(){
		return valorAuditadasAtuais.add(valorConfirmadasAtuais);
	}
	
	public BigDecimal getValorTotalApuradoFuturo(){
		return valorAuditadasFuturas.add(valorConfirmadasFuturas);
	}
	
	public int getQuantTotalApuradoAtual(){
		return quantidadeAuditadasAtuais+quantidadeConfirmadasAtuais;
	}
	
	public int getQuantTotalApuradoFuturo(){
		return quantidadeAuditadasFuturas+quantidadeConfirmadasFuturas;
	}

	public Date getCompetencia() {
		return competencia;
	}

	public Prestador getPrestador() {
		return prestador;
	}

	public Set<GuiaSimples> getGuiasMontanteAtual() {
		return guiasMontanteAtual;
	}

	public Set<GuiaSimples> getGuiasMontanteFuturo() {
		return guiasMontanteFuturo;
	}

	public int getQuantidadeFechadasAtuais() {
		return quantidadeFechadasAtuais;
	}

	public BigDecimal getValorFechadasAtuais() {
		return valorFechadasAtuais;
	}

	public int getQuantidadeEmAuditoriaAtuais() {
		return quantidadeEmAuditoriaAtuais;
	}

	public BigDecimal getValorEmAuditoriaAtuais() {
		return valorEmAuditoriaAtuais;
	}

	public int getQuantidadeAuditadasAtuais() {
		return quantidadeAuditadasAtuais;
	}

	public BigDecimal getValorAuditadasAtuais() {
		return valorAuditadasAtuais;
	}

	public int getQuantidadeConfirmadasAtuais() {
		return quantidadeConfirmadasAtuais;
	}

	public BigDecimal getValorConfirmadasAtuais() {
		return valorConfirmadasAtuais;
	}

	public int getQuantidadeFechadasFuturas() {
		return quantidadeFechadasFuturas;
	}

	public BigDecimal getValorFechadasFuturas() {
		return valorFechadasFuturas;
	}

	public int getQuantidadeEmAuditoriaFuturas() {
		return quantidadeEmAuditoriaFuturas;
	}

	public BigDecimal getValorEmAuditoriaFuturas() {
		return valorEmAuditoriaFuturas;
	}

	public int getQuantidadeAuditadasFuturas() {
		return quantidadeAuditadasFuturas;
	}

	public BigDecimal getValorAuditadasFuturas() {
		return valorAuditadasFuturas;
	}

	public int getQuantidadeConfirmadasFuturas() {
		return quantidadeConfirmadasFuturas;
	}

	public BigDecimal getValorConfirmadasFuturas() {
		return valorConfirmadasFuturas;
	}
	
}
