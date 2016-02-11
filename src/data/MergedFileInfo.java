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
	private int fixCount;
	private int changeCount;
	private long fileSize;
	
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
		this.fixCount = 0;
		this.changeCount = 0;
		this.fileSize = 0;
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
	public void setFixCount(Integer count){
		this.fixCount = count;
	}
	public void setChangeCount(Integer count){
		this.changeCount = count;
	}
	public void setFileSize(long size){
		this.fileSize = size;
	}
	
	public String getFilename(){
		return filename;
	}
	public Date getDate(){
		return versDate;
	}
	public boolean getSmellAB(){
		return smellAB;
	}
	public boolean getSmellAF(){
		return smellAF;
	}
	public boolean getSmellLF(){
		return smellLF;
	}
	public int getSmellCount(){
		return smellCount;
	}
	public boolean gethasSmell(){
		return hasSmell;
	}
	public boolean gethasChanged(){
		return hasChanged;
	}
	public boolean gethasFixed(){
		return hasFixed;
	}
	public int getFixCount(){
		return fixCount;
	}
	public int getChangeCount(){
		return changeCount;
	}
	public long getFileSize(){
		return fileSize;
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
