package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import data.ChangedFile;
import data.MergedFileInfo;
import input.CSVHelper;
import input.FileFinder;
import output.PreprocessOutput;
import output.SmellCSV;
import processing.Evaluation;
import processing.Preprocessing;

public class Program {

	// TODO: argumente einsetzen
	private static String csvPath = "/home/hnes/Masterarbeit/Repositories/openvpn/revisionsFull.csv";
	private static String smellDir = "/home/hnes/Masterarbeit/Results/openvpn/ABRes";
	private static String resultsDir = "/home/hnes/Masterarbeit/Results/";
	private static String project = "openvpn";
	private static int smellThreshold = 0;
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
		TreeMap<ChangedFile, String> bugMap = csvReader.getBugFiles();
		TreeMap<ChangedFile, String> changedMap = csvReader.getChangedFiles();		
				
		File projectInfo = new File(resultsDir + project + "/projectInfo.csv");
		ArrayList<Date> versionDates = new ArrayList<Date>();
		versionDates = CSVHelper.getProjectDates(projectInfo);
		
		Date startDate = versionDates.get(0);
		
		for(Date curDate : versionDates){
			if(startDate.equals(curDate)){
				continue;
			}
			ArrayList<MergedFileInfo> outputList = new ArrayList<MergedFileInfo>();
			
			HashSet<String> curBugSet = Preprocessing.getCurFiles(bugMap, startDate, curDate);
			HashSet<String> curChangedSet = Preprocessing.getCurFiles(changedMap, startDate, curDate);
			
			HashSet<String> smellABSet = CSVHelper.getSmells(startDate, "AB");
			HashSet<String> smellAFSet = CSVHelper.getSmells(startDate, "AF");
			HashSet<String> smellLFSet = CSVHelper.getSmells(startDate, "LF");
			
			HashSet<String> fileSet = CSVHelper.getVersionFiles(startDate);
			for(String s : fileSet){
				MergedFileInfo fileInfo = new MergedFileInfo(s, startDate);
				if(curBugSet.contains(s))
					fileInfo.sethasFixed();
				
				if(curChangedSet.contains(s))
					fileInfo.sethasChanged();
				
				if(smellABSet.contains(s))
					fileInfo.setSmellAB();
				
				if(smellAFSet.contains(s))
					fileInfo.setSmellAF();
				
				if(smellLFSet.contains(s))
					fileInfo.setSmellLF();
				
				outputList.add(fileInfo);
			}
			
			// Output
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = formatter.format(startDate);
			PreprocessOutput.writeCSV(outputList, dateStr);
			
			
			startDate = curDate;
		}

		
		// Evaluierung
		String path = Program.getResultsDir() + Program.getProject() + "/Correlated/../corOverview.csv";
		File csvOut = new File(path);
		BufferedWriter buff = null;
		try {
			buff = new BufferedWriter(new FileWriter( csvOut, true ));
			buff.write("Version Date, SF, SNF, NSF, NSNF, SC, SNC, NSC, NSNC, ORF, ORC");
			buff.newLine();
			buff.close();
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
	  	}
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
