package beans.JPN.voyageBeans;

import java.io.Serializable;

public class PortCall implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int locationIndex;
	String sailDate;
	String arriveDate;
	boolean load;
	boolean discharge;
	boolean lastLoad;
	
	public int getLocationIndex() {
		return locationIndex;
	}
	public void setLocationIndex(int locationIndex) {
		this.locationIndex = locationIndex;
	}
	public String getSailDate() {
		return sailDate;
	}
	public void setSailDate(String sailDate) {
		this.sailDate = sailDate;
	}
	public String getArriveDate() {
		return arriveDate;
	}
	public void setArriveDate(String arriveDate) {
		this.arriveDate = arriveDate;
	}
	public boolean isLoad() {
		return load;
	}
	public void setLoad(boolean load) {
		this.load = load;
	}
	public boolean isDischarge() {
		return discharge;
	}
	public void setDischarge(boolean discharge) {
		this.discharge = discharge;
	}
	public boolean isLastLoad() {
		return lastLoad;
	}
	public void setLastLoad(boolean lastLoad) {
		this.lastLoad = lastLoad;
	}

}
