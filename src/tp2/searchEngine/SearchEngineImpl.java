package tp2.searchEngine;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
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

	HashMap<Integer, WordBag> index;
	HashMap<String, ArrayList<Integer>> invertedIndex;

	HashSet<Integer> applicants;

	WordBag queryWordBag;
	Similarity mySimilarity;

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
		invertedIndex = new HashMap<String, ArrayList<Integer>>();

		queryWordBag = null;
	}

	@Override
	public void indexDatabase() {

		System.out.println("Index generating... 0%");
		double corpusPosition = 1;
		double corpusSize = (double) database.size();
		double percentageDone;
		double lastDisplay = 0.0;
		NumberFormat myFormat = new DecimalFormat("#0");

		for (DocumentInfo document : database) {

			myTokens = myTokenizer.tokenize(document.getContent().toLowerCase(Locale.forLanguageTag("en")));
			myTokens = enStopList.filter(myTokens);
			myTokens = myStemmer.stem(myTokens);

			myVocabulary.getVocabulary(myTokens);

			index.put(document.id-1, new WordBag(myTokens));

			// Inverted Index
			for (Entry<String, Integer> e : index.get(document.id).entrySet()) {
				if (!invertedIndex.containsKey(e.getKey())) {
					invertedIndex.put(e.getKey(), new ArrayList<Integer>());
				} else {
					invertedIndex.get(e.getKey()).add(document.id);
				}
			}
			percentageDone = (corpusPosition / corpusSize) * 100;
			corpusPosition += 1.0;
			if (percentageDone - lastDisplay >= 5.0) {
				System.out.println("Index generating... " + myFormat.format(percentageDone) + "%");
				lastDisplay = percentageDone;
			}
		}

		System.out.println("Index generated");
		mySimilarity = new Similarity(database,invertedIndex);

	}

	@Override
	public Vector<DocumentInfo> queryDatabase(String query) {

		Vector<DocumentInfo> results = new Vector<DocumentInfo>();

		if(query.equals("")) {
			System.out.println("Empty query");
			return results;
		}
		myQuery = myTokenizer.tokenize(query.toLowerCase(Locale.forLanguageTag("en")));
		myQuery = enStopList.filter(myQuery);
		myQuery = myStemmer.stem(myQuery);
		queryWordBag = new WordBag(myQuery);


		applicants = new HashSet<Integer>();

		if (queryWordBag.isEmpty()) {
			return results;
		} else {

			for (Entry<String, Integer> e : queryWordBag.entrySet()) {
				if (invertedIndex.get(e.getKey()) != null) {
					applicants.addAll(invertedIndex.get(e.getKey()));
				}
			}

			System.out.println("taille des applicants a la requete:" + applicants.size());

			results = querrySimilarity(Similarity.VECTORIDF);

			return results;
		}
	}

	/*
	 * DESCRIPTION Process the text through the tokenizer, stoplist, stemmer and put
	 * in wordbag INPUT String[] : querry entedred by the user OUTPUT void
	 */
	private void processString(String query) {
	}

	/*
	 * DESCRIPTION Test the querry through the corpus and give backs pertinence
	 * ordonated results INPUT SimilarityType DICE or other, see Similarity.java
	 * OUTPUT ordonated DocumentInfo Vector by pertinence
	 */
	public Vector<DocumentInfo> querrySimilarity(Integer similarityType) {

		Vector<DocumentInfo> results = new Vector<DocumentInfo>();
		TreeMap<Double, Integer> preResults = new TreeMap<Double, Integer>();

		double temp;

		if (applicants.isEmpty() || queryWordBag.isEmpty()) {
			return results;
		} else {

			for (Integer i : applicants) {
				temp = mySimilarity.computeSimilarity(queryWordBag, index.get(i), similarityType);
				preResults.put(temp, i);
			}
			// ICI, tous les resulats pour la requete sont ordonnés par pertinence dans le
			// treeMAp

			for (Entry<Double, Integer> e : preResults.entrySet()) {
				results.add(database.get(e.getValue()));
				System.out.println(e.getValue() + " has pertinence score of " + e.getKey());
			}

			// Reversing the collection in adequation with the UI display
			Collections.reverse(results);
			return results;
		}
	}

}
