package tp.stemmer;

import tp.stemmer.org.tartarus.snowball.SnowballStemmer;

/**
 * Multilingual stemmer.
 * This is a simple wrapper for the java versions of the Snowball stemmers
 * available at: snowball.tartarus.org.
 * @author Pierre Tirilly - pierre.tirilly@telecom-lille.fr
 *
 */
public class Stemmer {

	/**
	 * Available language and path to their classes.
	 * @author Pierre Tirilly - pierre.tirilly@telecom-lille.fr
	 * 
	 */
	public enum StemmerLanguage {
			DANISH("tp.stemmer.org.tartarus.snowball.ext.danishStemmer"),
			DUTCH("tp.stemmer.org.tartarus.snowball.ext.dutchStemmer"),
			ENGLISH("tp.stemmer.org.tartarus.snowball.ext.porterStemmer"),
			FINNISH("tp.stemmer.org.tartarus.snowball.ext.finnishStemmer"),
			FRENCH("tp.stemmer.org.tartarus.snowball.ext.frenchStemmer"),
			GERMAN("tp.stemmer.org.tartarus.snowball.ext.germanStemmer"), 
			HUNGARIAN("tp.stemmer.org.tartarus.snowball.ext.hungarianStemmer"), 
			ITALIAN("tp.stemmer.org.tartarus.snowball.ext.italianStemmer"),
			NORWEGIAN("tp.stemmer.org.tartarus.snowball.ext.norwegianStemmer"), 
			PORTUGUESE("tp.stemmer.org.tartarus.snowball.ext.portugueseStemmer"),
			ROMANIAN("tp.stemmer.org.tartarus.snowball.ext.romanianStemmer"),
			RUSSIAN("tp.stemmer.org.tartarus.snowball.ext.russianStemmer"), 
			SPANISH("tp.stemmer.org.tartarus.snowball.ext.spanishStemmer"),
			SWEDISH("tp.stemmer.org.tartarus.snowball.ext.swedishStemmer"), 
			TURKISH("tp.stemmer.org.tartarus.snowball.ext.turkishStemmer");
		
			private String className;
			
			StemmerLanguage( String className ) {
				this.className = className;
			}
			
			String getClassName() {
				return this.className;
			}
	};
	
	private SnowballStemmer stemmer;
	
	private StemmerLanguage language;
	
	/**
	 * Creates a new stemmer for the provided language.
	 * Example of use: new Stemmer( Stemmer.StemmerLanguage.FRENCH )
	 * @param language The language handled by the stemmer.
	 */
	public Stemmer( StemmerLanguage language ) {
		this.language = language;
		try {
			this.stemmer = (SnowballStemmer)(Class.forName(this.language.getClassName())
												.getConstructor().newInstance());
		} catch ( Exception e ) {
			System.err.println( "Error: unable to load stemmer class. Please your packages"
					+ "and their folder hierarchy.");
			e.printStackTrace();
			this.stemmer = null;
		}
	}
	
	/**
	 * Stems a single token.
	 * @param token The token to stem.
	 * @return The token after stemming.
	 */
	public String stem( String token ) {
		this.stemmer.setCurrent( token );
		this.stemmer.stem();
		return this.stemmer.getCurrent().trim();
	}
	
	/**
	 * Stems a list of tokens.
	 * @param tokens The list of tokens to stem.
	 * @return The list after stemming of all tokens.
	 */
	public String[] stem( String[] tokens ) {
		String[] stemmed = new String[tokens.length];
		for ( int i = 0 ; i < tokens.length ; i++ ) {
			stemmed[i] = this.stem( tokens[i] );
		}
		return stemmed;
	}
	
}
