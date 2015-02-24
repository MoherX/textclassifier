package FeatureSelection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import tools.BasicIO;
import tools.SortUtil;
import tools.preprocessing;

public class MatrixAnalysis {
  
  /*
   * Read bbc.classes file, get the category information of each news.
   * The file format is <document number> <category>
   * 0: business
   * 1: entertainment
   * 2: politics
   * 3: sport
   * 4: tech
   */
  public static Map<Integer, Integer> ReadClasses(String filePath) throws IOException {
	FileReader reader = new FileReader(filePath);
	BufferedReader br = new BufferedReader(reader);
	Map<Integer, Integer> classes = new HashMap<>();
	String s1 = null;
	String[] s2 = null;
	while ((s1 = br.readLine()) != null) {
	  s2 = s1.split(" ");
	  classes.put(Integer.parseInt(s2[0]), Integer.parseInt(s2[1]));
	}
	br.close();
	reader.close();
	return classes;
  }

  /*
   * Get how many documents does each category have.
   */
  public static Map<Integer, Integer> getNumDocsByCategory(Map<Integer, Integer> classes) {
	Map<Integer, Integer> result = new HashMap<>();
	int business = 0;
	int entertainment = 0;
	int politics = 0;
	int sport = 0;
	int tech = 0;
	Iterator it = classes.entrySet().iterator();
	while (it.hasNext()) {
	  Map.Entry entry = (Map.Entry) it.next();
	  Object key = entry.getKey();
	  Object val = entry.getValue();
	  int key1 = (int) key;
	  int val1 = (int) val;
	  if (val1 == 0) {
		business++;
	  }
	  if (val1 == 1) {
		entertainment++;
	  }
	  if (val1 == 2) {
		politics++;
	  }
	  if (val1 == 3) {
		sport++;
	  }
	  if (val1 == 4) {
		tech++;
	  }
	}
	result.put(0, business);
	result.put(1, entertainment);
	result.put(2, politics);
	result.put(3, sport);
	result.put(4, tech);
	return result;
  }

  /*
   * Read bbc.terms file, get all the terms and the corresponding index.
   * The file format is <term name>
   */
  public static Map<Integer, String> ReadTerms(String filePath) throws IOException {
	FileReader reader = new FileReader(filePath);
	BufferedReader br = new BufferedReader(reader);
	Map<Integer, String> terms = new HashMap<>();
	String s1 = null;
	int n = 1;
	while ((s1 = br.readLine()) != null) {
	  terms.put(n, s1);
	  n++;
	}
	return terms;
  }

  /*
   * Read bbc.mtx file, get the MatrixMarket matrix coordinate real general. 9635 terms, 2225 files, 286774 total term frequency
   * The file format is <term id> <document number> <term frequency>
   */
  public static Map<Integer, Map<Integer, Double>> ReadTFMatrix(String filePath) throws IOException {
	FileReader reader = new FileReader(filePath);
	BufferedReader br = new BufferedReader(reader);
	Map<Integer, Map<Integer, Double>> tfmatrix = new HashMap<>();
	Map<Integer, Double> tf1 = new HashMap<Integer, Double>();
	tfmatrix.put(1, tf1);
	String s1 = null;
	String[] s2 = null;
	int term = 1;
	while ((s1 = br.readLine()) != null) {
	  s2 = s1.split(" ");
	  if (term == Integer.parseInt(s2[0])) {
		tfmatrix.get(term).put(Integer.parseInt(s2[1]), Double.parseDouble(s2[2]));
	  } else {
		Map<Integer, Double> tf = new HashMap<>();
		tf.put(Integer.parseInt(s2[1]), Double.parseDouble(s2[2]));
		term++;
		tfmatrix.put(term, tf);
	  }
	}
	return tfmatrix;
  }

  /*
   * get the map for total term frequency of each term in each category
   */
  public static Map<Integer, Map<Integer, Double>> getTFmap(Map<Integer, Map<Integer, Double>> tfmatrix, String filePath) throws IOException {
	Map<Integer, Integer> classes = new HashMap<>();
	classes = (Map<Integer, Integer>) ReadClasses(filePath);
	Map<Integer, Integer> numOfCategory = getNumDocsByCategory(classes);
	Map<Integer, Map<Integer, Double>> TermTF = new HashMap<>();
	Iterator it = tfmatrix.entrySet().iterator();
	while (it.hasNext()) {
	  Map.Entry entry = (Map.Entry) it.next();
	  Object key = entry.getKey();
	  Object val = entry.getValue();
	  int key1 = (int) key;
	  Map<Integer, Double> val1 = (Map<Integer, Double>) val;
	  TermTF.put(key1, getTF(val1, numOfCategory));
	}
	return TermTF;
  }

  /*
   * get the total term frequency of one term in each category
   */
  public static Map<Integer, Double> getTF(Map<Integer, Double> tf, Map<Integer, Integer> numDocsByCategory) throws IOException {
	double business = 0;
	double entertainment = 0;
	double politics = 0;
	double sport = 0;
	double tech = 0;
	int num1 = numDocsByCategory.get(0);
	int num2 = num1 + numDocsByCategory.get(1);
	int num3 = num2 + numDocsByCategory.get(2);
	int num4 = num3 + numDocsByCategory.get(3);
	Map<Integer, Double> tf1 = new HashMap<>();
	Iterator iter = tf.entrySet().iterator();
	while (iter.hasNext()) {
	  Map.Entry entry = (Map.Entry) iter.next();
	  Object key = entry.getKey();
	  Object val = entry.getValue();
	  int key1 = (int) key;
	  double val1 = (double) val;
	  if (key1 <= num1) {
		business = business + val1;
	  }
	  if (key1 > num1 && key1 <= num2) {
		entertainment = entertainment + val1;
	  }
	  if (key1 > num2 && key1 <= num3) {
		politics = politics + val1;
	  }
	  if (key1 > num3 && key1 <= num4) {
		sport = sport + val1;
	  }
	  if (key1 > num4) {
		tech = tech + val1;
	  }
	}
	// TF Calculation
	tf1.put(0, (business + 1) / numDocsByCategory.get(0));
	tf1.put(1, (entertainment + 1) / numDocsByCategory.get(1));
	tf1.put(2, (politics + 1) / numDocsByCategory.get(2));
	tf1.put(3, (sport + 1) / numDocsByCategory.get(3));
	tf1.put(4, (tech + 1) / numDocsByCategory.get(4));
	return tf1;
  }

  /*
   * get the map for total inverse document frequency of each term in each category
   */
  public static Map<Integer, Map<Integer, Double>> getDFmap(Map<Integer, Map<Integer, Double>> tfmatrix, String filePath) throws IOException {
	Map<Integer, Integer> classes = new HashMap<>();
	classes = (Map<Integer, Integer>) ReadClasses(filePath);
	Map<Integer, Integer> numDocsByCategory = getNumDocsByCategory(classes);
	Map<Integer, Map<Integer, Double>> TermDF = new HashMap<>();
	Iterator it = tfmatrix.entrySet().iterator();
	while (it.hasNext()) {
	  Map.Entry entry = (Map.Entry) it.next();
	  Object key = entry.getKey();
	  Object val = entry.getValue();
	  int key1 = (int) key;
	  Map<Integer, Double> val1 = (Map<Integer, Double>) val;
	  TermDF.put(key1, getDF(val1, numDocsByCategory));
	}
	return TermDF;
  }
  
  /*
   * get the inverse document frequency of each term.
   */
  public static Map<Integer, Double> getDF(Map<Integer, Double> tf, Map<Integer, Integer> numDocsByCategory) throws IOException {
	double business = 0;
	double entertainment = 0;
	double politics = 0;
	double sport = 0;
	double tech = 0;
	int num1 = numDocsByCategory.get(0);
	int num2 = num1 + numDocsByCategory.get(1);
	int num3 = num2 + numDocsByCategory.get(2);
	int num4 = num3 + numDocsByCategory.get(3);
	int sum = num4 + numDocsByCategory.get(4);
	Map<Integer, Double> df = new HashMap<>();
	Iterator iter = tf.entrySet().iterator();
	while (iter.hasNext()) {
	  Map.Entry entry = (Map.Entry) iter.next();
	  Object key = entry.getKey();
	  Object val = entry.getValue();
	  int key1 = (int) key;
	  double val1 = (double) val;
	  if (key1 <= num1) {
		business = business + 1;
	  }
	  if (key1 > num1 && key1 <= num2) {
		entertainment = entertainment + 1;
	  }
	  if (key1 > num2 && key1 <= num3) {
		politics = politics + 1;
	  }
	  if (key1 > num3 && key1 <= num4) {
		sport = sport + 1;
	  }
	  if (key1 > num4) {
		tech = tech + 1;
	  }
	}
	// DF calculation
	df.put(0, business/(numDocsByCategory.get(0)));
	df.put(1, entertainment/(numDocsByCategory.get(1)));
	df.put(2, politics/(numDocsByCategory.get(2)));
	df.put(3, sport/(numDocsByCategory.get(3)));
	df.put(4, tech/(numDocsByCategory.get(4)));
	return df;
  }

  /*
   * get the inverse document frequency of each term.
   */
  public static Map<Integer, Double> getIDF(Map<Integer, Double> tf, Map<Integer, Integer> numDocsByCategory) throws IOException {
	double business = 0;
	double entertainment = 0;
	double politics = 0;
	double sport = 0;
	double tech = 0;
	int num1 = numDocsByCategory.get(0);
	int num2 = num1 + numDocsByCategory.get(1);
	int num3 = num2 + numDocsByCategory.get(2);
	int num4 = num3 + numDocsByCategory.get(3);
	int sum = num4 + numDocsByCategory.get(4);
	Map<Integer, Double> idf = new HashMap<>();
	Iterator iter = tf.entrySet().iterator();
	while (iter.hasNext()) {
	  Map.Entry entry = (Map.Entry) iter.next();
	  Object key = entry.getKey();
	  Object val = entry.getValue();
	  int key1 = (int) key;
	  double val1 = (double) val;
	  if (key1 <= num1) {
		business = business + 1;
	  }
	  if (key1 > num1 && key1 <= num2) {
		entertainment = entertainment + 1;
	  }
	  if (key1 > num2 && key1 <= num3) {
		politics = politics + 1;
	  }
	  if (key1 > num3 && key1 <= num4) {
		sport = sport + 1;
	  }
	  if (key1 > num4) {
		tech = tech + 1;
	  }
	}
	double sum1 = business + entertainment + politics + sport + tech;
	// IDF calculation
	idf.put(0, Math.log(10 * (sum * (business + 1)) / (sum1 * numDocsByCategory.get(0))));
	idf.put(1, Math.log((10 * sum * (entertainment + 1)) / (sum1 * numDocsByCategory.get(1))));
	idf.put(2, Math.log((10 * sum * (politics + 1)) / (sum1 * numDocsByCategory.get(2))));
	idf.put(3, Math.log((10 * sum * (sport + 1)) / (sum1 * numDocsByCategory.get(3))));
	idf.put(4, Math.log((10 * sum * (tech + 1)) / (sum1 * numDocsByCategory.get(4))));
	return idf;
  }

  /*
   * get the map for tfidf value of each term in each category
   */
  public Map<Integer, Map<Integer, Double>> getTFIDFmap(Map<Integer, Map<Integer, Double>> tfmatrix, String filePath) throws IOException {
	Map<Integer, Integer> classes = new HashMap<>();
	classes = (Map<Integer, Integer>) ReadClasses(filePath);
	Map<Integer, Integer> numDocsByCategory = getNumDocsByCategory(classes);
	Map<Integer, Map<Integer, Double>> TermTFIDF = new HashMap<>();
	Iterator it = tfmatrix.entrySet().iterator();
	while (it.hasNext()) {
	  Map.Entry entry = (Map.Entry) it.next();
	  Object key = entry.getKey();
	  Object val = entry.getValue();
	  int key1 = (int) key;
	  Map<Integer, Double> val1 = (Map<Integer, Double>) val;
	  TermTFIDF.put(key1, getTFIDF(val1, numDocsByCategory));
	}
	return TermTFIDF;
  }

  /*
   * Get the tfidf value of one term for one category
   */
  public static Map<Integer, Double> getTFIDF(Map<Integer, Double> tf, Map<Integer, Integer> numDocsByCategory) throws IOException {
	double business = 0;
	double entertainment = 0;
	double politics = 0;
	double sport = 0;
	double tech = 0;
	double business1 = 0;
	double entertainment1 = 0;
	double politics1 = 0;
	double sport1 = 0;
	double tech1 = 0;
	int num1 = numDocsByCategory.get(0);
	int num2 = num1 + numDocsByCategory.get(1);
	int num3 = num2 + numDocsByCategory.get(2);
	int num4 = num3 + numDocsByCategory.get(3);
	int sum = num4 + numDocsByCategory.get(4);
	Map<Integer, Double> tfidf = new HashMap<>();
	Iterator iter = tf.entrySet().iterator();
	while (iter.hasNext()) {
	  Map.Entry entry = (Map.Entry) iter.next();
	  Object key = entry.getKey();
	  Object val = entry.getValue();
	  int key1 = (int) key;
	  double val1 = (double) val;
	  if (key1 <= num1) {
		business = business + 1;
		business1 = business1 + val1;
	  }
	  if (key1 > num1 && key1 <= num2) {
		entertainment = entertainment + 1;
		entertainment1 = entertainment1 + val1;
	  }
	  if (key1 > num2 && key1 <= num3) {
		politics = politics + 1;
		politics1 = politics1 + val1;
	  }
	  if (key1 > num3 && key1 <= num4) {
		sport = sport + 1;
		sport1 = sport1 + val1;
	  }
	  if (key1 > num4) {
		tech = tech + 1;
		tech1 = tech1 + val1;
	  }
	}
	double sum1 = business + entertainment + politics + sport + tech;
	tfidf.put(0, (business1 + 1) / numDocsByCategory.get(0) * Math.log(10 * (sum * (business + 1)) / (sum1 * numDocsByCategory.get(0))));
	tfidf.put(1, (entertainment1 + 1) / numDocsByCategory.get(1) * Math.log((10 * sum * (entertainment + 1)) / (sum1 * numDocsByCategory.get(1))));
	tfidf.put(2, (politics1 + 1) / numDocsByCategory.get(2) * Math.log((10 * sum * (politics + 1)) / (sum1 * numDocsByCategory.get(2))));
	tfidf.put(3, (sport1 + 1) / numDocsByCategory.get(3) * Math.log((10 * sum * (sport + 1)) / (sum1 * numDocsByCategory.get(3))));
	tfidf.put(4, (tech1 + 1) / numDocsByCategory.get(4) * Math.log((10 * sum * (tech + 1)) / (sum1 * numDocsByCategory.get(4))));
	return tfidf;
  }

  /*
   * get tfidf value of the terms of each category.
   */
  public static void SaveTFIDF(Map<Integer, Map<Integer, Double>> TermTFIDF) throws IOException {
	Map<String, Double> business = new HashMap<>();
	Map<String, Double> entertainment = new HashMap<>();
	Map<String, Double> politics = new HashMap<>();
	Map<String, Double> sport = new HashMap<>();
	Map<String, Double> tech = new HashMap<>();
	String term = null;
	Map<Integer, String> terms = ReadTerms("bbc/bbc.terms");
	Iterator iter = TermTFIDF.entrySet().iterator();
	while (iter.hasNext()) {
	  Map.Entry entry = (Map.Entry) iter.next();
	  Object key = entry.getKey();
	  Object val = entry.getValue();
	  int key1 = (int) key;
	  Map<Integer, Double> tf = (Map<Integer, Double>) val;
	  term = terms.get(key1);
	  business.put(term, tf.get(0));
	  entertainment.put(term, tf.get(1));
	  politics.put(term, tf.get(2));
	  sport.put(term, tf.get(3));
	  tech.put(term, tf.get(4));
	}

	ArrayList<Map.Entry<String, Double>> sorted_business = new ArrayList<>(
			business.entrySet());
	SortUtil.sortEntry(sorted_business);
	String a = sorted_business.toString();
	BasicIO.writeTxtFile(a, "tfidf/business.txt");

	ArrayList<Map.Entry<String, Double>> sorted_entertainment = new ArrayList<>(
			entertainment.entrySet());
	SortUtil.sortEntry(sorted_entertainment);
	String b = sorted_entertainment.toString();
	BasicIO.writeTxtFile(b, "tfidf/entertainment.txt");

	ArrayList<Map.Entry<String, Double>> sorted_politics = new ArrayList<>(
			politics.entrySet());
	SortUtil.sortEntry(sorted_politics);
	String c = sorted_politics.toString();
	BasicIO.writeTxtFile(c, "tfidf/politics.txt");

	ArrayList<Map.Entry<String, Double>> sorted_sport = new ArrayList<>(
			sport.entrySet());
	SortUtil.sortEntry(sorted_sport);
	String d = sorted_sport.toString();
	BasicIO.writeTxtFile(d, "tfidf/sport.txt");

	ArrayList<Map.Entry<String, Double>> sorted_tech = new ArrayList<>(
			tech.entrySet());
	SortUtil.sortEntry(sorted_tech);
	String e = sorted_tech.toString();
	BasicIO.writeTxtFile(e, "tfidf/tech.txt");
  }

  public static void main(String args[]) throws IOException {
	MatrixAnalysis matrix = new MatrixAnalysis();
	Map<Integer, Map<Integer, Double>> TermTFIDF = new HashMap<>();
	Map<Integer, Map<Integer, Double>> tfmatrix = new HashMap<>();
	// Populate Term Frequency
	tfmatrix = matrix.ReadTFMatrix("bbc/bbc.mtx");
	// Calculate TFIDF
	TermTFIDF = matrix.getTFIDFmap(tfmatrix, "bbc/bbc.classes");
	// Remove useless terms.
	TermTFIDF = preprocessing.removeUselessTermsTF(TermTFIDF);
	
	matrix.SaveTFIDF(TermTFIDF);
	System.out.println("finished!");
  }

}
