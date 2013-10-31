package br.com.infowaypi.ecare.financeiro.faturamento;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AlteracaoFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.FaturamentoPassivo;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service do mapeamento <code>InsercaoFaturamentoPrestador.jhm.xml</code> responsável por <br>
 * incluir no faturamento mensal do Saúde Recife um prestador que não tenha tido guias faturadas. <br>
 * Os valores são inseridos manualmente por quem executa o fluxo.
 * @author Diogo Vinícius
 *
 */
public class InsercaoFaturamentoPrestador {

	public static String competencia = "";
	public static Prestador prestador;
	public static int tipo;
	public static final int FATURAMENTO_NORMAL = 1;
	public static final int FATURAMENTO_PASSIVO = 2;
	
	/**
	 * Método do 1º step-method
	 * @param comp
	 * @param prest
	 * @throws ValidateException
	 */
	public AbstractFaturamento informarDados(String comp, Prestador prest, Integer tipoFaturamento) throws ValidateException {
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("competencia", Utils.gerarCompetencia(comp)));
		List<AbstractFaturamento> faturamentos = sa.list(getKlass(tipoFaturamento));
		
		if (faturamentos.isEmpty()) {
			throw new  ValidateException("O faturamento desta competência ainda não foi processado");
		}
		
		sa.addParameter(new Equals("status", Constantes.FATURAMENTO_ABERTO));
		faturamentos = sa.list(Faturamento.class);
		if (faturamentos.isEmpty()) {
			throw new  ValidateException("O faturamento desta competência não está na situação Aberto");
		}
		
		AbstractFaturamento faturamento = null;
		if(tipoFaturamento == FATURAMENTO_NORMAL)
			faturamento = prest.getFaturamento(Utils.gerarCompetencia(comp));
		if(tipoFaturamento == FATURAMENTO_PASSIVO)
			faturamento = prest.getFaturamentoPassivo(Utils.gerarCompetencia(comp));
		
		boolean isPrestadorTemFaturamento = faturamento == null ? false : true;
		if (isPrestadorTemFaturamento) {
			if (faturamento.getStatus() == Constantes.FATURAMENTO_FECHADO) {
				throw new ValidateException("O faturamento do Prestador desta competência não está na situação Aberto");
			}
		}
		
		competencia = comp;
		prestador = prest;
		tipo = tipoFaturamento;
		
		return faturamento;
	}

	/**
	 * Método do 2º step-method
	 * @param valorFaturamento
	 * @param motivo
	 * @param usuario
	 * @return o faturamento do prestador a ser salvo
	 * @throws ValidateException
	 */
	public AbstractFaturamento criarFaturamentoComAlteracao(AbstractFaturamento faturamento, BigDecimal valorFaturamentoAcres, BigDecimal valorFaturamentoDecres, String motivo, UsuarioInterface usuario) throws ValidateException {
		AlteracaoFaturamento alteracao = new AlteracaoFaturamento();
		if(valorFaturamentoAcres == null) {
			valorFaturamentoAcres = BigDecimal.ZERO;
		}
		
		if(valorFaturamentoDecres == null) {
			valorFaturamentoDecres = BigDecimal.ZERO;
		}
		
		if(valorFaturamentoAcres.compareTo(BigDecimal.ZERO) == 0 && valorFaturamentoDecres.compareTo(BigDecimal.ZERO) == 0) {
			throw new RuntimeException("Caro usuário informe um valor.");
		}
		
		alteracao.setValorIncremento(valorFaturamentoAcres);
		alteracao.setValorDecremento(valorFaturamentoDecres);
		alteracao.setStatus(AlteracaoFaturamento.STATUS_ATIVO);
		alteracao.setData(new Date());
		alteracao.setMotivo(motivo);
		alteracao.setUsuario(usuario);
		
		if (faturamento == null) {
			if(tipo == FATURAMENTO_NORMAL)
				faturamento = new Faturamento();
			else
				faturamento = new FaturamentoPassivo();
			faturamento.setValorBruto(BigDecimal.ZERO);
			faturamento.setValorLiquido(BigDecimal.ZERO);
			faturamento.setPrestador(prestador);
			faturamento.setNome(prestador.getPessoaJuridica().getFantasia());
			faturamento.setInformacaoFinanceira(prestador.getInformacaoFinanceira());
			faturamento.setCategoria(prestador.getTipoPrestador());
			faturamento.setGeradoPosteriormente(true);
			faturamento.setMotivoGeracaoPosterior(motivo);
			faturamento.setDataGeracao(new Date());
			faturamento.setCompetencia(Utils.gerarCompetencia(competencia));
			alteracao.setFaturamento(faturamento);
			faturamento.getAlteracoesFaturamento().add(alteracao);
		}else {
			alteracao.setFaturamento(faturamento);
			faturamento.getAlteracoesFaturamento().add(alteracao);
		}
		
		faturamento.setValorBruto(faturamento.getValorBruto().add(alteracao.getSaldo()));
		
		if(faturamento.getValorBruto().compareTo(BigDecimal.ZERO) < 0) {
			throw new RuntimeException("Caro usuario, o saldo das alterações não pode deixar o valor do faturamento zerado.");
		}
		
		faturamento.getGuiasFaturaveis().size();
		
		return faturamento;
	}

	/**
	 * Método do 3º step-method
	 * @param faturamento
	 * @throws Exception
	 */
	public void conferirDados(AbstractFaturamento faturamento) throws Exception {
		ImplDAO.save(faturamento);
	}
	
	public Class getKlass(Integer tipo) {
		switch (tipo) {
		case FATURAMENTO_NORMAL:
			return Faturamento.class;
		default: 
			return FaturamentoPassivo.class;
		}
	}
	
}
