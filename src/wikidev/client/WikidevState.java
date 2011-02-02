package wikidev.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class WikidevState {

	protected LayoutPanel content;
	protected VerticalPanel nav;
	protected HorizontalPanel footer;
	
	protected Wikidev tools;
	
	public WikidevState( Wikidev target ){
		this.tools = target;
	}
	
	public void prepare( VerticalPanel nav , LayoutPanel content , HorizontalPanel footer){
		this.content = content;
		this.nav = nav;
		this.footer = footer;
		content.clear();
		nav.clear();
		footer.clear();
		stateStart();
	}
	
	public abstract void stateStart();
}
