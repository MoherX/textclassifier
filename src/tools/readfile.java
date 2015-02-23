package tools;

import java.io.File;
import java.util.ArrayList;

public class readfile {
	 final static void showAllFiles(File dir) throws Exception{
		  File[] fs = dir.listFiles();
		  for(int i=0; i<fs.length; i++){
		   System.out.println(fs[i].getAbsolutePath());
		   if(fs[i].isDirectory()){
		    try{
		     showAllFiles(fs[i]);
		    }catch(Exception e){}
		  }
		  }
		 }
	 public static ArrayList<String> readfiles(File dir){
		 File[] fs = dir.listFiles();
		 ArrayList<String> files=new ArrayList<String>();
		 for(int i=0; i<fs.length; i++){
			 files.add(fs[i].getAbsolutePath());
			   if(fs[i].isDirectory()){
			    try{
			    	readfiles(fs[i]);
			    }catch(Exception e){}
			  }
			  }
		 return files;
	 }
	 public static ArrayList<String> filenames(File dir){
		 File[] fs = dir.listFiles();
		 ArrayList<String> files=new ArrayList<String>();
		 for(int i=0; i<fs.length; i++){
			 files.add(fs[i].getName());
			   if(fs[i].isDirectory()){
			    try{
			    	readfiles(fs[i]);
			    }catch(Exception e){}
			  }
			  }
		 return files;
	 }

	public static void main(String[] args) throws Exception {
		
	}
}
