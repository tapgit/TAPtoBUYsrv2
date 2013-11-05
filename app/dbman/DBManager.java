package dbman;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import play.Logger;

public class DBManager {
	private static String driver = "org.postgresql.Driver";
	private static String db = "jdbc:postgresql://localhost:5432/TAPtoBUYDB";
	private static String user = "postgres";
	private static String pass = "postgres";

	
	
	public static void getAll(){
		try {
			Class.forName(driver);
			Connection connection = DriverManager.getConnection(db,user,pass);
			Statement statement = connection.createStatement();
			
			ResultSet rset = statement.executeQuery("select * from admins");
			if(!rset.next()){Logger.info("NO hay na");}
			while(rset.next()){
				Logger.info("Username: " + rset.getString(2) + " Password: " + rset.getString("pass") + "\n");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logger.info("EXCEPTION");
			e.printStackTrace();
		}
		
	}
}
