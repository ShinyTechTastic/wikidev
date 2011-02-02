package wikidev.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MagicDialog extends DialogBox {

	private Map<String,Button> buttons;

	private HorizontalPanel buttonPane;
	
	private Callback callback = null;
	 
	
	public MagicDialog( String title , Widget[] components , String[] buttons ){
		super();
		this.setText( title );
	      // Enable animation.
	      setAnimationEnabled(true);

	      // Enable glass background.
	      setGlassEnabled(true);


		buttonPane = new HorizontalPanel();

		VerticalPanel vp = new VerticalPanel();
		
		for ( Widget w : components ){
			vp.add(w);
		}
		
		this.buttons = new HashMap<String,Button>();
		
		for( final String s : buttons ){
			Button b = new Button( s );
			this.buttons.put( s , b );
			buttonPane.add( b );
			b.addClickHandler( new ClickHandler(){

				public void onClick(ClickEvent event) {
					MagicDialog.this.hide();
					callback.buttonPressed( s );
				}
				
			});
		}
		vp.add( buttonPane );
		
		this.add(vp);
	}
	
	public void go( Callback c ){
		callback = c;
		center();
	}
	
	
	public interface Callback{
		public abstract void buttonPressed( String s );
	}
}
