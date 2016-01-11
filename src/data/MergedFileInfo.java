package data;

import java.util.Date;

public class MergedFileInfo implements Comparable<MergedFileInfo>{

	private String filename;
	private boolean smellAB;
	private boolean smellAF;
	private boolean smellLF;
	private boolean hasSmell;
	private boolean hasChanged;
	private boolean hasFixed;
	private String version;
	private Date versDate;
	private int smellCount;
	
	public MergedFileInfo(String filename, Date verDate){
		this.filename = filename;
		this.smellAB = false;
		this.smellAF = false;
		this.smellLF = false;
		this.hasSmell = false;
		this.hasChanged = false;
		this.hasFixed = false;
		this.version = "";
		this.versDate = verDate;
		this.smellCount = 0;
	}
	
	public void setSmellAB(){
		this.smellAB = true;
		this.hasSmell = true;
		this.smellCount++;
	}
	
	public void setSmellAF(){
		this.smellAF = true;
		this.hasSmell = true;
		this.smellCount++;
	}
	
	public void setSmellLF(){
		this.smellLF = true;
		this.hasSmell = true;
		this.smellCount++;
	}
	
	public void sethasChanged(){
		this.hasChanged = true;
	}
	
	public void sethasFixed(){
		this.hasFixed = true;
	}
	
	public String getFilename(){
		return filename;
	}
	public Date getDate(){
		return versDate;
	}
	

	@Override
	public String toString(){
		return this.filename + " - Smell: " + this.hasSmell + " - Fixed: " + this.hasFixed + " - Changed: " + this.hasChanged + " - " + this.versDate;
	}
	
	@Override
	public int compareTo(MergedFileInfo obj) {
		if (obj.getDate().after(this.getDate()))
			return -1;
		else if(obj.getDate().before(this.getDate()))
			return 1;
		else{
			return obj.getFilename().compareTo(this.getFilename());
		}
	}
}
