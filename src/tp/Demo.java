package tp;

import java.util.Vector;

import tp.stemmer.Stemmer;
import tp2.searchEngine.Similarity;

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
		
		HashMap<Integer,WordBag> docList = new HashMap<Integer,WordBag>();
		
		//corpus
		for(DocumentInfo document : collection) {
			myTokens = myTokenizer.tokenize(document.getContent().toLowerCase(Locale.forLanguageTag("en")));
			myTokens = enStopList.filter(myTokens);
			myTokens = myStemmer.stem(myTokens);
			//myVocabulary.getVocabulary(myTokens);
			docList.put(document.id, new WordBag(myTokens));
		}
		
		//requete
		String myQuerry;
		myQuerry= "underpressure";
		WordBag querryWordBag = new WordBag();
		querryWordBag.addToken("underpressur");
		
		//underpressure 16
		//4354
		//5515
		
		double test1 = Similarity.computeSimilarity(querryWordBag, docList.get(16), Similarity.DICE);
		
		
//		for(Entry<String, Integer> e2 : docList.get(4354).entrySet()) {
//			System.out.println(e2.getValue() +" : "+ e2.getKey());
//
//		}
		
		System.out.println(test1);
		

		
	}
}
