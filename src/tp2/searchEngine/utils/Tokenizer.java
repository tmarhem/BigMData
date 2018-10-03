package tp2.searchEngine.utils;

import java.util.regex.Pattern;

/**
 * A quick tokenizer for natural text data.
 * Separators can be expressed as a string containing a list of chars
 * or as a regular expression.
 * @author Pierre Tirilly - pierre.tirilly@telecom-lille.fr
 *
 */
public class Tokenizer {
	
	// List of special characters to be escaped when translating lists of separators to a regex
	private static final char[] REGEX_SPECIAL_CHARS = { '+', '-', '*', '|', '&', '[', ']', '(', ')', '{', '}', '^', '?' };
	
	private String separatorsRegex;
	
	/**
	 * Creates a tokenizer instance with separators given as a list (string) of characters.
	 * @param separators A string containing the separators.
	 */
	public Tokenizer( String separators ) {
		this( separators, false );
	}
	
	/**
	 * Creates a tokenizer from a list of separators or a regex expressing the separators.
	 * @param separators List (as a string) of separator chars of regular expression
	 * expressing the separators.
	 * @param regexSeparators True if separators is a regex, false if it is a list.
	 */
	public Tokenizer( String separators, boolean regexSeparators ) {
		if ( regexSeparators ) {
			this.separatorsRegex = separators;
		} else {
			this.separatorsRegex = this.compileSeparatorsToRegex( separators );
		}
	}
	
	/**
	 * Tokenizes a string into an array of strings according to the tokenizer's separators.
	 * @param text The string to tokenize.
	 * @return Array of tokens.
	 */
	public String[] tokenize( String text ) {
		return text.split( this.separatorsRegex );
	}

	private String compileSeparatorsToRegex( String separators ) {
		String regex = "[";
		
		for ( int i = 0 ; i < separators.length() ; i++ ) {
			if ( this.isRegexSpecialChar( separators.charAt(i) ) ) {
				regex += Pattern.quote( "" + separators.charAt(i) );
			} else {
				regex += separators.charAt( i );
			}
		}
		regex += "]+";
		
		return regex;
	}
	
	private boolean isRegexSpecialChar( char c ) {
		boolean found = false;
		
		for ( int i = 0 ; i < REGEX_SPECIAL_CHARS.length && !found ; i++ ) {
			found = ( c == REGEX_SPECIAL_CHARS[i] );
		}
		
		return found;
	}
	
}
