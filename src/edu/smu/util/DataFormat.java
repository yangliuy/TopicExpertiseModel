package edu.smu.util;

import edu.smu.data.Alphabet;
import edu.smu.data.InstanceList;
import edu.smu.data.Instance;
import edu.smu.data.SparseVector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

public class DataFormat {
	/*public static InstanceList readInstanceListSingle(String file, Alphabet labelSet,Alphabet featSet ){
		InstanceList instList = new InstanceList();
		ArrayList<String> arrayInst = new ArrayList<String>();
		FileUtil.readLines(file, arrayInst);
		System.out.println( arrayInst.size());
		for(int i = 0; i < arrayInst.size(); i++ ){
			Instance inst = stringToInstance(arrayInst.get(i),labelSet, featSet );
			//System.out.println(inst.getFeatureVector().numEntries());
			instList.add(inst);
		}
		return instList;
	}*/
	
	public static InstanceList readInstanceList(String file, Alphabet labelSet,Alphabet featSet ){
		InstanceList instList = new InstanceList();
		ArrayList<String> arrayInst = new ArrayList<String>();
		FileUtil.readLines(file, arrayInst);
		System.out.println( arrayInst.size());
		for(int i = 0; i < arrayInst.size(); i++ ){
            //ClassLoadingMXBean bean = ManagementFactory.getClassLoadingMXBean();  
            //System.out.println(bean.getLoadedClassCount());
			Instance inst = stringToInstance(arrayInst.get(i),labelSet, featSet );
			//System.out.println(inst.getFeatureVector().numEntries());
			instList.add(inst);
			
		}
		return instList;
	}
	public static void extractInstance(ArrayList<String> tokens, int[] ind, double[] values, Alphabet featSet ){
		ind[0] = featSet.addSymbol("x_0_in_logistic_regression#"); 
		values[0] = 1.0;
		for(int i = 1; i < tokens.size(); i++ ){
			if( ! tokens.get(i).contains(":")){
				continue;
			}
			if( tokens.get(i).equals("#") ) {
				break;
			}
			String[] str = tokens.get(i).split(":");
			ind[i] = featSet.addSymbol(str[0]);
			values[i] = new Double(str[1]);
		}
	}
	public static Instance stringToInstance(String line, Alphabet labelSet, Alphabet featSet ){
		assert(line != null && line.length() > 0 && labelSet.size() > 0);
		ArrayList<String> tokens = new ArrayList<String>();
		StringUtil.tokenize(line, tokens);
		
		int label = labelSet.addSymbol(tokens.get(0));
		int[] ind = new int[tokens.size()];//adding the bias
		double[] values = new double[tokens.size()];//adding the bias
		extractInstance(tokens, ind, values, featSet);
		SparseVector feaVec = new SparseVector(ind, values);
		Instance inst = new Instance(feaVec, label);
		return inst;
	}

	public static int[][][] getWordsFromFile( String file, Alphabet vocab, Alphabet stopwords, Alphabet timeVocab, int limit) throws IOException{
		
		vocab.addSymbol("@@@@@@@@");
		
		BufferedReader in = new BufferedReader(new FileReader(new File(file)));
		String line;
		Vector<Vector<Vector<Integer> > > docs = new Vector<Vector<Vector<Integer> > >();
		Vector<Vector<Integer> > sens = new Vector<Vector<Integer> >();
		
		while( (line=in.readLine()) != null ){
			//System.out.println(line);
			if(line.contains("DOC-")){
				//System.out.println(line);
				//if( sens.size() > 1 && sens.size() < 50){
					//System.out.println(sens.size());
				if(sens.size() > 0)
					docs.add(sens);
				if( limit > 0 && docs.size() > limit ){
					break;
				}
				//}
				 sens = new Vector<Vector<Integer> >();
			} else {
				//System.out.println(line);
				StringTokenizer st = new StringTokenizer(line);
				Vector<Integer> words = new Vector<Integer>();
				
				String wrd = st.nextToken().substring(0,8);
				int wid = timeVocab.addSymbol(wrd.trim());
				words.add(wid);
				
				while( st.hasMoreTokens() ){
					wrd = st.nextToken();
					//System.out.println(wrd);
					int end = wrd.lastIndexOf("_");
					if(end != -1){
						wrd = wrd.substring(0,end);
					}
					if( stopwords.getIndex(wrd) != -1){
						continue;
					}
					//System.out.println(wrd);
					//int wid = vocab.getIndex(wrd);
					wid = vocab.addSymbol(wrd);
					if( wid == -1 ){
						//words.add(vocab.getIndex("@@@@@@@@"));
						System.out.println("@@@");
					} else {
						words.add(wid);
					}
				}
				if( words.size() > 1 && words.size() < 50 ){
					sens.add(words);
					/*for(int i = 0; i < words.size(); i++ ){
						System.out.print(" " + vocab.getSymbol(words.get(i)) + " ");
					}
					System.out.println();*/
				}
			}
		}
		int[][][] w = new int[docs.size()][][];
		for(int i = 0; i < docs.size(); i++ ){
			w[i] = new int[(docs.get(i)).size()][];
			for(int j = 0; j < docs.get(i).size(); j++){
				/**
				 * 
				 */
				w[i][j] = new int[docs.get(i).get(j).size()];
				//System.out.println(i + " " + j );
				for(int k = 0; k <docs.get(i).get(j).size(); k++){
					w[i][j][k] = docs.get(i).get(j).get(k);
					//System.out.print(" " + vocab.getSymbol(docs.get(i).get(j).get(k)) + " ");
				}
				//System.out.println();
			}
			//System.out.println("=============");
		}
		
		return w;
	}
	
	public static int[][][] getWordsFromFile( String file, Alphabet vocab, int limit ) throws IOException{
		
		vocab.addSymbol("@@@@@@@@");
		
		BufferedReader in = new BufferedReader(new FileReader(new File(file)));
		String line;
		Vector<Vector<Vector<Integer> > > docs = new Vector<Vector<Vector<Integer> > >();
		Vector<Vector<Integer> > sens = new Vector<Vector<Integer> >();
		
		while( (line=in.readLine()) != null ){
			if(line.contains("DOC-")){
				//System.out.println(line);
				//if( sens.size() > 1 && sens.size() < 50){
					//System.out.println(sens.size());
				if(sens.size() > 0)
					docs.add(sens);
				if( limit > 0 && docs.size() > limit ){
					break;
				}
				//}
				 sens = new Vector<Vector<Integer> >();
			} else {
				//System.out.println(line);
				StringTokenizer st = new StringTokenizer(line);
				Vector<Integer> words = new Vector<Integer>();
				while( st.hasMoreTokens() ){
					String wrd = st.nextToken();
					wrd = wrd.substring(0,wrd.lastIndexOf("_"));
					//System.out.println(wrd);
					//int wid = vocab.getIndex(wrd);
					int wid = vocab.addSymbol(wrd);
					if( wid == -1 ){
						//words.add(vocab.getIndex("@@@@@@@@"));
						System.out.println("@@@");
					} else {
						words.add(wid);
					}
				}
				if( words.size() > 5 && words.size() < 50 ){
					sens.add(words);
					/*for(int i = 0; i < words.size(); i++ ){
						System.out.print(" " + vocab.getSymbol(words.get(i)) + " ");
					}
					System.out.println();*/
				}
			}
		}
		int[][][] w = new int[docs.size()][][];
		for(int i = 0; i < docs.size(); i++ ){
			w[i] = new int[(docs.get(i)).size()][];
			for(int j = 0; j < docs.get(i).size(); j++){
				w[i][j] = new int[docs.get(i).get(j).size()];
				//System.out.println(i + " " + j );
				for(int k = 0; k <docs.get(i).get(j).size(); k++){
					w[i][j][k] = docs.get(i).get(j).get(k);
					//System.out.print(" " + vocab.getSymbol(docs.get(i).get(j).get(k)) + " ");
				}
				//System.out.println();
			}
			//System.out.println("=============");
		}
		return w;
	}

	
	public static int[][][] getWordsFromFile( String file, Alphabet vocab, Alphabet stopwords, int limit) throws IOException{
		//For AT Model
		vocab.addSymbol("@@@@@@@@");
		
		BufferedReader in = new BufferedReader(new FileReader(new File(file)));
		String line, orgline="";
		Vector<Vector<Vector<Integer> > > docs = new Vector<Vector<Vector<Integer> > >();
		Vector<Vector<Integer> > sens = new Vector<Vector<Integer> >();
		String pattern = "[^A-Z a-z]"; //need pattern to clean the line 7/3/13
                
		while( (line=in.readLine()) != null ){
			//System.out.println(line);
                    orgline=line;
			if(line.contains("DOC-")){
				//System.out.println(line);
				//if( sens.size() > 1 && sens.size() < 50){
					//System.out.println(sens.size());
				if(sens.size() > 0) //Swapna
					docs.add(sens);
				if( limit > 0 && docs.size() > limit ){
					break;
				}
				//}
				 sens = new Vector<Vector<Integer> >();
			} else {
				//System.out.println(line);
                                line=line.replaceAll(pattern," ");
                                line=line.toLowerCase();
				StringTokenizer st = new StringTokenizer(line);
				Vector<Integer> words = new Vector<Integer>();
				while( st.hasMoreTokens() ){
					String wrd = st.nextToken();
					//System.out.println(wrd);
					int end = wrd.lastIndexOf("_");
					if(end != -1){
						wrd = wrd.substring(0,end);
					}
					if( stopwords.getIndex(wrd) != -1){
						continue;
					}
					//System.out.println(wrd);
					//int wid = vocab.getIndex(wrd);
					int wid = vocab.addSymbol(wrd);
					if( wid == -1 ){
						//words.add(vocab.getIndex("@@@@@@@@"));
						System.out.println("@@@");
					} else {
						words.add(wid);
					}
				}
				//if( words.size() > 1 && words.size() < 50 ){
                                if( words.size() >0 ){ //Swapna we dont restrict
					sens.add(words);
					/*for(int i = 0; i < words.size(); i++ ){
						System.out.print(" " + vocab.getSymbol(words.get(i)) + " ");
					}
					System.out.println();*/
				}
                                else {
                                    int wid = vocab.addSymbol("invalid");
                                    if(wid==-1) wid=vocab.getIndex("invalid");
                                    words.add(wid);
                                    sens.add(words);
                                    //System.out.print(orgline);
                                }
			}
		}
                if(sens.size() > 0) //Swapna
                    docs.add(sens);
		int[][][] w = new int[docs.size()][][];
		for(int i = 0; i < docs.size(); i++ ){
			w[i] = new int[(docs.get(i)).size()][];
			for(int j = 0; j < docs.get(i).size(); j++){
				/**
				 * 
				 */
				w[i][j] = new int[docs.get(i).get(j).size()];
				//System.out.println(i + " " + j );
				for(int k = 0; k <docs.get(i).get(j).size(); k++){
					w[i][j][k] = docs.get(i).get(j).get(k);
					//System.out.print(" " + vocab.getSymbol(docs.get(i).get(j).get(k)) + " ");
				}
				//System.out.println();
			}
			//System.out.println("=============");
		}
		
		return w;
	}
	public static int[][][][] getFeatVecsFromFile(String file, Alphabet featSet, int limit) throws IOException{
		/*featSet.addSymbol("NULL_x[0,2]");
		featSet.addSymbol("0_x[0,2]");
		featSet.addSymbol("1_x[0,2]");
		featSet.addSymbol("2_x[0,2]");*/
		
		BufferedReader in = new BufferedReader(new FileReader(new File(file)));
		String line;
		Vector<Vector<Vector<Vector<Integer> > > >docs = new Vector<Vector<Vector<Vector<Integer> > > >();
		Vector<Vector<Vector<Integer> > > sens = new Vector< Vector<Vector<Integer> > >();
		
		while( (line=in.readLine()) != null ){
			if(line.contains("DOC-")){
				//System.out.println(line);
				//if( sens.size() > 1 && sens.size() < 50){
				if(sens.size() > 0)
					docs.add(sens);
				if( limit > 0 && docs.size() > limit ){
					break;
				}
				//}
				sens = new Vector< Vector<Vector<Integer> > >();
			} else {
				StringTokenizer st = new StringTokenizer(line);
				
				Vector<String> posv = new Vector<String>();
				Vector<String> wordv = new Vector<String>();
				
				while( st.hasMoreTokens() ){
					String pos = st.nextToken();
					String wrd = pos.substring(0,pos.lastIndexOf("_"));
					
					wordv.add(wrd);
					pos = pos.substring(pos.lastIndexOf("_")+1, pos.length());
					//System.out.println(wrd+"@@@@@@"+pos);
					posv.add(pos);
				}
				
				if( posv.size() > 5 && posv.size() < 50 ){
					Vector<Vector<Integer> > words = new Vector<Vector<Integer> >();
					for(int i = 0; i < posv.size(); i++ ){
						Vector<Integer> pvi = new Vector<Integer>();
						pvi.add( featSet.addSymbol("ME_BIAS"));
						String feat = "";
						
						int wid;
						//pos-1
						if( i > 0 ){
							feat = posv.get(i-1)+"_x[-1,1]";
							wid = featSet.addSymbol(feat);
							if( wid != -1 ){
								pvi.add(wid);
							}
						}
						//pos
						feat = posv.get(i)+"_x[0,1]";
						wid = featSet.addSymbol(feat);
						if( wid != -1 ){
							pvi.add(wid);
						} 
						//pos+1
						if( i < posv.size()-1 ){
							feat = posv.get(i+1)+"_x[1,1]";
							wid = featSet.addSymbol(feat);
							if( wid != -1 ){
								pvi.add(wid);
							} 
						}
						//w-1
						if( i > 0 ){
							feat = wordv.get(i-1)+"_x[-1,1]";
							wid = featSet.addSymbol(feat);
							if( wid != -1 ){
								pvi.add(wid);
							}
						}
						//w
						feat = wordv.get(i)+"_x[0,1]";
						wid = featSet.addSymbol(feat);
						if( wid != -1 ){
							pvi.add(wid);
						} 
						//w+1
						if( i < posv.size()-1 ){
							feat = wordv.get(i+1)+"_x[1,1]";
							wid = featSet.addSymbol(feat);
							if( wid != -1 ){
								pvi.add(wid);
							} 
						}
						words.add(pvi);
					}
					sens.add(words);
				}
			}
		}
		int[][][][] w = new int[docs.size()][][][];
		for(int i = 0; i < docs.size(); i++ ){
			w[i] = new int[docs.get(i).size()][][];
			for(int j = 0; j < docs.get(i).size(); j++){
				w[i][j] = new int[docs.get(i).get(j).size()][];
				for(int k = 0; k <docs.get(i).get(j).size(); k++){
					w[i][j][k] = new int[docs.get(i).get(j).get(k).size()];//+1];
					for(int s = 0; s < docs.get(i).get(j).get(k).size()-1; s++ ){
						w[i][j][k][s] = ((int)(docs.get(i).get(j).get(k).get(s)));
						//System.out.print(" " + featSet.getSymbol(docs.get(i).get(j).get(k).get(s)) + " ");
					}
					//System.out.println();
				}
				//System.out.println("-----------------------");
				//break;
			}
			//System.out.println("========================");
			//break;
		}
		return w;
	}
	
	public static int[][][][] getFeatVecsFromFile(String file, Alphabet featSet, Alphabet stopwords, int limit) throws IOException{
		/*featSet.addSymbol("NULL_x[0,2]");
		featSet.addSymbol("0_x[0,2]");
		featSet.addSymbol("1_x[0,2]");
		featSet.addSymbol("2_x[0,2]");*/
		
		BufferedReader in = new BufferedReader(new FileReader(new File(file)));
		String line;
		Vector<Vector<Vector<Vector<Integer> > > >docs = new Vector<Vector<Vector<Vector<Integer> > > >();
		Vector<Vector<Vector<Integer> > > sens = new Vector< Vector<Vector<Integer> > >();
		
		while( (line=in.readLine()) != null ){
			if(line.contains("DOC-")){
				//System.out.println(line);
				//if( sens.size() > 1 && sens.size() < 50){
				if(sens.size() > 0)
					docs.add(sens);
				if( limit > 0 && docs.size() > limit ){
					break;
				}
				//}
				sens = new Vector< Vector<Vector<Integer> > >();
			} else {
				StringTokenizer st = new StringTokenizer(line);
				
				Vector<String> posv = new Vector<String>();
				Vector<String> wordv = new Vector<String>();
				
				Vector<String> reservePOS = new Vector<String>();
				Vector<String> reserveWord = new Vector<String>();
				
				Vector<Integer> idx = new Vector<Integer>();
				
				int from = 0;
				
				while( st.hasMoreTokens() ){
					String pos = st.nextToken();
					String wrd = pos.substring(0,pos.lastIndexOf("_"));
					pos = pos.substring(pos.lastIndexOf("_")+1, pos.length());
					/*
					 * 
					 */
					reservePOS.add(pos);
					reserveWord.add(wrd);
					from++;
					
					if( stopwords.getIndex(wrd) != -1){
						continue;
					}
					idx.add(from);
					wordv.add(wrd);
					//System.out.println(wrd+"@@@@@@"+pos);
					posv.add(pos);
				}
				
				
				int trueIdx = 0;
				//if( posv.size() > 1 && posv.size() < 50 ){
                                if( posv.size() > 1){ //Swapna
					Vector<Vector<Integer> > words = new Vector<Vector<Integer> >();
					for(int i = 0; i < posv.size(); i++ ){
						
						trueIdx = idx.get(i)-1;
						
						Vector<Integer> pvi = new Vector<Integer>();
						pvi.add( featSet.addSymbol("ME_BIAS"));
						String feat = "";
						
						int wid;
						
						//pos-1
						if( trueIdx > 0 ){
							feat = reservePOS.get(trueIdx-1)+"_x[-1,1]";
							wid = featSet.addSymbol(feat);
							if( wid != -1 ){
								pvi.add(wid);
							}
						}
						//pos
						feat = reservePOS.get(trueIdx)+"_x[0,1]";
						wid = featSet.addSymbol(feat);
						if( wid != -1 ){
							pvi.add(wid);
						} 
						//pos+1
						if( trueIdx < reservePOS.size()-1 ){
							feat = reservePOS.get(trueIdx+1)+"_x[1,1]";
							wid = featSet.addSymbol(feat);
							if( wid != -1 ){
								pvi.add(wid);
							} 
						}
						
						//w-1
						if( trueIdx > 0 ){
							feat = reserveWord.get(trueIdx-1)+"_x[-1,1]";
							wid = featSet.addSymbol(feat);
							if( wid != -1 ){
								pvi.add(wid);
							}
						}
						//w
						feat = reserveWord.get(trueIdx)+"_x[0,1]";
						wid = featSet.addSymbol(feat);
						if( wid != -1 ){
							pvi.add(wid);
						} 
						//w+1
						if( trueIdx < reserveWord.size()-1 ){
							feat = reserveWord.get(trueIdx+1)+"_x[1,1]";
							wid = featSet.addSymbol(feat);
							if( wid != -1 ){
								pvi.add(wid);
							} 
						}
						words.add(pvi);
					}
					sens.add(words);
				}
			}
		}
		int[][][][] w = new int[docs.size()][][][];
		for(int i = 0; i < docs.size(); i++ ){
			w[i] = new int[docs.get(i).size()][][];
			for(int j = 0; j < docs.get(i).size(); j++){
				w[i][j] = new int[docs.get(i).get(j).size()][];
				for(int k = 0; k <docs.get(i).get(j).size(); k++){
					w[i][j][k] = new int[docs.get(i).get(j).get(k).size()];//+1];
					for(int s = 0; s < docs.get(i).get(j).get(k).size()-1; s++ ){
						w[i][j][k][s] = ((int)(docs.get(i).get(j).get(k).get(s)));
						//System.out.print(" " + featSet.getSymbol(docs.get(i).get(j).get(k).get(s)) + " ");
					}
					//System.out.println();
				}
				//System.out.println("-----------------------");
				//break;
			}
			//System.out.println("========================");
			//break;
		}
		return w;
	}
	
	public static Alphabet loadAlphabet(String vocabFile) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(new File(vocabFile)));
		Alphabet vocab = new Alphabet();
		String line;
		while( (line=in.readLine()) != null ){
			vocab.addSymbol(line.trim());
		}
		return vocab;
	}
	
	public static InstanceList loadInstanceList(int[][][][] w, Alphabet labelSet, Alphabet featSet){
		InstanceList instList = new InstanceList();
		for(int i = 0; i < w.length; i++ ){
			for(int j = 0; j < w[i].length; j++ ){
				for(int k = 0; k < w[i][j].length; k++ ){
					int[] ind = new int[w[i][j][k].length];
					double[] values = new double[w[i][j][k].length];
					for(int s = 0; s < ind.length; s++ ){
						ind[s] = (int)(w[i][j][k][s]);
						values[s] = 1.0;
					}
					Instance inst = new Instance(new SparseVector(ind, values), -1 );
					instList.add(inst);
				}
			}
		}
		return instList;
	}
	
	public static void check(int[][][][] w1, int[][][] w2){
		assert(w1.length == w2.length);
		int cnt1 = 0;
		for(int i = 0; i < w1.length; i++){
			for(int j = 0; j < w1[i].length; j++){
				cnt1 += w1[i][j].length;
			}
		}
		
		int cnt2 = 0;
		for(int i = 0; i < w2.length; i++){
			for(int j = 0; j < w2[i].length; j++){
				cnt2 += w2[i][j].length;
			}
		}
		assert( cnt1 == cnt2 );
		//System.out.println(cnt1);
	}
        public static int[][][] getWordsFromFile( String file, Alphabet vocab, Alphabet stopwords, int limit, String ET) throws IOException{
		
		//vocab.addSymbol("@@@@@@@@");
		
		BufferedReader in = new BufferedReader(new FileReader(new File(file)));
		String line;
		Vector<Vector<Vector<Integer> > > docs = new Vector<Vector<Vector<Integer> > >();
		Vector<Vector<Integer> > sens = new Vector<Vector<Integer> >();
		String prevline="";
		while( (line=in.readLine()) != null ){
			//System.out.println(line);
			//if(line.contains("DOC-")){
                        if(line.contains("DOC-")){
				//System.out.println(line);
				//if( sens.size() > 1 && sens.size() < 50){
					//System.out.println(sens.size());
				if(sens.size() > 0)
					docs.add(sens);
				if( limit > 0 && docs.size() > limit ){
					break;
				}
				//}
				 sens = new Vector<Vector<Integer> >();
			} else {
				//System.out.println(line);
                                
				StringTokenizer st = new StringTokenizer(line);
				Vector<Integer> words = new Vector<Integer>();
                                if(st.countTokens()==0) System.out.println(prevline);
				while( st.hasMoreTokens() ){
					String wrd = st.nextToken();
					int wid = vocab.addSymbol(wrd);
					if( wid == -1 ){
						//words.add(vocab.getIndex("@@@@@@@@"));
						System.out.println("@@@");
					} else {
						words.add(wid);
					}
				}
				//if( words.size() > 1 && words.size() < 50 ){
                                if( words.size() >0 ){ //Swapna we dont restrict
					sens.add(words);
					/*for(int i = 0; i < words.size(); i++ ){
						System.out.print(" " + vocab.getSymbol(words.get(i)) + " ");
					}
					System.out.println();*/
				}
			}
                        prevline=line;
		}
                if(sens.size() > 0) //Swapna
			docs.add(sens);
		int[][][] w = new int[docs.size()][][];
		for(int i = 0; i < docs.size(); i++ ){
			w[i] = new int[(docs.get(i)).size()][];
			for(int j = 0; j < docs.get(i).size(); j++){
				/**
				 * 
				 */
				w[i][j] = new int[docs.get(i).get(j).size()];
				//System.out.println(i + " " + j );
				for(int k = 0; k <docs.get(i).get(j).size(); k++){
					w[i][j][k] = docs.get(i).get(j).get(k);
					//System.out.print(" " + vocab.getSymbol(docs.get(i).get(j).get(k)) + " ");
				}
				//System.out.println();
			}
			//System.out.println("=============");
		}
		
		return w;
	}
        public static int[][] getWordsFromFile( String file, Alphabet vocab, Alphabet stopwords, int limit, int ET) throws IOException{
            //This one ignores the sentences
		//vocab.addSymbol("@@@@@@@@");
		
		BufferedReader in = new BufferedReader(new FileReader(new File(file)));
		String line;
		Vector<Vector<Integer> >  docs = new Vector<Vector<Integer> >();
		Vector<Vector<Integer> > sens = new Vector<Vector<Integer> >();
		String prevline="";
		while( (line=in.readLine()) != null ){
			//System.out.println(line);
			if(line.contains("DOC-")){
				//System.out.println(line);
				//if( sens.size() > 1 && sens.size() < 50){
					//System.out.println(sens.size());
				//S/if(sens.size() > 0)
				//S/	docs.add(sens);
				if( limit > 0 && docs.size() > limit ){
					break;
				}
				//}
				 sens = new Vector<Vector<Integer> >();
			} else {
				//System.out.println(line);
                                
				StringTokenizer st = new StringTokenizer(line);
				Vector<Integer> words = new Vector<Integer>();
                                if(st.countTokens()==0) System.out.println(prevline);
				while( st.hasMoreTokens() ){
					String wrd = st.nextToken();
					int wid = vocab.addSymbol(wrd);
					if( wid == -1 ){
						//words.add(vocab.getIndex("@@@@@@@@"));
						System.out.println("@@@");
					} else {
						words.add(wid);
					}
				}
				//if( words.size() > 1 && words.size() < 50 ){
                                if( words.size() >0 ){ //Swapna we dont restrict
					//sens.add(words);
                                    docs.add(words);
					/*for(int i = 0; i < words.size(); i++ ){
						System.out.print(" " + vocab.getSymbol(words.get(i)) + " ");
					}
					System.out.println();*/
				}
			}
                        prevline=line;
		}
		int[][] w = new int[docs.size()][];
		for(int i = 0; i < docs.size(); i++ ){
			w[i] = new int[(docs.get(i)).size()];
			for(int j = 0; j < docs.get(i).size(); j++){
				/**
				 * 
				 */
				//w[i][j] = new int[docs.get(i).get(j).size()];
				//System.out.println(i + " " + j );
				//for(int k = 0; k <docs.get(i).get(j).size(); k++){
					w[i][j] = docs.get(i).get(j);
					//System.out.print(" " + vocab.getSymbol(docs.get(i).get(j).get(k)) + " ");
				//}
				//System.out.println();
			}
			//System.out.println("=============");
		}
		
		return w;
	}
        public static int[][][] getWordsFromFileFormat2(String file, Alphabet vocab,
			int limit) throws IOException {

		vocab.addSymbol("@@@@@@@@");

		BufferedReader in = new BufferedReader(new FileReader(new File(file)));
		String line;
		Vector<Vector<Vector<Integer>>> docs = new Vector<Vector<Vector<Integer>>>();
		Vector<Vector<Integer>> sens = new Vector<Vector<Integer>>();

		while ((line = in.readLine()) != null) {
			// System.out.println(line);
			if (line.contains("DOC-")) {
				if (limit > 0 && docs.size() > limit) {
					break;
				}
//				 System.out.println(line);
				// if( sens.size() > 1 && sens.size() < 50){
				// System.out.println(sens.size());
				if (sens.size() > 0)
					docs.add(sens);
				// }
				sens = new Vector<Vector<Integer>>();
			} else {
				// System.out.println(line);
				StringTokenizer st = new StringTokenizer(line);
				Vector<Integer> words = new Vector<Integer>();
				while (st.hasMoreTokens()) {
					String wrd = st.nextToken().trim();
					// System.out.println(wrd);
					// int wid = vocab.getIndex(wrd);
					int wid = vocab.addSymbol(wrd);
					if (wid == -1) {
						// words.add(vocab.getIndex("@@@@@@@@"));
						System.out.println("@@@");
					} else {
						words.add(wid);
					}
				}
//				if (words.size() > 1 && words.size() < 50) {
				sens.add(words);
				/*
				 * for(int i = 0; i < words.size(); i++ ){
				 * System.out.print(" " + vocab.getSymbol(words.get(i)) +
				 * " "); } System.out.println();
				 */
			}
		}
		// add to docs
		if (sens.size() > 0)
			docs.add(sens);

		// process docs
		int[][][] w = new int[docs.size()][][];
		for (int i = 0; i < docs.size(); i++) {
			w[i] = new int[(docs.get(i)).size()][];
			for (int j = 0; j < docs.get(i).size(); j++) {
				/**
				 * 
				 */
				w[i][j] = new int[docs.get(i).get(j).size()];
				// System.out.println(i + " " + j );
				for (int k = 0; k < docs.get(i).get(j).size(); k++) {
					w[i][j][k] = docs.get(i).get(j).get(k);
//					 System.out.print(" " +
//					 vocab.getSymbol(docs.get(i).get(j).get(k)) + " ");
				}
//				 System.out.println();
			}
			// System.out.println("=============");
		}

		return w;
	}
        public static int[][][][][] getWordsFromFile( String file, String efile, Alphabet vocab, Alphabet stopwords, int limit) throws IOException{
		//this is for the idelogy topic opinion model
		vocab.addSymbol("@@@@@@@@");
                String pattern = "[^A-Z a-z_.-]";
                
		BufferedReader in = new BufferedReader(new FileReader(new File(file)));
		String line;
		Vector<Vector<Vector<Vector<Vector<Integer> > > > > docs = new Vector<Vector<Vector<Vector<Vector<Integer> > > > >();
                Vector<Vector<Vector<Vector<Integer> > > >  ques = new Vector<Vector<Vector<Vector<Integer> > > > ();
                Vector<Vector<Vector<Integer> > >   sides = new Vector<Vector<Vector<Integer> > >  ();
		Vector<Vector<Integer> > args = new Vector<Vector<Integer> >();
		boolean ndid=false, nqid=false;
                String author="", did="", side="", debTitle="";
                int debcnt=0;
                HashMap authList=new HashMap();
		while( (line=in.readLine()) != null ){
			//System.out.println(line);
                        author="";
                        String orgLine=line;
                        if(line.startsWith("ARG")) {
                            author=line.substring(0,line.indexOf(":")); //to cater the args that contains that authors names
                            if(author.contains(",")) {
                                author=author.substring(author.indexOf(",")+1);
                                debTitle=author.substring(author.indexOf(",")+1, author.lastIndexOf(","));
                                author=author.substring(0,author.indexOf(","));
                            }
                            line=line.substring(line.indexOf(":")); //to cater the args that contains that authors names
                            if(author.length()>0) {
                                line= author +" "+ line;
                                //authList.put(did+":"+side+":"+aid, file)
                            }
                        //line=line.replace("days ago", "");line=line.replace("??", "");
                        line="ARG:"+ line;
                        }
                        line=line.replace("'s", "").replace(":", " ");
                        String tempLine=line.replace("._", "||");
                        line=tempLine.replace(".", " ").replace("[", " ").replace("]", " ").replace("(", " ").replace(")"," ");
                        line=line.replace("||", "._");
//                        
                        while(line.contains("--")){
                            line=line.replace("--", " ");
                        }
                        line=line.replace(",", " ");
                        line=line.replace("?", " ");
                        line=line.replace("\"", " ");
                        while(line.contains("  ")){
                            line=line.replace("  ", " ");
                        }
                        line=line.replaceAll(pattern,"");
                        
                        if(line.startsWith("DID")){
                                ndid=true;
				//System.out.println(line);
				//if( sens.size() > 1 && sens.size() < 50){
				//System.out.println("Ques:" + ques.size());
                                if(args.size() > 0) 	sides.add(args);
                                if(sides.size() > 0) 	ques.add(sides);
				if(ques.size() > 0)	docs.add(ques); 
                                
                                if( limit > 0 && docs.size() > limit ){
					break;
				}
				ques = new Vector<Vector<Vector<Vector<Integer> > > >();
				 //sens = new Vector<Vector<Integer> >();
			}
                        else if(line.startsWith("QID")){
                                //if( sens.size() > 1 && sens.size() < 50){
				//System.out.println("Sides:" + sides.size());
                                
				if(!ndid & !nqid & args.size() > 0) 	sides.add(args);
                                if(!ndid & sides.size() > 0) 	ques.add(sides);
                                
                                
				if( limit > 0 && ques.size() > limit ){
					break;
				}
				sides = new Vector<Vector<Vector<Integer> > > ();
				 //sens = new Vector<Vector<Integer> >();
                                nqid=true;
			}
                        else if(line.startsWith("SID")){
                                //System.out.println(line);
				//if( sens.size() > 1 && sens.size() < 50){
                                if(!nqid & args.size() > 0) 	sides.add(args);
				//if(!ndid & args.size() > 0) 	sides.add(args);
                                
                                //System.out.println("Args:" + args.size());
				if( limit > 0 && args.size() > limit ){
					break;
				}
				args = new Vector<Vector<Integer> >  ();
				 //sens = new Vector<Vector<Integer> >();
                                nqid=false;
			}
                        else if(line.startsWith("ARG")){
				//System.out.println(line);
                                line=line.replace("ARG", "");
                                if(line.length()<10) line="this comment is invalid";
                                line=line.toLowerCase();
                                //line=line.substring(line.indexOf(":")+1);
                                //line=line.toLowerCase();
                                StringTokenizer st = new StringTokenizer(line);
				Vector<Integer> words = new Vector<Integer>();
				while( st.hasMoreTokens() ){
					String wrd = st.nextToken().trim();
                                        //wrd=wrd.replace("ARG", "");
                                        if(wrd.endsWith("."))
                                            wrd=wrd.substring(0,wrd.lastIndexOf("."));
                                        if(wrd.startsWith("-") || wrd.endsWith("-")){
                                            wrd=wrd.replace("-", " ");
                                        }
                                        wrd=wrd.trim();
					//System.out.println(wrd);
					if( wrd.trim().isEmpty() || stopwords.getIndex(wrd) != -1 || wrd.trim().length()>50){
                                        //if( wrd.trim().isEmpty()){
						continue;
					}
					//System.out.println(wrd);
					//int wid = vocab.getIndex(wrd);
					int wid = vocab.addSymbol(wrd);
					if( wid == -1 ){
						//words.add(vocab.getIndex("@@@@@@@@"));
						System.out.println("@@@");
					} else {
						words.add(wid);
					}
				}
				//if( words.size() > 1 && words.size() < 50 ){
                                if( words.size() ==0 ) System.out.println(orgLine);
                                if( words.size() >0 ){ //Swapna we dont restrict
					args.add(words);
                                        //System.out.println("DOC:" + debcnt++ +":" +debTitle + "\n"+ author);
                                        //System.out.println("DOC:" + debcnt++ +":" +debTitle + "|"+ orgLine);
					/*for(int i = 0; i < words.size(); i++ ){
						System.out.print(" " + vocab.getSymbol(words.get(i)) + " ");
					}
					System.out.println();*/
				}
                                ndid=false;
			}//args
                }
                // add to docs
                if(args.size() > 0) 	sides.add(args);
		if(sides.size() > 0) 	ques.add(sides);
                if(ques.size() > 0)	docs.add(ques);
                
                
		int[][][][][] w = new int[docs.size()][][][][];
		for(int i = 0; i < docs.size(); i++ ){
                    w[i] = new int[(docs.get(i)).size()][][][];
                    for(int j = 0; j < docs.get(i).size(); j++){
                        //System.out.println(i + " " + j);
                        w[i][j] = new int[docs.get(i).get(j).size()][][];
                        for(int k = 0; k <docs.get(i).get(j).size(); k++){
                            w[i][j][k] = new int[docs.get(i).get(j).get(k).size()][];
                            //System.out.println(i + " " + j + " "+k );
                            for(int l = 0; l<docs.get(i).get(j).get(k).size(); l++){
                                w[i][j][k][l] = new int[docs.get(i).get(j).get(k).get(l).size()];
                                for(int m = 0; m<docs.get(i).get(j).get(k).get(l).size(); m++){
                                    w[i][j][k][l][m] = docs.get(i).get(j).get(k).get(l).get(m);
                                //System.out.print(" " + vocab.getSymbol(docs.get(i).get(j).get(k).get(l).get(m)) + " ");
                                }//m - word
                                //System.out.println();
                            }//l
                        } //k
                    }//j		
                    
                } //i
			//System.out.println("=============");
		return w;
	}
        
        public static int[][] getWordsFromFileNS( String file, Alphabet vocab, Alphabet stopwords, int limit) throws IOException{
		//This version return the dcocs and its words. No sentences. Used for the inference of the ITO model
		vocab.addSymbol("@@@@@@@@");
		String pattern = "[^A-Z a-z_.-]";
                
		BufferedReader in = new BufferedReader(new FileReader(new File(file)));
		String line, content="", orgline="", docno="";
		Vector<Vector<Integer> > docs = new Vector<Vector<Integer> >();
		Vector<Integer> words = new Vector<Integer>();
                
		while( (line=in.readLine()) != null ){
                        orgline=line;
                        line=line.replace("'s", "").replace(":", " ");
                        String tempLine=line.replace("._", "||");
                        line=tempLine.replace(".", " ").replace("[", " ").replace("]", " ").replace("(", " ").replace(")"," ");
                        line=line.replace("||", "._");
//                        
                        while(line.contains("--")){
                            line=line.replace("--", " ");
                        }
                        line=line.replace(",", " ");
                        line=line.replace("?", " ");
                        line=line.replace("\"", " ");
                        while(line.contains("  ")){
                            line=line.replace("  ", " ");
                        }
                        line=line.replaceAll(pattern,"");
                        
			//System.out.println(line);
			if(line.startsWith("DOC")){
				//System.out.println(orgline);
				if(words.size() > 0){ //Swapna
                                //if(words.size() > 0 && content.length()>4) { //Swapna
					docs.add(words);
                                        System.out.println(docno);
                                }
                                //else
                                //    System.out.println(orgline);
				if( limit > 0 && docs.size() > limit ){
					break;
				}
				//}
				docno=orgline;
                                words = new Vector<Integer>();
			} else {
				//System.out.println(line);
                                line=line.toLowerCase().trim();
                                if(line.length()<4) line="";
                                content=line;
                                
				StringTokenizer st = new StringTokenizer(line);
				
				while( st.hasMoreTokens() ){
					String wrd = st.nextToken();
					//System.out.println(wrd);
					if(wrd.endsWith("."))
                                            wrd=wrd.substring(0,wrd.lastIndexOf("."));
                                        if(wrd.startsWith("-") || wrd.endsWith("-")){
                                            wrd=wrd.replace("-", " ");
                                        }
					if( wrd.trim().isEmpty() || stopwords.getIndex(wrd) != -1){
                                        //if( wrd.trim().isEmpty()){
						continue;
					}
					//System.out.println(wrd);
					int wid = vocab.getIndex(wrd);
					//int wid = vocab.addSymbol(wrd);
					if( wid > 0 ){
						words.add(wid);
					}
				}
			}
		}
                System.out.println(docs.size());
		int[][] w = new int[docs.size()][];
		for(int i = 0; i < docs.size(); i++ ){
                    w[i] = new int[(docs.get(i)).size()];
                    for(int j = 0; j < docs.get(i).size(); j++){
                        w[i][j] = docs.get(i).get(j);
                    }
                    //System.out.println("=============");
		}
		return w;
	}
        
        public static void getWordsFromFileLDA( String file, Alphabet vocab, Alphabet stopwords) throws IOException{
		//This version return the dcocs and its words. No sentences. Used for the inference of the ITO model
		vocab.addSymbol("@@@@@@@@");
		String pattern = "[^A-Z a-z_.-]";
                
		BufferedReader in = new BufferedReader(new FileReader(new File(file)));
		String line;
		//Vector<Vector<Integer> > docs = new Vector<Vector<Integer> >();
		Vector<Integer> words = new Vector<Integer>();
		while( (line=in.readLine()) != null ){
                        line=line.replace("'s", "").replace(":", " ");
                        String tempLine=line.replace("._", "||");
                        line=tempLine.replace(".", " ").replace("[", " ").replace("]", " ").replace("(", " ").replace(")"," ");
                        line=line.replace("||", "._");
//                        
                        while(line.contains("--")){
                            line=line.replace("--", " ");
                        }
                        line=line.replace(",", " ");
                        line=line.replace("?", " ");
                        line=line.replace("\"", " ");
                        while(line.contains("  ")){
                            line=line.replace("  ", " ");
                        }
                        line=line.replaceAll(pattern,"");
                        
			//System.out.println(line);
			if(line.contains("ARG")){
                            words = new Vector<Integer>();
				//System.out.println(line);
                                line=line.toLowerCase();
				StringTokenizer st = new StringTokenizer(line);
				
				while( st.hasMoreTokens() ){
					String wrd = st.nextToken();
					//System.out.println(wrd);
					if(wrd.endsWith("."))
                                            wrd=wrd.substring(0,wrd.lastIndexOf("."));
                                        if(wrd.startsWith("-") || wrd.endsWith("-")){
                                            wrd=wrd.replace("-", " ");
                                        }
					if( wrd.trim().isEmpty() || stopwords.getIndex(wrd) != -1){
                                        //if( wrd.trim().isEmpty()){
						continue;
					}
					System.out.print(wrd + " " );
					int wid = vocab.getIndex(wrd);
					//int wid = vocab.addSymbol(wrd);
					if( wid > 0 ){
						words.add(wid);
					}
				}
                                System.out.println();
                                //System.out.println(words.toString());
			}
		}
                
                
	}
        
        public static void getWordsFromFileLDATest( String file, Alphabet vocab, Alphabet stopwords) throws IOException{
		//This version return the dcocs and its words. No sentences. Used for the inference of the ITO model
		vocab.addSymbol("@@@@@@@@");
		String pattern = "[^A-Z a-z_.-]";
                int wrdCnt=0;
		BufferedReader in = new BufferedReader(new FileReader(new File(file)));
		String line;
		//Vector<Vector<Integer> > docs = new Vector<Vector<Integer> >();
		Vector<Integer> words = new Vector<Integer>();
                String doc="", orgline="";
		while( (line=in.readLine()) != null ){
                        orgline=line;
                        line=line.replace("'s", "").replace(":", " ");
                        String tempLine=line.replace("._", "||");
                        line=tempLine.replace(".", " ").replace("[", " ").replace("]", " ").replace("(", " ").replace(")"," ");
                        line=line.replace("||", "._");
//                        
                        while(line.contains("--")){
                            line=line.replace("--", " ");
                        }
                        line=line.replace(",", " ");
                        line=line.replace("?", " ");
                        line=line.replace("\"", " ");
                        while(line.contains("  ")){
                            line=line.replace("  ", " ");
                        }
                        line=line.replaceAll(pattern,"");
                        
			//System.out.println(line);
                        if(line.startsWith("DOC")) doc=orgline;
			if(!line.startsWith("DOC")){
                            words = new Vector<Integer>();
				//System.out.println(line);
                                wrdCnt=0;
                                line=line.toLowerCase();
				StringTokenizer st = new StringTokenizer(line);
				
				while( st.hasMoreTokens() ){
					String wrd = st.nextToken();
					//System.out.println(wrd);
					if(wrd.endsWith("."))
                                            wrd=wrd.substring(0,wrd.lastIndexOf("."));
                                        if(wrd.startsWith("-") || wrd.endsWith("-")){
                                            wrd=wrd.replace("-", " ");
                                        }
					if( wrd.trim().isEmpty() || stopwords.getIndex(wrd) != -1){
                                        //if( wrd.trim().isEmpty()){
						continue;
					}
                                        if(!wrd.trim().isEmpty()){
                                            //System.out.print(wrd + " " );
                                            wrdCnt++;
                                        }
					int wid = vocab.getIndex(wrd);
					//int wid = vocab.addSymbol(wrd);
					if( wid > 0 ){
						words.add(wid);
					}
				}
                                if(wrdCnt>0){
                                    System.out.println(doc);
                                    //System.out.println();
                                }
                                //System.out.println(words.toString());
			}
		}
                
        }
}
