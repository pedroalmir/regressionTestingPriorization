package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings("unchecked")
public class ResumoGuiasComAcompanhamentoAnestesico {
	
	private GuiaCompleta guiaOrigem;
	private GuiaAcompanhamentoAnestesico guiaAcompanhamentoAnestesico;
	
	public GuiaCompleta getGuiaOrigem() {
		return guiaOrigem;
	}
	
	public void setGuiaOrigem(GuiaCompleta guiaOrigem) {
		this.guiaOrigem = guiaOrigem;
	}
	
	public GuiaAcompanhamentoAnestesico getGuiaAcompanhamentoAnestesico() {
		return guiaAcompanhamentoAnestesico;
	}
	
	public void setGuiaAcompanhamentoAnestesico(GuiaAcompanhamentoAnestesico guiaAcompanhamentoAnestesico) {
		this.guiaAcompanhamentoAnestesico = guiaAcompanhamentoAnestesico;
	}
	
	public List<Prestador> getPrestadoresQueRealizamProcedimentos() throws Exception {
		List<Prestador> prestadores = new ArrayList<Prestador>();
		prestadores.addAll(prestadoresAptosASerExibidos());
		Utils.sort(prestadores, "valorTotalProcedimentos", "pessoaJuridica.fantasia");
		return prestadores;
	}

	private List<Prestador> buscarTodosPrestadoresAtivos() {
		return HibernateUtil.currentSession().createCriteria(Prestador.class)
			.add(Expression.eq("situacao.descricao", SituacaoEnum.ATIVO.descricao()))
				.list();
	}
	
	private List<Prestador> prestadoresAptosASerExibidos() throws Exception {
		List<Prestador> prestadores = buscarTodosPrestadoresAtivos();
		Iterator<Prestador> iterator = prestadores.iterator();
		boolean realizaTodosOsProcedimentos, verificaProcedimentosAssociados;
		
		while (iterator.hasNext()) {
			Prestador prestador = iterator.next();
			
			verificaProcedimentosAssociados = prestador.isVerificaProcedimentosAssociados();
			realizaTodosOsProcedimentos = prestador.getProcedimentos().containsAll(this.guiaOrigem.getProcedimentosDaTabelaCBHPM());
			
			if (verificaProcedimentosAssociados && realizaTodosOsProcedimentos) {
				setValorTotalProcedimentos(this.guiaOrigem.getProcedimentos(), prestador);
			} else {
				iterator.remove();
			}
		}
		
		return prestadores;
	}

	private void setValorTotalProcedimentos(Set<ProcedimentoInterface> procedimentos,
			Prestador prestador) {
		BigDecimal valorParaOsProcedimentos = prestador.getValorTotalProcedimentos(procedimentos);
		prestador.setValorTotalProcedimentos(valorParaOsProcedimentos);
	}
	
}

