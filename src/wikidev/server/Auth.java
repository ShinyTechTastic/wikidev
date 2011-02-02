package wikidev.server;

import javax.servlet.http.HttpServletRequest;

import wikidev.server.data.UserDetails;
import wikidev.server.db.DatabaseHandler;

public abstract class Auth {

	public static Auth getCurrentUser( HttpServletRequest r ){
		return DatabaseHandler.getUser( (String)r.getAttribute("user" ) );
	}
		
	public abstract String getName();
	public abstract int getId();

}
