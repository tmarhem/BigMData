package tp2.searchEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import tp.Vocabulary;
import tp.WordBag;
import tp2.searchEngine.utils.Stemmer;
import tp2.searchEngine.utils.StopList;
import tp2.searchEngine.utils.Tokenizer;
/*
 * Interpolation 11 points
 * TODO MODELE ENSEMBLISTE
 * 				VECTORIEL
 * 				TF-IDF
 * 				NO NORM
 * ZIPF de base
 * apres stop list
 * apres stem
 */
public class SearchEngineImpl extends SearchEngine {

	private static final String ENSTOP = "data/stopListEnglish.txt";
	
	String regex;
	String[] myTokens, myQuery;
	Tokenizer myTokenizer;
	StopList enStopList;
	Stemmer myStemmer;
	Vocabulary myVocabulary;
	
	HashMap<Integer,WordBag> index;
	HashMap<String, ArrayList<Integer>> invertedIndex;
	
	HashSet<Integer> applicants;
	
	WordBag querryWordBag;

	public SearchEngineImpl() {
		this.database = new Vector<DocumentInfo>();
		
		regex = " ,.;:()'\"<>";
		myTokens = null;
		myQuery = null;
		myTokenizer = new Tokenizer(regex);
		enStopList = new StopList(ENSTOP);
		myStemmer = new Stemmer(Stemmer.StemmerLanguage.ENGLISH);
		myVocabulary = new Vocabulary();
		
		index = new HashMap<Integer, WordBag>();
		invertedIndex = new HashMap<String,ArrayList<Integer>>();
		
		querryWordBag = null;
	}
	
	@Override
	public void indexDatabase() {
		
		System.out.println("Index generating...");

		
		for(DocumentInfo document : database) {
			
			//TODO SEPARATE INTO CLASS/FUNCTION TREATMENT
			myTokens = myTokenizer.tokenize(document.getContent().toLowerCase(Locale.forLanguageTag("en")));
			myTokens = enStopList.filter(myTokens);
			myTokens = myStemmer.stem(myTokens);
			/////////////////////////////////////////////////
			
			myVocabulary.getVocabulary(myTokens);
			
			index.put(document.id, new WordBag(myTokens));

			
			//Inverted Index
			for(Entry<String,Integer> e : index.get(document.id).entrySet()) {
				if(!invertedIndex.containsKey(e.getKey())) {
					invertedIndex.put(e.getKey(), new ArrayList<Integer>());
				} else {
						invertedIndex.get(e.getKey()).add(document.id);
				}
			}
		}
		
		System.out.println("Index generated");

		
		

	}

	@Override
	public Vector<DocumentInfo> queryDatabase(String query) {
		
		//TODO SEPARATE INTO CLASS/FUNCTION TREATMENT
		myQuery = myTokenizer.tokenize(query.toLowerCase(Locale.forLanguageTag("en")));
		myQuery = enStopList.filter(myQuery);
		myQuery = myStemmer.stem(myQuery);
		////////////////////////////////////////////
		
		querryWordBag = new WordBag(myQuery);
		
		Vector<DocumentInfo> results = new Vector<DocumentInfo>();
		
		applicants = new HashSet<Integer>();
		
		for(Entry<String,Integer> e : querryWordBag.entrySet()) {
			applicants.addAll(invertedIndex.get(e.getKey()));
		}
		System.out.println("taille des applicants a la requete:"+applicants.size());
		
		//WORKING UNTIL HERE
		//traitement des documents possiblement pertinents
		//TODO DEBUG SImILARITY
		results = querrySimilarity(Similarity.DICE);
		
		return results;
	}
	
	public Vector<DocumentInfo> querrySimilarity(Integer similarityType){
		Vector<DocumentInfo> results = new Vector<DocumentInfo>();
		TreeMap<Double,Integer> preResults = new TreeMap<Double,Integer>();
		
		double temp;
		
		for(Integer i : applicants){
			temp = Similarity.computeSimilarity(querryWordBag, index.get(i), similarityType);
			preResults.put(temp, i);
		}
		//ICI, tous les resulats pour la requete sont ordonnés par pertinence dans le treeMAp
		
		for(Entry<Double,Integer> e : preResults.entrySet()) {
			results.add(database.get(e.getValue()));
			System.out.println(e.getValue()+" has pertinence score of "+e.getKey());
		}
		
		return results;
	}

}
