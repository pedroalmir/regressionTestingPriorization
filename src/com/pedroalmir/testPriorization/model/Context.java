/**
 * 
 */
package com.pedroalmir.testPriorization.model;

/**
 * @author Pedro Almir
 *
 */
public class Context {
	
	private final String baseFolder;
	private final String couplingFile;
	private final String sqfdFile;
	private final String testCoverageFile;
	private final String cyclomaticFile;
	private final String fclFile;
	
	/**
	 * @param numOfClasses
	 * @param numOfRequirements
	 */
	public Context(int numOfClasses, int numOfRequirements) {
		String BASE_FOLDER = "C:/Users/Pedro Almir/Dropbox/UFPI/Mestrado/Priorização de Casos de Teste/Arquivos Base/AleatoriosDistNormal/cl_%d_req_%d/";
		String COUPLING_FILE = "New-Coupling-cl-%d.csv";
		String SQFD_FILE = "New-SQFD-cl-%d-req-%d.csv";
		String TEST_COVERAGE_FILE = "New-TestCoverage-cl-%d-req-%d.csv";
		String CYCLOMATIC_FILE = "New-Complexity-cl-%d.csv";
		
		this.baseFolder = String.format(BASE_FOLDER, numOfClasses, numOfRequirements);
		this.couplingFile = String.format(COUPLING_FILE, numOfClasses);
		this.sqfdFile = String.format(SQFD_FILE, numOfClasses, numOfRequirements);
		this.testCoverageFile = String.format(TEST_COVERAGE_FILE, numOfClasses, numOfRequirements);
		this.cyclomaticFile = String.format(CYCLOMATIC_FILE, numOfClasses);
		
		String FCL_PATH = "C:/Users/Pedro Almir/Desktop/MeusProjetos/IC_TCC/Athena/workspaceKepler/RegressionTestingPriorization/data/RegressionTestingPriorization.fcl";
		this.fclFile = FCL_PATH;
	}
	
	/**
	 * @return the baseFolder
	 */
	public String getBaseFolder() {
		return baseFolder;
	}
	/**
	 * @return the couplingFile
	 */
	public String getCouplingFile() {
		return couplingFile;
	}
	/**
	 * @return the sqfdFile
	 */
	public String getSqfdFile() {
		return sqfdFile;
	}
	/**
	 * @return the testCoverageFile
	 */
	public String getTestCoverageFile() {
		return testCoverageFile;
	}
	/**
	 * @return the cyclomaticFile
	 */
	public String getCyclomaticFile() {
		return cyclomaticFile;
	}
	/**
	 * @return the fclFile
	 */
	public String getFclFile() {
		return fclFile;
	}
}
