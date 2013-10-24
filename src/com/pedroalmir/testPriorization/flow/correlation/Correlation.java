package com.pedroalmir.testPriorization.flow.correlation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import au.com.bytecode.opencsv.CSVWriter;

public class Correlation {
	private final static String BASE_FOLDER = "C:/Users/Pedro Almir/Dropbox/UFPI/Mestrado/Priorização de Casos de Teste/Arquivos Base/Aleatorios/DadosAleatorios/";
    
    public static void main(String[] args) throws IOException {
            int contClass;
            
            //1000-20
            contClass = 1000;
            generetaFiles(contClass, contClass/50);
            
            //1000-100
            contClass = 1000;
            generetaFiles(contClass, contClass/10);
            
            //5000-500
            contClass = 5000;
            generetaFiles(contClass, contClass/10);
            
            //10000-200
            contClass = 1000;
            generetaFiles(contClass, contClass/50);
    }
    
    public static void generetaFiles(int contClass, int reqCount) throws IOException{
            generateSQFD(contClass, reqCount, 5, BASE_FOLDER + "SQFD-cl-"+contClass+"-req-"+reqCount+".csv");
            generateTest(contClass, reqCount, BASE_FOLDER + "TestCoverage-cl-"+contClass+"-req-"+reqCount+".csv", 5, 0.3, 1);
            generateCoupling(contClass, BASE_FOLDER + "Coupling-cl-"+contClass+".csv");
    }
    
    private static void generateCoupling(int numberOfClass, String addressFile) throws IOException {
            Random random = new Random();
            
            List<String[]> data = new ArrayList<String[]>();
            CSVWriter writer = new CSVWriter(new FileWriter(addressFile), ';');
            /* Header */
            String[] header = new String[numberOfClass];
            for(int i = 0; i < numberOfClass; i++){
                    header[i] = "Class["+(i+1)+"]";
            }
            data.add(header);
            /* Body */
            String[] body = new String[numberOfClass];
            for(int i = 0; i < numberOfClass; i++){
                    body[i] = random.nextInt(11) + "";
            }
            data.add(body);
            writer.writeAll(data);
            writer.close();
    }

    public static void generateSQFD(int numberOfClass, int numberOfRequirement, int countClient, String addressFile) throws IOException{
            Integer[] correlation = { 0, 0, 0, 0, 0, 1, 1, 1, 3, 3, 9 };
            Random random = new Random();
            String[] stringLine;
            List<String[]> data = new ArrayList<String[]>();
            
            CSVWriter writer = new CSVWriter(new FileWriter(addressFile), ';');
            
            //add 2 to generate client priority column and description
            numberOfClass += 1 + countClient;
            //add 1 to generate description
            numberOfRequirement++;
            
            //each line
            for(int req = 0; req < numberOfRequirement; req++){
                    stringLine = new String[numberOfClass];
                    //each column
                    for(int clas = 0; clas < numberOfClass; clas++){
                            //title of first row
                            if(req==0){
                                    if(clas==0){
                                            stringLine[clas] = "Requirement\\Class";
                                    }else if(clas < numberOfClass-countClient){
                                            stringLine[clas] = "Class["+clas+"]";
                                    }else{
                                            stringLine[clas] = "Client Priority";
                                    }
                            }else{
                                    //title of row
                                    if(clas==0){
                                            stringLine[clas] = "Requirement["+req+"]";
                                    }else if(clas < numberOfClass-countClient){
                                            stringLine[clas] = correlation[random.nextInt(correlation.length)].toString();
                                    }else{
                                            stringLine[clas] = new Integer(random.nextInt(11)).toString();
                                    }
                                    
                            }
                    }
                    data.add(stringLine);
            }
            
            writer.writeAll(data);
            writer.close();
    }
    
    public static void generateTest(int numberOfClass, int numberOfTests, String addressFile, double percentClassCoverageChance, double minCoverage, double maxCoverage) throws IOException{
            Random random = new Random();
            String[] stringLine;
            List<String[]> data = new ArrayList<String[]>();
            
            CSVWriter writer = new CSVWriter(new FileWriter(addressFile), ';');
            
            //add 2 to generate client priority column and description
            numberOfClass += 2;
            //add 1 to generate description
            numberOfTests++;
            
            double countCoverage, coverage;
            double minTemp = 1;
            double maxTemp = 4;
            
            //each line
            for(int test = 0; test < numberOfTests; test++){
                    stringLine = new String[numberOfClass];
                    countCoverage = 0;
                    //each column
                    for(int clas = 0; clas < numberOfClass; clas++){
                            //title of first row
                            if(test==0){
                                    if(clas==0){
                                            stringLine[clas] = "Test\\Class";
                                    }else if(clas < numberOfClass-1){
                                            stringLine[clas] = "Class["+clas+"]";
                                    }else{
                                            stringLine[clas] = "Temp";
                                    }
                            }else{
                                    //title of row
                                    if(clas==0){
                                            stringLine[clas] = "Test["+test+"]";
                                    }else if(clas < numberOfClass-1){
                                            //depending on the percentage, simulate coverage
                                            if(random.nextDouble() * 100 <= percentClassCoverageChance){
                                                    coverage = new Double(minCoverage + (maxCoverage - minCoverage) * random.nextDouble());
                                                    stringLine[clas] = String.format("%.4f", coverage);
                                                    countCoverage += coverage;
                                            }else{
                                                    stringLine[clas] = "0";
                                            }
                                    }else{
                                            stringLine[clas] = String.format("%.4f", (new Double(minTemp + (maxTemp - minTemp) * random.nextDouble())) * countCoverage );
                                    }
                            }
                    }
                    data.add(stringLine);
            }
            
            writer.writeAll(data);
            writer.close();
    }
}
