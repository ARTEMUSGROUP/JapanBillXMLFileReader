package beans.JPN.voyageBeans;

import java.io.Serializable;

import migrate.MigrateJPNVoyageDetails;

public class AMSResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Response response[];
	public Response[] getResponse() {
		return response;
	}
	public void setResponse(Response[] response) {
		this.response = response;
	}
	public String toXML() {
		return MigrateJPNVoyageDetails.bean2Xml(this);
	
	}


}
