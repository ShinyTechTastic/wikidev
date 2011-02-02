package wikidev.shared;

public class VersionStates {

	public enum Status {
		DEV( "Developers only" ),
		ALPHA( "Alpha release (no support)" ),
		BETA( "Beta release (some support)"),
		STABLE( "Usable version" ),
		DEPRECIATED( "Usage not recommended" );
		
		private String desc;
		
		private Status( String s ){
			desc = s;
		}
		
		public String getDescription(){
			return desc;
		}
	}

}
