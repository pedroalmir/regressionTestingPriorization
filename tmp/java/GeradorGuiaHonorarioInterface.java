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
 * Interface que marca uma guia que pode gerar Honorário.
 * 
 * @author Eduardo
 *
 */
public interface GeradorGuiaHonorarioInterface {

	/**
	 * Verifica se possui o primeiro auxiliar ou anestesista.
	 * Não é verificado se possui segundo ou terceiro auxiliar pois não é permitida a 
	 * inserção desses sem a existência de um primeiro auxiliar.
	 * 
	 * @return todos os procedimentos com honorários médicos.
	 */
	public Set<ProcedimentoInterface> getProcedimentosAptosAGerarHonorariosMedicos();
	
	/**
	 * Retorna os procedimentos aptos para geração de honorários para anestesita e auxiliar de anestesista.
	 * @return
	 */
	public Set<ProcedimentoInterface> getProcedimentosAptosAGerarHonorariosAnestesista();
	/**
	 * @return todas as diárias da guia.
	 */
	public Set<Diaria> getAcomodacoes();
	
	/**
	 * 
	 * @return procedimentos a ser adicionados no fluxo de "Registrar Honorário"
	 */
	public Set<ProcedimentoHonorario> getProcedimentosHonorario();

	/**
	 * 
	 * @return procedimentos marcados para gerar honorario no fluxo de "Registrar Honorário" para o prestador não-anestesista
	 */
	public Set<ProcedimentoInterface> getProcedimentosQueVaoGerarHonorario();
	
	/**
	 * 
	 * @return procedimentos marcados para gerar honorario no fluxo de "Registrar Honorário" para o prestador Anestesista
	 */
	public Set<ProcedimentoInterface> getProcedimentosQueVaoGerarHonorarioAnestesista();
	
	/**
	 * 
	 * @return Os procedimentos que ainda não geraram todos os honorários anestesistas possíveis. Especifico para prestador anestesista.  
	 */
	public Set<ProcedimentoInterface> getProcedimentosQueAindaPodemGerarHonorariosAnestesitas();
	
	/**
	 * Retorna os procedimentos Cirurgicos que ainda não geraram todos os honorários anestesistas possíveis. Especifico para prestador anestesista.
	 * @return
	 */
	public Set<ProcedimentoCirurgico> getProcedimentosCirurgicosQueAindaPodemGerarHonorariosAnestesitas();
	
	/**
	 * Retorna os procedimentos Normais que ainda não geraram todos os honorários anestesistas possíveis. Especifico para prestador anestesista.
	 * @return
	 */
	public Set<Procedimento> getProcedimentosNormaisQueAindaPodemGerarHonorariosAnestesitas();
	
	public Set<Procedimento> getProcedimentosExameQueGeraramHonorariosExternos();
	
	/**
	 * 
	 * @return Os procedimentos que ainda não geraram todos os honorários possíveis. Especifico para prestadores não anestesistas. 
	 */
	public Set<ProcedimentoInterface> getProcedimentosQueAindaPodemGerarHonorarios();
	
	
	/**
	 * 
	 * @return Booleano que indica se uma guia possui ou não procdimentos ainda apptos para geracao de honorarios. Específico para prestadores não anestesistas. 
	 */
	public boolean isPossuiProcdimentosQueAindaGeramHonorarios();
	
	
	/**
	 * 
	 * @return Retorna o procedimento cirurgico mais recente realizado por um profissional. Específico para prestadores não anestesistas. 
	 */
	public ProcedimentoCirurgicoInterface getProcedimentoMaisRecenteRealizadoPeloProfissional(Profissional profissional);
	
	/**
	 * 
	 * @return boleano que indica se um profissinal (prestador pessoa física) pode gerar honorários para esta guia.
	 * As condições são 2:
	 * <ol>
	 * <li> O profissional é responsável por algum procedimento cirúrgico na guia origem OU</li>
	 * <li> A guia origem possui pelo menos um procedimento que não tenha profissional responsável.</li>
	 * </ol>
	 * 
	 */
	public boolean isProfissionalPodeRegistrarHonorarioIndividual(Profissional profissional);
	
	/**
	 * 
	 * @return Método usado para capturar a coleção de procediementos cirúrgicos que já possuem data de realização. Método criado para ser usado
	 * no fluxo de registro de honorário inidividual para impedir que sejam alteradas as datas de procedimentos que já tenham data de realização.
	 */
	public Set<ProcedimentoCirurgicoInterface> getProcedimentoQueJaPossuemDataDeRealizacao();
	
	/**
	 * 
	 * @return Método usado para capturar a coleção de procediementos cirúrgicos que não possuem data de realização. Método criado para ser usado
	 * no fluxo de registro de honorário inidividual para para que sejam feitas as devidas validações sobre estes procedimentos.
	 */
	public Set<ProcedimentoCirurgicoInterface> getProcedimentoQueNaoPossuemDataDeRealizacao();
	
	/**
	 * 
	 * @return a autorização da guia.
	 */
	public String getAutorizacao();

	/**
	 * Seta o profissional que vai ser usado no fluxo de Registrar honorarios para que seja exibida apenas os procedimentos que pdem ser 
	 * modificados por ele.
	 */
	public void setProfissionalDoFluxo(Profissional profissional);

	/**
	 * Indica se o honorário está sendo gerado para presador pessoa fisica ou juridica
	 * @param geracaoParaHonorarioMedico <code>true</code> para prestador pessoaFisica, 
	 * <code>false</code> caso contrario
	 */
	public void setGeracaoParaPrestadorMedico(boolean geracaoParaHonorarioMedico);

	/**
	 * 	
	 * @return Procedimentos não glosados que geraram honorarios para auditoria.
	 */
	public Set<ProcedimentoCirurgico> getProcedimentosQueGeraramHonorariosExternos();
	
	/**
	 * Retorna um valor numerico que representa se uma guia foi totalmente ou parcialmente auditada ou se não foi auditada.
	 * @return 
	 * <ul>
	 * 		<li>Para guia não auditada, o retorno é 0(zero);</li>
	 * 		<li>Para guia parcialmente auditada, o retorno é 1(um);</li>
	 * 		<li>Para guia totalmente, auditada o retorno é 2(dois);</li>
	 * 	</ul>
	 */
	public int getPrioridadeEmAuditoria();
	
	public Set<GuiaHonorarioMedico> getGuiasFilhasDeHonorarioMedico();
}
