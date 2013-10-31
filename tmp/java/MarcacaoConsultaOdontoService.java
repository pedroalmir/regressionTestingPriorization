package br.com.infowaypi.ecarebc.service.odonto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoConsultaEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.service.Agente;
import br.com.infowaypi.ecarebc.service.MarcacaoService;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service básico para marcação de consultas odontológicas
 * 
 * <br>
 * Exemplo de uso:
 * 
 * <pre>
 * 
 *    
 * </pre>
 * 
 * <br>
 * Limitações:
 * 
 * @author Danilo Nogueira Portela
 * @version
 * @see br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdonto
 */
public class MarcacaoConsultaOdontoService<S extends AbstractSegurado> extends MarcacaoService<GuiaConsultaOdonto> {
	 
	public MarcacaoConsultaOdontoService(){
		super();
	}
	
	@Override
	public GuiaConsultaOdonto getGuiaInstanceFor(UsuarioInterface usuario) {
		return new GuiaConsultaOdonto(usuario);
	} 
	/**
	 * Cria uma guia de consulta Odontologia
	 * */
	public GuiaConsultaOdonto criarGuiaPrestador(S segurado, Prestador prestador, UsuarioInterface usuario, Profissional profissional, TipoConsultaEnum tipoConsulta) throws Exception {
		Boolean isProfissionalOdonto =  profissional.getEspecialidades().contains(this.getEspecialidadeOdonto());
		Boolean isPrestadorOdonto =  prestador.getEspecialidades().contains(this.getEspecialidadeOdonto());
		
		Assert.isTrue(isProfissionalOdonto, MensagemErroEnum.PROFISSIONAL_NAO_ATENDE_ESPECIALIDADE.getMessage("Odontologia"));
		Assert.isTrue(isPrestadorOdonto, MensagemErroEnum.PRESTADOR_NAO_ATENDE_ESPECIALIDADE.getMessage("Odontologia"));
		
		ArrayList<Procedimento> procedimentos = criarProcedimentoConsulta(tipoConsulta, usuario);
		
		GuiaConsultaOdonto guiaGerada = criarGuia(segurado, prestador, usuario, null, profissional, this.getEspecialidadeOdonto(tipoConsulta), procedimentos, Utils.format(new Date()), 
				MotivoEnum.AGENDADA_NO_PRESTADOR.getMessage(), Agente.PRESTADOR);
		
		return guiaGerada;
	}
	/**
	 * Método que sobrescreve a marcação de consulta odonto no SR. Este método recebe a especialidade.
	 * Que pode ser ODONTOLOGIA GERAL (procedimento 'Consulta ODONTOLOGIA GERAL' 99000001 ),
	 * ou outra especialidade odonto (procedimento 'Consulta Odontológica Especializada' 99000156)
	 */
	public GuiaConsultaOdonto criarGuiaPrestador(S segurado, Prestador prestador, UsuarioInterface usuario, Profissional profissional, Especialidade especialidade) throws Exception {
		Boolean isProfissionalOdonto =  profissional.getEspecialidades().contains(especialidade);
		Boolean isPrestadorOdonto =  prestador.getEspecialidades().contains(especialidade);
		
		Assert.isTrue(isProfissionalOdonto, MensagemErroEnum.PROFISSIONAL_NAO_ATENDE_ESPECIALIDADE.getMessage(especialidade.getDescricao()));
		Assert.isTrue(isPrestadorOdonto, MensagemErroEnum.PRESTADOR_NAO_ATENDE_ESPECIALIDADE.getMessage(especialidade.getDescricao()));
		
		ArrayList<Procedimento> procedimentos = new ArrayList<Procedimento>();
		if (especialidade.getDescricao().equals("ODONTOLOGIA GERAL")){
			procedimentos = criarProcedimentoConsulta(TipoConsultaEnum.CONSULTA_ODONTOLOGICA, usuario);
		} else {
			procedimentos = criarProcedimentoConsulta(TipoConsultaEnum.CONSULTA_ODONTOLOGICA_ESPECIALIZADA, usuario);
		}
		
		GuiaConsultaOdonto guiaGerada = criarGuia(segurado, prestador, usuario, null, profissional, especialidade, procedimentos, Utils.format(new Date()), 
				MotivoEnum.AGENDADA_NO_PRESTADOR.getMessage(), Agente.PRESTADOR);
		
		return guiaGerada;
	}

	private ArrayList<Procedimento> criarProcedimentoConsulta(TipoConsultaEnum tipoConsulta, UsuarioInterface usuario) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("codigo",tipoConsulta.getCodigo()));
		TabelaCBHPM procedimentoDaTabelaCBHPM = sa.uniqueResult(TabelaCBHPM.class);
		
		ArrayList<Procedimento> procedimentos = new ArrayList<Procedimento>();
		Procedimento procedimento = new Procedimento(usuario);
		procedimento.getSituacao().setDescricao(SituacaoEnum.AGENDADA.descricao());
		procedimento.setProcedimentoDaTabelaCBHPM(procedimentoDaTabelaCBHPM);
		procedimento.calcularCampos();
		procedimentos.add(procedimento);
		return procedimentos;
	}
	
	public GuiaConsultaOdonto criarGuiaMarcador(S segurado, UsuarioInterface usuario) throws Exception {
		GuiaConsultaOdonto guiaGerada = criarGuia(segurado, null, usuario, null,null, this.getEspecialidadeOdonto(), null,
				Utils.hoje(), MotivoEnum.CONFIRMADA_NO_ecare.getMessage(), Agente.MARCADOR);
		
		return guiaGerada;
	}
	
		
	public GuiaConsultaOdonto criarGuiaLancamento(S segurado, Boolean ignorarValidacao, 
			Prestador prestador, Profissional profissional, UsuarioInterface usuario) throws Exception {
		
		setIgnoreValidacao(ignorarValidacao);
			
		Boolean isProfissionalOdonto =  profissional.getEspecialidades().contains(this.getEspecialidadeOdonto());
		Boolean isPrestadorOdonto =  prestador.getEspecialidades().contains(this.getEspecialidadeOdonto());
		
		Assert.isTrue(isProfissionalOdonto, MensagemErroEnum.PROFISSIONAL_NAO_ATENDE_ESPECIALIDADE.getMessage("Odontologia"));
		Assert.isTrue(isPrestadorOdonto, MensagemErroEnum.PROFISSIONAL_NAO_ATENDE_ESPECIALIDADE.getMessage("Odontologia"));
		
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
		
		
		
		GuiaConsultaOdonto guiaGerada = criarGuia(segurado, prestador, usuario, null,profissional, this.getEspecialidadeOdonto(), null,
				Utils.format(new Date()), motivo, Agente.SUPERVISOR);
		
		guiaGerada.getSituacao().setDescricao(SituacaoEnum.AGENDADA.descricao());
		
		return guiaGerada;
	}
	
	public void salvarGuia(GuiaSimples guia) throws Exception {
		super.salvarGuia(guia);
	}
	
	@Override
	public  void onAfterCriarGuia(GuiaConsultaOdonto guia, Collection<? extends ProcedimentoInterface> procedimentos) throws Exception {
		guia.setDataTerminoAtendimento(guia.getDataAtendimento());
		guia.addAllProcedimentos(procedimentos);
	}

}
