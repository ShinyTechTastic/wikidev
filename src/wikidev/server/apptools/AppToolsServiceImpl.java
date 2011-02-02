package wikidev.server.apptools;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import wikidev.client.apptools.AppToolsService;
import wikidev.server.data.App;
import wikidev.server.data.Version;
import wikidev.shared.BasicAppToolsData;

public class AppToolsServiceImpl  extends RemoteServiceServlet implements AppToolsService {

	private static final long serialVersionUID = 1L;

	public BasicAppToolsData getDetailsFromRequest(String request) {
		String[] part = request.split("/");
		// note this starts with a /wikidev/res/ so [0,1,2] is not useful
		
		String app = null;
		String version = null;
		String resource = null;
		
		switch ( part.length ){
		case 0:
		case 1:
		case 2:
			// error we need at least app/resource
			return null;
		case 3:
			// assume app/resource
			app = part[3];
			version = ""; // note we go for default version if not found...
			resource = part[4];
			break;
		case 4:
		default:
			// assume app/version/resource
			app = part[3];
			version = part[4];
			resource = part[5];
		}
	
		// find the resource in the database?
		App a = App.findApp( app );
		if ( a == null ) return null;
		Version v = a.getVersion( version );
		if ( v == null )
			v= a.getLatestStable();
		if ( v == null ) return null;
		
		return new BasicAppToolsData(
				a.getName(),
				v.getName(),
				v.getStatus()		);
	}

}
