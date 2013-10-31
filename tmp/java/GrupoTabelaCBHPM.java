package br.com.infowaypi.ecarebc.atendimentos.enums;

/**
 * Enumeration para especificar os grupos de procedimentos da tabela CBHPM
 * @author Danilo Nogueira Portela
 */
public enum GrupoTabelaCBHPM {
	
	NENHUM(-1,"Nenhum"),
	LABORATORIAL(1,"Procedimentos Laboratoriais",
			SubGrupoTabelaCBHPM.ENDOSCOPIA_TERAPEUTICA,
			SubGrupoTabelaCBHPM.BIOQUIMICA_1,
			SubGrupoTabelaCBHPM.BIOQUIMICA_2,
			SubGrupoTabelaCBHPM.COPROLOGIA,
			SubGrupoTabelaCBHPM.HEMATOLOGIA_LABORATORIAL,
			SubGrupoTabelaCBHPM.ENDOCRINOLOGIA_LABORATORIAL,
			SubGrupoTabelaCBHPM.IMUNOLOGIA_1,
			SubGrupoTabelaCBHPM.IMUNOLOGIA_2,
			SubGrupoTabelaCBHPM.IMUNOLOGIA_3,
			SubGrupoTabelaCBHPM.LIQUIDOS,
			SubGrupoTabelaCBHPM.MICROBIOLOGIA,
			SubGrupoTabelaCBHPM.URINALISE,
			SubGrupoTabelaCBHPM.MEDICINA_LABORATORIAL_DIVERSOS,
			SubGrupoTabelaCBHPM.TOXICOLOGIA_MONITORACAO_TERAPEUTICA,
			SubGrupoTabelaCBHPM.BIOLOGIA_MOLECULAR,
			SubGrupoTabelaCBHPM.TRANSFUSAO,
			SubGrupoTabelaCBHPM.PROCESSAMENTO,
			SubGrupoTabelaCBHPM.MEDICINA_TRANSFUSIONAL_PROCEDIMENTOS,
			SubGrupoTabelaCBHPM.CITOGENETICA,
			SubGrupoTabelaCBHPM.GENETICA_BIOQUIMICA,
			SubGrupoTabelaCBHPM.GENETICA_MOLECULAR,
			SubGrupoTabelaCBHPM.ANATOMIA_PATOLOGICA_E_CITOPATOLOGIA_PROCEDIMENTOS,
			SubGrupoTabelaCBHPM.RADIOIMUNOENSAIO_INVITRO,
			SubGrupoTabelaCBHPM.TESTES_PARA_DIAGNOSTICO_PROCEDIMENTOS),
			
	RADIOLOGICO(2,"Procedimentos Radiológicos",
			SubGrupoTabelaCBHPM.ECG_TE,
			SubGrupoTabelaCBHPM.TUBO_DIGESTIVO,
			SubGrupoTabelaCBHPM.ELETROENCEFALOGRAMA,
			SubGrupoTabelaCBHPM.EXAMES_OSTEO_MUSCULO_ARTICULARES,
			SubGrupoTabelaCBHPM.ESPIROMETRIA,
			SubGrupoTabelaCBHPM.ENDOSCOPIA_DIAGNOSTICA,
			SubGrupoTabelaCBHPM.CARDIOVASCULAR_INVIVO,
			SubGrupoTabelaCBHPM.DIGESTIVO_INVIVO,
			SubGrupoTabelaCBHPM.ENDOCRINO_INVIVO,
			SubGrupoTabelaCBHPM.GENITURINARIO_INVIVO,
			SubGrupoTabelaCBHPM.HEMATOLOGICO_INVIVO,
			SubGrupoTabelaCBHPM.MUSCULO_ESQUELETICO_INVIVO,
			SubGrupoTabelaCBHPM.NERVOSO_INVIVO,
			SubGrupoTabelaCBHPM.ONCOLOGIA_INFECTOLOGIA_INVIVO,
			SubGrupoTabelaCBHPM.RESPIRATORIO_INVIVO,
			SubGrupoTabelaCBHPM.MEDICINA_NUCLEAR_OUTROS_INVIVO, 
			SubGrupoTabelaCBHPM.CRANIO_E_FACE,
			SubGrupoTabelaCBHPM.COLUNA_VERTEBRAL,
			SubGrupoTabelaCBHPM.ESQUELETO_TORAXICO_E_MEMBROS_SUPERIORES,
			SubGrupoTabelaCBHPM.BACIA_E_MEMBROS_INFERIORES,
			SubGrupoTabelaCBHPM.TORAX,
			SubGrupoTabelaCBHPM.SISTEMA_DIGESTIVO,
			SubGrupoTabelaCBHPM.SISTEMA_URINARIO,
			SubGrupoTabelaCBHPM.RADIOLOGIA_GERAL_OUTROS_EXAMES,
			SubGrupoTabelaCBHPM.RADIOLOGIA_GERAL_PROCEDIMENTOS_ESPECIAIS,
			SubGrupoTabelaCBHPM.NEURORRADIOLOGIA,
			SubGrupoTabelaCBHPM.RADIOSCOPIA,
			SubGrupoTabelaCBHPM.ANGIOGRAFIA_VICERAL_E_PERIFERICA,
			SubGrupoTabelaCBHPM.RADIOLOGIA_INTERVENCIONISTA_1,
			SubGrupoTabelaCBHPM.RADIOLOGIA_INTERVENCIONISTA_2,
			SubGrupoTabelaCBHPM.ULTRA_SONOGRAFIA_DIAGNOSTICA,
			SubGrupoTabelaCBHPM.ULTRA_SONOGRAFIA_INTERVENCIONISTA,
			SubGrupoTabelaCBHPM.TOMOGRAFIA_COMPUTADORIZADA_DIAGNOSTICA,
			SubGrupoTabelaCBHPM.TOMOGRAFIA_COMPUTADORIZADA_INTERVENCIONISTA,
			SubGrupoTabelaCBHPM.RESSONANCIA_MAGNETICA_DIAGNOSTICA,
			SubGrupoTabelaCBHPM.RESSONANCIA_MAGNETICA_INTERVENCIONISTA,
			SubGrupoTabelaCBHPM.RADIOTERAPIA_EXTERNA,
			SubGrupoTabelaCBHPM.RADIOTERAPIA_INTERVENCIONISTA,
			SubGrupoTabelaCBHPM.EXAMES_ESPECIFICOS_PROCEDIMENTOS,
			SubGrupoTabelaCBHPM.OUTROS_PROCEDIMENTOS_DIAGNOSTICOS);
	
	private Integer valor;
	private String nome;
	private SubGrupoTabelaCBHPM[] subGrupos;
	
	GrupoTabelaCBHPM(Integer valor, String nome, SubGrupoTabelaCBHPM ... subGrupos){
		this.valor = valor;
		this.nome = nome;
		this.subGrupos = subGrupos;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public SubGrupoTabelaCBHPM[] getSubGrupos() {
		return subGrupos;
	}

	public void setSubGrupos(SubGrupoTabelaCBHPM[] subGrupos) {
		this.subGrupos = subGrupos;
	}

	public Integer getValor() {
		return valor;
	}

	public void setValor(Integer valor) {
		this.valor = valor;
	}

}
