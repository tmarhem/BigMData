package tp2.searchEngine;

import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import tp.WordBag;

public class Similarity {

	public final static Integer DICE = 0;
	public final static Integer VECTOR = 1;
	public final static Integer VECTORIDF = 2;
	public final static Integer VECTORIDF_NONORM = 3;

	int dbSize;
	HashMap<String, ArrayList<Integer>> invertedIndex;

	public Similarity(Vector<DocumentInfo> database, HashMap<String, ArrayList<Integer>> invertedIndex) {
		dbSize = database.size();
		this.invertedIndex = invertedIndex;
	}

	public double computeIdf(String term) {

		return 0.0;
	}

	public double computeSimilarity(WordBag querry, WordBag applicant, Integer similarityType) {

		double result = 0;
		double numerator, denomQuery, denomApplicant;

		switch (similarityType) {

		// AGGREGATION MODEL -- DICE
		case 0:

			double commonWordsCounter = 0;

			for (Entry<String, Integer> e : querry.entrySet()) {

				for (Entry<String, Integer> e2 : applicant.entrySet()) {

					if (e.getKey().equals(e2.getKey())) {
						commonWordsCounter++;
					}
				}
			}

			result = ((2 * commonWordsCounter) / ((double) querry.getSize() + (double) applicant.getSize()));

			return result;

		// VECTOR MODEL
		case 1:
			numerator = 0;
			denomApplicant = 0;
			denomQuery = 0;
			result = 0;
			// numerator, if mot commun, += produit de leur freq respectives
			// denomQuery sqrt sommE des carrés des fréquences non nulles
			// denomApplicant sqrt sommE des carrés des fréquences non nulle

			for (Entry<String, Integer> e : querry.entrySet()) {

				for (Entry<String, Integer> e2 : applicant.entrySet()) {
					denomQuery += Math.pow((double) e.getValue(), 2.0);
					denomApplicant += Math.pow((double) e2.getValue(), 2.0);

					if (e.getKey().equals(e2.getKey())) {
						numerator += ((double) e.getValue() * (double) e2.getValue());
					}
				}
			}

			result = (numerator / ((Math.sqrt(denomQuery)) * (Math.sqrt(denomApplicant))));
			return result;

		// VECTOR IDF MODEL
		case 2:
			numerator = 0;
			denomApplicant = 0;
			denomQuery = 0;
			result = 0;
			double idf = 0;

			for (Entry<String, Integer> e : querry.entrySet()) {

				for (Entry<String, Integer> e2 : applicant.entrySet()) {
					denomQuery += Math.pow((double) e.getValue(), 2.0);
					denomApplicant += Math.pow((double) e2.getValue(), 2.0);

					if (e.getKey().equals(e2.getKey())) {
						if(invertedIndex.get(e.getKey()).size()!=0) {
							idf = Math.log(dbSize/(invertedIndex.get(e.getKey()).size()));
						} else {
							idf = 1;
						}
						numerator += (((double) e.getValue() * (double) e2.getValue())*idf);
					}
				}
			}

			result = (numerator / ((Math.sqrt(denomQuery)) * (Math.sqrt(denomApplicant))));
			return result;
			
			// VECTOR IDF MODEL NO NORM
					case 3:
						numerator = 0;
						denomApplicant = 0;
						denomQuery = 0;
						result = 0;
						double idfnonorm = 0;

						for (Entry<String, Integer> e : querry.entrySet()) {

							for (Entry<String, Integer> e2 : applicant.entrySet()) {
								denomQuery += Math.pow((double) e.getValue(), 2.0);
								denomApplicant += Math.pow((double) e2.getValue(), 2.0);

								if (e.getKey().equals(e2.getKey())) {
									if(invertedIndex.get(e.getKey()).size()!=0) {
										idf = Math.log(dbSize/(invertedIndex.get(e.getKey()).size()));
									} else {
										idf = 1;
									}
									numerator += (((double) e.getValue() * (double) e2.getValue())*idfnonorm);
								}
							}
						}

						return numerator;

		default:
			return 0.0;

		}

	}
}
