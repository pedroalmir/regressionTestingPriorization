package br.com.infowaypi.ecarebc.atendimentos.honorario;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.Diaria;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Pacote;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings({"unchecked"})
public class GeradorGuiaHonorarioComponent implements GeradorGuiaHonorarioInterface{

	private final Integer PORTE_ANESTESICO_7 = 7;
	private final Integer PORTE_ANESTESICO_8 = 8;

	private GuiaCompleta guia;
	private Profissional profissionalDoFluxo;
	private boolean geracaoParaPrestadorMedico;

	public GeradorGuiaHonorarioComponent(GuiaCompleta guia) {
		this.guia = guia;
	}

	@Override
	public Set<Diaria> getAcomodacoes() {
		Set<Diaria> acomodacoes = new HashSet<Diaria>();
		for (ItemDiaria itemDiaria : (Set<ItemDiaria>) guia.getItensDiaria()) {
			acomodacoes.add(itemDiaria.getDiaria());
		}
		return acomodacoes;
	}

	@Override
	public Set<ProcedimentoInterface> getProcedimentosAptosAGerarHonorariosMedicos() {	
		Set<ProcedimentoInterface> procedimentosComHonorarios = new HashSet<ProcedimentoInterface>();

		getProcedimentosAptosAGerarHonorarios(procedimentosComHonorarios);

		excluirProcedimentosContidosEmPacotes(procedimentosComHonorarios);	

		return procedimentosComHonorarios;
	}

	@Override
	public Set<ProcedimentoInterface> getProcedimentosAptosAGerarHonorariosAnestesista() {	
		Set<ProcedimentoInterface> procedimentosComHonorarios = new HashSet<ProcedimentoInterface>();

		getProcedimentosAptosAGerarHonorarios(procedimentosComHonorarios);

		return procedimentosComHonorarios;
	}

	private void getProcedimentosAptosAGerarHonorarios(Set<ProcedimentoInterface> procedimentosComHonorarios) {
		for (ProcedimentoInterface procedimento : (Set<ProcedimentoInterface>) guia.getProcedimentosCirurgicosNaoCanceladosENegadosNaoNaoAutorizadosENaoSolicitados()) {
			
			boolean isFaturadoHonorarioPassivo = procedimento.isSituacaoAtual(SituacaoEnum.FATURADA_PASSIVO_HONORARIO.descricao());
			
			if(!isFaturadoHonorarioPassivo && procedimento.getProcedimentoDaTabelaCBHPM().getPorteAnestesicoFormatado() > 0) {
				procedimentosComHonorarios.add(procedimento);
			}
		}
	}

	private void excluirProcedimentosContidosEmPacotes(Set<ProcedimentoInterface> procedimentosComHonorarios) {
		Set<ItemPacote> itensPacotes = guia.getItensPacoteNaoCanceladosENegados();
		List<AcordoPacote> acordosPacotes = this.getAcordosDosPacotesContidosNaGuia(itensPacotes);
		
		for (Iterator iterator = procedimentosComHonorarios.iterator(); iterator.hasNext();) {
			ProcedimentoInterface procedimento = (ProcedimentoInterface) iterator.next();

			boolean excluiProcedimento = excluiProcedimentosContidosEmPacotes(procedimento, acordosPacotes);
			
			if (excluiProcedimento){
				iterator.remove();
			}
		}
	}

	private List<AcordoPacote> getAcordosDosPacotesContidosNaGuia(Set<ItemPacote> itensPacotes) {
		List<AcordoPacote> acordosPacotes = new ArrayList<AcordoPacote>();
		
		for (ItemPacote itemPacote : itensPacotes) {
			acordosPacotes.add(guia.getPrestador().getAcordoPacote(itemPacote.getPacote())); 
		}
		return acordosPacotes;
	}

	private boolean excluiProcedimentosContidosEmPacotes(ProcedimentoInterface procedimento, List<AcordoPacote>  acordosPacotes) {
		TabelaCBHPM procedimentoDaTabela = procedimento.getProcedimentoDaTabelaCBHPM();

		for (AcordoPacote acordoPacote: acordosPacotes){
			if(acordoPacote != null){
				Pacote pacote = acordoPacote.getPacote();
				Set<TabelaCBHPM> procedimentos = (Set<TabelaCBHPM>)pacote.getProcedimentosCBHPM();

				boolean containsProcedimento = procedimentos.contains(procedimentoDaTabela); 
				boolean acordoIncluiHonorario = acordoPacote.getIncluiHonorario();

				if (containsProcedimento && acordoIncluiHonorario){
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public Set<ProcedimentoHonorario> getProcedimentosHonorario() {
		return guia.getProcedimentos(ProcedimentoHonorario.class);
	}

	@Override
	public Set<ProcedimentoInterface> getProcedimentosQueVaoGerarHonorario() {
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		Set<ProcedimentoInterface> procedimentosQueVaoGerarHonorario = this.getProcedimentosAptosAGerarHonorariosMedicos();
		for (ProcedimentoInterface procedimento : procedimentosQueVaoGerarHonorario) {
			Boolean marcadoParaGerarHonorario = procedimento.getAdicionarHonorario();
			if (marcadoParaGerarHonorario != null) {
				if (marcadoParaGerarHonorario) {
					procedimentos.add(procedimento);
				}
			}
		}
		return procedimentos;

	}
	
	@Override
	public Set<ProcedimentoInterface> getProcedimentosQueVaoGerarHonorarioAnestesista() {
		Set<ProcedimentoInterface> procedimentos = new HashSet<ProcedimentoInterface>();
		Set<ProcedimentoInterface> procedimentosQueVaoGerarHonorario = this.getProcedimentosAptosAGerarHonorariosAnestesista();
		for (ProcedimentoInterface procedimento : procedimentosQueVaoGerarHonorario) {
			Boolean marcadoParaGerarHonorario = procedimento.getAdicionarHonorario();
			if (marcadoParaGerarHonorario != null) {
				if (marcadoParaGerarHonorario) {
					procedimentos.add(procedimento);
				}
			}
		}
		return procedimentos;

	}

	@Override
	public Set<ProcedimentoInterface> getProcedimentosQueAindaPodemGerarHonorariosAnestesitas() {
		Set<ProcedimentoInterface> procedimentosQueAindaPodemGerarHonorario = new HashSet<ProcedimentoInterface>();
		Set<ProcedimentoInterface> procedimentosQuePodemGerarHonorario = this.getProcedimentosAptosAGerarHonorariosAnestesista();

		for (ProcedimentoInterface procedimento : procedimentosQuePodemGerarHonorario) {
			boolean aindaGeraHonorario = aindaGeraHonorarioAnestesista(procedimento);

			if (aindaGeraHonorario) 
				procedimentosQueAindaPodemGerarHonorario.add(procedimento);
		}

		return procedimentosQueAindaPodemGerarHonorario;
	}

	@Override 
	public Set<ProcedimentoCirurgico> getProcedimentosCirurgicosQueAindaPodemGerarHonorariosAnestesitas() {
		return ProcedimentoUtils.getProcedimentosDaHeranca(ProcedimentoCirurgico.class, getProcedimentosQueAindaPodemGerarHonorariosAnestesitas());
	}
	
	@Override 
	public Set<Procedimento> getProcedimentosNormaisQueAindaPodemGerarHonorariosAnestesitas() {
		return ProcedimentoUtils.getProcedimentosDaClasse(Procedimento.class, getProcedimentosQueAindaPodemGerarHonorariosAnestesitas());
	}

	private boolean aindaGeraHonorarioAnestesista(ProcedimentoInterface procedimento){
		boolean geraHonorarioAuxiliarAnestesista = false;
		boolean aindaGeraHonorario 				 = false;
		boolean temHonorarioAnestesita 			 = false;
		boolean temHonorarioAuxiliarAnestesita 	 = false;

		Set<Honorario> honorarios = procedimento.getTodosOsHonorariosDoProcedimento();
		Integer porteAnestesico = procedimento.getProcedimentoDaTabelaCBHPM().getPorteAnestesicoFormatado();

		boolean isPorte7 = porteAnestesico.equals(PORTE_ANESTESICO_7);
		boolean isPorte8 = porteAnestesico.equals(PORTE_ANESTESICO_8); 

		if ( isPorte7 || isPorte8 )
			geraHonorarioAuxiliarAnestesista = true;

		for (Honorario honorario : honorarios) {
			int grauDeParticipacao = honorario.getGrauDeParticipacao();

			boolean isGrauAnestesita = GrauDeParticipacaoEnum.ANESTESISTA.getCodigo().equals(grauDeParticipacao);
			if (isGrauAnestesita)
				temHonorarioAnestesita = true;

			boolean isGrauAuxiliarAnestesita = (GrauDeParticipacaoEnum.AUXILIAR_ANESTESISTA.getCodigo().equals(grauDeParticipacao)); 
			if (isGrauAuxiliarAnestesita || !geraHonorarioAuxiliarAnestesista)
				temHonorarioAuxiliarAnestesita = true;
		}

		aindaGeraHonorario = (temHonorarioAnestesita && temHonorarioAuxiliarAnestesita);

		return !aindaGeraHonorario;
	}

	@Override
	public Set<ProcedimentoInterface> getProcedimentosQueAindaPodemGerarHonorarios() {
		Set<ProcedimentoInterface> procedimentosQueAindaPodemGerarHonorario = new HashSet<ProcedimentoInterface>();
		Set<ProcedimentoInterface> procedimentosQuePodemGerarHonorario = this.getProcedimentosAptosAGerarHonorariosMedicos();

		for (ProcedimentoInterface procedimento : procedimentosQuePodemGerarHonorario) {
//			daqui foi retirado um código que só mostrava os honorários se o prestador da sessao fosse omedico responsavel do procedimento

			boolean aindaGeraHonorario = aindaGeraHonorario(procedimento);
			if (aindaGeraHonorario) {
				procedimentosQueAindaPodemGerarHonorario.add(procedimento);
			}
		}

		return procedimentosQueAindaPodemGerarHonorario;
	}

	private boolean aindaGeraHonorario(ProcedimentoInterface procedimento){
		boolean aindaGeraHonorario 				= false;
		boolean temHonorarioResponsavel 		= false;
		boolean temHonorarioPrimeiroAuxiliar 	= false;
		boolean temHonorarioSegundoAuxiliar 	= false;
		boolean temHonorarioTereceiroAuxiliar 	= false;

		Set<Honorario> honorarios = procedimento.getTodosOsHonorariosDoProcedimento();

		for (Honorario honorario : honorarios) {
			int grauDeParticipacao = honorario.getGrauDeParticipacao();

			if (GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo().equals(grauDeParticipacao))
				temHonorarioResponsavel = true;

			if (GrauDeParticipacaoEnum.PRIMEIRO_AUXILIAR.getCodigo().equals(grauDeParticipacao))
				temHonorarioPrimeiroAuxiliar = true;

			if (GrauDeParticipacaoEnum.SEGUNDO_AUXILIAR.getCodigo().equals(grauDeParticipacao))
				temHonorarioSegundoAuxiliar = true;

			if (GrauDeParticipacaoEnum.TERCEIRO_AUXILIAR.getCodigo().equals(grauDeParticipacao))
				temHonorarioTereceiroAuxiliar = true;
		}
		aindaGeraHonorario = (temHonorarioResponsavel && temHonorarioPrimeiroAuxiliar && temHonorarioSegundoAuxiliar && temHonorarioTereceiroAuxiliar);

		return !aindaGeraHonorario;
	}

	@Override
	public boolean isPossuiProcdimentosQueAindaGeramHonorarios() {
		if (this.getProcedimentosQueAindaPodemGerarHonorarios().size() > 0 )
			return true;

		return false;
	}

	@Override
	public ProcedimentoCirurgicoInterface getProcedimentoMaisRecenteRealizadoPeloProfissional(Profissional profissional) {

		List<ProcedimentoCirurgico> procedimentosRealizadosPeloProfissional = getProcedimentosRealizadosPeloProfissional(profissional);
		int ultimaPosicao;

		Utils.sort(procedimentosRealizadosPeloProfissional, "dataRealizacao");

		if (procedimentosRealizadosPeloProfissional.size() > 0){
			ultimaPosicao = (procedimentosRealizadosPeloProfissional.size() - 1);
		} else {
			return null;
		}

		ProcedimentoCirurgicoInterface procedimentoMaisRecente = procedimentosRealizadosPeloProfissional.get(ultimaPosicao);

		return procedimentoMaisRecente;
	}

	private List<ProcedimentoCirurgico> getProcedimentosRealizadosPeloProfissional(Profissional profissional){

		Set<ProcedimentoCirurgico> procdimentoCirurgicos = guia.getProcedimentosCirurgicosNaoCanceladosENegados();
		List<ProcedimentoCirurgico> procedimentosRealizadosPeloProfissional = new ArrayList<ProcedimentoCirurgico>();

		for (ProcedimentoCirurgico procedimentoCirurgico : procdimentoCirurgicos) {
			Profissional profissionalResponsavel = procedimentoCirurgico.getProfissionalResponsavel();
			Date dataRealizacao = procedimentoCirurgico.getDataRealizacao();

			if (profissionalResponsavel != null && dataRealizacao != null) {
				if (profissionalResponsavel.equals(profissional)) {
					procedimentosRealizadosPeloProfissional.add(procedimentoCirurgico);
				}
			}
		}

		return procedimentosRealizadosPeloProfissional;
	}


	@Override
	public boolean isProfissionalPodeRegistrarHonorarioIndividual(Profissional profissional) {
		for (ProcedimentoInterface proc : (Set<ProcedimentoInterface>) guia.getProcedimentosNaoCanceladosENegados()) {
			if (proc.getProfissionalResponsavel() == null) {
				return true;
			}
		}
		if (this.getProcedimentoMaisRecenteRealizadoPeloProfissional(profissional) != null) {
			return true;
		}
		return false;
	}

	@Override
	public Set<ProcedimentoCirurgicoInterface> getProcedimentoQueJaPossuemDataDeRealizacao() {
		Set<ProcedimentoInterface> procedimentos = this.getProcedimentosAptosAGerarHonorariosMedicos();
		Set<ProcedimentoCirurgicoInterface> procedimentosComData = new HashSet<ProcedimentoCirurgicoInterface>();

		for (ProcedimentoInterface procedimento : procedimentos) {
			if (procedimento instanceof ProcedimentoCirurgico){
				ProcedimentoCirurgico procedimentoCirurgico = (ProcedimentoCirurgico) procedimento;
				if (procedimentoCirurgico.getDataRealizacao() != null)
					procedimentosComData.add(procedimentoCirurgico);
			}
		}

		return procedimentosComData;
	}

	@Override
	public Set<ProcedimentoCirurgicoInterface> getProcedimentoQueNaoPossuemDataDeRealizacao() {
		Set<ProcedimentoInterface> procedimentos = this.getProcedimentosAptosAGerarHonorariosMedicos();
		Set<ProcedimentoCirurgicoInterface> procedimetosSemData = new HashSet<ProcedimentoCirurgicoInterface>();

		for (ProcedimentoInterface procedimento : procedimentos) {
			if (procedimento instanceof ProcedimentoCirurgico){
				ProcedimentoCirurgico procedimentoCirurgico = (ProcedimentoCirurgico) procedimento;
				if (procedimentoCirurgico.getDataRealizacao() == null)
					procedimetosSemData.add(procedimentoCirurgico);
			}
		}

		return procedimetosSemData;
	}

	@Override
	public Set<Procedimento> getProcedimentosExameQueGeraramHonorariosExternos() {
		Set<Procedimento> procedimentos = new HashSet<Procedimento>();
		
		Set<ProcedimentoInterface> procedimentosDaGuia = guia.getProcedimentosNaoCanceladosENegados();
		for (ProcedimentoInterface procedimento : procedimentosDaGuia) {
			boolean possuiHonorarioExterno = !procedimento.getHonorariosExternosNaoCanceladosEGlosados().isEmpty();
			if (possuiHonorarioExterno) {
				procedimentos.add((Procedimento) procedimento);
			}
		}
		
		return procedimentos;
	}

	
	public Set<ProcedimentoCirurgico> getProcedimentosQueGeraramHonorariosExternos() {
		Set<ProcedimentoCirurgico> procedimentos = new HashSet<ProcedimentoCirurgico>();
		
		Set<ProcedimentoInterface> procedimentosDaGuia = guia.getProcedimentosCirurgicosNaoCanceladosENegados();
		for (ProcedimentoInterface procedimento : procedimentosDaGuia) {
			boolean possuiHonorarioExterno = !procedimento.getHonorariosExternosNaoCanceladosEGlosados().isEmpty();
			if (possuiHonorarioExterno) {
				procedimentos.add((ProcedimentoCirurgico)procedimento);
			}
		}
		
		return procedimentos;
	}
	
	@Override
	public String getAutorizacao() {
		return guia.getAutorizacao();
	}

	@Override
	public void setProfissionalDoFluxo(Profissional profissional) {
		this.profissionalDoFluxo = profissional;
	}

	@Override
	public void setGeracaoParaPrestadorMedico(boolean geracaoParaPrestadorMedico) {
		this.geracaoParaPrestadorMedico = geracaoParaPrestadorMedico;
	}

	@Override
	public int getPrioridadeEmAuditoria() {
		int result;
		boolean algumaAuditada = false;
		boolean algumaRecebida = false;
		
		Set<GuiaHonorarioMedico> guiasFilhas = guia.getGuiasFilhasDeHonorarioMedicoAptasPraAuditoria();
		for (GuiaHonorarioMedico guia : guiasFilhas) {
			boolean isGuiaAuditadaOuCancelada = guia.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao()) ||
					guia.isSituacaoAtual(SituacaoEnum.GLOSADO.descricao()) ||
					guia.isSituacaoAtual(SituacaoEnum.FATURADA.descricao()) ||
					guia.isSituacaoAtual(SituacaoEnum.PAGO.descricao()) ||
					guia.isSituacaoAtual(SituacaoEnum.INAPTO.descricao()) ||
					guia.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
			
			if (isGuiaAuditadaOuCancelada 
					) {
				algumaAuditada = true;
			} else {
				algumaRecebida = true;
			}
			
			if (algumaAuditada && algumaRecebida) {
				break;
			}
		}
		
		if (algumaAuditada && algumaRecebida) {
			result = 1; // as guias filhas estão em ambas as situações.
		} else if (algumaRecebida) {
			result = 0; // todas as guias filhas estão recebidas.
		} else {
			result = 2; // todas as guias filhas estão auditadas.
		}
		
		return result;
	}

	@Override
	public Set<GuiaHonorarioMedico> getGuiasFilhasDeHonorarioMedico() {
		return null;
	}

}

