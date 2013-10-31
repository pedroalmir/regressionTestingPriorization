package br.com.infowaypi.ecarebc.atendimentos.visitors;

import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemTaxa;
import br.com.infowaypi.ecarebc.atendimentos.honorario.Honorario;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOutros;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

public class ItemGlosavelGlosarVisitor implements ItemGlosavelVisitor {

	private UsuarioInterface usuario;

	public ItemGlosavelGlosarVisitor(UsuarioInterface usuario) {
		this.usuario = usuario;
	}
	
	@Override
	public ItemGlosavel visit(ItemPacote item) {
		Assert.isNotNull(item.getMotivoGlosa(), MensagemErroEnum.ITEM_GLOSADO_SEM_MOTIVO_DE_GLOSA.getMessage(item.getPacote().getCodigoDescricao()));
		item.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), item.getMotivoGlosa().getDescricao(), new Date());
		
		BigDecimal novoValor = item.getGuia().getValorTotal().subtract(new BigDecimal(item.getValorTotal()));
		item.getGuia().setValorTotal(novoValor);

		return null;
	}

	@Override
	public ItemGlosavel visit(ItemDiaria item) {
		Assert.isNotNull(item.getMotivoGlosa(), MensagemErroEnum.ITEM_GLOSADO_SEM_MOTIVO_DE_GLOSA.getMessage(item.getDiaria().getCodigoDescricao()));
		
		item.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), item.getMotivoGlosa().getDescricao(), new Date());
		
		BigDecimal novoValor = item.getGuia().getValorTotal().subtract(new BigDecimal(item.getValorTotal()));
		item.getGuia().setValorTotal(novoValor);

		return null;
	}
	
	@Override
	public ItemGlosavel visit(ItemGasoterapia item) {
		Assert.isNotNull(item.getMotivoGlosa(), MensagemErroEnum.GASOTERAPIA_GLOSADA_SEM_MOTIVO_DE_GLOSA.getMessage(item.getGasoterapia().getDescricao()));
		
		item.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), item.getMotivoGlosa().getDescricao(), new Date());
		
		BigDecimal novoValor = item.getGuia().getValorTotal().subtract(new BigDecimal(item.getValorTotal()));
		item.getGuia().setValorTotal(novoValor);
		return null;
	}
	
	@Override
	public ItemGlosavel visit(ItemTaxa item) {
		Assert.isNotNull(item.getMotivoGlosa(), MensagemErroEnum.TAXA_GLOSADA_SEM_MOTIVO_DE_GLOSA.getMessage(item.getTaxa().getDescricao()));
		
		item.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), item.getMotivoGlosa().getDescricao(), new Date());
		
		BigDecimal novoValor = item.getGuia().getValorTotal().subtract(new BigDecimal(item.getValorTotal()));
		item.getGuia().setValorTotal(novoValor);
		return null;

	}

	@Override
	public ItemGlosavel visit(ProcedimentoCirurgico procedimento) {
		Assert.isNotNull(procedimento.getMotivoGlosaProcedimento(), MensagemErroEnum.PROCEDIMENTO_GLOSADO_SEM_MOTIVO_DE_GLOSA.getMessage(procedimento.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()));
		procedimento.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), procedimento.getMotivoGlosaProcedimento().getDescricao(), new Date());
		for (Honorario honorario : procedimento.getHonorariosGuiaOrigem()) {
			honorario.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), MotivoEnum.GLOSADO_AUTOMATICAMENTE.getMessage(), new Date());
		}
		BigDecimal novoValor = procedimento.getGuia().getValorTotal().subtract(procedimento.getValorTotal());
		procedimento.getGuia().setValorTotal(novoValor);
		return null;
	}

	@Override
	public ItemGlosavel visit(Procedimento procedimento) {
		Assert.isNotNull(procedimento.getMotivoGlosaProcedimento(), MensagemErroEnum.PROCEDIMENTO_GLOSADO_SEM_MOTIVO_DE_GLOSA
				.getMessage(procedimento.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()));
		
		procedimento.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), procedimento.getMotivoGlosaProcedimento().getDescricao(), new Date());
		
		return null;
	}

	@Override
	public ItemGlosavel visit(ProcedimentoHonorario procedimentoHonorario) {
		Assert.isNotNull(procedimentoHonorario.getMotivoGlosa(), MensagemErroEnum.PROCEDIMENTO_GLOSADO_SEM_MOTIVO_DE_GLOSA
				.getMessage(procedimentoHonorario.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()));
		
		procedimentoHonorario.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), procedimentoHonorario.getMotivoGlosa(), new Date());
		return null;
	}

	
	@Override
	public ItemGlosavel visit(ProcedimentoOutros procedimentoOutros) {
		Assert.isNotNull(procedimentoOutros.getMotivoGlosaProcedimento(), MensagemErroEnum.PROCEDIMENTO_GLOSADO_SEM_MOTIVO_DE_GLOSA
				.getMessage(procedimentoOutros.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()));
		
		procedimentoOutros.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), procedimentoOutros.getMotivoGlosaProcedimento().getDescricao(), new Date());
		
		return null;

	}
}