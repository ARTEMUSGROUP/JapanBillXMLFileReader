/**
 * 
 */
package beans.amsBeans;

/**
 * @author Rohit
 * @Date 18 feb 2011
 * @Description This Bean is for the State Information.
 */
public class StateBean {
	private String country;
	private String state;
	private String name;
	
	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
