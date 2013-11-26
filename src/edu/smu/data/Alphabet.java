package edu.smu.data;

import java.util.*;
import java.io.*;

/**
 * An Alphabet object stores the mapping between symbols (represented by
 * String objects) and integers (represented by Integer objects). It can be
 * used to map feature strings to feature indices, for example, or to map
 * class labels to class indices.
 * <p>
 * A symbol can never be deleted from an Alphabet object once it has been
 * added. Integers are assigned to symbols sequentially, starting from 0. 
 * For example, suppose we have the following code to insert symbols into an 
 * Alphabet object:
 * <p>
 *   Alphabet alpha = new Alphabet();
 *   alpha.addSymbol("a");
 *   alpha.addSymbol("b");
 *   alpha.addSymbol("z");
 *   alpha.addSymbol("a");
 *   alpha.addSymbol("c");
 * <p>
 * Then internally the following mapping is stored:
 * <p>
 *   a -- 0
 *   b -- 1
 *   z -- 2
 *   c -- 3
 */

public class Alphabet {  

  /**
   * Constructs a new Alphabet object with no symbol stored.
   */
  public Alphabet() {
    indices = new HashMap<String, Integer>();
    symbols = new ArrayList<String>();
  }
  
  public Alphabet(String[] symbols) {
	indices = new HashMap<String, Integer>();
	this.symbols = new ArrayList<String>();
	addSymbols(symbols);
  }

  /**
   * Adds a new symbol into the Alphabet object, and returns the integer 
   * assigned to this symbol. If this symbol is already stored in the Alphabet
   * then no new integer is assigned to it and the old integer assigned to it
   * is returned.
   * @param sym A symbol to be added
   * @return The index assigned to the newly added symbol
   */
  public int addSymbol(String sym) {
	if(sym == null){
		return -1;
	}
    if (!indices.containsKey(sym)) {
      indices.put(sym, new Integer(indices.size()));
      symbols.add(sym);
    }
    return indices.get(sym).intValue();
  }

  /**
   * Returns the index associated with the symbol.
   * @param sym A symbol of which the index is to be returned
   * @return The index associated with the given symbol or -1 if the symbol is
   * not stored in the Alphabet
   */
  public int getIndex(String sym) {
    if (indices.containsKey(sym)) {
      return indices.get(sym).intValue();
    }
    return -1;
  }

  /**
   * Returns the symbol at the given index position.
   * @param index The index position at which the symbol is to be returned
   * @return The symbol at the given index position or null if the index is 
   * out of range (index < 0 || index >= size())
   */
  public String getSymbol(int index) {
    if (index >= 0 && index < symbols.size()) {
      return symbols.get(index);
    }
    return null;
  }

  /**
   * Returns the size of the Alphabet.
   * @return The size of this Alphabet object, i.e. the number of symbols 
   * stored in the Alphabet.
   */
  public int size() {
//	  System.out.println("SYS=" + symbols.size());
    return indices.size();
  }
 
  /**
   * Add a array of symbols into current Alphabet
   * @param A array of Strings
   */
  public void addSymbols(String[] symbols){
	  assert(symbols.length > 0 );
	  for(int i = 0; i < symbols.length; i++){
		  addSymbol(symbols[i]);
	  }
  }
  public void display(){
	  Iterator<String> ite = indices.keySet().iterator();
	  while( ite.hasNext() ){
		  String key = ite.next();
		  //if( indices.get(key) > 2000 )
			//  System.out.print(key + " " + indices.get(key));
		  System.out.print(indices.get(key) + " ");
	  }
	  System.out.println("\n" + "[" + symbols.size()+ "]");
	  for(int i = 0; i < symbols.size(); i++){
		  System.out.print( symbols.get(i) + " ");
	  }
  }
  public void saveVocab(String file) throws IOException{
	  BufferedWriter out = new BufferedWriter(
              new FileWriter( new File(file)));
          
	  Iterator<String> ite = indices.keySet().iterator();
	  while(ite.hasNext()){
		  String wrd = ite.next(); 
		  int id = indices.get(wrd);
		  out.write( wrd + " " + id + "\n");
	  }
	  out.flush();
	  out.close();
  }
  private HashMap<String, Integer> indices;
  private ArrayList<String> symbols;

}