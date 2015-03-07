package tools;

import static Classifier.Classifier.readTFIDF;
import static FeatureSelection.MatrixAnalysis.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class preprocessing {
  
  public static ArrayList<String> readStopWordList() {
	ArrayList<String> arrList = new ArrayList<>();
	try {
	  String encoding = "utf-8";
	  File inputFile = new File("stopwordlist.txt");
	  if (inputFile.isFile() && inputFile.exists()) {
		InputStreamReader inputRead = new InputStreamReader(
				new FileInputStream(inputFile), encoding);
		BufferedReader bufferedReader = new BufferedReader(inputRead);
		String lineTxt = null;
		while ((lineTxt = bufferedReader.readLine()) != null) {
		  arrList.add(lineTxt);
		}
		bufferedReader.close();
	  }
	} catch (Exception e) {
	  e.printStackTrace();
	}
	return arrList;
  }

  /**
   * Calculates the distances between 5 numbers and verifies if the maximum
   * distance is greater than a threshold value.
   *
   * @param numbers
   * @param threshold
   * @return
   */
  private static boolean absDistance(double[] numbers, double threshold) {
	double maxdistance = Double.MIN_VALUE;
	double temp;
	temp = Math.abs(numbers[0] - numbers[1]);
	if (temp > maxdistance) {
	  maxdistance = temp;
	}
	temp = Math.abs(numbers[0] - numbers[2]);
	if (temp > maxdistance) {
	  maxdistance = temp;
	}
	temp = Math.abs(numbers[0] - numbers[3]);
	if (temp > maxdistance) {
	  maxdistance = temp;
	}
	temp = Math.abs(numbers[0] - numbers[4]);
	if (temp > maxdistance) {
	  maxdistance = temp;
	}
	temp = Math.abs(numbers[1] - numbers[2]);
	if (temp > maxdistance) {
	  maxdistance = temp;
	}
	temp = Math.abs(numbers[1] - numbers[3]);
	if (temp > maxdistance) {
	  maxdistance = temp;
	}
	temp = Math.abs(numbers[1] - numbers[4]);
	if (temp > maxdistance) {
	  maxdistance = temp;
	}
	temp = Math.abs(numbers[2] - numbers[3]);
	if (temp > maxdistance) {
	  maxdistance = temp;
	}
	temp = Math.abs(numbers[2] - numbers[4]);
	if (temp > maxdistance) {
	  maxdistance = temp;
	}
	temp = Math.abs(numbers[3] - numbers[4]);
	if (temp > maxdistance) {
	  maxdistance = temp;
	}
	
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
	  Map<Integer, Double> totalTF = (Map<Integer, Double>) entry.getValue();
	  // Get the term frequency for each category
	  double businessFreq = totalTF.get(0);
	  double entertnmFreq = totalTF.get(1);
	  double politicsFreq = totalTF.get(2);
	  double sportFreq = totalTF.get(3);
	  double techFreq = totalTF.get(4);
	  // Check whether the frequencies values are close to each other
	  double THRESHOLD = 0.2;
	  double[] numbers = {businessFreq, entertnmFreq, politicsFreq, sportFreq, techFreq};
	  if (absDistance(numbers, THRESHOLD) == true) // numbers are too close
	  {
		TermTFIDF_Copy.remove(termIndex);
	  }
	  // TODO remove terms which appear in top positions in different categories.
	}
	return TermTFIDF_Copy;
  }

  /**
   * Remove useless terms based on DF
   */
  public static List<Integer> removeUselessTermsDF(Map<Integer, Map<Integer, Double>> tfmatrix) throws IOException {
	Map<Integer, Map<Integer, Double>> TermDF = getDFmap(tfmatrix, "bbc/bbc.classes");
	List<Integer> termsToDelete = new ArrayList<Integer>();
	Iterator it = TermDF.entrySet().iterator();
	while (it.hasNext()) {
	  Map.Entry entry = (Map.Entry) it.next();
	  int termIndex = (int) entry.getKey();
	  Map<Integer, Double> totalTF = (Map<Integer, Double>) entry.getValue();
	  // Get the term frequency for each category
	  double businessFreq = totalTF.get(0);
	  double entertnmFreq = totalTF.get(1);
	  double politicsFreq = totalTF.get(2);
	  double sportFreq = totalTF.get(3);
	  double techFreq = totalTF.get(4);
	  // Check whether the frequencies values are close to each other
	  double THRESHOLD = 100;
	  double[] numbers = {businessFreq, entertnmFreq, politicsFreq, sportFreq, techFreq};
	  if (absDistance(numbers, THRESHOLD) == true) // numbers are too close
	  {
		termsToDelete.add(termIndex);
	  }
	}
	return termsToDelete;
  }
  
  public static ArrayList<Map.Entry<String, Double>> getTopNTerms(ArrayList<Map.Entry<String, Double>> catArray, int n) {
	ArrayList<Map.Entry<String, Double>> subList = new ArrayList<>();
	for (int i = 0; i < n; i++) {
	  subList.add(catArray.get(i));
	}
	return subList;
  }
  
  public static ArrayList<String> getRepeatedTerms(ArrayList<String> terms) {
	// Store items that are duplicates in result.
	ArrayList<String> result = new ArrayList<>();
	// Record encountered for the first time.
	HashSet<String> setfirst = new HashSet<>();
	// Record encountered for the second time.
	HashSet<String> setsecond = new HashSet<>();

	// Loop over argument list.
	for (String item : terms) {
	  // If String is not in set, add it to the and the set.
	  if (!setfirst.contains(item)) {
		setfirst.add(item);
	  } else if(!setsecond.contains(item)) {
		setsecond.add(item);
	  } else if (!result.contains(item)) // if the set contains at least 2 times the term, put in the list
		  result.add(item);
	}
	return result;
  }
}
