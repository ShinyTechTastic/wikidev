package wikidev.shared;

import java.io.Serializable;

public class VersionMetadata implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String name;
	public VersionStates.Status state;
	public String author;
}
