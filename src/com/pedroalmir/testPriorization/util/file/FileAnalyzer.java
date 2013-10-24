package com.pedroalmir.testPriorization.util.file;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pedroalmir.testPriorization.util.enums.EnumPatternPosition;


public class FileAnalyzer {
        
        public static String getFileContent(File file) throws IOException{
                String line = "";
                Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
                line += scanner.nextLine() + "\n";
        }
        return line;
        }
        
        public static int countPattern(String content, String patt, boolean caseSensitive){
                Pattern pattern; 
                pattern = caseSensitive ? Pattern.compile(patt) : Pattern.compile(patt, Pattern.CASE_INSENSITIVE);
        Matcher  matcher = pattern.matcher(content);
        int count = 0;
        while (matcher.find()){
                count++;
        }
        return count;
        }
        
        public static String getPatternFirst(String content, String patt, Integer group){
                return getPattern(content, patt, group, EnumPatternPosition.FIRST);
        }
        
        public static String getPatternLast(String content, String patt, Integer group){
                return getPattern(content, patt, group, EnumPatternPosition.LAST);
        }
        
        public static String getPattern(String content, String patt, Integer group, EnumPatternPosition pattPosition){
                String retorno = ""; 
                Pattern pattern = Pattern.compile(patt, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                    if(pattPosition.getDescription().equals(EnumPatternPosition.FIRST.getDescription())){
                            if(group != null){
                                    return matcher.group(group);
                            }else{
                                    return matcher.group();
                            }
                    }else if(pattPosition.getDescription().equals(EnumPatternPosition.LAST.getDescription())){
                            if(group != null){
                                    retorno = matcher.group(group);
                            }else{
                                    retorno = matcher.group();
                            }
                    }else if(pattPosition.getDescription().equals(EnumPatternPosition.ANY_WHERE.getDescription())){
                            if(group != null){
                                    retorno += matcher.group(group) + ";";
                            }else{
                                    retorno += matcher.group() + ";";
                            }
                    }
            }
            return retorno;
        }
}