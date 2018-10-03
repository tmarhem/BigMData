package tp;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class WordBag {
	public static HashMap<String, Integer> wordBagHash;

	public WordBag() {
		wordBagHash = new HashMap<String, Integer>();
	}
	
	public WordBag(String[] terms) {
		wordBagHash = new HashMap<String, Integer>();

		for (String term : terms) {
			if (!wordBagHash.containsKey(term)) {
				wordBagHash.put(term, 1);
			} else {
				wordBagHash.replace(term, wordBagHash.get(term) + 1);
			}
		}
	}

	public void addToken(String term) {
		if (!wordBagHash.containsKey(term)) {
			wordBagHash.put(term, 1);
		} else {
			wordBagHash.replace(term, wordBagHash.get(term) + 1);
		}
	}

	public void addTokens(String[] terms) {
		for (String term : terms) {
			if (!wordBagHash.containsKey(term)) {
				wordBagHash.put(term, 1);
			} else {
				wordBagHash.replace(term, wordBagHash.get(term) + 1);
			}
		}
	}

	public int getSize() {
		return wordBagHash.size();
	}
	
	public Set<Entry<String,Integer>> entrySet(){
		return wordBagHash.entrySet();
	}
}
