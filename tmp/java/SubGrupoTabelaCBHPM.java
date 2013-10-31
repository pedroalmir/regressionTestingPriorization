package br.com.infowaypi.ecarebc.atendimentos.enums;

/**
 * Enumeration para especificar os subgrupos de procedimentos da tabela CBHPM
 * @author Danilo Nogueira Portela
 *
 */
public enum SubGrupoTabelaCBHPM {

	//Eletrofisiol�gicos/Mec�nicos e Funcionais
	ECG_TE("40101","ECG - TE"),
	TUBO_DIGESTIVO("40102", "Tubo Digestivo"),
	ELETROENCEFALOGRAMA("40103","Eletroencefalograma"),			
	EXAMES_OSTEO_MUSCULO_ARTICULARES("40104","Exames �steo-M�sculo-Articulares"),			
	ESPIROMETRIA("40105","Espirometria"),			
	
	//Endosc�picos
	ENDOSCOPIA_DIAGNOSTICA("40201","Endoscopia Diagn�stica"),
	ENDOSCOPIA_TERAPEUTICA("40202","Endoscopia Terap�utica"),	
	
	//Medicina Laboratorial
	BIOQUIMICA_1("40301","Bioqu�mica I"),
	BIOQUIMICA_2("40302","Bioqu�mica II"),
	COPROLOGIA("40303","Coprologia"),
	HEMATOLOGIA_LABORATORIAL("40304","Hematologia Laboratorial"),
	ENDOCRINOLOGIA_LABORATORIAL("40305","Endocrinologia Laboratorial"),
	IMUNOLOGIA_1("40306","Imunologia 1"),
	IMUNOLOGIA_2("40307","Imunologia 2"),
	IMUNOLOGIA_3("40308","Imunologia 3"),
	LIQUIDOS("40309","L�quidos (Cefalorraqueano (L�quor), Seminal, Amni�tico, Sinovial e outros)"),
	MICROBIOLOGIA("40310","Microbiologia"),
	URINALISE("40311","Urin�lise"),
	MEDICINA_LABORATORIAL_DIVERSOS("40312","Diversos de Medicina Laboratorial"),
	TOXICOLOGIA_MONITORACAO_TERAPEUTICA("40313","Toxicologia/Monitora��o Terap�utica"),
	BIOLOGIA_MOLECULAR("40314","Biologia Molecular"),
	
	//Medicina Transfusional
	TRANSFUSAO("40401","Transfus�o"),
	PROCESSAMENTO("40402","Processamento"),
	MEDICINA_TRANSFUSIONAL_PROCEDIMENTOS("40403","Procedimentos de Medicina Transfusional"),
	
	//Gen�tica
	CITOGENETICA("40501","Citogen�tica"),
	GENETICA_BIOQUIMICA("40502","Gen�tica Bioqu�mica"),
	GENETICA_MOLECULAR("40503","Gen�tica Molecular"),
	
	//Anatomia Patol�gica e Citopatologia
	ANATOMIA_PATOLOGICA_E_CITOPATOLOGIA_PROCEDIMENTOS("40601","Procedimentos de Anatomia Patol�gica e Citopatologia"),
	
	//Medicina Nuclear
	CARDIOVASCULAR_INVIVO("40701","Cardiovascular in vivo"),
	DIGESTIVO_INVIVO("40702","Cardiovascular in vivo"),
	ENDOCRINO_INVIVO("40703","End�crino in vivo"),
	GENITURINARIO_INVIVO("40704","Geniturin�rio in vivo"),
	HEMATOLOGICO_INVIVO("40705","Hematol�gico in vivo"),
	MUSCULO_ESQUELETICO_INVIVO("40706","M�sculo Esquel�tico 'in vivo'"),
	NERVOSO_INVIVO("40707","Nervoso 'in vivo'"),
	ONCOLOGIA_INFECTOLOGIA_INVIVO("40708","Oncologia-Infectologia 'in vivo'"),
	RESPIRATORIO_INVIVO("40709","Respirat�rio 'in vivo'"),
	TERAPIA_INVIVO("40710","Terapia 'in vivo'"),
	MEDICINA_NUCLEAR_OUTROS_INVIVO("40711","Outros 'in vivo' de Medicina Nuclear"),
	RADIOIMUNOENSAIO_INVITRO("40712","Radio-imuno-ensaio 'in vitro'"),		 
	
	//Radiologia Geral
	CRANIO_E_FACE("40801","Cr�nio e Face"),
	COLUNA_VERTEBRAL("40802","Coluna Vertebral"),
	ESQUELETO_TORAXICO_E_MEMBROS_SUPERIORES("40803","Esqueleto Tor�xico e Membros Superiores"),
	BACIA_E_MEMBROS_INFERIORES("40804","Bacia e Membros Inferiores"),
	TORAX("40805","T�rax"),
	SISTEMA_DIGESTIVO("40806","Sistema Digestivo"),
	SISTEMA_URINARIO("40807","Sistema Urin�rio"),
	RADIOLOGIA_GERAL_OUTROS_EXAMES("40808","Outros Exames de Radiologia Geral"),
	RADIOLOGIA_GERAL_PROCEDIMENTOS_ESPECIAIS("40809","Procedimentos Especiais de Radiologia Geral"),
	NEURORRADIOLOGIA("40810","Neurorradiologia"),
	RADIOSCOPIA("40811","Radioscopia"),
	ANGIOGRAFIA_VICERAL_E_PERIFERICA("40812","Angiologia Viceral e Perif�rica"),
	RADIOLOGIA_INTERVENCIONISTA_1("40813","Radiologia Intervencionista 1"),
	RADIOLOGIA_INTERVENCIONISTA_2("40814","Radiologia Intervencionista 2"),
	
	//Ultra-Sonografia
	ULTRA_SONOGRAFIA_DIAGNOSTICA("40901","Ultra-Sonografia Diagn�stica"),
	ULTRA_SONOGRAFIA_INTERVENCIONISTA("40902","Ultra-Sonografia Intervencionista"),
	
	//Tomografia Computadorizada
	TOMOGRAFIA_COMPUTADORIZADA_DIAGNOSTICA("41001","Tomografia Computadorizada Diagn�stica"),
	TOMOGRAFIA_COMPUTADORIZADA_INTERVENCIONISTA("41002","Tomografia Computadorizada Intervencionista"),

	//Resson�ncia Magn�tica
	RESSONANCIA_MAGNETICA_DIAGNOSTICA("41101","Resson�ncia Magn�tica Diagn�stica"),
	RESSONANCIA_MAGNETICA_INTERVENCIONISTA("41102","Resson�ncia Magn�tica Intervencionista"),
	
	//Radioterapia
	RADIOTERAPIA_EXTERNA("41201","Radioterapia Externa"),
	RADIOTERAPIA_INTERVENCIONISTA("41202","Radioterapia Intervencionista"),
	
	//Exames Espec�ficos
	EXAMES_ESPECIFICOS_PROCEDIMENTOS("41301","Procedimentos de exames espec�ficos"),
	
	//Testes para Diagn�stico
	TESTES_PARA_DIAGNOSTICO_PROCEDIMENTOS("41401","Procedimentos de Testes para Diagn�stico"),
	
	//Outros
	OUTROS_PROCEDIMENTOS_DIAGNOSTICOS("41501","Outros Procedimentos Diagn�sticos");
	
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
