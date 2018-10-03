package tp;

import java.util.HashMap;

public class WordBag {
	public static HashMap<String, Integer> wordBagHash;
	
	public WordBag() {
		wordBagHash = new HashMap<String, Integer>();
	}
	
	public void addWordBag( String term) {
		if(!wordBagHash.containsKey(term)) {
			wordBagHash.put(term, 1);
		} else {
			wordBagHash.replace(term, wordBagHash.get(term)+1);
		}
	}
	
	public int getSize() {
		return wordBagHash.size();
	}
}
