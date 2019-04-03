package beans.JPN.xmlBeans;

import java.io.Serializable;

public class Cargo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int packageIndex;
	String harmonizedCode;
	String hazardCode;
	String description;
    String UN;
	String CLASS;
	int manufacturerIndex;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getPackageIndex() {
		return packageIndex;
	}
	public void setPackageIndex(int packageIndex) {
		this.packageIndex = packageIndex;
	}
	public String getHarmonizedCode() {
		return harmonizedCode;
	}
	public void setHarmonizedCode(String harmonizedCode) {
		this.harmonizedCode = harmonizedCode;
	}
	public String getHazardCode() {
		return hazardCode;
	}
	public void setHazardCode(String hazardCode) {
		this.hazardCode = hazardCode;
	}
	public int getManufacturerIndex() {
		return manufacturerIndex;
	}
	public void setManufacturerIndex(int manufacturerIndex) {
		this.manufacturerIndex = manufacturerIndex;
	}
	public String getUN() {
		return UN;
	}
	public void setUN(String uN) {
		UN = uN;
	}
	public String getCLASS() {
		return CLASS;
	}
	public void setCLASS(String cLASS) {
		CLASS = cLASS;
	}
	
	
	

}
