package br.com.infowaypi.ecarebc.service.consultas;

import java.util.Collection;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoConsultaEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.service.Agente;
import br.com.infowaypi.ecarebc.service.MarcacaoService;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class MarcacaoConsultaService<S extends AbstractSegurado> extends MarcacaoService<GuiaConsulta<ProcedimentoInterface>> {
	
	private TipoConsultaEnum tipoConsulta;

	@Override
	public GuiaConsulta getGuiaInstanceFor(UsuarioInterface usuario) {
		return new GuiaConsulta(usuario);
	} 
	
	public GuiaConsulta criarGuiaPrestador(S segurado,
			TipoConsultaEnum tipoConsulta, 
			Prestador prestador,
			UsuarioInterface usuario,
			Profissional profissional, 
			Especialidade especialidade, 
			String dataDeAtendimento)
			throws Exception {
		
		Assert.isNotEquals(especialidade.getDescricao(), "Odontologia", MensagemErroEnum.ESPECIALIDADE_CONSULTA_INVALIDA.getMessage("Odontologia"));
		Assert.isNotEmpty(dataDeAtendimento, MensagemErroEnum.DATA_ATENDIMENTO_NAO_INFORMADA.getMessage());
		
		this.tipoConsulta = tipoConsulta;
		
		GuiaConsulta guiaGerada = criarGuia(segurado, prestador, usuario, null,
				profissional,especialidade,null,dataDeAtendimento, MotivoEnum.AGENDADA_NO_PRESTADOR.getMessage(), Agente.PRESTADOR);

		return guiaGerada;
	}
	
	public GuiaConsulta criarGuiaMarcador(S segurado,TipoConsultaEnum tipoConsulta,
			UsuarioInterface usuario) throws Exception {
		this.tipoConsulta = tipoConsulta;
		GuiaConsulta guiaGerada = criarGuia(segurado, null, usuario, null,null, null, null,
				Utils.hoje(), MotivoEnum.AGENDADA_NO_ecare.getMessage(), Agente.MARCADOR);

		return guiaGerada;
	}
	
	
	public GuiaConsulta criarGuiaLancamento(S segurado,TipoConsultaEnum tipoConsulta,
			String dataDeAtendimento, Prestador prestador, 
			Profissional profissional, Especialidade especialidade, 
			UsuarioInterface usuario) throws Exception {

		//setIgnoreValidacao(ignorarValidacao);
		this.tipoConsulta = tipoConsulta;
		
		String motivo = MotivoEnum.AGENDADA_NO_LANCAMENTO_MANUAL.getMessage();
		if(usuario.getRole().equals(Role.ROOT.getValor()))
			motivo = MotivoEnum.AGENDADA_PELO_TI.getMessage();
		if(usuario.getRole().equals(Role.CENTRAL_DE_SERVICOS.getValor()))
			motivo = MotivoEnum.AGENDADA_PELA_CENTRAL_DE_SERVICOS.getMessage();
		if(usuario.getRole().equals(Role.AUDITOR.getValor()))
			motivo = MotivoEnum.AGENDADA_PELO_AUDITOR.getMessage();
		if(usuario.getRole().equals(Role.DIGITADOR.getValor()))
			motivo = MotivoEnum.AGENDADA_PELO_DIGITADOR.getMessage();
		if(usuario.getRole().equals(Role.DIRETORIA_MEDICA.getValor()))
			motivo = MotivoEnum.AGENDADA_PELO_DIRETOR.getMessage();
		
		
		GuiaConsulta guiaGerada = criarGuia(segurado, prestador, usuario, null,profissional, especialidade,null,
				dataDeAtendimento, motivo, Agente.SUPERVISOR);
		
		return guiaGerada;
	}
	
	
	public void salvarGuia(GuiaSimples guia) throws Exception {
		guia.recalcularValores();
		super.salvarGuia(guia);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onAfterCriarGuia(GuiaConsulta<ProcedimentoInterface> guia, Collection<? extends ProcedimentoInterface> procedimentos) throws Exception {
		guia.addProcedimentoConsulta(this.tipoConsulta);
	}

}
