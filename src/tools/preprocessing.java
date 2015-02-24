package tools;

import static FeatureSelection.MatrixAnalysis.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class preprocessing {

  public static HashMap<String, String> readStopWordList() {
	HashMap<String, String> hashMap = new HashMap<String, String>();
	try {
	  String encoding = "utf-8";
	  File inputFile = new File("files/stopwordlist.txt");
	  if (inputFile.isFile() && inputFile.exists()) {
		InputStreamReader inputRead = new InputStreamReader(
				new FileInputStream(inputFile), encoding);
		BufferedReader bufferedReader = new BufferedReader(inputRead);
		String lineTxt = null;
		while ((lineTxt = bufferedReader.readLine()) != null) {
		  hashMap.put(lineTxt, null);
		}
		bufferedReader.close();
	  }
	} catch (Exception e) {
	  e.printStackTrace();
	}
	return hashMap;
  }

  /**
   * Calculates the distances between 5 numbers and verifies if the maximum
   * distance is greater than a threshold value.
   * @param numbers
   * @param threshold
   * @return 
   */
  private static boolean absDistance(double []numbers, double threshold) {
	double maxdistance = Double.MIN_VALUE;
	double temp;
	temp = Math.abs(numbers[0] - numbers[1]);
	if(temp > maxdistance)
	  maxdistance = temp;
	temp = Math.abs(numbers[0] - numbers[2]);
	if(temp > maxdistance)
	  maxdistance = temp;
	temp = Math.abs(numbers[0] - numbers[3]);
	if(temp > maxdistance)
	  maxdistance = temp;
	temp = Math.abs(numbers[0] - numbers[4]);
	if(temp > maxdistance)
	  maxdistance = temp;
	temp = Math.abs(numbers[1] - numbers[2]);
	if(temp > maxdistance)
	  maxdistance = temp;
	temp = Math.abs(numbers[1] - numbers[3]);
	if(temp > maxdistance)
	  maxdistance = temp;
	temp = Math.abs(numbers[1] - numbers[4]);
	if(temp > maxdistance)
	  maxdistance = temp;
	temp = Math.abs(numbers[2] - numbers[3]);
	if(temp > maxdistance)
	  maxdistance = temp;
	temp = Math.abs(numbers[2] - numbers[4]);
	if(temp > maxdistance)
	  maxdistance = temp;
	temp = Math.abs(numbers[3] - numbers[4]);
	if(temp > maxdistance)
	  maxdistance = temp;
	
	return maxdistance < threshold;
  }
  /**
   * Remove useless terms based on TF
   *
   * @throws java.io.IOException
   */
  public static Map<Integer, Map<Integer, Double>> removeUselessTermsTF(Map<Integer, Map<Integer, Double>> TermTFIDF) throws IOException {
	Map<Integer, Map<Integer, Double>> TermTFIDF_Copy = new HashMap<>(TermTFIDF);
	Iterator it = TermTFIDF.entrySet().iterator();
	while (it.hasNext()) {
	  Map.Entry entry = (Map.Entry) it.next();
	  int termIndex = (int) entry.getKey();
	  Map<Integer, Double> totalTF = (Map<Integer, Double>)entry.getValue();
	  // Get the term frequency for each category
	  double businessFreq = totalTF.get(0);
	  double entertnmFreq = totalTF.get(1);
	  double politicsFreq = totalTF.get(2);
	  double sportFreq    = totalTF.get(3);
	  double techFreq     = totalTF.get(4);
	  // Check whether the frequencies values are close to each other
	  double THRESHOLD = 0.2;
	  double[] numbers = { businessFreq, entertnmFreq, politicsFreq, sportFreq, techFreq};
	  if(absDistance(numbers, THRESHOLD) == true) // numbers are too close
		TermTFIDF_Copy.remove(termIndex);
	  // TODO remove terms which appear in top positions in different categories.
	}
	return TermTFIDF_Copy;
  }

  /**
   * Remove useless terms based on DF
   */
  public static void removeUselessTermsDF() throws IOException {
	// TODO
	Map<Integer, Map<Integer, Double>> DFmap = null;
	DFmap = getDFmap(DFmap, "bbc/bbc.classes");

	Iterator it = DFmap.entrySet().iterator();
	while (it.hasNext()) {
	  Map.Entry entry = (Map.Entry) it.next();
	  int termIndex = (int) entry.getKey();
	  Map<Integer, Double> totalDF = (Map<Integer, Double>)entry.getValue();
	  // Get the term frequency for each category
	  double businessFreq = totalDF.get(0);
	  double entertnmFreq = totalDF.get(1);
	  double politicsFreq = totalDF.get(2);
	  double sportFreq    = totalDF.get(3);
	  double techFreq     = totalDF.get(4);
	  // Check whether the frequencies values are close to each other
	  double THRESHOLD = 0.5;
	  double avg = (businessFreq + entertnmFreq + politicsFreq + sportFreq + techFreq)/5.0;
	  if(Math.abs(avg - businessFreq) < THRESHOLD) // numbers are too close
		DFmap.remove(termIndex);
	}
  }
}
