/**
 * 
 */
package wikidev.client.apptools;

import wikidev.shared.BasicAppToolsData;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * @author chris
 *
 */
public class AppTools implements EntryPoint {


	private final AppToolsServiceAsync apptoolsService = GWT.create(AppToolsService.class);
	private BasicAppToolsData data = null;
	
	/* (non-Javadoc)
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	public void onModuleLoad() {
		registerNative( this );
		
		// Now we create an object to hold our App information
		AppPanel c = new AppPanel( "appPanel" );
		
		// Request the application info
		String request = Window.Location.getPath();
		GWT.log( request );
		apptoolsService.getDetailsFromRequest( request , c );
	}
	
	private native void registerNative( AppTools data ) /*-{
		// create a JavaScript object that can call into these functions
		appInterface = {
				version : $entry(data.@wikidev.client.apptools.AppTools::getVersionNumber() ),
			};
	}-*/ ;

	public String getVersionNumber(){
		if ( data != null ) return data.getVersion();
		return "unknown";
	}
}
