package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.TransactionManagerHibernate;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;
/**
 * 
 * @author Idelvane Santana
 * Utilizado no flow de agrupamento de prestadores nas faixas de guias, para o faturamento.
 * P.S: Consegui em 74 segundos na web...
 */
public class AgruparPrestadoresFaturamento  extends Service{
	
	private List<Prestador> prestadores;
	private int quantidadeG1;
	private int quantidadeG2;
	private int quantidadeG3;
	private int quantidadeG4;
	private int quantidadeG5;
	
	public AgruparPrestadoresFaturamento(){}
	
	public AgruparPrestadoresFaturamento(Set<Prestador> prestadoresValidos, int quantidadeG1, int quantidadeG2, int quantidadeG3, int quantidadeG4, int quantidadeG5){
		List<Prestador> prestadoresPassados  = new ArrayList<Prestador>(prestadoresValidos);
		this.prestadores = prestadoresPassados;
		this.quantidadeG1 = quantidadeG1;
		this.quantidadeG2 = quantidadeG2;
		this.quantidadeG3 = quantidadeG3;
		this.quantidadeG4 = quantidadeG4;
		this.quantidadeG5 = quantidadeG5;
	}
	@SuppressWarnings("unchecked")
	public AgruparPrestadoresFaturamento agruparPrestadores(String competencia) throws Exception{
		long inicio = System.nanoTime();
		Assert.isNotEmpty(competencia, "A competência deve ser informada.");
		Date competenciaInformada = super.getCompetencia(competencia);
		procuraFaturamento(competenciaInformada);
		
		List<FaixaGuiaFaturamento> faixas = ImplDAO.findByParam((Iterator)null, FaixaGuiaFaturamento.class);
		Set<Prestador> prestadoresValidos = new HashSet<Prestador>();
		
		try {
			Connection conn = HibernateUtil.currentSession().connection();
			PreparedStatement pstPrestadores;
			String selectPrestadores = "select idPrestador from IapepSaude.ASSOCIADOS_Prestador where " +
					"geraFaturamento = 1 and idPrestador in (select idPrestador from IapepSaude.ASSOCIADOS_Faturamento" +
					" where competencia = ? and status = 'A')";
			pstPrestadores = conn.prepareStatement(selectPrestadores);
			pstPrestadores.setDate(1,Utils.convert(competenciaInformada));
			ResultSet resPrestadores = pstPrestadores.executeQuery();
			int i = 0;
			Assert.isNotNull(resPrestadores,"Não foram encontrados prestadores que geraram faturamento nesta competência.");
			PreparedStatement pstGuias = conn.prepareStatement(" select count(*) from IapepSaude.ATENDIMENTO_Guia " +
			"where descricao = 'Confirmado(a)' and idFaturamento is not null and idPrestador = ? ");
			
			TransactionManagerHibernate tm  = new TransactionManagerHibernate();
			tm.openSession();
			tm.beginTransaction();
			
			while (resPrestadores.next()) {
				Long idPrestador =  resPrestadores.getLong(1);
				Prestador prestador = (Prestador) ImplDAO.findById(idPrestador,Prestador.class);
				Assert.isNotNull(prestador,"Prestador inválido");
				Integer countGuias =0;
				
				pstGuias.setLong(1, idPrestador);
				ResultSet resultCount = pstGuias.executeQuery();
				if(resultCount.next() && resultCount != null){
					 countGuias = resultCount.getInt(1);
				}
				try {
					
					for (FaixaGuiaFaturamento faixa: faixas) {
						if(countGuias.compareTo(faixa.getValorFaixaDe())>=0 && countGuias.compareTo(faixa.getValorFaixaAte()) <=0){
							prestadoresValidos.add(prestador);	
							prestador.setFaixa(faixa.getIdFaixaGuia());
						}
					}
					i++;
					
				} catch (Exception e) {
					throw new ValidateException("Não foi possível salvar a faixa do prestador.");
				}
				
			}	
				tm.commit();
			
			for (Iterator<Prestador> iter = prestadoresValidos.iterator(); iter.hasNext();) {
				Prestador prestador = iter.next();

				if(prestador.getFaixa().equals(Constantes.FAIXA_1)){
					this.quantidadeG1 += 1; 
				}else
					if(prestador.getFaixa().equals(Constantes.FAIXA_2)){
							this.quantidadeG2 += 1;
						}
					else 
						if(prestador.getFaixa().equals(Constantes.FAIXA_3)){
								this.quantidadeG3 += 1;
						} else
							if(prestador.getFaixa().equals(Constantes.FAIXA_4)){
								this.quantidadeG4 += 1;
							}else this.quantidadeG5 +=1;
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		AgruparPrestadoresFaturamento agrupamento = new AgruparPrestadoresFaturamento(prestadoresValidos, quantidadeG1, quantidadeG2, quantidadeG3, quantidadeG4, quantidadeG5);
		long fim = System.nanoTime();
		
		return agrupamento;
	}

	@SuppressWarnings("unchecked")
	private void procuraFaturamento(Date competenciaInformada) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("competencia", competenciaInformada));
		sa.addParameter(new Equals("status", "A"));
		List<Faturamento> faturamentos = sa.list(Faturamento.class);
		Assert.isNotEmpty(faturamentos, "Não foi gerado faturamento para está competência.");
		sa.clearAllParameters();
		faturamentos = null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Prestador> getPrestadores() {
		Collections.sort(this.prestadores, new Comparator(){
			public int compare(Object o1, Object o2){
				Prestador prestador1 = (Prestador) o1;
				Prestador prestador2 = (Prestador) o2;
				return prestador1.getDescricaoFaixa().compareTo(prestador2.getDescricaoFaixa());
			}
		}) ;
		return prestadores;
	}
	
	public int getQuantidadeG1() {
		return quantidadeG1;
	}

	public int getQuantidadeG2() {
		return quantidadeG2;
	}

	public int getQuantidadeG3() {
		return quantidadeG3;
	}

	public int getQuantidadeG4() {
		return quantidadeG4;
	}

	public void finalizar(AgruparPrestadoresFaturamento agrupamento){}

	public int getQuantidadeG5() {
		return quantidadeG5;
	}

	
}
