package br.com.infowaypi.ecarebc.odonto;

import br.com.infowaypi.ecarebc.odonto.enums.DenteEnum;
import br.com.infowaypi.ecarebc.odonto.enums.DenticaoEnum;
import br.com.infowaypi.ecarebc.odonto.enums.EstruturaOdontoEnum;
import br.com.infowaypi.ecarebc.odonto.enums.LadoEnum;
import br.com.infowaypi.ecarebc.odonto.enums.PosicaoEnum;
import br.com.infowaypi.ecarebc.odonto.enums.QuadranteEnum;

/**
 * Interface com constantes comuns a estruturas odontológicas
 * @author Danilo Nogueira Portela
 */
public interface EstruturaOdontoInterface{
	 
	public static final Integer DENTICAO = EstruturaOdontoEnum.DENTICAO.getValor();
	public static final Integer ARCADA = EstruturaOdontoEnum.ARCADA.getValor();
	public static final Integer QUADRANTE = EstruturaOdontoEnum.QUADRANTE.getValor();
	public static final Integer DENTE = EstruturaOdontoEnum.DENTE.getValor();
	public static final Integer FACE = EstruturaOdontoEnum.FACE.getValor();
	
	public static final String TIPO_DECIDUO = DenticaoEnum.DECIDUO.getDescricao();
	public static final String TIPO_PERMANENTE = DenticaoEnum.PERMANENTE.getDescricao();
	
	public static final String POSICAO_SUPERIOR = PosicaoEnum.SUPERIOR.getDescricao();
	public static final String POSICAO_INFERIOR = PosicaoEnum.INFERIOR.getDescricao();
	
	public static final String LADO_DIREITO = LadoEnum.DIREITO.getDescricao();
	public static final String LADO_ESQUERDO = LadoEnum.ESQUERDO.getDescricao();
	
	
	public static final Integer NENHUM = DenteEnum.NENHUM.getNumero();
	public static final Integer DENTE_11 = DenteEnum.INCISIVO_CENTRAL_SUPERIOR_DIREITO_PERMANENTE.getNumero();
	public static final Integer DENTE_12 = DenteEnum.INCISIVO_LATERAL_SUPERIOR_DIREITO_PERMANENTE.getNumero();
	public static final Integer DENTE_13 = DenteEnum.CANINO_SUPERIOR_DIREITO_PERMANENTE.getNumero();
	public static final Integer DENTE_14 = DenteEnum.PRE_MOLAR1_SUPERIOR_DIREITO_PERMANENTE.getNumero();
	public static final Integer DENTE_15 = DenteEnum.PRE_MOLAR2_SUPERIOR_DIREITO_PERMANENTE.getNumero();
	public static final Integer DENTE_16 = DenteEnum.MOLAR1_SUPERIOR_DIREITO_PERMANENTE.getNumero();
	public static final Integer DENTE_17 = DenteEnum.MOLAR2_SUPERIOR_DIREITO_PERMANENTE.getNumero();
	public static final Integer DENTE_18 = DenteEnum.MOLAR3_SUPERIOR_DIREITO_PERMANENTE.getNumero();
	
	public static final Integer DENTE_21 = DenteEnum.INCISIVO_CENTRAL_SUPERIOR_ESQUERDO_PERMANENTE.getNumero();
	public static final Integer DENTE_22 = DenteEnum.INCISIVO_LATERAL_SUPERIOR_ESQUERDO_PERMANENTE.getNumero();
	public static final Integer DENTE_23 = DenteEnum.CANINO_SUPERIOR_ESQUERDO_PERMANENTE.getNumero();
	public static final Integer DENTE_24 = DenteEnum.PRE_MOLAR1_SUPERIOR_ESQUERDO_PERMANENTE.getNumero();
	public static final Integer DENTE_25 = DenteEnum.PRE_MOLAR2_SUPERIOR_ESQUERDO_PERMANENTE.getNumero();
	public static final Integer DENTE_26 = DenteEnum.MOLAR1_SUPERIOR_ESQUERDO_PERMANENTE.getNumero();
	public static final Integer DENTE_27 = DenteEnum.MOLAR2_SUPERIOR_ESQUERDO_PERMANENTE.getNumero();
	public static final Integer DENTE_28 = DenteEnum.MOLAR3_SUPERIOR_ESQUERDO_PERMANENTE.getNumero();
	
	public static final Integer DENTE_31 = DenteEnum.INCISIVO_CENTRAL_INFERIOR_ESQUERDO_PERMANENTE.getNumero();
	public static final Integer DENTE_32 = DenteEnum.INCISIVO_LATERAL_INFERIOR_ESQUERDO_PERMANENTE.getNumero();
	public static final Integer DENTE_33 = DenteEnum.CANINO_INFERIOR_ESQUERDO_PERMANENTE.getNumero();
	public static final Integer DENTE_34 = DenteEnum.PRE_MOLAR1_INFERIOR_ESQUERDO_PERMANENTE.getNumero();
	public static final Integer DENTE_35 = DenteEnum.PRE_MOLAR2_INFERIOR_ESQUERDO_PERMANENTE.getNumero();
	public static final Integer DENTE_36 = DenteEnum.MOLAR1_INFERIOR_ESQUERDO_PERMANENTE.getNumero();
	public static final Integer DENTE_37 = DenteEnum.MOLAR2_INFERIOR_ESQUERDO_PERMANENTE.getNumero();
	public static final Integer DENTE_38 = DenteEnum.MOLAR3_INFERIOR_ESQUERDO_PERMANENTE.getNumero();
	
	public static final Integer DENTE_41 = DenteEnum.INCISIVO_CENTRAL_INFERIOR_DIREITO_PERMANENTE.getNumero();
	public static final Integer DENTE_42 = DenteEnum.INCISIVO_LATERAL_INFERIOR_DIREITO_PERMANENTE.getNumero();
	public static final Integer DENTE_43 = DenteEnum.CANINO_INFERIOR_DIREITO_PERMANENTE.getNumero();
	public static final Integer DENTE_44 = DenteEnum.PRE_MOLAR1_INFERIOR_DIREITO_PERMANENTE.getNumero();
	public static final Integer DENTE_45 = DenteEnum.PRE_MOLAR2_INFERIOR_DIREITO_PERMANENTE.getNumero();
	public static final Integer DENTE_46 = DenteEnum.MOLAR1_INFERIOR_DIREITO_PERMANENTE.getNumero();
	public static final Integer DENTE_47 = DenteEnum.MOLAR2_INFERIOR_DIREITO_PERMANENTE.getNumero();
	public static final Integer DENTE_48 = DenteEnum.MOLAR3_INFERIOR_DIREITO_PERMANENTE.getNumero();
	
	public static final Integer DENTE_51 = DenteEnum.INCISIVO_CENTRAL_SUPERIOR_DIREITO_DECIDUO.getNumero();
	public static final Integer DENTE_52 = DenteEnum.INCISIVO_LATERAL_SUPERIOR_DIREITO_DECIDUO.getNumero();
	public static final Integer DENTE_53 = DenteEnum.CANINO_SUPERIOR_DIREITO_DECIDUO.getNumero();
	public static final Integer DENTE_54 = DenteEnum.MOLAR1_SUPERIOR_DIREITO_DECIDUO.getNumero();
	public static final Integer DENTE_55 = DenteEnum.MOLAR2_SUPERIOR_DIREITO_DECIDUO.getNumero();
	public static final Integer DENTE_56 = DenteEnum.MOLAR3_SUPERIOR_DIREITO_DECIDUO.getNumero();
	
	public static final Integer DENTE_61 = DenteEnum.INCISIVO_CENTRAL_SUPERIOR_ESQUERDO_DECIDUO.getNumero();
	public static final Integer DENTE_62 = DenteEnum.INCISIVO_LATERAL_SUPERIOR_ESQUERDO_DECIDUO.getNumero();
	public static final Integer DENTE_63 = DenteEnum.CANINO_SUPERIOR_ESQUERDO_DECIDUO.getNumero();
	public static final Integer DENTE_64 = DenteEnum.MOLAR1_SUPERIOR_ESQUERDO_DECIDUO.getNumero();
	public static final Integer DENTE_65 = DenteEnum.MOLAR2_SUPERIOR_ESQUERDO_DECIDUO.getNumero();
	public static final Integer DENTE_66 = DenteEnum.MOLAR3_SUPERIOR_ESQUERDO_DECIDUO.getNumero();
	
	public static final Integer DENTE_71 = DenteEnum.INCISIVO_CENTRAL_INFERIOR_ESQUERDO_DECIDUO.getNumero();
	public static final Integer DENTE_72 = DenteEnum.INCISIVO_LATERAL_INFERIOR_ESQUERDO_DECIDUO.getNumero();
	public static final Integer DENTE_73 = DenteEnum.CANINO_INFERIOR_ESQUERDO_DECIDUO.getNumero();
	public static final Integer DENTE_74 = DenteEnum.MOLAR1_INFERIOR_ESQUERDO_DECIDUO.getNumero();
	public static final Integer DENTE_75 = DenteEnum.MOLAR2_INFERIOR_ESQUERDO_DECIDUO.getNumero();
	public static final Integer DENTE_76 = DenteEnum.MOLAR3_INFERIOR_ESQUERDO_DECIDUO.getNumero();
	
	public static final Integer DENTE_81 = DenteEnum.INCISIVO_CENTRAL_INFERIOR_DIREITO_DECIDUO.getNumero();
	public static final Integer DENTE_82 = DenteEnum.INCISIVO_LATERAL_INFERIOR_DIREITO_DECIDUO.getNumero();
	public static final Integer DENTE_83 = DenteEnum.CANINO_INFERIOR_DIREITO_DECIDUO.getNumero();
	public static final Integer DENTE_84 = DenteEnum.MOLAR1_INFERIOR_DIREITO_DECIDUO.getNumero();
	public static final Integer DENTE_85 = DenteEnum.MOLAR2_INFERIOR_DIREITO_DECIDUO.getNumero();
	public static final Integer DENTE_86 = DenteEnum.MOLAR3_INFERIOR_DIREITO_DECIDUO.getNumero();

	public static final Integer QUADRANTE_1 = QuadranteEnum.SUPERIOR_DIREITO_PERMANENTE.getNumero();
	public static final Integer QUADRANTE_2 = QuadranteEnum.SUPERIOR_ESQUERDO_PERMANENTE.getNumero();
	public static final Integer QUADRANTE_3 = QuadranteEnum.INFERIOR_ESQUERDO_PERMANENTE.getNumero();
	public static final Integer QUADRANTE_4 = QuadranteEnum.INFERIOR_DIREITO_PERMANENTE.getNumero();
	public static final Integer QUADRANTE_5 = QuadranteEnum.SUPERIOR_DIREITO_DECIDUO.getNumero();
	public static final Integer QUADRANTE_6 = QuadranteEnum.SUPERIOR_ESQUERDO_DECIDUO.getNumero();
	public static final Integer QUADRANTE_7 = QuadranteEnum.INFERIOR_ESQUERDO_DECIDUO.getNumero();
	public static final Integer QUADRANTE_8 = QuadranteEnum.INFERIOR_DIREITO_DECIDUO.getNumero();
	
	public String getDescricao();
	public void setDescricao(String descricao);
	
}
