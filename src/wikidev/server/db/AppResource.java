package wikidev.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import wikidev.server.Auth;
import wikidev.shared.ResourceMetadata;

public class AppResource {

	private static final Connection conn = DatabaseHandler.getConnection();
	
	private static PreparedStatement getAppId = null;
	private static PreparedStatement getVersionId = null;
	private static PreparedStatement getAppList = null;
	private static PreparedStatement getAppVersions = null;
	private static PreparedStatement getAppVersionResources = null;
	private static PreparedStatement newApp = null;
	private static PreparedStatement newVersion = null;
	private static PreparedStatement copyResources = null;
	private static PreparedStatement createEmptyResources = null;
	private static PreparedStatement deleteResources = null;
	
	private static final String TEMPLATE_APP = "Sample";
	private static final String TEMPLATE_VERSION = "Initial";
	
	
	static {
		try {
			getAppId = conn.prepareStatement("SELECT app_id FROM app WHERE app_name = ?");
			getVersionId = conn.prepareStatement("SELECT ver_id FROM ver WHERE app_id = ? AND ver_name = ?");
			getAppList = conn.prepareStatement("SELECT app_name FROM app ORDER BY app_name ");
			getAppVersions = conn.prepareStatement("SELECT ver_name FROM ver WHERE app_id = ? ORDER BY ver_id ");
			getAppVersionResources = conn.prepareStatement("SELECT res_name , rev_mime FROM res JOIN rev ON res.rev_id = rev.rev_id WHERE ver_id = ? ORDER BY res_name ");
			newApp = conn.prepareStatement("INSERT app (app_name) VALUES(?)");
			newVersion = conn.prepareStatement("INSERT ver (app_id,ver_name,ver_flags) VALUES(?,?,?)");
			copyResources = conn.prepareStatement("INSERT res (ver_id, res_name , res_mime , res_file) "+
									" SELECT ? , res_name , res_mime , res_file FROM res WHERE ver_id = ?");
			createEmptyResources = conn.prepareStatement("INSERT res (ver_id,res_name,res_mime,res_file) VALUES(?,?,?,\"\")");
			deleteResources = conn.prepareStatement("DELETE FROM res WHERE ver_id=? AND res_name=?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> getApps( boolean incDev ){
		LinkedList<String> retval = new LinkedList<String>();
		try{
		ResultSet rs = getAppList.executeQuery();
		while ( rs.next() ){
			retval.add( rs.getString(1) );
		}
		}catch( SQLException e){
			
		}
		return retval;
	}
	
	public static int getAppId( String name ) throws SQLException{
		getAppId.setString(1, name );
		return DatabaseHandler.singleIntValue( getAppId );
	}
	
	public static int getVersionId( String app , String version ) throws SQLException{
		int appId = getAppId( app );
		if ( appId == -1 ) return -1 ;
		return getVersionId( appId , version );
	}

	public static int getVersionId( int appId , String version ) throws SQLException{
		getVersionId.setInt(1, appId );
		getVersionId.setString(2, version );
		return DatabaseHandler.singleIntValue( getVersionId );
	}
	
	public static List<String> getVersions( String app , boolean incDev ){
		try{
			int appId = getAppId( app );
			if ( appId == -1 ) return null;
			getAppVersions.setInt(1, appId);
			LinkedList<String> retval = new LinkedList<String>();
			ResultSet rs = getAppVersions.executeQuery();
			while ( rs.next() ){
				retval.add( rs.getString(1) );
			}
			return retval;
		}catch( SQLException e){
			return null;
		}
	}

	public static List<ResourceMetadata> getResources(String app, String version) {
		LinkedList<ResourceMetadata> retval = new LinkedList<ResourceMetadata>();
		try{
			int verid = getVersionId( app , version );
			getAppVersionResources.setInt(1 , verid );
			ResultSet rs = getAppVersionResources.executeQuery();
			while ( rs.next() ){
				retval.add( new ResourceMetadata( rs.getString(1) , rs.getString(2) ) );
			}
		}catch( SQLException e){
			e.printStackTrace();
		}
		return retval;
	}

	public static void newApp(String name, String initialVersion, Auth auth) {
		try{
			newApp.setString(1, name);
			newApp.executeUpdate();
			newVersion( name , initialVersion , TEMPLATE_APP , TEMPLATE_VERSION , auth );
		}catch( SQLException e){
			
		}
	}

	public static void newVersion(String app, String version, String prevVersion, Auth auth) {
		newVersion( app, version , app , prevVersion , auth );
	}
	
	public static void newVersion(String app, String version, String prevApp , String prevVersion , Auth auth ) {
		try{
			int appId = getAppId( app );
			if ( appId == -1 )	return;
			newVersion.setInt( 1 , appId );
			newVersion.setString( 2 , version );
			newVersion.setInt( 3 , 0 );
			newVersion.executeUpdate();
			int verid = getVersionId( app , version );
			if ( verid == -1 ) return;
			int copyid = getVersionId( prevApp , prevVersion );
			if ( copyid == -1 ) return;
			
			copyResources.setInt( 1 , verid );
			copyResources.setInt( 2 , copyid );
			
			copyResources.executeUpdate();
			
		}catch( SQLException e){
			
		}
	}

	public static void createEmpty(String app, String version, String name,
			String mime, Auth auth) throws IllegalArgumentException {
		try{
			createEmpty( getVersionId(app,version) , name , mime );
		}catch( SQLException e){
			throw new IllegalArgumentException( e.getMessage() );
		}
	}

	public static void createEmpty(int verId , String name,
			String mime) throws IllegalArgumentException {
		try{
			createEmptyResources.setInt( 1 , verId  );
			createEmptyResources.setString( 2 , name );
			createEmptyResources.setString( 3 , mime );
			createEmptyResources.executeUpdate();
		}catch( SQLException e){
			throw new IllegalArgumentException( e.getMessage() );
		}
	}

	public static void delete(String app, String version, String name, Auth auth) {
		try{
			int ver_id = getVersionId(app,version);
			deleteResources.setInt( 1 , ver_id );
			deleteResources.setString( 2 , name );
			deleteResources.executeUpdate();
		}catch( SQLException e){
			throw new IllegalArgumentException( e.getMessage() );
		}
	}
}
