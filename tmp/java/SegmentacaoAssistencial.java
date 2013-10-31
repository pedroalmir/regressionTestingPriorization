package br.com.infowaypi.ecarebc.produto;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;

/**
 * @author DANNYLVAN
 * 
 */
public class SegmentacaoAssistencial implements Serializable{

	private static final long serialVersionUID = 5728265818224127467L;

	private Long idSegmentacaoAssistencial;
	
	private String descricao;
	
	private TipoSegmentacao tipoSegmentacaoEnum;
	
	private CaraterEnum caraterEnum;
	
	public Long getIdSegmentacaoAssistencial() {
		return idSegmentacaoAssistencial;
	}

	public void setIdSegmentacaoAssistencial(Long idSegmentacaoAssistencial) {
		this.idSegmentacaoAssistencial = idSegmentacaoAssistencial;
	}

	public CaraterEnum getCaraterEnum() {
		return caraterEnum;
	}

	public void setCaraterEnum(CaraterEnum caraterEnum) {
		this.caraterEnum = caraterEnum;
	}
	
	public String getCarater() {
		return caraterEnum.getDescricao();
	}
	
	public void setCarater(String caraterDescricao) {
		this.caraterEnum = CaraterEnum.getTipoSegmentacaoByDescricao(caraterDescricao);
	}

	public TipoSegmentacao getTipoSegmentacaoEnum() {
		return tipoSegmentacaoEnum;
	}

	public void setTipoSegmentacaoEnum(TipoSegmentacao tipoSegmentacaoEnum) {
		this.tipoSegmentacaoEnum = tipoSegmentacaoEnum;
	}
	
	public String getTipoSegmentacao() {
		return tipoSegmentacaoEnum.getDescricao();
	}
	
	public void setTipoSegmentacao(String tipoSegmentacao) {
		this.tipoSegmentacaoEnum = TipoSegmentacao.getTipoSegmentacaoByDescricao(tipoSegmentacao);
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SegmentacaoAssistencial)) {
			return false;
		}
		SegmentacaoAssistencial segmentacao = (SegmentacaoAssistencial) object;
		return new EqualsBuilder()
				.append(this.getCarater(), segmentacao.getCarater())
				.append(this.getTipoSegmentacao(), segmentacao.getTipoSegmentacao()).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(1308791639, 309108973)
		.append(this.getCarater())
		.append(this.getTipoSegmentacao())
		.toHashCode();
	}
	
	public static void gerarSegmentacoes() throws Exception {
		
		if(new SearchAgent().list(SegmentacaoAssistencial.class).isEmpty()){
			SegmentacaoAssistencial sa1 = new SegmentacaoAssistencial();
			sa1.setDescricao("Ambulatorial Urgencia");
			sa1.setCaraterEnum(CaraterEnum.URGENCIA);
			sa1.setTipoSegmentacaoEnum(TipoSegmentacao.AMBULATORIAL);
			
			SegmentacaoAssistencial sa2 = new SegmentacaoAssistencial();
			sa2.setDescricao("Ambulatorial  Eletivo");
			sa2.setCaraterEnum(CaraterEnum.ELETIVO);
			sa2.setTipoSegmentacaoEnum(TipoSegmentacao.AMBULATORIAL);
			
			SegmentacaoAssistencial sa3 = new SegmentacaoAssistencial();
			sa3.setDescricao("Hospitalar Urgencia");
			sa3.setCaraterEnum(CaraterEnum.URGENCIA);
			sa3.setTipoSegmentacaoEnum(TipoSegmentacao.HOSPITALAR);
			
			SegmentacaoAssistencial sa4 = new SegmentacaoAssistencial();
			sa4.setDescricao("Hospitalar Eletivo");
			sa4.setCaraterEnum(CaraterEnum.ELETIVO);
			sa4.setTipoSegmentacaoEnum(TipoSegmentacao.HOSPITALAR);
			
			SegmentacaoAssistencial sa5 = new SegmentacaoAssistencial();
			sa5.setDescricao("Obstetrícia Urgencia");
			sa5.setCaraterEnum(CaraterEnum.URGENCIA);
			sa5.setTipoSegmentacaoEnum(TipoSegmentacao.HOSPITALAR_COM_OBSTETRICIA);
			
			SegmentacaoAssistencial sa6 = new SegmentacaoAssistencial();
			sa6.setDescricao("Obstetrícia Eletivo");
			sa6.setCaraterEnum(CaraterEnum.ELETIVO);
			sa6.setTipoSegmentacaoEnum(TipoSegmentacao.HOSPITALAR_COM_OBSTETRICIA);
			
			SegmentacaoAssistencial sa7 = new SegmentacaoAssistencial();
			sa7.setDescricao("Odontologico");
			sa7.setCaraterEnum(CaraterEnum.AMBOS);
			sa7.setTipoSegmentacaoEnum(TipoSegmentacao.ODONTOLOGICO);
			
			HibernateUtil.currentSession().flush();
		
			ImplDAO.save(sa1);
			ImplDAO.save(sa2);
			ImplDAO.save(sa3);
			ImplDAO.save(sa4);
			ImplDAO.save(sa5);
			ImplDAO.save(sa6);
			ImplDAO.save(sa7);
		}
		
		for (SegmentacaoAssistencialEnum segmentacao : SegmentacaoAssistencialEnum.values()) {
			SearchAgent sa = new SearchAgent();
			sa.addParameter(new Equals("descricao", segmentacao.getDescricao()));
			segmentacao.setSegmentacaoAssistencial(sa.uniqueResult(SegmentacaoAssistencial.class));
		}
	}
}

