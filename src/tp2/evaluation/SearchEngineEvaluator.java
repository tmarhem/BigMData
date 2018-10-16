package tp2.evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import tp2.searchEngine.DocumentInfo;
import tp2.searchEngine.SearchEngine;
import tp2.searchEngine.SearchEngineImpl;
import tp2.searchEngine.Similarity;

/**
 * A simple search engine evaluator computing (recall, precision) points for a
 * single query. It is assumed that the search engine would return results and
 * that there are expected results for all queries.
 * 
 * @author Pierre Tirilly
 */
public class SearchEngineEvaluator {

	/**
	 * Pattern to parse a line of TREC ground truth files.
	 */
	private static final String GT_REGEX_PATTERN = "(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)";

	/**
	 * Pattern to parse a query file line with the following format : query_id
	 * query_text
	 */
	private static final String QRY_REGEX_PATTERN = "^\\s*(\\d+)\\s+(.+)$";

	/**
	 * Inner class to store the computed (recall, precision) couples.
	 * 
	 * @author Pierre Tirilly
	 */
	private class RecallPrecisionPoint {
		double recall;
		double precision;

		/**
		 * Constructor.
		 * 
		 * @param recall    The recall value of the point.
		 * @param precision The precision value of the point.
		 */
		private RecallPrecisionPoint(double recall, double precision) {
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
	private HashMap<Integer, Vector<Integer>> groundtruth;

	/**
	 * The queries as <query_id, query_text> couples.
	 */
	private HashMap<Integer, String> queries;

	/**
	 * Constructor for a search engine evaluator.
	 * 
	 * @param searchEngine The search engine to be evaluated.
	 */
	public SearchEngineEvaluator(SearchEngine searchEngine) {
		this.searchEngine = searchEngine;
		this.queries = null;
		this.groundtruth = null;
	}

	/**
	 * Reads an input query file where each line has the following format : query_id
	 * query_text.
	 * 
	 * @param file The file to be read.
	 */
	public void readQueryFile(String file) {
		BufferedReader br;
		String line;
		Pattern qryPattern = Pattern.compile(QRY_REGEX_PATTERN);
		Matcher qryMatcher = qryPattern.matcher("");
		int queryId;
		String queryContent;

		this.queries = new HashMap<Integer, String>();

		try {
			br = new BufferedReader(new FileReader(file));

			while (br.ready()) {
				line = br.readLine();
				line.trim();
				qryMatcher.reset(line);
				if (qryMatcher.matches()) {
					queryId = Integer.parseInt(qryMatcher.group(1));
					queryContent = qryMatcher.group(2);
					this.queries.put(queryId, queryContent);
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Read TREC ground truth files.
	 * 
	 * @param file The file to be read.
	 */
	public void readGroundTruthFile(String file) {
		BufferedReader br;
		String line;
		Pattern gtPattern = Pattern.compile(GT_REGEX_PATTERN);
		Matcher gtMatcher = gtPattern.matcher("");
		int queryId;
		int docId;

		// initialize ground truth map
		this.groundtruth = new HashMap<Integer, Vector<Integer>>();

		try {
			br = new BufferedReader(new FileReader(file));

			while (br.ready()) {
				line = br.readLine();
				line.trim();
				gtMatcher.reset(line);
				if (gtMatcher.matches()) {
					queryId = Integer.parseInt(gtMatcher.group(1));
					docId = Integer.parseInt(gtMatcher.group(2));
					if (!this.groundtruth.containsKey(queryId)) {
						this.groundtruth.put(queryId, new Vector<Integer>());
					}
					this.groundtruth.get(queryId).add(docId);
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Computes precision-recall points for the given query.
	 * 
	 * @param queryId The ID of the query to use.
	 * @return A vector of the computed (precision, recall) points, or null if no
	 *         groundtruth exists for the query.
	 */
	public Vector<RecallPrecisionPoint> evaluateRecallPrecisionPoints(int queryId) {
		return this.evaluate(queryId, false);
	}

	/**
	 * Computes precision-recall for the given query using 11-pt interpolation.
	 * 
	 * @param queryId The ID of the query to use.
	 * @return A vector of the 11 (precision, recall) points, or null if no
	 *         groundtruth exists for the query.
	 */
	public Vector<RecallPrecisionPoint> evaluate11pt(int queryId) {
		return this.evaluate(queryId, true);
	}

	/**
	 * Computes precision-recall points for the given query.
	 * 
	 * @param queryId              The ID of the query to use.
	 * @param use11ptInterpolation If true, 11-point interpolation is used.
	 * @return A vector of the computed (precision, recall) points, or null if no
	 *         groundtruth exists for the query.
	 */
	private Vector<RecallPrecisionPoint> evaluate(int queryId, boolean use11ptInterpolation) {
		int truePositives;
		String query;
		Vector<DocumentInfo> searchResults;
		Vector<RecallPrecisionPoint> recallPrecisionPoints = null;

		// get the query
		if (this.queries.containsKey(queryId)) {
			query = this.queries.get(queryId);
		} else {
			System.err.println("Error: cannot find query " + queryId + ".");
			return null;
		}

		// get the search results for this query
		searchResults = this.searchEngine.queryDatabase(query);

		// evaluate
		if (this.groundtruth.containsKey(queryId) && this.groundtruth.get(queryId).size() > 0) { // if groundtruth
																									// contains
																									// documents
			recallPrecisionPoints = new Vector<RecallPrecisionPoint>();
			if (searchResults.size() == 0) { // nothing retrieved
				recallPrecisionPoints.add(new RecallPrecisionPoint(0.0, 0.0));
			} else {

				// compute recall and precision points for each DCV
				truePositives = 0;
				for (int i = 0; i < searchResults.size(); i++) {
					if (this.groundtruth.get(queryId).contains(searchResults.get(i).getId())) {
						truePositives++;
					}
					recallPrecisionPoints.add(new RecallPrecisionPoint(
							(double) truePositives / (double) this.groundtruth.get(queryId).size(),
							(double) truePositives / (double) (i + 1)));
				}
				recallPrecisionPoints = this.interpolate(recallPrecisionPoints);
			}

			if (use11ptInterpolation) {
				recallPrecisionPoints = this.to11pt(recallPrecisionPoints);
			}
		}

		return recallPrecisionPoints;
	}

	/**
	 * Computes average precision for the given query.
	 * 
	 * @param queryId The ID of the query to use.
	 * @return The average precision for the given query. Returns -1.0 if no
	 *         groundtruth exists for the query.
	 */
	public double evaluateAveragePrecision(int queryId) {
		int truePositives;
		String query;
		Vector<DocumentInfo> searchResults;
		double ap;

		// get the query
		if (this.queries.containsKey(queryId)) {
			query = this.queries.get(queryId);
		} else {
			System.err.println("Error: cannot find query " + queryId + ".");
			return Double.NaN;
		}

		// get the search results for this query
		searchResults = this.searchEngine.queryDatabase(query);

		// evaluate
		ap = 0.0;
		if (!this.groundtruth.containsKey(queryId) || this.groundtruth.get(queryId).size() == 0) { // no documents to
																									// retrieve
			ap = Double.NaN; // skip query
		} else if (searchResults.size() != 0) {
			// compute precision point for each relevant document returned
			truePositives = 0;
			for (int i = 0; i < searchResults.size(); i++) {
				if (this.groundtruth.get(queryId).contains(searchResults.get(i).getId())) {
					truePositives++;
					ap += (double) truePositives / (double) (i + 1);
				}
			}
			ap /= this.groundtruth.get(queryId).size();
		}

		return ap;
	}

	// interpolation of recall-precision curve
	private Vector<RecallPrecisionPoint> interpolate(Vector<RecallPrecisionPoint> points) {
		TreeMap<Double, RecallPrecisionPoint> interpolatedPoints = new TreeMap<Double, SearchEngineEvaluator.RecallPrecisionPoint>();
		double maxPrecision;
		double minRecall = 1.01;

		for (RecallPrecisionPoint rpp : points) {
			if (minRecall >= rpp.recall) {
				minRecall = rpp.recall;
			}
			if (!interpolatedPoints.containsKey(rpp.recall)) { // if recall point does not exist yet
				maxPrecision = 0.0;
				for (RecallPrecisionPoint rpp2 : points) {
					if (rpp2.recall >= rpp.recall && rpp2.precision >= maxPrecision) {
						maxPrecision = rpp2.precision;
					}
				}
				interpolatedPoints.put(rpp.recall, new RecallPrecisionPoint(rpp.recall, maxPrecision));
			}
		}

		// precision at minimum recall for recall 0 point
		interpolatedPoints.put(0.0, new RecallPrecisionPoint(0.0, interpolatedPoints.get(minRecall).precision));

		return new Vector<RecallPrecisionPoint>(interpolatedPoints.values());
	}

	// compute 11-pt recall precision points from sorted, interpolated regular
	// recall precision points
	private Vector<RecallPrecisionPoint> to11pt(Vector<RecallPrecisionPoint> points) {
		Vector<RecallPrecisionPoint> points11 = new Vector<RecallPrecisionPoint>();
		RecallPrecisionPoint newPoint;

		for (double cut = 0.0; cut <= 1.0; cut += 0.1) {
			newPoint = null;
			for (RecallPrecisionPoint p : points) {
				if (p.recall >= cut) {
					newPoint = new RecallPrecisionPoint(cut, p.precision);
					break;
				}
			}
			if (newPoint == null) {
				newPoint = new RecallPrecisionPoint(cut, 0.0);
			}
			points11.add(newPoint);
		}

		return points11;
	}

	/**
	 * Returns the numbers of queries in the evaluator.
	 * 
	 * @return The number of queries in the evaluator
	 */
	public int nbQueries() {
		return this.queries.size();
	}

	/**
	 * Outputs the specified (recall, precision) points to a file.
	 * 
	 * @param file The path of the file to write.
	 * @param rpps A vector of (recall, precision) points to output.
	 */
	public void outputRPPoints(String file, Vector<RecallPrecisionPoint> rpps) {
		if (rpps == null || file == null) {
			return;
		}

		BufferedWriter writer;

		try {
			writer = new BufferedWriter(new FileWriter(file));
			for (RecallPrecisionPoint rpp : rpps) {
				writer.write(rpp.toString());
				writer.newLine();
			}
			writer.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	// Main method
	// Use: java SearchEngineEvaluator database_file query_file query_id
	// groundtruth_file
	public static void main(String[] args) throws IOException {

		/*
		 * Credits to https://www.callicoder.com/java-write-excel-file-apache-poi/ For
		 * the excel outputs
		 */

		HashMap<String, Integer> filesQryNber = new HashMap<String, Integer>();
		filesQryNber.putIfAbsent("cacm", 64);
		//filesQryNber.putIfAbsent("cisi", 111);
		//filesQryNber.putIfAbsent("cran", 365);
		//filesQryNber.putIfAbsent("lisa", 34);
		//filesQryNber.putIfAbsent("med", 30);
		//filesQryNber.putIfAbsent("time", 82);

		Integer[] similarities = { Similarity.DICE, Similarity.VECTOR, Similarity.VECTORIDF,
				Similarity.VECTORIDF_NONORM };
		String databaseFilePath;
		String queryFile;
		String groundTruthFile;
		SearchEngineImpl se;
		SearchEngineEvaluator see;
		
		//String[FileName][Similarity][QRY NUMBER][ RPPPOINT]
		Double[][][][] result = new Double[10][10][1000][15];
		int fileNameCtr = 0;
		int rppCtr = 0;
		
		// EXCELL
		/*
		 * Workbook workbook = new XSSFWorkbook(); CreationHelper createHelper =
		 * workbook.getCreationHelper(); HashMap<Integer, Sheet> sheets = new
		 * HashMap<Integer, Sheet>(); HashMap<Integer, Row> rows = new
		 * HashMap<Integer,Row>(); Integer[] numbers = {0, 1, 2, 3, 4, 5}; String[]
		 * headers = {"Interpolated point/Similaity Type", "Qry ID", "DICE", "VECTOR",
		 * "VECTOR IDF", "VECTOR IDF NO NORM"}; String [] int11points=
		 * {"0.0","0.1","0.2","0.3","0.4","0.5","0.6","0.7","0.8","0.9","1"};
		 * 
		 * // XLS different sheets Sheet[] sheetsArray = { workbook.createSheet("CACM"),
		 * workbook.createSheet("CISI"), workbook.createSheet("CRAN"),
		 * workbook.createSheet("LISA"), workbook.createSheet("MED"),
		 * workbook.createSheet("TIME") };
		 */

		// HEARDERS
		/*
		 * int i = 0; for (Sheet s : sheetsArray) { sheets.put(i, s); i++; }
		 * 
		 * int i1=0; for (Entry<Integer, Sheet> e : sheets.entrySet()) {
		 * e.getValue().createRow(0); for(String s : int11points) {
		 * e.getValue().createRow(i1++).createCell(0).setCellValue(s); } int i2 = 0;
		 * for(String s : headers) {
		 * e.getValue().getRow(0).createCell(i2).setCellValue(s);
		 * e.getValue().autoSizeColumn(i2++); }
		 * 
		 * }
		 */

		//TODO Locate where to compile all queries to 1
		// TODO WARNING some of the requests answers may be faulty and crush the
		// averages
///////////////////////////////////

//FOR EACH FILE
		for (Entry<String, Integer> e : filesQryNber.entrySet()) {

			// INITIALIZING FILE
			databaseFilePath = "evaluation/" + e.getKey() + "/" + e.getKey() + ".trec";
			queryFile = "evaluation/" + e.getKey() + "/" + e.getKey() + ".qry";
			groundTruthFile = "evaluation/" + e.getKey() + "/" + e.getKey() + ".qrel";
			se = null;
			see = null;
			se = new SearchEngineImpl();
			see = new SearchEngineEvaluator(se);
			se.loadDatabaseFile(databaseFilePath);
			see.readGroundTruthFile(groundTruthFile);
			see.readQueryFile(queryFile);
			
			System.out.println("FILE Entered "+e.getKey());

			// FOR EACH SIMILARITY
			for (Integer similarity : similarities) {
				System.out.println("	Similarity Entered "+similarity);

				//Adjust similarity type parameter
				se.setSimilarityType(similarity);
				
				// FOR EACH QRY
				for (int iQry = 1; iQry < e.getValue(); iQry++) {
					System.out.println("		Query Entered "+iQry+" out of "+e.getValue());

					//Calculate similarity
					for(rppCtr = 0; rppCtr<11 ; rppCtr++) {

						if(see.evaluate11pt(iQry)!=null) {
							if(!see.evaluate11pt(iQry).isEmpty()) {
								result[fileNameCtr][similarity][iQry][rppCtr]= see.evaluate11pt(iQry).get(rppCtr).recall/see.evaluate11pt(iQry).get(rppCtr).precision;
							}
						}
					}
					
					//Change l-5 to break two dimensions and assign value here
					//result[fileNameCtr][similarity] = average<iQry,rppCtr>;
				}
			}
		}
		
for(Double[][][] d:result) {
	for(Double[][] d1:d) {
		for(Double[] d2 : d1) {
			for(Double d3:d2) {
				System.out.println("Looping frenetically on"+d3);
			}
		}
	}
	
}
////////////////////////////////////

//EXCELL OUTPUT
		/*
		 * // Write the output to a file FileOutputStream fileOut = new
		 * FileOutputStream("poi-generated-file.xlsx"); workbook.write(fileOut);
		 * fileOut.close(); // Closing the workbook workbook.close();
		 */

	}

}
