package tp2.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import tp2.searchEngine.DocumentInfo;

/**
 * A panel to browse text documents. It contains two parts :
 *  - on the right, a list of available documents and their first four lines are displayed
 *  - in the left, the complete document that was clicked by the user is displayed
 *  
 * @author Pierre Tirilly - pierre.tirilly@telecom-lille.fr
 *
 */
public class DocumentBrowser extends JPanel implements ActionListener, MouseListener {

	/**
	 * Inner class providing a text area to display a part of a document in the list
	 * of available documents
	 * 
	 * @author Pierre Tirilly - pierre.tirilly@telecom-lille.fr
	 *
	 */
	private class DocumentEntry extends JTextArea {
		
		/**
		 * The information of the displayed document
		 */
		private DocumentInfo docInfo;
		
		/**
		 * Constructor of a new list entry
		 * @param docInfo Document to be displayed in the entry
		 */
		public DocumentEntry( DocumentInfo docInfo ) {
			super( docInfo.getContent() );
			this.docInfo = docInfo;
		}
		
		/**
		 * Getter of the DocumentInfo object of the displayed document
		 * @return The DocumentInfo object of the displayed document
		 */
		public DocumentInfo getDocInfo() {
			return this.docInfo;
		}	
	}
	
	// GUI-related constants
	private static final Color DOCUMENT_TEXT_COLOR = Color.BLACK;
	private static final Color LIST_BACKGROUND_COLOR = Color.WHITE;
	private static final String HEADER_DEFAULT_TEXT = "Number of documents: ";
	private static final int DOCUMENT_ENTRY_HEIGHT = 80;
	private static final int HEADER_STRUT_HEIGHT = 10;
	private static final int INTER_DOCUMENT_STRUT_HEIGHT = 5;
	private static final int ENTRY_BORDER_THICKNESS = 1;
	private static final int BROWSER_BORDER_THICKNESS = 5;
	private static final Color ENTRY_BORDER_COLOR = Color.BLACK; 

	/**
	 * Vector of the documents being displayed
	 */
	private Vector<DocumentInfo> displayedDocuments;
	
	/**
	 * Panel containing the list of available documents
	 */
	private JPanel listPanel;
	
	/**
	 * Scroll panel containing the list panel
	 */
	private JScrollPane listScrollPanel;
	
	/**
	 * Panel displayed the selected document
	 */
	private JTextArea documentPanel;

	/**
	 * Scroll panel containing the selected document panel
	 */
	private JScrollPane documentScrollPanel;
	
	/**
	 * Label displaying the number of available documents
	 */
	private JLabel headerLabel;
	
	
	/**
	 * Constructor of a browsing panel for text documents
	 * @param documents The documents to be displayed in the browsing panel
	 * @param width The width of the panel
	 * @param height The height of the panel
	 */
	public DocumentBrowser( Vector<DocumentInfo> documents, int width, int height ) {	
		super( new BorderLayout() );
		this.setPreferredSize( new Dimension( width, height ) );
		this.setBorder( BorderFactory.createEmptyBorder( 0, BROWSER_BORDER_THICKNESS, BROWSER_BORDER_THICKNESS, BROWSER_BORDER_THICKNESS ) );
		this.setPanels( width, height);
		this.setDisplayedDocuments( documents );
	}
 
	/**
	 * Set the displayed document vector and update the GUI
	 * @param displayedDocuments The new vector of documents to be displayed
	 */
	public void setDisplayedDocuments( Vector<DocumentInfo> displayedDocuments ) {
		this.documentPanel.setText( new String() );
		this.displayedDocuments = displayedDocuments;
		this.listPanel.removeAll();
		if ( this.displayedDocuments != null ) {
			for ( DocumentInfo di : this.displayedDocuments ) {
				this.addDocumentEntry( di );
			}
			this.headerLabel.setText( HEADER_DEFAULT_TEXT + this.displayedDocuments.size() + "     " );
			this.listScrollPanel.getViewport().setViewPosition( new Point( 0, 0 ) );
		}
		this.revalidate();
		this.repaint();		
		return;
	}
	
	// Sets the panels of the GUI
	private void setPanels( int width, int height ) {
		JSplitPane splitPanel;
		JPanel headerPanel;
		
		// setting the document list panel into a scroll panel
		this.listPanel = new JPanel();
		this.listPanel.setLayout( new BoxLayout( this.listPanel, BoxLayout.PAGE_AXIS ) );
		this.listPanel.setBackground( LIST_BACKGROUND_COLOR );
		this.listScrollPanel = new JScrollPane( this.listPanel );
		this.listScrollPanel.setPreferredSize( new Dimension( width / 2 , height) );
		
		// setting the document panel into a scroll panel
		this.documentPanel = new JTextArea();
		this.documentScrollPanel = new JScrollPane( this.documentPanel );
		this.documentScrollPanel.setPreferredSize( new Dimension( width / 2 , height) );
		
		// lay out list and document scroll panels into a split panel
		splitPanel = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		splitPanel.setLeftComponent( this.documentScrollPanel );
		splitPanel.setRightComponent( this.listScrollPanel );
		
		// label at top of panel indicating the number of displayed documents 
		this.headerLabel = new JLabel();
		this.headerLabel.setAlignmentX( RIGHT_ALIGNMENT );
		headerPanel = new JPanel();
		headerPanel.setLayout( new BoxLayout( headerPanel, BoxLayout.PAGE_AXIS ) );
		headerPanel.add( Box.createVerticalStrut( HEADER_STRUT_HEIGHT ) );
		headerPanel.add( this.headerLabel );
		headerPanel.add( Box.createVerticalStrut( HEADER_STRUT_HEIGHT ) );
		this.add( headerPanel, BorderLayout.NORTH );
		
		// add split panel to the main container
		this.add( splitPanel, BorderLayout.CENTER );
		
		return;
	}
	
	// adds a document entry to the document list panel
	private void addDocumentEntry( DocumentInfo docInfo ) {
		DocumentEntry entry;

		entry = new DocumentEntry( docInfo );
		entry.setPreferredSize( new Dimension( this.getWidth() / 2, DOCUMENT_ENTRY_HEIGHT ) );
		entry.setBorder( BorderFactory.createTitledBorder( 
				BorderFactory.createLineBorder( ENTRY_BORDER_COLOR, ENTRY_BORDER_THICKNESS ),
				"Document " + docInfo.getId() ));
		entry.setLineWrap( false );
		entry.setEditable( false );
		entry.setEnabled( false );
		entry.setDisabledTextColor( DOCUMENT_TEXT_COLOR );
		entry.addMouseListener( this );

		this.listPanel.add( entry );
		this.listPanel.add( Box.createVerticalStrut( INTER_DOCUMENT_STRUT_HEIGHT ) );
	}
	
	/**
	 * Updates the size of the panel
	 * @param width The width of the panel
	 * @param height The new height of the panel
	 */
	public void updatesize( int width, int height ) {
		this.setPreferredSize( new Dimension( width, height ) );
		this.repaint();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		DocumentEntry clickedEntry;
		if ( e.getSource() instanceof DocumentEntry ) {
			clickedEntry = (DocumentEntry)e.getSource();
			this.documentPanel.setText( clickedEntry.getDocInfo().getContent() );
			this.documentScrollPanel.getViewport().setViewPosition( new Point( 0, 0 ) );
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void actionPerformed(ActionEvent e) {}

}
