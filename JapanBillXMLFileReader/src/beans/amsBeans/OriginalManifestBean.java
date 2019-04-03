/**
 * 
 */
package beans.amsBeans;

import java.util.ArrayList;

/**
 * @author Rohit
 * @Date 11 April 2011
 */
public class OriginalManifestBean {

	private int vesselListId;
	private int voyageListId;
	private String loadPortListId;
	private String dischargePortListId;
	private String loadPort;
	private String loadPortCode;
	private String dischargePort;
	private String dischargePortCode;
	private String eta;
	private Boolean sendManifest;
	private Boolean is_Manifest_Error;	
	public ArrayList<OriginalManifestBean> objmOrignalManifestBeans;	
	private String dta;
	private String loadPortUnCode;
	private String dischargePortUnCode;
	
	public String getLoadPortUnCode() {
		return loadPortUnCode;
	}
	public void setLoadPortUnCode(String loadPortUnCode) {
		this.loadPortUnCode = loadPortUnCode;
	}
	
	public String getDischargePortUnCode() {
		return dischargePortUnCode;
	}
	public void setDischargePortUnCode(String dischargePortUnCode) {
		this.dischargePortUnCode = dischargePortUnCode;
	}
	public String getDta() {
		return dta;
	}
	public void setDta(String dta) {
		this.dta = dta;
	}
	public Boolean getIs_Manifest_Error() {
		return is_Manifest_Error;
	}
	public void setIs_Manifest_Error(Boolean is_Manifest_Error) {
		this.is_Manifest_Error = is_Manifest_Error;
	}
	public int getVesselListId() {
		return vesselListId;
	}
	public void setVesselListId(int vesselListId) {
		this.vesselListId = vesselListId;
	}
	public int getVoyageListId() {
		return voyageListId;
	}
	public void setVoyageListId(int voyageListId) {
		this.voyageListId = voyageListId;
	}
	
	public String getLoadPort() {
		return loadPort;
	}
	/**
	 * @param loadPort the loadPort to set
	 */
	public void setLoadPort(String loadPort) {
		this.loadPort = loadPort;
	}
	/**
	 * @return the loadPortCode
	 */
	public String getLoadPortCode() {
		return loadPortCode;
	}
	/**
	 * @param loadPortCode the loadPortCode to set
	 */
	public void setLoadPortCode(String loadPortCode) {
		this.loadPortCode = loadPortCode;
	}
	/**
	 * @return the dischargePort
	 */
	public String getDischargePort() {
		return dischargePort;
	}
	/**
	 * @param dischargePort the dischargePort to set
	 */
	public void setDischargePort(String dischargePort) {
		this.dischargePort = dischargePort;
	}
	/**
	 * @return the dischargePortCode
	 */
	public String getDischargePortCode() {
		return dischargePortCode;
	}
	/**
	 * @param dischargePortCode the dischargePortCode to set
	 */
	public void setDischargePortCode(String dischargePortCode) {
		this.dischargePortCode = dischargePortCode;
	}
	/**
	 * @return the eta
	 */
	public String getEta() {
		return eta;
	}
	/**
	 * @param eta the eta to set
	 */
	public void setEta(String eta) {
		this.eta = eta;
	}
	/**
	 * @return the sendManifest
	 */
	public Boolean getSendManifest() {
		return sendManifest;
	}
	/**
	 * @param sendManifest the sendManifest to set
	 */
	public void setSendManifest(Boolean sendManifest) {
		this.sendManifest = sendManifest;
	}
	public ArrayList<OriginalManifestBean> getObjmOrignalManifestBeans() {
		return objmOrignalManifestBeans;
	}
	public void setObjmOrignalManifestBeans(
			ArrayList<OriginalManifestBean> objmOrignalManifestBeans) {
		this.objmOrignalManifestBeans = objmOrignalManifestBeans;
	}
	public String getLoadPortListId() {
		return loadPortListId;
	}
	public void setLoadPortListId(String loadPortListId) {
		this.loadPortListId = loadPortListId;
	}
	public String getDischargePortListId() {
		return dischargePortListId;
	}
	public void setDischargePortListId(String dischargePortListId) {
		this.dischargePortListId = dischargePortListId;
	}
	
	
}
