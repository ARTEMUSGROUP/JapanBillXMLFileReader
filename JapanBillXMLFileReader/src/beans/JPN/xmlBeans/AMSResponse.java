package beans.JPN.xmlBeans;

import java.io.Serializable;

import migrate.MigrateJPNBLXml;;

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
		return MigrateJPNBLXml.bean2Xml(this);
	
	}


}
