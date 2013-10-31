/**
 * 
 */
package br.com.infowaypi.ecare.resumos;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.HashedMap;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus Vinicius
 *
 */
public class ResumoUtilizacaoUrgencia {
	
	private static final String[] TIPOS_DE_GUIA = {"CUR","AUR","IUR"};
	private static final String[] SITUACAO_DESCRICOES = {"Pago(a)","Faturado(a)","Auditado(a)","Recebido(a)","Fechado(a)"};
	private Date dataInicial;
	private Date dataFinal;
	private Prestador prestador;
	private Integer quantidadeGuias = 0;
	private Map<String, List<Agrupamento>> agrupamentosPorBeneficiario = new HashMap<String, List<Agrupamento>>();
	private Set<Resultado> resultados = new HashSet<Resultado>();

	public ResumoUtilizacaoUrgencia(Date dataInicial, Date dataFinal,Prestador prestador, Integer quantidadeGuias) {
		this.dataFinal = dataFinal;
		this.dataInicial = dataInicial;
		
		boolean isPrestadorNull = true;
		if(prestador != null)
			isPrestadorNull = false;
		
		if(quantidadeGuias != null) {
			this.quantidadeGuias = quantidadeGuias;
		}
		
		
		this.prestador = prestador;
		this.quantidadeGuias = quantidadeGuias;
		
		computaResumo(isPrestadorNull);
	}

	private void computaResumo(boolean isPrestadorNull) {
		
		
		String hqlPrestadorNulo = "select segurado.numeroDoCartao, segurado.pessoaFisica.nome,guia.tipoDeGuia, count(*) from " +
				"Segurado as segurado, GuiaSimples as guia where segurado.idSegurado = guia.segurado.idSegurado " +
				"and guia.situacao.descricao in(:situacoes) and guia.dataTerminoAtendimento between :dataInicial and :dataFinal " +
				"and guia.tipoDeGuia in(:tipoDeGuia)" +
				"group by segurado.numeroDoCartao, segurado.pessoaFisica.nome,guia.tipoDeGuia " +
				"order by count(*) desc ";
		
		String hqlPrestadorNaoNulo = "select segurado.numeroDoCartao, segurado.pessoaFisica.nome,guia.tipoDeGuia, count(*) from " +
				"Segurado as segurado, GuiaSimples as guia where segurado.idSegurado = guia.segurado.idSegurado and guia.prestador = :prestador and " +
				"guia.situacao.descricao in(:situacoes) and guia.dataTerminoAtendimento between :dataInicial and :dataFinal " +
				"and guia.tipoDeGuia in(:tipoDeGuia)" +
				"group by segurado.numeroDoCartao, segurado.pessoaFisica.nome,guia.tipoDeGuia " +
				"order by count(*) desc ";
		
		List<Object[]> resultSet = new ArrayList<Object[]>();
		if(isPrestadorNull) {
			resultSet = HibernateUtil.currentSession().createQuery(hqlPrestadorNulo)
				.setParameterList("situacoes",SITUACAO_DESCRICOES)
				.setDate("dataInicial", this.dataInicial)
				.setDate("dataFinal", this.dataFinal)
				.setParameterList("tipoDeGuia", TIPOS_DE_GUIA)
			.list();
		}else {
		    resultSet = HibernateUtil.currentSession().createQuery(hqlPrestadorNaoNulo)
				.setParameterList("situacoes",SITUACAO_DESCRICOES)
				.setParameter("prestador", prestador)
				.setDate("dataInicial", this.dataInicial)
				.setDate("dataFinal", this.dataFinal)
				.setParameterList("tipoDeGuia", TIPOS_DE_GUIA)
			.list();
		}
			
		
		Agrupamento agrupamento;
		for (Object[] objects : resultSet) {
			agrupamento = new Agrupamento();
			agrupamento.setNumeroCartao((String)objects[0]);
			agrupamento.setNome((String)objects[1]);
			agrupamento.setTipoDeGuia((String) objects[2]);
			agrupamento.setQuantidadeGuias(Integer.valueOf(String.valueOf(objects[3])));
			
			if(!this.agrupamentosPorBeneficiario.keySet().contains(agrupamento.getNumeroCartao())) {
				List<Agrupamento> agrupamentos = new ArrayList<Agrupamento>();
				this.agrupamentosPorBeneficiario.put(agrupamento.getNumeroCartao(), agrupamentos);	
			}
			
			this.agrupamentosPorBeneficiario.get(agrupamento.getNumeroCartao()).add(agrupamento);
			
		}
		
		Resultado resultado;
		
		for (String key : agrupamentosPorBeneficiario.keySet()) {
			resultado = new Resultado();
			resultado.setNumeroCartao(key);
			int quantCUR = 0;
			int quantAUR = 0;
			int quantIUR = 0;
			for (Agrupamento agrup : agrupamentosPorBeneficiario.get(key)) {
				resultado.setNome(agrup.getNome());
				if(agrup.isCUR()) {
					quantCUR = agrup.getQuantidadeGuias();
				}
				if(agrup.isAUR()) {
					quantAUR = agrup.getQuantidadeGuias();
				}
				if(agrup.isIUR()) {
					quantIUR = agrup.getQuantidadeGuias();
				}
			}
			
			resultado.setQuantAUR(quantAUR);
			resultado.setQuantIUR(quantIUR);
			resultado.setQuantCUR(quantCUR);
			
			resultado.setQuantidadeTotalGuias(quantAUR+quantCUR+quantIUR);
			
			if(resultado.getQuantidadeTotalGuias() >= this.quantidadeGuias) {
				this.resultados.add(resultado);
			}	
		}
		
	}
	
	public class Agrupamento {
		String numeroCartao;
		String nome;
		String tipoDeGuia;
		int quantidadeGuias;
		
		public String getNumeroCartao() {
			return numeroCartao;
		}
		public void setNumeroCartao(String numeroCartao) {
			this.numeroCartao = numeroCartao;
		}
		public String getNome() {
			return nome;
		}
		public void setNome(String nome) {
			this.nome = nome;
		}
		public String getTipoDeGuia() {
			return tipoDeGuia;
		}
		public void setTipoDeGuia(String tipoDeGuia) {
			this.tipoDeGuia = tipoDeGuia;
		}
		public int getQuantidadeGuias() {
			return quantidadeGuias;
		}
		public void setQuantidadeGuias(int quantidadeGuias) {
			this.quantidadeGuias = quantidadeGuias;
		}
		public boolean isCUR() {
			if(tipoDeGuia.equals("CUR")) {
				return true;
			}
			return false;
		}
		public boolean isIUR() {
			if(tipoDeGuia.equals("IUR")) {
				return true;
			}
			return false;
		}
		public boolean isAUR() {
			if(tipoDeGuia.equals("AUR")) {
				return true;
			}
			return false;
		}	
	
	}
		
	
	public class Resultado {
		String numeroCartao;
		String nome;
		int quantCUR;
		int quantIUR;
		int quantAUR;
		int quantidadeTotalGuias;
		
		public String getNumeroCartao() {
			return numeroCartao;
		}
		public void setNumeroCartao(String numeroCartao) {
			this.numeroCartao = numeroCartao;
		}
		public String getNome() {
			return nome;
		}
		public void setNome(String nome) {
			this.nome = nome;
		}
		public int getQuantCUR() {
			return quantCUR;
		}
		public void setQuantCUR(int quantCUR) {
			this.quantCUR = quantCUR;
		}
		public int getQuantIUR() {
			return quantIUR;
		}
		public void setQuantIUR(int quantIUR) {
			this.quantIUR = quantIUR;
		}
		public int getQuantAUR() {
			return quantAUR;
		}
		public void setQuantAUR(int quantAUR) {
			this.quantAUR = quantAUR;
		}
		public int getQuantidadeTotalGuias() {
			return quantidadeTotalGuias;
		}
		public void setQuantidadeTotalGuias(int quantidadeTotalGuias) {
			this.quantidadeTotalGuias = quantidadeTotalGuias;
		}
		
	}
	
	public static void main(String[] args) {
		String hql1 = "select segurado.numeroDoCartao, segurado.pessoaFisica.nome,guia.tipoDeGuia ,count(*) from " +
		"Segurado as segurado, GuiaSimples as guia where segurado.idSegurado = guia.segurado.idSegurado and " +
		"guia.situacao.descricao in(:situacoes) and guia.dataTerminoAtendimento between :dataInicial and :dataFinal " +
		"and guia.tipoDeGuia in(:tipoDeGuia)" +
		"group by segurado.numeroDoCartao, segurado.pessoaFisica.nome,guia.tipoDeGuia " +
		"having count(*) >= :quantidade " +
		"order by segurado.numeroDoCartao,count(*) desc ";
		
		List<Object[]> resultado = new ArrayList<Object[]>();
			resultado = HibernateUtil.currentSession().createQuery(hql1)
				.setParameterList("situacoes",SITUACAO_DESCRICOES)
				.setDate("dataInicial", Utils.parse("01/07/2009"))
				.setDate("dataFinal", Utils.parse("31/12/2009"))
				.setParameterList("tipoDeGuia", TIPOS_DE_GUIA)
				.setInteger("quantidade", 0)
		.list();
			
			for (Object[] objects : resultado) {
				System.out.print((String)objects[0] + ";");
				System.out.print((String)objects[1] + ";");
				System.out.print((String) objects[2] + ";");
				System.out.println(Integer.valueOf(String.valueOf(objects[3])));
			}
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Prestador getPrestador() {
		return prestador;
	}

	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}

	public Integer getQuantidadeGuias() {
		return quantidadeGuias;
	}

	public void setQuantidadeGuias(Integer quantidadeGuias) {
		this.quantidadeGuias = quantidadeGuias;
	}

	public Map<String, List<Agrupamento>> getAgrupamentosPorBeneficiario() {
		return agrupamentosPorBeneficiario;
	}

	public void setAgrupamentosPorBeneficiario(
			Map<String, List<Agrupamento>> agrupamentosPorBeneficiario) {
		this.agrupamentosPorBeneficiario = agrupamentosPorBeneficiario;
	}

	public Set<Resultado> getResultados() {
		return this.resultados;
	}

	public void setResultados(Set<Resultado> resultados) {
		this.resultados = resultados;
	}
}
