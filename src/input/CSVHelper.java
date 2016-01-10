package input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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
		
		HashSet<String> smellSet = new HashSet<String>();
		
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
								
				if(smellScore >= smellThreshold)
					smellSet.add(curFileStr);
			}
		} catch (IOException e1) {
			System.out.println("Fehler beim lesen/schreiben der Datei!");
			e1.printStackTrace();
		}
		
		return smellSet;
	}
	
	/**
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
	 * 
	 * @param curFile
	 * @param smellThreshold
	 * @return
	 */
	public static TreeMap<String, Double> getFeatureMap(String curFile, int smellThreshold){
		String csvFile = curFile;
		TreeMap<String, Double> featMap = new TreeMap<String, Double>();
		
		try {
			CSVReader reader = new CSVReader(new FileReader(csvFile));
			String[] nextLine;
			reader.readNext(); //erste Zeile überspringen
			while ((nextLine = reader.readNext()) != null) {
				String featName = nextLine[0];	
				double smellScore = Double.parseDouble(nextLine[1]);
								
				if(smellScore >= smellThreshold)
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
	 * @param projectInfo
	 * @return
	 */
	public static ArrayList<String> getProjectDates(File projectInfo){
		String filePath = projectInfo.getAbsolutePath();
		ArrayList<String> resultList = new ArrayList<String>();
		
		try {
			CSVReader reader = new CSVReader(new FileReader(filePath));
			String[] nextLine;
			reader.readNext(); //erste Zeile überspringen
			while ((nextLine = reader.readNext()) != null) {
				String dateStr = nextLine[1];	
				
				resultList.add(dateStr);
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
}

