package processing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;

import com.opencsv.CSVReader;

import main.Program;

public class Evaluation {

	public static void evalFile(File file){
		
		String csvFile = file.getAbsolutePath();
		
		double smellFixed = 0;
		double smellChanged = 0;
		double smellNotFixed = 0;
		double smellNotChanged = 0;
		double nonSmellFixed = 0;
		double nonSmellChanged = 0;
		double nonSmellNotFixed = 0;
		double nonSmellNotChanged = 0;
		int abCount = 0;
		int afCount = 0;
		int lfCount = 0;
		Date date = Preprocessing.getDateFromFileName(file);
		
		try {
			CSVReader reader = new CSVReader(new FileReader(csvFile));
			String[] nextLine;
			reader.readNext(); //erste Zeile überspringen
			while ((nextLine = reader.readNext()) != null) {
				String fileName = nextLine[0];
		
				String hasSmell = nextLine[5];
				String hasFixed = nextLine[6];
				String hasChanged = nextLine[7];
				
				if(hasSmell.equals("YES")){
					if(hasFixed.equals("YES")){
						smellFixed++;
						smellChanged++;
					}else{ //hasFixed.equals("NO")
						smellNotFixed++;
						if(hasChanged.equals("YES")){
							smellChanged++;
						}else{ //hasChanged.equals("NO")
							smellNotChanged++;
						}
					}
				}else{ //hasSmell.equals("NO")
					if(hasFixed.equals("YES")){
						nonSmellFixed++;
						nonSmellChanged++;
					}else{ //hasFixed.equals("NO")
						nonSmellNotFixed++;
						if(hasChanged.equals("YES")){
							nonSmellChanged++;
						}else{ //hasChanged.equals("NO")
							nonSmellNotChanged++;
						}
					}
				}
				
				if(nextLine[1].equals("1")){
					abCount++;
				}
				if(nextLine[2].equals("1")){
					afCount++;
				}
				if(nextLine[3].equals("1")){
					lfCount++;
				}
			}
		} catch (IOException e1) {
			System.out.println("Fehler beim lesen/schreiben der Datei!");
			e1.printStackTrace();
		}
		
		// Berechnungen
		// Odds-Ratios
		double orFixed = 0;
		double orChanged = 0;
		//System.out.println(smellFixed + "," + smellNotFixed + "," + nonSmellFixed + "," + nonSmellNotFixed);
		if(nonSmellNotFixed != 0 && nonSmellFixed != 0){
			orFixed = ((smellFixed / smellNotFixed) / (nonSmellFixed / nonSmellNotFixed));
		}
		if(nonSmellChanged != 0 && nonSmellNotChanged != 0){
			orChanged = ((smellChanged / smellNotChanged) / (nonSmellChanged / nonSmellNotChanged));
		}
		
		String path = Program.getResultsDir() + Program.getProject() + "/Correlated/../corOverview.csv";
		File csvOut = new File(path);
		BufferedWriter buff = null;
		try {
			buff = new BufferedWriter(new FileWriter( csvOut, true ));
			//buff.write("Version Date, SF, SNF, NSF, NSNF, SC, SNC, NSC, NSNC");
			buff.write(date + "," + (int) smellFixed + "," + (int) smellNotFixed + "," + (int) nonSmellFixed + "," + (int) nonSmellNotFixed
					+ "," + (int) smellChanged + "," + (int) smellNotChanged + "," + (int) nonSmellChanged+ "," + (int) nonSmellNotChanged
					+ "," + orFixed + "," + orChanged + "," + abCount + "," + afCount + "," + lfCount);
			buff.newLine();
			buff.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
