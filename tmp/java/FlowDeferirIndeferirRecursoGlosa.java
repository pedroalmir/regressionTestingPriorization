package br.com.infowaypi.ecare.services.recurso;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.FlushMode;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe que abstrai o fluxo de deferimento/indeferimento de recursos de glosa.
 * @author Luciano Rocha
 *
 */
public class FlowDeferirIndeferirRecursoGlosa {

	private UsuarioInterface usuarioDoFluxo;
	
	/**
	 * Método que faz a busca dos recursos de glosa de acordo com os parâmetros passados.  
	 * @author Luciano Rocha
	 * @param autorizacao
	 * @param dataInicial
	 * @param dataFinal
	 * @param prestador
	 * @param situacao
	 * @return
	 * @throws ValidateException
	 */
	public ResumoDeferimentoIndeferimentoDeRecurso buscarRecursos(
			String autorizacao, String dataInicial, String dataFinal,
			UsuarioInterface usuario, Prestador prestador, String situacao)
			throws ValidateException {
		
		this.usuarioDoFluxo = usuario;
		
		List<GuiaRecursoGlosa> recursosFiltradosSituacao = ManagerBuscaGRG.buscaGuiasRecursoGlosas(autorizacao, dataInicial, dataFinal, prestador, situacao, usuario);
		
		validateGuiasSituacao(recursosFiltradosSituacao, situacao);
		
		ResumoDeferimentoIndeferimentoDeRecurso resumo = new ResumoDeferimentoIndeferimentoDeRecurso(recursosFiltradosSituacao);

		return resumo;
	}

	

	/**
	 * Método responsável pela seleção de um recurso de glosa.
	 * @author Luciano Rocha
	 * @param recursoSelecionado
	 * @param resumo
	 * @return
	 * @throws ValidateException
	 */
	public GuiaRecursoGlosa selecionarRecurso(GuiaRecursoGlosa recursoSelecionado, ResumoDeferimentoIndeferimentoDeRecurso resumo) throws ValidateException{
		
		validateSelecao(recursoSelecionado);
		validateRecursoSelecionado(recursoSelecionado);
		tocarObjetos(recursoSelecionado);
		
		return recursoSelecionado;
	}
	
	/**
	 * Método responsável por deferir ou indeferir os itens de recurso do recurso de glosa selecionado.
	 * @author Luciano Rocha
	 * @param recurso
	 * @param usuario
	 * @return
	 * @throws ValidateException
	 */
	public GuiaRecursoGlosa deferirIndeferirRecursos(GuiaRecursoGlosa recurso, UsuarioInterface usuario) throws ValidateException{
		Set<ItemRecursoGlosa> itensRecurso = recurso.getItensRecurso();
		validateItensRecurso(itensRecurso);
		
		for (ItemRecursoGlosa iRG : itensRecurso) {
			if (iRG.getDeferir() != null) {
				if (iRG.getDeferir()) {
					Assert.isNotNull(iRG.getJustificativaDeferimentoIndeferimento(), "A justificativa para deferir um recurso é obrigatória.");
					iRG.mudarSituacao(usuario, SituacaoEnum.DEFERIDO.descricao(), "Resurso de Glosa deferido pelo auditor do Saúde Recife.", new Date());
					iRG.setDeferir(null);
				} 
				else if (!iRG.getDeferir()){
					Assert.isNotNull(iRG.getJustificativaDeferimentoIndeferimento(), "A justificativa para indeferir um recurso é obrigatória.");
					iRG.mudarSituacao(usuario, SituacaoEnum.INDEFERIDO.descricao(), "Resurso de Glosa indeferido pelo auditor do Saúde Recife.", new Date());
					iRG.setDeferir(null);
				}
			}
		}
		
		return recurso;
	}
	
	/**
	 * Método responsável por salvar o recurso com os itens de recursos deferidos ou indeferidos. 
	 * Também recalcula o valor da guia referente a este recurso.
	 * @author Luciano Rocha
	 * @param recurso
	 * @return
	 * @throws Exception
	 */
	public GuiaRecursoGlosa salvarRecurso(GuiaRecursoGlosa recurso) throws Exception{
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		alteraSituacaoDaGRG(recurso);
		ImplDAO.save(recurso);
		recurso.getGuiaOrigem().recalcularValores();
		return recurso;
	}
	
	private void alteraSituacaoDaGRG(GuiaRecursoGlosa recurso) {
		int count = 0;
		for (ItemRecursoGlosa item : recurso.getItensRecurso()) {
			if (item.isSituacaoAtual(SituacaoEnum.DEFERIDO.descricao())) {
				recurso.mudarSituacao(usuarioDoFluxo, SituacaoEnum.DEFERIDO.descricao(), "Item deferido contido na Guia.", new Date());
			} else if (item.isSituacaoAtual(SituacaoEnum.INDEFERIDO.descricao())) {
				count++;
			}
		}
		if (recurso.getItensRecurso().size() == count) {
			recurso.mudarSituacao(usuarioDoFluxo, SituacaoEnum.INDEFERIDO.descricao(), "Itens da guia indeferidos", new Date());
		}
	}

	//############## VALIDATORS DO FLUXO ####################
	/**
	 * Verifica se para o recurso selecionado os itens de recurso já foram faturados e/ou pagos.
	 * @author Luciano Rocha 
	 * @param recurso
	 * @throws ValidateException
	 */
	public void validateRecursoSelecionado(GuiaRecursoGlosa recurso) throws ValidateException{
		int count = 0;
		for (ItemRecursoGlosa itemRecurso : recurso.getItensRecurso()) {
			boolean isItemFaturadoOuPago = itemRecurso.getSituacao().getDescricao().equals(SituacaoEnum.FATURADA.descricao())
					|| itemRecurso.getSituacao().getDescricao().equals(SituacaoEnum.PAGO.descricao());
			if (isItemFaturadoOuPago) {
				count++;
			}
		}
		
		if (count != 0 && count == recurso.getItensRecurso().size()) {
			throw new ValidateException("Todos os itens de recurso já foram faturados e/ou pagos.");
		}
	}

	/**
	 * Verifica se algum recurso foi selecionado.
	 * @author Luciano Rocha
	 * @param recursoSelecionado
	 * @throws ValidateException
	 */
	private void validateSelecao(GuiaRecursoGlosa recursoSelecionado) throws ValidateException {
		if (recursoSelecionado == null) {
			throw new ValidateException("Selecione algum recurso de glosa.");
		}
	}
	
	/**
	 * Valida se foi deferido ou indeferido algum item de recurso dos que foram filtrados na busca. 
	 * @author Luciano Rocha
	 * @param itensRecurso
	 * @throws ValidateException
	 */
	private void validateItensRecurso(Set<ItemRecursoGlosa> itensRecurso) throws ValidateException {
		int count = 0;
		for (ItemRecursoGlosa iRG : itensRecurso) {
			if (iRG.getDeferir() != null) {
			  count++;
			}
		}
		if (count != itensRecurso.size()) {
			throw new ValidateException("Há recursos não avaliados nessa guia. Marque deferir ou indeferir e tente novamente.");
		}
	}
	
	//#################### TOCAR OBJETOS DO FLUXO ####################
	/**
	 * Método tocarObjetos() específico para o fluxo de deferimento/indeferimento de recurso glosa.
	 * @author Luciano Rocha
	 */
	private void tocarObjetos(GuiaRecursoGlosa recurso){
		Prestador prestador = recurso.getGuiaOrigem().getPrestador();
		if(recurso.getGuiaOrigem().getUltimoLote() != null){
			recurso.getGuiaOrigem().getUltimoLote().getIdentificador(); 
		}
		
		if (prestador != null){
			prestador.tocarObjetos();
		}
		
		if(recurso.getGuiaOrigem() != null && recurso.getGuiaOrigem().getGuiaOrigem() != null){
			recurso.getGuiaOrigem().getGuiaOrigem().tocarObjetos();
		}
		recurso.getGuiaOrigem().getGuiasFilhas().size();
	}
	
	/**
	 * Verifica se os recursos filtrados possuem algum item de recurso na situação passada como parâmetro na busca.
	 * @author Luciano Rocha
	 * @param recursos
	 * @param situacao
	 * @throws ValidateException
	 */
	private void validateGuiasSituacao(List<GuiaRecursoGlosa> recursos, String situacao) throws ValidateException {
		if (recursos == null || recursos.size() < 1) {
			throw new ValidateException("Nenhum recurso foi encontrado na situação " + situacao + " para os parâmetros passados.");
		}
	}
}
