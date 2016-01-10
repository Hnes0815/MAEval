package main;

import java.io.File;
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
import input.CSVHelper;
import input.FileFinder;
import output.SmellCSV;
import processing.Preprocessing;

public class Program {

	// TODO: argumente einsetzen
	private static String csvPath = "/home/hnes/Masterarbeit/Repositories/openvpn/revisionsFull.csv";
	private static String smellDir = "/home/hnes/Masterarbeit/Results/openvpn/ABRes";
	private static String resultsDir = "/home/hnes/Masterarbeit/Results/";
	private static String project = "openvpn";
	private static int smellThreshold = 0;
	
	private static String smellModeStr = "";
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		analyzeInput(args);
		
		smellModeStr = "AB";
		Preprocessing.preprocessData(csvPath);
		smellModeStr = "AF";
		Preprocessing.preprocessData(csvPath);
		smellModeStr = "LF";
		Preprocessing.preprocessData(csvPath);
		

		
		// TODO : Die eben erstellte CSV Datei mit den gesammelten Smells muss 
		//			mit den Bugfixes korreliert werden
		// SmellFixEval.correlateSmellFixes(smellCSV, fixCSV);
		File projectInfo = new File(resultsDir + project + "/projectInfo.csv");
		ArrayList<String> versionDates = new ArrayList<String>();
		versionDates = CSVHelper.getProjectDates(projectInfo);
		
	    // TODO: Liste durchgehen, pro File ein "MergedFileInfo" Objekt erstellen und zum schluss schreiben
		
		/* Lädt alle gebugfixten Dateien zwischen zwei Daten in ein Set */
		//HashSet<String> curBugSet = getCurFiles(bugMap, prevDate, curDate);
		
		/* 
		 * Lädt alle geänderten Dateien zwischen zwei Daten in ein Set ... 
		 * funktioniert mit gleicher Funktion nur andere Map rein
		 */
		//HashSet<String>	curChangedSet = getCurFiles(changedMap, prevDate, curDate);	
	      
	      
	    // kleines Debugging
	    //for(String curStr : curChangedSet){
	    //	System.out.println("File: " + curStr);
	    //}
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
		
		
		
		
		return true;
		
	}
}
