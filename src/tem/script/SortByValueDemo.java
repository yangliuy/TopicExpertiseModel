package tem.script;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;


public class SortByValueDemo {
	
	public static void main(String[] args) throws SQLException {
		SortDemo();
	}

	/**
	 * @param args
	 * @return 
	 */
		private static void SortDemo() {
	        // TODO Auto-generated method stub
	       HashMap<String, Integer> sideMap = new HashMap<String, Integer>();
	       
	       sideMap.put("google", 3000);
	       sideMap.put("baidu", 600);
	       sideMap.put("amazon", 1000);
	       sideMap.put("apple", 5000);
	       
	       ValueComparator bvc = new ValueComparator(sideMap);
	       TreeMap<String, Integer> sortedSideMap = new TreeMap<String, Integer>(bvc);
	       System. out.println("sideMap size " + sideMap.size());
	       sortedSideMap.putAll(sideMap);
	       System. out.println("sortedSideMap :" + sortedSideMap);
	      
	}
}
