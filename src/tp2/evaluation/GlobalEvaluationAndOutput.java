package tp2.evaluation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Map.Entry;

import tp2.evaluation.SearchEngineEvaluator.RecallPrecisionPoint;
import tp2.searchEngine.SearchEngine;
import tp2.searchEngine.SearchEngineImpl;
import tp2.searchEngine.Similarity;

public class GlobalEvaluationAndOutput {

	static HashMap<String, Integer> filesQryNber;
	static HashMap<String, Integer> similarities;

	static String databaseFilePath;
	static String queryFile;
	static String groundTruthFile;
	static SearchEngineImpl se;
	static SearchEngineEvaluator see;

	// String[FileName][Similarity][QRY NUMBER][RPPPOINT]
	static Double[][][][] result;
	static HashMap<Integer, Vector<RecallPrecisionPoint>> queriesResults;
	static HashMap<Integer, Double> averagedQueries;
	static HashMap<Integer,HashMap<Integer, Double>> similarityResults;
	static int fileNameCtr;
	static int rppCtr;

	static Workbook wb;
	static CreationHelper createHelper;

	// HashMap< FILE , SIMILARITY, POINTID, DOUBLE>

	public GlobalEvaluationAndOutput() {
		filesQryNber = new HashMap<String, Integer>();
		filesQryNber.put("cacm", 64);
// 		filesQryNber.putIfAbsent("cisi", 111);
// 		filesQryNber.putIfAbsent("cran", 365);
//		filesQryNber.putIfAbsent("lisa", 34);
//		filesQryNber.putIfAbsent("med", 30);
//		filesQryNber.putIfAbsent("time", 82);

		similarities = new HashMap<String, Integer>();
		similarities.putIfAbsent("DICE", Similarity.DICE);
//		similarities.putIfAbsent("DICE", Similarity.VECTOR);
//		similarities.putIfAbsent("DICE", Similarity.VECTORIDF);
//		similarities.putIfAbsent("DICE", Similarity.VECTORIDF_NONORM);

		se = new SearchEngineImpl();
		see = new SearchEngineEvaluator(se);

		result = new Double[10][10][1000][15];
		queriesResults = new HashMap<Integer, Vector<RecallPrecisionPoint>>();
		averagedQueries = new HashMap<Integer, Double>();
		fileNameCtr = 0;
		rppCtr = 0;

		wb = new XSSFWorkbook();
		createHelper = wb.getCreationHelper();
	}

	private void produceXLSOutput() throws FileNotFoundException, IOException {
		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream("SearchEngineEvaluation.xlsx");
		wb.write(fileOut);
		fileOut.close(); // Closing the workbook workbook.close();
	}
	
	/*
	 * Adds sheet in workbook
	 */
	public static void fillSheet(HashMap<Integer, Double> pointsMap, Sheet s, String similarityType) throws IOException {
		

	}

	/*
	 * Adds sheet in workbook with the headers 
	 * INPUT String sheetName name given to the sheet
	 */
	public Sheet createSheet(String sheetName) throws IOException {

		Sheet s = wb.createSheet(sheetName);

		// HEARDERS
		// First Row
		String[] int11points = { "0.0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1" };
		// First column
		String[] headers = { "Interpolated point", "DICE", "VECTOR", "VECTOR IDF", "VECTOR IDF NO NORM" };

		int i1 = 1;
		s.createRow(0);
		for (String s1 : int11points) {
			s.createRow(i1++).createCell(0).setCellValue(s1);
		}
		int i2 =0;
		for (String s2 : headers) {
			s.getRow(0).createCell(i2).setCellValue(s2);
			s.autoSizeColumn(i2++);
		}
		return s;
	}

	/*
	 * For a specific eval document and similiarity type, Compiles the different
	 * queries into a single set of 11 points INPUT HashMap of the results from the
	 * different queries OUTPUT HashMap of results for the 11 interpolated points
	 */
	public HashMap<Integer, Double> averageQueries(HashMap<Integer, Vector<RecallPrecisionPoint>> queriesResults) {

		// result map
		HashMap<Integer, Double> result = new HashMap<Integer, Double>();
		// nb querys actually computed (may be different from total query)
		int queryCtr = 0;
		double lastValue = 0;

		// Summing all values for each 0.1
		for (Entry<Integer, Vector<RecallPrecisionPoint>> e : queriesResults.entrySet()) {
			if (e.getValue() != null) {
				queryCtr++;

				// for each interpolated point
				for (int ctr = 0; ctr < 10; ctr++) {
					// If exists, add to current val
					if (result.containsKey(ctr)) {
						lastValue = result.get(ctr);
						result.replace(ctr, lastValue + e.getValue().get(ctr).result());
					} else {
						// else make first put
						result.putIfAbsent(ctr, e.getValue().get(ctr).result());
					}
				}
			}
		}

		// Dividing by the nb of queries computed
		if (!result.isEmpty()) {
			for (Entry<Integer, Double> eResult : result.entrySet()) {
				// error case
				if (queryCtr == 0) {
					System.err.println("No query could be computed");
					return result;
				} else {
					eResult.setValue(eResult.getValue() / queryCtr);
				}
			}
		}
		return result;
	}

	/*
	 * For a specific eval document and similarity type, compute each query into a
	 * Map INPUT QueryFile Entry <String NAME, Integer QRY_NB> OUTPUT HashMap
	 * <Integer QRY_NB, Vector<RPP> result>
	 */
	public HashMap<Integer, Vector<RecallPrecisionPoint>> computeQueries(Entry<String, Integer> eQryFile) {

		HashMap<Integer, Vector<RecallPrecisionPoint>> result;
		result = new HashMap<Integer, Vector<RecallPrecisionPoint>>();

		for (int qryNumber = 0; qryNumber <= eQryFile.getValue(); qryNumber++) {
			if (result.containsKey(qryNumber)) {
				System.err.println("Query number conflict for " + qryNumber + " : " + eQryFile.getKey());
			}
			result.putIfAbsent(qryNumber, see.evaluate11pt(qryNumber));
		}

		return result;
	}

	public static void main(String[] args) throws IOException {

		/*
		 * Credits to https://www.callicoder.com/java-write-excel-file-apache-poi/ For
		 * the excel outputs
		 */

		// TODO WARNING some of the requests answers may be faulty and crush the
		// averages -> do not count null answers
///////////////////////////////////PROGRAM GUIDELINE////////////////////////////////////
		/*
		 * Create WorkBook For each file For each similarity type Create Sheet
		 * "File-Similarity type" compute queries average queries output in XLS sheet
		 * 
		 * TODO NEED ONE MORE MAP <Similarity, Results>
		 */
		
		GlobalEvaluationAndOutput mGEAO = new GlobalEvaluationAndOutput();

//FOR EACH FILE
		for (Entry<String, Integer> e : filesQryNber.entrySet()) {

			// INITIALIZING FILE
			databaseFilePath = "evaluation/" + e.getKey() + "/" + e.getKey() + ".trec";
			queryFile = "evaluation/" + e.getKey() + "/" + e.getKey() + ".qry";
			groundTruthFile = "evaluation/" + e.getKey() + "/" + e.getKey() + ".qrel";

			se.loadDatabaseFile(databaseFilePath);
			see.readGroundTruthFile(groundTruthFile);
			see.readQueryFile(queryFile);

			System.out.println("FILE Entered " + e.getKey());

			// FOR EACH SIMILARITY
			for (Entry<String, Integer> similarity : similarities.entrySet()) {
				mGEAO.createSheet("test");
				mGEAO.produceXLSOutput();
			}
		}
	}
}
