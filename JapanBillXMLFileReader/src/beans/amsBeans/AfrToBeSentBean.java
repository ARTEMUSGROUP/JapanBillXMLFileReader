package beans.amsBeans;

public class AfrToBeSentBean {
	String loginScac; 
	int voyageId; 
	String loadPort;
	String dischargePort; 
	int billLadingId;
	String equipmentNumber;
	String controlIdentifier; 
	String actionCode;
	String amendmentCode; 
	String sentDate; 
	String sentUser;
	boolean isRegisterCompletionID;
	
	
	public boolean isRegisterCompletionID() {
		return isRegisterCompletionID;
	}
	public void setRegisterCompletionID(boolean isRegisterCompletionID) {
		this.isRegisterCompletionID = isRegisterCompletionID;
	}
	public String getLoginScac() {
		return loginScac;
	}
	public void setLoginScac(String loginScac) {
		this.loginScac = loginScac;
	}
	public int getVoyageId() {
		return voyageId;
	}
	public void setVoyageId(int voyageId) {
		this.voyageId = voyageId;
	}
	public String getLoadPort() {
		return loadPort;
	}
	public void setLoadPort(String loadPort) {
		this.loadPort = loadPort;
	}
	public String getDischargePort() {
		return dischargePort;
	}
	public void setDischargePort(String dischargePort) {
		this.dischargePort = dischargePort;
	}
	public int getBillLadingId() {
		return billLadingId;
	}
	public void setBillLadingId(int billLadingId) {
		this.billLadingId = billLadingId;
	}
	public String getEquipmentNumber() {
		return equipmentNumber;
	}
	public void setEquipmentNumber(String equipmentNumber) {
		this.equipmentNumber = equipmentNumber;
	}
	public String getControlIdentifier() {
		return controlIdentifier;
	}
	public void setControlIdentifier(String controlIdentifier) {
		this.controlIdentifier = controlIdentifier;
	}
	public String getActionCode() {
		return actionCode;
	}
	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}
	public String getAmendmentCode() {
		return amendmentCode;
	}
	public void setAmendmentCode(String amendmentCode) {
		this.amendmentCode = amendmentCode;
	}
	public String getSentDate() {
		return sentDate;
	}
	public void setSentDate(String sentDate) {
		this.sentDate = sentDate;
	}
	public String getSentUser() {
		return sentUser;
	}
	public void setSentUser(String sentUser) {
		this.sentUser = sentUser;
	}

}
