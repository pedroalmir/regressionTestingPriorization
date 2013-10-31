package br.com.infowaypi.ecarebc.enums;

public enum CapituloProcedimentoEnum {


	CLINICOS_AMBULATORIAS("Procedimentos Clínicos Ambulatoriais", "20100007", "20105029", 1),
	CLINICOS_HOSPITALARES("Procedimentos Clínicos Hospitalares", "20201010", "20204140", 1),
	ENDOSCOPIA_DIAGNOSTICA("Endoscopia Diagnóstica", "40201015", "40201287", 2),
	ENDOSCOPIA_TERAPEUTICA("Endoscopia Terapêutica", "40202011", "40202658", 3),
	MEDICINA_NUCLEAR("Medicina Nuclear", "40701018", "40712021", 2),
	RADIOLOGIA_GERAL("Radiologia Geral", "40801012", "40812146", 3),
	ULTRASONOGRAFIA("UltraSonografia", "40901017", "40902110", 2),
	TOMOGRAFIA_COMPUTADORIZADA("Tomografia Computadorizada", "41001010", "41002032", 2),
	RESSONANCIA_MAGNETICA("Ressonância Magnética", "41101014", "41102010", 3),
	RADIOTERAPIA("Radioterapia", "41201019", "41202104", 3),
	EXAMES_ESPECIFICOS("Exames Específicos", "41301013", "41301382", 1),
	TESTE_PARA_DIAGNOSTICO("Testes para Diagnóstico", "41401018", "41401506", 1),
	OUTROS_PROCEDIMENTO_DIAGNOSTICOS("Outros Procedimentos Diagnósticos", "41501012", "41501160", 1);

	private String descricao;
	private String codigoInicial;
	private String codigoFinal;
	private int porteAnestesico;

	CapituloProcedimentoEnum(String descricao, String codigoInicial, String codigoFinal, int porteAnestesico){
		this.descricao = descricao;
		this.codigoInicial = codigoInicial;
		this.codigoFinal = codigoFinal;
		this.porteAnestesico = porteAnestesico;
	}

	public String descricao() {
		return descricao;
	}

	public String codigoInicial() {
		return codigoInicial;
	}

	public String codigoFinal() {
		return codigoFinal;
	}

	public static boolean isAptoASolicitacaoDeAcompanhamentoAnesteciso(String codigo){
		if(isCapituloClinicosAmbulatoriais(codigo)){
			return true;
		}
		
		if (isCapituloClinicosHospitalares(codigo)){
			return true;	
		}
		
		if (isEndoscopiaDiagnosticas(codigo)){
			return true;
		}
		
		if (isEndoscopiaTerapeutica(codigo)){
			return true;
		}
		
		if (isMedicinaNuclear(codigo)){
			return true;
		}
		
		if (isRadiologiaGeral(codigo)){
			return true;
		}
		
		if (isUltraSonografia(codigo)){
			return true;
		}
		
		if (isTomografiaComputadorizada(codigo)){
			return true;
		}
		
		if (isRessonanciaMagnetica(codigo)){
			return true;
		}
		
		if (isRadioterapia(codigo)){
			return true;
		}
	    
		if (isExamesEspecificos(codigo)){
			return true;
		}
		
	    if (isTesteParaDiagnostico(codigo)){
	    	return true;
		}

		if (isOutrosProcedimentosDiagnosticos(codigo)){
			return true;
		}
		
		return false;
	}

	public static int getPorteAnestesicoDoCapituloDoProcedimentos(String codigo){
		if (isCapituloClinicosAmbulatoriais(codigo)) {
			return CLINICOS_AMBULATORIAS.porteAnestesico;
		}

		if (isCapituloClinicosHospitalares(codigo)){
			return CLINICOS_HOSPITALARES.porteAnestesico;
		}

		if (isEndoscopiaDiagnosticas(codigo)){
			return ENDOSCOPIA_DIAGNOSTICA.porteAnestesico;
		}

		if (isEndoscopiaTerapeutica(codigo)){
			return ENDOSCOPIA_TERAPEUTICA.porteAnestesico;
		}
		
		if (isMedicinaNuclear(codigo)){
			return MEDICINA_NUCLEAR.porteAnestesico;
		}

		if (isRadiologiaGeral(codigo)){
			return RADIOLOGIA_GERAL.porteAnestesico;
		}
		
		if (isUltraSonografia(codigo)){
			return ULTRASONOGRAFIA.porteAnestesico;
		}
		
		if (isTomografiaComputadorizada(codigo)){
			return TOMOGRAFIA_COMPUTADORIZADA.porteAnestesico;
		}
		
		if (isRessonanciaMagnetica(codigo)){
			return RESSONANCIA_MAGNETICA.porteAnestesico;
		}
		
		if (isRadioterapia(codigo)){
			return RADIOTERAPIA.porteAnestesico;
		}
		
		if (isExamesEspecificos(codigo)){
			return EXAMES_ESPECIFICOS.porteAnestesico;
		}
		
		if (isTesteParaDiagnostico(codigo)){
			return TESTE_PARA_DIAGNOSTICO.porteAnestesico;
		}
		
		if (isOutrosProcedimentosDiagnosticos(codigo)){
			return OUTROS_PROCEDIMENTO_DIAGNOSTICOS.porteAnestesico;
		}
		
		return 0;
	}

	private static boolean isCapituloClinicosAmbulatoriais(String codigo){
		if ((codigo.compareToIgnoreCase(CLINICOS_AMBULATORIAS.codigoInicial) >=0) && (codigo.compareToIgnoreCase(CLINICOS_AMBULATORIAS.codigoFinal) <=0)){
			return true;
		}

		return false;
	}

	private static boolean isCapituloClinicosHospitalares(String codigo){
		if ((codigo.compareToIgnoreCase(CLINICOS_HOSPITALARES.codigoInicial) >=0) && (codigo.compareToIgnoreCase(CLINICOS_HOSPITALARES.codigoFinal) <=0)){
			return true;
		}

		return false;
	}

	private static boolean isEndoscopiaDiagnosticas(String codigo){
		if ((codigo.compareToIgnoreCase(ENDOSCOPIA_DIAGNOSTICA.codigoInicial) >=0) && (codigo.compareToIgnoreCase(ENDOSCOPIA_DIAGNOSTICA.codigoFinal) <=0)){
			return true;
		}

		return false;
	}

	private static boolean isEndoscopiaTerapeutica(String codigo){
		if ((codigo.compareToIgnoreCase(ENDOSCOPIA_TERAPEUTICA.codigoInicial) >=0) && (codigo.compareToIgnoreCase(ENDOSCOPIA_TERAPEUTICA.codigoFinal) <=0)){
			return true;
		}

		return false;
	}

	private static boolean isMedicinaNuclear(String codigo){
		if ((codigo.compareToIgnoreCase(MEDICINA_NUCLEAR.codigoInicial) >=0) && (codigo.compareToIgnoreCase(MEDICINA_NUCLEAR.codigoFinal) <=0)){
			return true;
		}

		return false;
	}

	private static boolean isRadiologiaGeral(String codigo){
		if ((codigo.compareToIgnoreCase(RADIOLOGIA_GERAL.codigoInicial) >=0) && (codigo.compareToIgnoreCase(RADIOLOGIA_GERAL.codigoFinal) <=0)){
			return true;
		}

		return false;
	}

	private static boolean isUltraSonografia(String codigo){
		if ((codigo.compareToIgnoreCase(ULTRASONOGRAFIA.codigoInicial) >=0) && (codigo.compareToIgnoreCase(ULTRASONOGRAFIA.codigoFinal) <=0)){
			return true;
		}

		return false;
	}

	private static boolean isTomografiaComputadorizada(String codigo){
		if ((codigo.compareToIgnoreCase(TOMOGRAFIA_COMPUTADORIZADA.codigoInicial) >=0) && (codigo.compareToIgnoreCase(TOMOGRAFIA_COMPUTADORIZADA.codigoFinal) <=0)){
			return true;
		}

		return false;
	}

	private static boolean isRessonanciaMagnetica(String codigo){
		if ((codigo.compareToIgnoreCase(RESSONANCIA_MAGNETICA.codigoInicial) >=0) && (codigo.compareToIgnoreCase(RESSONANCIA_MAGNETICA.codigoFinal) <=0)){
			return true;
		}

		return false;
	}

	private static boolean isRadioterapia(String codigo){
		if ((codigo.compareToIgnoreCase(RADIOTERAPIA.codigoInicial) >=0) && (codigo.compareToIgnoreCase(RADIOTERAPIA.codigoFinal) <=0)){
			return true;
		}

		return false;
	}

	private static boolean isExamesEspecificos(String codigo){
		if ((codigo.compareToIgnoreCase(EXAMES_ESPECIFICOS.codigoInicial) >=0) && (codigo.compareToIgnoreCase(EXAMES_ESPECIFICOS.codigoFinal) <=0)){
			return true;
		}

		return false;
	}

	private static boolean isTesteParaDiagnostico(String codigo){
		if ((codigo.compareToIgnoreCase(TESTE_PARA_DIAGNOSTICO.codigoInicial) >=0) && (codigo.compareToIgnoreCase(TESTE_PARA_DIAGNOSTICO.codigoFinal) <=0)){
			return true;
		}

		return false;
	}

	private static boolean isOutrosProcedimentosDiagnosticos(String codigo){
		if ((codigo.compareToIgnoreCase(OUTROS_PROCEDIMENTO_DIAGNOSTICOS.codigoInicial) >=0) && (codigo.compareToIgnoreCase(OUTROS_PROCEDIMENTO_DIAGNOSTICOS.codigoFinal) <=0)){
			return true;
		}

		return false;
	}
}
