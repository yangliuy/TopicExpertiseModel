package edu.smu.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class RemoveIllegalChar {
	public static void main() throws IOException{
		
		BufferedReader in = new BufferedReader(  new FileReader(new File("C:\\cygwin\\home\\xzhao\\opinion_mining\\data\\hotel.txt") ));
		BufferedWriter out = new BufferedWriter(  new FileWriter(new File("C:\\cygwin\\home\\xzhao\\opinion_mining\\data\\hotel.good.txt") ));
		
		String line = "";
		
		while( (line=in.readLine()) != null ){
			StringTokenizer st = new StringTokenizer(line);
			while( st.hasMoreTokens() ){
				String word = st.nextToken();
				if( word.indexOf("_") == -1 ){
					continue;
				}
				out.write(word+" ");
			}
			out.write("\n");
		}
	}
}
