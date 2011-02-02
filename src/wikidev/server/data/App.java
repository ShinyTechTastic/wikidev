package wikidev.server.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import wikidev.server.Auth;
import wikidev.server.db.DatabaseHandler;

public class App {

	public static App findApp( String name ){
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( "SELECT app_id FROM app WHERE app_name = ?" );
			s.setString(1, name );
			ResultSet rs = s.executeQuery();
			if( rs.next() ){
				try{
					int app_id = rs.getInt(1);
					return App.getById( app_id );
				}catch(Exception e ){
					System.err.println( e.getMessage() );
				}
			}
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<App> findAllApps( boolean incDev ){
		List<App> retval = new LinkedList<App>();
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( "SELECT app_id FROM app" );
			ResultSet rs = s.executeQuery();
			while( rs.next() ){
				try{
					int app_id = rs.getInt(1);
					retval.add( App.getById( app_id ) );
				}catch(Exception e ){
					System.err.println( e.getMessage() );
				}
			}
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
		return retval;
	}
	
	public static void createApp( String name , String desc , String initialVersion, Auth auth ){
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( "INSERT app ( app_name , app_description , user_id ) VALUES ( ? , ? , ?)" );
			s.setString( 1 , name);
			s.setString( 2 , desc );
			s.setInt( 3 , auth.getId() );
			s.execute();
			App a = findApp( name );
			a.prepareFrom( "template" , "template" , initialVersion , auth ); // the defualt app templates
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	

	public static App getById( int app_id ) throws Exception{
		return new App( app_id );
	}

	private static java.sql.Connection conn = DatabaseHandler.getConnection();
	
	private final int app_id;
	private String name;
	private String description;
	
	private App( int app_id ) throws Exception {
		this.app_id = app_id;
		java.sql.PreparedStatement s = conn.prepareStatement( "SELECT app_name , app_description FROM app WHERE app_id = ?" );
		s.setInt( 1 , app_id );
		ResultSet rs = s.executeQuery();
		if ( rs.next() ){
			this.name = rs.getString( 1 );
			this.description = rs.getString( 2 );
		}else{
			throw new Exception( "app_id not found in database" );
		}
	}
	
	public String getName(){
		return name;
	}
	
	public String getDescription(){
		return description;
	}
	
	public Version getLatestStable(){
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( "SELECT ver_id FROM ver WHERE app_id = ? AND ver_flags = ? ORDER BY ver_id DESC LIMIT 1" );
			s.setInt( 1 , app_id );
			s.setInt( 2 , wikidev.shared.VersionStates.Status.STABLE.ordinal() );
			ResultSet rs = s.executeQuery();
			if( rs.next() ){
				int ver_id = rs.getInt(1);
				return Version.getById( ver_id , this );
			}
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Version> getVersions(){
		List<Version> retval = new LinkedList<Version>();
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( "SELECT ver_id FROM ver WHERE app_id = ?" );
			s.setInt( 1 , app_id );
			ResultSet rs = s.executeQuery();
			while( rs.next() ){
				int ver_id = rs.getInt(1);
				retval.add( Version.getById( ver_id , this ) );
			}
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
		return retval;
	}
	
	public Version getVersion( String name ){
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( "SELECT ver_id FROM ver WHERE app_id = ? AND ver_name = ?" );
			s.setInt( 1 , app_id );
			s.setString( 2 , name );
			ResultSet rs = s.executeQuery();
			while( rs.next() ){
				int ver_id = rs.getInt(1);
				return Version.getById( ver_id , this );
			}
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
		return null;
	}
	
	public Version createVersion( String name , String prev , Auth auth ){
		return getVersion( prev ).branch( name , auth ); 
	}
	
	private void prepareFrom(String app, String version, String initial , Auth auth ) {
		App a = findApp( app );
		Version v = a.getVersion( version );
		v.branch( initial , this , auth); // branch into this application..
	}

	public int getAppId() {
		return app_id;
	}
}
