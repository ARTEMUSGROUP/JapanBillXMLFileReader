/**
 * 
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import beans.amsBeans.PackageBean;
import beans.amsBeans.PackageTypeBean;
import connectionfactory.DBConnectionFactory;

/**
 * @author Vikas
 *
 */
public class PackageTypeDAO {
	private Connection con=null;
	private Statement objmStatement;
	private ResultSet objmResultSet,rs;
	private PreparedStatement objPreparedStatement;
	public PackageTypeDAO(){
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
	public ArrayList<PackageTypeBean> getpackageList(){
		ArrayList<PackageTypeBean> objmList = new ArrayList<PackageTypeBean>();
		try {
			objmStatement=con.createStatement();
			objmResultSet=objmStatement.executeQuery("SELECT package_type,package_desc FROM package_type order by package_desc");
			while(objmResultSet.next()){
				PackageTypeBean objPackageType = new PackageTypeBean();
				objPackageType.setPackageType(objmResultSet.getString(1));
				objPackageType.setPackageDesc(objmResultSet.getString(2));
				objmList.add(objPackageType);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return objmList;
	}
	public PackageBean getPackageType(String packageDesc){
		PackageBean objmPackageBean = new PackageBean();
		try{
			objmStatement=con.createStatement();
			objmResultSet=objmStatement.executeQuery("(SELECT package_type FROM package_type where package_desc='"+packageDesc+"') " +
					"union " +
					"(SELECT package_type FROM addmore_package where package_desc='"+packageDesc+"')");
			if(objmResultSet.next()){
				objmPackageBean.setPackages(objmResultSet.getString(1));
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return objmPackageBean;
	}
	public PackageBean getData(String packageType){
		PackageBean objmPackageBean = new PackageBean();
		try{
			objmStatement=con.createStatement();
			objmResultSet=objmStatement.executeQuery("SELECT package_desc FROM package_type where package_type='"+packageType+"'");
			while(objmResultSet.next()){
					objmPackageBean.setPackages(objmResultSet.getString(1));
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return objmPackageBean;
	}
	
	public int getsystemcode(String hazmatcode,String classname) {
		 int systemcode=0;
		try{
			objPreparedStatement=con.prepareStatement("SELECT system_code FROM new_hazard_code where  un_code=? and class=? ");
			objPreparedStatement.setString(1, hazmatcode);
			objPreparedStatement.setString(2, classname);
			rs=objPreparedStatement.executeQuery();
			if(rs.next())
				systemcode=rs.getInt(1);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return systemcode ;
	}
	
	public void closeAll(){
		try {
			if(con!=null)
				con.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
