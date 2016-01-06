package output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import input.CSVHelper;
import main.Program;

public class SmellCSV {

	/**
	 * 
	 * @param smellSet
	 * @param curDate
	 */
	public static void writeCurSmells(HashSet<String> smellSet, Date curDate){
		// In CSV Datei schreiben
	  String smellMode = Program.getSmellMode();	
  	  File csvOut = new File(Program.getSmellDir() + "/../" + smellMode + "smellOverview.csv");
    
  	  for(String smell : smellSet){
  		BufferedWriter buff;
		try {
			buff = new BufferedWriter(new FileWriter( csvOut, true ));
			buff.write( smell + "," + curDate );
		    buff.newLine();
		    buff.close();
		} catch (IOException e1) {
			System.out.println("Fehler beim lesen/schreiben der Datei!");
			e1.printStackTrace();
		}
  	  }
  	  
	}
	
	/**
	 * 
	 * @param smellCSV
	 * @param locationXML
	 */
	public static void processXMLFile(File smellCSV, File locationXML){
		String fileName = smellCSV.getName();
		File csvOut = new File(smellCSV.getAbsolutePath());
		// TODO: für LargeFeature Analyse müssen die FileLocations aus der XML in csv Form 
		// umgewandelt werden - um die analyse dann einheitlich fortführen zu können
		// Erste Spalte FileName - zweite Spalte SmellScore
		
		// smellCSV einlesen und danach löschen (da neu erstellt wird)
		TreeMap<String, Double> featureMap = CSVHelper.getFeatureMap(smellCSV.getAbsolutePath(), Program.getThreshold());
		smellCSV.delete();
		
		// die eingelesenen Features mit der XML abgleichen und Filenamen pro Feature auslesen
		for(String s : featureMap.keySet()){
			HashSet<String> fileSetXML = new HashSet<String>();
			try {
				fileSetXML = getFilesFromXML(locationXML, s);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// TODO: FeatureNamen und Score in CSV schreiben
			for(String fileNameStr : fileSetXML){
		  		BufferedWriter buff;
				try {
					buff = new BufferedWriter(new FileWriter( csvOut, true ));
					buff.write( fileNameStr + "," + featureMap.get(s) +","+ s);
				    buff.newLine();
				    buff.close();
				} catch (IOException e1) {
					System.out.println("Fehler beim lesen/schreiben der Datei!");
					e1.printStackTrace();
				}
		  	  }
		}
		
	}
	
	/**
	 * 
	 * @param locationXML
	 * @param feature
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static HashSet<String> getFilesFromXML(File locationXML, String feature) throws ParserConfigurationException, SAXException, IOException{
		HashSet<String> fileSet = new HashSet<String>();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(locationXML.getAbsolutePath());
		
		doc.getDocumentElement().normalize();
		
		NodeList featList = doc.getElementsByTagName("data.Feature");
		System.out.println("ukupno:"+featList.getLength());
		for (int j = 0; j < featList.getLength(); j++) {
	         Element el = (org.w3c.dom.Element) featList.item(j);
	         
	         NodeList nameL = el.getElementsByTagName("Name");
	         String name = nameL.item(0).getTextContent();
	         
	         if(name.equals(feature)){
	        	 NodeList fileList = el.getElementsByTagName("compilationFiles");
	        	 Element testEle = (Element) fileList.item(0);
	        	 
	        	 NodeList testList = testEle.getElementsByTagName("string");
	        	 for (int i = 0; i < testList.getLength(); i++) {
	        		 Node test = testList.item(i);
	        		 
	        		 System.out.println(test.getTextContent());
	        		 fileSet.add(test.getTextContent());
	        	 }	        	 
	         }         
	     }
		
		return fileSet;
	}
}
