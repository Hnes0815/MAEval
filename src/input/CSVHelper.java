package input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.TreeMap;

import com.opencsv.CSVReader;

import data.ChangedFile;
import main.Program;


public class CSVHelper {

	private String detectionPath;
	// Liste für geänderte Dateien
	private TreeMap<ChangedFile, String> changedFiles = new TreeMap<ChangedFile, String>();
	// Liste für jeden x-ten Bugfix Commit
	private TreeMap<ChangedFile, String> bugFiles = new TreeMap<ChangedFile, String>();
	
	// Liste für geänderte Dateien
	private TreeMap<ChangedFile, String> changedFilesSingle = new TreeMap<ChangedFile, String>();
	// Liste für jeden x-ten Bugfix Commit
	private TreeMap<ChangedFile, String> bugFilesSingle = new TreeMap<ChangedFile, String>();
	private double percentile = Program.getPercentile();
	
	/**
	 * Instantiates a new CSVReader
	 *
	 * @param detPath the path of the bugdetection csv
	 */
	public CSVHelper(){
		
	}
	
	/**
	 * 
	 * @param curFile
	 * @param smellThreshold
	 * @return
	 */
	public HashSet<String> getSmellsFromFile(String curFile, int smellThreshold){
		String csvFile = curFile;
		String smellModeStr = Program.getSmellMode();
		double totalSmells = 0;
		HashSet<String> smellSet = new HashSet<String>();
		ArrayList<Double> scoreList = new ArrayList<Double>();
		TreeMap<String, Double> tempMap = new TreeMap<String, Double>();
		try {
			CSVReader reader = new CSVReader(new FileReader(csvFile));
			String[] nextLine;
			reader.readNext(); //erste Zeile überspringen
			while ((nextLine = reader.readNext()) != null) {
				String fileName = nextLine[0];
				//int startingLine = Integer.parseInt(nextLine[1]);
				//String methodName = nextLine[2];
				double smellScore;
				if(smellModeStr.equals("AB")){
					smellScore = Double.parseDouble(nextLine[3]);
				}else{
					smellScore = Double.parseDouble(nextLine[1]);
				}
				//filename muss noch beschnitten werden
				int fileIdx = fileName.lastIndexOf("locations/") + "locations/".length();
				int lastdotIdx = fileName.lastIndexOf(".");
	    		String curFileStr = fileName.substring(fileIdx, lastdotIdx);
								
				if(smellScore >= smellThreshold){
					
					//smellSet.add(curFileStr);
					totalSmells++;
					if(!tempMap.containsKey(curFileStr)){
						tempMap.put(curFileStr, smellScore);
						scoreList.add(smellScore);
					}
				}
					
			}
		} catch (IOException e1) {
			System.out.println("Fehler beim lesen/schreiben der Datei!");
			e1.printStackTrace();
		}
		
		double percSum = percentile * (double) tempMap.size();
		double totalSum = 0;
		double tempThresh = 0;
		
	    Collections.sort(scoreList);
	    Collections.reverse(scoreList);
		
	    for(double temp:scoreList){
	    	totalSum++;
	    	if(totalSum >= percSum){
	    		tempThresh = temp;
	    		break;
	    	}
	    }
	    
		for(String file : tempMap.keySet()){
			if(smellModeStr.equals("LF")){
				smellSet.add(file);
			}
			if(tempMap.get(file) >= tempThresh){
				
				smellSet.add(file);
			}
		}
		return smellSet;
	}
	
	/**
	 * Nimmt die ursprüngliche CSV-Datei von MetricMiner2 und erstellt die Listen 
	 * der Bugfixes und geänderten Dateien mit ihren Änderungsdaten
	 * 
	 * @param csvString
	 */
	public void processFile(String csvString){
		String csvFile = csvString;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

			    // use comma as separator
				String[] commit = line.split(cvsSplitBy);
				String curHash = commit[0];
				boolean bugfixCommit = Boolean.parseBoolean(commit[1]);
				int bugfixCount = Integer.parseInt(commit[8]);
				String strDate = commit[7];
				String fileName = commit[3];
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		        Date dateStr;
		        Date comDate = null;
				try {
					dateStr = formatter.parse(strDate);
					String formattedDate = formatter.format(dateStr);
			        //System.out.println("yyyy-MM-dd date is ==>"+formattedDate);
			        comDate = formatter.parse(formattedDate);
				} catch (ParseException e) {
					System.out.println("Datum konnte nicht korrekt eingelesen werden!");
					e.printStackTrace();
				}
		        
				
				//System.out.println("Hash: " + curHash 
	            //                     + " , isBugfix: " + bugfixCommit 
	            //                     + " , Datum: " + comDate);

				ChangedFile chFile = new ChangedFile(fileName, curHash, comDate);
				
				changedFiles.put(chFile, fileName);
				if(bugfixCommit)
					bugFiles.put(chFile, fileName);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	} 
	
	/**
	 * Nimmt die ursprüngliche CSV-Datei von MetricMiner2 und erstellt die Listen 
	 * der Bugfixes und geänderten Dateien mit ihren Änderungsdaten
	 * 
	 * @param csvString
	 */
	public void processFileSingle(String csvString){
		String csvFile = csvString;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		
		
		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				
				
			    // use comma as separator
				String[] commit = line.split(cvsSplitBy);
				String curHash = commit[0];
				boolean bugfixCommit = Boolean.parseBoolean(commit[1]);
				int bugfixCount = (Integer.parseInt(commit[8]) -1) % 100;
				String strDate = commit[7];
				String fileName = null;
				if(bugfixCount < 10){
					fileName = commit[3] + "0" +bugfixCount;
				}else{
					fileName = commit[3] + bugfixCount;
				}
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		        Date dateStr;
		        Date comDate = null;
				try {
					dateStr = formatter.parse(strDate);
					String formattedDate = formatter.format(dateStr);
			        //System.out.println("yyyy-MM-dd date is ==>"+formattedDate);
			        comDate = formatter.parse(formattedDate);
				} catch (ParseException e) {
					System.out.println("Datum konnte nicht korrekt eingelesen werden!");
					e.printStackTrace();
				}
		        
				
				//System.out.println("Hash: " + curHash 
	            //                     + " , isBugfix: " + bugfixCommit 
	            //                     + " , Datum: " + comDate);

				ChangedFile chFile = new ChangedFile(fileName, curHash, comDate);
				
				changedFilesSingle.put(chFile, fileName);
				if(bugfixCommit)
					bugFilesSingle.put(chFile, fileName);
				
				
				
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	} 
	
	
	/**
	 * 
	 * @param curFile
	 * @param smellThreshold
	 * @return
	 */
	public static TreeMap<String, Double> getFeatureMap(String curFile, int smellThreshold){
		String csvFile = curFile;
		TreeMap<String, Double> featMap = new TreeMap<String, Double>();
		
		int lofcThresh = Program.getlofcThresh();
		int nofcThresh = Program.getnofcThresh();
		
		try {
			CSVReader reader = new CSVReader(new FileReader(csvFile));
			String[] nextLine;
			reader.readNext(); //erste Zeile überspringen
			while ((nextLine = reader.readNext()) != null) {
				String featName = nextLine[0];	
				double smellScore = Double.parseDouble(nextLine[1]);
				int lofc = Integer.parseInt(nextLine[8]);
				int nofc = Integer.parseInt(nextLine[6]);
				
				if(lofc >= lofcThresh || nofc >= nofcThresh)
					featMap.put(featName, smellScore);
			}
		} catch (IOException e1) {
			System.out.println("Fehler beim lesen/schreiben der Datei!");
			e1.printStackTrace();
		}
		
		return featMap;
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	public static HashSet<String> getVersionFiles(Date date){
		HashSet<String> resultSet = new HashSet<String>();
		
		String resultsDir = Program.getResultsDir();
		String project = Program.getProject();
		
		try {
			CSVReader reader = new CSVReader(new FileReader(resultsDir + project + "/projectAnalysis.csv"));
			String[] nextLine;
			reader.readNext(); //erste Zeile überspringen
			while ((nextLine = reader.readNext()) != null) {
				String fileName = nextLine[0];	
				String dateStr = nextLine[1];
								
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				
				Date verDate = null;
				try {
					verDate = formatter.parse(dateStr);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
						
				if(verDate.equals(date))
					resultSet.add(fileName);
			}
		} catch (IOException e1) {
			System.out.println("Fehler beim lesen/schreiben der Datei!");
			e1.printStackTrace();
		}
		
		
		return resultSet;
	}
	
	public static HashSet<String> getSmells(Date date, String mode){
		HashSet<String> resultSet = new HashSet<String>();
		
		String resultsDir = Program.getResultsDir();
		String project = Program.getProject();
		
		try {
			CSVReader reader = null;
			if(mode.equals("AB")){
				reader = new CSVReader(new FileReader(resultsDir + project + "/" + mode + "smellOverview.csv"));
			}else if(mode.equals("AF")){
				reader = new CSVReader(new FileReader(resultsDir + project + "/" + mode + "smellOverview.csv"));
			}else if(mode.equals("LF")){
				reader = new CSVReader(new FileReader(resultsDir + project + "/" + mode + "smellOverview.csv"));
			}else{
				System.out.println("Something went wrong!");
			}
		    //reader = new CSVReader(new FileReader(resultsDir + project + "/projectAnalysis.csv"));
			String[] nextLine;
			reader.readNext(); //erste Zeile überspringen
			while ((nextLine = reader.readNext()) != null) {
				String fileName = nextLine[0];	
				String dateStr = nextLine[1];
					
				//System.out.println(dateStr);
				
				SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy" , Locale.ENGLISH);				
				
				Date verDate = null;
				try {
					verDate = formatter.parse(dateStr);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
						
				if(verDate.equals(date))
					resultSet.add(fileName);
			}
		} catch (IOException e1) {
			System.out.println("Fehler beim lesen/schreiben der Datei!");
			e1.printStackTrace();
		}
		
		
		return resultSet;
	}
	
	/**
	 * 
	 * @param projectInfo
	 * @return
	 */
	public static ArrayList<Date> getProjectDates(File projectInfo){
		String filePath = projectInfo.getAbsolutePath();
		ArrayList<Date> resultList = new ArrayList<Date>();
		
		try {
			CSVReader reader = new CSVReader(new FileReader(filePath));
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				String dateStr = nextLine[1];	
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				
				Date verDate = null;
				try {
					verDate = formatter.parse(dateStr);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				resultList.add(verDate);
			}
		} catch (IOException e1) {
			System.out.println("Fehler beim lesen/schreiben der Datei!");
			e1.printStackTrace();
		}
		
		return resultList;
	}
	
	/**
	 * Getter for changed Files
	 * @return
	 */
	public TreeMap<ChangedFile, String> getChangedFiles(){
		return changedFiles;
	}
	
	/**
	 * Getter for BugCommits
	 * @return
	 */
	public TreeMap<ChangedFile, String> getBugFiles(){
		return bugFiles;
	}
	
	/**
	 * Getter for changed Files
	 * @return
	 */
	public TreeMap<ChangedFile, String> getChangedFilesSingle(){
		return changedFilesSingle;
	}
	
	/**
	 * Getter for BugCommits
	 * @return
	 */
	public TreeMap<ChangedFile, String> getBugFilesSingle(){
		return bugFilesSingle;
	}
}

