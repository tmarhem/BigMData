package tp2.searchEngine;

import tp2.gui.SearchEngineUI;

public class SearchEngineLauncher {

	public static void main( String[] args ) {
		
		//TODO: initialize search engine
		final SearchEngineImpl se = new SearchEngineImpl();
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new SearchEngineUI( se );
            }
        });	
	}
}
