package edu.smu.util;

import java.util.*;

public class StringUtil {

  /**
   * Splits the given <code>String</code> into tokens.
   *
   * @param line The <code>String</code> to be tokenized.
   * @param tokens The <code>ArrayList</code> to store the tokens.
   */
  public static void tokenize(String line, ArrayList<String> tokens) {
    StringTokenizer strTok = new StringTokenizer(line);
    while(strTok.hasMoreTokens()) {
      String token = strTok.nextToken();
      tokens.add(token);
    }
  }
}