package tp;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

public class Vocabulary {

	public HashMap<String,Integer> vocabulary;
	
	public Vocabulary() {
		vocabulary = new HashMap<String, Integer>();
	}
	
	/*
	 * Retourne un HasMap de couple termes,occurences a partir d'une liste de termes
	 */
	public void getVocabulary(String[] tokens) {
		for (String token : tokens) {
			token.toLowerCase();
			if(!vocabulary.containsKey(token)) {
				vocabulary.put(token,1);
			}
			else {
				vocabulary.replace(token, vocabulary.get(token)+1);
			}
		}
	}
	
	/*
	 * Retourne le nombre de termes distincts
	 */
	public int getSize() {
		return vocabulary.size();
	}
	
	/*
	 * Retourne les occurences d'une entrée dans un hasmap
	 */
	public int getFreq(String entry) {
		if(vocabulary.containsKey(entry)) return vocabulary.get(entry);
		else return 1;
	}
	
	public int getHapaxFreq() {
		int result = 0;
		for( Entry<String,Integer> e : vocabulary.entrySet()) {
			if (e.getValue()==1) result++;
		}
		return result;
	}
	
	public Set<Entry<String, Integer>> entrySet(){
		return vocabulary.entrySet();
	}
	
	public String getHighest(){
		Integer highest = 0;
		String highestKey= "init";
		
		for(Entry<String,Integer> e : vocabulary.entrySet()) {
			if (Integer.compare(e.getValue(), highest)==1) {
				highest = e.getValue();
				highestKey = e.getKey();
			}
		}
		
		return highestKey;
	}
}
