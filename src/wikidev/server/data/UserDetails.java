package wikidev.server.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import wikidev.server.Auth;
import wikidev.server.db.DatabaseHandler;

public class UserDetails extends Auth{

	private static final Connection conn = DatabaseHandler.getConnection();
	
	public static Auth getUser(String name) {
		try{
			 PreparedStatement  s = conn.prepareStatement (
	           "SELECT user_id, display FROM user where name=?");
			 s.setString(1, name );
			 ResultSet rs = s.executeQuery();
			 if ( rs.next() ){
				 return new UserDetails( rs.getInt(1) , rs.getString(2) );
			 }
		}catch( SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	
	private final String name;
	private final int id;
	
	private UserDetails( int id , String name ){
		this.id = id;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}


}
