package wikidev.client;

import java.util.List;

import wikidev.shared.ResourceMetadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

public class StateVerInfo extends WikidevState {
	
	private final ResourceServiceAsync resourceService = GWT.create(ResourceService.class);

	private TextArea textArea = null;
	private String currentResource = null;
	private Button preview; 
	
	public StateVerInfo(Wikidev target) {
		super(target);
		
		preview = new Button("Preview");
		preview.setTitle("in a new tab");
		preview.setEnabled( false );
		preview.addClickHandler( new ClickHandler(){

			public void onClick(ClickEvent event) {
				Window.open( "/wikidev/res/"+appName+"/"+versionName+"/"+currentResource, "wikidev-preview" , "" );
			}
			
		});

		textArea = new TextArea();
		textArea.addStyleName("text-editor");
		textArea.setEnabled( false );
		
		textArea.addChangeHandler( new ChangeHandler(){

			public void onChange(ChangeEvent event) {
				if ( currentResource != null ){
					// upload to server
					resourceService.setResource( appName, versionName, currentResource , textArea.getText() ,
							new AsyncCallback<Void>(){

								public void onFailure(Throwable caught) {
									
								}

								public void onSuccess(Void result) {
									
								} 
						
					} );
				}
			}
			
		});
		
	}
	
	public String appName;
	public String versionName;

	public void setApp(String name) {
		appName = name;
	}

	public void setVer(String name) {
		versionName = name;
	}
	
	public void buildRes( final ResourceMetadata res, VerticalPanel target ){	
		target.add( new ResourceComp( res ) );	
	}
	
	public void displayResource(  ResourceMetadata res ){
		displayResource( res.getName() );
	}
	
	public void displayResource( final String res ){
		textArea.setEnabled( false );
		currentResource = null;
		resourceService.getResource(appName, versionName, res, new AsyncCallback<String>(){

			public void onFailure(Throwable caught) {
				
			}

			public void onSuccess(String result) {
				textArea.setText( result );
				currentResource = res;
				textArea.setEnabled( true );
				preview.setEnabled( true );
			}
			
		});
	}
	
	@Override
	public void stateStart() {
		
		content.add( textArea );
		footer.add( preview );
		textArea.setText("");
        textArea.setEnabled( false );
			
		resourceService.getResources(appName, versionName , new AsyncCallback<List<ResourceMetadata>>(){

			public void onFailure(Throwable caught) {
				tools.showMessage("Oh eck",caught.getMessage());
				nav.add( new Hyperlink( "back to AppStore" ,"appstore" ) );
			}

			public void onSuccess(List<ResourceMetadata> result) {
				if ( result != null )
				for( ResourceMetadata res : result ){
					buildRes(res , nav );
				}

				Anchor a = new Anchor( "create new resource" );
				a.addStyleName("bullet newres");
				a.addClickHandler( new ClickHandler(){

					public void onClick(ClickEvent event) {
						doNewResource();
					}
					
				});
				nav.add( a );

				a = new Anchor( "upload new Resource");
				a.addStyleName("bullet uploadres");	
				a.addClickHandler( new ClickHandler(){

					public void onClick(ClickEvent event) {
						doNewUploadResource( "" );
					}
					
				});
				nav.add( a );
				
				Hyperlink hl = new Hyperlink( "back to Application Versions" ,"app:"+appName );
				hl.addStyleName("bullet app");
				nav.add( hl );
				
				hl = new Hyperlink( "back to AppStore" ,"appstore" );
				hl.addStyleName("bullet appstore");
				nav.add( hl );
			}
			
		});
	}


	public void doNewUploadResource( String defname ){
		final DialogBox db = new DialogBox();
		final FormPanel form = new FormPanel();
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		
		db.setTitle("New Resource");
		VerticalPanel vp = new VerticalPanel();
		final TextBox name = new TextBox();
		name.setText( defname );
		name.setName("resname");
		vp.add( new Label("Resource name") );
		vp.add(name);
		FileUpload upload = new FileUpload();
		vp.add( upload );
		upload.setName("data");
		HorizontalPanel hp = new HorizontalPanel();
		Button cancel = new Button("Cancel");
		cancel.addClickHandler( new ClickHandler(){

			public void onClick(ClickEvent event) {
				db.hide();
			}
			
		});
		Button confirm = new Button("Create");
		confirm.addClickHandler( new ClickHandler(){

			public void onClick(ClickEvent event) {
				form.submit();
			}
			
		});
		hp.add(cancel);
		hp.add(confirm);
		vp.add( hp );
		
		vp.add( new Hidden("appName",appName ) );
		vp.add( new Hidden("versionName",versionName ) );
		
		form.add( vp );
		db.add(form);
		
		form.addSubmitHandler( new SubmitHandler(){

			public void onSubmit(SubmitEvent event) {
				form.setAction( "/wikidev/res/"+appName+"/"+versionName+"/"+name.getText() );
			}
			
		});
		
		form.addSubmitCompleteHandler( new SubmitCompleteHandler(){

			public void onSubmitComplete(SubmitCompleteEvent event) {
				db.hide();
				String resname = name.getText();
				tools.showMessage("Upload", "Uploading complete");
				content.clear();
				nav.clear();
				footer.clear();
				stateStart();	// reload resources
				displayResource(resname);
			}
			
		});

		
		db.center();
		
	}

	public void doNewResource(){
		final DialogBox db = new DialogBox();
		db.setTitle("New Resource");
		VerticalPanel vp = new VerticalPanel();
		final TextBox name = new TextBox();
		vp.add( new Label("Resource name") );
		vp.add(name);
		vp.add( new Label("Mime Type") );  
		final ListBox lb = new ListBox();
		lb.addItem("text/html");
		lb.addItem("text/javascript");
		lb.addItem("text/xml");
		lb.addItem("text/css");
		lb.addItem("text/csv");
		lb.addItem("text/plain");
		vp.add( lb );
		HorizontalPanel hp = new HorizontalPanel();
		Button cancel = new Button("Cancel");
		cancel.addClickHandler( new ClickHandler(){

			public void onClick(ClickEvent event) {
				db.hide();
			}
			
		});
		Button confirm = new Button("Create");
		confirm.addClickHandler( new ClickHandler(){

			public void onClick(ClickEvent event) {
				final String mime = lb.getItemText( lb.getSelectedIndex() );
				final String resname = name.getText();
				resourceService.createBlankResource( appName , versionName , resname , mime ,
						new AsyncCallback<Void>(){

							public void onFailure(Throwable caught) {
								db.hide();
								tools.showMessage("New Resource","Failed to create new resource: "+caught.getMessage() );
							}

							public void onSuccess(Void result) {
								db.hide();
								tools.showMessage("New Resource","Created a new resource.." );
								content.clear();
								nav.clear();
								footer.clear();
								stateStart();	// reload resources
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
	
	private class ResourceComp extends Composite {

		public ResourceComp(final ResourceMetadata res) {
			HorizontalPanel target = new HorizontalPanel();
			
			Anchor a = new Anchor( res.getName() );
			a.addStyleName("bullet resource resource-"+res.getSimpleMimeType() );
				a.addClickHandler( new ClickHandler(){
	
					public void onClick(ClickEvent event) {
						if ( res.isEditable() ){
							displayResource( res );
						}else{
							doNewUploadResource( res.getName() );
						}
					}
				});
			target.add( a );
			Anchor del = new Anchor( "[del]" );
			del.addStyleName( "deleteRes" );
			del.addClickHandler( new ClickHandler(){

				public void onClick(ClickEvent event) {
					deleteResource( res );
				}
				
			});
			target.add( del );
			
			Anchor mod = new Anchor( "[edit]" );
			mod.addStyleName( "editRes" );
			mod.addClickHandler( new ClickHandler(){

				public void onClick(ClickEvent event) {				
					modifyResource( res );
				}
				
			});
			target.add( mod );
				
		    initWidget(target);
		}
		
	}

	private void deleteResource( final ResourceMetadata res  ){
		
		tools.showYesNo("Delete resource","Are you sure you want to remove resource \""+res.getName()+"\"?",
				new YesNoCallback(){ 
		
			public void onYes(){
				resourceService.deleteResource( appName , versionName, res.getName() , new AsyncCallback<Void>(){
		
					public void onFailure(Throwable caught) {
						
					}
		
					public void onSuccess(Void result) {
						content.clear();
						nav.clear();
						footer.clear();
						stateStart();	// reload resources
					}
				});
			};
		});
	}
	
	private void modifyResource( final ResourceMetadata res  ){
		// TODO: Implement this
	}
}
