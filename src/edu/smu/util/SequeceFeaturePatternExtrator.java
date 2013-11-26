package edu.smu.util;

import java.util.ArrayList;

import edu.smu.data.Alphabet;
import edu.smu.data.Instance;
import edu.smu.data.Sequence;
import edu.smu.data.SequenceList;
import edu.smu.data.SparseVector;

public class SequeceFeaturePatternExtrator {
	public static SequenceList getTrainSeqListFromFileWithPreviousLabel(String dataFile, String templateFile, Alphabet featSet, Alphabet labelSet){
		ArrayList<String> dataLines = new ArrayList<String>();//
		FileUtil.readLinesBySequence(dataFile, dataLines);
		
		ArrayList<String> templateLines = new ArrayList<String>();//
		FileUtil.readLines(templateFile, templateLines);
		
		//System.out.println(dataLines.size());
		
		int[] x = new int[templateLines.size()];
		int[] y = new int[templateLines.size()];
		for(int i = 0; i < templateLines.size(); i++){
			String[] pat = templateLines.get(i).split(",");
			x[i] = new Integer(pat[0]);
			y[i] = new Integer(pat[1]);
		}
		
		SequenceList seqList = new SequenceList(labelSet);
		featSet.addSymbol("ME_BIAS");
		
		for(int i = 0; i < dataLines.size(); i++){
			//System.out.println(dataLines.get(i));
			seqList.addSequence( str2Seq(dataLines.get(i), x, y, featSet, labelSet, Integer.MAX_VALUE  ) );
		}
		return seqList; 
	}
	
	public static SequenceList[] getTestSeqListFromFileWithPreviousLabel(String dataFile, String templateFile, Alphabet featSet, Alphabet labelSet){
		ArrayList<String> dataLines = new ArrayList<String>();//
		FileUtil.readLinesBySequence(dataFile, dataLines);
		
		ArrayList<String> templateLines = new ArrayList<String>();//
		FileUtil.readLines(templateFile, templateLines);
		
		//System.out.println(dataLines.size());
		
		int[] x = new int[templateLines.size()];
		int[] y = new int[templateLines.size()];
		for(int i = 0; i < templateLines.size(); i++){
			String[] pat = templateLines.get(i).split(",");
			x[i] = new Integer(pat[0]);
			y[i] = new Integer(pat[1]);
		}
		
		SequenceList[] seqList = new SequenceList[labelSet.size()];
		for(int i = 0; i < seqList.length; i++ ){
			seqList[i] = new SequenceList(labelSet);
		}
		featSet.addSymbol("ME_BIAS");
		
		for(int i = 0; i < dataLines.size(); i++){
			//System.out.println(dataLines.get(i));
			for(int l = 0; l < labelSet.size(); l++ ){
				seqList[l].addSequence( str2Seq(dataLines.get(i), x, y, featSet, labelSet, l ) );
			}
		}
		return seqList; 
	}
	
	public static SequenceList getSeqListFromFile(String dataFile, String templateFile, Alphabet featSet, Alphabet labelSet){
		ArrayList<String> dataLines = new ArrayList<String>();//
		FileUtil.readLinesBySequence(dataFile, dataLines);
		
		ArrayList<String> templateLines = new ArrayList<String>();//
		FileUtil.readLines(templateFile, templateLines);
		
		//System.out.println(dataLines.size());
		
		int[] x = new int[templateLines.size()];
		int[] y = new int[templateLines.size()];
		for(int i = 0; i < templateLines.size(); i++){
			String[] pat = templateLines.get(i).split(",");
			x[i] = new Integer(pat[0]);
			y[i] = new Integer(pat[1]);
		}
		
		SequenceList seqList = new SequenceList(labelSet);
		featSet.addSymbol("ME_BIAS");
		
		for(int i = 0; i < dataLines.size(); i++){
			//System.out.println(dataLines.get(i));
			seqList.addSequence( str2Seq(dataLines.get(i), x, y, featSet, labelSet, -1 ));
		}
		return seqList; 
	}
	public static Sequence str2Seq(String line, int[] x, int[] y, Alphabet featSet, Alphabet labelSet, int prevLabel){
		//System.out.println(line);
		String[] str = line.split("@");
		String[][] item = new String[str.length][];
		for(int i = 0; i < item.length; i++ ){
			//System.out.println(str[i]);
			item[i] = str[i].split(" ");
		}
		
		/*for(int i = 0; i < item.length; i++){
			for(int j = 0; j < item[i].length; j++){
				System.out.println( item[i][j] + "\t");
			}
			System.out.println();
		}*/
		
		int row = item.length;
		int col = item[0].length;
		
		//System.out.println(row);
		//System.out.println(col);
		
		Sequence seq = new Sequence();
		
		for(int r = 0; r < row; r++){
			//int label = labelSet.addSymbol(item[r][col-1]);
			int label = labelSet.addSymbol(item[r][col-1].substring(0,1));
			
			ArrayList<Integer> arrInd = new ArrayList<Integer>();
			ArrayList<Double> arrValue = new ArrayList<Double>();
			
			String post = "";
			
			/**
			 * Adding the set prev_label;
			 */
			if( prevLabel >= 0 && prevLabel < labelSet.size() && r >= 1){
				//if( prev){
					
				//}
				//System.out.println("prev=_" + labelSet.getSymbol(prevLabel));
				//arrInd.add( featSet.addSymbol("prev=_" + labelSet.getSymbol(prevLabel)+"_"+item[r-1][1]));
				//arrValue.add( 1.0 );
				//arrInd.add( featSet.addSymbol("prev=_" + labelSet.getSymbol(prevLabel)));
				//arrValue.add( 1.0 );
				post = "_prev=_" + labelSet.getSymbol(prevLabel);
			}/**
		      *  Adding the previous label;
		      */
			else if( prevLabel == Integer.MAX_VALUE && r >= 1 ){
				//arrInd.add( featSet.addSymbol("prev=_" + item[r-1][col-1]));
				//System.out.println("prev=_" + item[r-1][col-1]);
				//arrValue.add( 1.0 );
				post = "_prev=_" + item[r-1][col-1] ;
			}
			
			for(int i = 0; i < x.length; i++){
				int tx = x[i] + r;
				int ty = y[i];
				if( tx >= 0 && tx < row && ty >= 0 && ty < col ){
					String fea = item[tx][ty] + "_x["+ new Integer(x[i])+"," + new Integer(y[i]) + "]" + post;
					//System.out.println(fea);
					int fId = featSet.addSymbol(fea);
					double v = 1.0;
					arrInd.add(fId);
					arrValue.add(v);
					
					
					fea = item[tx][ty] + "_x["+ new Integer(x[i])+"," + new Integer(y[i]) + "]";
					arrInd.add(featSet.addSymbol(fea));
					arrValue.add(v);
					
				}
			}
			int[] inds = new int[arrInd.size()];
			MatrixOps.arrayListToArray(arrInd, inds);
			double[] values = new double[arrValue.size()];
			MatrixOps.arrayListToArray(arrValue, values);
			
			//System.out.println(inds.length + " " + values.length);
			Instance inst = new Instance(new SparseVector(inds, values), label);
			//inst.display();
			seq.addInstance(inst);
		}
		return seq;
	}
}
