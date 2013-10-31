package com.pedroalmir.testPriorization.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.pedroalmir.testPriorization.util.enums.EnumPatternPosition;

public class FileAnalyzer {

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String getFileContent(File file) throws IOException {
		String line = "";
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			line += scanner.nextLine() + "\n";
		}
		return line;
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String getContent(File file) throws IOException {
		if (file.isFile()) {
			if (FilenameUtils.getExtension(file.getPath()).equals("xml")) {
				return FileUtils.readFileToString(file, "ISO-8859-1");
			}else{
				return FileUtils.readFileToString(file);
			}
		}
		return "";
	}

	/**
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String readFile(File file) throws FileNotFoundException {
		// System.out.println("Reading from file.");
		StringBuilder text = new StringBuilder();
		String NL = System.getProperty("line.separator");
		Scanner scanner = null;
		if (FilenameUtils.getExtension(file.getPath()).equals("xml")) {
			scanner = new Scanner(new FileInputStream(file.getAbsolutePath()), "ISO-8859-1");
		} else {
			scanner = new Scanner(new FileInputStream(file.getAbsolutePath()), "UTF-8");
		}
		try {
			while (scanner.hasNextLine()) {
				text.append(scanner.nextLine() + NL);
			}
		} finally {
			scanner.close();
		}
		return text.toString();
	}

	/**
	 * @param content
	 * @param patt
	 * @param caseSensitive
	 * @return
	 */
	public static int countPattern(String content, String patt, boolean caseSensitive) {
		Pattern pattern = caseSensitive ? Pattern.compile(patt) : Pattern.compile(patt, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(content);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		return count;
	}

	/**
	 * @param content
	 * @param patt
	 * @param group
	 * @return
	 */
	public static String getPatternFirst(String content, String patt, Integer group) {
		return getPattern(content, patt, group, EnumPatternPosition.FIRST);
	}

	public static String getPatternLast(String content, String patt, Integer group) {
		return getPattern(content, patt, group, EnumPatternPosition.LAST);
	}

	public static String getPattern(String content, String patt, Integer group, EnumPatternPosition pattPosition) {
		String retorno = "";
		Pattern pattern = Pattern.compile(patt, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			if (pattPosition.getDescription().equals(EnumPatternPosition.FIRST.getDescription())) {
				if (group != null) {
					return matcher.group(group);
				} else {
					return matcher.group();
				}
			} else if (pattPosition.getDescription().equals(EnumPatternPosition.LAST.getDescription())) {
				if (group != null) {
					retorno = matcher.group(group);
				} else {
					retorno = matcher.group();
				}
			} else if (pattPosition.getDescription().equals(EnumPatternPosition.ANY_WHERE.getDescription())) {
				if (group != null) {
					retorno += matcher.group(group) + ";";
				} else {
					retorno += matcher.group() + ";";
				}
			}
		}
		return retorno;
	}
}