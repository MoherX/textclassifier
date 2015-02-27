package FeatureSelection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tools.BasicIO;

public class PairAnalysis {
	
	/*
	 * Read bbc.terms file, get all the terms and the corresponding index.
	 */
	public Map<Integer, String> ReadTerms(String filePath) throws IOException{
		FileReader reader=new FileReader(filePath);
		BufferedReader br=new BufferedReader(reader);
		Map<Integer,String> terms=new HashMap<Integer, String>();
		String s1=null;
		int n=1;
			while ((s1=br.readLine())!=null){
				terms.put(n,s1);
				n++;
			}
		return terms;		
	}

	/*
	 * PairFinding can find the term pairs.
	 */
	public Map<String[], String[]> PairFinding(Map<Integer,Map<Integer,Double>> tfmatrix) throws IOException{
		Map<String[],String[]> pairs=new HashMap<String[],String[]>();	
		Map<Integer,String> terms=new HashMap<Integer, String>();
		int[] num=CategoryInfo();
		terms=ReadTerms("bbc/bbc.terms");
		for(int i=1;i<tfmatrix.size();i++){
			Map<Integer,Double> tf1=tfmatrix.get(i);
			for(int j=i+1;j<tfmatrix.size();j++){
				Map<Integer,Double> tf2=tfmatrix.get(j);
				String[] result=GetCorrelation(tf1,tf2,num);
				if(Double.parseDouble(result[0])>0.3){
					String[] pair=new String[2];
					pair[0]=terms.get(i);
					pair[1]=terms.get(j);
					pairs.put(pair, result);	
				} 
			}
		}
		return pairs;
	}
	
	/*
	 * Read bbc.classes file, get the category information of each news.
	 * 0: business
	 * 1: entertainment
	 * 2: politics
	 * 3: sport
	 * 4: tech
	 */
	public Map<Integer, Integer> ReadClasses(String filePath) throws IOException{
		FileReader reader=new FileReader(filePath);
		BufferedReader br=new BufferedReader(reader); 
		Map<Integer,Integer> classes=new HashMap<Integer,Integer>();
		 String s1 = null;
		 String[] s2=null;
		  while((s1 = br.readLine()) != null) {
			  s2=s1.split(" ");
			  classes.put(Integer.parseInt(s2[0]), Integer.parseInt(s2[1]));
		  }
		 br.close();
		 reader.close();
		 return classes;
	}
		
	/*
	 * get the correlation of two terms.
	 */
	public String[] GetCorrelation(Map<Integer, Double> tf1,Map<Integer, Double> tf2,int[] num){
		double score=0;
		String category=null;
		String[] result=new String[2];
		int overlap=0;
		int size1=tf1.size();
		int size2=tf2.size();
		int size=(size1>size2)?size1:size2;
		List<Integer> overlapdoc = new ArrayList<Integer>();
		Iterator it=tf1.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			int key1=(int) key;
			if(tf2.containsKey(key1)) {
				overlapdoc.add(key1);
				overlap++;	
			}
		}
		score=overlap*1.0/(size1+size2);
		if(score>0.3){
			category=judgeCategory(overlapdoc,num);
		}
		result[0]=Double.toString(score);
		result[1]=category;
		return result;
	}
	
	/*
	 * judge which category of one pair belongs to
	 */
	public String judgeCategory(List<Integer> overlap, int[] num){
		int index=0;
		double business=0;
		double entertainment=0;
		double politics=0;
		double sport=0;
		double tech=0;
		for(int i=0;i<overlap.size();i++){
			index=overlap.get(i);
			 if(index<=num[0]) business=business+1;
			    if(index>num[0]&&index<=num[1]) entertainment=entertainment+1;
			    if(index>num[1]&&index<=num[2]) politics=politics+1;
			    if(index>num[2]&&index<=num[3]) sport=sport+1;
			    if(index>num[3]) tech=tech+1;
		}
		String result="sport";
		double maxvalue=sport;
		if(tech>maxvalue) {
			result="tech";
			maxvalue=tech;
		}
		if(business>maxvalue){
			result="business";
			maxvalue=business;
		}
		if(politics>maxvalue){
			result="politics";
			maxvalue=politics;
		}
		if(entertainment>maxvalue){
			result="entertainment";
			maxvalue=entertainment;
		}
		return result;
	}
	
	/*
	 * get the category information
	 */
	public int[] CategoryInfo() throws IOException{
		Map<Integer,Integer> classes=new HashMap<Integer,Integer>();
		classes=(Map<Integer, Integer>) ReadClasses("bbc/bbc.classes");
		Map<Integer,Integer> numOfCategory=numOfCategory(classes);
		int[] num= new int[4];
		 num[0]=numOfCategory.get(0);
	     num[1]=num[0]+numOfCategory.get(1);
	     num[2]=num[1]+numOfCategory.get(2);
	     num[3]=num[2]+numOfCategory.get(3);
	     return num;
	}
	
	/*
	 * Get the file number of each category
	 */
	public Map<Integer,Integer> numOfCategory(Map<Integer,Integer> classes){
		Map<Integer,Integer> result=new HashMap<Integer,Integer>();
		int business=0;
		int entertainment=0;
		int politics=0;
		int sport=0;
		int tech=0;
		Iterator it=classes.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry=(Map.Entry) it.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			int key1=(int) key;
			int val1=(int) val;
			if(val1==0) business++;
			if(val1==1) entertainment++;
			if(val1==2) politics++;
			if(val1==3) sport++;
			if(val1==4) tech++;	
		}
		result.put(0, business);
		result.put(1, entertainment);
		result.put(2, politics);
		result.put(3, sport);
		result.put(4, tech);
		return result;
	}
	
	
	/*
	 * Read bbc.mtx file, get the MatrixMarket matrix coordinate real general. 9635 terms, 2225 files, 286774 total term frequency
	 */
	public Map<Integer, Map<Integer, Double>> ReadTFMatrix(String filePath) throws IOException{
		FileReader reader=new FileReader(filePath);
		BufferedReader br=new BufferedReader(reader);
		Map<Integer,Map<Integer,Double>> tfmatrix=new HashMap<Integer,Map<Integer,Double>>();
		Map<Integer,Double> tf1=new HashMap<Integer,Double>();
		tfmatrix.put(1, tf1);
		String s1=null;
		String[] s2=null;
		int term=1;
			while((s1=br.readLine())!=null){
				s2=s1.split(" ");
				if(term==Integer.parseInt(s2[0])){
					tfmatrix.get(term).put(Integer.parseInt(s2[1]), Double.parseDouble(s2[2]));
				}
				else{
					Map<Integer,Double> tf=new HashMap<Integer,Double>();
					tf.put(Integer.parseInt(s2[1]), Double.parseDouble(s2[2]));
					term++;
					tfmatrix.put(term, tf);
				}	
			}
			return tfmatrix;
	}
	/*
	 * pairs reformat, and save to file based on category
	 */
	public void savepair(Map<String[],String[]> pairs){
		//Map<String,String[]> business=new HashMap<String,String[]>();
		//Map<String,String[]> entertainment=new HashMap<String,String[]>();
		//Map<String,String[]> politics=new HashMap<String,String[]>();
		//Map<String,String[]> sport=new HashMap<String,String[]>();
		//Map<String,String[]> tech=new HashMap<String,String[]>();
		//List<String[]> business=new ArrayList<String[]>();
		//List<String[]> entertainment=new ArrayList<String[]>();
		//List<String[]> politics=new ArrayList<String[]>();
		//List<String[]> sport=new ArrayList<String[]>();
		//List<String[]> tech=new ArrayList<String[]>();
		StringBuffer business=new StringBuffer();
		StringBuffer entertainment=new StringBuffer();
		StringBuffer politics=new StringBuffer();
		StringBuffer sport=new StringBuffer();
		StringBuffer tech=new StringBuffer();
		
		Iterator it=pairs.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry=(Map.Entry) it.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			String[] pair=(String[]) key;
			String[] score=(String[]) val;
			if(score[1].equals("business")){
				business.append(score[0]+" "+pair[0]+" "+pair[1]+";");
			} 
			if(score[1].equals("entertainment")) {
				entertainment.append(score[0]+" "+pair[0]+" "+pair[1]+";");
			}
			if(score[1].equals("politics")) {
				politics.append(score[0]+" "+pair[0]+" "+pair[1]+";");
			}
			if(score[1].equals("sport")) {
				sport.append(score[0]+" "+pair[0]+" "+pair[1]+";");
			}
			if(score[1].equals("tech")) {
				tech.append(score[0]+" "+pair[0]+" "+pair[1]+";");
			}
		}
		String b=business.toString();
		BasicIO.writeTxtFile(b, "tfidf/businesspair.txt");
		String e=entertainment.toString();
		BasicIO.writeTxtFile(e, "tfidf/entertainmentpair.txt");
		String p=politics.toString();
		BasicIO.writeTxtFile(p, "tfidf/politicspair.txt");
		String s=sport.toString();
		BasicIO.writeTxtFile(s, "tfidf/sportpair.txt");
		String t=tech.toString();
		BasicIO.writeTxtFile(t, "tfidf/techpair.txt");
	}
	
	
	
	public static void main(String args[]) throws IOException{
		Map<Integer, Map<Integer, Double>> tfmatrix= new HashMap<Integer, Map<Integer, Double>>();
		PairAnalysis pairanalysis=new PairAnalysis();
		tfmatrix=pairanalysis.ReadTFMatrix("bbc/bbc.mtx");
		Map<String[],String[]> pairs=new HashMap<String[],String[]>();
		pairs=pairanalysis.PairFinding(tfmatrix);
		System.out.println(pairs.size());
		pairanalysis.savepair(pairs);
	}
}
