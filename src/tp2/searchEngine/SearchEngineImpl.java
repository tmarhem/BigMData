package tp2.searchEngine;

import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import tp.Vocabulary;
import tp.WordBag;
import tp2.searchEngine.utils.Stemmer;
import tp2.searchEngine.utils.StopList;
import tp2.searchEngine.utils.Tokenizer;

public class SearchEngineImpl extends SearchEngine {

	private static final String ENSTOP = null;
	
	String regex;
	String[] myTokens;
	Tokenizer myTokenizer;
	StopList enStopList;
	Stemmer myStemmer;
	Vocabulary myVocabulary;
	
	HashMap<Integer,WordBag> index;

	public SearchEngineImpl() {
		this.database = new Vector<DocumentInfo>();
		
		regex = " ,.;:()'\"<>";
		myTokens = null;
		myTokenizer = new Tokenizer(regex);
		enStopList = new StopList(ENSTOP);
		myStemmer = new Stemmer(Stemmer.StemmerLanguage.ENGLISH);
		myVocabulary = new Vocabulary();
		
		index = new HashMap<Integer, WordBag>();
		}
	
	@Override
	public void indexDatabase() {
		
		for(DocumentInfo document : database) {
			myTokens = myTokenizer.tokenize(document.getContent().toLowerCase(Locale.forLanguageTag("en")));
			myTokens = enStopList.filter(myTokens);
			myTokens = myStemmer.stem(myTokens);
			myVocabulary.getVocabulary(myTokens);
//			//myWordBag.addWordBag(document.id, myVocabulary);
			
			/*
			 * Pour chaque document 
			 * J'enrichi le ovcabulaire
			 * je crée un wordbag Hasmap<documentId, Hahmap<terme, freq>
			 * 
			 */
		}

	}

	@Override
	public Vector<DocumentInfo> queryDatabase(String query) {
		// TODO Auto-generated method stub
		return null;
	}

}
