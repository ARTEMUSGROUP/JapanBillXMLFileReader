package beans.JPN.voyageBeans;

import java.io.Serializable;

public class Location implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int index;
	String name;
	String country;
	String customsCode;
	String unCode;
	String canadaCustomsOffice;
	String providence;
	String holdEmail;
	String type;
	boolean isCustomForeign;
	
	
	public boolean getIsCustomForeign() {
		return isCustomForeign;
	}
	public void setIsCustomForeign(boolean isCustomForeign) {
		this.isCustomForeign = isCustomForeign;
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
	public String getCustomsCode() {
		return customsCode;
	}
	public void setCustomsCode(String customsCode) {
		this.customsCode = customsCode;
	}
	public String getUnCode() {
		return unCode;
	}
	public void setUnCode(String unCode) {
		this.unCode = unCode;
	}
	public String getCanadaCustomsOffice() {
		return canadaCustomsOffice;
	}
	public void setCanadaCustomsOffice(String canadaCustomsOffice) {
		this.canadaCustomsOffice = canadaCustomsOffice;
	}
	public String getProvidence() {
		return providence;
	}
	public void setProvidence(String providence) {
		this.providence = providence;
	}
	public String getHoldEmail() {
		return holdEmail;
	}
	public void setHoldEmail(String holdEmail) {
		this.holdEmail = holdEmail;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	

}
