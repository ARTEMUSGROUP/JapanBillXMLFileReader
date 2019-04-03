package beans.JPN.voyageBeans;

import java.io.Serializable;

public class AMS implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String scac;
	Location location[];
    Voyage voyage;
    String messege="";
    
    public String getScac() {
		return scac;
	}
	public void setScac(String scac) {
		this.scac = scac;
	}
	public Location[] getLocation() {
		return location;
	}
	public void setLocation(Location[] location) {
		this.location = location;
	}
	public Voyage getVoyage() {
		return voyage;
	}
	public void setVoyage(Voyage voyage) {
		this.voyage = voyage;
	}
	public String getMessege() {
		return messege;
	}
	public void setMessege(String messege) {
		this.messege = messege;
	}
	

}
