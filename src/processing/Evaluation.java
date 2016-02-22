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
		// {smellyFix, smellyNotFix, nonsmellyFix, nonsmellyNotFix}
		int[] totalValues = {0,0,0,0};
		int[] bundleValues = {0,0,0,0};
		int[] fileValues = {0,0,0,0};
		int[] featureValues = {0,0,0,0};
		int[] abafValues = {0,0,0,0};
		// {smellyFix, nonSmellyFix}
		int[] totalFixTotals = {0,0,0,0};
		int[] bundleFixTotals = {0,0,0,0};
		int[] fileFixTotals = {0,0,0,0};
		int[] featureFixTotals = {0,0,0,0};
		int[] abafFixTotals = {0,0,0,0};
		int abCount = 0;
		int afCount = 0;
		int lfCount = 0;
		int smFixCount = 0;
		int smChangeCount = 0;
		int nsFixCount = 0;
		int nsChangeCount = 0;
		int smellySizeAmount = 0;
		int nonSmellySizeAmount = 0;
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
						smellFixed++; totalValues[0]++;
						smellChanged++;
						
						// Abfrage für die einzelnen Smells
						if(Integer.parseInt(nextLine[1])==1){
							bundleValues[0]++;
							bundleFixTotals[0] += Integer.parseInt(nextLine[8]);
							bundleFixTotals[2] += Integer.parseInt(nextLine[10]);
						}else{
							bundleValues[2]++;
							bundleFixTotals[1] += Integer.parseInt(nextLine[8]);
							bundleFixTotals[3] += Integer.parseInt(nextLine[10]);
						}
						if(Integer.parseInt(nextLine[2])==1){
							fileValues[0]++;
							fileFixTotals[0] += Integer.parseInt(nextLine[8]);
							fileFixTotals[2] += Integer.parseInt(nextLine[10]);
						}else{
							fileValues[2]++;
							fileFixTotals[1] += Integer.parseInt(nextLine[8]);
							fileFixTotals[3] += Integer.parseInt(nextLine[10]);
						}
						if(Integer.parseInt(nextLine[3])==1){
							featureValues[0]++;
							featureFixTotals[0] += Integer.parseInt(nextLine[8]);
							featureFixTotals[2] += Integer.parseInt(nextLine[10]);
						}else{
							featureValues[2]++;
							featureFixTotals[1] += Integer.parseInt(nextLine[8]);
							featureFixTotals[3] += Integer.parseInt(nextLine[10]);
						}
						if(Integer.parseInt(nextLine[1])==1 || Integer.parseInt(nextLine[2])==1){
							abafValues[0]++;
							abafFixTotals[0] += Integer.parseInt(nextLine[8]);
							abafFixTotals[2] += Integer.parseInt(nextLine[10]);
						}else{
							abafValues[2]++;
							abafFixTotals[1] += Integer.parseInt(nextLine[8]);
							abafFixTotals[3] += Integer.parseInt(nextLine[10]);
						}		
						
					}else{ //hasFixed.equals("NO")
						smellNotFixed++; totalValues[1]++;
						
						// Abfrage für die einzelnen Smells
						if(Integer.parseInt(nextLine[1])==1){
							bundleValues[1]++;
							bundleFixTotals[2] += Integer.parseInt(nextLine[10]);
						}else{
							bundleValues[3]++;
							bundleFixTotals[3] += Integer.parseInt(nextLine[10]);
						}
						if(Integer.parseInt(nextLine[2])==1){
							fileValues[1]++;
							fileFixTotals[2] += Integer.parseInt(nextLine[10]);
						}else{
							fileValues[3]++;
							fileFixTotals[3] += Integer.parseInt(nextLine[10]);
						}
						if(Integer.parseInt(nextLine[3])==1){
							featureValues[1]++;
							featureFixTotals[2] += Integer.parseInt(nextLine[10]);
						}else{
							featureValues[3]++;
							featureFixTotals[3] += Integer.parseInt(nextLine[10]);
						}
						if(Integer.parseInt(nextLine[1])==1 || Integer.parseInt(nextLine[2])==1){
							abafValues[1]++;
							abafFixTotals[2] += Integer.parseInt(nextLine[10]);
						}else{
							abafValues[3]++;
							abafFixTotals[3] += Integer.parseInt(nextLine[10]);
						}
						
						/** CHANGE **/
						if(hasChanged.equals("YES")){
							smellChanged++;
						}else{ //hasChanged.equals("NO")
							smellNotChanged++;
						}
					}
					
					totalFixTotals[0] += Integer.parseInt(nextLine[8]);
					totalFixTotals[2] += Integer.parseInt(nextLine[10]);
					smFixCount += Integer.parseInt(nextLine[8]);
					smChangeCount += Integer.parseInt(nextLine[9]);
					smellySizeAmount += Integer.parseInt(nextLine[10]);
				}else{ //hasSmell.equals("NO")
					if(hasFixed.equals("YES")){
						nonSmellFixed++; totalValues[2]++;
						nonSmellChanged++;
						
						bundleValues[2]++;
						fileValues[2]++;
						featureValues[2]++;
						abafValues[2]++;
						
					}else{ //hasFixed.equals("NO")
						nonSmellNotFixed++; totalValues[3]++;
						
						bundleValues[3]++;
						fileValues[3]++;
						featureValues[3]++;
						abafValues[3]++;
						
						/** CHANGE **/
						if(hasChanged.equals("YES")){
							nonSmellChanged++;
						}else{ //hasChanged.equals("NO")
							nonSmellNotChanged++;
						}
					}
					
					totalFixTotals[1] += Integer.parseInt(nextLine[8]);
					bundleFixTotals[1] += Integer.parseInt(nextLine[8]);
					fileFixTotals[1] += Integer.parseInt(nextLine[8]);
					featureFixTotals[1] += Integer.parseInt(nextLine[8]);
					abafFixTotals[1] += Integer.parseInt(nextLine[8]);
					// SIZE
					totalFixTotals[3] += Integer.parseInt(nextLine[10]);
					bundleFixTotals[3] += Integer.parseInt(nextLine[10]);
					fileFixTotals[3] += Integer.parseInt(nextLine[10]);
					featureFixTotals[3] += Integer.parseInt(nextLine[10]);
					abafFixTotals[3] += Integer.parseInt(nextLine[10]);
					
					nsFixCount += Integer.parseInt(nextLine[8]);
					nsChangeCount += Integer.parseInt(nextLine[9]);
					nonSmellySizeAmount += Integer.parseInt(nextLine[10]);
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
		/*
		double orFixed = 0;
		double orChanged = 0;
		//System.out.println(smellFixed + "," + smellNotFixed + "," + nonSmellFixed + "," + nonSmellNotFixed);
		if(nonSmellNotFixed != 0 && nonSmellFixed != 0){
			orFixed = ((smellFixed / smellNotFixed) / (nonSmellFixed / nonSmellNotFixed));
		}
		if(nonSmellChanged != 0 && nonSmellNotChanged != 0){
			orChanged = ((smellChanged / smellNotChanged) / (nonSmellChanged / nonSmellNotChanged));
		}
		*/
		
		// TOTALS
		int smellyAmount = totalValues[0] + totalValues[1];
		int nonSmellyAmount = totalValues[2] + totalValues[3];
		
		double smellySizeMean = 0;
		double nonSmellySizeMean = 0;
		if(smellyAmount != 0)
			smellySizeMean = totalFixTotals[2] / smellyAmount;
		if(nonSmellyAmount != 0)
			nonSmellySizeMean = totalFixTotals[3] /nonSmellyAmount;
		// AB
		int absmellyAmount = bundleValues[0] + bundleValues[1];
		int abnonSmellyAmount = bundleValues[2] + bundleValues[3];
		
		double absmellySizeMean = 0;
		double abnonSmellySizeMean = 0;
		if(absmellyAmount != 0)
			absmellySizeMean = bundleFixTotals[2] / absmellyAmount;
		if(abnonSmellyAmount != 0)
			abnonSmellySizeMean = bundleFixTotals[3] / abnonSmellyAmount;
		// AF
		int afsmellyAmount = fileValues[0] + fileValues[1];
		int afnonSmellyAmount = fileValues[2] + fileValues[3];
		
		double afsmellySizeMean = 0;
		double afnonSmellySizeMean = 0;
		if(afsmellyAmount != 0)
			afsmellySizeMean = fileFixTotals[2] / afsmellyAmount;
		if(afnonSmellyAmount != 0)
			afnonSmellySizeMean = fileFixTotals[3] / afnonSmellyAmount;
		// LF
		int lfsmellyAmount = featureValues[0] + featureValues[1];
		int lfnonSmellyAmount = featureValues[2] + featureValues[3];
		
		double lfsmellySizeMean = 0;
		double lfnonSmellySizeMean = 0;
		if(lfsmellyAmount != 0)
			lfsmellySizeMean = featureFixTotals[2] / lfsmellyAmount;
		if(lfnonSmellyAmount != 0)
			lfnonSmellySizeMean = featureFixTotals[3] / lfnonSmellyAmount;
		// AB
		int abafsmellyAmount = abafValues[0] + abafValues[1];
		int abafnonSmellyAmount = abafValues[2] + abafValues[3];
		
		double abafsmellySizeMean = 0;
		double abafnonSmellySizeMean = 0;
		if(abafsmellyAmount != 0)
			abafsmellySizeMean = abafFixTotals[2] / abafsmellyAmount;
		if(abafnonSmellyAmount != 0)
			abafnonSmellySizeMean = abafFixTotals[3] / abafnonSmellyAmount;
		
		String path = Program.getResultsDir() + Program.getProject() + "/Correlated/../corOverview.csv";
		File csvOut = new File(path);
		BufferedWriter buff = null;
		try {
			buff = new BufferedWriter(new FileWriter( csvOut, true ));
			//buff.write("Version Date, SF, SNF, NSF, NSNF, SC, SNC, NSC, NSNC");
			buff.write(date + "," + abCount + "," + afCount + "," + lfCount 
					+ "," + totalValues[0] + "," + totalValues[1] + "," + totalValues[2] + "," + totalValues[3]
					+ "," + smellyAmount + "," + nonSmellyAmount + "," + totalFixTotals[0] + "," + totalFixTotals[1]  + "," + smellySizeMean + "," + nonSmellySizeMean 
					+ "," + bundleValues[0] + "," + bundleValues[1] + "," + bundleValues[2] + "," + bundleValues[3] 
					+ "," + absmellyAmount + "," + abnonSmellyAmount + "," + bundleFixTotals[0] + "," + bundleFixTotals[1] + "," + absmellySizeMean + "," + abnonSmellySizeMean 
					+ "," + fileValues[0] + "," + fileValues[1] + "," + fileValues[2] + "," + fileValues[3] 
					+ "," + afsmellyAmount + "," + afnonSmellyAmount + "," + fileFixTotals[0] + "," + fileFixTotals[1] + "," + afsmellySizeMean + "," + afnonSmellySizeMean 
					+ "," + featureValues[0] + "," + featureValues[1] + "," + featureValues[2] + "," + featureValues[3] 
					+ "," + lfsmellyAmount + "," + lfnonSmellyAmount + "," + featureFixTotals[0] + "," + featureFixTotals[1] + "," + lfsmellySizeMean + "," + lfnonSmellySizeMean 
					+ "," + abafValues[0] + "," + abafValues[1] + "," + abafValues[2] + "," + abafValues[3] 
					+ "," + abafsmellyAmount + "," + abafnonSmellyAmount + "," + abafFixTotals[0] + "," + abafFixTotals[1] + "," + abafsmellySizeMean + "," + abafnonSmellySizeMean 
					/*+ "," + (int) smellChanged + "," + (int) smellNotChanged + "," + (int) nonSmellChanged+ "," + (int) nonSmellNotChanged + "," + smChangeCount + "," + nsChangeCount*/
							);
			buff.newLine();
			buff.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
