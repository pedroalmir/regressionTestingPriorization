/**
 * 
 */
package br.com.infowaypi.ecare.financeiro.boletos;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.FlushMode;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.financeiro.ResumoCobrancas;
import br.com.infowaypi.ecare.financeiro.conta.ContaMatricula;
import br.com.infowaypi.ecare.segurados.Matricula;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service responsável pelo cancelamento de cobranças (Boletos).
 * @author <a href="mailto:mquixaba@gmail.com">Marcus BOolean</a>
 * @since 2008-12-11 10:00
 * 
 */
public class CancelarBoletosService {
	
	public ResumoCobrancas buscarBoletos(String cpf, String numeroDoCartao, Date competencia) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("situacao.descricao", SituacaoEnum.ABERTO.descricao()));
		
		if(!Utils.isStringVazia(cpf))
			sa.addParameter(new Equals("titular.pessoaFisica.cpf", cpf));
		
		if(!Utils.isStringVazia(numeroDoCartao))
			sa.addParameter(new Equals("titular.numeroDoCartao", numeroDoCartao));
		
		if(competencia != null)
			sa.addParameter(new Equals("competencia", competencia));
		
		List<Cobranca> cobrancas = sa.list(Cobranca.class);
		
		Assert.isNotEmpty(cobrancas, "Caro usuário, não foi possivel encontrar boletos aptos ao cancelamento a partir dos parâmetros informados.");
		
		ResumoCobrancas resumo = new ResumoCobrancas();
		resumo.setCobrancas(cobrancas);
		resumo.setCompetencia(competencia);
		tocarObjetos(resumo);
		
		return resumo;
	}
	
	public ResumoCobrancas informarValores(String motivoCancelamento,Collection<Cobranca> cobrancas, ResumoCobrancas resumo) throws Exception {
		
		Assert.isNotNull(motivoCancelamento, "Caro usuário, a informação do campo \"Motivo de Cancelamento\" é obrigatório.");
		Assert.isNotEmpty(cobrancas, "Caro usário, é necessário selecionar pelo menos um boleto");
		
		resumo.setMotivo(motivoCancelamento);
		resumo.getCobrancas().clear();
		resumo.getCobrancas().addAll(cobrancas);
		
		return resumo;
	}
	
	public ResumoCobrancas conferirDados(ResumoCobrancas resumo, UsuarioInterface usuario) throws Exception {
			
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		
		Set<TitularFinanceiroSR> titulares = new HashSet<TitularFinanceiroSR>();
		for (Cobranca cobranca : resumo.getCobrancas()) {
			cobranca.mudarSituacao(usuario, SituacaoEnum.CANCELADO.descricao(), resumo.getMotivo(), new Date());
			for (GuiaSimples guia : cobranca.getGuias()) {
				guia.setFluxoFinanceiro(null);
				ImplDAO.save(guia);
			}
			
			titulares.add(cobranca.getTitular());
			cobranca.setGuias(null);
			ImplDAO.save(cobranca);
		}
		
		List<Matricula> matriculas = HibernateUtil.currentSession().createCriteria(Matricula.class)
																	.add(Expression.in("segurado", titulares))
																	.add(Expression.eq("tipoPagamento", Constantes.BOLETO))
																	.list();
	
		for (Matricula matricula : matriculas) {
			for (ContaMatricula contaMatricula : matricula.getContasMatriculas(resumo.getCompetencia())) {
				if(!contaMatricula.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())){
					contaMatricula.mudarSituacao(usuario, SituacaoEnum.CANCELADO.descricao(), "Cancelamento de boletos", new Date());
					ImplDAO.save(contaMatricula);
				}
			}
		}
		
		return resumo;
	}
	
	public void finalizar(ResumoCobrancas resumo) {}
	
	private void tocarObjetos(ResumoCobrancas resumo){
		for (Cobranca co : resumo.getCobrancas()) {
			for (SituacaoInterface sit : co.getSituacoes()) {
				sit.getDescricao();
			}
			co.getGuias().size();
			co.getTitular().getConsultasPromocionais().size();
		}
	}
}
