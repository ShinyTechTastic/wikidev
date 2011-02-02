package wikidev.server.data;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

import wikidev.server.Auth;
import wikidev.server.db.DatabaseHandler;

public class Revision {

	public static Revision getById(int rev_id, Resource resource) {
		return new Revision( rev_id );	
	}

	private static java.sql.Connection conn = DatabaseHandler.getConnection();

	private int rev_id;
	private String mime;
	
	private Revision( int rev_id ){
		this.rev_id = rev_id;
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( "SELECT mime FROM rev WHERE rev_id = ?" );
			s.setInt( 1 , rev_id );
			ResultSet rs = s.executeQuery();
			if( rs.next() ){
				mime = rs.getString(1);
			}
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
	}
	
	public String getDataString(){
		try {
			return convertStreamToString( getDataInputStream() );
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getMimeType(){
		return mime;
	}
	
	public InputStream getDataInputStream(){
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( "SELECT file FROM rev WHERE rev_id = ?" );
			s.setInt( 1 , rev_id );
			ResultSet rs = s.executeQuery();
			if( rs.next() ){
				return rs.getBinaryStream(1);
			}
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
		return null;
	}
	
	
	public Revision branch( String mime , Auth a ){
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( "INSERT rev( mime , file , rev_parent , user_id ) VALUES ( ? , NULL , ? , ? )"  , Statement.RETURN_GENERATED_KEYS );
			s.setString( 1 , mime );
			s.setInt( 2 , rev_id );
			s.setInt( 3 , a.getId() );
			s.execute();
			ResultSet rs= s.getGeneratedKeys();
			rs.next();
			int newKey = rs.getInt(1);
			return Revision.getById( newKey , null );
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
		return null;
	}

	public static Revision create(String mime, InputStream data, Auth auth) {
		try{
			java.sql.PreparedStatement s = conn.prepareStatement( "INSERT rev( mime , file , rev_parent , user_id ) VALUES ( ? , ? , NULL , ? )" , Statement.RETURN_GENERATED_KEYS );
			s.setString( 1 , mime );
			s.setBinaryStream( 2 , data );
			s.setInt( 3 , auth.getId() );
			s.execute();
			ResultSet rs = s.getGeneratedKeys();
			rs.next();
			int newKey = rs.getInt(1);
			return Revision.getById( newKey , null );
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
		return null;
	}
	
	public int getId() {
		return rev_id;
	}

	
	/**
	 * These methods should only be called on a newly branched Revision
	 * @param data
	 * @return
	 */
	public Revision set(String data) {
		return set( createInputStreamFromString(data) ); 
	}

	public Revision set(InputStream data) {
		try{
			java.sql.PreparedStatement s = conn.prepareStatement(
					"UPDATe rev SET file = ? WHERE rev_id = ? AND file is null" );
			s.setBinaryStream( 1 , data );
			s.setInt( 2 , rev_id );
			s.execute();
		}catch(SQLException e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
		return this;
	}


    public static final String convertStreamToString(InputStream is)
            throws IOException {
        /*
         * To convert the InputStream to String we use the
         * Reader.read(char[] buffer) method. We iterate until the
         * Reader return -1 which means there's no more data to
         * read. We use the StringWriter class to produce the string.
         */
        if (is != null) {
            Writer writer = new StringWriter();
 
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {       
            return "";
        }
    }

    public static final InputStream createInputStreamFromString( String s ){
		try {
			   InputStream is = new ByteArrayInputStream(s.getBytes("UTF-8"));
			   return is;
			} catch (UnsupportedEncodingException e) {
			           e.printStackTrace();
			}
			return null;
	}



}
