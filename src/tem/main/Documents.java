package tem.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tem.com.FileUtil;
import tem.com.Stopwords;
import tem.conf.PathConfig;
import tem.parser.Porter;
import tem.parser.StanfordTokenizer;

/**Documents class for posts in Stack Overflow Forum
 * Preprocess posts and transfer them to word index representation
 * @author yangliu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yangliuyx@gmail.com
 */

public class Documents implements java.io.Serializable{
	private static final long serialVersionUID = 2L;
	
	ArrayList<Document> docs; 
	//term map
	Map<String, Integer> termToIndexMap;
	ArrayList<String> indexToTermMap;
	Map<String,Integer> termCountMap;
	
	//tag map
	Map<String, Integer> tagToIndexMap;
	ArrayList<String> indexToTagMap;
	Map<String,Integer> tagCountMap;
	
	//vote map
	Map<String, Integer> voteToIndexMap;
	ArrayList<String> indexToVoteMap;
	Map<String,Integer> voteCountMap;
	
	public Documents(){
		docs = new ArrayList<Document>();
		termToIndexMap = new HashMap<String, Integer>();
		indexToTermMap = new ArrayList<String>();
		termCountMap = new HashMap<String, Integer>();
		
		tagToIndexMap = new HashMap<String, Integer>();
		indexToTagMap = new ArrayList<String>();
		tagCountMap = new HashMap<String, Integer>();
		voteToIndexMap = new HashMap<String, Integer>();
		indexToVoteMap = new ArrayList<String>();
		voteCountMap = new HashMap<String, Integer>();
	}
	
	public void readDocs(String docsPath, String minPostNum){
		Stopwords stopwords = new Stopwords();
		Porter stemmer = new Porter();
		for(File docFile : new File(docsPath).listFiles()){
			Document doc = new Document(docFile, termToIndexMap, indexToTermMap, termCountMap,
										tagToIndexMap, indexToTagMap, tagCountMap, voteToIndexMap, indexToVoteMap, voteCountMap, stopwords, stemmer, minPostNum);
			docs.add(doc);
		}
	}
	
	public void readQATestDocs(String testDataFolder, Documents trainDocSet) {
		// TODO Auto-generated method stub
		//Use the same vocalbulary of trianDocSet
		copyTrainDocVocals(trainDocSet);
		System.out.println("train terms: " + trainDocSet.termToIndexMap.size());
		
		System.out.println("after copy terms: " + termToIndexMap.size());
		System.out.println("after copy votes: " + voteToIndexMap.size());
		System.out.println("after copy tags: " + tagToIndexMap.size());
		
		String questionFile = testDataFolder + "testData.questions";
		ArrayList<String> questionLines = new ArrayList<String>();
		String answersFolder = testDataFolder + "answers/";
		String tagsFolder = testDataFolder + "tags/";
		System.out.println("tag folder: " + tagsFolder);
		ArrayList<String> tagsLines = new ArrayList<String>();
		Stopwords stopwords = new Stopwords();
		Porter stemmer = new Porter();
		//Read test question file
		Document doc = new Document(new File(questionFile), termToIndexMap, indexToTermMap, termCountMap,
				tagToIndexMap, indexToTagMap, tagCountMap, voteToIndexMap, indexToVoteMap, voteCountMap, stopwords, stemmer, 1);
		docs.add(doc);
		
		//Print tags of questions
		FileUtil.readLines(questionFile, questionLines);
		for(String questionLine : questionLines){
			String[] qTokens = questionLine.split("\t");
			String qTagFile = tagsFolder + qTokens[0] + ".tags";
			tagsLines.clear();
			for(int i = 0; i < Integer.valueOf(qTokens[17]); i++){
				tagsLines.add( qTokens[0] + "\t" + qTokens[16]);
			}
			FileUtil.writeLines(qTagFile, tagsLines);
		}
		
		//Read test answer files
		for(File docFile : new File(answersFolder).listFiles()){
			doc = new Document(docFile, termToIndexMap, indexToTermMap, termCountMap,
										tagToIndexMap, indexToTagMap, tagCountMap, voteToIndexMap, indexToVoteMap, voteCountMap, stopwords, stemmer,2);
			docs.add(doc);
		}
	}
	
	public void copyTrainDocVocals(Documents trainDocSet) {
		// TODO Auto-generated method stub

		this.termToIndexMap.putAll(trainDocSet.termToIndexMap);
		this.termCountMap.putAll(trainDocSet.termCountMap);
		this.indexToTermMap.addAll(trainDocSet.indexToTermMap);
		
		this.tagToIndexMap.putAll(trainDocSet.tagToIndexMap);
		this.tagCountMap.putAll(trainDocSet.tagCountMap);
		this.indexToTagMap.addAll(trainDocSet.indexToTagMap);
		
		this.voteToIndexMap.putAll(trainDocSet.voteToIndexMap);
		this.voteCountMap.putAll(trainDocSet.voteCountMap);
		this.indexToVoteMap.addAll(trainDocSet.indexToVoteMap);
	}

	public static class Document implements java.io.Serializable {	
		
		private static final long serialVersionUID = 1L;
		String docName; // document name which includes user id
		int [] postID;//N postID
		int [] postTypeID;//N post type ID. 1 denotes question; 2 denotes answers
		int [] parentID;//N parent ID. Only used when postTypeID is 2; ID of the question that answered by it
		int [] acceptedAnswerID;//N accepted answer ID; only present if postTypeID is 1;
		//ArrayList<String> creationDate = new ArrayList<String>();//N created date of the post
		int [] votes; //N
		//int [] viewCount;//N 
		int [][] docWords; //N*L   N posts * L words
		int [][] tags; //N*P N posts, the number of tags for each posts are P_n
		int [] ownerUserID;//N
		//ArrayList<String> closedDate = new ArrayList<String>();//N
		ArrayList<String> title = new ArrayList<String>();//N
		//int [] answerCount;//N
		//int [] commentCount;//N
		//int [] favoriteCount;//N
		
		//Constructor for QA test post
		public Document(File docFile, Map<String, Integer> termToIndexMap,
				ArrayList<String> indexToTermMap,
				Map<String, Integer> termCountMap,
				Map<String, Integer> tagToIndexMap,
				ArrayList<String> indexToTagMap,
				Map<String, Integer> tagCountMap,
				Map<String, Integer> voteToIndexMap,
				ArrayList<String> indexToVoteMap,
				Map<String, Integer> voteCountMap, Stopwords stopwords,
				Porter stemmer, int QAType) {
			// TODO Auto-generated constructor stub
			docName = docFile.getName();
			System.out.println("Now File is : " + docFile.getName());
			//Read file and initialize document object
			ArrayList<String> docLines = new ArrayList<String>();
			ArrayList<String> docTags = new ArrayList<String>();
			FileUtil.readLines(docFile.getAbsolutePath(), docLines);
			
			//Initialize variables in document object
			int linesSize = docLines.size();
			System.out.println("docLines size: " + linesSize);
			postID = new int[linesSize];
			postTypeID = new int[linesSize];
			parentID = new int[linesSize];
			acceptedAnswerID = new int[linesSize];
			//viewCount = new int[linesSize];
			docWords = new int[linesSize][];
			ownerUserID = new int[linesSize];
			//answerCount = new int[linesSize];
			//commentCount = new int[linesSize];
			//favoriteCount = new int[linesSize];
			tags = new int[linesSize][];
			votes = new int[linesSize];
			//read tags
			if(docLines.size() > 0 && QAType == 2){
				String tagFile = PathConfig.testDataPath + "tags/" + docLines.get(0).split("\t")[2] + ".tags";
				FileUtil.readLines(tagFile, docTags);
			}
			
			for(int i = 0; i < linesSize; i++){
				String[] lineTokens = docLines.get(i).split("\t");
				if(lineTokens.length != 20){
					System.err.println("post file format error: " + docLines.get(i));
					continue;
				}
				postID[i] = Integer.parseInt(lineTokens[0]);
				postTypeID[i] = Integer.parseInt(lineTokens[1]);
				parentID[i] = Integer.parseInt(lineTokens[2]);
				acceptedAnswerID[i] = Integer.parseInt(lineTokens[3]);
				//creationDate.add(lineTokens[4]);
				//viewCount[i] = Integer.parseInt(lineTokens[6]);
				ownerUserID[i] = Integer.parseInt(lineTokens[8]);
				//closedDate.add(lineTokens[14]);
				title.add(lineTokens[15]);
				
				//Process tags
				//Build index
				String tagString;
				if(postTypeID[i] == 1){// question
					tagString = lineTokens[16];
				} else { // answer
					String answerTag = docTags.get(i).split("\t")[1];
					tagString = answerTag;
				}
				String[] tagArray = tagString.replaceAll("[<>]", " ").split("  ");
				tags[i] = new int[tagArray.length];
				for (int j = 0; j < tagArray.length; j++) {
					String tagTrimed = tagArray[j].replace(" ", "");
					if (!tagToIndexMap.containsKey(tagTrimed)) {
						tagToIndexMap.put(tagTrimed, tagToIndexMap.size());
						indexToTagMap.add(tagTrimed);
						tagCountMap.put(tagTrimed, new Integer(0));
					} 
					tagCountMap.put(tagTrimed, tagCountMap.get(tagTrimed) + new Integer(1));
					tags[i][j] = tagToIndexMap.get(tagTrimed);
				}
				
				//Process votes
				//Put more votes
				String vote = ProcessVote(lineTokens[5]);
				if (!voteToIndexMap.containsKey(vote)) {
					voteToIndexMap.put(vote, voteToIndexMap.size());
					indexToVoteMap.add(vote);
					voteCountMap.put(vote, new Integer(0));
				} 
				voteCountMap.put(vote, voteCountMap.get(vote) + new Integer(1));
				votes[i] = voteToIndexMap.get(vote);
				
			    //Preprocess post  body
				String originalPost = lineTokens[7];
				int maxWordLength = 30;
				String preprocessedPost = preprocessPost(originalPost, stemmer, maxWordLength);
				ArrayList<String> words = new ArrayList<String>();
				FileUtil.tokenize(preprocessedPost, words);
				
				docWords[i] = new int[words.size()];
				for (int j = 0; j < words.size(); j++) {
					String wordString = words.get(j).toLowerCase().trim();
					if (!termToIndexMap.containsKey(wordString)) {
						termToIndexMap.put(wordString, termToIndexMap.size());
						indexToTermMap.add(wordString);
						termCountMap.put(wordString, new Integer(0));
					} 
					termCountMap.put(wordString, termCountMap.get(wordString) + new Integer(1));
					docWords[i][j] = termToIndexMap.get(wordString);
				}
			}
		}
		
		public Document(File docFile, Map<String, Integer> termToIndexMap, ArrayList<String> indexToTermMap, Map<String, Integer> termCountMap,
			Map<String, Integer> tagToIndexMap, ArrayList<String> indexToTagMap, Map<String,Integer> tagCountMap,  Map<String, Integer> voteToIndexMap,
			ArrayList<String> indexToVoteMap, Map<String,Integer> voteCountMap, Stopwords stopwords, Porter stemmer, String minPostNum){
			docName = docFile.getName();
			System.out.println("Now File is : " + docFile.getName());
			//Read file and initialize document object
			ArrayList<String> docLines = new ArrayList<String>();
			ArrayList<String> docTags = new ArrayList<String>();
			FileUtil.readLines(docFile.getAbsolutePath(), docLines);
			
			//Initialize variables in document object
			int linesSize = docLines.size();
			System.out.println("docLines size: " + linesSize);
			postID = new int[linesSize];
			postTypeID = new int[linesSize];
			parentID = new int[linesSize];
			acceptedAnswerID = new int[linesSize];
			//viewCount = new int[linesSize];
			docWords = new int[linesSize][];
			ownerUserID = new int[linesSize];
			//answerCount = new int[linesSize];
			//commentCount = new int[linesSize];
			//favoriteCount = new int[linesSize];
			tags = new int[linesSize][];
			votes = new int[linesSize];
			//read tags
			if(docLines.size() > 0){
				//"data/originalData/USER1000/tags/";
				String tagFile = PathConfig.originalDataPath + "USER" +minPostNum + "/tags/" + docLines.get(0).split("\t")[8] + ".tags";
				FileUtil.readLines(tagFile, docTags);
			}
			
			for(int i = 0; i < linesSize; i++){
				String[] lineTokens = docLines.get(i).split("\t");
				if(lineTokens.length != 20){
					System.err.println("post file format error: " + docLines.get(i));
					continue;
				}
				postID[i] = Integer.parseInt(lineTokens[0]);
				postTypeID[i] = Integer.parseInt(lineTokens[1]);
				parentID[i] = Integer.parseInt(lineTokens[2]);
				acceptedAnswerID[i] = Integer.parseInt(lineTokens[3]);
				//creationDate.add(lineTokens[4]);
				//viewCount[i] = Integer.parseInt(lineTokens[6]);
				ownerUserID[i] = Integer.parseInt(lineTokens[8]);
				//closedDate.add(lineTokens[14]);
				title.add(lineTokens[15]);
				
				//Process tags
				//Build index
				String tagString;
				if(postTypeID[i] == 1){// question
					tagString = lineTokens[16];
				} else { // answer
					String answerTag = docTags.get(i).split("\t")[1];
					tagString = answerTag;
				}
				String[] tagArray = tagString.replaceAll("[<>]", " ").split("  ");
				tags[i] = new int[tagArray.length];
				for (int j = 0; j < tagArray.length; j++) {
					String tagTrimed = tagArray[j].replace(" ", "");
					if (!tagToIndexMap.containsKey(tagTrimed)) {
						tagToIndexMap.put(tagTrimed, tagToIndexMap.size());
						indexToTagMap.add(tagTrimed);
						tagCountMap.put(tagTrimed, new Integer(0));
					} 
					tagCountMap.put(tagTrimed, tagCountMap.get(tagTrimed) + new Integer(1));
					tags[i][j] = tagToIndexMap.get(tagTrimed);
				}
				
				//Process votes
				//Bin some votes to the same value to add redundancy
				//Build index
				String vote = ProcessVote(lineTokens[5]);
				if (!voteToIndexMap.containsKey(vote)) {
					voteToIndexMap.put(vote, voteToIndexMap.size());
					indexToVoteMap.add(vote);
					voteCountMap.put(vote, new Integer(0));
				} 
				voteCountMap.put(vote, voteCountMap.get(vote) + new Integer(1));
				votes[i] = voteToIndexMap.get(vote);
				
				//answerCount[i] = Integer.parseInt(lineTokens[17]);
				//commentCount[i] = Integer.parseInt(lineTokens[18]);
				//favoriteCount[i] = Integer.parseInt(lineTokens[19]);

			    //Preprocess post  body
				String originalPost = lineTokens[7];
				int maxWordLength = 30;
				String preprocessedPost = preprocessPost(originalPost, stemmer, maxWordLength);
				ArrayList<String> words = new ArrayList<String>();
				FileUtil.tokenize(preprocessedPost, words);
				
				docWords[i] = new int[words.size()];
				for (int j = 0; j < words.size(); j++) {
					String wordString = words.get(j).toLowerCase().trim();
					if (!termToIndexMap.containsKey(wordString)) {
						termToIndexMap.put(wordString, termToIndexMap.size());
						indexToTermMap.add(wordString);
						termCountMap.put(wordString, new Integer(0));
					} 
					termCountMap.put(wordString, termCountMap.get(wordString) + new Integer(1));
					docWords[i][j] = termToIndexMap.get(wordString);
				}
			}
		}

		private String ProcessVote(String vote) {
			// TODO Auto-generated method stub
			/*int v = Integer.valueOf(vote);
			int bin;
			if(v < 15) {
				bin = v;
			} else if(v >= 20 && v < 1000){
				bin = (int)(v / 20) * 100;
			} else {
				bin = (int)(v / 200) * 200;
			}
			return String.valueOf(bin);*/
			return vote;
		}

		private String preprocessPost(String originalPost, Porter stemmer, int maxWordLength) {
			// TODO Auto-generated method stub
			//System.out.println("0 before preprocess : " + originalPost);
			
			//1 tokenize text
			List<String> sents = StanfordTokenizer.tokenizeSents(originalPost);
			//System.out.println("1 after tokenize: ");
			  //for(String sentence:sents) { 
			   //  System.out.print(sentence + " ");
			  //}
			 //System.out.println();
			 
			//2 discard any code snippets in posts
			boolean codeFlag = false;
			String prePost = "";
			for(String sent : sents){
				for(String word : sent.split(" ")){
					if(word.equals("<code>")){
						codeFlag = true;
					}
					if(word.equals("</code>")){
						codeFlag = false;
					}
					if(!codeFlag){
						prePost += word + " ";
					}
				}
			}
			//System.out.println("2 after remove code: " + prePost);
			
			//3 remove all HTML tags. eg. <b></b> <a href="">
			prePost = prePost.replaceAll("<[^>]*>", "");
			//System.out.println("3 after remove html tag: " + prePost);
			
			//4 remove stop words and stem text
			//Currently skip stem
			String resPost = "";
			for(String word : prePost.split(" ")){
				if(!isNoiseWord(word) && !Stopwords.isStopword(word) && word.length() < maxWordLength){//delete too long word
					//String stemedWord = stemmer.stripAffixes(word);
					resPost += word + " ";
				}
			}
			
			//System.out.println("4 remove stop words and stem text RES : " + resPost);
			
			//5 optional step: remove words which appear less than 5 times
			return resPost;
		}

		public static boolean isNoiseWord(String string) {
			// TODO Auto-generated method stub
			string = string.toLowerCase().trim();
			Pattern MY_PATTERN = Pattern.compile(".*[a-zA-Z]+.*");
			Matcher m = MY_PATTERN.matcher(string);
			// filter @xxx and URL
			if(string.matches(".*www\\..*") || string.matches(".*\\.com.*") || 
					string.matches(".*http:.*") )
				return true;
			if (!m.matches()) {
				return true;
			} else
				return false;
		}
	}

	public void deleteRareTerms(int minTimes) {
		// TODO Auto-generated method stub
		//Update term map
		Map<String, Integer> newTermToIndexMap = new HashMap<String, Integer>();
		ArrayList<String> newIndexToTermMap = new ArrayList<String>();
		Map<String, Integer> newTermCountMap = new HashMap<String, Integer>();
		
		for(String term : this.termCountMap.keySet()){
			if(this.termCountMap.get(term) >= minTimes){
				if (!newTermToIndexMap.containsKey(term)) {
					newTermToIndexMap.put(term, newTermToIndexMap.size());
					newIndexToTermMap.add(term);
					newTermCountMap.put(term, new Integer(0));
				} 
				newTermCountMap.put(term, newTermCountMap.get(term) + new Integer(1));
			}
		}
		
		//Update docWords
		for(int u = 0; u < this.docs.size(); u++){
			int[][] newDocWords = new int[this.docs.get(u).docWords.length][];
			for(int n = 0; n < this.docs.get(u).docWords.length; n++){
				if( this.docs.get(u).docWords[n] == null)  continue;
				int postL = 0;
				for(int l = 0; l < this.docs.get(u).docWords[n].length; l++){
					if(this.termCountMap.get(indexToTermMap.get(this.docs.get(u).docWords[n][l])) >= minTimes){
						postL++;
					}
				}
				int count = 0;
				newDocWords[n] = new int[postL];
				for(int l = 0; l < this.docs.get(u).docWords[n].length; l++){
					if(this.termCountMap.get(indexToTermMap.get(this.docs.get(u).docWords[n][l])) >= minTimes){
						newDocWords[n][count] = newTermToIndexMap.get(this.indexToTermMap.get(this.docs.get(u).docWords[n][l]));
						count++;
					}
				}
			}
			this.docs.get(u).docWords = newDocWords;
		}
		
		this.indexToTermMap = newIndexToTermMap;
		this.termCountMap.clear();
		this.termCountMap.clear();
		this.termToIndexMap.putAll(newTermToIndexMap);
		this.termCountMap.putAll(newTermCountMap);
	}
}
