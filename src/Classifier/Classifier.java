package Classifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FeatureSelection.Porter;
import tools.BasicIO;

public class Classifier {
	public static String Classifier(String input1){
		String input=preprocess(input1);
		String[] words=input.split(" ");
		words=Porter.StemArgs(words);
		int length = words.length;
		String sports1=BasicIO.readTxtFile("tfidf/sport.txt");
		Map<String,Double> sports2=readTFIDF(sports1);
		String tech1=BasicIO.readTxtFile("tfidf/tech.txt");
		Map<String,Double> tech2=readTFIDF(tech1);
		String business1=BasicIO.readTxtFile("tfidf/business.txt");
		Map<String,Double> business2=readTFIDF(business1);
		String politics1=BasicIO.readTxtFile("tfidf/politics.txt");
		Map<String,Double> politics2=readTFIDF(politics1);
		String entertainment1=BasicIO.readTxtFile("tfidf/entertainment.txt");
		Map<String,Double> entertainment2=readTFIDF(entertainment1);
		double sport=0;
		double tech=0;
		double business=0;
		double politics=0;
		double entertainment=0;
		for(int i=0;i<words.length;i++){
			if(sports2.containsKey(words[i])) sport=sport+sports2.get(words[i]);
			if(tech2.containsKey(words[i])) tech=tech+tech2.get(words[i]);
			if(business2.containsKey(words[i])) business=business+business2.get(words[i]);
			if(politics2.containsKey(words[i])) politics=politics+politics2.get(words[i]);
			if(entertainment2.containsKey(words[i])) entertainment=entertainment+entertainment2.get(words[i]);
		}
		String result="sports";
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
		if(maxvalue<(5*(length*1.00/75))){
			result="The news does not belong to any category!";
		}
		return result;

	}
	public static List<String> readTF(String input){
		String[] a=input.split(", |=");
		List<String> ab=new ArrayList<String>();
		for(int j=0;j<a.length;j++){
			ab.add(a[j]);	
			j++;
		}
		return ab;
	}
	public static Map<String, Double> readTFIDF(String input){
		String[] a=input.split(", |=");
		Map<String,Double> tfidf=new HashMap<String,Double>();
		for(int j=0;j<a.length;j++){
			tfidf.put(a[j], Double.parseDouble(a[++j].trim()));
		}
		return tfidf;
	}
	public static String preprocess(String input){
		String str=input.replaceAll("[^a-zA-Z0-9 ]", "");
		return str;
		
	}
	public static void test(){
		String a=BasicIO.readTxtFile("/Users/wuqinghao/Documents/ucsb/14fall/CS_273/data/sports_test1.txt");
		//String[] b=a.split("talk.politics.misc	|talk.politics.mideast	|talk.politics.guns	");
		//String[] b=a.split("comp.graphics	|comp.os.ms-windows.misc	|comp.sys.ibm.pc.hardware	|comp.sys.mac.hardware|comp.windows.x	");
		//String[] b=a.split("rec.autos	|rec.motorcycles	");
		//String[] b=a.split("soc.religion.christian	|talk.religion.misc	");
		String[] b=a.split("rec.sport.baseball	|rec.sport.hockey	");
		int count_politics=0;
		int count_comp=0;
		int count_auto=0;
		int count_religion=0;
		int count_sports=0;
		int others=0;
		for(int i=1;i<b.length;i++){
			if(Classifier(b[i]).equals("politics")) count_politics++;
			if(Classifier(b[i]).equals("comp")) count_comp++;
			if(Classifier(b[i]).equals("religion")) count_religion++;
			if(Classifier(b[i]).equals("sports")) count_sports++;
			if(Classifier(b[i]).equals("auto")) count_auto++;
			if(Classifier(b[i]).equals("The news does not belong to any category!")) others++;
			
		}
		System.out.println(b.length-1);
		System.out.println("politics: "+count_politics);
		System.out.println("comp: "+count_comp);
		System.out.println("religion: "+count_religion);
		System.out.println("sports: "+count_sports);
		System.out.println("auto: "+count_auto);
		System.out.println("others: "+others);
	}
	public static void main(String args[]){
		//test();
		//String a=BasicIO.readTxtFile("a.txt");
		String result=Classifier("President Barack Obama told Democrats on Friday that their work has improved the economy while strengthening the middle class, and jabbed at Republicans for trying to take the credit after stiffly opposing his agenda for six years. Speaking at the Democratic National Committee's winter meeting, Obama said it is no accident that his policies have lifted the country out of the recession he inherited when he took office. GOP predictions of doom and gloom over policies like health care have proven untrue, the president said.");
		System.out.println(result);
	}
}
