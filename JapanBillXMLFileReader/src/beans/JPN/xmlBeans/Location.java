package beans.JPN.xmlBeans;

import java.io.Serializable;

public class Location implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int index;
	String name;
	String country;
	String providence;
	String type;
	String UnCode;
	
	
	
	public String getUnCode() {
		return UnCode;
	}
	public void setUnCode(String unCode) {
		UnCode = unCode;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProvidence() {
		return providence;
	}
	public void setProvidence(String providence) {
		this.providence = providence;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
