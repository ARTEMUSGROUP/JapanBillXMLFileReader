package beans.JPN.voyageBeans;

import java.io.Serializable;

public class Voyage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String number;
	String vessel;
	String scac;
	String crewMembers;
	String passengers;
	String reportNumber;
	PortCall portCall[];
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getVessel() {
		return vessel;
	}
	public void setVessel(String vessel) {
		this.vessel = vessel;
	}
	public String getScac() {
		return scac;
	}
	public void setScac(String scac) {
		this.scac = scac;
	}
	public String getCrewMembers() {
		return crewMembers;
	}
	public void setCrewMembers(String crewMembers) {
		this.crewMembers = crewMembers;
	}
	public String getPassengers() {
		return passengers;
	}
	public void setPassengers(String passengers) {
		this.passengers = passengers;
	}
	public String getReportNumber() {
		return reportNumber;
	}
	public void setReportNumber(String reportNumber) {
		this.reportNumber = reportNumber;
	}
	public PortCall[] getPortCall() {
		return portCall;
	}
	public void setPortCall(PortCall[] portCall) {
		this.portCall = portCall;
	}
	
}
