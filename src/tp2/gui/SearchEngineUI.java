package tp2.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import tp2.searchEngine.DocumentInfo;
import tp2.searchEngine.SearchEngine;

/**
 * A GUI for an image retrieval system.
 * @author Pierre Tirilly - pierre.tirilly@telecom-lille.fr
 *
 */
public class SearchEngineUI extends JFrame implements ActionListener, ComponentListener {

	
	/* Icon file of the main frame. */
	private static final String SOFTWARE_LOGO_PATH = "data/telecom-lille-transparent.png";
	
	/* Dimensions of the components of the interface. */
	private static final int DEFAULT_FRAME_WIDTH = 1200;
	private static final int DEFAULT_FRAME_HEIGHT = 800;
	private static final int DEFAULT_BROWSER_WIDTH = 500;
	private static final int DEFAULT_BROWSER_HEIGHT = 700;
	private static final int DEFAULT_QUERY_PANEL_WIDTH = 300;
	private static final int TOP_PANEL_HEIGHT = 50;
	private static final Dimension BUTTON_DIMENSION = new Dimension( 180, 30 );
	private static final int QUERY_FIELD_LENGTH = 30;
	
	/* Texts appearing in the components. */
	private static final String LOAD_BUTTON_TEXT = "Load database";
	private static final String QUERY_BUTTON_TEXT = "Search";
	private static final String QUERY_LABEL_TEXT = "Query: ";
	private static final String QUERY_FIELD_DEFAULT_TEXT = "Enter query here";
	private static final String FRAME_TITLE = "BigMMData text search engine v0.15";
	
	/**
	 * The search engine used by the interface.
	 */
	private SearchEngine searchEngine;

	/**
	 * The panel allowing the user to browse the whole database or the search results.
	 */
	private DocumentBrowser documentBrowser;
	
	/**
	 * The button to load the database file.
	 */
	private JButton loadDatabaseButton;
	
	/**
	 * The button to query the database using the selected query.
	 */
	private JButton queryButton;
	
	/**
	 * Text field to enter the query
	 */
	private JTextField queryField;
	
	/**
	 * Label of the query field
	 */
	private JLabel queryLabel;
	
	/**
	 * The file chooser used to load the database file.
	 */
	private JFileChooser fileChooser;
	
	/**
	 * Constructor taking a search engine as an input. 
	 * @param searchEngine The search engine to be used by the system (should not be null).
	 */
	public SearchEngineUI( SearchEngine searchEngine ) {
		super( FRAME_TITLE );
		
		this.setIconImage( new ImageIcon( SearchEngineUI.class.getClassLoader().getResource( SOFTWARE_LOGO_PATH ) ).getImage() );
		
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.addComponentListener( this );
		
		this.fileChooser = new JFileChooser();
		this.searchEngine = searchEngine;
		
		this.createComponents();
		this.setPanels();
		
		this.setVisible( true );
		this.pack();
	}

	// creates the buttons of the interface
	private void createComponents() {
		
		// load database button
		this.loadDatabaseButton = new JButton( LOAD_BUTTON_TEXT );
		this.loadDatabaseButton.setPreferredSize( BUTTON_DIMENSION );
		this.loadDatabaseButton.addActionListener( this );
		this.loadDatabaseButton.setEnabled( this.searchEngine != null );
		
		// query button
		this.queryButton = new JButton( QUERY_BUTTON_TEXT );
		this.queryButton.setPreferredSize( BUTTON_DIMENSION );
		this.queryButton.addActionListener( this );
		this.queryButton.setEnabled( false );
		
		// query field
		this.queryField = new JTextField( QUERY_FIELD_DEFAULT_TEXT, QUERY_FIELD_LENGTH );
		this.queryField.setPreferredSize( new Dimension( this.queryField.getWidth(), BUTTON_DIMENSION.height ) );
		this.queryField.setMaximumSize( new Dimension( this.queryField.getWidth(), BUTTON_DIMENSION.height ) );
		this.queryField.setEditable( true );
		this.queryField.setEnabled( false );
	
		// query label
		this.queryLabel = new JLabel( QUERY_LABEL_TEXT );
		this.queryLabel.setEnabled( true );
	}
	
	// lays out the different components of the interface
	private void setPanels() {
		JPanel topPanel;
		
		// set general container of the frame and its properties
		this.setContentPane( new JPanel() );
		this.getContentPane().setLayout( new BorderLayout() );
		this.getContentPane().setPreferredSize( new Dimension( DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT ) );
		
		// set top panel with buttons
		topPanel = new JPanel();
		topPanel.setLayout( new BoxLayout( topPanel, BoxLayout.LINE_AXIS ) );
		topPanel.setPreferredSize( new Dimension( DEFAULT_FRAME_WIDTH, TOP_PANEL_HEIGHT ) );
		topPanel.add( Box.createHorizontalStrut( 50 ) );
		topPanel.add( this.loadDatabaseButton );
		topPanel.add( Box.createHorizontalGlue() );
		topPanel.add( this.queryLabel );
		topPanel.add( this.queryField);
		topPanel.add( Box.createHorizontalStrut( 10 ) );
		topPanel.add( this.queryButton );
		topPanel.add( Box.createHorizontalStrut( 50 ) );
		this.getContentPane().add( topPanel, BorderLayout.NORTH );
		
		// set browser panel
		this.documentBrowser = new DocumentBrowser( new Vector<DocumentInfo>(), DEFAULT_BROWSER_WIDTH, DEFAULT_BROWSER_HEIGHT );
		this.getContentPane().add( this.documentBrowser, BorderLayout.CENTER );
	}
	
	// loads a collection file in a separate thread and displays a window
	// notifying the user to wait.
	private void loadCollectionFile( final File collectionFile ) {
		
		class OpenFileWorker extends SwingWorker<Void, Void> {

			private File collectionFile;
			private JDialog window;
			
			public OpenFileWorker( JFrame parent, File collectionFile ) {
				super();
				this.collectionFile = collectionFile;
				this.window = new JDialog( parent );
				window.setUndecorated(true);
				window.getContentPane().add( new JLabel( "Opening text database..." ) );
				window.setLocationRelativeTo( parent );
				window.setModal(false);
				window.pack();
				window.setVisible(true);
				
			}
			
			protected Void doInBackground() {
				SearchEngineUI.this.setEnabled( false );
				searchEngine.loadDatabaseFile( this.collectionFile.getAbsolutePath() );
				return null;
			}
			
			protected void done() {
				documentBrowser.setDisplayedDocuments( searchEngine.getDatabase() );
				queryField.setEnabled( searchEngine.getDatabase() != null );
				queryButton.setEnabled( searchEngine.getDatabase() != null );
				repaint();
				window.setVisible(false);
				window.dispose();
				SearchEngineUI.this.setEnabled( true );
			}
		}
		
		final OpenFileWorker worker = new OpenFileWorker(this, collectionFile);
		worker.execute();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// load a new database
		if ( e.getSource() == this.loadDatabaseButton ) {
			if ( this.fileChooser.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION ) {
				this.loadCollectionFile( this.fileChooser.getSelectedFile() );
			}
		}
		
		// queries the database with the current query selected by the user
		if ( e.getSource() == this.queryButton && this.searchEngine != null && this.searchEngine.getDatabase() != null ) {
			Vector<DocumentInfo> searchResults = this.searchEngine.queryDatabase( this.queryField.getText() );
			this.documentBrowser.setDisplayedDocuments( searchResults );
		}

	}

	@Override
	public void componentResized(ComponentEvent e) {
		if ( e.getSource() == this && e.getID() == ComponentEvent.COMPONENT_RESIZED ) {
			this.documentBrowser.updatesize( this.getContentPane().getWidth() - DEFAULT_QUERY_PANEL_WIDTH,
					this.getContentPane().getHeight() - TOP_PANEL_HEIGHT );
			this.repaint();
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}
	
}
