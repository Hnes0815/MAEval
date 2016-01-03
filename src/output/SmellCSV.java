package output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;

import main.Program;

public class SmellCSV {

	public static void writeCurSmells(HashSet<String> smellSet, Date curDate){
		// In CSV Datei schreiben
  	  File csvOut = new File(Program.getSmellDir() + "/../smellOverview.csv");
    
  	  for(String smell : smellSet){
  		BufferedWriter buff;
		try {
			buff = new BufferedWriter(new FileWriter( csvOut, true ));
			buff.write( smell + "," + curDate);
		    buff.newLine();
		    buff.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
  	  }
  	  
	}
}
