package wikidev.server;

import java.util.LinkedList;
import java.util.List;

import wikidev.client.ResourceService;
import wikidev.server.data.App;
import wikidev.server.data.Resource;
import wikidev.server.data.Revision;
import wikidev.server.data.Version;
import wikidev.shared.AppMetadata;
import wikidev.shared.BadNameException;
import wikidev.shared.FieldVerifier;
import wikidev.shared.ResourceMetadata;
import wikidev.shared.VersionMetadata;
import wikidev.shared.VersionStates.Status;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ResourceServiceImpl extends RemoteServiceServlet implements
		ResourceService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void createNewApp(String name, String desc , String initialVersion) throws BadNameException {
		if ( ! FieldVerifier.isValidName(name) ) throw new BadNameException();
		App.createApp(name,desc , initialVersion, Auth.getCurrentUser( this.getThreadLocalRequest() ) ) ;
	}

	public void createNewVersion(String app, String version , String prevVersion) throws BadNameException {
		App a = App.findApp( app );
		if ( ! FieldVerifier.isValidName(version) ) throw new BadNameException();
		a.createVersion(version, prevVersion,  Auth.getCurrentUser( this.getThreadLocalRequest() )  );
	}

	public List<AppMetadata> getApps(boolean showBeta) {
		List<App> apps =  App.findAllApps( showBeta );
		List<AppMetadata> retval = new LinkedList<AppMetadata>();
		for ( App a : apps )
			retval.add( new AppMetadata( 
					a.getName() , a.getDescription() , 
					(a.getLatestStable() != null ) ) );
		return retval;
	}

	public String getResource(String app, String version, String name)
			throws IllegalArgumentException {
		App a = App.findApp( app );
		Version v = a.getVersion( version );
		Resource r = v.getResource( name );
		return r.getRevision().getDataString();
	}

	public void setResource(String app, String version, String name, String data)
			throws IllegalArgumentException {
		App a = App.findApp( app );
		Version v = a.getVersion( version );
		Resource res = v.getResource( name );
		res.update( res.getRevision().getMimeType() , data, Auth.getCurrentUser( this.getThreadLocalRequest() ) );
	}

	public List<VersionMetadata> getVersions(String app, boolean showBeta) {
		App a = App.findApp( app );
		List<Version> versions = a.getVersions();
		List<VersionMetadata> retval = new LinkedList<VersionMetadata>();
		for ( Version v : versions ){
			VersionMetadata vm = new VersionMetadata();
			vm.author = v.getName();
			vm.name = v.getName();
			vm.state = v.getStatus();
			retval.add( vm );
		}
		return retval;
	}

	public List<ResourceMetadata> getResources(String app, String version ) {
		App a = App.findApp( app );
		Version v = a.getVersion( version );
		List<ResourceMetadata> rmd = new LinkedList<ResourceMetadata>();
		for( Resource r : v.getResources() ){
			rmd.add( new ResourceMetadata( r.getName() , r.getRevision().getMimeType() ) );
		}
		return rmd;
	}

	public void createBlankResource(String app, String version, String name,
			String mime) throws BadNameException {
		if ( ! FieldVerifier.isValidName(name) ) throw new BadNameException();
		App a = App.findApp( app );
		Version v = a.getVersion( version );
		v.createResource(name, mime, Revision.createInputStreamFromString(""), Auth.getCurrentUser( this.getThreadLocalRequest() ) );
	}

	public void deleteResource(String app, String version, String name) {
		App a = App.findApp( app );
		Version v = a.getVersion( version );
		v.removeResource( name );
	}

	public void setVersionState(String app, String version, Status state) {
		App a = App.findApp( app );
		Version v = a.getVersion( version );
		v.setStatus(state, Auth.getCurrentUser( this.getThreadLocalRequest() ) );
	}
	
	
}
