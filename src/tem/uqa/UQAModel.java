package tem.uqa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class UQAModel {
    private boolean first = true;
	//private int Y;
	private double ALPHA;
	private double BETA;
        private double GAMMA;
        
	private int V;
	private int D;
	private int T;
        private int C;
        
	private int[][] ctw;
	private int[] ctdot;
	
	private int[][][] cdst;
	private int[][] cdsdot;
	
	private int[][][] words;
        private int[][] tags;
	
	private int[][][] assignZ;
        private int[][][] assignZc; //??????
	
        private int[][] nzw; // T * V for topics
        private int[] nzwdot; //T  for topics
        
        private int[][] mzc; //T * V for category/tags
        private int[] mzcdot; //U * T  for category/tags
        
        private int[][] lzdot; //U*T
        
        
	private Map<String, Integer> vocab;
        private Map<String, Integer> vocabTag;
        //new ;
        
        private ArrayList<String> vocabTerm;
	
	private double[] probs;
        
	public UQAModel(int T, Map<String, Integer> vocab, ArrayList<String> vocabTerm, Map<String, Integer> vocabTag, int[][][] words, int tags[][]){
		
		this.T = T;
                //we should assign C to total number of unique tags
                HashSet set=new HashSet();
                int totaltags=0;
                for(int d=0;d<tags.length;d++){
                    for(int j=0; j<tags[d].length;j++){
                        set.add(tags[d][j]); //unique tags
                        //set.add(tags[d][0]); //use first tag as category
                        //totaltags++;
                    }
                        //System.out.println("tags in cons:" + tags[d][0]); 
                }
                this.C=set.size(); //unique tags
                //this.C=totaltags; //all tags
                System.out.println("C:" + C);
		this.V = vocab.size();

		this.vocab = vocab;
		this.words = words;
                this.tags=tags;
		this.vocabTag = vocabTag;
                
		this.D = words.length;
		this.ALPHA = 50.0 / T;
		this.BETA = 0.05;
                this.GAMMA=50.0/C;
		
		this.assignZ = new int[D][][];
		cdst = new int[D][][];
		cdsdot = new int[D][];
                
                nzw=new int[T][V]; //U * T * V for topics
                nzwdot= new int[T]; //U * T  for topics
        
                mzc=new int[T][C]; //U * T * V for category/tags number of times tag c is assigned to topic z
                mzcdot=new int[T]; //U * T  for category/tags
        
                lzdot=new int[D][T]; //U*T
        
        
		int totwords=0;
		for(int d = 0; d < D; d++ ){
			assignZ[d] = new int[words[d].length][];
			cdst[d] = new int[words[d].length][];
			cdsdot[d] = new int[words[d].length];
                        totwords=0;
			for(int s = 0; s < words[d].length; s++ ){
				cdst[d][s] = new int[T];
				assignZ[d][s] = new int[words[d][s].length];
                                totwords+=words[d][s].length;
			}
                   //nzw[d][T]=new int[totwords];
		}
		//for(int d = 0; d < D; d++ ){
                //    mzc[d][T]=new int[tags.length];
                //}
		ctw = new int[T][V];
		ctdot = new int[T];
		
		probs = new double[T];
	}
	
	public void run(int iter){
		//Initialize 
		if( first )
			init();	
		System.out.println("Initializing done!");
		int cnt = 0;
		while(cnt < iter){
			System.out.print("iter... " + cnt );
			for(int d = 0; d < D; d++ ){
				for(int s = 0; s < words[d].length; s++ ){
					for(int n = 0; n < words[d][s].length; n++ ){
                                                //for each category of the user sample the topic for()
                                            //for(int tag=0;tag<tags[d].length;tag++){
                                                //int c=tags[d][tag];
                                                int c=tags[d][s]; //use first tag as category of the post
						sampleTopic(d,s,n,c); 
                                            //}
					}
				}
			}
			System.out.println(" DONE");
			cnt++;
		}
	}
	
	private void sampleTopic(int d, int s, int n, int c){
		//c is the current tag
		int t = assignZ[d][s][n];
		int v = words[d][s][n];
                //int c=tags[d][n];
                cdst[d][s][t]--;
		cdsdot[d][s]--;
		
		ctw[t][v]--;
		ctdot[t]--;
		
                
                nzw[t][v]--;
                nzwdot[t]--;
                
                mzc[t][c]--;
                mzcdot[t]--;
                
                lzdot[d][t]--;
                
                double prob1=1, prob2=1, prob3=1;
		for(t = 0; t < T; t++){
                    prob1=1; prob2=1; prob3=1;
                    //probs[t] = (cdst[d][s][t]+ALPHA)/(cdsdot[d][s]+T*ALPHA)* (ctw[t][v]+BETA) / (ctdot[t]+V*BETA);
                    //prob1=(nzw[t][v] +BETA -1)/(nzwdot[t] + V * BETA -1 );
                    prob1=(nzw[t][v] +BETA)/(nzwdot[t] + V * BETA);
                    prob2=(mzc[t][c] +GAMMA)/(mzcdot[t] + C * GAMMA );
                    //prob2=(mzc[t][c] +GAMMA -1)/(mzcdot[t] + C * GAMMA -1 );
                    prob3=lzdot[d][t] + ALPHA;
                    //prob3=(lzdot[d][t] + ALPHA - 1);
                    probs[t]=prob1 * prob2 * prob3;
		}
		
		for(t = 1; t < T; t++ ){
			probs[t] += probs[t-1];
		}
		double r = Math.random() * probs[T-1];
		
		for(t = 0; t < T; t++ ){
			//System.out.print( probs[t] + "\t");
			if( probs[t] >= r){
				break;
			}
		}
		if(t == T){
			for(t = 0; t < T; t++ ){
				System.out.println(probs[t]);
			}
		}
                //t=roulette_sample(probs);
		assignZ[d][s][n] = t;
//System.out.println("new t:" + t);                
                nzw[t][v]++;
                nzwdot[t]++;
                
                mzc[t][c]++;
                mzcdot[t]++;
                
                lzdot[d][t]++;
                
                cdst[d][s][t]++;
		cdsdot[d][s]++;
		ctw[t][v]++;
		ctdot[t]++;
	}
        private int roulette_sample(double[] p) {
		// Accumulate multi-nominal parameters
		int n = p.length;
		double pt[] = new double[n];
		pt[0] = p[0];
		for (int i = 1; i < n; i++) {
			pt[i] = pt[i - 1] + p[i];
		}

		// scaled sample because of unnormalized p[]
		double rouletter = Math.random() * pt[n - 1];
		int sample = 0;
		for (; sample < n; sample++) {
			if (pt[sample] >= rouletter)
				break;
		}

		if (sample > n - 1) {
			System.err.println("Sampling error!");
			for (int i = 0; i < p.length; i++) {
				System.err.print(p[i] + "\t");
			}
			System.err.print(sample + "\t");
			System.exit(0);
		}
		return sample;
	}
	public void init(){
		first = false;
		
		for(int d = 0; d < D; d++ ){
                    HashSet tagset=new HashSet();
			for(int s = 0; s < words[d].length; s++ ){
				for(int n = 0; n < words[d][s].length; n++ ){
					
					int v = words[d][s][n];
                                        //System.out.println(vocab.containsValue(v));
					int t = (int)(Math.random() * T);
					
					assignZ[d][s][n] = t;
					
					ctw[t][v]++;
					ctdot[t]++;
					
					cdst[d][s][t]++;
					cdsdot[d][s]++;
                                        
                                        nzw[t][v]++;
                                        nzwdot[t]++;
                
                                        lzdot[d][t]++;
                                        /*for(int tag = 0; tag < tags[d].length; tag++ ){
                                            mzc[t][tag]++;
                                            mzcdot[t]++;
                                        }*/
                                        int c=tags[d][s];
                                        mzc[t][c]++; //use the first tag as category
                                        mzcdot[t]++;
                                        
                                        tagset.add(t);
				}
			}// for each word in the post
                        
		}
                /*for(int d = 0; d < D; d++ ){
                    System.out.println(d + ":" + tags[d].length);
                    for(int tag = 0; tag < tags[d].length; tag++ ){
                        int t = (int)(Math.random() * T);
                        mzc[t][tag]++;
                        mzcdot[t]++;
                        lzdot[d][t]++;
                    }
                }*/
                
	}

	
	public void printTopics(int K, String file, boolean debug) throws IOException{
                Map<Integer, String> vocabRev=new HashMap<Integer, String>();
                Set keySet=vocab.keySet();
                Iterator it=keySet.iterator();
                while(it.hasNext()){
                     String key=it.next().toString();
                      vocabRev.put(vocab.get(key), key);
                }
                
		BufferedWriter out = new BufferedWriter(
                new FileWriter( new File(file)));//, true ));
		List<Pair> LP = new ArrayList();
		for(int t = 0; t < T; t++){
			out.write("Topic " + t + "\n");
			
			if(debug){
				System.out.print("Topic " + t + "\n");
			}
			LP.clear();
			
			for(int v = 0; v < V; v++ ){
				//if( t < T && ctw[t][v] > 0 )
				//	LP.add(new Pair(v,ctw[t][v]));
                            if( t < T && nzw[t][v] > 0 )
					LP.add(new Pair(v,nzw[t][v]));
			}
			Collections.sort(LP);
			
			for(int i = 0; i < K && i < LP.size(); i++ ) {
				if( LP.get(i).getW() > 0 ){
					out.write( "\t" + vocabRev.get(LP.get(i).getK())+" "+LP.get(i).getW() + "\n");
					out.flush();
					if(debug){
                                            System.out.print( "\t" + vocabRev.get(LP.get(i).getK())+" "+LP.get(i).getW() + "\n");
					}
				}
			}
		}
		out.flush();
		out.close();
	}
        
        public void printTopicsCategories(int K, String file, boolean debug) throws IOException{
                Map<Integer, String> vocabRev=new HashMap<Integer, String>();
                Set keySet=vocabTag.keySet();
                Iterator it=keySet.iterator();
                while(it.hasNext()){
                     String key=it.next().toString();
                      vocabRev.put(vocabTag.get(key), key);
                }
		BufferedWriter out = new BufferedWriter(
                new FileWriter( new File(file)));//, true ));
		List<Pair> LP = new ArrayList();
		for(int t = 0; t < T; t++){
			out.write("Topic - Categories " + t + "\n");
			
			if(debug){
				System.out.print("Topic - Categories " + t + "\n");
			}
			LP.clear();
			
			for(int c = 0; c < C; c++ ){
				//if( t < T && ctw[t][v] > 0 )
				//	LP.add(new Pair(v,ctw[t][v]));
                            if( t < T && mzc[t][c] > 0 )
					LP.add(new Pair(c,mzc[t][c]));
			}
			Collections.sort(LP);
			
			for(int i = 0; i < K && i < LP.size(); i++ ) {
				if( LP.get(i).getW() > 0 ){
					out.write( "\t" + vocabRev.get(LP.get(i).getK())+" "+LP.get(i).getW() + "\n");
					out.flush();
					if(debug){
                                            System.out.print( "\t" + vocabRev.get(LP.get(i).getK())+" "+LP.get(i).getW() + "\n");
					}
				}
			}
		}
		out.flush();
		out.close();
	}
        
        public void saveGamma(String file) throws IOException{
		BufferedWriter out = new BufferedWriter(
                new FileWriter( new File(file)));//, true ));
		out.write(T + " " + V + "\n");
		for(int t = 0; t < T; t++){
			for(int c = 0; c < C; c++){
				//double a = (ctw[t][v]+BETA) / (ctdot[t]+V*BETA) ;
                                double a =(mzc[t][c] +GAMMA)/(mzcdot[t] + C * GAMMA );
				out.write(new Double(a).toString() + " ");
			}
			out.write("\n");
		}
		out.flush();
		out.close();
	}
        
	public void saveBeta(String file) throws IOException{
		BufferedWriter out = new BufferedWriter(
                new FileWriter( new File(file)));//, true ));
		out.write(T + " " + V + "\n");
		for(int t = 0; t < T; t++){
			for(int v = 0; v < V; v++){
				//double a = (ctw[t][v]+BETA) / (ctdot[t]+V*BETA) ;
                                double a =(nzw[t][v] +BETA)/(nzwdot[t] + V * BETA);
				out.write(new Double(a).toString() + " ");
			}
			out.write("\n");
		}
		out.flush();
		out.close();
	}
	
        public void saveAlpha(String file) throws IOException{
		BufferedWriter out = new BufferedWriter(
                new FileWriter( new File(file)));//, true ));
		out.write(T + " " + V + "\n");
                for(int d = 0; d < D; d++){
                    //for(int s = 0; s < words[d].length; s++ ){
			for(int t = 0; t < T; t++){
				//double a = (ctw[t][v]+BETA) / (ctdot[t]+V*BETA) ;
                                //double a = (cdst[d][s][t]+ALPHA)/(cdsdot[d][s]+T*ALPHA);
                                double a =lzdot[d][t] + ALPHA;
				out.write(new Double(a).toString() + " ");
			}
			out.write("\n");
                    //}
		}
		out.flush();
		out.close();
	}
        
        public void saveVocab(String file) throws IOException{
		BufferedWriter out = new BufferedWriter(
                new FileWriter( new File(file)));//, true ));
		//get vocab and save 
                Set keyset=vocab.keySet();
                Iterator it=keyset.iterator();
                while(it.hasNext()){
                    out.write(it.next().toString() + "\n");
                    //out.write(vocabit.next()).toString() + "\n");
                }
		out.flush();
		out.close();
	}
        
	class Pair implements Comparable {
		private int key;
		private int w;
		public Pair(int k, int w2){
			key = k;
			w = w2;
		}
		public int getK(){
			return key;
		}
		public int getW(){
			return w;
		}
		public int compareTo(Object P ) {
			Pair A = (Pair)P;
			if( w > A.w )
				return -1;
			else if( w < A.w )
				return 1;
			if( w == A.w )
			{
				if( key < A.key )
					return 1;
				else if( key > A.key )
					return -1;
			}
			return 0;
		}
	}
	
	
}
