package tp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

public class Vocabulary {

	public ArrayList<String> vocabulary;
	
	public Vocabulary() {
		vocabulary = new ArrayList<String>();
	}
	
	/*
	 * Retourne un HasMap de couple termes,occurences a partir d'une liste de termes
	 */
	public void getVocabulary(String[] tokens) {
		for (String token : tokens) {
			if(!vocabulary.contains(token)) {
				vocabulary.add(token);
			}
		}
	}
	
	/*
	 * Retourne le nombre de termes distincts
	 */
	public int getSize() {
		return vocabulary.size();
	}
	
	
}
