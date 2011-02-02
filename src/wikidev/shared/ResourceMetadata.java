package wikidev.shared;

import java.io.Serializable;

public class ResourceMetadata implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String mime;
	
	public ResourceMetadata(){
		name = "";
		mime = "";
	}
	
	public ResourceMetadata( String name , String mime ){
		this.name = name;
		this.mime = mime;
	}
	
	public String getName(){
		return name;
	}

	public String getMimeType(){
		return mime;
	}

	public String getSimpleMimeType() {
		if ( mime == null ) return "text";
		// this returns a simple version of the mime type...
		if ( mime.contains("html") ) return "html";
		if ( mime.contains("js") ) return "js";
		if ( mime.contains("javascript") ) return "js";
		if ( mime.contains("xml") ) return "xml";
		if ( mime.contains("css") ) return "css";
		if ( mime.contains("csv") ) return "csv";

		if ( mime.contains("text") ) return "text";
		return "unknown";
	}

	public boolean isEditable() {
		if ( mime.contains("text") ) return true;
		
		return false;
	}
}
