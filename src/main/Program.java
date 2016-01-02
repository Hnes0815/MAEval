package main;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import data.ChangedFile;
import input.CSVHelper;
import input.FileFinder;

public class Program {

	// TODO: argumente einsetzen
	private static String csvPath = "/home/hnes/Masterarbeit/Repositories/openvpn/revisionsFull.csv";
	private static String smellDir = "/home/hnes/Masterarbeit/Results/AnnotationBundle/openvpn/ABRes";
	private static int smellThreshold = 0;
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		CSVHelper csvReader = new CSVHelper();
		
		csvReader.processFile(csvPath);
		TreeMap<ChangedFile, String> bugMap = csvReader.getBugFiles();
		TreeMap<ChangedFile, String> changedMap = csvReader.getChangedFiles();
		
		//// TODO: automatisieren...
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		Date prevDate = null;
		Date curDate = null;
		try {
			prevDate = formatter.parse("2006-01-07");
			curDate = formatter.parse("2008-11-20");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		////
		
		Date startDate;
		Date endDate;
		
		// Anzahl der .csv Dateien checken
  	    String pathFind = smellDir;
  	    System.out.println( "Suche im Pfad: " + pathFind );
  	    List<File> filesFind = FileFinder.find( pathFind, "(.*\\.csv$)" );
    
  	    int filesCount = filesFind.size();
  	    System.out.printf( "Fand %d Datei%s.%n",
            filesCount, filesCount == 1 ? "" : "en" );
  	    
  	    Collections.sort(filesFind);
  	    
  	    // Erstes File nehmen und daraus das erste Datum ableiten
  	    File startDateFile = filesFind.get(0);
  	    startDate = getDateFromFileName(startDateFile);
		
		for(File f : filesFind){
			String filePath = f.getAbsolutePath();
			endDate = getDateFromFileName(f);				
			
			HashSet<String> smellyFileSet = csvReader.getSmellsFromFileAB(filePath, smellThreshold);
			
			System.out.println(filePath);
			for(String curFile : smellyFileSet){
				int fileIdx = curFile.lastIndexOf("locations/") + "locations/".length();
				int lastdotIdx = curFile.lastIndexOf(".");
	    		String curFileStr = curFile.substring(fileIdx, lastdotIdx);
				System.out.println(curFileStr);
			}
		}
  	    
		/* Lädt alle gebugfixten Dateien zwischen zwei Daten in ein Set */
		HashSet<String> curBugSet = getCurBugfixes(bugMap, prevDate, curDate);
		
		/* 
		 * Lädt alle geänderten Dateien zwischen zwei Daten in ein Set ... 
		 * funktioniert mit gleicher Funktion nur andere Map rein
		 */
		HashSet<String>	curChangedSet = getCurBugfixes(changedMap, prevDate, curDate);	
	      
	      
	    // kleines Debugging
	    //for(String curStr : curBugSet){
	    //	System.out.println("File: " + curStr);
	    //}
	}
	
	/**
	 * Gets the Date from Filename with Format "yyyy-MM-dd"
	 * 
	 * @param f File
	 * @return Date
	 */
	private static Date getDateFromFileName(File f){
		Date retDate = null;
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		String filePath = f.getAbsolutePath();
		int slashIdx = filePath.lastIndexOf("/") + 1;
		int pointIdx = filePath.lastIndexOf(".");
		String curDateStr = filePath.substring(slashIdx, pointIdx);
		try {
			retDate = formatter.parse(curDateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retDate;
	}
	
	/**
	 * Lädt alle gebugfixten Dateien zwischen zwei Daten in ein Set
	 * 
	 * @param bugMap Map mit allen Bugfixes
	 * @param startDate 
	 * @param endDate
	 * @return alle Bugfixes zwischen StartDate und EndDate
	 */
	private static HashSet<String> getCurBugfixes(TreeMap<ChangedFile, String> bugMap, Date startDate, Date endDate){
		HashSet<String> curBugSet = new HashSet<String>();
	      for(ChangedFile keySec : bugMap.keySet())
		    {
			  if(startDate.after(keySec.getDate())) continue;
			  if(endDate.before(keySec.getDate())) break;
		      //System.out.print("Key: " + keySec.getDate() + " - ");
		      //System.out.print("Value: " + bugMap.get(keySec) + "\n");
		      
		      curBugSet.add(bugMap.get(keySec));
		    }
	      
	    return curBugSet;
	}
	
	/**
	 * Analyze input to decide what to do during runtime
	 *
	 * @param args the input arguments
	 * @return true, if input is correct
	 */
	private static boolean analyzeInput(String[] args){
		return false;
		
	}
}
