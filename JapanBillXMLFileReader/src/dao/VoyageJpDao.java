package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import beans.amsBeans.LocationBean;
import beans.amsBeans.PortDetailsBean;
import beans.amsBeans.VoyageBean;
import connectionfactory.DBConnectionFactory;

public class VoyageJpDao {
	private Connection con;
	private PreparedStatement stmt = null,stmt1=null;
	ResultSet rs=null;
	private static Log log = LogFactory.getLog("dao.VoyageJpDAO");

	 public VoyageJpDao() {
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
	public void commit(boolean commit) throws Exception{
		if(commit)
			con.commit();
		else
			con.rollback();
	}
	public void closeAll(){
		try {
			if (stmt!=null){
				stmt.close();
			}
			if(con!=null){
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
/**
 * @author Rohit
 * @Date 2 march 2011
 * @Descripion This method is created for the Inserting the Voyage Details .
 * @method insert
 * @return result ;
 * @param objmVoyageBean,objmPortDetailsBean
**/
	public String insert(VoyageBean objmVoyageBean) {

			String result="";
			int autoIncrementId=0;
			try {
				con.setAutoCommit(false);
				stmt= con.prepareStatement("Insert into jp_voyage " +
						"(login_scac, voyage_number, vessel_id, vessel_scac, crew_members," +
						" passengers, report_number, created_user, created_date,is_jp_voyage)" +
						" VALUES (?,?,?,?,?,?,?,?,now(),'Y') ",Statement.RETURN_GENERATED_KEYS);
				stmt.setString(1,objmVoyageBean.getLoginScac());
				stmt.setString(2, objmVoyageBean.getVoyage());
				stmt.setInt(3,objmVoyageBean.getVesselId());
				stmt.setString(4,objmVoyageBean.getScacCode());
				stmt.setString(5, objmVoyageBean.getCrewMembers());
				stmt.setString(6, objmVoyageBean.getPassengers());
				stmt.setString(7, objmVoyageBean.getReportNumber());
				stmt.setString(8, objmVoyageBean.getCreatedUser());
				
				
				
				stmt.execute();
					rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					autoIncrementId = rs.getInt(1);
				}
				stmt= con.prepareStatement("Insert into jp_voyage_details " +
						"(login_scac, voyage_id, location_id, is_last_load_port, " +
						" terminal, sailing_date, arrival_date, is_load_port, " +
						" is_discharge_port, location_name)" +
						" values(?,?,?,?,?,?,?,?,?,?)");
				
				stmt.setString(1, objmVoyageBean.getLoginScac());
				stmt.setInt(2, autoIncrementId);
				for(int i=0;i<objmVoyageBean.getObjmPortDetailsBeans().size();i++){
					stmt.setInt(3, objmVoyageBean.getObjmPortDetailsBeans().get(i).getLocationId());
					stmt.setBoolean(4, objmVoyageBean.getObjmPortDetailsBeans().get(i).getLastLoadPort());
					stmt.setString(5, objmVoyageBean.getObjmPortDetailsBeans().get(i).getTerminal());
					stmt.setString(6, objmVoyageBean.getObjmPortDetailsBeans().get(i).getSailingDate());
					stmt.setString(7, objmVoyageBean.getObjmPortDetailsBeans().get(i).getArrivalDate());
					stmt.setBoolean(8, objmVoyageBean.getObjmPortDetailsBeans().get(i).getLoad());
					stmt.setBoolean(9, objmVoyageBean.getObjmPortDetailsBeans().get(i).getDischarge());
					stmt.setString(10, objmVoyageBean.getObjmPortDetailsBeans().get(i).getLocation());
					stmt.execute();
					
					/*stmt1= con.prepareStatement("update location " +
							"set is_voyage_created=true " +
							" where location_id=? and login_scac=?");
					
					stmt1.setInt(1, objmVoyageBean.getObjmPortDetailsBeans().get(i).getLocationId());
					stmt1.setString(2, objmVoyageBean.getLoginScac());
					stmt1.executeUpdate();*/
				}

				stmt= con.prepareStatement("update vessel " +
						"set is_voyage_created=true " +
						" where vessel_id=? and login_scac=?");
				
				stmt.setInt(1, objmVoyageBean.getVesselId());
				stmt.setString(2, objmVoyageBean.getLoginScac());
				stmt.executeUpdate();
				
				result="Success";
			} catch (SQLException e) {
				e.printStackTrace();
				result="Exception";
			}catch (Exception e) {
				e.printStackTrace();
				result="Exception";
			}finally{
				try {
					if (!result.equals("Success")) {
						con.rollback();
						log.error("Error in Insert query...");
						result="Fail";
					}else{
						con.commit();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			return result;
		}
	
	/**
	 * @author Rohit
	 * @Date 28 feb 2011
	 * @Descripion This method is for the Update Voyage Details.
	 * @method Update
	 * @return result;
	 * @param objmVoyageBean,objmPortDetailsBean
	**/
		public String update (VoyageBean objmVoyageBean) {

			String result="";
			int a =0,c=0;
			try {
				stmt = con.prepareStatement("update jp_voyage set crew_members=?, " +
						" passengers =?, report_number =? where voyage_id=?");
				stmt.setString(1,objmVoyageBean.getCrewMembers());
				stmt.setString(2,objmVoyageBean.getPassengers());
				stmt.setString(3,objmVoyageBean.getReportNumber());
				stmt.setInt(4, objmVoyageBean.getVoyageId());
				
				a=stmt.executeUpdate();
				stmt = con.prepareStatement("delete from jp_voyage_details where voyage_id=?");
				stmt.setInt(1, objmVoyageBean.getVoyageId());
				stmt.executeUpdate();
				
				stmt= con.prepareStatement("Insert into jp_voyage_details " +
						"(login_scac, voyage_id, location_id, is_last_load_port, " +
						" terminal, sailing_date, arrival_date, is_load_port, " +
						" is_discharge_port, location_name)" +
						" values(?,?,?,?,?,?,?,?,?,?)");
				
				stmt.setString(1, objmVoyageBean.getLoginScac());
				stmt.setInt(2, objmVoyageBean.getVoyageId());
				for(int i=0;i<objmVoyageBean.getObjmPortDetailsBeans().size();i++){
					stmt.setInt(3, objmVoyageBean.getObjmPortDetailsBeans().get(i).getLocationId());
					stmt.setBoolean(4, objmVoyageBean.getObjmPortDetailsBeans().get(i).getLastLoadPort());
					stmt.setString(5, objmVoyageBean.getObjmPortDetailsBeans().get(i).getTerminal());
					stmt.setString(6, objmVoyageBean.getObjmPortDetailsBeans().get(i).getSailingDate());
					stmt.setString(7, objmVoyageBean.getObjmPortDetailsBeans().get(i).getArrivalDate());
					stmt.setBoolean(8, objmVoyageBean.getObjmPortDetailsBeans().get(i).getLoad());
					stmt.setBoolean(9, objmVoyageBean.getObjmPortDetailsBeans().get(i).getDischarge());
					stmt.setString(10, objmVoyageBean.getObjmPortDetailsBeans().get(i).getLocation());
					c=stmt.executeUpdate();
					
					stmt1= con.prepareStatement("update location " +
							"set is_voyage_created=true " +
							" where location_id=? and login_scac=?");
					
					stmt1.setInt(1, objmVoyageBean.getObjmPortDetailsBeans().get(i).getLocationId());
					stmt1.setString(2, objmVoyageBean.getLoginScac());
					stmt1.executeUpdate();
				}
				if(a>0  && c>0){
					result="Success";
				}else{
					result="Fail";
				}
			} catch (SQLException e) {
				e.printStackTrace();
				result="Exception";
			}catch (Exception e) {
				e.printStackTrace();
				result="Exception";
			}finally{
				try {
					if (result.equals("Success"))
						con.commit();
					else{
						con.rollback();
					log.error("Error in Update query...");
						result="Fail";
					}	
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			return result;
		}
/**
 * @author Rohit 
 * @Date 2 march 2011
 * @Descripion This method is for the delete Voyage Details.
 * @method delete
 * @return result;
 * @param voyageId
**/
		public String delete (int voyageId ) {
			String result="";
			int a=0,b=0;
			try {
				stmt = con.prepareStatement("Delete from jp_voyage where voyage_id=?");
				stmt.setInt(1, voyageId);
				a=stmt.executeUpdate();
				
				stmt = con.prepareStatement("Delete from jp_voyage_details where voyage_id=?");
				stmt.setInt(1, voyageId);
				
				b= stmt.executeUpdate();
				if(a>0 && b>0){
				result="Success";
				}else {
					result="Fail";
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				try {
					if (result.equals("Success"))
						con.commit();
					else{
						con.rollback();
						log.error("Error in Delete query...");
						result="Fail";
					}	
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			return result;
		}	
		
/**
 * @author Rohit
 * @Date 2 March 2011
 * @Descripion This method is for reriving the list of Voyage Details.
 * @method getList
 * @return objmList;
 *  @param searchVoyage,loginScac
**/
		public ArrayList<VoyageBean> getList (String searchVoyage ,String loginScac ) {
			ArrayList<VoyageBean> objmList=null;
			ResultSet rs=null;
			try {
				objmList = new ArrayList<VoyageBean>();
				
				VoyageBean objmVoyageBean;
				stmt = con.prepareStatement("Select v.voyage_id, v.voyage_number, " +
						" v.vessel_id,ve.vessel_name from jp_voyage v,vessel ve  where v.vessel_id=ve.vessel_id and v.login_scac=? and v.voyage_number like ?");
				stmt.setString(1,loginScac);
				stmt.setString(2, searchVoyage+"%");
				rs= stmt.executeQuery();
				while(rs.next()){
					objmVoyageBean = new VoyageBean(); 
					objmVoyageBean.setVoyageId(rs.getInt(1));
					objmVoyageBean.setVoyage(rs.getString(2));
					objmVoyageBean.setVesselId(rs.getInt(3));
					objmVoyageBean.setVesselName(rs.getString(4));
					objmList.add(objmVoyageBean);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return objmList;
		}

		public ArrayList<VoyageBean> getListbyVesselName (String searchVessel ,String loginScac ) {
			ArrayList<VoyageBean> objmList=null;
			ResultSet rs=null;
			try {
				objmList = new ArrayList<VoyageBean>();
				
				VoyageBean objmVoyageBean;
				stmt = con.prepareStatement("Select v.voyage_id, v.voyage_number, " +
						" v.vessel_id,ve.vessel_name from jp_voyage v,vessel ve  where v.vessel_id=ve.vessel_id and v.login_scac=? and ve.vessel_name like ?");
				stmt.setString(1,loginScac);
				stmt.setString(2, searchVessel+"%");
				rs= stmt.executeQuery();
				while(rs.next()){
					objmVoyageBean = new VoyageBean(); 
					objmVoyageBean.setVoyageId(rs.getInt(1));
					objmVoyageBean.setVoyage(rs.getString(2));
					objmVoyageBean.setVesselId(rs.getInt(3));
					objmVoyageBean.setVesselName(rs.getString(4));
					objmList.add(objmVoyageBean);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return objmList;
		}
/**
 * @author Ajoy
 * @Date 20 April 2011
 * @Descripion This method is for reriving the list of Voyage Details.
 * @method getList
 * @return objmList;
 *  @param vesselId,loginScac
**/
				public ArrayList<VoyageBean> getList (int vesselId ,String loginScac ) {
					ArrayList<VoyageBean> objmList=null;
					ResultSet rs=null;
					try {
						objmList = new ArrayList<VoyageBean>();
						
						VoyageBean objmVoyageBean;
						stmt = con.prepareStatement("Select v.voyage_id, v.voyage_number, " +
								" v.vessel_id,ve.vessel_name from jp_voyage v,vessel ve where v.vessel_id = ve.vessel_id and v.login_scac=? and v.vessel_id = ?");
						stmt.setString(1,loginScac);
						stmt.setInt(2, vesselId);
						rs= stmt.executeQuery();
						while(rs.next()){
							objmVoyageBean = new VoyageBean(); 
							objmVoyageBean.setVoyageId(rs.getInt(1));
							objmVoyageBean.setVoyage(rs.getString(2));
							objmVoyageBean.setVesselId(rs.getInt(3));
							objmVoyageBean.setVesselName(rs.getString(4));
							objmList.add(objmVoyageBean);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return objmList;
				}		
/**
 * @author Rohit 
 * @Date 1 march 2011
 * @Descripion This method is for the retriving the Vessel Details .
 * @method getData
 * @return objmVesselBean;
 * @param vesselId
**/
		public void getErrorMessage(Exception e,VoyageBean objmVoyageBean) {
			StackTraceElement[] stack = e.getStackTrace();
		    String exception = e.getMessage()+ "\n\t\t";
		    for (StackTraceElement s : stack) {
		        exception = exception + s.toString() + "\n\t\t";
		    }
		    objmVoyageBean.setException(exception);
		}
		public VoyageBean getData (int voyageId ) {
			ResultSet rs=null;
			VoyageBean objmVoyageBean=new VoyageBean();
			ArrayList<PortDetailsBean>objmList=null;
			PortDetailsBean objmPortDetailsBean;
			try {
				objmList = new ArrayList<PortDetailsBean>();
				stmt = con.prepareStatement("Select voyage_id, voyage_number, a.vessel_id ,b.vessel_name," +
						" vessel_scac, crew_members, passengers, report_number from jp_voyage a, " +
						" vessel b " +
						" where a.voyage_id= ?" + 
						" and b.vessel_id = a.vessel_id");
				stmt.setInt(1, voyageId);
				rs= stmt.executeQuery();
				while(rs.next()){
					objmVoyageBean.setVoyageId(rs.getInt(1));
					objmVoyageBean.setVoyage(rs.getString(2));
					objmVoyageBean.setVesselId(rs.getInt(3));
					objmVoyageBean.setVesselName(rs.getString(4));
					objmVoyageBean.setScacCode(rs.getString(5));
					objmVoyageBean.setCrewMembers(rs.getString(6));
					objmVoyageBean.setPassengers(rs.getString(7));
					objmVoyageBean.setReportNumber(rs.getString(8));
				}
				stmt = con.prepareStatement("select v.location_name,v.location_id," +
						" v.terminal,DATE_FORMAT(v.arrival_date,'%Y-%m-%d %H:%i'),DATE_FORMAT(v.sailing_date,'%Y-%m-%d %H:%i'),v.is_load_port," +
						" v.is_discharge_port,is_last_load_port, ifnull((select distinct 1 " +
						" from jp_bill_header v1 where v1.login_scac = v.login_scac " +
						" and v1.voyage_id = v.voyage_id and (v1.load_port = l.location_code " +
						" or v1.discharge_port = l.location_code)),0) as is_bill_created " +
						" from  location l, jp_voyage_details v where v.voyage_id = ? " +
						" and l.location_id = v.location_id order by v.sailing_date");
				stmt.setInt(1, voyageId);
				rs= stmt.executeQuery();
				while(rs.next()){
					objmPortDetailsBean = new PortDetailsBean();
					objmPortDetailsBean.setLocation(rs.getString(1));
					objmPortDetailsBean.setLocationId(rs.getInt(2));
					objmPortDetailsBean.setTerminal(rs.getString(3));
					objmPortDetailsBean.setArrivalDate(rs.getString(4));
					objmPortDetailsBean.setSailingDate(rs.getString(5));
					objmPortDetailsBean.setLoad(rs.getBoolean(6));
					objmPortDetailsBean.setDischarge(rs.getBoolean(7));
					objmPortDetailsBean.setLastLoadPort(rs.getBoolean(8));
					objmPortDetailsBean.setIsAmsSent(rs.getBoolean(9));
					objmList.add(objmPortDetailsBean);
				}
				objmVoyageBean.setObjmPortDetailsBeans(objmList);
				
			} catch (Exception e) {
				e.printStackTrace();
				getErrorMessage(e, objmVoyageBean);
			}
			return objmVoyageBean;
		}
		
/**
 * @author Rohit
 * @date 2 March 2011
 * @method isExist
 * @return result
 * @parm vesselName,loginScac, voyageNumber
**/
	public Boolean isExist(String loginScac, int vesselId , String voyageNumber){
		System.out.println(loginScac + "=="+ vesselId +"=="+ voyageNumber);
		ResultSet rs=null;
		Boolean result=true;
		try{
			stmt = con.prepareStatement("Select " +
					" voyage_id, login_scac, voyage_number, vessel_id, vessel_scac," +
					" crew_members, passengers, report_number, created_user, created_date" +
					" from jp_voyage " +
					" where login_scac=? and vessel_id=? and voyage_number=?");
			stmt.setString(1, loginScac);		
			stmt.setInt(2, vesselId);
			stmt.setString(3, voyageNumber);
					rs=stmt.executeQuery();
			if (rs.next()){
				result=true;
			}else{
				result=false;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public int getVoyageId(String loginScac, int vesselId , String voyageNumber){
		ResultSet rs=null;
		try{
			stmt = con.prepareStatement("Select " +
					" voyage_id from jp_voyage " +
					" where login_scac=? and vessel_id=? and voyage_number=?");
			stmt.setString(1, loginScac);		
			stmt.setInt(2, vesselId);
			stmt.setString(3, voyageNumber);
					rs=stmt.executeQuery();
			if (rs.next()){
				return rs.getInt(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	public Object getVoyage(String loginScac, String vesselId) {
		ArrayList<VoyageBean> objmList=null;
		ResultSet rs=null;
		try {
			objmList = new ArrayList<VoyageBean>();
			
			VoyageBean objmVoyageBean;
			stmt = con.prepareStatement("Select voyage_id, voyage_number " +
					" FROM jp_voyage " +
					" where vessel_id = ? and  login_scac=? ");				
			stmt.setString(1,vesselId);
			stmt.setString(2,loginScac);
			log.info(stmt.toString());
			rs= stmt.executeQuery();				
			while(rs.next()){
				objmVoyageBean =new VoyageBean();
				objmVoyageBean.setVoyageId(rs.getInt(1));
				objmVoyageBean.setVoyage(rs.getString(2));					
				objmList.add(objmVoyageBean);
			}				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return objmList;
	}
	
	public Object getVoyageList(String loginScac, String vesselId) {
		ArrayList<VoyageBean> objmList=null;
		ResultSet rs=null;
		try {
			objmList = new ArrayList<VoyageBean>();
			
			VoyageBean objmVoyageBean;
			stmt = con.prepareStatement("Select distinct a.voyage_id, a.voyage_number" +
					" FROM jp_voyage a, jp_bill_header b" +
					" where a.vessel_id = ?" +
					" and  a.login_scac= ?" +
					" and b.voyage_id = a.voyage_id" +
					" and b.login_scac = a.login_scac");				
			stmt.setString(1,vesselId);
			stmt.setString(2,loginScac);
			log.info(stmt.toString());
			rs= stmt.executeQuery();				
			while(rs.next()){
				objmVoyageBean =new VoyageBean();
				objmVoyageBean.setVoyageId(rs.getInt(1));
				objmVoyageBean.setVoyage(rs.getString(2));					
				objmList.add(objmVoyageBean);
			}				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return objmList;
	}
	
	public LocationBean getPortNameFromVoyage(String loginScac,String locationCode, int voyageId){
		LocationBean objmLocationBean = new LocationBean();
		try{
			
			stmt=con.prepareStatement("select a.location_name from jp_voyage_details a, location b " +
					" where b.login_scac=? and b.login_scac = a.login_scac" +
					" and b.location_code = ?" +
					" and a.login_scac = b.login_scac" +
					" and a.voyage_id = ?" +
					" and a.location_id = b.location_id");
			stmt.setString(1, loginScac);
			stmt.setString(2, locationCode);
			stmt.setInt(3, voyageId);
			rs=stmt.executeQuery();
			while(rs.next()){
				objmLocationBean.setLocationName(rs.getString(1));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return objmLocationBean;
	}
	public PortDetailsBean getCanadaFirstDischargePort(int voyage_id){
		PortDetailsBean objmPortDetailsBean=null;
		try{
			stmt = con.prepareStatement("select c.location_code,a.arrival_date from jp_voyage_details a ,canada_port b, location c " +
					"where voyage_id=? and is_discharge_port=true and "+
					"a.login_scac=c.login_scac and a.location_id=c.location_id and c.location_code=b.port_code order by arrival_date asc limit 1");
			stmt.setInt(1, voyage_id);
			rs=stmt.executeQuery();
			if(rs.next()){
				objmPortDetailsBean=new PortDetailsBean();
				objmPortDetailsBean.setLocation(rs.getString(1));
				objmPortDetailsBean.setArrivalDate(rs.getString(2));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return objmPortDetailsBean;
		
	}
	
	public ArrayList<String> getFrobPortForCanadaDischargePort(int voyageId,String loginScac, PortDetailsBean objmPortDetailsBean){
		ArrayList<String> objmList=new ArrayList<String>();
		try{
			stmt = con.prepareStatement("SELECT c.location_code FROM jp_voyage_details a, location c " +
					"WHERE a.arrival_date>? and a.is_discharge_port=true and a.login_scac=? and voyage_id=? and " +
					"a.location_id=c.location_id and c.location_code not in (select port_code from canada_port d)");
			stmt.setString(1, objmPortDetailsBean.getArrivalDate());
			stmt.setString(2, loginScac);
			stmt.setInt(3, voyageId);
			rs=stmt.executeQuery();
			while(rs.next()){
				objmList.add(rs.getString(1));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return objmList;
		
	}
	public ArrayList<VoyageBean> getAMSVoyageList(String loginScac, String vesselId) {
		ArrayList<VoyageBean> objmList=null;
		ResultSet rs=null;
		try {
			objmList = new ArrayList<VoyageBean>();
			
			VoyageBean objmVoyageBean;
			stmt = con.prepareStatement("Select distinct a.voyage_id, a.voyage_number" +
					" FROM jp_voyage a " +
					" where a.vessel_id = ?" +
					" and  a.login_scac= ?");				
			stmt.setString(1,vesselId);
			stmt.setString(2,loginScac);
			log.info(stmt.toString());
			rs= stmt.executeQuery();				
			while(rs.next()){
				objmVoyageBean =new VoyageBean();
				objmVoyageBean.setVoyageId(rs.getInt(1));
				objmVoyageBean.setVoyage(rs.getString(2));					
				objmList.add(objmVoyageBean);
			}				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return objmList;
	}
}
