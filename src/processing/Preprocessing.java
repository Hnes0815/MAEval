package processing;

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
import main.Program;
import output.SmellCSV;

public class Preprocessing {

	
	public static void preprocessData(String csvPath){
		String resDir = Program.getResultsDir();
		String project = Program.getProject();
		String smellModeStr = Program.getSmellMode();
		String smellDir = resDir + project + "/" + smellModeStr + "Res";	
		int smellThreshold = Program.getThreshold();
		CSVHelper csvReader = new CSVHelper();
		
		csvReader.processFile(csvPath);
	//	TreeMap<ChangedFile, String> bugMap = csvReader.getBugFiles();
		TreeMap<ChangedFile, String> changedMap = csvReader.getChangedFiles();
		
		
		Date startDate;
		Date endDate;
		
		// Anzahl der .csv Dateien checken
  	    String pathFind = smellDir;
  	    System.out.println( "Suche im Pfad: " + pathFind );
  	    List<File> filesFind = FileFinder.find( pathFind, "(.*\\.csv$)" );
    
  	    // für LargeFeature Analyse müssen die FileLocations aus der XML in csv Form 
  		// umgewandelt werden - um die analyse dann einheitlich fortführen zu können
  		// Erste Spalte FileName - zweite Spalte SmellScore
  		if(smellModeStr.equals("LF")){
  			List<File> filesFindXML = FileFinder.find(pathFind, "(.*\\.xml$)");
  			
  			for(File csvFile : filesFind){
  				String xmlPath = csvFile.getAbsolutePath().substring(0, csvFile.getAbsolutePath().length()-3) + "xml";
  				File xmlFile = new File(xmlPath);
  				SmellCSV.processXMLFile(csvFile, xmlFile);	
  			}
  		}
  	    
  	    int filesCount = filesFind.size();
  	    System.out.printf( "Fand %d Datei%s.%n",
            filesCount, filesCount == 1 ? "" : "en" );
  	    
  	    Collections.sort(filesFind);
  	    
  	    // Erstes File nehmen und daraus das erste Datum ableiten
  	    File startDateFile = filesFind.get(0);
  	    String prevFilePath = startDateFile.getAbsolutePath();
  	    startDate = getDateFromFileName(startDateFile);
		HashSet<String> prevSmellyFileSet = csvReader.getSmellsFromFile(prevFilePath, smellThreshold);
  	    
		for(File f : filesFind){
			String filePath = f.getAbsolutePath();
			endDate = getDateFromFileName(f);				
			
			HashSet<String> smellyFileSet = csvReader.getSmellsFromFile(filePath, smellThreshold);
			
			System.out.println(filePath);
			for(String curFile : smellyFileSet){
				System.out.println(curFile);
			}
			
			if(startDate.equals(endDate)){
				// Wenn Anfangs und Enddatum gleich (nur beim ersten mal der Fall)
				// dann schreibe alle Smells in die CSV
				SmellCSV.writeCurSmells(smellyFileSet, endDate);
			}else{
				// ansonsten müssen erst die geänderten Daten rausgerechnet werden
				// lädt alle geänderten Dateien zwischen zwei Daten in ein Set
				HashSet<String>	curChangedSet = getCurFiles(changedMap, startDate, endDate);
				
				// nimmt ursprüngliche Smell Liste und löscht alle geänderten Files raus
				HashSet<String> notChangedSet = deleteDuplicates(prevSmellyFileSet, curChangedSet);
				
				// nimmt aktuelle Smell Liste und fügt sie zu den ursprünglichen (außer den gelöschten hinzu)
				for(String s : smellyFileSet){
					notChangedSet.add(s);			// Sets sind unique daher keine Abfrage nötig
				}
				
				// zum schluss dieses Set in die CSV schreiben
				SmellCSV.writeCurSmells(notChangedSet, endDate);
			}
			
			// prevSmellyFileSet und startDate ändern
			startDate = endDate;
			prevSmellyFileSet = smellyFileSet;
		}
	}
	
	/**
	 * nimmt ursprüngliche Smell Liste und löscht alle geänderten Files raus
	 * 
	 * @param currentSet
	 * @param duplicateSet
	 * @return
	 */
	private static HashSet<String> deleteDuplicates(HashSet<String> currentSet, HashSet<String> duplicateSet){
		HashSet<String> resultSet = new HashSet<String>();
		for(String curString : currentSet){
			if(!duplicateSet.contains(curString)){
				resultSet.add(curString);
			}
		}
		return resultSet;
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
			System.out.println("Datum konnte nicht korrekt eingelesen werden!");
			e.printStackTrace();
		}
		
		return retDate;
	}
	
	
	/**
	 * Lädt alle Dateien aus einer TreeMap<ChangedFile, String> zwischen zwei Daten in ein Set
	 * 
	 * @param bugMap TreeMap mit allen Dateien im ChangedFile-Objektformat
	 * @param startDate 
	 * @param endDate
	 * @return alle Bugfixes zwischen StartDate und EndDate
	 */
	public static HashSet<String> getCurFiles(TreeMap<ChangedFile, String> bugMap, Date startDate, Date endDate){
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
}
