package wikidev.client.apptools;

import wikidev.shared.BasicAppToolsData;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AppToolsServiceAsync {

	void getDetailsFromRequest(String request,
			AsyncCallback<BasicAppToolsData> callback);

}
