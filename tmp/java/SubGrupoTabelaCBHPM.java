package br.com.infowaypi.ecarebc.atendimentos.enums;

/**
 * Enumeration para especificar os subgrupos de procedimentos da tabela CBHPM
 * @author Danilo Nogueira Portela
 *
 */
public enum SubGrupoTabelaCBHPM {

	//Eletrofisiológicos/Mecânicos e Funcionais
	ECG_TE("40101","ECG - TE"),
	TUBO_DIGESTIVO("40102", "Tubo Digestivo"),
	ELETROENCEFALOGRAMA("40103","Eletroencefalograma"),			
	EXAMES_OSTEO_MUSCULO_ARTICULARES("40104","Exames Ósteo-Músculo-Articulares"),			
	ESPIROMETRIA("40105","Espirometria"),			
	
	//Endoscópicos
	ENDOSCOPIA_DIAGNOSTICA("40201","Endoscopia Diagnóstica"),
	ENDOSCOPIA_TERAPEUTICA("40202","Endoscopia Terapêutica"),	
	
	//Medicina Laboratorial
	BIOQUIMICA_1("40301","Bioquímica I"),
	BIOQUIMICA_2("40302","Bioquímica II"),
	COPROLOGIA("40303","Coprologia"),
	HEMATOLOGIA_LABORATORIAL("40304","Hematologia Laboratorial"),
	ENDOCRINOLOGIA_LABORATORIAL("40305","Endocrinologia Laboratorial"),
	IMUNOLOGIA_1("40306","Imunologia 1"),
	IMUNOLOGIA_2("40307","Imunologia 2"),
	IMUNOLOGIA_3("40308","Imunologia 3"),
	LIQUIDOS("40309","Líquidos (Cefalorraqueano (Líquor), Seminal, Amniótico, Sinovial e outros)"),
	MICROBIOLOGIA("40310","Microbiologia"),
	URINALISE("40311","Urinálise"),
	MEDICINA_LABORATORIAL_DIVERSOS("40312","Diversos de Medicina Laboratorial"),
	TOXICOLOGIA_MONITORACAO_TERAPEUTICA("40313","Toxicologia/Monitoração Terapêutica"),
	BIOLOGIA_MOLECULAR("40314","Biologia Molecular"),
	
	//Medicina Transfusional
	TRANSFUSAO("40401","Transfusão"),
	PROCESSAMENTO("40402","Processamento"),
	MEDICINA_TRANSFUSIONAL_PROCEDIMENTOS("40403","Procedimentos de Medicina Transfusional"),
	
	//Genética
	CITOGENETICA("40501","Citogenética"),
	GENETICA_BIOQUIMICA("40502","Genética Bioquímica"),
	GENETICA_MOLECULAR("40503","Genética Molecular"),
	
	//Anatomia Patológica e Citopatologia
	ANATOMIA_PATOLOGICA_E_CITOPATOLOGIA_PROCEDIMENTOS("40601","Procedimentos de Anatomia Patológica e Citopatologia"),
	
	//Medicina Nuclear
	CARDIOVASCULAR_INVIVO("40701","Cardiovascular in vivo"),
	DIGESTIVO_INVIVO("40702","Cardiovascular in vivo"),
	ENDOCRINO_INVIVO("40703","Endócrino in vivo"),
	GENITURINARIO_INVIVO("40704","Geniturinário in vivo"),
	HEMATOLOGICO_INVIVO("40705","Hematológico in vivo"),
	MUSCULO_ESQUELETICO_INVIVO("40706","Músculo Esquelético 'in vivo'"),
	NERVOSO_INVIVO("40707","Nervoso 'in vivo'"),
	ONCOLOGIA_INFECTOLOGIA_INVIVO("40708","Oncologia-Infectologia 'in vivo'"),
	RESPIRATORIO_INVIVO("40709","Respiratório 'in vivo'"),
	TERAPIA_INVIVO("40710","Terapia 'in vivo'"),
	MEDICINA_NUCLEAR_OUTROS_INVIVO("40711","Outros 'in vivo' de Medicina Nuclear"),
	RADIOIMUNOENSAIO_INVITRO("40712","Radio-imuno-ensaio 'in vitro'"),		 
	
	//Radiologia Geral
	CRANIO_E_FACE("40801","Crânio e Face"),
	COLUNA_VERTEBRAL("40802","Coluna Vertebral"),
	ESQUELETO_TORAXICO_E_MEMBROS_SUPERIORES("40803","Esqueleto Toráxico e Membros Superiores"),
	BACIA_E_MEMBROS_INFERIORES("40804","Bacia e Membros Inferiores"),
	TORAX("40805","Tórax"),
	SISTEMA_DIGESTIVO("40806","Sistema Digestivo"),
	SISTEMA_URINARIO("40807","Sistema Urinário"),
	RADIOLOGIA_GERAL_OUTROS_EXAMES("40808","Outros Exames de Radiologia Geral"),
	RADIOLOGIA_GERAL_PROCEDIMENTOS_ESPECIAIS("40809","Procedimentos Especiais de Radiologia Geral"),
	NEURORRADIOLOGIA("40810","Neurorradiologia"),
	RADIOSCOPIA("40811","Radioscopia"),
	ANGIOGRAFIA_VICERAL_E_PERIFERICA("40812","Angiologia Viceral e Periférica"),
	RADIOLOGIA_INTERVENCIONISTA_1("40813","Radiologia Intervencionista 1"),
	RADIOLOGIA_INTERVENCIONISTA_2("40814","Radiologia Intervencionista 2"),
	
	//Ultra-Sonografia
	ULTRA_SONOGRAFIA_DIAGNOSTICA("40901","Ultra-Sonografia Diagnóstica"),
	ULTRA_SONOGRAFIA_INTERVENCIONISTA("40902","Ultra-Sonografia Intervencionista"),
	
	//Tomografia Computadorizada
	TOMOGRAFIA_COMPUTADORIZADA_DIAGNOSTICA("41001","Tomografia Computadorizada Diagnóstica"),
	TOMOGRAFIA_COMPUTADORIZADA_INTERVENCIONISTA("41002","Tomografia Computadorizada Intervencionista"),

	//Ressonância Magnética
	RESSONANCIA_MAGNETICA_DIAGNOSTICA("41101","Ressonância Magnética Diagnóstica"),
	RESSONANCIA_MAGNETICA_INTERVENCIONISTA("41102","Ressonância Magnética Intervencionista"),
	
	//Radioterapia
	RADIOTERAPIA_EXTERNA("41201","Radioterapia Externa"),
	RADIOTERAPIA_INTERVENCIONISTA("41202","Radioterapia Intervencionista"),
	
	//Exames Específicos
	EXAMES_ESPECIFICOS_PROCEDIMENTOS("41301","Procedimentos de exames específicos"),
	
	//Testes para Diagnóstico
	TESTES_PARA_DIAGNOSTICO_PROCEDIMENTOS("41401","Procedimentos de Testes para Diagnóstico"),
	
	//Outros
	OUTROS_PROCEDIMENTOS_DIAGNOSTICOS("41501","Outros Procedimentos Diagnósticos");
	
	private String nome;
	private String codigoSubGrupo;
	
	SubGrupoTabelaCBHPM(String codigo, String nome){
		this.nome = nome;
		this.codigoSubGrupo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCodigo() {
		return codigoSubGrupo;
	}

	public void setCodigo(String codigo) {
		this.codigoSubGrupo = codigo;
	}
	
	public static SubGrupoTabelaCBHPM getSubGrupoPorCodigo(String codigo) {
		for(SubGrupoTabelaCBHPM subGrupo : SubGrupoTabelaCBHPM.values()) {
			if(codigo.startsWith(subGrupo.getCodigo())) {
				return subGrupo;
			}
		}
		return OUTROS_PROCEDIMENTOS_DIAGNOSTICOS;
	}

}
