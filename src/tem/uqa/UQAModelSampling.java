package tem.uqa;

import java.io.IOException;
import edu.smu.data.Alphabet;
import edu.smu.util.DataFormat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;

public class UQAModelSampling {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
                String base = "C:\\PhD\\2013\\FindExpertCQA Stack\\Results\\LDAUQA\\";
                String dataFile = base + "/ITO/dataEntitiesAll_bigrams.txt";
		//String dataFile = "./data/nyt.ftm";//format.txt";//kitch.txt";//"data_res.txt";//"data_res.txt";//
		//String dataFile = "C:/PhD/2012/ActionKnowledge/Action/src/data/datafilePER.txt";//format.txt";//kitch.txt";//"data_res.txt";//"data_res.txt";//
                //String dataFile = "C:/PhD/2012/ActionKnowledge/Action/src/data/datafileAdj.txt"; //for adj
		//String resFile = "C:/PhD/2012/ActionKnowledge/Action/src/data/res_50topics_ETAdj.txt";
                String resFile = base+"res_LDA.txt";
		String stopFile = "C:/PhD/2012/ActionKnowledge/Action/src/data/stopwords.txt";
		String betaFile = base+"betaFileLDA.txt";
                String alphaFile = base+"alphaFileLDA.txt";
                String gammaFile = base+"gammaFileLDA.txt";
                String topcatFile = base+"topcatFileLDA.txt";
                
		String vocabFileSave = base+"vocabLDA.txt";
                
		Alphabet vocab = new Alphabet(); //DataFormat.loadAlphabet(vocabFile);//
		Alphabet stopwords = DataFormat.loadAlphabet(stopFile);
                String originalDataPath="C:\\PhD\\2013\\FindExpertCQA Stack\\Raw data and Analysis\\Raw Data\\ThreeM\\";
                String minPostNum="100";
                String originalDocsPath = originalDataPath + "USER" + minPostNum + "/posts/";
                getTags(originalDocsPath);
                
               /* Documents docSet = new Documents();
		docSet.readDocs(originalDocsPath, minPostNum);
		
                int[][][] w = new int[docSet.docs.size()][][];
                
                for (int u = 0; u < docSet.docs.size(); u++) {
                    w[u] = new int[docSet.docs.get(u).docWords.length][]; //no of posts for each user
			for (int n = 0; n < docSet.docs.get(u).docWords.length; n++) {
                            w[u][n] = new int[docSet.docs.get(u).docWords[n].length]; // no of words in each post
                            for (int l = 0; l < docSet.docs.get(u).docWords[n].length; l++) {
                                int term = docSet.docs.get(u).docWords[n][l];
                                w[u][n][l] = term;
                                //System.out.println(term);
                            }
                        }
                }
                
                int[][] tags=new int[docSet.docs.size()][];
                for (int u = 0; u < docSet.docs.size(); u++) {
                    tags[u] = new int[docSet.docs.get(u).tags.length]; //no of tags for each user
			for (int n = 0; n < docSet.docs.get(u).tags.length; n++) {
                                int term = docSet.docs.get(u).tags[n];
                                tags[u][n] = term;
                                //remember each post has only one tag in the current document implementation
                                if (u==0) System.out.println(docSet.indexToTagMap.get(term) + ":" );
                                
                        }
                        System.out.println();
                        //System.out.println("tags:" + tags[u][0]);
                }
                
                //int[][][] words = DataFormat.getWordsFromFile(dataFile,vocab ,stopwords, -1, "ET" );	
                
                
		int numTokens = 0, numEntites=0;
		for(int d = 0; d < w.length; d++){
			for(int s = 0; s < w[d].length; s++){
				numTokens += w[d][s].length;
			}
		}
                System.out.println(w.length);
                
                
		System.out.println("Totally " + numTokens +  " tokens.");
		/**
		 * 
		 */
		/**
		 * running up
		 */
		//ATSenLDA ATlda = new ATSenLDA(50, vocab, words);
                //ETModelLDA ATlda = new ETModelLDA(20, vocab, entity, words, entities);
                
                //System.out.println(docSet.termToIndexMap);
                System.out.println("index term");
                //System.out.println(docSet.indexToTermMap);
                
                //LDA ATlda = new LDA(20,  docSet.termToIndexMap, docSet.indexToTermMap, w);
               // UQAModel ATlda = new UQAModel(15,  docSet.termToIndexMap, docSet.indexToTermMap, docSet.tagToIndexMap, w, tags);
                //LDAUQA ATlda = new LDAUQA(15,  docSet.termToIndexMap, docSet.indexToTermMap, w, tags);
		
		/**
		 * 
		 */
            /*    ATlda.saveVocab(vocabFileSave);
                
		System.out.println("==============================NEW ITERATION ==============================");
		ATlda.run(1000);
		
		ATlda.printTopics(20, resFile+"1000", true); //print n topic words
                
		ATlda.saveBeta(betaFile);
                ATlda.saveAlpha(alphaFile);
                ATlda.saveBeta(gammaFile);
                
                ATlda.printTopicsCategories(20, topcatFile+"1000", true);*/
                        
		
	}
        
        public static void getTags(String docsPath){
            double tags[][]=new double[618][2];
            try{
                BufferedReader bf;
                ArrayList<String> docLines = new ArrayList<String>();
                System.out.println("tags");
                String line, tag;
                HashMap<String, Integer> hm=new HashMap<String, Integer>();
                StringTokenizer st;
                int cnt=0;
                int usercnt=0;
                for(File docFile : new File(docsPath+ "USER100\\tags\\").listFiles()){
                    bf=new BufferedReader(new FileReader(docFile));
                    while ((line=bf.readLine())!=null) { 
                        tag=line.substring(line.indexOf(" ")+1);
                        st=new StringTokenizer(line, ">");
                        while(st.hasMoreTokens()){
                            tag=st.nextToken(); tag=tag.replace("<", "");
                            if(hm.containsKey(tag)){
                               cnt=hm.get(tag);
                               hm.remove(tag);
                               hm.put(tag, ++cnt);
                            }
                            else
                                hm.put(tag, 1);
                        }
                    }
                    
                    //tags[usercnt][0]=hm.
                }
            }catch(Exception e){
                
            }
        }
}
