/**
 * 
 */
package beans.amsBeans;

/**
 * @author Vikas
 *
 */
public class EquipmentBean {
	String equipment;
	String sizeType;
	String sealDetails;
	String[] seal;
	String serviceType;
	String containerOwnership;
	String vanningType;
	String costomConventionId;
	
	
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getContainerOwnership() {
		return containerOwnership;
	}
	public void setContainerOwnership(String containerOwnership) {
		this.containerOwnership = containerOwnership;
	}
	public String getVanningType() {
		return vanningType;
	}
	public void setVanningType(String vanningType) {
		this.vanningType = vanningType;
	}
	public String getCostomConventionId() {
		return costomConventionId;
	}
	public void setCostomConventionId(String costomConventionId) {
		this.costomConventionId = costomConventionId;
	}
	public String getEquipment() {
		return equipment;
	}
	public String[] getSeal() {
		return seal;
	}
	public void setSeal(String[] seal) {
		this.seal = seal;
	}
	public void setEquipment(String equipment) {
		this.equipment = equipment;
	}
	public String getSizeType() {
		return sizeType;
	}
	public void setSizeType(String sizeType) {
		this.sizeType = sizeType;
	}
	public String getSealDetails() {
		return sealDetails;
	}
	public void setSealDetails(String sealDetails) {
		this.sealDetails = sealDetails;
	}
}
