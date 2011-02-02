package wikidev.client;

import wikidev.shared.AppMetadata;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;

public class AppButton extends Composite {

	public AppButton( AppMetadata app ){
		String appname = app.getName();
		HorizontalPanel panel = new HorizontalPanel();
	    panel.addStyleName("appButton");
	    Anchor a = new Anchor( appname , false , "/wikidev/res/"+appname+"/home" );
	    a.addStyleName("app-button");
	    panel.add( a );
	    Hyperlink e = new Hyperlink( "edit" ,"app:"+appname );
	    e.addStyleName("app-edit");
	    panel.add( e );

	    initWidget(panel);
	    
	    if ( app.isStable() ){
	    	panel.addStyleName( "App-Stable" );
	    }else{
	    	panel.addStyleName( "App-Dev" );
		}
	    
	}
	
}
