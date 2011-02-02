package wikidev.server.data;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import wikidev.server.Auth;
import wikidev.server.db.DatabaseHandler;
import wikidev.shared.VersionStates.Status;

public class Version {

	public static Version getById(int ver_id, App app ) {
		return new Version( ver_id , app );
	}

	private static java.sql.Connection conn = DatabaseHandler.getConnection();
	
	
	private int ver_id;
	private String name;
	private Status status;
	private App app;
	
	private Version( int ver_id , App app ){
		this.ver_id = ver_id;
		this.app = app;
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( "SELECT ver_name , ver_flags FROM ver WHERE ver_id = ?" );
			s.setInt( 1 , ver_id );
			ResultSet rs = s.executeQuery();
			if( rs.next() ){
				name = rs.getString(1);
				int statusInt = rs.getInt(2);
				// This id daft, there must be a better way...
				for( Status st : Status.values() ){
					if ( st.ordinal() == statusInt )
						status = st;
				}
			}
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}

	}
	
	public String getName(){
		return name;
	}
	
	public List<Resource> getResources(){
		List<Resource> retval = new LinkedList<Resource>();
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( "SELECT res_id FROM res WHERE ver_id = ?" );
			s.setInt( 1 , ver_id );
			ResultSet rs = s.executeQuery();
			while( rs.next() ){
				int ver_id = rs.getInt(1);
				retval.add( Resource.getById( ver_id , this ) );
			}
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
		return retval;	
	}
	
	public Resource getResource( String name ){
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( "SELECT res_id FROM res WHERE ver_id = ? AND res_name = ?" );
			s.setInt( 1 , ver_id );
			s.setString( 2 , name );
			ResultSet rs = s.executeQuery();
			if( rs.next() ){
				int ver_id = rs.getInt(1);
				return Resource.getById( ver_id , this );
			}
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
		return null;	
	}
	
	public Status getStatus(){
		return status;
	}
	
	public void setStatus( Status newStatus , Auth auth ){
		// TODO: Check the authorisation
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( "UPDATE ver SET ver_flags = ? WHERE ver_id = ?");
			s.setInt( 1 , newStatus.ordinal() );
			s.setInt( 2 , ver_id );
			s.execute();
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
		status = newStatus;
	}

	public Version branch(String name, Auth auth) {
		return branch( name , this.app , auth );
	}

	public Version branch(String name, App a, Auth auth) {
		Version newVersion = createVersion( name , this , Status.DEV , a , auth );
		newVersion.copyAllResources( this );
		return newVersion;
	}
	
	private Version createVersion(String name, Version orig, Status status , App a , Auth auth) {
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( 
					"INSERT ver( ver_name , app_id , ver_flags) VALUES (?,?,?)" );
			s.setString( 1 , name );
			s.setInt( 2 , a.getAppId() );
			s.setInt( 3 , status.ordinal() );
			s.executeUpdate();
			return orig.app.getVersion( name );
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
		return null;
	}

	private void copyAllResources( Version other ) {
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( 
					"INSERT res( ver_id , res_name , rev_id ) SELECT ? , res_name , rev_id FROM res WHERE ver_id = ?" );
			s.setInt( 1 , this.ver_id );
			s.setInt( 2 , other.ver_id );
			s.executeUpdate();
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}	
	}

	public boolean isStable() {
		return status == Status.STABLE;
	}

	public void createResource(String resource, String mime, InputStream data, Auth auth) {
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( 
					"INSERT res( ver_id , res_name , rev_id ) VALUES (?,?,?)" );
			s.setInt( 1 , this.ver_id );
			s.setString( 2 , resource );
			s.setInt( 3 , Revision.create( mime , data , auth ).getId() );
			s.executeUpdate();
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
	}

	public void removeResource(String resource) {
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( 
					"DELETE FROM res WHERE ver_id = ? AND res_name = ?" );
			s.setInt( 1 , this.ver_id );
			s.setString( 2 , resource );
			s.executeUpdate();
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
	}

}
