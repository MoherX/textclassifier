package Classifier;

import static FeatureSelection.PairAnalysis.ReadPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FeatureSelection.Porter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.BasicIO;

public class Classifier {

  String input;
  String[] words;
  String sports1;
  Map<String, Double> sports2;

  String tech1;
  Map<String, Double> tech2;

  String business1;
  Map<String, Double> business2;

  String politics1;
  Map<String, Double> politics2;

  String entertainment1;
  Map<String, Double> entertainment2;

  Map<Double, String[]> businessPairMap;
  Map<Double, String[]> sportsPairMap;
  Map<Double, String[]> entertainmentPairMap;
  Map<Double, String[]> politicsPairMap;
  Map<Double, String[]> techPairMap;

  double numDocs = 0; // Total number of documents read
  double correct = 0; // Number of correct classifications

  double politicsCorrect = 0; // Number of correct classifications for politics
  double sportsCorrect = 0; // Number of correct classifications for sports
  double techCorrect = 0; // Number of correct classifications for tech
  double businessCorrect = 0; // Number of correct classifications for business
  double entertainmentCorrect = 0; // Number of correct classifications for entertainment

  int[][] confusionMatrix;

  public int getCategoryNumber(String category) {
	/*
	 * Get a score for each category
	 * 0: business
	 * 1: entertainment
	 * 2: politics
	 * 3: sport
	 * 4: tech
	 */
	switch (category) {
	  case "business":
		return 0;
	  case "entertainment":
		return 1;
	  case "politics":
		return 2;
	  case "sports":
		return 3;
	  case "tech":
		return 4;
	}
	return -1;
  }

  public Classifier() throws IOException {
	// Read TFIDF
	sports1 = BasicIO.readTxtFile("tfidf/sport.txt");
	sports1 = sports1.replace("[", "");
	sports1 = sports1.replace("]", "");
	sports2 = readTFIDF(sports1);

	tech1 = BasicIO.readTxtFile("tfidf/tech.txt");
	tech1 = tech1.replace("[", "");
	tech1 = tech1.replace("]", "");
	tech2 = readTFIDF(tech1);

	business1 = BasicIO.readTxtFile("tfidf/business.txt");
	business1 = business1.replace("[", "");
	business1 = business1.replace("]", "");
	business2 = readTFIDF(business1);

	politics1 = BasicIO.readTxtFile("tfidf/politics.txt");
	politics1 = politics1.replace("[", "");
	politics1 = politics1.replace("]", "");
	politics2 = readTFIDF(politics1);

	entertainment1 = BasicIO.readTxtFile("tfidf/entertainment.txt");
	entertainment1 = entertainment1.replace("[", "");
	entertainment1 = entertainment1.replace("]", "");
	entertainment2 = readTFIDF(entertainment1);

	// Read Pairs
	businessPairMap = ReadPair("tfidf/businesspair.txt");
	sportsPairMap = ReadPair("tfidf/sportpair.txt");
	entertainmentPairMap = ReadPair("tfidf/entertainmentpair.txt");
	politicsPairMap = ReadPair("tfidf/politicspair.txt");
	techPairMap = ReadPair("tfidf/techpair.txt");

	// Initialize confusion matrix
	confusionMatrix = new int[5][5];
  }

  public Double[] getPairScores(String[] words) {
	List<String> wordList = Arrays.asList(words);
	Double[] scores = new Double[5];
	scores[0] = 0.0;
	scores[1] = 0.0;
	scores[2] = 0.0;
	scores[3] = 0.0;
	scores[4] = 0.0;
	/*
	 * Get a score for each category
	 * 0: business
	 * 1: entertainment
	 * 2: politicsCorrect
	 * 3: sport
	 * 4: tech
	 */

	// Iterate through all pair maps
	// Business
	Iterator it = businessPairMap.entrySet().iterator();
	while (it.hasNext()) {
	  Map.Entry entry = (Map.Entry) it.next();
	  Double key = (Double) entry.getKey();
	  String[] val = (String[]) entry.getValue();
	  if (val.length != 2) {
		continue;
	  }
	  if (wordList.contains(val[0]) && wordList.contains(val[1])) {
		scores[0] += key;
	  }
	}

	// Entertainment
	it = entertainmentPairMap.entrySet().iterator();
	while (it.hasNext()) {
	  Map.Entry entry = (Map.Entry) it.next();
	  Double key = (Double) entry.getKey();
	  String[] val = (String[]) entry.getValue();
	  if (val.length != 2) {
		continue;
	  }
	  if (wordList.contains(val[0]) && wordList.contains(val[1])) {
		scores[1] += key;
	  }
	}

	// Sport
	it = politicsPairMap.entrySet().iterator();
	while (it.hasNext()) {
	  Map.Entry entry = (Map.Entry) it.next();
	  Double key = (Double) entry.getKey();
	  String[] val = (String[]) entry.getValue();
	  if (val.length != 2) {
		continue;
	  }
	  if (wordList.contains(val[0]) && wordList.contains(val[1])) {
		scores[2] += key;
	  }
	}

	// Sport
	it = sportsPairMap.entrySet().iterator();
	while (it.hasNext()) {
	  Map.Entry entry = (Map.Entry) it.next();
	  Double key = (Double) entry.getKey();
	  String[] val = (String[]) entry.getValue();
	  if (val.length != 2) {
		continue;
	  }
	  if (wordList.contains(val[0]) && wordList.contains(val[1])) {
		scores[3] += key;
	  }
	}

	// Tech
	it = techPairMap.entrySet().iterator();
	while (it.hasNext()) {
	  Map.Entry entry = (Map.Entry) it.next();
	  Double key = (Double) entry.getKey();
	  String[] val = (String[]) entry.getValue();
	  if (val.length != 2) {
		continue;
	  }
	  if (wordList.contains(val[0]) && wordList.contains(val[1])) {
		scores[4] += key;
	  }
	}
	return scores;
  }

  /**
   * Classifies a document
   *
   * @param input1 string with the document contents
   * @return
   */
  public String classify(String input1) {

	input = preprocess(input1);
	words = input.split(" ");
	words = Porter.StemArgs(words);

	// Get pair scores
	Double[] pairscores = getPairScores(words);
	double sport = pairscores[3];
	double tech = pairscores[4];
	double business = pairscores[0];
	double politics = pairscores[2];
	double entertainment = pairscores[1];
	sport *= 10;
	tech *= 10;
	business *= 10;
	politics *= 10;
	entertainment = pairscores[1];

	int length = words.length;

	for (int i = 0; i < words.length; i++) {
	  if (sports2.containsKey(words[i])) {
		sport = sport + sports2.get(words[i]);
	  }
	  if (tech2.containsKey(words[i])) {
		tech = tech + tech2.get(words[i]);
	  }
	  if (business2.containsKey(words[i])) {
		business = business + business2.get(words[i]);
	  }
	  if (politics2.containsKey(words[i])) {
		politics = politics + politics2.get(words[i]);
	  }
	  if (entertainment2.containsKey(words[i])) {
		entertainment = entertainment + entertainment2.get(words[i]);
	  }
	}
	String result = "sports";
	double maxvalue = sport;
	if (tech > maxvalue) {
	  result = "tech";
	  maxvalue = tech;
	}
	if (business > maxvalue) {
	  result = "business";
	  maxvalue = business;
	}
	if (politics > maxvalue) {
	  result = "politics";
	  maxvalue = politics;
	}
	if (entertainment > maxvalue) {
	  result = "entertainment";
	  maxvalue = entertainment;
	}
	return result;

  }

  public static List<String> readTF(String input) {
	String[] a = input.split(", |=");
	List<String> ab = new ArrayList<String>();
	for (int j = 0; j < a.length; j++) {
	  ab.add(a[j]);
	  j++;
	}
	return ab;
  }

  public static Map<String, Double> readTFIDF(String input) {
	String[] a = input.split(", |=");
	Map<String, Double> tfidf = new HashMap<String, Double>();
	for (int j = 0; j < a.length; j++) {
	  tfidf.put(a[j], Double.parseDouble(a[++j].trim()));
	}
	return tfidf;
  }

  public static String preprocess(String input) {
	String str = input.replaceAll("[^a-zA-Z0-9 ]", "");
	return str;

  }

  /**
   * Classifies the 20 newsgroups data set
   *
   * @param clsf classifier object
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static void classify20Newsgroups(Classifier clsf) throws FileNotFoundException, IOException {
	double politics = 0; // Number testing documents in politics category
	double sports = 0; // Number testing documents in sports category
	double tech = 0; // Number testing documents in tech category

	// Read the 20 newsgroups dataset
	try (BufferedReader br = new BufferedReader(new FileReader("20ng-train-no-stop.txt"))) {
	  for (String line; (line = br.readLine()) != null;) {
		// process the line.
		// Separate category from news content
		String[] contents = line.split("\t");
		// Get category
		String category = contents[0];

		// Skip these. BBC does not have a matching category
		if (category.contains("alt") || category.contains("religion") || category.contains("forsale")
				|| category.contains("sci") || category.contains("autos") || category.contains("motorcycles")) {
		  continue;
		}

		// Define the category according to the classifier
		if (category.contains("politics")) {
		  category = "politics";
		  politics++;
		} else if (category.contains("sport")) {
		  category = "sports";
		  sports++;
		} else if (category.contains("comp")) {
		  category = "tech";
		  tech++;
		}
		// Missing: business and enterntainment

		// Get classifier result
		String result = clsf.classify(contents[1]);

		// Compare classifier result with dataset actual category
		if (result.equals(category)) { // Correct classification
		  clsf.correct++;
		  if (result.equals("politics")) {
			clsf.politicsCorrect++;
		  } else if (result.equals("sports")) {
			clsf.sportsCorrect++;
		  } else if (result.equals("tech")) {
			clsf.techCorrect++;
		  }
		} else { // Incorrect classification
		  /*
		   * Get a score for each category
		   * 0: business
		   * 1: entertainment
		   * 2: politicsCorrect
		   * 3: sport
		   * 4: tech
		   */
		  int categoryNo = clsf.getCategoryNumber(category);
		  int resultNo = clsf.getCategoryNumber(result);
		  clsf.confusionMatrix[resultNo][categoryNo]++;
		}

		clsf.numDocs++;
	  }
	  System.out.println("Classifier politics accuracy = " + clsf.politicsCorrect / politics);
	  System.out.println("Classifier sports accuracy = " + clsf.sportsCorrect / sports);
	  System.out.println("Classifier tech accuracy = " + clsf.techCorrect / tech);
	}
  }

  /**
   * Classifies the Reuters data set
   *
   * @param clsf classifier object
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static void classifyReuters(Classifier clsf) throws FileNotFoundException, IOException {
	double businessDocs = 0;

	// Read the 20 newsgroups dataset
	try (BufferedReader br = new BufferedReader(new FileReader("r8-train-no-stop.txt"))) {
	  for (String line; (line = br.readLine()) != null;) {
		// process the line.
		// Separate category from news content
		String[] contents = line.split("\t");
		// Get category
		String category = contents[0];

		// Skip these. BBC does not have a matching category
		if (category.contains("earn")) {
		  continue;
		}
		category = "business";
		// Get classifier result
		String result = clsf.classify(contents[1]);

		// Compare classifier result with dataset actual category
		if (result.equals(category)) { // Correct classification
		  clsf.businessCorrect++;
		  clsf.correct++;
		} else {
		  int categoryNo = clsf.getCategoryNumber(category);
		  int resultNo = clsf.getCategoryNumber(result);
		  clsf.confusionMatrix[resultNo][categoryNo]++;
		}
		clsf.numDocs++;
		businessDocs++;
	  }
	  System.out.println("Classifier business accuracy = " + clsf.businessCorrect / businessDocs);
	}
  }

  /**
   * Get all files from a directory
   *
   * @param dir directory to be searched
   * @return list containing the name of all files in dir.
   */
  public static ArrayList<String> readFiles(File dir) {
	File[] fs = dir.listFiles();
	ArrayList<String> files = new ArrayList<>();
	for (int i = 0; i < fs.length; i++) {
	  files.add(fs[i].getAbsolutePath());
	  if (fs[i].isDirectory()) {
		try {
		  readFiles(fs[i]);
		} catch (Exception e) {
		}
	  }
	}
	return files;
  }

  /**
   * Classifies the MSN entertainment data set
   *
   * @param clsf classifier object
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static void classifyMSN(Classifier clsf) throws FileNotFoundException, IOException {
	double entertainmentDocs = 0;

	// Get all files in the MSN dataset directory
	File dir = new File("entertainment.msn.com");
	ArrayList<String> files = readFiles(dir);

	// Read the MSN dataset
	for (String file : files) {
	  String fileContents = null;
	  try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		// Gather all content from a file
		for (String line; (line = br.readLine()) != null;) {
		  fileContents += line;
		}

		String category = "entertainment";
		// Get classifier result
		String result = clsf.classify(fileContents);

		// Compare classifier result with dataset actual category
		if (result.equals(category)) { // Correct classification
		  clsf.entertainmentCorrect++;
		  clsf.correct++;
		} else {
		  int categoryNo = clsf.getCategoryNumber(category);
		  int resultNo = clsf.getCategoryNumber(result);
		  clsf.confusionMatrix[resultNo][categoryNo]++;
		}
		clsf.numDocs++;
		entertainmentDocs++;
	  }
	}
	double accuracy = clsf.correct / clsf.numDocs;
	System.out.println("Classifier entertainment accuracy = " + clsf.entertainmentCorrect / entertainmentDocs);
	System.out.println("Classifier total accuracy = " + accuracy);

	// Print number of correct/incorrect classifications by category
	System.out.println("Correct politics classifications = " + clsf.politicsCorrect);
	System.out.println("Correct sports classifications = " + clsf.sportsCorrect);
	System.out.println("Correct tech classifications = " + clsf.techCorrect);
	System.out.println("Correct business classifications = " + clsf.businessCorrect);

	clsf.confusionMatrix[0][0] = (int) clsf.businessCorrect;
	clsf.confusionMatrix[1][1] = (int) clsf.entertainmentCorrect;
	clsf.confusionMatrix[2][2] = (int) clsf.politicsCorrect;
	clsf.confusionMatrix[3][3] = (int) clsf.sportsCorrect;
	clsf.confusionMatrix[4][4] = (int) clsf.techCorrect;
	
	for (int i = 0; i < 5; i++) {
	  for (int j = 0; j < 5; j++) {
		System.out.print(clsf.confusionMatrix[i][j] + " ");
	  }
	  System.out.print("\n");
	}
  }

  public static void main(String args[]) throws IOException {
	// Initialize classifier
	Classifier clsf = new Classifier();
	try {
	  classify20Newsgroups(clsf);
	  classifyReuters(clsf);
	  classifyMSN(clsf);
	} catch (IOException ex) {
	  Logger.getLogger(Classifier.class
			  .getName()).log(Level.SEVERE, null, ex);
	}

  }
}
