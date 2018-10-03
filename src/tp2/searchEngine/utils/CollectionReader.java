package tp2.searchEngine.utils;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import tp2.searchEngine.DocumentInfo;

/**
 * This class provides a Handler to parse text database XML files using a
 * SAX parser. Files must follow this format :
 * <?xml version="1.0" encoding="UTF-8"?>
 * <TEXTBASE>
 *  <DOC>
 *   <DOCNO>000001</DOCNO>
 *   <TEXT>
 *   Preliminary Report-International Algebraic Language
 *   </TEXT>
 *  </DOC>
 *  <DOC>
 *   <DOCNO>000002</DOCNO>
 *   <TEXT>
 *   Extraction of Roots by Repeated Subtractions for Digital Computers
 *   </TEXT>
 *  </DOC>
 *  ...
 * </TEXTBASE>
 * where DOCNO is the identifier of a document and the TEXT tags indicate
 * the content of the documents.
 * 
 * @author Pierre Tirilly - pierre.tirilly@telecom-lille.fr
 *
 */
public class CollectionReader implements ContentHandler {

	/**
	 * Vector of DocumentInfo objects describing the content of the documents
	 */
	private Vector<DocumentInfo> database;
	
	/**
	 * Object storing the document being currently parsed
	 */
	private DocumentInfo currentDocumentInfo;
	
	/**
	 * Text being currently parsed
	 */
	private String currentData;
	
	/**
	 * Constructor for a CollectionReader object
	 */
	public CollectionReader() {
		this.database = null;
		this.currentData = null;
		this.currentDocumentInfo = null;
	}
	
	/**
	 * Returns the database read during parsing as a vector of DocumentInfo objects.
	 * @return The database read during the parsing.
	 */
	public Vector<DocumentInfo> getDatabase() {
		return this.database;
	}

	@Override
	public void startDocument() throws SAXException {
		this.database = new Vector<DocumentInfo>();
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		
		if ( qName.equals( "DOC" ) ) {
			this.currentDocumentInfo = new DocumentInfo();
		} else if ( qName.equals( "DOCNO" ) || qName.equals( "TEXT" ) ) {
			if ( this.currentDocumentInfo == null ) {
				throw new SAXException( "Error in XML file format: <" + qName + "> markup found outside of <DOC> markup.");
			}
			this.currentData = null;
		}
		return;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		if ( qName.equals( "DOC" ) ) {
		
			this.database.add( this.currentDocumentInfo );
			this.currentDocumentInfo = null;
			
		} else if ( qName.equals( "DOCNO" ) ) {
			
			if ( this.currentDocumentInfo != null ) {
				this.currentDocumentInfo.setId( Integer.parseInt( this.currentData ) );
				this.currentData = null;
			} else {
				throw new SAXException( "Error in XML file format: closing </" + qName + "> markup without opening <" + qName + "> markup.");
			}
			
		} else if ( qName.equals( "TEXT") ) {

			if ( this.currentDocumentInfo != null ) {
				this.currentDocumentInfo.setContent( this.currentData );
				this.currentData = null;
			} else {
				throw new SAXException( "Error in XML file format: closing </" + qName + "> markup without opening <" + qName + "> markup.");
			}
		} 
		
		return;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if ( this.currentData == null ) {
			this.currentData = new String( ch ).substring( start, start + length ).trim();
		} else {
			this.currentData += " " + new String( ch ).substring( start, start + length );
			this.currentData = this.currentData.trim();
		}
		return;
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
	}
	
	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
	}
	
	@Override
	public void setDocumentLocator(Locator locator) {
	}
	
	/**
	 * Reads a collection file at the TREC format.
	 * @param databaseFilePath The path to the collection file.
	 * @return The collection as a vector of DocumentInfo instances.
	 */
	public static Vector<DocumentInfo> readDatabaseFile( String databaseFilePath ) {
		XMLReader parser = null;
		Vector<DocumentInfo> database = null;
		File databaseFile = new File( databaseFilePath );
		try {
			parser = XMLReaderFactory.createXMLReader();
			parser.setContentHandler( new CollectionReader() );
			parser.parse( databaseFile.toURI().toString() );
		} catch ( SAXException saxe ) {
			System.err.println( "Error: cannot parse database file " + databaseFile.getAbsolutePath() + "." );
			saxe.printStackTrace();
		} catch ( IOException ioe ) {
			System.err.println( "Error: cannot open database file " + databaseFile.getAbsolutePath() + "." );
			ioe.printStackTrace();
		}

		if( parser != null ) {
			database = ((CollectionReader)parser.getContentHandler()).getDatabase();
		}
		
		return database;
	}
		

}
