package tem.uqa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tem.com.FileUtil;
import tem.conf.ConstantConfig;
import tem.conf.PathConfig;
import tem.main.Documents;
import tem.main.TEMModel;
import tem.main.TEMModelSampling.modelparameters;

public class UQAModelRes implements java.io.Serializable {

	private static final long serialVersionUID = 2L;
	
	//term map
	public Map<String, Integer> termToIndexMap;
	public Map<Integer, String> indexToTermMap;
	
	//tag map
	public Map<String, Integer> tagToIndexMap;
	public Map<Integer, String> indexToTagMap;
	
	//user map
	public Map<String, Integer> userToIndexMap;
	public Map<Integer, String>  indexToUserMap;
	
	//theta User topic distribution
	public double [][] theta;
	
	//phi topic word distribution
	public double [][] phi;
	
	//psi topic tag distribution
	public double [][] psi;
	
	public UQAModelRes(){
		termToIndexMap = new HashMap<String, Integer>();
		indexToTermMap =  new HashMap<Integer, String>();
		
		tagToIndexMap = new HashMap<String, Integer>();
		indexToTagMap = new HashMap<Integer, String>();
		
		userToIndexMap = new HashMap<String, Integer>();
		indexToUserMap = new HashMap<Integer, String>();
	}
	
	public UQAModelRes(String resPath){
		termToIndexMap = new HashMap<String, Integer>();
		indexToTermMap =  new HashMap<Integer, String>();
		
		tagToIndexMap = new HashMap<String, Integer>();
		indexToTagMap = new HashMap<Integer, String>();
		
		userToIndexMap = new HashMap<String, Integer>();
		indexToUserMap = new HashMap<Integer, String>();
		
		readMap((resPath + "termMap"), termToIndexMap, indexToTermMap);
		readMap((resPath + "tagMap"), tagToIndexMap, indexToTagMap);
		readMapForOneCol((resPath + "userMap"), userToIndexMap, indexToUserMap);
		
		
		theta = FileUtil.read2DArray(resPath + "thetaUT");
		phi = FileUtil.read2DArray(resPath + "phiTV");
		psi =  FileUtil.read2DArray(resPath + "psiTC");	
	}

	private void readMapForOneCol(String fileName,
			Map<String, Integer> termToIndexMap2,
			Map<Integer, String> indexToTermMap2) {
		// TODO Auto-generated method stub
		//Build index from 0
		System.out.println("read map from " + fileName);
		ArrayList<String> lines = new ArrayList<String>();
		FileUtil.readLines(fileName, lines);
		for(int i = 0; i < lines.size(); i++){
			termToIndexMap2.put(lines.get(i).trim(), new Integer(i));
			indexToTermMap2.put(new Integer(i), lines.get(i).trim());
		}
	}

	private void readMap(String fileName, Map<String, Integer> termToIndexMap2,
			Map<Integer, String> indexToTermMap2) {
		// TODO Auto-generated method stub
		System.out.println("read map from " + fileName);
		ArrayList<String> lines = new ArrayList<String>();
		FileUtil.readLines(fileName, lines);
		for(String line : lines){
			String [] tokens = line.split("\t");
			int index = Integer.parseInt(tokens[1]);
			termToIndexMap2.put(tokens[0], new Integer(index));
			indexToTermMap2.put(new Integer(index), tokens[0]);
		}
	}
	
	public static void main(String[] args) throws IOException,
	ClassNotFoundException {

		String UQAPath = PathConfig.UQAPath;
		//UQAModelRes uqaRes = new UQAModelRes(UQAPath);
		UQAModelRes uqaRes = new UQAModelRes();
		
		String dataFile = UQAPath + "UQAModelRes.data";
		
		uqaRes = FileUtil.loadClass(uqaRes, dataFile);
		 //FileUtil.saveClass(uqaRes, dataFile);
		 System.out.println(uqaRes.indexToTagMap.size());
		 System.out.println(uqaRes.indexToTermMap.size());
		 System.out.println(uqaRes.indexToUserMap.size());
	}
}
