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
   * Remove useless terms based on TF
   *
   * @throws java.io.IOException
   */
  public static void removeUselessTermsTF() throws IOException {

	Map<Integer, Map<Integer, Double>> TFmap = null;
	TFmap = getTFmap(TFmap, "bbc/bbc.classes");

	Iterator it = TFmap.entrySet().iterator();
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
	  double THRESHOLD = 0.5;
	  double avg = (businessFreq + entertnmFreq + politicsFreq + sportFreq + techFreq)/5.0;
	  //TODO
	  if(Math.abs(avg - businessFreq) < THRESHOLD) // numbers are too close
		TFmap.remove(termIndex);
	}
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
