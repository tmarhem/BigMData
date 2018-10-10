package tp2.searchEngine;

import java.util.Map.Entry;

import tp.WordBag;

public class Similarity {

	public final static Integer DICE = 0;
	public final static Integer VECTOR = 1;
	public final static Integer VECTORIDF = 3;
	public final static Integer VECTORIDF_NONORM = 4;

	public static double computeSimilarity(WordBag querry, WordBag applicant, Integer similarityType) {
		

		switch (similarityType) {

		// DICE
		case 0:

			double commonWordsCounter = 0;

			for (Entry<String, Integer> e : querry.entrySet()) {

				for (Entry<String, Integer> e2 : applicant.entrySet()) {

					System.out.println(e.getKey().equals(e2.getKey()));
					if (e.getKey().equals(e2.getKey())) {
						commonWordsCounter++;
					}
				}
			}

			double result =((2 * commonWordsCounter) / ((double)querry.getSize() + (double)applicant.getSize()));

			return result;

		default:
			return 0.0;

		}

	}
}
