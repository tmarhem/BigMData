package tp;

import tp.DocumentInfo;

/**
 * Abstract class providing basing identification elements of a document
 * (content and ID). Users can extend
 * it to provide their own description and similarity metrics.
 * @author Pierre Tirilly
 */
public class DocumentInfo {

	/**
	 * The content of the document.
	 */
    protected String content;
    
    /**
     * The ID number of the document.
     */
    protected int id;
    
    /**
     * Returns the content of the document.
     * @return The content of the document.
     */
	public String getContent() {
		return this.content;
	}

	/**
	 * Sets the content of the document.
	 * @param content The new content of the document.
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Returns the ID of the document.
	 * @return The ID of the document.
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Sets the ID of the document
	 * @param id The ID of the document
	 */
	public void setId(int id) {
		this.id = id;
	}
}

