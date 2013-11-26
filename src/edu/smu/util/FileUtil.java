package edu.smu.util;

import java.util.*;
import java.io.*;

public class FileUtil {

  public static void readLines(String file, ArrayList<String> lines) {
    BufferedReader reader = null;

    try {
      
      reader = new BufferedReader(new FileReader(new File(file)));

      String line = null;
      while( (line = reader.readLine()) != null ) {
        lines.add(line);
      }

    } catch(FileNotFoundException e) {
      e.printStackTrace();
    } catch(IOException e) {
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch(IOException e) {
          e.printStackTrace();          
        }
      }
    }
    
  }
  
  public static void readLinesBySequence(String file, ArrayList<String> lines) {
	    BufferedReader reader = null;
	    String content;
	    try {
	      
	      reader = new BufferedReader(new FileReader(new File(file)));

	      String line = null;
	      content = "";
	      while( (line = reader.readLine()) != null ) {
	    	 // System.out.println(line);
		     // System.out.println(line.length());
	    	  //System.out.println(line);
	    	 // System.out.println(line.length());

	    	if( line.length() > 0 ){
	    		if( content.length() > 0 )
	    			content += "@" + line;
	    		else
	    			content = line;
	    	} else {
	    		if( content.length() > 0 ){
	    			//System.out.println(content);
	    			lines.add(content);
	    			content = "";
	    		}
	    	}
	      }
	     
	      if( content.length() > 0 ){
	    	  lines.add(content);
	      }

	    } catch(FileNotFoundException e) {
	      e.printStackTrace();
	    } catch(IOException e) {
	      e.printStackTrace();
	    } finally {
	      if (reader != null) {
	        try {
	          reader.close();
	        } catch(IOException e) {
	          e.printStackTrace();          
	        }
	      }
	    }
	    //System.out.println(lines.size());
  }

  public static void writeLines(String file, ArrayList<String> lines) {
    BufferedWriter writer = null;

    try {
      
      writer = new BufferedWriter(new FileWriter(new File(file)));
      
      for(int i = 0; i < lines.size(); i++) {
        writer.write(lines.get(i) + "\n");
      }

    } catch(FileNotFoundException e) {
      e.printStackTrace();
    } catch(IOException e) {
      e.printStackTrace();
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch(IOException e) {
          e.printStackTrace();          
        }
      }
    }
    
  }


}