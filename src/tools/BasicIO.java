package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class BasicIO {
	public static String readTxtFile(String inputFilePath) {
		String txtContent = "";
		try {
			String encoding = "utf-8";
			File inputFile = new File(inputFilePath);
			if (inputFile.isFile() && inputFile.exists()) {
				InputStreamReader inputRead = new InputStreamReader(
						new FileInputStream(inputFile), encoding);
				BufferedReader bufferedReader = new BufferedReader(inputRead);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					txtContent += lineTxt;
					txtContent += "\r\n";
				}
				bufferedReader.close();
			}
		} catch (Exception e) {
			System.out.println("Wrong!");
			e.printStackTrace();
		}
		return txtContent;
	}
	public static void newFolder(String folderPath){
		try{
			File filePath=new File(folderPath);
			if(!filePath.exists()){
				filePath.mkdir();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void delFolder(String folderPath){
		try{
		String filePath= folderPath;
		File delPath = new File(filePath);
		 File files[] = delPath.listFiles(); 
	    for(int i=0;i<files.length;i++){ 
	    files[i].delete(); 
	     } 
		delPath.delete();
		}
	   catch (Exception e) {
		 System.out.println("No such file.");e.printStackTrace();}
	      }
	
	public static void writeTxtFile(String str, String filePath) {
		try {
			File fileSave = new File(filePath);
			PrintWriter printWriter = new PrintWriter(fileSave);
			printWriter.print(str);
			printWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
