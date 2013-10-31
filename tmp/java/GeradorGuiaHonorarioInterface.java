package br.com.infowaypi.ecarebc.atendimentos.honorario;

import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.Diaria;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;


/**
 * 
 * Interface que marca uma guia que pode gerar Honor�rio.
 * 
 * @author Eduardo
 *
 */
public interface GeradorGuiaHonorarioInterface {

	/**
	 * Verifica se possui o primeiro auxiliar ou anestesista.
	 * N�o � verificado se possui segundo ou terceiro auxiliar pois n�o � permitida a 
	 * inser��o desses sem a exist�ncia de um primeiro auxiliar.
	 * 
	 * @return todos os procedimentos com honor�rios m�dicos.
	 */
	public Set<ProcedimentoInterface> getProcedimentosAptosAGerarHonorariosMedicos();
	
	/**
	 * Retorna os procedimentos aptos para gera��o de honor�rios para anestesita e auxiliar de anestesista.
	 * @return
	 */
	public Set<ProcedimentoInterface> getProcedimentosAptosAGerarHonorariosAnestesista();
	/**
	 * @return todas as di�rias da guia.
	 */
	public Set<Diaria> getAcomodacoes();
	
	/**
	 * 
	 * @return procedimentos a ser adicionados no fluxo de "Registrar Honor�rio"
	 */
	public Set<ProcedimentoHonorario> getProcedimentosHonorario();

	/**
	 * 
	 * @return procedimentos marcados para gerar honorario no fluxo de "Registrar Honor�rio" para o prestador n�o-anestesista
	 */
	public Set<ProcedimentoInterface> getProcedimentosQueVaoGerarHonorario();
	
	/**
	 * 
	 * @return procedimentos marcados para gerar honorario no fluxo de "Registrar Honor�rio" para o prestador Anestesista
	 */
	public Set<ProcedimentoInterface> getProcedimentosQueVaoGerarHonorarioAnestesista();
	
	/**
	 * 
	 * @return Os procedimentos que ainda n�o geraram todos os honor�rios anestesistas poss�veis. Especifico para prestador anestesista.  
	 */
	public Set<ProcedimentoInterface> getProcedimentosQueAindaPodemGerarHonorariosAnestesitas();
	
	/**
	 * Retorna os procedimentos Cirurgicos que ainda n�o geraram todos os honor�rios anestesistas poss�veis. Especifico para prestador anestesista.
	 * @return
	 */
	public Set<ProcedimentoCirurgico> getProcedimentosCirurgicosQueAindaPodemGerarHonorariosAnestesitas();
	
	/**
	 * Retorna os procedimentos Normais que ainda n�o geraram todos os honor�rios anestesistas poss�veis. Especifico para prestador anestesista.
	 * @return
	 */
	public Set<Procedimento> getProcedimentosNormaisQueAindaPodemGerarHonorariosAnestesitas();
	
	public Set<Procedimento> getProcedimentosExameQueGeraramHonorariosExternos();
	
	/**
	 * 
	 * @return Os procedimentos que ainda n�o geraram todos os honor�rios poss�veis. Especifico para prestadores n�o anestesistas. 
	 */
	public Set<ProcedimentoInterface> getProcedimentosQueAindaPodemGerarHonorarios();
	
	
	/**
	 * 
	 * @return Booleano que indica se uma guia possui ou n�o procdimentos ainda apptos para geracao de honorarios. Espec�fico para prestadores n�o anestesistas. 
	 */
	public boolean isPossuiProcdimentosQueAindaGeramHonorarios();
	
	
	/**
	 * 
	 * @return Retorna o procedimento cirurgico mais recente realizado por um profissional. Espec�fico para prestadores n�o anestesistas. 
	 */
	public ProcedimentoCirurgicoInterface getProcedimentoMaisRecenteRealizadoPeloProfissional(Profissional profissional);
	
	/**
	 * 
	 * @return boleano que indica se um profissinal (prestador pessoa f�sica) pode gerar honor�rios para esta guia.
	 * As condi��es s�o 2:
	 * <ol>
	 * <li> O profissional � respons�vel por algum procedimento cir�rgico na guia origem OU</li>
	 * <li> A guia origem possui pelo menos um procedimento que n�o tenha profissional respons�vel.</li>
	 * </ol>
	 * 
	 */
	public boolean isProfissionalPodeRegistrarHonorarioIndividual(Profissional profissional);
	
	/**
	 * 
	 * @return M�todo usado para capturar a cole��o de procediementos cir�rgicos que j� possuem data de realiza��o. M�todo criado para ser usado
	 * no fluxo de registro de honor�rio inidividual para impedir que sejam alteradas as datas de procedimentos que j� tenham data de realiza��o.
	 */
	public Set<ProcedimentoCirurgicoInterface> getProcedimentoQueJaPossuemDataDeRealizacao();
	
	/**
	 * 
	 * @return M�todo usado para capturar a cole��o de procediementos cir�rgicos que n�o possuem data de realiza��o. M�todo criado para ser usado
	 * no fluxo de registro de honor�rio inidividual para para que sejam feitas as devidas valida��es sobre estes procedimentos.
	 */
	public Set<ProcedimentoCirurgicoInterface> getProcedimentoQueNaoPossuemDataDeRealizacao();
	
	/**
	 * 
	 * @return a autoriza��o da guia.
	 */
	public String getAutorizacao();

	/**
	 * Seta o profissional que vai ser usado no fluxo de Registrar honorarios para que seja exibida apenas os procedimentos que pdem ser 
	 * modificados por ele.
	 */
	public void setProfissionalDoFluxo(Profissional profissional);

	/**
	 * Indica se o honor�rio est� sendo gerado para presador pessoa fisica ou juridica
	 * @param geracaoParaHonorarioMedico <code>true</code> para prestador pessoaFisica, 
	 * <code>false</code> caso contrario
	 */
	public void setGeracaoParaPrestadorMedico(boolean geracaoParaHonorarioMedico);

	/**
	 * 	
	 * @return Procedimentos n�o glosados que geraram honorarios para auditoria.
	 */
	public Set<ProcedimentoCirurgico> getProcedimentosQueGeraramHonorariosExternos();
	
	/**
	 * Retorna um valor numerico que representa se uma guia foi totalmente ou parcialmente auditada ou se n�o foi auditada.
	 * @return 
	 * <ul>
	 * 		<li>Para guia n�o auditada, o retorno � 0(zero);</li>
	 * 		<li>Para guia parcialmente auditada, o retorno � 1(um);</li>
	 * 		<li>Para guia totalmente, auditada o retorno � 2(dois);</li>
	 * 	</ul>
	 */
	public int getPrioridadeEmAuditoria();
	
	public Set<GuiaHonorarioMedico> getGuiasFilhasDeHonorarioMedico();
}
