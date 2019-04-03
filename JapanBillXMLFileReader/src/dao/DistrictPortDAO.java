/**
 * 
 */
package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import beans.amsBeans.DistrictPortBean;
import connectionfactory.DBConnectionFactory;

/**
 * @author Rohit
 * @date 25 feb 2011
 *
 */
public class DistrictPortDAO {

	private Connection con;
	private Statement stmt = null;
	private ResultSet rs = null;
	public DistrictPortDAO() {
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
 * @date 25 feb 2011
 * @return objmList
 * @description This method is for the retriving the District Port list 
**/	
	public ArrayList<DistrictPortBean> getList() {
		ArrayList<DistrictPortBean> objmList=null;
		try {
			objmList = new ArrayList<DistrictPortBean>();
			stmt=con.createStatement();
			String countryQuery="Select port_code,port_name from district_port order by port_name";
			rs = stmt.executeQuery(countryQuery);
			while(rs.next()){
				DistrictPortBean objmDistrictPortBean = new DistrictPortBean();
				objmDistrictPortBean.setPortCode(rs.getString(1));
				objmDistrictPortBean.setPortName(rs.getString(2));
				objmList.add(objmDistrictPortBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return objmList;
	}
	public DistrictPortBean getData(String portCode){
		DistrictPortBean objmDistrictPortBean=new DistrictPortBean();
		try {
			stmt=con.createStatement();
			String countryQuery="Select port_code,port_name from district_port where port_code='"+portCode+"'";
			rs = stmt.executeQuery(countryQuery);
			if(rs.next()){
				objmDistrictPortBean.setPortCode(rs.getString(1));
				objmDistrictPortBean.setPortName(rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return objmDistrictPortBean;
	}
}
