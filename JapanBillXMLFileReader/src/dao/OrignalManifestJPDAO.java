package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import beans.amsBeans.BillDetailBean;
import beans.amsBeans.ForeignPortBean;
import beans.amsBeans.OriginalManifestBean;
import connectionfactory.DBConnectionFactory;

public class OrignalManifestJPDAO {
	private Connection con;
	private Statement stmt = null;
	private ResultSet rs = null,rs1=null;
	private PreparedStatement pstmt = null, pstmt1 = null, pstmt2 = null,pstmt3 = null, pstmt4 = null,pstmt5 = null;
	private static Log log = LogFactory.getLog("dao.OrignalManifestJPDAO");


	public OrignalManifestJPDAO() {
		getConnection();
	}

	public void getConnection() {
		try {
			con = new DBConnectionFactory().getConnection();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setAutoCommit(Boolean autoCommit){
		try {
			con.setAutoCommit(autoCommit);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	public void commit(Boolean autoCommit){
		try {
			if(autoCommit)
				con.commit();
			else
				con.rollback();
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}
	public void closeAll() {
		try {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	

	

	/**
	 * @author Rohit
	 * @Date 11 april 2011
	 * @Descripion This method is for reriving the Voyage list .
	 * @method getManifestList
	 * @return objmList;
	 **/
	public ArrayList<OriginalManifestBean> getManifestList(int voyageId,
			String loadPortList, String dischargePortList, String loginScac) {
		ArrayList<OriginalManifestBean> objmList = null;
		ResultSet rs = null;
		try {
			objmList = new ArrayList<OriginalManifestBean>();
			OriginalManifestBean objmOrignalManifestBean;
			String query = "SELECT a.voyage_id,e.location_name,c.location_code, c.unlocode, f.location_name,"
					+ " d.location_code, d.unlocode, b.eta, b.is_ams_sent,"
					+ " ifnull((select distinct true "
					+ " from jp_bill_header e, jp_bill_detail_status f "
					+ " where e.voyage_id = a.voyage_id and e.load_port = b.load_port and"
					+ " e.discharge_port = b.discharge_port and f.bill_lading_id = e.bill_lading_id "
					+ " and f.is_manifest_error = true), false) as test "
					+ " FROM jp_voyage a, jp_voyage_port_details b, location c, location d,jp_voyage_details e,jp_voyage_details f "
					+ " Where a.login_scac=? " 
					+ " and a.login_scac = c.login_scac " 
					+ " and a.login_scac = d.login_scac"
					+ " and a.voyage_id=? "
					+ " and a.voyage_id=b.voyage_id "
					+ " and b.load_port=c.location_code "
					+ " and b.discharge_port=d.location_code "
					+ " and b.load_port like ? "
					+ " and b.discharge_port like ? " 
					+ " and c.location_id=e.location_id " 
					+ " and d.location_id=f.location_id " 
					+ " and a.voyage_id=e.voyage_id " 
					+ " and a.voyage_id=f.voyage_id "
					+ " and e.is_load_port=true "
					+ " and f.is_discharge_port=true ";


			pstmt = con.prepareStatement(query);
			pstmt.setString(1, loginScac);
			pstmt.setInt(2, voyageId);
			pstmt.setString(3, loadPortList + "%");
			pstmt.setString(4, dischargePortList + "%");
			log.info(pstmt.toString());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				objmOrignalManifestBean = new OriginalManifestBean();
				objmOrignalManifestBean.setVoyageListId(rs.getInt(1));
				objmOrignalManifestBean.setLoadPort(rs.getString(2));
				objmOrignalManifestBean.setLoadPortCode(rs.getString(3));
				objmOrignalManifestBean.setLoadPortUnCode(rs.getString(4));
				objmOrignalManifestBean.setDischargePort(rs.getString(5));
				objmOrignalManifestBean.setDischargePortCode(rs.getString(6));
				objmOrignalManifestBean.setDischargePortUnCode(rs.getString(7));
				objmOrignalManifestBean.setEta(rs.getString(8));				
				objmOrignalManifestBean.setSendManifest(false);
				objmOrignalManifestBean.setIs_Manifest_Error(rs.getBoolean(10));
				objmList.add(objmOrignalManifestBean);
			}
		} catch (SQLException e) {
			log.error("Error In List retriving..");
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Error In List retriving..");
			e.printStackTrace();
		}
		return objmList;
	}

	/**
	 * @author Rohit
	 * @Date 26 april 2011
	 * @Descripion This method is for reriving the update original AMS .
	 * @method update
	 **/
	public String update(OriginalManifestBean objBean,String userName, String loginScac) {
		String result = "";
		BillHeaderJpDAO objmBillHeaderDAO=new BillHeaderJpDAO();
		
		String firstJPDischargePortCode=objmBillHeaderDAO.getDistrictPortForFROB(objBean.getObjmOrignalManifestBeans().get(0).getVoyageListId(), loginScac);
		ArrayList<ForeignPortBean> objmForeignPortBeans=objmBillHeaderDAO.getFrobPorts(objBean.getObjmOrignalManifestBeans().get(0).getVoyageListId(), loginScac);
		
		ArrayList a = new ArrayList();
		OriginalManifestBean objmOriginalManifestBeans;
		try {
			
			pstmt3 = con.prepareStatement("Insert into afr_to_be_sent" +
					" (login_scac, voyage_id, load_port, discharge_port, " +
					" bill_lading_id, equipment_number, control_identifier, " +
					" action_code, amendment_code, sent_date, sent_user,isRegisterCompletionID) "
					+ " values (?, ?, ?, ?, ?, '', 'MI','','', now(), ?,0)");

			pstmt4 = con
					.prepareStatement("Update jp_bill_detail_status "
							+ " set is_readonly=true where bill_lading_id=? and login_scac=?");
			log.info("Size :"+objBean.getObjmOrignalManifestBeans().size());
			pstmt5=con
			.prepareStatement("Select * from afr_to_be_sent " +
					" where bill_lading_id=? " +
					" and voyage_id=? " +
					" and load_port=? " +
					" and discharge_port=? " +
					" and login_scac=? " +
					" and control_identifier='MI'");
			
			for (int i = 0; i < objBean.getObjmOrignalManifestBeans().size(); i++) {
				objmOriginalManifestBeans = (OriginalManifestBean) objBean.getObjmOrignalManifestBeans().get(i);
				if(!isFROBPort(objmOriginalManifestBeans.getDischargePortCode())){
					String tempDischargePort="'"+objmOriginalManifestBeans.getDischargePortCode()+"'";
					if(objmOriginalManifestBeans.getDischargePortCode().equals(firstJPDischargePortCode)){
						for(int j=0;j<objmForeignPortBeans.size();j++)
							tempDischargePort=tempDischargePort+",'"+objmForeignPortBeans.get(j).getPortName()+"'";
					}
					pstmt=con.prepareStatement("insert ignore into jp_voyage_port_details " +
							" select a.login_scac, a.voyage_id,"+objmOriginalManifestBeans.getLoadPortCode()+ 
							", b.location_code, false, a.arrival_date, a.sailing_date from jp_voyage_details a, " +
							" location b where a.login_scac = b.login_scac and b.location_id = a.location_id " +
							" and a.login_scac = ? and a.voyage_id = ? and b.location_code in ("+tempDischargePort+")");
						pstmt.setString(1, loginScac);
						pstmt.setInt(2, objmOriginalManifestBeans.getVoyageListId());
						pstmt.executeUpdate();
					
					pstmt1 = con
					.prepareStatement("Update jp_voyage_port_details "
							+ " set is_ams_sent=true where load_port=? and discharge_port in ("+tempDischargePort+") "
							+ " and voyage_id=? and login_scac=? and is_ams_sent=false");
					//if (objmOriginalManifestBeans.getSendManifest()) {
						pstmt1.setString(1, objmOriginalManifestBeans.getLoadPortCode());
						pstmt1.setInt(2, objmOriginalManifestBeans.getVoyageListId());
						pstmt1.setString(3, loginScac);
						log.info("1 : "+pstmt1.toString());
						a.add(pstmt1.executeUpdate());							
					//}
					pstmt2 = con
					.prepareStatement("Select distinct a.bill_lading_id " +
							" from jp_bill_header a " +
							" where a.voyage_id=? " +
							" and a.load_port=? " +
							" and a.bill_status!='DELETED'" +
							" and a.discharge_port in ("+tempDischargePort+") ");
					pstmt2.setInt(1, objmOriginalManifestBeans.getVoyageListId());
					pstmt2.setString(2, objmOriginalManifestBeans.getLoadPortCode());
					log.info("2 : "+pstmt2.toString());
					rs = pstmt2.executeQuery();
	
					while (rs.next()) {
						//code for avoid multiple entry of voyageid,load_por,Discharge_por,bill_lading _id,Controlidentifier="MI",login_scac
						pstmt5.setInt(1, rs.getInt(1));
						pstmt5.setInt(2, objmOriginalManifestBeans.getVoyageListId());
						pstmt5.setString(3, objmOriginalManifestBeans.getLoadPortCode());
						pstmt5.setString(4, objmOriginalManifestBeans.getDischargePortCode());
						pstmt5.setString(5, loginScac);
						log.info("3 : "+pstmt5.toString());
						rs1 = pstmt5.executeQuery();
						//end of avoid multiple entry of voyageid,load_por,Discharge_por,bill_lading _id,Controlidentifier="MI",login_scac
						if(!rs1.next()){						
							pstmt3.setString(1, loginScac);
							pstmt3.setInt(2, objmOriginalManifestBeans.getVoyageListId());
							pstmt3.setString(3, objmOriginalManifestBeans.getLoadPortCode());
							pstmt3.setString(4,objmOriginalManifestBeans.getDischargePortCode());
							pstmt3.setInt(5, rs.getInt(1));
							//pstmt3.setString(6, rs.getString(2));
							pstmt3.setString(6, userName);
							log.info("3 : "+pstmt3.toString());
							a.add(pstmt3.executeUpdate());
			
							pstmt4.setInt(1, rs.getInt(1));
							pstmt4.setString(2, loginScac);
							log.info("4 : "+pstmt4.toString());
							a.add(pstmt4.executeUpdate());
						}
					}	
				}
			}
			if( objBean.getObjmOrignalManifestBeans().size() > 0){
				result = "Success";
				return result;
			}else{
				if (a.contains(0)) {
					result = "Error";
				} else {
					result = "Success";
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			result = "Error";
		}finally{
				try{
					objmBillHeaderDAO.closeAll();
				  }
				catch (Exception e) {
					e.printStackTrace();
					} 
			}
		return result;
	}
	public String updateFOBE(OriginalManifestBean objBean,String userName, String loginScac) {
		String result = "";
		BillHeaderJpDAO objmBillHeaderDAO=new BillHeaderJpDAO();
		ReferenceNumberDAO objReferenceNumberDAO=new ReferenceNumberDAO();
		ArrayList a = new ArrayList();
		OriginalManifestBean objmOriginalManifestBeans;
		try {
			pstmt3 = con.prepareStatement("Insert into afr_sent" +
					" (login_scac, voyage_id, load_port, discharge_port, " +
					" bill_lading_id, equipment_number, control_identifier, " +
					" action_code, amendment_code, sent_date, sent_user,reference_number,status,message,batch_number,transmitted_date) "
					+ " values (?, ?, ?, ?, ?, '', 'MI','','', now(), ? , ? ,'','','',now())");

			pstmt4 = con
					.prepareStatement("Update jp_bill_detail_status "
							+ " set is_readonly=true where bill_lading_id=? and login_scac=?");
			log.info("Size :"+objBean.getObjmOrignalManifestBeans().size());
			pstmt5=con
			.prepareStatement("Select * from afr_sent " +
					" where bill_lading_id=? " +
					" and voyage_id=? " +
					" and load_port=? " +
					" and discharge_port=? " +
					" and login_scac=? " +
					" and control_identifier='MI'");
			
			for (int i = 0; i < objBean.getObjmOrignalManifestBeans().size(); i++) {
				
				objmOriginalManifestBeans = (OriginalManifestBean) objBean.getObjmOrignalManifestBeans().get(i);
				if(isFROBPort(objmOriginalManifestBeans.getDischargePortCode())){
					String refNumber="ORM"+ objReferenceNumberDAO.getReferenceNumber(loginScac);
					pstmt1 = con.prepareStatement("Update jp_voyage_port_details "
							+ " set is_ams_sent=true where load_port=? and discharge_port='"+objmOriginalManifestBeans.getDischargePort()+"' "
							+ " and voyage_id=? and login_scac=? and is_ams_sent=false");
					pstmt1.setString(1, objmOriginalManifestBeans.getLoadPortCode());
					pstmt1.setInt(2, objmOriginalManifestBeans.getVoyageListId());
					pstmt1.setString(3, loginScac);
					log.info("1 : "+pstmt1.toString());
					a.add(pstmt1.executeUpdate());							
	
					pstmt2 = con.prepareStatement("Select distinct a.bill_lading_id " +
						" from jp_bill_header a " +
						" where a.voyage_id=? " +
						" and a.load_port=? " +
						" and a.bill_status!='DELETED'" +
						" and a.discharge_port='"+objmOriginalManifestBeans.getDischargePortCode()+"' ");
					pstmt2.setInt(1, objmOriginalManifestBeans.getVoyageListId());
					pstmt2.setString(2, objmOriginalManifestBeans.getLoadPortCode());
					log.info("2 : "+pstmt2.toString());
					rs = pstmt2.executeQuery();
	
					while (rs.next()) {
						//code for avoid multiple entry of voyageid,load_por,Discharge_por,bill_lading _id,Controlidentifier="MI",login_scac
						pstmt5.setInt(1, rs.getInt(1));
						pstmt5.setInt(2, objmOriginalManifestBeans.getVoyageListId());
						pstmt5.setString(3, objmOriginalManifestBeans.getLoadPortCode());
						pstmt5.setString(4, objmOriginalManifestBeans.getDischargePortCode());
						pstmt5.setString(5, loginScac);
						log.info("3 : "+pstmt5.toString());
						rs1 = pstmt5.executeQuery();
						//end of avoid multiple entry of voyageid,load_por,Discharge_por,bill_lading _id,Controlidentifier="MI",login_scac
						if(!rs1.next()){
							pstmt3.setString(1, loginScac);
							pstmt3.setInt(2, objmOriginalManifestBeans.getVoyageListId());
							pstmt3.setString(3, objmOriginalManifestBeans.getLoadPortCode());
							pstmt3.setString(4,objmOriginalManifestBeans.getDischargePortCode());
							pstmt3.setInt(5, rs.getInt(1));
							pstmt3.setString(6, userName);
							pstmt3.setString(7, refNumber);
							log.info("3 : "+pstmt3.toString());
							a.add(pstmt3.executeUpdate());
			
							pstmt4.setInt(1, rs.getInt(1));
							pstmt4.setString(2, loginScac);
							log.info("4 : "+pstmt4.toString());
							a.add(pstmt4.executeUpdate());
						}
					}				
				}
			}
			if( objBean.getObjmOrignalManifestBeans().size() > 0){
				result = "Success";
				return result;
			}else{
				if (a.contains(0)) {
					result = "Error";
				} else {
					result = "Success";
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			result = "Error";
		}finally{
				try{
					objReferenceNumberDAO.closeAll();
					objmBillHeaderDAO.closeAll();
				  }
				catch (Exception e) {
					e.printStackTrace();
					} 
			}
		return result;
	}

	/**
	 * @author Rohit
	 * @Date 26 april 2011
	 * @Descripion This method is for reriving the update original AMS .
	 * @method getErrorBilll
	 **/
	public ArrayList<BillDetailBean> getErrorBill(int voyageId, String loadPort, String dischargePort,
			String loginScac) {
		ArrayList<BillDetailBean> objmList = new ArrayList<BillDetailBean>();
		try {
			objmList = new ArrayList<BillDetailBean>();
			pstmt = con
					.prepareStatement("SELECT a.bill_lading_number,a.bill_lading_id FROM jp_bill_header a,"
							+ " jp_bill_detail_status b, jp_voyage c Where a.login_scac=? and a.bill_lading_id=b.bill_lading_id "
							+ " and a.load_port=? and a.discharge_port=? and a.voyage_number=c.voyage_number "
							+ " and c.voyage_id=? and a.voyage_id=c.voyage_id and b.is_manifest_error=true");
			pstmt.setString(1, loginScac);
			pstmt.setString(2, loadPort);
			pstmt.setString(3, dischargePort);
			pstmt.setInt(4, voyageId);
			log.info(pstmt.toString());
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				BillDetailBean objmBillDetailBean = new BillDetailBean();
				objmBillDetailBean.setBillLadingNumber(rs.getString(1));
				objmBillDetailBean.setBillLadingId(rs.getInt(2));
				objmList.add(objmBillDetailBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objmList;
	}
	
	public Boolean isFROBPort(String dischargePort){
		Boolean result=false;
		PreparedStatement objmPreparedStatement=null;
		try {
			//System.out.println(dischargePort);
			objmPreparedStatement=con.prepareStatement("select * from foreign_port a where a.port_code=? "+
								 " and a.port_code not in(select b.port_code from japan_port b where b.port_code=a.port_code) "+
								 " union "+
								 " select * from district_port a where a.port_code=?");
			objmPreparedStatement.setString(1, dischargePort);
			objmPreparedStatement.setString(2, dischargePort);
			rs=objmPreparedStatement.executeQuery();
			if(rs.next())
				result=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Boolean isJapanPort(String dischargePort){
		System.out.println("isJapanPort");
		Boolean result=false;
		PreparedStatement objmPreparedStatement=null;
		try {
			System.out.println(dischargePort);
			objmPreparedStatement=con.prepareStatement("select * from japan_port where port_code=?");
			objmPreparedStatement.setString(1, dischargePort);
			rs=objmPreparedStatement.executeQuery();
			if(rs.next())
				result=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Boolean isCanadaPort(String dischargePort){
		Boolean result=false;
		PreparedStatement objmPreparedStatement=null;
		try {
			objmPreparedStatement=con.prepareStatement("select * from canada_port where port_code=?");
			objmPreparedStatement.setString(1, dischargePort);
			rs=objmPreparedStatement.executeQuery();
			if(rs.next())
				result=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
