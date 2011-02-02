package wikidev.shared;

import java.io.Serializable;

public class AppMetadata implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String desc;
	private boolean stable;
	
	protected AppMetadata(){};

	
	public AppMetadata( String n , String  o , boolean stable ){
		name = n;
		desc = o;
		this.stable = stable;
	};
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return desc;
	}

	public boolean isStable() {
		return stable;
	}
}
