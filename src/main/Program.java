package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import com.opencsv.CSVReader;

import data.ChangedFile;
import data.CommitFile;
import data.MergedFileInfo;
import input.CSVHelper;
import input.FileFinder;
import output.PreprocessOutput;
import output.SmellCSV;
import processing.Evaluation;
import processing.EvaluationSize;
import processing.Preprocessing;

public class Program {

	// TODO: argumente einsetzen
	private static String csvPath = "/home/hnes/Masterarbeit/Repositories/httpd/revisionsFull.csv";
	private static String smellDir = "/home/hnes/Masterarbeit/Results/httpd/";
	private static String tempPath = "/home/hnes/Masterarbeit/Temp/";
	private static String resultsDir = "/home/hnes/Masterarbeit/Results/";
	private static String project = "httpd";
	private static int smellThreshold = 0;
	private static double percentile = 0.7;	// prozentualer Anteil der notenbesten Smells die genommen werden
	private static double sizePercentile = 0.25;
	private static int lofcThresh = 1112;
	private static int nofcThresh = 56;
	private static String smellModeStr = "";
	
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		analyzeInput(args);
		
		/* PREPROCESSING of the Smell Data */
	
		smellModeStr = "AB";
		Preprocessing.preprocessData(csvPath);
		smellModeStr = "AF";
		Preprocessing.preprocessData(csvPath);
		smellModeStr = "LF";
		Preprocessing.preprocessData(csvPath);
	
		
		/* PREPROCESSING of the Project Data */
		
		CSVHelper csvReader = new CSVHelper();
		csvReader.processFile(csvPath);
		csvReader.processFileSingle(csvPath);
		TreeMap<ChangedFile, String> bugMap = csvReader.getBugFiles();
		TreeMap<ChangedFile, String> changedMap = csvReader.getChangedFiles();		
		TreeMap<ChangedFile, String> bugMapSingle = csvReader.getBugFilesSingle();
		TreeMap<ChangedFile, String> changedMapSingle = csvReader.getChangedFilesSingle();
		
		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//		System.out.println("BUGMAP AUSGABE");
//		for(ChangedFile chFile : bugMap.keySet()){
//			System.out.println(chFile.getHash() + " - " + chFile.getDate() + " - " + chFile.getFile());
//		}
//		System.out.println("BUGMAP AUSGABE ENDE");
//		System.out.println("BUGMAPSINGLE AUSGABE");
//		for(ChangedFile chFile : bugMapSingle.keySet()){
//			System.out.println(chFile.getHash() + " - " + chFile.getDate() + " - " + chFile.getFile());
//		}
//		System.out.println("BUGMAPSINGLE AUSGABE ENDE");
		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		File projectInfo = new File(resultsDir + project + "/projectInfo.csv");
		ArrayList<Date> versionDates = new ArrayList<Date>();
		versionDates = CSVHelper.getProjectDates(projectInfo);
		
		Date startDate = versionDates.get(0);
		
		for(Date curDate : versionDates){
			if(startDate.equals(curDate)){
				continue;
			}
			ArrayList<MergedFileInfo> outputList = new ArrayList<MergedFileInfo>();
			
			// Vorbereitung für MergedFileInfos
			HashMap<String, Integer> curBugSet = Preprocessing.getCurFiles(bugMap, startDate, curDate);
			HashMap<String, Integer> curChangedSet = Preprocessing.getCurFiles(changedMap, startDate, curDate);
			
			// Vorbereitung für Proportion Test
			HashMap<ChangedFile, String> curBugMap = Preprocessing.getCurMap(bugMap, startDate, curDate);
			
						
			// TODO: Vorbereitung für einzelne Commits (nur für die einzelnen Ratios pro Commit nötig... wahrscheinlich wieder zu entfernen)
			HashMap<String, Integer> curBugSetSingle = Preprocessing.getCurFiles(bugMapSingle, startDate, curDate);
			HashMap<String, Integer> curChangedSetSingle = Preprocessing.getCurFiles(changedMapSingle, startDate, curDate);
			////////////////////////////////////////////////
			
			HashSet<String> smellABSet = CSVHelper.getSmells(startDate, "AB");
			HashSet<String> smellAFSet = CSVHelper.getSmells(startDate, "AF");
			HashSet<String> smellLFSet = CSVHelper.getSmells(startDate, "LF");
			
			HashSet<String> curVersionSmellyFiles = new HashSet<String>();
			
			HashSet<String> fileSet = CSVHelper.getVersionFiles(startDate);
			for(String s : fileSet){
				MergedFileInfo fileInfo = new MergedFileInfo(s, startDate);
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				String dateStr = formatter.format(startDate);
				
				String filePath = tempPath + project + "/" + dateStr + "/_cppstats/" + s;
				System.out.println("FILE DEBUG: " + filePath);
				File tempFile = new File(filePath);
				fileInfo.setFileSize(tempFile.length());
				
				if(curBugSet.containsKey(s)){
					fileInfo.sethasFixed();
					fileInfo.setFixCount(curBugSet.get(s));
				}
				
				if(curChangedSet.containsKey(s)){
					fileInfo.sethasChanged();
					fileInfo.setChangeCount(curChangedSet.get(s));
				}
				
				if(smellABSet.contains(s)){
					curVersionSmellyFiles.add(s);
					fileInfo.setSmellAB();
				}
				
				if(smellAFSet.contains(s)){
					curVersionSmellyFiles.add(s);
					fileInfo.setSmellAF();
				}
				
				if(smellLFSet.contains(s)){
					curVersionSmellyFiles.add(s);
					fileInfo.setSmellLF();
				}
				
				outputList.add(fileInfo);
			}
			
			// Output
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = formatter.format(startDate);
			
							
			// TODO:  (nur für die einzelnen Ratios pro Commit nötig... wahrscheinlich wieder zu entfernen)
			String path = Program.getResultsDir() + Program.getProject() + "/CorrelatedRatio/";
			File mkDir = new File(path);
			mkDir.mkdirs();
			File csvOut = new File(path + dateStr +"_ratio.csv");
			BufferedWriter buffW = null;
			int smellyFixAmount = 0;
			int nonSmellyFixAmount = 0;
			
			/* --------------------------------------------------------------------- */
			// Aktuelle Commitfile Map in Liste umwandeln
			ArrayList<CommitFile> listOfBugcommits = new ArrayList<CommitFile>();
			for(ChangedFile chFile : curBugMap.keySet()){
				String fileName = chFile.getFile();
				boolean smelly;
				if(curVersionSmellyFiles.contains(fileName)){
					smelly = true;
				}else{
					smelly = false;
				}
				CommitFile comFile = new CommitFile(fileName, smelly);
				listOfBugcommits.add(comFile);
			}
			int curSnapshotSizeDebug = curBugMap.size();
			int curSnapshotSize = listOfBugcommits.size();
			System.out.println("DEBUG SNAPSHOT SIZE: " + curSnapshotSizeDebug + " - " + curSnapshotSize);
			
			if(curSnapshotSize <= 1){
				smellyFixAmount = 0;
				nonSmellyFixAmount = 0;	
			}else{
				int i = 0;
				while(i <= 40){
					int randomNum = randInt(0, curSnapshotSize-1);
					System.out.println(listOfBugcommits.get(randomNum).getFile());
					if(fileSet.contains(listOfBugcommits.get(randomNum).getFile())){
						if(listOfBugcommits.get(randomNum).getSmelly()){
							smellyFixAmount++;
						}else{
							nonSmellyFixAmount++;
						}
						i++;
					}	
				}
			}
			
			
			/* --------------------------------------------------------------------- */
			
			/* ---------------------------------------------------------------------
			// Commits von 0 bis 99 durchgehen
			for(int i = 0; i<100; i++){
				int smellyFix = 0;
				int nonSmellyFix = 0;
				
				// Die Commit Files durchgehen 
				for(String fixedFile : curBugSetSingle.keySet()){
					if(i < 10){
						if(fixedFile.contains("0"+i)){		// wenn der aktuelle Commit in der Datei steht
			//				System.out.println("FixedFile: "+fixedFile);
							String compFile = fixedFile.substring(0, fixedFile.length()- 2);	// die Commitnummer aus dem File löschen
			//				System.out.println("CompFile: "+ compFile);
							if(curVersionSmellyFiles.contains(compFile)){		// wenn das gefixte File in den Smelly Files steht
								smellyFix++;// gefixte smellyFiles hochzählen
								smellyFixAmount++;
							}else{												// sonst
								nonSmellyFix++;									// fixes in nonSmellyFiles hochzählen
								nonSmellyFixAmount++;
							}
						}
					}else{
						if(fixedFile.contains(String.valueOf(i))){
							String compFile = fixedFile.substring(0, fixedFile.length()-2);
							if(curVersionSmellyFiles.contains(compFile)){
								smellyFix++;
								smellyFixAmount++;
							}else{
								nonSmellyFix++;
								nonSmellyFixAmount++;
							}
						}
					}
				}
								
				try {
					buffW = new BufferedWriter(new FileWriter( csvOut, true ));
					buffW.write(smellyFix +","+ nonSmellyFix);
					buffW.newLine();
					buffW.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			---------------------------------------------------------------- */
			try {
				buffW = new BufferedWriter(new FileWriter( csvOut, true ));
				buffW.write(smellyFixAmount +","+ nonSmellyFixAmount +","+ curSnapshotSize);
				buffW.newLine();
				buffW.flush();
				buffW.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO: ENDE von (nur für die einzelnen Ratios pro Commit nötig... wahrscheinlich wieder zu entfernen)
			
			
			PreprocessOutput.writeCSV(outputList, dateStr);
			
			
			startDate = curDate;
		}

		
		
		
		// Evaluierung
		String path = Program.getResultsDir() + Program.getProject() + "/Correlated/../corOverview.csv";
		File csvOut = new File(path);
		BufferedWriter buff = null;
		try {
			buff = new BufferedWriter(new FileWriter( csvOut, true ));
			buff.write("Version Date, sABC, sAFC, sLFC,  "
					+ "SF, SNF, NSF, NSNF, smellAmount, nSmellAmount, sFixCount, nsFixCount, schnitt smSize, schnitt nsSize, "
					+ "AB_SF,AB_SNF,AB_NSF,AB_NSNF,AB_smellAmount, AB_nSmellAmount, AB_sFixCount, AB_nsFixCount, AB_schnitt smSize, AB_schnitt nsSize, "
					+ "AF_SF,AF_SNF,AF_NSF,AF_NSNF, AF_smellAmount, AF_nSmellAmount, AF_sFixCount, AF_nsFixCount, AF_schnitt smSize, AF_schnitt nsSize, "
					+ "LF_SF,LF_SNF,LF_NSF,LF_NSNF, LF_smellAmount, LF_nSmellAmount, LF_sFixCount, LF_nsFixCount, LF_schnitt smSize, LF_schnitt nsSize,"
					+ "ABAF_SF,ABAF_SNF,ABAF_NSF,ABAF_NSNF,ABAF_smellAmount, ABAF_nSmellAmount, ABAF_sFixCount, ABAF_nsFixCount, ABAF_schnitt smSize, ABAF_schnitt nsSize");
			buff.newLine();
			buff.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Evaluierung mit Size Komponente
		String pathSize = Program.getResultsDir() + Program.getProject() + "/Correlated/../corOverviewSize.csv";
		File csvOutSize = new File(pathSize);
		BufferedWriter buffSize = null;
		try {
			buffSize = new BufferedWriter(new FileWriter( csvOutSize, true ));
			buffSize.write("Version Date, sABC, sAFC, sLFC,  "
					+ "SF, SNF, NSF, NSNF, smellAmount, nSmellAmount, sFixCount, nsFixCount, schnitt smSize, schnitt nsSize, "
					+ "AB_SF,AB_SNF,AB_NSF,AB_NSNF,AB_smellAmount, AB_nSmellAmount, AB_sFixCount, AB_nsFixCount, AB_schnitt smSize, AB_schnitt nsSize, "
					+ "AF_SF,AF_SNF,AF_NSF,AF_NSNF, AF_smellAmount, AF_nSmellAmount, AF_sFixCount, AF_nsFixCount, AF_schnitt smSize, AF_schnitt nsSize, "
					+ "LF_SF,LF_SNF,LF_NSF,LF_NSNF, LF_smellAmount, LF_nSmellAmount, LF_sFixCount, LF_nsFixCount, LF_schnitt smSize, LF_schnitt nsSize,"
					+ "ABAF_SF,ABAF_SNF,ABAF_NSF,ABAF_NSNF,ABAF_smellAmount, ABAF_nSmellAmount, ABAF_sFixCount, ABAF_nsFixCount, ABAF_schnitt smSize, ABAF_schnitt nsSize");
			buffSize.newLine();
			buffSize.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String pathFind = Program.getResultsDir() + Program.getProject() + "/Correlated/";
		System.out.println( "Suche im Pfad: " + pathFind );
	  	List<File> filesFind = FileFinder.find( pathFind, "(.*\\.csv$)" );
		
	  	Collections.sort(filesFind);
	  	
	  	for(File f : filesFind){
	  		Evaluation.evalFile(f);
	  		
	  		//TODO: Size Threshold berechnen und dann evalFile mit diesem aufrufen...
	  		ArrayList<Double> sizeList = new ArrayList<Double>();
	  		try {
				CSVReader reader = new CSVReader(new FileReader(f));
				String[] nextLine;
				reader.readNext(); //erste Zeile überspringen
				while ((nextLine = reader.readNext()) != null) {
					
					double fileSize = Double.parseDouble(nextLine[10]);								
					sizeList.add(fileSize);
																	
				}
			} catch (IOException e1) {
				System.out.println("Fehler beim lesen/schreiben der Datei!");
				e1.printStackTrace();
			}
	  		
	  		double percSum = sizePercentile * (double) sizeList.size();
			double totalSum = 0;
			double tempThresh = 0;
			
		    Collections.sort(sizeList);
		    Collections.reverse(sizeList);
			
		    for(double temp:sizeList){
		    	totalSum++;
		    	if(totalSum >= percSum){
		    		tempThresh = temp;
		    		break;
		    	}
		    }
		    
		    EvaluationSize.evalFileSize(f, tempThresh);
	  	}
	}
	
	/** 
	 * Random Int
	 */
	public static int randInt(int min, int max){
		Random rand = new Random();
		int randomNum = 0;
		
		randomNum = rand.nextInt((max - min) + 1) + min;
		
		return randomNum;
	}
	
	/**
	 * Getter for Smell Directory
	 * @return String of Smell Directory
	 */
	public static String getSmellDir(){
		return smellDir;
	}
	
	/**
	 * Getter for Results Directory
	 * @return String of Smell Directory
	 */
	public static String getResultsDir(){
		return resultsDir;
	}
	
	/**
	 * Getter for Project Name
	 * @return String of Smell Directory
	 */
	public static String getProject(){
		return project;
	}
	
	/**
	 * Getter for Smell Mode
	 * @return String of Smell Mode
	 */
	public static String getSmellMode(){
		return smellModeStr;
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getThreshold(){
		return smellThreshold;
	}
	
	public static double getPercentile(){
		return percentile;
	}
	
	public static int getlofcThresh(){
		return lofcThresh;
	}
	public static int getnofcThresh(){
		return nofcThresh;
	}
	
	/**
	 * Analyze input to decide what to do during runtime
	 *
	 * @param args the input arguments
	 * @return true, if input is correct
	 */
	private static boolean analyzeInput(String[] args){
		// for easier handling, transform to list
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].contains("--"))
				args [i] = args[i].toLowerCase();
		}
		
		List<String> input = Arrays.asList(args);
		
		if(input.contains("--preprocessing"))
		{
			//programMode = "preprocessing";
		}
		if(input.contains("--evaluate")){
			//programMode = "evaluate";
		}
		
		
		return true;
		
	}
}
