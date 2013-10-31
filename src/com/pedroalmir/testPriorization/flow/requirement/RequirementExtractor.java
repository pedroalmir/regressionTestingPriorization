/**
 * 
 */
package com.pedroalmir.testPriorization.flow.requirement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

import com.pedroalmir.testPriorization.model.Klass;
import com.pedroalmir.testPriorization.model.Requirement;
import com.pedroalmir.testPriorization.util.enums.EnumJhmType;
import com.pedroalmir.testPriorization.util.file.FileAnalyzer;
import com.pedroalmir.testPriorization.util.file.MyDirectoryWalker;

/**
 * @author Pedro Almir
 * 
 */
public class RequirementExtractor {
	private static final String MYPATH = "C:/Eclipse/junojee/infoway/desenvolvimento_MAA/src/";
	private static final String MYEXTENSION = ".jhm.xml";
	private static final int MAXDEPTH = 100;
	private static final String FILEADDRESS = "C:/RequisitosMAA.csv";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		File dir = new File(MYPATH);
		MyDirectoryWalker walker = new MyDirectoryWalker(MYEXTENSION, MAXDEPTH);
		List<File> files = new ArrayList<File>();

		// collect all files with specified extension inside dir
		if (dir.isDirectory()) {
			files = walker.getExtensionFiles(dir);
		} else {
			System.out.println("Isn't a folder!");
		}

		List<String[]> data = new ArrayList<String[]>();
		CSVWriter writer = new CSVWriter(new FileWriter(FILEADDRESS), ';');

		/* Header */
		data.add(new String[] { "Requeriment", "Description", "Type" });

		// collect information from files
		String description = "", content = "", type = "";
		for (int i = 0; i < files.size(); i++) {
			File file = files.get(i);

			content = FileAnalyzer.getFileContent(file);
			// get description
			description = FileAnalyzer.getPatternFirst(content, "(<description.*?>)(.+?)(</description>)", 2);
			if (description.equals("")) {
				description = FileAnalyzer.getPatternFirst(content, "display-name=\"(.+?)\"", 1);
			}

			// get jhm type
			if (content.contains("<class-mapping")) {
				type = EnumJhmType.CLASS_MAPPING.getDescription();
			} else if (content.contains("<flow")) {
				type = EnumJhmType.FLOW.getDescription();
			} else if (content.contains("<report")) {
				type = EnumJhmType.REPORT.getDescription();
			}

			/* Body */
			data.add(new String[] { file.getName(), description, type });
		}

		writer.writeAll(data);
		writer.close();
	}
	
	/**
	 * @param files
	 * @return list of requirements
	 * @throws IOException 
	 */
	public static LinkedList<Requirement> extractRequirements(List<File> jhmFiles, List<File> javaFiles) throws IOException{
		LinkedList<Requirement> requirements = new LinkedList<Requirement>();
		for (int i = 0; i < jhmFiles.size(); i++) {
			File file = jhmFiles.get(i);
			/* File content */
			//String content = FileAnalyzer.getFileContent(file);
			String content = FileAnalyzer.getContent(file);
			
			if(content.isEmpty()){
				continue;
			}
			
			if(file.getPath().contains(".jhm.xml") && FileAnalyzer.countPattern(content, "<jheat-tag(.+?)name=\"(.+?)\"", false) == 0){
				/*  */
				Requirement requirement = new Requirement(Long.valueOf(i), file.getName().replace(".jhm.xml", ""));
				/* Get File Description */
				String description = FileAnalyzer.getPatternFirst(content, "(<description.*?>)(.+?)(</description>)", 2);
				if (description.equals("")) {
					description = FileAnalyzer.getPatternFirst(content, "display-name=\"(.+?)\"", 1);
				}
				requirement.setDescription(description);
				/* Get Main Class */
				String mainClassPath = FileAnalyzer.getPatternFirst(content, "class=\"(.+?)\"", 1);
				if(mainClassPath != null && !mainClassPath.isEmpty()){
					int begin = mainClassPath.lastIndexOf('.') + 1;
					String klassName = mainClassPath.substring(begin, mainClassPath.length()) + ".java";
					File mainJava = getFileByClassName(klassName, javaFiles);
					if(mainJava != null){
						Klass mainKlass = new Klass(Long.valueOf(i), klassName);
						String klassContent = FileAnalyzer.getContent(mainJava);
						if(klassContent.isEmpty()){
							continue;
						}
						mainKlass.setContent(klassContent);
						requirement.setMainClass(mainKlass);
					}else{
						continue;
					}
				}
				/* get jhm type */
				if (content.contains("<class-mapping")) {
					requirement.setEnumJhmType(EnumJhmType.CLASS_MAPPING);
				} else if (content.contains("<flow")) {
					requirement.setEnumJhmType(EnumJhmType.FLOW);
				} else if (content.contains("<report")) {
					requirement.setEnumJhmType(EnumJhmType.REPORT);
				}else{
					System.out.println("Aqui!!");
				}
				System.out.println(requirement);
				requirements.add(requirement);
			}
		}
		return requirements;
	}

	/**
	 * @param klassName
	 * @param files
	 * @return
	 */
	private static File getFileByClassName(String klassName, List<File> files) {
		for (File file : files) {
			if(file.getName().equals(klassName)){
				return file;
			}
		}
		return null;
	}

}
