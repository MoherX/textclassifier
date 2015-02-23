package tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

public class SortUtil {

	private static Comparator<Entry<String, Double>> comparator = new Comparator<Map.Entry<String,Double>>() {

		@Override
		public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
		
	};
	
	private static Comparator<Entry<String, Integer>> comparator1 = new Comparator<Map.Entry<String,Integer>>() {

		@Override
		public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
		
	};
	
	private static Comparator<Entry<Integer, Double>> comparator2 = new Comparator<Map.Entry<Integer,Double>>() {

		@Override
		public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
		
	};

	public static void sortEntry(ArrayList<Entry<String, Double>> entries) {
		Collections.sort(entries, comparator);
	}
	
	public static void sortEntry1(ArrayList<Entry<String, Integer>> entries) {
		Collections.sort(entries, comparator1);
	}
	
	public static void sortEntry2(ArrayList<Entry<Integer, Double>> entries) {
		Collections.sort(entries, comparator2);
	}
}
