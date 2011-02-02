package wikidev.client;

import java.util.List;

import wikidev.shared.ResourceMetadata;
import wikidev.shared.VersionMetadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class StateAppInfo extends WikidevState {
	
	private final ResourceServiceAsync resourceService = GWT.create(ResourceService.class);

	public StateAppInfo(Wikidev target) {
		super(target);
	}
	
	public String appName;
	private List<VersionMetadata> versions = null;

	public void setApp(String name) {
		appName = name;
	}
	
	public FlowPanel target;
	
	@Override
	public void stateStart() {
		target = new FlowPanel();
		content.add( new ScrollPanel(target) );
		refreshDisplay();
	}
	
	public void refreshDisplay(){
		nav.clear();
		target.clear();
		
		Anchor a = new Anchor( "new Version" );
		a.addStyleName("bullet newver");
		a.addClickHandler( new ClickHandler(){

			public void onClick(ClickEvent event) {
				doNewVersionDialog();
			}
			
		});
		nav.add( a );
		
		Hyperlink hl = new Hyperlink( "back to AppStore" ,"appstore" );
		hl.addStyleName("bullet appstore");
		nav.add( hl );
		resourceService.getVersions( appName ,  false , new AsyncCallback<List<VersionMetadata>>(){

			public void onFailure(Throwable caught) {
				tools.showMessage("Oh eck",caught.getMessage());
				nav.add( new Hyperlink( "back to AppStore" ,"appstore" ) );
			}

			public void onSuccess(List<VersionMetadata> result) {
				if ( result != null ){
					versions = result;
					for( VersionMetadata version : result ){
						target.add( new VersionComp( version ) );
					}
				}
			}
			
		});

	}

	public void doNewVersionDialog(){
		final DialogBox db = new DialogBox();
		db.setTitle("New Version");
		VerticalPanel vp = new VerticalPanel();
		final TextBox name = new TextBox();
		vp.add(name);
		vp.add( new Label("You must select which version you wish to satrt from.") );  
		final ListBox lb = new ListBox();
		if ( versions != null )
			for ( VersionMetadata s : versions ){
				lb.addItem( s.name );
			}
		vp.add( lb );
		HorizontalPanel hp = new HorizontalPanel();
		Button cancel = new Button("Cancel");
		cancel.addClickHandler( new ClickHandler(){

			public void onClick(ClickEvent event) {
				db.removeFromParent();
			}
			
		});
		Button confirm = new Button("Create");
		confirm.addClickHandler( new ClickHandler(){

			public void onClick(ClickEvent event) {
				final String prevVersion = versions.get( lb.getSelectedIndex() ).name;
				final String newVersion = name.getText();
				resourceService.createNewVersion( appName , newVersion,
						prevVersion, new AsyncCallback<Void>(){

							public void onFailure(Throwable caught) {
								db.removeFromParent();
								tools.showMessage("New Version","Failed to create new version: "+caught.getMessage() );
								StateAppInfo.this.refreshDisplay();
							}

							public void onSuccess(Void result) {
								db.removeFromParent();
								tools.showMessage("New Version","Create a new version.." );
								tools.goToState( "appVer:"+appName+":"+newVersion );
							}
					
				});
			}
			
		});
		hp.add(cancel);
		hp.add(confirm);
		vp.add( hp );
		db.add(vp);
		db.center();
	}
	
	private class VersionComp extends Composite {

		public VersionComp(final VersionMetadata res) {
			HorizontalPanel target = new HorizontalPanel();
			
/*
			Hyperlink hl = new Hyperlink(  version ,"appVer:"+appName+":"+version );
			
			hl.addStyleName("bullet ver");
			target.add( hl );
	*/		
			Hyperlink hl = new Hyperlink(  res.name ,"appVer:"+appName+":"+res.name );
			hl.addStyleName("bullet ver ver-"+res.state.toString() );
			target.add( hl );
			target.add( hl );
			
			Anchor mod = new Anchor( "[mod]" );
			mod.addStyleName( "editVer" );
			mod.addClickHandler( new ClickHandler(){

				public void onClick(ClickEvent event) {
					VersionStateDialog vsd = new VersionStateDialog( "Change version "+res.name );
					vsd.show( appName , res.name, res.state , resourceService , new AsyncCallback<Void>(){

						public void onFailure(Throwable caught) {
							GWT.log( caught.getMessage() );
						}

						public void onSuccess(Void result) {
							StateAppInfo.this.refreshDisplay();
						}
						
					});
				}
				
			});
			target.add( mod );
				
		    initWidget(target);
		}
		
	}

}
