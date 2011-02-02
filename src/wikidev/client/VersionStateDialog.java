package wikidev.client;

import wikidev.shared.VersionStates;
import wikidev.shared.VersionStates.Status;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class VersionStateDialog {

	private static String[] buttons = { "Yes" , "No" };
	private StateWidget[] option;
	private Label label = new Label();
	private MagicDialog md;
	
	public VersionStateDialog(String title ) {
		md = new MagicDialog(title, initComponents() , buttons);
	}

	public void show( final String app , final String version , Status state, final ResourceServiceAsync rs , final AsyncCallback<Void> target){
		label.setText("Set version for "+app+" v "+version );
		
		for ( int i=0 ; i<option.length;i++){
			option[i].selectIf( state );
		}		
		
		md.go( new MagicDialog.Callback(){

			public void buttonPressed(String s) {
				if ( s.equalsIgnoreCase("Yes") ){
					// do the version detail update.
					Status status = Status.DEV;
					for ( StateWidget sw : option )
						if ( sw.getValue() ) status = sw.val;
					
					rs.setVersionState( app , version , status, target );
				}
			}
			
		});
	}
	
	private final Widget[] initComponents(){
		Status[] v = VersionStates.Status.values();
		Widget[] c = new Widget[ v.length + 1];
		option = new StateWidget[ v.length ];
		c[0] = label;
		for ( int i=0 ; i<v.length;i++){
			option[i] = new StateWidget( v[i] );
			c[i+1] = option[i];
		}
		return c;
	}
	
	private class StateWidget extends RadioButton{

		public final Status val;
		
		public StateWidget(Status v) {
			super( "VersionStateGroup" , v.toString() );
			val = v; 
		}

		public void selectIf(Status state) {
			this.setValue( val.equals(state) );
		}
	}
}

