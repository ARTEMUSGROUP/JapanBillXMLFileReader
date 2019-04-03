package beans.JPN.xmlBeans;

import java.io.Serializable;

public class BL implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String number;
	String billType;
	String masterBillNo;
	String masterBillScac;
	String voyage;
	String vessel;
	String moveType;
	String nvoType;
	String notifySCAC;
	String hblScac;
	Equipment[] equipment;
	Package[] Package;

	Cargo[] cargo;
	PartyRef[] partyRef;
	LocationRef locationRef[];
	AMSNotify amsNotify[];
		
		
	
	public String getHblScac() {
		return hblScac;
	}
	public void setHblScac(String hblScac) {
		this.hblScac = hblScac;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public String getMasterBillNo() {
		return masterBillNo;
	}
	public void setMasterBillNo(String masterBillNo) {
		this.masterBillNo = masterBillNo;
	}
	public String getMasterBillScac() {
		return masterBillScac;
	}
	public void setMasterBillScac(String masterBillScac) {
		this.masterBillScac = masterBillScac;
	}
	public String getVoyage() {
		return voyage;
	}
	public void setVoyage(String voyage) {
		this.voyage = voyage;
	}
	public String getVessel() {
		return vessel;
	}
	public void setVessel(String vessel) {
		this.vessel = vessel;
	}
	public String getMoveType() {
		return moveType;
	}
	public void setMoveType(String moveType) {
		this.moveType = moveType;
	}
	public String getNvoType() {
		return nvoType;
	}
	public void setNvoType(String nvoType) {
		this.nvoType = nvoType;
	}
	public String getNotifySCAC() {
		return notifySCAC;
	}
	public void setNotifySCAC(String notifySCAC) {
		this.notifySCAC = notifySCAC;
	}
	public Equipment[] getEquipment() {
		return equipment;
	}
	public void setEquipment(Equipment[] equipment) {
		this.equipment = equipment;
	}
	
	
	public Package[] getPackage() {
		return Package;
	}
	public void setPackage(Package[] package1) {
		Package = package1;
	}
	public Cargo[] getCargo() {
		return cargo;
	}
	public void setCargo(Cargo[] cargo) {
		this.cargo = cargo;
	}
	public PartyRef[] getPartyRef() {
		return partyRef;
	}
	public void setPartyRef(PartyRef[] partyRef) {
		this.partyRef = partyRef;
	}
	public LocationRef[] getLocationRef() {
		return locationRef;
	}
	public void setLocationRef(LocationRef[] locationRef) {
		this.locationRef = locationRef;
	}
	public AMSNotify[] getAmsNotify() {
		return amsNotify;
	}
	public void setAmsNotify(AMSNotify[] amsNotify) {
		this.amsNotify = amsNotify;
	}

	
	
	
	
	
	
	
	
	

}
