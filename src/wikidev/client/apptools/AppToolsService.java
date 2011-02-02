package wikidev.client.apptools;

import wikidev.shared.BasicAppToolsData;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("apptools")
public interface AppToolsService extends RemoteService{

	public BasicAppToolsData getDetailsFromRequest( String request );
}
