package tp2.evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tp2.searchEngine.DocumentInfo;
import tp2.searchEngine.SearchEngine;

/**
 * A simple search engine evaluator computing (recall, precision) points for a single query.
 * It is assumed that the search engine would return results and that there are expected results for all queries.
 * @author Pierre Tirilly
 */
public class SearchEngineEvaluator {

	/**
	 * Pattern to parse a line of TREC ground truth files.
	 */
	private static final String GT_REGEX_PATTERN = "(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)";
	
	/**
	 * Pattern to parse a query file line with the following format : query_id query_text
	 */
	private static final String QRY_REGEX_PATTERN = "^\\s*(\\d+)\\s+(.+)$";
	
	/**
	 * Inner class to store the computed (recall, precision) couples.
	 * @author Pierre Tirilly
	 */
	private class RecallPrecisionPoint {
		double recall;
		double precision;
		
		/**
		 * Constructor.
		 * @param recall The recall value of the point.
		 * @param precision The precision value of the point.
		 */
		private RecallPrecisionPoint( double recall, double precision ) {
			this.recall = recall;
			this.precision = precision;
		}
		
		@Override
		public String toString() {
			return this.recall + " " + this.precision;
		}
	}
	
	/**
	 * The search engine to be evaluated.
	 */
	private SearchEngine searchEngine;
	
	/**
	 * The ground truth of the queries as <query_id, doc_id list> couples.
	 */
	private HashMap< Integer, Vector< Integer > > groundtruth;
	
	/**
	 * The queries as <query_id, query_text> couples.
	 */
	private HashMap< Integer, String > queries;
	
	/**
	 * Constructor for a search engine evaluator.
	 * @param searchEngine The search engine to be evaluated.
	 */
	public SearchEngineEvaluator( SearchEngine searchEngine ) {
		this.searchEngine = searchEngine;
		this.queries = null;
		this.groundtruth = null;
	}
	
	/**
	 * Reads an input query file where each line has the following format :
	 *     query_id query_text.
	 * @param file The file to be read.
	 */
	public void readQueryFile( String file ) {
		BufferedReader br;
		String line;
		Pattern qryPattern = Pattern.compile( QRY_REGEX_PATTERN );
		Matcher qryMatcher = qryPattern.matcher( "" );
		int queryId;
		String queryContent;
		
		this.queries = new HashMap<Integer, String>();
		
		try {
			br = new BufferedReader( new FileReader( file ) );
			
			while ( br.ready() ) {
				line = br.readLine();
				line.trim();
				qryMatcher.reset( line );
				if ( qryMatcher.matches() ) {
					queryId = Integer.parseInt( qryMatcher.group( 1 ) );
					queryContent = qryMatcher.group( 2 );
					this.queries.put( queryId, queryContent );
				}
			}	
		} catch ( IOException ioe ) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Read TREC ground truth files.
	 * @param file The file to be read.
	 */
	public void readGroundTruthFile( String file ) {
		BufferedReader br;
		String line;
		Pattern gtPattern = Pattern.compile( GT_REGEX_PATTERN );
		Matcher gtMatcher = gtPattern.matcher( "" );
		int queryId;
		int docId;
		
		// initialize ground truth map
		this.groundtruth = new HashMap<Integer, Vector<Integer>>();
		
		try {
			br = new BufferedReader( new FileReader( file ) );
			
			while ( br.ready() ) {
				line = br.readLine();
				line.trim();
				gtMatcher.reset( line );
				if ( gtMatcher.matches() ) {
					queryId = Integer.parseInt( gtMatcher.group( 1 ) );
					docId = Integer.parseInt( gtMatcher.group( 2 ) );
					if ( !this.groundtruth.containsKey( queryId ) ) {
						this.groundtruth.put( queryId, new Vector<Integer>() );
					}
					this.groundtruth.get( queryId ).add( docId );
				}
			}	
		} catch ( IOException ioe ) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Computes precision-recall points for the given query.
	 * @param queryId The ID of the query to use.
	 * @return A vector of the computed (precision, recall) points, or null
	 * if no groundtruth exists for the query.
	 */
	public Vector<RecallPrecisionPoint> evaluateRecallPrecisionPoints( int queryId ) {
		return this.evaluate(queryId, false);		
	}

	/**
	 * Computes precision-recall for the given query using 11-pt interpolation.
	 * @param queryId The ID of the query to use.
	 * @return A vector of the 11 (precision, recall) points, or null
	 * if no groundtruth exists for the query.
	 */
	public Vector<RecallPrecisionPoint> evaluate11pt( int queryId ) {
		return this.evaluate(queryId, true);
	}

	
	/**
	 * Computes precision-recall points for the given query.
	 * @param queryId The ID of the query to use.
	 * @param use11ptInterpolation If true, 11-point interpolation is used.
	 * @return A vector of the computed (precision, recall) points, or null
	 * if no groundtruth exists for the query.
	 */
	private Vector<RecallPrecisionPoint> evaluate( int queryId, boolean use11ptInterpolation ) {
		int truePositives;
		String query;
		Vector<DocumentInfo> searchResults;
		Vector<RecallPrecisionPoint> recallPrecisionPoints = null;
		
		// get the query
		if ( this.queries.containsKey( queryId ) ) {
			query = this.queries.get( queryId );	
		} else {
			System.err.println( "Error: cannot find query " + queryId + "." );
			return null;
		}
		
		// get the search results for this query
		searchResults = this.searchEngine.queryDatabase( query );

		// evaluate
		if ( this.groundtruth.containsKey(queryId) && this.groundtruth.get(queryId).size() > 0 ) { // if groundtruth contains documents
		 	recallPrecisionPoints = new Vector<RecallPrecisionPoint>();
			if ( searchResults.size() == 0 ){ // nothing retrieved 
		 		recallPrecisionPoints.add(new RecallPrecisionPoint(0.0, 0.0));
		 	} else  {  
			
		 		// compute recall and precision points for each DCV
		 		truePositives = 0;
		 		for ( int i = 0 ; i < searchResults.size() ; i++ ) {
		 			if ( this.groundtruth.get( queryId )
		 					.contains( 
		 							searchResults.get( i ).getId() ) ) {
		 				truePositives++;
		 			}
		 			recallPrecisionPoints.add( new RecallPrecisionPoint( (double)truePositives / (double)this.groundtruth.get( queryId ).size(),
		 																(double)truePositives / (double)( i + 1) ) );
		 		}
		 		recallPrecisionPoints = this.interpolate( recallPrecisionPoints );
		 	}
			
			if( use11ptInterpolation ) {
				recallPrecisionPoints = this.to11pt( recallPrecisionPoints );
			}
		}

		return recallPrecisionPoints;
	}

	/**
	 * Computes average precision for the given query.
	 * @param queryId The ID of the query to use.
	 * @return The average precision for the given query. Returns -1.0
	 * if no groundtruth exists for the query.
	 */
	public double evaluateAveragePrecision( int queryId ) {
		int truePositives;
		String query;
		Vector<DocumentInfo> searchResults;
		double ap;
		
		// get the query
		if ( this.queries.containsKey( queryId ) ) {
			query = this.queries.get( queryId );	
		} else {
			System.err.println( "Error: cannot find query " + queryId + "." );
			return Double.NaN;
		}
		
		// get the search results for this query
		searchResults = this.searchEngine.queryDatabase( query );

		// evaluate
		ap = 0.0;
		if ( !this.groundtruth.containsKey(queryId) || this.groundtruth.get(queryId).size() == 0 ) { // no documents to retrieve
			ap = Double.NaN; // skip query
		} else if ( searchResults.size() != 0 ){
			// compute precision point for each relevant document returned
			truePositives = 0;
			for ( int i = 0 ; i < searchResults.size() ; i++ ) {
				if ( this.groundtruth.get( queryId )
						.contains( 
								searchResults.get( i ).getId() ) ) {
					truePositives++;
					ap += (double)truePositives / (double)(i + 1); 
				}
			}
			ap /= this.groundtruth.get(queryId).size();
		}
		
		return ap;
	}

	// interpolation of recall-precision curve
	private Vector<RecallPrecisionPoint> interpolate( Vector<RecallPrecisionPoint> points ) {
		TreeMap< Double, RecallPrecisionPoint > interpolatedPoints = new TreeMap<Double, SearchEngineEvaluator.RecallPrecisionPoint>();
		double maxPrecision;
		double minRecall = 1.01;
		
		for ( RecallPrecisionPoint rpp : points ) {
			if ( minRecall >= rpp.recall ) {
				minRecall = rpp.recall;
			}
			if ( !interpolatedPoints.containsKey( rpp.recall ) ) { // if recall point does not exist yet
				maxPrecision = 0.0;
				for ( RecallPrecisionPoint rpp2 : points ) {
					if ( rpp2.recall >= rpp.recall && rpp2.precision >= maxPrecision ) {
						maxPrecision = rpp2.precision;
					}
				}
				interpolatedPoints.put( rpp.recall, new RecallPrecisionPoint(rpp.recall, maxPrecision) );
			}
		}
		
		// precision at minimum recall for recall 0 point 
		interpolatedPoints.put( 0.0, new RecallPrecisionPoint( 0.0, interpolatedPoints.get(minRecall).precision) );
		
		return new Vector<RecallPrecisionPoint>( interpolatedPoints.values() );
	}

	// compute 11-pt recall precision points from sorted, interpolated regular recall precision points
	private Vector<RecallPrecisionPoint> to11pt( Vector<RecallPrecisionPoint> points ) {
		Vector<RecallPrecisionPoint> points11 = new Vector<RecallPrecisionPoint>();
		RecallPrecisionPoint newPoint;
		
		for ( double cut = 0.0 ; cut <= 1.0 ; cut += 0.1 ) {
			newPoint = null;
			for ( RecallPrecisionPoint p : points ) {
				if( p.recall >= cut ) {
					newPoint = new RecallPrecisionPoint(cut, p.precision);
					break;
				}
			}
			if ( newPoint == null ) {
				newPoint = new RecallPrecisionPoint(cut, 0.0);
			}
			points11.add(newPoint);
		}
		
		return points11;
	}
	
	/**
	 * Returns the numbers of queries in the evaluator.
	 * @return The number of queries in the evaluator
	 */
	public int nbQueries() {
		return this.queries.size();
	}
	
	/**
	 * Outputs the specified (recall, precision) points to a file.
	 * @param file The path of the file to write.
	 * @param rpps A vector of (recall, precision) points to output.
	 */
	public void outputRPPoints(String file, Vector<RecallPrecisionPoint> rpps) {
		if ( rpps == null || file == null ) {
			return;
		}
		
		BufferedWriter writer;
		
		try {
			writer = new BufferedWriter(new FileWriter(file));
			for( RecallPrecisionPoint rpp : rpps ) {
				writer.write(rpp.toString());
				writer.newLine();
			}
			writer.close();
		} catch( IOException ioe ) {
			ioe.printStackTrace();
		}
	}
	
	// Main method
	// Use: java SearchEngineEvaluator database_file query_file query_id groundtruth_file
	public static void main( String[] args ) {
		
		// check argument number
		if ( args.length != 4 ) {
			System.err.println("Use:\n\tjava SearchEngineEvaluator database_file query_file query_id groundtruth_file" );
			return;
		}
		
		// get arguments
		String databaseFilePath = args[0];
		String queryFile = args[1];
		int queryId = Integer.parseInt( args[2] );
		String groundTruthFile = args[3];
		
		SearchEngine se = null;
		SearchEngineEvaluator see = null;
		Vector<RecallPrecisionPoint> rpps = null;
		double ap = 0.0;
		
		// TODO: initialize search engine object
		se = null;
		
		// load database file
		se.loadDatabaseFile( databaseFilePath );
		
		// create evaluator
		see = new SearchEngineEvaluator( se );
		
		// load ground truth file
		see.readGroundTruthFile( groundTruthFile );
		
		// load query file
		see.readQueryFile( queryFile );
		
		// compute interpolated recall-precision points
		rpps = see.evaluateRecallPrecisionPoints( queryId );
		
		// compute average precision
		ap = see.evaluateAveragePrecision( queryId );
		
		// print out results
		if ( rpps != null ) {
			for ( RecallPrecisionPoint rpp : rpps ) {
				System.out.println( rpp );
			}
		} else {
			System.err.println( "Error: no (precision, recall) points could be computed." );
		}
		
		System.out.println( "Average precision: " + ap );
	}
	
}
