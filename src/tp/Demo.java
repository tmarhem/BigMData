package tp;

import java.util.Vector;

import tp.stemmer.Stemmer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;


/**
 * Dome: reading and printing a collection
 * @author Pierre Tirilly - pierre.tirilly@telecom-lille.fr
 *
 */
public class Demo {
	
	// File contianing the collection to load

	private final static String WIKIFR = "data/frwiki_100K.trec";
	private final static String WIKIEN = "data/enwiki_100k.trec";
	private final static String REUTERS = "data/reuters-21578.trec";
	
	private final static String ENSTOP = "data/stopListEnglish.txt";
	private final static String FRSTOP = "data/stopListFrancais.txt";
	
	
	public static void main( String[] args ) {
		
		// Load the collection file
		Vector<DocumentInfo> collection = CollectionReader.readDatabaseFile( REUTERS );
		

		//Tokenizer /  Stoplist/  Stemmer /vocabulary /wordbag
		String regex = " ,.;:()'\"<>";
		Tokenizer myTokenizer = new Tokenizer(regex);
		StopList enStopList = new StopList(ENSTOP);
		Stemmer myStemmer = new Stemmer(Stemmer.StemmerLanguage.ENGLISH);
		Vocabulary myVocabulary = new Vocabulary();
		WordBag myWordBag = new WordBag();

		String[] myTokens = null; 
		
		
		for(DocumentInfo document : collection) {
			myTokens = myTokenizer.tokenize(document.getContent().toLowerCase(Locale.forLanguageTag("en")));
			myTokens = enStopList.filter(myTokens);
			myTokens = myStemmer.stem(myTokens);
			myVocabulary.getVocabulary(myTokens);
			//myWordBag.addWordBag(document.id, myVocabulary);
		}
		
		
		//Print resultat vocabulaire
		for(Entry<String,Integer> e : myVocabulary.entrySet()) {
			System.out.println(e.getKey() +"   :"+ e.getValue());
		}
		System.out.println("");
		System.out.println("taille vocabulaire: " + myVocabulary.getSize());
		System.out.println("nb occurence mot 'le': "+myVocabulary.getFreq("le"));
		System.out.println("nb hapax: " + myVocabulary.getHapaxFreq());
		System.out.println("wordbags number: " + myWordBag.getSize());
		System.out.println("highest entry: " + myVocabulary.getHighest() + "  :" + myVocabulary.getFreq(myVocabulary.getHighest()));


		
		
		
	}
}
