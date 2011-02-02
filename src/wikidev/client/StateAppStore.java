package wikidev.client;

import java.util.List;

import wikidev.shared.AppMetadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class StateAppStore extends WikidevState {
	
	private final ResourceServiceAsync resourceService = GWT.create(ResourceService.class);

	public StateAppStore(Wikidev target) {
		super(target);
	}

	@Override
	public void stateStart() {
		final FlowPanel target = new FlowPanel();
		content.add( new ScrollPanel(target) );
		resourceService.getApps( false , new AsyncCallback<List<AppMetadata>>(){
			
			public void onFailure(Throwable caught) {
				tools.showMessage("Oh eck",caught.getMessage());
			}

			public void onSuccess(List<AppMetadata> result) {
				for( AppMetadata appname : result ){
					AppButton ab = new AppButton( appname );
					target.add( ab );
				}
				
				// Create new app button
				Anchor a = new Anchor( "create new App" );
				a.addClickHandler( new ClickHandler(){

					public void onClick(ClickEvent event) {
						showNewAppDialog();
					}
					
				});
				a.addStyleName("bullet newapp");
				nav.add( a );
			}
			
		});
	}

	public void showNewAppDialog(){

		final TextBox appName = new TextBox();
		final RichTextArea description = new RichTextArea();
		final Button create = new Button("Create");
		
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dialogVPanel.add(new Label("name:") );
		dialogVPanel.add(appName);
		dialogVPanel.add(new Label("description:") );
		dialogVPanel.add(description);
		dialogVPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		dialogVPanel.add(create);

		final DialogBox db = new DialogBox( true , true );
		db.setTitle("New Application");
		db.add( dialogVPanel );

		create.addClickHandler( new ClickHandler(){

			public void onClick(ClickEvent event) {
				resourceService.createNewApp( appName.getText(), description.getText() , "Initital", new AsyncCallback<Void>(){

					public void onFailure(Throwable caught) {
						tools.showMessage("Failed to create a new applcation","sorry. "+caught.getMessage() );
						db.hide();
					}

					public void onSuccess(Void result) {
						tools.showMessage("Applcation created!","Your application has now been created...");
						tools.goToState("app:"+appName.getText() );
						db.hide();
					}
					
				});
				
			}
			
		});
		db.center();
		}
}
