package br.com.infowaypi.ecare.services.honorarios;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.services.honorarios.validators.GuiaHonorarioDataDeRealizacaoProcedimentoCirurgicoValidator;
import br.com.infowaypi.ecare.services.honorarios.validators.GuiaHonorarioDataDeSolicitacaoDaVisitaHospitalarValidator;
import br.com.infowaypi.ecare.services.honorarios.validators.GuiaHonorarioDataProcedimentoCirurgicoValidator;
import br.com.infowaypi.ecare.services.honorarios.validators.GuiaHonorarioHonorarioDuplicado;
import br.com.infowaypi.ecare.services.honorarios.validators.GuiaHonorarioMarcouOuInseriuPeloMenosUmProcedimentoOuPacoteValidate;
import br.com.infowaypi.ecare.services.honorarios.validators.GuiaHonorarioPacoteDeHonorarioCompativelComAGuiaValidator;
import br.com.infowaypi.ecare.services.honorarios.validators.GuiaHonorarioPorcentagemDataCirurgiaValidator;
import br.com.infowaypi.ecare.services.honorarios.validators.GuiaHonorarioProcedimentoComProfissionalEmGuiaPagaValidator;
import br.com.infowaypi.ecare.services.honorarios.validators.GuiaHonorarioProcedimentoSemProfissionalEmGuiaPagaAjusteTemporarioValidator;
import br.com.infowaypi.ecare.services.honorarios.validators.GuiaHonorarioProfissionalCredenciadoValidator;
import br.com.infowaypi.ecare.services.honorarios.validators.GuiaHonorarioProfissionalResponsavelDiferenteValidator;
import br.com.infowaypi.ecare.services.honorarios.validators.RegistrarHonorarioIndividualCommandRunner;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacoteHonorario;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GeradorGuiaHonorarioInterface;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.atendimentos.honorario.HonorarioUtils;
import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;


/**
 * Service responsável pelo registro (reclamação) de honorário individual
 * (externo). Durante esse processo, é informado um procedimento e o profissional
 * que teve participação nesse procedimento bem como o grau de participação. Ao 
 * fim desse fluxo, o procedimento deve estar na situação "Honorário Gerado" e 
 * a guia que contem o procedimento deve ter a GHM gerada (que contem o honorario)
 * como guiaFilha. É importante salientar que <b>caso o procedimento já possua 
 * honorários internos/IGFs gerados para ele, o IGF será removido enquanto o honorário
 * interno passará para a situação "Glosado(a)".</b>
 * 
 * @changes Eduardo
 *
 */
@SuppressWarnings({"unchecked"})
public class RegistrarHonorarioIndividual extends Service {
	
	private static final List<String> situacoesPermitidas = Arrays.asList(SituacaoEnum.ABERTO.descricao(), SituacaoEnum.AUDITADO.descricao(),
															SituacaoEnum.SOLICITADO_PRORROGACAO.descricao(), SituacaoEnum.PRORROGADO.descricao(), 
															SituacaoEnum.NAO_PRORROGADO.descricao(), SituacaoEnum.ALTA_REGISTRADA.descricao(), 
															SituacaoEnum.FECHADO.descricao(), SituacaoEnum.ENVIADO.descricao(), 
															SituacaoEnum.RECEBIDO.descricao(),SituacaoEnum.FATURADA.descricao(),SituacaoEnum.PAGO.descricao());
	
	public GeradorGuiaHonorarioInterface buscarGuia(String autorizacao, Prestador prestador) throws ValidateException {
		Assert.isTrue(prestador.isGeraHonorario(), "Este prestador não gera honorário individual");

		prestador.tocarObjetos();

		GuiaSimples guia = (GuiaSimples) HibernateUtil.currentSession().createCriteria(GuiaSimples.class)
																		.add(Expression.eq("autorizacao", autorizacao))
																		.uniqueResult();
		
		validaGuiaEncontrada(guia);		
		
		if (guia instanceof GeradorGuiaHonorarioInterface) {
			GeradorGuiaHonorarioInterface guiaComHonorario = (GeradorGuiaHonorarioInterface) guia;
			
			boolean geraHonorarioPessoaFisica = prestador.isGeraHonorarioPessoaFisica();
			guiaComHonorario.setGeracaoParaPrestadorMedico(geraHonorarioPessoaFisica);
			
			if (geraHonorarioPessoaFisica) {
				Profissional profissional = HonorarioUtils.getProfissional(prestador);
				guiaComHonorario.setProfissionalDoFluxo(profissional);
			}
			
			Set<ProcedimentoInterface> procedimentos = guiaComHonorario.getProcedimentosQueAindaPodemGerarHonorarios();
			for (ProcedimentoInterface procedimento : procedimentos) {
				tocarObjeto(procedimento);
			}
			
			guia.tocarObjetos();
			return guiaComHonorario;
		} else {
			throw new RuntimeException("A guia informada não pode gerar honorário.");
		}
	}

	public Profissional selecionarProfissional(GeradorGuiaHonorarioInterface guiaOrigem, Profissional profissional) {
		guiaOrigem.getAutorizacao();
		return profissional;
	}
	
	public GuiaHonorarioMedico informarHonorarios(Profissional profissional,
			int grauDeParticipacao, Collection<ItemPacoteHonorario> itensPacote, GeradorGuiaHonorarioInterface guiaOrigem,
			Collection<ProcedimentoHonorario> procedimentosOutros,
			Prestador prestador, UsuarioInterface usuario) throws Exception {
		
		GuiaCompleta<ProcedimentoInterface> guiaCompleta = (GuiaCompleta<ProcedimentoInterface>) guiaOrigem;
		AbstractSegurado segurado = guiaCompleta.getSegurado();
		
		RegistrarHonorarioIndividualCommandRunner commandRunner = new RegistrarHonorarioIndividualCommandRunner(
				new GuiaHonorarioProcedimentoComProfissionalEmGuiaPagaValidator(),
				new GuiaHonorarioProcedimentoSemProfissionalEmGuiaPagaAjusteTemporarioValidator(),
				new GuiaHonorarioProfissionalCredenciadoValidator(),
				new GuiaHonorarioProfissionalResponsavelDiferenteValidator(),
				new GuiaHonorarioPacoteDeHonorarioCompativelComAGuiaValidator(),
				new GuiaHonorarioMarcouOuInseriuPeloMenosUmProcedimentoOuPacoteValidate(),
				new GuiaHonorarioHonorarioDuplicado(),
				new GuiaHonorarioDataDeRealizacaoProcedimentoCirurgicoValidator(),
				new GuiaHonorarioPorcentagemDataCirurgiaValidator(),
				new GuiaHonorarioDataProcedimentoCirurgicoValidator(),
				new GuiaHonorarioDataDeSolicitacaoDaVisitaHospitalarValidator()
		);
		validaInformacoesFornecidas(profissional, grauDeParticipacao, guiaOrigem, segurado, prestador, procedimentosOutros, itensPacote, commandRunner);
		
		GuiaHonorarioMedico guiaHonorario;
		if(itensPacote.isEmpty()){
			guiaHonorario = GuiaHonorarioMedico.criarGuiaHonorario(profissional, grauDeParticipacao, guiaOrigem, usuario, segurado, prestador, procedimentosOutros); 
		} else {
			
			//Contingencia para bug da reclamação de honorário de pacotes contidos em guias pagas.
			if(guiaCompleta.getSituacao().getDescricao().equals(SituacaoEnum.PAGO.descricao())){
				throw new ValidateException("A guia de origem do pacote em questão já foi paga, portanto não mais sendo possível reclamar o honorário externamente.");
			}
			
			guiaHonorario = GuiaHonorarioMedico.criarGuiaHonorarioPacote(profissional, guiaOrigem, usuario, segurado, prestador, itensPacote);
		}
		tocarObjetosParaHonorario(guiaHonorario);
		
		return guiaHonorario;
	}

	public GuiaHonorarioMedico informarHonorarios(Profissional profissional, Collection<ItemPacoteHonorario> itensPacote,
			GeradorGuiaHonorarioInterface guiaOrigem, Collection<ProcedimentoHonorario> procedimentosOutros,
			Prestador prestador, UsuarioInterface usuario) throws Exception {
		
		GuiaCompleta<ProcedimentoInterface> guiaCompleta = (GuiaCompleta<ProcedimentoInterface>) guiaOrigem;
		AbstractSegurado segurado = guiaCompleta.getSegurado();

		RegistrarHonorarioIndividualCommandRunner commandRunner = new RegistrarHonorarioIndividualCommandRunner(
				new GuiaHonorarioProfissionalCredenciadoValidator(),
				new GuiaHonorarioMarcouOuInseriuPeloMenosUmProcedimentoOuPacoteValidate(),
				new GuiaHonorarioDataDeSolicitacaoDaVisitaHospitalarValidator(),
				new GuiaHonorarioDataProcedimentoCirurgicoValidator()
		);

		validaInformacoesFornecidas(profissional, GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo(), guiaOrigem, segurado, prestador, procedimentosOutros, itensPacote, commandRunner);
		
		GuiaHonorarioMedico guiaHonorario;
		if(itensPacote.isEmpty()){
			guiaHonorario = GuiaHonorarioMedico.criarGuiaHonorario(profissional, GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo(), guiaOrigem, usuario, segurado, prestador, procedimentosOutros); 
		} else{
			guiaHonorario = GuiaHonorarioMedico.criarGuiaHonorarioPacote(profissional, guiaOrigem, usuario, segurado, prestador, itensPacote);
		}
		tocarObjetosParaHonorario(guiaHonorario);

		return guiaHonorario;
	}

	private void validaInformacoesFornecidas(Profissional profissional,	Integer grauDeParticipacao, GeradorGuiaHonorarioInterface guiaOrigem,
												AbstractSegurado segurado, Prestador prestador,	Collection<ProcedimentoHonorario> procedimentosOutros,
												Collection<ItemPacoteHonorario> itensPacote, RegistrarHonorarioIndividualCommandRunner commandRunner) throws Exception {
		commandRunner.setValores(grauDeParticipacao, guiaOrigem, profissional, prestador, segurado, procedimentosOutros, itensPacote);
		commandRunner.execute();
	}

	public GuiaSimples conferirDados(GuiaHonorarioMedico guia) throws Exception {
		guia.removeHonorarioInternoAposGeracaoHonorarioExterno();
		guia.getGuiaOrigem().recalcularValores();
		super.salvarGuia(guia);
		ImplDAO.save(guia.getGuiaOrigem());
		
		return guia;
	}

	private void validaGuiaEncontrada(GuiaSimples guia) throws ValidateException {
		Assert.isNotNull(guia, "Nenhuma guia foi encontrada.");

		if (!situacoesPermitidas.contains(guia.getSituacao().getDescricao()))
			throw new ValidateException(MensagemErroEnumSR.GUIA_EM_SITUACAO_NAO_PERMITIDA.getMessage(guia.getAutorizacao(), guia.getSituacao().getDescricao()));
	}

	private void tocarObjeto(ProcedimentoInterface procedimento) {
		Profissional profissionalResponsavel = procedimento.getProfissionalResponsavel();
		if (profissionalResponsavel != null) {
			profissionalResponsavel.getPrestadores().size();
		}

		procedimento.getSituacoes().size();
		for (SituacaoInterface situcao: procedimento.getSituacoes()) {
			situcao.getUsuario();
		}
	}

	private void tocarObjetosParaHonorario(GuiaHonorarioMedico guiaHonorario) {
		guiaHonorario.getPrestador().tocarObjetos();
	}
}