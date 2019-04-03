package migrate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import mail.SendMail;
import beans.JPN.voyageBeans.AMS;
import beans.JPN.voyageBeans.AMSResponse;
import beans.JPN.voyageBeans.Location;
import beans.JPN.voyageBeans.PortCall;
import beans.JPN.voyageBeans.Response;
import beans.JPN.voyageBeans.Voyage;
import beans.amsBeans.ForeignPortBean;
import beans.amsBeans.LocationBean;
import beans.amsBeans.PortDetailsBean;
import beans.amsBeans.VesselBean;
import beans.amsBeans.VoyageBean;

import com.wutka.jox.JOXBeanInputStream;
import com.wutka.jox.JOXBeanOutputStream;

import dao.LoadProperty;
import dao.LocationDAO;
import dao.VesselDAO;
import dao.VoyageJpDao;

public class MigrateJPNVoyageDetails extends LoadProperty {
	ArrayList<Response> objresponselist=new ArrayList<Response>();
	VoyageBean objVoyageBean=null;
	LocationDAO objLocationDAO=null;
	static Response objresponsebean=null;
	VesselDAO objVesselDAO=null;
	VoyageJpDao objVoyageDAO=null;
	Properties objmProperties=null;
	String subject="";
	String loginScac;
	int voyageId;
	boolean result=false;
	Voyage voyagebean=null;
	FileInputStream objmFileInputStream=null;
	JOXBeanInputStream joxIn=null;
	AMS ams=null;
	public void MigrateVoyage(File objmFile, String mainpath, String dbname) throws Exception{
		
		try {
			objmProperties=loadProperty();
			objVoyageBean=new VoyageBean();
			objLocationDAO=new LocationDAO();
			objVesselDAO=new VesselDAO();
			objVoyageDAO=new VoyageJpDao();
		
			objmFileInputStream=new FileInputStream(objmFile);
			joxIn = new JOXBeanInputStream(objmFileInputStream);
			ams =(AMS) joxIn.readObject(AMS.class);
			loginScac=ams.getScac();
			voyagebean = ams.getVoyage();
			
			validateCountry(ams.getLocation());
			ValidateUnCode(ams.getLocation());
			
			if(result==true){
			validateLocation(ams.getLocation());
			populateVoyage(ams);
			validateport(ams);
			}
			   if(objVoyageDAO.isExist(loginScac, objVoyageBean.getVesselId(),objVoyageBean.getVoyage())){
//					Source=Source+objmProperties.getProperty("OTHER_SOURCE");
//					element=element+objmProperties.getProperty("OTHER_ELEMENTV");
//					code=code+objmProperties.getProperty("OTHER_CODE");
//					description=description+objmProperties.getProperty("VOYAGE_ALREADY_EXISTS_DESCRIPTION").replaceAll("-", voyagebean.getNumber());
				   generateResponseBean("Voyage", "Voyage with Voyage number '"+voyagebean.getNumber()+"' already exists","ERROR");
					result=false;
				}else{
					
					
					if(result==true){
					
					 if(objVoyageDAO.insert(objVoyageBean).equals("Success")){
//						Source=Source+objmProperties.getProperty("OTHER_SOURCE");
//						element=element+objmProperties.getProperty("OTHER_ELEMENTV");
//						code=code+objmProperties.getProperty("OTHER_SUCESS_CODE");
//						description=description+objmProperties.getProperty("VOYAGE_SAVED_DESCRIPION").replaceAll("hi", voyagebean.getNumber());
						 
				       generateResponseBean("Voyage", "Voyage with Voyage number '"+voyagebean.getNumber()+"' Saved Sucessfully","INFO");
						result=true;
					}else
						result=false;
						}
				   }
//			    	 if(result){
//							String s="cmd.exe /c start a.bat \""+objmProperties.getProperty("CLIENT_PATH")+objmFile.getName()+"\" \""+objmProperties.getProperty("INPUT_PATH")+objmFile.getName()+"\"";
//							s=s+" \""+objmProperties.getProperty("INPUT_PATH")+objmFile.getName()+"\"";
//							Runtime.getRuntime().exec(s);
//							result=true;
//						}else{
//							String s="cmd.exe /c start a.bat \""+objmProperties.getProperty("CLIENT_PATH")+objmFile.getName()+"\" \""+objmProperties.getProperty("ERROR_PATH")+objmFile.getName()+"\"";
//							s=s+" \""+objmProperties.getProperty("INPUT_PATH")+objmFile.getName()+"\"";
//							Runtime.getRuntime().exec(s);
//							result=false;
//						}
//			     }catch (Exception e) {
//					// TODO: handle exception
//				}
			  
			}catch (IOException e) {
				e.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally{
				try {
					if(objmFileInputStream!=null)
						objmFileInputStream.close();
					if(joxIn!=null)
						joxIn.close();
					if(result){
						System.out.println("Migration complete");
					}else{
						System.out.println("Migration incomplete");
					}
					objLocationDAO.commit(result);
					objVoyageDAO.commit(result);
					if(objLocationDAO!=null)
						objLocationDAO.closeAll();
					if(objVoyageDAO!=null)
						objVoyageDAO.closeAll();
					FileWriter fr = null;
					Writer br = null;
					if(!ams.getMessege().equals("")){
//						Response.setSource("DATA");
//						Response.setElement("");
//						Response.setCode("ERROR");
						generateResponseBean(" ", ams.getMessege(),"ERROR");
						//Response.setDescription(amsbean.getMessage());
					}
					 try{
						   AMSResponse objAmsResponse=new AMSResponse();
							Response[] x=new Response[0];
							x=(Response[]) objresponselist.toArray(x);
							objAmsResponse.setResponse(x);
							String xmlStr=objAmsResponse.toXML();
					   	    fr= new FileWriter(new File(mainpath+"RESPONSE\\"+objmFile.getName()+""));
					        br= new BufferedWriter(fr);
						    br.write(xmlStr);
						 }catch (Exception e) {
								e.printStackTrace();
							}finally{
								if(br!=null){
									br.flush();
									br.close();
									fr.close();
								}
								SendMail objmSendMail=new SendMail();
							    objmSendMail.sendMail(dbname,mainpath+"RESPONSE\\"+objmFile.getName(),objmFile.getName(),"JAPAN Artemus :Voyage Response File"+"("+loginScac+")");
							    if(result)
							    	moveFile(objmFile,mainpath+"PROCESSED\\"+objmFile.getName());
							    else
							    	moveFile(objmFile,mainpath+"ERROR\\"+objmFile.getName());
							}
					
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
	}
	
	

	public void moveFile(File objmFile,String destPath){
		Boolean result1=true;
		File destFile=null;
		StringBuffer objmBuffer = null;
		BufferedWriter objmBufferedWriter=null;
		BufferedReader objmBufferedReader=null;
		try {
			//code to read from file
			objmBuffer=new StringBuffer();
			objmBufferedReader=new BufferedReader(new FileReader(objmFile));
			String s="";
			while((s=objmBufferedReader.readLine())!=null){
				objmBuffer.append(s);
			}
			
			//code to write into file
			destFile=new File(destPath);
			destFile.createNewFile();
			objmBufferedWriter=new BufferedWriter(new FileWriter(destFile));
			objmBufferedWriter.write(objmBuffer.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			result1=false;
		}finally{
			try {
				if(objmBufferedWriter!=null){
					objmBufferedWriter.flush();
					objmBufferedWriter.close();
				}if(objmBufferedReader!=null){
					objmBufferedReader.close();
				}
				if(result1)
					objmFile.delete();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	public static String  bean2Xml(Object bean) {
		
		ByteArrayOutputStream xmlData = new ByteArrayOutputStream();
		JOXBeanOutputStream joxOut = new JOXBeanOutputStream(xmlData);
		try{
		joxOut.writeObject(beanName(bean), bean);
		return xmlData.toString();
		} 
		catch (IOException exc){
		exc.printStackTrace();
		return null;
		}
		finally{
		try{
		xmlData.close();
		joxOut.close();
		}
		catch (Exception e){
		e.printStackTrace();
		}
		}
	}
	
	private static String beanName(Object bean){
		String fullClassName = bean.getClass().getName();
		String classNameTemp = fullClassName.substring(fullClassName.lastIndexOf(".") + 1, fullClassName.length());
		return classNameTemp.substring(0, 1)+ classNameTemp.substring(1);
		}

	private void validateport(AMS ams)throws Exception {
		
		ArrayList<PortDetailsBean> objmPortDetailsBeans=new ArrayList<PortDetailsBean>();
		Location[] objLocationbean=ams.getLocation();
		PortCall[] objPortCallbean=voyagebean.getPortCall();
		boolean portValidation=false;
		for(PortCall portCall:ams.getVoyage().getPortCall()){
		   if(portCall.isDischarge()==true){
			   portValidation=true;
			   break;
		   }
		}
		if(!portValidation){
//		    Response.setSource("data");
//			Response.setElement("PortCall");
//			Response.setCode("ERROR");
//			Response.setDescription("Voyage should have atleast one discharge port");
			generateResponseBean("PortCall", "Voyage should have atleast one discharge port","ERROR");
			result=false;
	   }
		portValidation=false;
		for(PortCall portCall:ams.getVoyage().getPortCall()){
		   if(portCall.isLoad()==true){
			   portValidation=true;
			   break;
		   }
		}
		if(!portValidation){
//		    Response.setSource("data");
//			Response.setElement("PortCall");
//			Response.setCode("ERROR");
//			Response.setDescription("Voyage should have atleast one load port");
			generateResponseBean("PortCall", "Voyage should have atleast one load port","ERROR");
			result=false;
	   }
		
		portValidation=false;
		for(PortCall portCall:ams.getVoyage().getPortCall()){
			   if(portCall.isLastLoad()==true){
				   portValidation=true;
				   break;
			   }
			}
			if(!portValidation){
//			    Response.setSource("data");
//				Response.setElement("PortCall");
//				Response.setCode("ERROR");
//				Response.setDescription("Voyage should have atleast one load port");
				generateResponseBean("PortCall", "PortCall should have atleast one Lastload is 'TRUE'.","ERROR");
				result=false;
		   }
			else{
		
		String lastloaddate;
		String dischargeDate;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Date lastloaddate1 = null;
    	Date dichargedate1 = null;
		for(PortCall portCall:ams.getVoyage().getPortCall()){
			if(portCall.isLastLoad()==true){
				lastloaddate=portCall.getArriveDate();
				try {
					lastloaddate1= sdf.parse(lastloaddate);	
				} catch (Exception e) {
					System.out.println("Date is not in correct format");
					generateResponseBean("PortCall", "PortCall date is in incorrect format, correct format is YYYY-MM-DD.","ERROR");
					e.printStackTrace();
					result=false;
				}
				
				break;
			}
			
		}
		for(PortCall portCall:ams.getVoyage().getPortCall()){
			if(portCall.isDischarge()==true){
				dischargeDate = portCall.getArriveDate();
			try{
				dichargedate1=sdf.parse(dischargeDate);
			} catch (Exception e) {
				System.out.println("Date is not in correct format");
				generateResponseBean("PortCall", "PortCall date is in incorrect format, correct format is YYYY-MM-DD.","ERROR");
				e.printStackTrace();
				result=false;
				break;
			}
				if(dichargedate1.compareTo(lastloaddate1)>=0 || dichargedate1.compareTo(lastloaddate1)==0 ){
					result=true;
				}else{
//					Response.setSource("data");
//					Response.setElement("PortCall");
//					Response.setCode("ERROR");
//					Response.setDescription("The arrival date of discharge port "+amsvoyageBean.getLocation()[portCall.getLocationIndex()]+" should not be less than last load port.");
					if((ams.getLocation().length) < portCall.getLocationIndex()){
						generateResponseBean("PortCall", "The LocationIndex '"+portCall.getLocationIndex()+"'in a PortCall is invalid", "ERROR");
						result=false;
						break;
					}else{
						generateResponseBean("PortCall", "The arrival date of discharge port '"+ams.getLocation()[portCall.getLocationIndex()-1].getName()+"' should not be less than last load port.","ERROR");
						result=false;
						break;
					}
				}
			}
		}
		for(PortCall portCallbean:objPortCallbean){
			PortDetailsBean objPortDetailsBean=new PortDetailsBean();
			for(Location locationbean:objLocationbean){
				if(locationbean.getIndex()==portCallbean.getLocationIndex()){
					objPortDetailsBean.setArrivalDate(portCallbean.getArriveDate());
					objPortDetailsBean.setSailingDate(portCallbean.getSailDate());
					objPortDetailsBean.setLoad(portCallbean.isLoad());
					objPortDetailsBean.setDischarge(portCallbean.isDischarge());
					objPortDetailsBean.setLastLoadPort(portCallbean.isLastLoad());
					objPortDetailsBean.setLocation(locationbean.getName());
					objPortDetailsBean.setIsAmsSent(false);
					int locationid=objLocationDAO.getLocationId(locationbean.getName(), loginScac);
					objPortDetailsBean.setLocationId(locationid);
					objPortDetailsBean.setTerminal("");
					}
				}
			objmPortDetailsBeans.add(objPortDetailsBean);
			}
		
		objVoyageBean.setObjmPortDetailsBeans(objmPortDetailsBeans);
		   }
	}

	private void populateVoyage(AMS ams) {
		//code to validate vessel exists
		String lloyadscode=voyagebean.getVessel();
		VesselBean objVesselBean=objVesselDAO.getDataXML(lloyadscode, loginScac);
		if(objVesselBean==null){
//			Source=Source+objmProperties.getProperty("OTHER_SOURCE");
//			element=element+objmProperties.getProperty("OTHER_ELEMENTV");
//			code=code+objmProperties.getProperty("OTHER_CODE");
//			description=description+objmProperties.getProperty("VESSEL_NOT_PRESENT_DESCIPTION").replaceAll("-", lloyadscode);
			generateResponseBean("Voyage", "vessel '"+ams.getVoyage().getVessel()+"'not present","ERROR");
			result=false;
		}else{
			objVoyageBean.setVesselName(objVesselBean.getVesselName());
			objVoyageBean.setVesselId(objVesselBean.getVesselId());
			
		}
		//code to validate voyage exists
		objVoyageBean.setCreatedUser("admin");
		objVoyageBean.setLoginScac(loginScac);
		objVoyageBean.setVoyage(voyagebean.getNumber());
	
		
		if(voyagebean.getCrewMembers()==null)
			objVoyageBean.setCrewMembers("");
		else
			objVoyageBean.setCrewMembers(voyagebean.getCrewMembers());
		if(voyagebean.getPassengers()==null)
			objVoyageBean.setPassengers("");
		else
			objVoyageBean.setPassengers(voyagebean.getPassengers());
		if(voyagebean.getReportNumber()==null)
			objVoyageBean.setReportNumber("");
		else
			objVoyageBean.setReportNumber(voyagebean.getReportNumber());
		if(voyagebean.getScac()==null)
			objVoyageBean.setScacCode("");
		else
			objVoyageBean.setScacCode(loginScac);

	}

	public boolean validateCountry(Location[] country) {
		result=true;
		LocationBean objmLocationBean=new LocationBean();
		for(Location locationbean:country){
			objmLocationBean=convertINXLLocationToAmsLocation(locationbean,objmLocationBean);
			//System.out.println("Location Code: "+objmLocationBean.getLocationCode()+"Country: "+objmLocationBean.getCountry());
			if(objLocationDAO.checkForCountryName(objmLocationBean.getCountry())){
				System.out.println("Country exist");
			}
		   else{
			  
				generateResponseBean("Country", "Country Code '"+objmLocationBean.getCountry()+"' does not exist.", "ERROR");
				result = false;
			}
		}
	
		return result;
	}
	
	public boolean ValidateUnCode(Location[] customCode) {
		
		for(Location locationbean:customCode){
			if(locationbean.getCustomsCode()==null){
				String customCodefromUNCode = objLocationDAO.locationCodeFromUnlocode(locationbean.getUnCode(),loginScac);
				locationbean.setCustomsCode(customCodefromUNCode);
				//locationbean.setName();
			}
			if(objLocationDAO.isForeignPort(locationbean.getCustomsCode())){
				
				locationbean.setIsCustomForeign(true);
			}
			else if(objLocationDAO.isDisctrictPort(locationbean.getCustomsCode())){
				locationbean.setIsCustomForeign(false);
			}
			else{
				generateResponseBean("CUSTOMCODE", "CustomCode '"+locationbean.getUnCode()+"' does not exist.", "ERROR");
				result = false;
			}
		}
		return result;
	}
	
	public boolean validateLocation(Location[] location) {
			
		LocationBean objmLocationBean=new LocationBean();
		for(Location locationbean:location){
			objmLocationBean=convertINXLLocationToAmsLocation(locationbean,objmLocationBean);
			if(objLocationDAO.checkForLocationName(objmLocationBean.getLocationName(),loginScac))
				System.out.println("Location already exist");
		   else{
			  
				if(objLocationDAO.insert(objmLocationBean).startsWith("Success")){
					result=true;
				}else{
					result=false;
					break;
				 }
			}
		}
		if(result)
			System.out.print("Location saved successfully...");
		else
			System.out.print("Error in Insert query...");
		return result;
	}

	
    public LocationBean convertINXLLocationToAmsLocation(Location locationbean, LocationBean objmLocationBean) {
		objmLocationBean.setLoginScac(loginScac);
		
		if(locationbean.getCustomsCode()==null)
			objmLocationBean.setLocationCode("");
		else
			objmLocationBean.setLocationCode(locationbean.getCustomsCode());
		if(locationbean.getName()==null)
			objmLocationBean.setLocationName("");
		else
			objmLocationBean.setLocationName(locationbean.getName());
		if(locationbean.getCountry()==null)
			objmLocationBean.setCountry("");
		else
			objmLocationBean.setCountry(locationbean.getCountry());
		
		if(locationbean.getUnCode()==null)
			objmLocationBean.setUnlocode("");
		else
			objmLocationBean.setUnlocode(locationbean.getUnCode());		
		
		if(locationbean.getType()==null)
			objmLocationBean.setLocationType("");
		else{
			if(locationbean.getType().equals("marine"))
				objmLocationBean.setLocationType("M");
		}
		if(locationbean.getProvidence()==null)
			objmLocationBean.setState("");
		else
			objmLocationBean.setState(locationbean.getProvidence());
	   objmLocationBean.setIsVoyageCreated(false);
	   objmLocationBean.setHoldAtLp("");
	   objmLocationBean.setCreatedUser("admin");
	   objmLocationBean.setCreatedDate("");
	   //objmLocationBean.setIsCustomForeign(false);
	   if(locationbean.getIsCustomForeign()==true)
			objmLocationBean.setIsCustomForeign(true);
	   else if(locationbean.getIsCustomForeign()==false)
			objmLocationBean.setIsCustomForeign(false);
		else
			System.out.println("Error");
       
    return objmLocationBean;
	
	}
    public void generateResponseBean(String tag,String Desc,String Code){
		Response objResponse=new Response();
		objResponse.setSource("data");
		objResponse.setElement(tag);
		objResponse.setCode(Code);
		objResponse.setDescription(Desc);
		objresponselist.add(objResponse);
		
	}
}
