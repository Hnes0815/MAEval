package data;

import java.util.Date;

public class MergedFileInfo {

	public String filename;
	public boolean smellAB;
	public boolean smellAF;
	public boolean smellLF;
	public boolean hasChanged;
	public boolean hasFixed;
	public String version;
	public Date versDate;
	
	public MergedFileInfo(String filename){
		this.filename = filename;
	}
}
