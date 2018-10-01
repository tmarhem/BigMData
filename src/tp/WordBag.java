package tp;

import java.util.HashMap;

public class WordBag {
	public static HashMap<Integer, Vocabulary> wordBagHash;
	
	public WordBag() {
		wordBagHash = new HashMap<Integer, Vocabulary>();
	}
	
	public void addWordBag( Integer id, Vocabulary myVocabulary) {
		if(!wordBagHash.containsKey(id)) {
			wordBagHash.put(id, myVocabulary);
		} else {
			wordBagHash.replace(id, myVocabulary);
		}
	}
	
	public int getSize() {
		return wordBagHash.size();
	}
}
