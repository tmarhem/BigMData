 package tp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * Class for stop-list management.
 * @author Pierre Tirilly
 *
 */
public class StopList {

	private Set<String> stopWords;
	
	/**
	 * Creates a new stop list contained in the given file.
	 * The file must contain a single stop word per line, in ASCII format.
	 * @param fileName The path to the stop-list file
	 */
	public StopList( String fileName ) {
		this.stopWords = this.readStopListFile( fileName );
	}

	/**
	 * Tests if a term is in the stop-list
	 * @param term The term to test
	 * @return True if the term is in the stop-list, false otherwise.
	 */
	public boolean contains( String term ) {
		return this.stopWords.contains( term );
	}
	
	/**
	 * Filters out stop words from a list of terms.
	 * @param terms The list of terms to filter out.
	 * @return The list of terms with stop words removed.
	 */
	public String[] filter( String[] terms ) {
		
		Vector<String> filteredTerms = new Vector<String>();
		
		for ( String s : terms ) {
			if ( !this.contains( s ) ) {
				filteredTerms.add( s );
			}
		}
		
		return filteredTerms.toArray( new String[filteredTerms.size()] );
	}
	
	private Set<String> readStopListFile( String fileName ) {
		
		BufferedReader reader;
		String currentWord;
		Set<String> stopWords = new HashSet<>();
		
		try {
			reader = new BufferedReader( new FileReader(fileName) );
			while ( (currentWord = reader.readLine()) != null ) {
				stopWords.add( new String( currentWord.trim() ) );
			}
			reader.close();
		} catch( IOException ioe ) {
			System.err.println( "Error reading stop list file " + fileName + "." );
			ioe.printStackTrace();
		}
		
		return stopWords;
	}
}
