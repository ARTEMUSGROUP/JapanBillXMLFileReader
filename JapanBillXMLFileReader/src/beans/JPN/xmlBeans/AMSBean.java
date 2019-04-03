package beans.JPN.xmlBeans;

import java.io.Serializable;

public class AMSBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String scac;
	Party party[];
	Location location[];
	BL bl;
	private String message="";
	
	public String getScac() {
		return scac;
	}
	public void setScac(String scac) {
		this.scac = scac;
	}
	public Party[] getParty() {
		return party;
	}
	public void setParty(Party[] party) {
		this.party = party;
	}
	public Location[] getLocation() {
		return location;
	}
	public void setLocation(Location[] location) {
		this.location = location;
	}
	public BL getBl() {
		return bl;
	}
	public void setBl(BL bl) {
		this.bl = bl;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	

}
