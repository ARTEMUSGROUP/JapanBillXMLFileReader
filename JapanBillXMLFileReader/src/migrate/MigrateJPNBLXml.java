
package migrate;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mail.SendMail;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.NodeList;

import com.mysql.jdbc.StringUtils;
import com.wutka.jox.JOXBeanInputStream;
import com.wutka.jox.JOXBeanOutputStream;

import beans.amsBeans.CargoBean;
import beans.amsBeans.EquipmentBean;
import beans.amsBeans.NotifyBean;
import beans.amsBeans.BillDetailBean;
import beans.amsBeans.CustomerProfileBean;
import beans.amsBeans.LocationBean;
import beans.amsBeans.PackageBean;
import beans.amsBeans.VesselBean;
import beans.JPN.xmlBeans.AMSBean;
import beans.JPN.xmlBeans.AMSNotify;
import beans.JPN.xmlBeans.AMSResponse;
import beans.JPN.xmlBeans.Attribute;
import beans.JPN.xmlBeans.BL;
import beans.JPN.xmlBeans.Cargo;
import beans.JPN.xmlBeans.Equipment;
import beans.JPN.xmlBeans.Location;
import beans.JPN.xmlBeans.LocationRef;
import beans.JPN.xmlBeans.Package;
import beans.JPN.xmlBeans.Party;
import beans.JPN.xmlBeans.PartyRef;
import beans.JPN.xmlBeans.Response;
import dao.BillHeaderJpDAO;
import dao.CustomerProfileDAO;
import dao.LoadProperty;
import dao.LocationDAO;
import dao.PackageTypeDAO;
import dao.VesselDAO;
import dao.VoyageJpDao;

public class MigrateJPNBLXml extends LoadProperty{
	BillDetailBean objBillDetailBean=null;
	ArrayList<Response> objresponselist=new ArrayList<Response>();
	String loginScac; 
	BL blbean=null;
	String message="";
	String subject="";
	static String description="";
	static String code="";
	static String element="";
	static String Source="";
	boolean result=true;
	boolean mailResult=true;
	CustomerProfileDAO objCustomerProfileDAO=null;
	BillHeaderJpDAO objBillHeaderDAO=null;
	LocationDAO objLocationDAO=null;
	VesselDAO objVesselDAO=null;
	VoyageJpDao objmVoyageDAO=null;
	PackageTypeDAO objmPackageTypeDAO=null;
	Properties objmProperties=null;
	FileInputStream objmFileInputStream=null;
	JOXBeanInputStream joxIn=null;
	AMSBean amsbean=null;
	public void MigrateBill(File objmFile,String mainpath,String dbname ) throws Exception{
		try {
			objBillDetailBean=new BillDetailBean();
			objCustomerProfileDAO=new CustomerProfileDAO();
			objBillHeaderDAO=new BillHeaderJpDAO();
			objLocationDAO=new LocationDAO();
			objmVoyageDAO = new VoyageJpDao();
			objVesselDAO=new VesselDAO();
			objmPackageTypeDAO=new PackageTypeDAO();
			objBillHeaderDAO.setAutoCommit(false);
			//ByteArrayInputStream xmlData = new ByteArrayInputStream(xmlstr.getBytes());
			objmFileInputStream=new FileInputStream(objmFile);
			joxIn = new JOXBeanInputStream(objmFileInputStream);
			amsbean =(AMSBean) joxIn.readObject(AMSBean.class);
						
			readCargoDescription(amsbean,objmFile);
			loginScac=amsbean.getScac();
			objBillDetailBean.setLoginScac(loginScac);
			objBillDetailBean.setHblScac(loginScac);
			blbean = amsbean.getBl();
			if(validateCustomer(amsbean.getParty())){
				populateConsigneeShipper(amsbean);
				populateBillDetails(amsbean);
				populateEquipment(amsbean);
				populatePackage(amsbean);
				validateCargo(amsbean);
				if(objBillHeaderDAO.isExists(objBillDetailBean.getBillLadingNumber(), objBillDetailBean.getLoginScac())){
					generateResponseBean("BL", "Bill with bill of lading number '"+blbean.getNumber()+"' already exists","ERROR");
//					Response.setSource("data");
//					Response.setElement("BL");
//					Response.setCode("ERROR");
//					Response.setDescription("Bill with bill of lading number '"+blbean.getNumber()+"' already exists");
//					objresponselist.populateConsigneeShipperadd(Response);
					result=false;
				}else{
					if(result==true){
						convertToUpper();
						//beanPropertiesToUpperCase(objBillDetailBean);
						
						System.out.println("objBillDetailBean.getNotifyName() :"+objBillDetailBean.getNotifyName());
					    if(objBillHeaderDAO.insert(objBillDetailBean).equals("Success")){
					    	generateResponseBean("BL", "Bill '" +blbean.getNumber()+ "' Saved succesfully","INFO");
//					    	Response.setSource("internal");
//							Response.setElement("");
//							Response.setCode("INFO");
//							Response.setDescription(" bill '" +blbean.getNumber()+ "' Save succesfully");
//							objresponselist.add(Response);
							result=true;
						}else{
							if(objBillDetailBean.getShipperName()==null){
								generateResponseBean("PartyRef"," The Shipper does not exist in the BL '"+blbean.getNumber()+"', You must add Shipper in the BL. ","ERROR");
							}
							if(objBillDetailBean.getConsigneeName()==null){
								generateResponseBean("PartyRef"," The Consignee does not exist in the BL '"+blbean.getNumber()+"', You must add consignee in the BL. ","ERROR");
							}
							if(objBillDetailBean.getNotifyName()==null || objBillDetailBean.getNotifyName().equals("")){
								generateResponseBean("PartyRef"," The Notify does not exist in the BL '"+blbean.getNumber()+"', You must add Notify in the BL. ","ERROR");
							}
							result=false;
						}
					}
				}
			}
//			try{
//				if(result){
//					String s="cmd.exe /c start a.bat \""+objmProperties.getProperty("CLIENT_PATH")+objmFile.getName()+"\" \""+objmProperties.getProperty("INPUT_PATH")+objmFile.getName()+"\"";
//					s=s+" \""+objmProperties.getProperty("INPUT_PATH")+objmFile.getName()+"\"";
//					Runtime.getRuntime().exec(s);
//					result=true;
//				}else{
//					String s="cmd.exe /c start a.bat \""+objmProperties.getProperty("CLIENT_PATH")+objmFile.getName()+"\" \""+objmProperties.getProperty("ERROR_PATH")+objmFile.getName()+"\"";
//					s=s+" \""+objmProperties.getProperty("INPUT_PATH")+objmFile.getName()+"\"";
//					Runtime.getRuntime().exec(s);
//					result=false;
//				}
//		     }catch (Exception e) {
//				e.printStackTrace();
//			}
		}catch (Exception e) {
			e.printStackTrace();
			result=false;
			amsbean.setMessage("Exception while processing the file");
		}finally{
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
				objCustomerProfileDAO.commitTransaction(result);
				objBillHeaderDAO.commitTransaction(result);
				objLocationDAO.commit(result);
				FileWriter fr = null;
				Writer br = null;
				if(!amsbean.getMessage().equals("")){
//					Response.setSource("DATA");
//					Response.setElement("");
//					Response.setCode("ERROR");
					generateResponseBean(" ", amsbean.getMessage(),"ERROR");
					//Response.setDescription(amsbean.getMessage());
				}
				 try{
					AMSResponse objAmsResponse=new AMSResponse();
					Response[] x=new Response[0];
					x=(Response[]) objresponselist.toArray(x);
					objAmsResponse.setResponse(x);
					String xmlStr=objAmsResponse.toXML();
					//fr= new FileWriter(mainpath+"RESPONSE/"+objmFile.getName());
			   	    //----While putting on the server use following code---//
			   	    fr= new FileWriter(mainpath+"RESPONSE\\"+objmFile.getName());
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
						//objmSendMail.sendMail(dbname,mainpath+"RESPONSE/"+objmFile.getName(),objmFile.getName()," JAPAN Artemus :Bill Response File."+"("+loginScac+")");
					    //While putting on the server use following code
					    objmSendMail.sendMail(dbname,mainpath+"RESPONSE\\"+objmFile.getName(),objmFile.getName()," JAPAN Artemus :Bill Response File"+"("+loginScac+")");
					    if(result)
					    	//moveFile(objmFile,mainpath+"PROCESSED/"+objmFile.getName());
					    	moveFile(objmFile,mainpath+"PROCESSED\\"+objmFile.getName());
					    else
					    	//moveFile(objmFile,mainpath+"ERROR/"+objmFile.getName());
					    	moveFile(objmFile,mainpath+"ERROR\\"+objmFile.getName());
					}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
	}
	
	private void readCargoDescription(AMSBean amsbean, File objmFile){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			Cargo objmList[]=amsbean.getBl().getCargo();
			DocumentBuilder db = dbf.newDocumentBuilder();
			NodeList d=db.parse(objmFile).getElementsByTagName("Cargo");
			for(int i=0;i<d.getLength();i++){
				objmList[i].setDescription(d.item(i).getTextContent());
			}
		}catch(Exception e) {
			e.printStackTrace();
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
	
	
	public void populateBillDetails(AMSBean amsBean) throws Exception{
		ArrayList<NotifyBean> objmNotifyBeans=new ArrayList<NotifyBean>(); 
		objBillDetailBean.setBillLadingNumber(blbean.getNumber());
		//code to validate vessel exists
		String lloyadscode =blbean.getVessel();
		VesselBean objVesselBean=objVesselDAO.getDataXML(lloyadscode, loginScac);
		if(objVesselBean==null){
		//	Response.setSource("data");
//			Response.setElement("BL");
//			Response.setCode("ERROR");
//			Response.setDescription("vessel with name '"+amsbean.getBl().getVessel()+"' does not Exist");
//			objresponselist.add(Response);
			generateResponseBean("BL", "vessel with name '"+amsbean.getBl().getVessel()+"' does not Exist","ERROR");
			result=false;
		}else{
			objBillDetailBean.setVesselName(objVesselBean.getVesselName());
			objBillDetailBean.setCarrierScac(objVesselBean.getUsaScacCode());
		}
		//code to validate voyage exists
		int voyageId=objmVoyageDAO.getVoyageId(loginScac,objVesselBean.getVesselId(),blbean.getVoyage());
		if(voyageId==0){
//			Response.setSource("data");
//			Response.setElement("BL");
//			Response.setCode("ERROR");
//			Response.setDescription("voyage '"+amsbean.getBl().getVoyage()+"' does not Exist");
//			objresponselist.add(Response);
			generateResponseBean("BL", "voyage '"+amsbean.getBl().getVoyage()+"' does not Exist","ERROR");
			result=false;
			//throw ex;
		}else{
			objBillDetailBean.setVoyageNumber(blbean.getVoyage());
			objBillDetailBean.setVoyageId(voyageId);
			objBillDetailBean.setVesselName(objVesselBean.getVesselName()+"-"+blbean.getVoyage());
		}
		//validate Place of receipt,Load Port,Discharge port
		
		if(blbean.getHblScac()==null||blbean.getHblScac().equals("")){
			objBillDetailBean.setHblScac(loginScac);
			
		}else{
			if(blbean.getHblScac().length()==4){
				objBillDetailBean.setHblScac(blbean.getHblScac());
			}else{
				generateResponseBean("BL", "HBL SCAC '"+blbean.getHblScac()+"' must have 4 characters","ERROR");
				result=false;
			}
		}
		if(blbean.getBillType()==null)
			objBillDetailBean.setBillType("");
		else
			objBillDetailBean.setBillType(blbean.getBillType());
		if(blbean.getBillType()==null)
			objBillDetailBean.setBillType("");
		else
			objBillDetailBean.setBillType(blbean.getBillType());
		if(blbean.getMasterBillNo()==null)
			objBillDetailBean.setMasterBill("");
		else
			objBillDetailBean.setMasterBill(blbean.getMasterBillNo());
		if(blbean.getMasterBillScac()==null)
			objBillDetailBean.setScacBill("");
		else
			objBillDetailBean.setScacBill(blbean.getMasterBillScac());
		if(blbean.getMoveType()==null)
			objBillDetailBean.setMoveType("");
		else
			objBillDetailBean.setMoveType(blbean.getMoveType());
		if(blbean.getNvoType()==null)
			objBillDetailBean.setNvoType("");
		else
			objBillDetailBean.setNvoType(blbean.getNvoType());
		populateBillPort(amsBean,voyageId);
		AMSNotify[] objmNotifybean=blbean.getAmsNotify();
		if(objmNotifybean!=null){
		for(AMSNotify amsNotifybean:objmNotifybean){
			NotifyBean objmNotifyBean=new NotifyBean();
			if(amsNotifybean.getScac()==null)
				objmNotifyBean.setScacCode("");
			else
				objmNotifyBean.setScacCode(amsNotifybean.getScac());
		objmNotifyBeans.add(objmNotifyBean);
		}
		objBillDetailBean.setObjmNotifyBean(objmNotifyBeans);
		//default value of billdetailbean
		objBillDetailBean.setBillStatus("COMPLETE");
		objBillDetailBean.setOriginCountry("");
		objBillDetailBean.setCreatedUser("admin");
		objBillDetailBean.setCarnetNumber("");
		objBillDetailBean.setCarnetCountry("");
		objBillDetailBean.setShipmentSubType("");
		objBillDetailBean.setEstimatedValue(1);
		objBillDetailBean.setEstimatedQuantity(2);
		objBillDetailBean.setUnitOfMeasure("");
		objBillDetailBean.setEstimatedWeight(25);
		objBillDetailBean.setWeightQualifier("");
		objBillDetailBean.setIsAmendment(false);
		objBillDetailBean.setIsIsfSent(false);
		objBillDetailBean.setIsValidAms(false);
		objBillDetailBean.setIsValidIsf(false);	
		objBillDetailBean.setAmsErrorDescription("");
		objBillDetailBean.setIsfErrorDescription("");
		}
		else{
			//generateResponseBean("AMSNotify", Desc, lloyadscode);
			generateResponseBean("AMSNotify", "AMS Notify SCAC has not been entered for the Bill '" +blbean.getNumber()+ "' ","ERROR");
			result = false;
		}
	}
	
	public boolean validateCustomer(Party[] objmPartyBean) throws Exception{
		CustomerProfileBean objmCustomerProfileBean=null;
		for(Party partybean:objmPartyBean){
			objmCustomerProfileBean = new CustomerProfileBean();
			objmCustomerProfileBean=convertPARTYBeanToCustomerProfileBean(partybean, objmCustomerProfileBean);
			objCustomerProfileDAO.isExistXMLCustomer(objmCustomerProfileBean);
			if(objmCustomerProfileBean.getCustomerId()!=0){
				System.out.print("Customer Exist");
				result=true;
			}else{
				if(objCustomerProfileDAO.insert(objmCustomerProfileBean).startsWith("Success")){
					result=true;
				}else{
					result=false;
					break;
				}
			}
		 }
		if(result)
			System.out.print("customers saved successfully...");
		else
			System.out.print("Error in Insert query...");
		return result;
	}
	
	
	public void populateConsigneeShipper(AMSBean amsbean) throws Exception{
		 objBillDetailBean.setBookingPartyName("");
		 objBillDetailBean.setBookingPartyAddress("");
		 objBillDetailBean.setIsBookingParty(false);
		 objBillDetailBean.setSellerAddress("");
		 objBillDetailBean.setSellerName("");
		 objBillDetailBean.setIsSeller(false);
		 objBillDetailBean.setStufferName("");
		 objBillDetailBean.setStufferAddress("");
		 objBillDetailBean.setIsStuffer(false);
		 objBillDetailBean.setConsolidatorAddress("");
		 objBillDetailBean.setIsConsolidator(false);
		 objBillDetailBean.setConsolidatorName("");
		 objBillDetailBean.setIsImporter(false);
		 objBillDetailBean.setImporterName("");
		 objBillDetailBean.setImporterAddress("");
		 objBillDetailBean.setBuyerAddress("");
		 objBillDetailBean.setBuyerName("");
		 objBillDetailBean.setIsBuyer(false);
		 objBillDetailBean.setShipToName("");
		 objBillDetailBean.setShipToAddress("");
		 objBillDetailBean.setIsShipTo(false);
		 objBillDetailBean.setNotifyName("");
		 objBillDetailBean.setNotifyAddress("");
		 objBillDetailBean.setIsNotify(false);
		String Address="";
		CustomerProfileBean objmCustomerProfileBean=new CustomerProfileBean();
		PartyRef[] objpPartyRefbean=blbean.getPartyRef();
		for(PartyRef partyRefbean:objpPartyRefbean){
			int index=partyRefbean.getIndex();
			objmCustomerProfileBean=getCustomerInfoID(amsbean,index);
			if(objmCustomerProfileBean!=null){
				if(partyRefbean.getName().equals("Shipper")){
					objBillDetailBean.setShipperName(objmCustomerProfileBean.getCustomerName());
					Address=objmCustomerProfileBean.getAddress1()+"\r\n";
					if(!objmCustomerProfileBean.getAddress2().equals(""))
						Address=Address+objmCustomerProfileBean.getAddress2()+"\r\n";
					Address=Address+objmCustomerProfileBean.getCity()+" "+objmCustomerProfileBean.getState()+" "+objmCustomerProfileBean.getZipCode()+" "+objmCustomerProfileBean.getCountry();
					objBillDetailBean.setShipperAddress(Address);
					objBillDetailBean.setShipperId(objmCustomerProfileBean.getCustomerId());
					
					if(objmCustomerProfileBean.getPhoneNo()!=null || !objmCustomerProfileBean.getPhoneNo().equals(""))
						objBillDetailBean.setShipperPhone(objmCustomerProfileBean.getPhoneNo());
					else{
						generateResponseBean("PARTY", "The customer '"+objmCustomerProfileBean.getCustomerName()+"' must have Phone Number","ERROR");
						result=false;
					}
				}
				else if(partyRefbean.getName().equals("Consignee")){
					objBillDetailBean.setConsigneeName(objmCustomerProfileBean.getCustomerName());
					Address=objmCustomerProfileBean.getAddress1()+"\r\n";
					if(!objmCustomerProfileBean.getAddress2().equals(""))
						Address=Address+objmCustomerProfileBean.getAddress2()+"\r\n";
					Address=Address+objmCustomerProfileBean.getCity()+" "+objmCustomerProfileBean.getState()+" "+objmCustomerProfileBean.getZipCode()+" "+objmCustomerProfileBean.getCountry();
					objBillDetailBean.setConsigneeAddress(Address);
					objBillDetailBean.setConsigneeId(objmCustomerProfileBean.getCustomerId());
					
					if(objmCustomerProfileBean.getPhoneNo()!=null || !objmCustomerProfileBean.getPhoneNo().equals(""))
						objBillDetailBean.setConsigneePhone(objmCustomerProfileBean.getPhoneNo());
					else{
						generateResponseBean("PARTY", "The customer '"+objmCustomerProfileBean.getCustomerName()+"' must have Phone Number","ERROR");
						result=false;
					}
				 }
				else if(partyRefbean.getName().equals("Notify")){
						objBillDetailBean.setNotifyName(objmCustomerProfileBean.getCustomerName());
						Address=objmCustomerProfileBean.getAddress1()+"\r\n";
						if(!objmCustomerProfileBean.getAddress2().equals(""))
							Address=Address+objmCustomerProfileBean.getAddress2()+"\r\n";
						Address=Address+objmCustomerProfileBean.getCity()+" "+objmCustomerProfileBean.getState()+" "+objmCustomerProfileBean.getZipCode()+" "+objmCustomerProfileBean.getCountry();
						objBillDetailBean.setNotifyAddress(Address);
						objBillDetailBean.setNotifyId(objmCustomerProfileBean.getCustomerId());
						
						if(objmCustomerProfileBean.getPhoneNo()!=null || !objmCustomerProfileBean.getPhoneNo().equals(""))
							objBillDetailBean.setNotifyPhone(objmCustomerProfileBean.getPhoneNo());
						else{
							generateResponseBean("PARTY", "The Customer '"+objmCustomerProfileBean.getCustomerName()+"' must have Phone Number","ERROR");
							result=false;
						}
				}
				else if(partyRefbean.getName().equals("Ship To")){
					objBillDetailBean.setShipToName(objmCustomerProfileBean.getCustomerName());
					Address=objmCustomerProfileBean.getAddress1()+"\r\n";
					if(!objmCustomerProfileBean.getAddress2().equals(""))
						Address=Address+objmCustomerProfileBean.getAddress2()+"\r\n";
					Address=Address+objmCustomerProfileBean.getCity()+" "+objmCustomerProfileBean.getState()+" "+objmCustomerProfileBean.getZipCode()+" "+objmCustomerProfileBean.getCountry();
					objBillDetailBean.setShipToAddress(Address);
					objBillDetailBean.setShipToId(objmCustomerProfileBean.getCustomerId());
				}
				else if(partyRefbean.getName().equals("Booking Party")){
					objBillDetailBean.setBookingPartyName(objmCustomerProfileBean.getCustomerName());
					Address=objmCustomerProfileBean.getAddress1()+"\r\n";
					if(!objmCustomerProfileBean.getAddress2().equals(""))
						Address=Address+objmCustomerProfileBean.getAddress2()+"\r\n";
					Address=Address+objmCustomerProfileBean.getCity()+" "+objmCustomerProfileBean.getState()+" "+objmCustomerProfileBean.getZipCode()+" "+objmCustomerProfileBean.getCountry();
					objBillDetailBean.setBookingPartyAddress(Address);
					objBillDetailBean.setBookingPartyId(objmCustomerProfileBean.getCustomerId());
				}
				else if(partyRefbean.getName().equals("Seller")){
					objBillDetailBean.setSellerName(objmCustomerProfileBean.getCustomerName());
					Address=objmCustomerProfileBean.getAddress1()+"\r\n";
					if(!objmCustomerProfileBean.getAddress2().equals(""))
						Address=Address+objmCustomerProfileBean.getAddress2()+"\r\n";
					Address=Address+objmCustomerProfileBean.getCity()+" "+objmCustomerProfileBean.getState()+" "+objmCustomerProfileBean.getZipCode()+" "+objmCustomerProfileBean.getCountry();
					objBillDetailBean.setSellerAddress(Address);
					objBillDetailBean.setSellerId(objmCustomerProfileBean.getCustomerId());
				}
				else if(partyRefbean.getName().equals("Consolidator")){
					objBillDetailBean.setConsolidatorName(objmCustomerProfileBean.getCustomerName());
					Address=objmCustomerProfileBean.getAddress1()+"\r\n";
					if(!objmCustomerProfileBean.getAddress2().equals(""))
						Address=Address+objmCustomerProfileBean.getAddress2()+"\r\n";
					Address=Address+objmCustomerProfileBean.getCity()+" "+objmCustomerProfileBean.getState()+" "+objmCustomerProfileBean.getZipCode()+" "+objmCustomerProfileBean.getCountry();
					objBillDetailBean.setConsolidatorAddress(Address);
					objBillDetailBean.setConsolidatorId(objmCustomerProfileBean.getCustomerId());
				}
				else if(partyRefbean.getName().equals("Stuffer")){
					objBillDetailBean.setStufferName(objmCustomerProfileBean.getCustomerName());
					Address=objmCustomerProfileBean.getAddress1()+"\r\n";
					if(!objmCustomerProfileBean.getAddress2().equals(""))
						Address=Address+objmCustomerProfileBean.getAddress2()+"\r\n";
					Address=Address+objmCustomerProfileBean.getCity()+" "+objmCustomerProfileBean.getState()+" "+objmCustomerProfileBean.getZipCode()+" "+objmCustomerProfileBean.getCountry();
					objBillDetailBean.setStufferAddress(Address);
					objBillDetailBean.setStufferId(objmCustomerProfileBean.getCustomerId());
				}
				else if(partyRefbean.getName().equals("Importer")){
					objBillDetailBean.setImporterName(objmCustomerProfileBean.getCustomerName());
					Address=objmCustomerProfileBean.getAddress1()+"\r\n";
					if(!objmCustomerProfileBean.getAddress2().equals(""))
						Address=Address+objmCustomerProfileBean.getAddress2()+"\r\n";
					Address=Address+objmCustomerProfileBean.getCity()+" "+objmCustomerProfileBean.getState()+" "+objmCustomerProfileBean.getZipCode()+" "+objmCustomerProfileBean.getCountry();
					objBillDetailBean.setImporterAddress(Address);
					objBillDetailBean.setImporterId(objmCustomerProfileBean.getCustomerId());
				}
				else if(partyRefbean.getName().equals("Buyer")){
					objBillDetailBean.setBuyerName(objmCustomerProfileBean.getCustomerName());
					Address=objmCustomerProfileBean.getAddress1()+"\r\n";
					if(!objmCustomerProfileBean.getAddress2().equals(""))
						Address=Address+objmCustomerProfileBean.getAddress2()+"\r\n";
					Address=Address+objmCustomerProfileBean.getCity()+" "+objmCustomerProfileBean.getState()+" "+objmCustomerProfileBean.getZipCode()+" "+objmCustomerProfileBean.getCountry();
					objBillDetailBean.setBuyerAddress(Address);
					objBillDetailBean.setBuyerId(objmCustomerProfileBean.getCustomerId());
				}
		}else{
//						Response.setSource("data");
//						Response.setElement("PartyRef");
//						Response.setCode("ERROR");
//						Response.setDescription("Invalid Party code  '"+index+"' ");
//						objresponselist.add(Response);
						
			       generateResponseBean("PartyRef"," Invalid Party code  '"+index+"' ","ERROR");
			       result=false;
					//throw ex;
			}
		}
		if(objBillDetailBean.getConsigneeId()==objBillDetailBean.getNotifyId())
			objBillDetailBean.setIsNotify(true);
		else
			objBillDetailBean.setIsNotify(false);
		
		if(objBillDetailBean.getConsigneeId()==objBillDetailBean.getImporterId())
			objBillDetailBean.setIsImporter(true);
		else
			objBillDetailBean.setIsImporter(false);
		
		if(objBillDetailBean.getConsigneeId()==objBillDetailBean.getBuyerId())
			objBillDetailBean.setIsBuyer(true);
		else
			objBillDetailBean.setIsBuyer(false);
		
		if(objBillDetailBean.getConsigneeId()==objBillDetailBean.getShipToId())
			objBillDetailBean.setIsShipTo(true);
		else
			objBillDetailBean.setIsShipTo(false);
		
		if(objBillDetailBean.getShipperId()==objBillDetailBean.getBookingPartyId())
			objBillDetailBean.setIsBookingParty(true);
		else
			objBillDetailBean.setIsBookingParty(false);
		
		if(objBillDetailBean.getShipperId()==objBillDetailBean.getSellerId())
			objBillDetailBean.setIsSeller(true);
		else
			objBillDetailBean.setIsSeller(false);
		
		if(objBillDetailBean.getShipperId()==objBillDetailBean.getConsolidatorId())
			objBillDetailBean.setIsConsolidator(true);
		else
			objBillDetailBean.setIsConsolidator(false);
		
		if(objBillDetailBean.getShipperId()==objBillDetailBean.getStufferId())
			objBillDetailBean.setIsStuffer(true);
		else
			objBillDetailBean.setIsStuffer(false);
	}
	public CustomerProfileBean getCustomerInfoID(AMSBean amsBean, int index)throws Exception {
		CustomerProfileBean objmCustomerProfileBean=null;
		Party[] objmPartyBean=amsBean.getParty();
		for (Party partybean:objmPartyBean){
			if(partybean.getIndex()==index){
				objmCustomerProfileBean=new CustomerProfileBean();
				objmCustomerProfileBean=convertPARTYBeanToCustomerProfileBean(partybean, objmCustomerProfileBean);
				objCustomerProfileDAO.isExistXMLCustomer(objmCustomerProfileBean);
			}
		}
		return objmCustomerProfileBean;
		
	}
	
	public CustomerProfileBean convertPARTYBeanToCustomerProfileBean(Party partybean, CustomerProfileBean objmCustomerProfileBean) {
		if(partybean.getName()==null)
			objmCustomerProfileBean.setCustomerName("");
		else
			objmCustomerProfileBean.setCustomerName(partybean.getName());
		if(partybean.getAddress1()==null)
			 objmCustomerProfileBean.setAddress1("");
		else
			objmCustomerProfileBean.setAddress1(partybean.getAddress1());
		if(partybean.getAddress2()==null)
			objmCustomerProfileBean.setAddress2("");
		else
			objmCustomerProfileBean.setAddress2(partybean.getAddress2());
		if(partybean.getCountry()==null)
			objmCustomerProfileBean.setCountry("");
		else
		 objmCustomerProfileBean.setCountry(partybean.getCountry());
		if(partybean.getCity()==null)
			 objmCustomerProfileBean.setCity("");
		else
			objmCustomerProfileBean.setCity(partybean.getCity());
		if(partybean.getProvidence()==null)
			objmCustomerProfileBean.setState("");
		else
			objmCustomerProfileBean.setState(partybean.getProvidence());
		if(partybean.getPostCode()==null)
			 objmCustomerProfileBean.setZipCode("");
	    else
			objmCustomerProfileBean.setZipCode(partybean.getPostCode());
		
		if(partybean.getPhoneNumber()==null)
			objmCustomerProfileBean.setPhoneNo("");
	    else
	    	objmCustomerProfileBean.setPhoneNo(partybean.getPhoneNumber());
		
	 objmCustomerProfileBean.setLoginScac(loginScac);
	 objmCustomerProfileBean.setAddressType("main");
	 objmCustomerProfileBean.setCountryOfIssuance("");
	 
	 objmCustomerProfileBean.setFaxNo("");
	 objmCustomerProfileBean.setDob("");
	 if(partybean.getEntityType()==null)
		 objmCustomerProfileBean.setEntityType("");
	 else
		 objmCustomerProfileBean.setEntityType(partybean.getEntityType());
	 if(partybean.getEntityNumber()==null)
		 objmCustomerProfileBean.setEntityNumber("");
	 else
		 objmCustomerProfileBean.setEntityNumber(partybean.getEntityNumber());
	 
	 objmCustomerProfileBean.setCreatedUser("admin");
	 objmCustomerProfileBean.setCreatedDate("");
	return objmCustomerProfileBean;
	}
	public void populateBillPort(AMSBean amsbean, int voyageId ) throws Exception{
		LocationBean objmLocationBeans=new LocationBean();
		Location[] objmlLocationBean=amsbean.getLocation();
		LocationRef[] objmLocationRefbean=blbean.getLocationRef();
		for(LocationRef locationRefbean:objmLocationRefbean){
			for(Location locationbean:objmlLocationBean){
				if(locationRefbean.getIndex()==locationbean.getIndex()){
											
					if(locationRefbean.getName().toLowerCase().equals("discharge")){
							if(locationbean.getUnCode()!=null){
								System.out.println("locationbean.getUnCode(): "+ locationbean.getUnCode());
								String locationCodefromUNCode = objLocationDAO.locationCodeFromUnlocode(locationbean.getUnCode(),loginScac);
								System.out.println("locationCodefromUNCode: "+ locationCodefromUNCode);
								objmLocationBeans=objLocationDAO.getLocationDataFromUnCode(voyageId, locationCodefromUNCode, "Discharge");
								if(objmLocationBeans!=null){
									objBillDetailBean.setDischargePort(objmLocationBeans.getLocationCode());
								}else{								
									String locationName = objLocationDAO.locationNameFromName(locationbean.getName(),loginScac);
									objmLocationBeans=objLocationDAO.getLocationData(voyageId, locationName, "Discharge");
									if(objmLocationBeans!=null){
										objBillDetailBean.setDischargePort(objmLocationBeans.getLocationCode());									
									}else{
										generateResponseBean("LocationRef", "Location '"+locationbean.getUnCode()+"' does not exist for the Voyage '"+blbean.getVoyage()+"'.","ERROR");
										result=false;
									}
										
								}
							}else{								
								String locationName = objLocationDAO.locationNameFromName(locationbean.getName(),loginScac);
								objmLocationBeans=objLocationDAO.getLocationData(voyageId, locationName, "Discharge");
								if(objmLocationBeans!=null){
									objBillDetailBean.setDischargePort(objmLocationBeans.getLocationCode());									
								}else{
									generateResponseBean("LocationRef", "Location '"+locationbean.getName()+"' does Not Exist","ERROR");
									result=false;
								}
									
							}
						}
							
						if(locationRefbean.getName().toLowerCase().equals("loading")){
							if(locationbean.getUnCode()!=null){
								String locationCodefromUNCode = objLocationDAO.locationCodeFromUnlocode(locationbean.getUnCode(),loginScac);
								objmLocationBeans=objLocationDAO.getLocationDataFromUnCode(voyageId, locationCodefromUNCode, "Loading");
								if(objmLocationBeans!=null){
									objBillDetailBean.setLoadPort(objmLocationBeans.getLocationCode());
								}else{								
									String locationName = objLocationDAO.locationNameFromName(locationbean.getName(),loginScac);
									objmLocationBeans=objLocationDAO.getLocationData(voyageId, locationName, "Loading");
									if(objmLocationBeans!=null){
										objBillDetailBean.setLoadPort(objmLocationBeans.getLocationCode());									
									}else{
										//generateResponseBean("LocationRef", "Location '"+locationbean.getUnCode()+"' does Not Exist","ERROR");
										generateResponseBean("LocationRef", "Location '"+locationbean.getUnCode()+"' does not exist for the Voyage '"+blbean.getVoyage()+"'.","ERROR");
										result=false;
									}
										
								}
							
							}
							else{								
								String locationName = objLocationDAO.locationNameFromName(locationbean.getName(),loginScac);
								objmLocationBeans=objLocationDAO.getLocationData(voyageId, locationName, "Loading");
								if(objmLocationBeans!=null){
									objBillDetailBean.setLoadPort(objmLocationBeans.getLocationCode());									
								}else{
									generateResponseBean("LocationRef", "Location '"+locationbean.getName()+"' does Not Exist","ERROR");
									result=false;
								}
									
							}
							
						}
						
						if(locationRefbean.getName().toLowerCase().equals("receipt"))
							objBillDetailBean.setReceiptPlace(locationbean.getName());
								
						if(locationRefbean.getName().toLowerCase().equals("delivery"))
							objBillDetailBean.setDeliveryPlace(locationbean.getName());
				}
			}
		}
	}	
	public void populateEquipment(AMSBean amsbean) throws Exception{
		Equipment[] objmEquipmentsbean=blbean.getEquipment();
		ArrayList<EquipmentBean> objmEquipmentBeans=new ArrayList<EquipmentBean>();
		for(Equipment eqipackagebean:objmEquipmentsbean){
			EquipmentBean objEquipmentBean=new EquipmentBean();
			if(eqipackagebean.getEquipmentNumber()==null)
				objEquipmentBean.setEquipment("");
			else
				objEquipmentBean.setEquipment(eqipackagebean.getEquipmentNumber());
			if(eqipackagebean.getType()==null)
				objEquipmentBean.setSizeType("");
			else
				objEquipmentBean.setSizeType(eqipackagebean.getType());
			
			String SEAL1=eqipackagebean.getSeals();
			if(SEAL1!=null){
				String[] SEAL=SEAL1.split(",");
				if(SEAL!=null)
					for(int i=0;i<SEAL.length;i++)
						if(SEAL[i]==null)
							SEAL[i]="";
				objEquipmentBean.setSeal(SEAL);
			}
			objmEquipmentBeans.add(objEquipmentBean);	
		}
		objBillDetailBean.setObjmEquipmentBean(objmEquipmentBeans);
	}
	
	public void populatePackage(AMSBean amsbean) throws Exception{
		ArrayList<PackageBean> objmPackageBeans=new ArrayList<PackageBean>();
		Equipment[] objmEquipmentsbean=blbean.getEquipment();
		Package[] objmPackagesbean=blbean.getPackage();
		boolean found =false;
		
		for(Equipment eqipackageBn:objmEquipmentsbean){
			int index = eqipackageBn.getIndex();
			for(Package packageBn:objmPackagesbean){
				if(index == packageBn.getEquipmentIndex()){
					//
					//
					found=true;
					break;
				}	
			}
			
		}
		if(!found){
			generateResponseBean("Package", "Equipment Does not have package ","ERROR");
	    	result=false;
		}
		
		if(result == true){
		for(Package packagebean:objmPackagesbean){
			for(Equipment eqipackagebean:objmEquipmentsbean){
				if(packagebean.getEquipmentIndex()==eqipackagebean.getIndex()){
					PackageBean objPackageBean=new PackageBean();
					objPackageBean.setPackageEquipment(eqipackagebean.getEquipmentNumber());
					if(packagebean.getMarks()==null)
						objPackageBean.setMarks("");
					else
						objPackageBean.setMarks(packagebean.getMarks());
					if(packagebean.getPieceCount()==null)
						objPackageBean.setPieces("");
					else
						objPackageBean.setPieces(packagebean.getPieceCount());
					if(packagebean.getPackages()==null)
						objPackageBean.setPackages("");
					else{
						PackageBean objmPackageBean=new PackageBean();
						objmPackageBean=objmPackageTypeDAO.getPackageType(packagebean.getPackages());
						if(objmPackageBean.getPackages()==null){
//							Response.setSource("data");
//							Response.setElement("Package");
//							Response.setCode("ERROR");
//							Response.setDescription("Not a Valid Package Type, Process Aborted ");
//							objresponselist.add(Response);
//							result=false;
							generateResponseBean("Package","Not a Valid Package Type, Process Aborted ","ERROR");
							//throw ex;
							result=false;
						}else
						objPackageBean.setPackages(objmPackageBean.getPackages());
					   }
					Attribute[] objmAttributesbean=packagebean.getAttribute();
				   for(Attribute attributebean:objmAttributesbean){
						//weight
						if(attributebean.getType()!=null){
							if(!attributebean.getType().equals("Ms. (M)")){
							 if(attributebean.getType().equals("Wt. (M)")) {
								if(attributebean.getValue()==null)
									objPackageBean.setWtm(Double.parseDouble("0.0"));
								else
									objPackageBean.setWtm(Double.parseDouble(attributebean.getValue()));
								if(attributebean.getUnits()!=null)
									objPackageBean.setWtmUnit(attributebean.getUnits());
								objPackageBean.setWti(Double.parseDouble("0.0"));
								objPackageBean.setWtiUnit("");
							}else{
								if(attributebean.getValue()==null)
									objPackageBean.setWti(Double.parseDouble("0.0"));
								else
									objPackageBean.setWti(Double.parseDouble(attributebean.getValue()));
								if(attributebean.getUnits()!=null)
									objPackageBean.setWtiUnit(attributebean.getUnits());
								objPackageBean.setWtm(Double.parseDouble("0.0"));
								objPackageBean.setWtmUnit("");
								}
						  }
						    if(!attributebean.getType().equals("Wt. (M)")){
							 if(attributebean.getType().equals("Ms. (M)")){
								if(attributebean.getValue()==null){
									objPackageBean.setMsm(Double.parseDouble("0.0"));
								}else{
									objPackageBean.setMsm(Double.parseDouble(attributebean.getValue()));
								}
								if(attributebean.getUnits()!=null)
									objPackageBean.setMsmUnit(attributebean.getUnits());
								objPackageBean.setMsi(Double.parseDouble("0.0"));
								objPackageBean.setMsiUnit("");
							}else{
								if(attributebean.getValue()==null){
									objPackageBean.setMsi(Double.parseDouble("0.0"));
								}else{
									objPackageBean.setMsi(Double.parseDouble(attributebean.getValue()));
								}
								if(attributebean.getUnits()!=null)
									//System.out.println("unitttt :"+attributebean.getUnits().substring(0, 2));
									objPackageBean.setMsiUnit(attributebean.getUnits());
								objPackageBean.setMsm(Double.parseDouble("0.0"));
								objPackageBean.setMsmUnit("");
							}
						}
						}
						objPackageBean.setLength(0.0);
						objPackageBean.setLengthUnit("");
						objPackageBean.setWidth(0.0);
						objPackageBean.setWidthUnit("");
						objPackageBean.setHeight(0.0);
						objPackageBean.setHeightUnit("");
						objPackageBean.setSet(0.0);
						objPackageBean.setSetUnit("");
						objPackageBean.setMin(0.0);
						objPackageBean.setMinUnit("");
						objPackageBean.setMax(0.0);
						objPackageBean.setMaxUnit("");
						objPackageBean.setVents(0.0);
						objPackageBean.setVentsUnit("");
						objPackageBean.setDrainage(0.0);
						objPackageBean.setDrainageUnit("");   
					}
				   objmPackageBeans.add(objPackageBean);
				   
			     }
//				else{
////			    	    Response.setSource("data");
////						Response.setElement("Package");
////						Response.setCode("ERROR");
////						Response.setDescription("Invalid Package index  for equipment '"+packagebean.getEquipmentIndex()+"' " );
////						objresponselist.add(Response);
////						result=false;
//			    	 generateResponseBean("Package", "Invalid Package index  for equipment '"+packagebean.getEquipmentIndex()+"' ","ERROR");
//			    	 result=false;
//						//throw ex;
//			     }
		    	  }
		 	
	    	 }
		}
			
			objBillDetailBean.setObjmPackageBean(objmPackageBeans);
	
}
	    
	public void validateCargo(AMSBean amsbean) throws Exception{
		ArrayList<CargoBean> objmCargoBeans=new ArrayList<CargoBean>();
		Package[] objmPackagesbean=blbean.getPackage();
		Cargo[] objcagobean=blbean.getCargo();
		Equipment[] objmEquipmentsbean=blbean.getEquipment();
		Party[] objmPartybean=amsbean.getParty();
		for(Equipment eqipackagebean:objmEquipmentsbean){
			for(Cargo cargobean:objcagobean){
				for (Package packagebean:objmPackagesbean){	
				if(cargobean.getPackageIndex()==packagebean.getIndex()&& packagebean.getEquipmentIndex()==eqipackagebean.getIndex()){
					CargoBean objmCargoBean=new CargoBean();
					objmCargoBean.setCargoEquipment(eqipackagebean.getEquipmentNumber());
					if(cargobean.getHarmonizedCode()==null)
						objmCargoBean.setHarmonizedCode("");
					else
						objmCargoBean.setHarmonizedCode(cargobean.getHarmonizedCode());
				    if(cargobean.getDescription()==null)
						objmCargoBean.setGoodsDescription("");
					else
						objmCargoBean.setGoodsDescription(cargobean.getDescription().trim());
					
				    if((cargobean.getUN()==null || cargobean.getUN()=="") && (cargobean.getCLASS()==null || cargobean.getCLASS()==""))
						objmCargoBean.setHazardCode("");
					else{
						int systemcode=objmPackageTypeDAO.getsystemcode(cargobean.getUN(),cargobean.getCLASS());
						if(systemcode==0){
//							Response.setSource("data");
//							Response.setElement("Cargo");
//							Response.setCode("ERROR");
//							Response.setDescription("Invalid Hazard Code  '"+cargobean.getUN()+"' " );
//							objresponselist.add(Response);
//							result=false;
						generateResponseBean("Cargo", "Invalid Hazard Code  '"+cargobean.getUN()+"' ","ERROR");
						result=false;
							//throw ex;
						}
						objmCargoBean.setHazardCode(systemcode+"");
					    }
					
			
					for(Party partybean:objmPartybean){
						if(cargobean.getManufacturerIndex()==partybean.getIndex()){
						CustomerProfileBean objmCustomerProfileBean=new CustomerProfileBean();
						objmCustomerProfileBean=convertPARTYBeanToCustomerProfileBean(partybean, objmCustomerProfileBean);
						objCustomerProfileDAO.isExistXMLCustomer(objmCustomerProfileBean);
						if(objmCustomerProfileBean.getCustomerId()!=0){
							objmCargoBean.setManufacturer(objmCustomerProfileBean.getCustomerName());
							objmCargoBean.setManufacturerId(objmCustomerProfileBean.getCustomerId());
						}
					}
				}
				objmCargoBean.setCargoCountry("");	
				objmCargoBeans.add(objmCargoBean);
				break;
			}
		}
	  }
	}
	objBillDetailBean.setObjmCargoBean(objmCargoBeans);
	}	
	
	public void generateResponseBean(String tag,String Desc,String code){
		Response objResponse=new Response();
		objResponse.setSource("data");
		objResponse.setElement(tag);
		objResponse.setCode(code);
		objResponse.setDescription(Desc);
		objresponselist.add(objResponse);
		
	}
	
	
	public static void beanPropertiesToUpperCase(Object object)  {
		 
        BeanUtilsBean beansUtilsBean = BeanUtilsBean.getInstance();
         
        //Return the entire set of properties for which the specified bean provides a read method
        Map mapProperties;
        try {
            mapProperties = beansUtilsBean.describe(object);
             
            //Set of properties of the bean
            Set properties = mapProperties.keySet();
             
            //bucle obver the properties
            for (Object property : properties) {
                //if the property is a String we convert tu uppercase
                if (PropertyUtils.getSimpleProperty(object, property.toString())instanceof String ){
                    String value = (String) mapProperties.get(property);
                    if (!StringUtils.isNullOrEmpty(value)){   
                        //System.out.println("toUppercase: "+property+" : " + value + " -> " + value.toUpperCase());
                        beansUtilsBean.setProperty(object, property.toString(),value.toUpperCase());
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
        	e.printStackTrace();
        } catch (NoSuchMethodException e) {
        	e.printStackTrace();
        }
    }   
	public void convertToUpper(){
		beanPropertiesToUpperCase(objBillDetailBean);
		for (int i=0;i<objBillDetailBean.getObjmNotifyBean().size();i++){
			beanPropertiesToUpperCase(objBillDetailBean.getObjmNotifyBean().get(i));
		}
		for (int i=0;i<objBillDetailBean.getObjmEquipmentBean().size();i++){
			beanPropertiesToUpperCase(objBillDetailBean.getObjmEquipmentBean().get(i));
		}
		for (int i=0;i<objBillDetailBean.getObjmPackageBean().size();i++){
			beanPropertiesToUpperCase(objBillDetailBean.getObjmPackageBean().get(i));
		}
		for (int i=0;i<objBillDetailBean.getObjmCargoBean().size();i++){
			beanPropertiesToUpperCase(objBillDetailBean.getObjmCargoBean().get(i));
		}
		
	}
	
}