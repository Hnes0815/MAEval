package output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import data.MergedFileInfo;
import main.Program;

public class PreprocessOutput {

	public static void writeCSV(ArrayList<MergedFileInfo> outputList, String dateStr){
		String path = Program.getResultsDir() + Program.getProject() + "/Correlated/";
		File mkDir = new File(path);
		mkDir.mkdirs();
		File csvOut = new File(path + dateStr +".csv");
		BufferedWriter buff = null;
		try {
			buff = new BufferedWriter(new FileWriter( csvOut, true ));
			buff.write("Filename, Annotation Bundle, Annotation File, Large Feature, Smell Count, hasS, hasF, hasC, FixCount, ChangeCount, File Size");
			buff.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(MergedFileInfo info : outputList){
			//System.out.println(info);
			
			try {
				
				String smellAB = (info.getSmellAB()) ? "1" : "0";
				String smellAF = (info.getSmellAF()) ? "1" : "0";
				String smellLF = (info.getSmellLF()) ? "1" : "0";
				String hasSmell = (info.gethasSmell()) ? "YES" : "NO";
				String hasFixed = (info.gethasFixed()) ? "YES" : "NO";
				String hasChanged = (info.gethasChanged()) ? "YES" : "NO";
				buff.write( info.getFilename() + "," + smellAB + "," + smellAF + "," 
							+ smellLF + "," + info.getSmellCount() + "," + hasSmell + "," 
						    + hasFixed + "," + hasChanged + "," + info.getFixCount() + "," + info.getChangeCount() + "," + info.getFileSize());
			    buff.newLine();
			    
			} catch (IOException e1) {
				System.out.println("Fehler beim lesen/schreiben der Datei!");
				e1.printStackTrace();
			}
		}
		
		try {
			buff.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
