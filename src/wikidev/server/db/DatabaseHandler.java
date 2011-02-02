package wikidev.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import wikidev.server.Auth;
import wikidev.server.data.UserDetails;

public class DatabaseHandler {

	private static Connection conn;
	
	public static Connection getConnection(){
		if ( conn == null ) connect();
		return conn;
	}
	
	private static void connect(){
        try
        {
            String userName = "sandbox";
            String password = "4uvTTnqsnBh22AY9";
            String url = "jdbc:mysql://localhost/sandbox";
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            conn = DriverManager.getConnection (url, userName, password);
            System.out.println ("Database connection established");
        }
        catch (Exception e)
        {
        	System.err.println( e.getMessage() );
        	e.printStackTrace();
            System.err.println ("Cannot connect to database server");
        }
	}

	public static int singleIntValue(PreparedStatement ps) throws SQLException {
		ResultSet rs = ps.executeQuery();
		if ( rs.next() )
			return rs.getInt(1);
		return -1;
	}

	public static String singleStringValue(PreparedStatement ps)  throws SQLException {
		ResultSet rs = ps.executeQuery();
		if ( rs.next() )
			return rs.getString(1);
		return null;
	}

	public static Auth getUser(String name) {
		return UserDetails.getUser( "chris" ); // todo not hardcode this...
	}

}
