package wikidev.shared;

import java.io.Serializable;

import wikidev.shared.VersionStates.Status;

public class BasicAppToolsData implements Serializable {

	private static final long serialVersionUID = 1L;
	private String appName;
	private String appVersion;
	private Status verisonStatus;
	
	protected BasicAppToolsData(){
		
	}
	
	public BasicAppToolsData( String app , String ver , Status verStatus ){
		appName = app;
		appVersion = ver;
		verisonStatus = verStatus;
	}
	
	public String getAppName(){
		return appName;
	}
	
	public String getVersion(){
		return appVersion;
	}
	
	public Status getVersionStatus(){
		return verisonStatus;
	}
}
