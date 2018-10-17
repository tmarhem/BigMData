package tp2.evaluation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map.Entry;

import tp2.evaluation.SearchEngineEvaluator.RecallPrecisionPoint;
import tp2.searchEngine.SearchEngineImpl;
import tp2.searchEngine.Similarity;

public class GlobalEvaluationAndOutput {
	
	static HashMap<String, Integer> filesQryNber;
	static HashMap<String, Integer> similarities;
	
	public GlobalEvaluationAndOutput() {
		filesQryNber = new HashMap<String, Integer>();
		filesQryNber.putIfAbsent("cacm", 64);
		// filesQryNber.putIfAbsent("cisi", 111);
		// filesQryNber.putIfAbsent("cran", 365);
		// filesQryNber.putIfAbsent("lisa", 34);
		// filesQryNber.putIfAbsent("med", 30);
		// filesQryNber.putIfAbsent("time", 82);

		similarities = new HashMap<String, Integer>();
		similarities.putIfAbsent("DICE", Similarity.DICE);
		//TODO ADD OTHER SIMILARITIES

	}

	public static void main(String[] args) throws IOException{

		// se searchengine
		// see searchengine evaluator

		/*
		 * Credits to https://www.callicoder.com/java-write-excel-file-apache-poi/ For
		 * the excel outputs
		 */

		
		String databaseFilePath;
		String queryFile;
		String groundTruthFile;
		SearchEngineImpl se;
		SearchEngineEvaluator see;

		// String[FileName][Similarity][QRY NUMBER][ RPPPOINT]
		Double[][][][] result = new Double[10][10][1000][15];
		HashMap<Integer, Vector<RecallPrecisionPoint>> queriesResults = new HashMap<Integer, Vector<RecallPrecisionPoint>>();
		int fileNameCtr = 0;
		int rppCtr = 0;

		// TODO Locate where to compile all queries to 1
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

			System.out.println("FILE Entered " + e.getKey());

			// FOR EACH SIMILARITY
			for (Entry<String,Integer> similarity : similarities.entrySet()) {
				System.out.println("	Similarity Entered " + similarity.getValue());

				// Adjust similarity type parameter
				se.setSimilarityType(similarity.getValue());

				// FOR EACH QRY
				for (int iQry = 1; iQry <= e.getValue(); iQry++) {
					System.out.println("		Query Entered " + iQry + " out of " + e.getValue());

					// Calculate similarity
					for (rppCtr = 0; rppCtr < 11; rppCtr++) {

						if (see.evaluate11pt(iQry) != null) {
							if (!see.evaluate11pt(iQry).isEmpty()) {
								queriesResults.put(iQry, see.evaluate11pt(iQry));
								// result[fileNameCtr][similarity][iQry][rppCtr]=
								// see.evaluate11pt(iQry).get(rppCtr).recall/see.evaluate11pt(iQry).get(rppCtr).precision;
							}
						}
					}

					// Change l-5 to break two dimensions and assign value here
					// result[fileNameCtr][similarity] = average<iQry,rppCtr>;
				}

				// Average the queries for a similarity
				HashMap<Integer, Double> tempResults = new HashMap<Integer, Double>();
				Double tempValue = 0.0;
				// FOR EACH QUERY
				for (Entry<Integer, Vector<RecallPrecisionPoint>> e1 : queriesResults.entrySet()) {
					// FOR EACH RPP
					// SUM
					int ctr = 0;
					for (RecallPrecisionPoint rpp : e1.getValue()) {
						// ADD IT TO GLOBAL QUERY RESULTS
						if (tempResults.containsKey(ctr)) {
							// TODO RESULTS GIVES BACK INFINITY IF VALUES ARE 0's
							tempResults.replace(ctr, tempResults.get(ctr) + rpp.result());
						}
						tempResults.putIfAbsent(ctr, rpp.result());
						ctr++;
					}
				}
				// DIVIDE BY QRY NUMBER
				for (Entry<Integer, Double> e2 : tempResults.entrySet()) {
					System.out.println(e2.getKey() + " : " + e2.getValue());
				}

				for (Entry<Integer, Double> e2 : tempResults.entrySet()) {
					tempValue = e2.getValue();
					tempResults.replace(e2.getKey(), tempValue / e.getValue());

					// DISPLAY RESULTS
					System.out.println(e2.getKey() + " : " + e2.getValue());
				}
				System.out.println(tempResults.size());

			}
		}

	}
////////////////////////////////////

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

//EXCELL OUTPUT
	/*
	 * // Write the output to a file FileOutputStream fileOut = new
	 * FileOutputStream("poi-generated-file.xlsx"); workbook.write(fileOut);
	 * fileOut.close(); // Closing the workbook workbook.close();
	 */

}
