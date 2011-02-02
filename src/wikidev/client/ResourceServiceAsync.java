package wikidev.client;

import java.util.List;

import wikidev.shared.AppMetadata;
import wikidev.shared.ResourceMetadata;
import wikidev.shared.VersionMetadata;
import wikidev.shared.VersionStates.Status;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ResourceServiceAsync {

	void createNewApp(String name, String desc , String initialVersion,
			AsyncCallback<Void> callback);

	void getApps(boolean showBeta, AsyncCallback<List<AppMetadata>> callback);

	void getResource(String app, String version, String name,
			AsyncCallback<String> callback);

	void setResource(String app, String version, String name,
			String data, AsyncCallback<Void> callback);

	void getVersions(String app, boolean showBeta,
			AsyncCallback<List<VersionMetadata>> callback);

	void createNewVersion(String app, String version, String prevVersion,
			AsyncCallback<Void> callback);

	void getResources(String app, String version,
			AsyncCallback<List<ResourceMetadata>> callback);

	void createBlankResource(String app, String version, String name,
			String mime, AsyncCallback<Void> callback);

	void deleteResource(String app, String version, String name,
			AsyncCallback<Void> callback);

	void setVersionState(String app, String version, Status state,
			AsyncCallback<Void> callback);

}
