package wikidev.server.data;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import wikidev.server.Auth;
import wikidev.server.db.DatabaseHandler;

public class Resource {

	public static Resource getById(int res_id , Version ver ) {
		return new Resource( res_id , ver );
	}

	private static java.sql.Connection conn = DatabaseHandler.getConnection();

	private String name;
	private int rev_id;
	private int res_id;
	
	public Resource(int res_id, Version ver) {
		this.res_id = res_id;
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( "SELECT res_name , rev_id FROM res WHERE res_id = ?" );
			s.setInt( 1 , res_id );
			ResultSet rs = s.executeQuery();
			if( rs.next() ){
				name = rs.getString(1);
				rev_id = rs.getInt(2);
			}
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
	}
	
	public Revision getRevision(){
		return Revision.getById( rev_id , this );
	}
	
	public void setRevision( Revision r ){
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( "UPDATE res SET rev_id = ? WHERE res_id = ?" );
			s.setInt( 1 , r.getId() );
			s.setInt( 2 , res_id );
			s.execute();
			rev_id = r.getId();
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}

	}
	
	public String getName(){
		return name;
	}
	
	public void update( String mime , InputStream data , Auth auth ){
		Revision r = getRevision().branch( mime , auth ).set( data );
		setRevision( r );
	}
	
	public void update( String mime , String data , Auth auth ){
		Revision r = getRevision().branch( mime , auth ).set( data );
		setRevision( r );
	}

	
}
