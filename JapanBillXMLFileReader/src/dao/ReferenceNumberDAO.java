package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connectionfactory.DBConnectionFactory;

public class ReferenceNumberDAO {
	private Connection con;
	private PreparedStatement stmt = null;
	private ResultSet rs = null;
	
	public ReferenceNumberDAO() {
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
	public int getReferenceNumber(String loginScac){
		int referenceNumber=0;
		try {
			stmt=con.prepareStatement("SELECT (ifnull(max(orm_number),0)+1) FROM orm where login_scac=?");
			stmt.setString(1, loginScac);
			rs=stmt.executeQuery();
			if(rs.next()){
				referenceNumber=rs.getInt(1);
				stmt=con.prepareStatement("update orm set orm_number=? where login_scac=?");
				stmt.setInt(1,referenceNumber);
				stmt.setString(2,loginScac);
				if(stmt.executeUpdate()!=1){
					referenceNumber=0;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return referenceNumber;
	}
}
