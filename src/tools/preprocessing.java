package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class preprocessing {

	public static HashMap<String,String> readStopWordList() {
		HashMap<String,String> hashMap=new HashMap<String,String>();
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
	
	public static void main(String args[]){
		HashMap<String,String> stopwords=new HashMap<String,String>();
		stopwords=readStopWordList();
		if(stopwords.containsKey("no")){
		System.out.println(stopwords.size());}
	}
}
