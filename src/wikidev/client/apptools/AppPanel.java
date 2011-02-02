package wikidev.client.apptools;

import wikidev.shared.BasicAppToolsData;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

public class AppPanel implements AsyncCallback<BasicAppToolsData> {

	
	private HTML target;
	
	public AppPanel( String id ){
		target = HTML.wrap( DOM.getElementById(id) );
		target.setHTML( "Loading..." );
	}

	public void onFailure(Throwable caught) {
		target.setHTML("Failed");
	}

	public void onSuccess(BasicAppToolsData result) {
		if ( result != null ){
			String html = "<span class='AppName'>"+ result.getAppName()+"</span><br>";
			html += "<span class='VersionName'>"+ result.getVersion()+"</span><br>";
			
			target.addStyleName( "AppPanel-"+result.getVersionStatus().toString() );
			String text = "";
			switch( result.getVersionStatus() ){
				case DEV:	text = "Warning! Development Version";		break;
				case ALPHA:	text = "Alpha Test Version";		break;
				case BETA:	text = "Beta Test Version";		break;
				case STABLE:	text = "Stable Version";		break;
				case DEPRECIATED:	text = "Depreciated Version";		break;
			}
			html += "<a class='Details' href='/Wikidev.html?#app:"+result.getAppName()+"'>"+ text+"</span>";
			
			target.setHTML( html );
		}else{
			onFailure( null );
		}
	}
}
