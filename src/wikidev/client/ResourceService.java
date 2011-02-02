package wikidev.client;

import java.util.List;

import wikidev.shared.AppMetadata;
import wikidev.shared.BadNameException;
import wikidev.shared.ResourceMetadata;
import wikidev.shared.VersionMetadata;
import wikidev.shared.VersionStates.Status;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("resource")
public interface ResourceService extends RemoteService {
	String getResource( String app , String version , String name) throws IllegalArgumentException;
	void setResource( String app , String version , String name , String data) throws IllegalArgumentException;
	
	public List<AppMetadata> getApps( boolean showBeta );
	public List<VersionMetadata> getVersions( String app , boolean showBeta );
	public List<ResourceMetadata> getResources( String app , String version );
	public void createNewApp( String name , String desc , String initialVersion ) throws BadNameException;
	public void createNewVersion( String app , String version , String prevVersion ) throws BadNameException;
	public void createBlankResource( String app , String version , String name , String mime ) throws BadNameException;
	public void deleteResource( String app , String version , String name );
	public void setVersionState( String app , String version , Status state );
}
