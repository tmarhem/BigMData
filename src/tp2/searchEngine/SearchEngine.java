package tp2.searchEngine;

import java.util.Vector;

import tp2.searchEngine.utils.CollectionReader;

/**
 * Abstract class for a textual search engine.
 * @author Pierre Tirilly - pierre.tirilly@telecom-lille.fr
 *
 */
public abstract class SearchEngine {

	/**
	 * Vector containing the documents (DocumentInfo instances) of the database to be indexed and searched.
	 */
	protected Vector<DocumentInfo> database;
	
	/**
	 * Returns the content of the database indexed by the search engine.
	 * @return A vector of DocumentInfo-derived objects representing the documents indexed by the search Engine.
	 */
	public Vector<DocumentInfo> getDatabase() {
		return this.database;
	}
	
	/**
	 * Sets the current database.
	 * @param database The database to set set as current.
	 */
	public void setDatabase( Vector<DocumentInfo> database ) {
		this.database = database;
	}
	
	/**
	 * Loads the database as described in a TREC file.
	 * @param databaseFilePath The path to the TREC file containing the database.
	 */
	public void loadDatabaseFile( String databaseFilePath ) {
		this.setDatabase( CollectionReader.readDatabaseFile(databaseFilePath) );
		this.indexDatabase();
	}
	
	/**
	 * Creates the descriptors and index for the current database.
	 */
	public abstract void indexDatabase();
	
	/**
	 * Searches the database for documents that are similar to the query provided.
	 * The search results are sorted in decreasing order of similarity to the query.
	 * @param query The query data as a string.
	 * @return A vector of documents (described as DocumentInfo objects) in decreasing order of similarity.
	 */
	public abstract Vector<DocumentInfo> queryDatabase( String query );
	
}
