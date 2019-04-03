package beans.amsBeans;

public class HazardBean {

	private String UNCode;
	private String description;
	private int systemcode;
	private String className;
	
	public String getUNCode() {
		return UNCode;
	}
	public void setUNCode(String uNCode) {
		UNCode = uNCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getSystemcode() {
		return systemcode;
	}
	public void setSystemcode(int systemcode) {
		this.systemcode = systemcode;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
}
