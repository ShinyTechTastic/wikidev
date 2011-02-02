package wikidev.client;

import java.util.List;

import wikidev.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Wikidev implements EntryPoint, ValueChangeHandler<String> {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	private LayoutPanel header = new LayoutPanel();
	private HorizontalPanel footer = new HorizontalPanel();
	private VerticalPanel nav = new VerticalPanel();
	private LayoutPanel content = new LayoutPanel();
	
	private DialogBox dialogBox;
	private HTML serverResponseLabel = new HTML();
	private Button closeButton;
	private Button yesButton;
	private Button noButton;
	private HTML title = new HTML("Please wait.");
	
	private YesNoCallback diloagCallback = null;
	
	public void showMessage( String title , String message ){
		dialogBox.setText( title );
		serverResponseLabel.setHTML(message);
		closeButton.setVisible( true );
		yesButton.setVisible( false );
		noButton.setVisible( false );
		dialogBox.center();
		closeButton.setFocus(true);
	}
	
	public void setTitle( String s ){
		title.setHTML( s );
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		DockLayoutPanel p = new DockLayoutPanel(Unit.EM);
		ScrollPanel sp = new ScrollPanel();
		sp.add( nav );
		p.addNorth( header, 2);
		p.addSouth( footer, 2);
		p.addWest( sp , 10);
		p.add( content );
		RootLayoutPanel.get().add(p);
		
		header.add( title );
		footer.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);

		
		// Create the popup dialog box
		dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		
		closeButton.addClickHandler( new ClickHandler(){

			public void onClick(ClickEvent event) {
				dialogBox.removeFromParent();
			}
			
		} );

		yesButton = new Button("Yes");
		// We can set the id of a widget by accessing its Element
		yesButton.getElement().setId("yesButton");
		
		yesButton.addClickHandler( new ClickHandler(){

			public void onClick(ClickEvent event) {
				dialogBox.removeFromParent();
				diloagCallback.onYes();
			}
			
		} );

		noButton = new Button("No");
		// We can set the id of a widget by accessing its Element
		noButton.getElement().setId("noButton");
		
		noButton.addClickHandler( new ClickHandler(){

			public void onClick(ClickEvent event) {
				dialogBox.removeFromParent();
				diloagCallback.onNo();
			}
			
		} );

		
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogVPanel.add(yesButton);
		dialogVPanel.add(noButton);
		dialogBox.setWidget(dialogVPanel);
		
	    String initToken = History.getToken();
	    if (initToken.length() == 0) {
	      History.newItem("appstore");
	    }
	    
	    // Add history listener
	    History.addValueChangeHandler(this);

	    // Now that we've setup our listener, fire the initial history state.
	    History.fireCurrentHistoryState();
	}
	
	private final StateAppStore store = new StateAppStore( this );
	private final StateAppInfo  appInfo = new StateAppInfo( this );
	private final StateVerInfo  verInfo = new StateVerInfo( this );
	
	public void onValueChange(ValueChangeEvent<String> event) {
		String historyKey = event.getValue();
		
		if ( historyKey.equalsIgnoreCase("appstore") ){
			// show the store window
			setTitle("App Store");
			store.prepare( nav , content , footer );
		}else if ( historyKey.startsWith("app:") ){
			// show the app info window
			setTitle("App info");
			appInfo.setApp( historyKey.substring(4) );
			appInfo.prepare( nav , content , footer );
		}else if ( historyKey.startsWith("appVer:") ){
			setTitle("Version info");
			// show the app verison info window
			String[] s = historyKey.substring(7).split(":");
			if ( s.length > 1 ){
				verInfo.setApp( s[0] );
				verInfo.setVer( s[1] );
				verInfo.prepare( nav , content , footer );
			}
		}
	}

	public void goToState( String s ){
		History.newItem(s);
	    History.fireCurrentHistoryState();
	}

	public void showYesNo(String title , String message, YesNoCallback yesNoCallback) {
		dialogBox.setText( title );
		diloagCallback = yesNoCallback;
		serverResponseLabel.setHTML(message);
		dialogBox.center();
		closeButton.setVisible( false );
		yesButton.setVisible( true );
		yesButton.setVisible( true );
	}
}
