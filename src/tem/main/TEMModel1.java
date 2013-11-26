package tem.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tem.com.ComUtil;
import tem.com.FileUtil;
import tem.conf.PathConfig;
import tem.main.ModelComFunc;
import tem.main.TEMModelSampling.modelparameters;

/**
 * Class for Topic Expertise Model
 * 
 * @author yangliu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yangliuyx@gmail.com
 */

public class TEMModel1 implements java.io.Serializable {

	FGMM fgmm;
	float[][] GMMData; // GMM data

	private static final long serialVersionUID = 1L;

	private boolean debug = false;// for convenience of debug
	private int U, K, ExpertNum, TagNum, V, S;// total number of users, topics,
												// expertise, tags, terms,
												// votes(score)

	// dirichlet priors
	private float alpha;// user specific topic distribution
	private float beta;// topic user specific expertise distribution;
	private float eta;// topic specific tag distribution
	private float gamma;// topic expertise specific word distribution
	private float xi;// expertise specific vote distribution

	// model parameters
	private float[][] theta;// U*K
	private float[][][] phi;// K*U*E
	private float[][] psi;// K*T
	private float[][][] varphi;// K*E*V
	private float[][] tau;// E*S
	private int Z[][][];// U*N*L topic label for each word
	private int E[][][];// U*N*L expertise label for each word

	// Temporary count variables while sampling
	private int CUK[][]; // U*K
	private int CUKsum[]; // U
	private int CKUE[][][]; // K*U*E
	private int CKUEsum[][]; // K*U
	private int CKT[][]; // K*T
	private int CKTsum[]; // K
	private int CKEV[][][]; // K*E*V
	private int CKEVsum[][]; // K*E
	// private int CES[][]; // E*S
	// private int CESsum[]; // E

	private int iterations;// times of iterations
	private int saveStep;// number of iterations between two saving
	private int beginSaveIters;// begin save model at this iteration

	public TEMModel1() {
		System.out.println("Construct a null model object");
	}

	public TEMModel1(modelparameters modelparam) {
		// TODO Auto-generated constructor stub
		alpha = modelparam.alpha;
		beta = modelparam.beta;
		gamma = modelparam.gamma;
		eta = modelparam.eta;
		xi = modelparam.xi;
		ExpertNum = modelparam.expertiseNum;
		K = modelparam.topicNum;

		iterations = modelparam.iteration;
		saveStep = modelparam.saveStep;
		beginSaveIters = modelparam.beginSaveIters;
	}

	public void initializeModel(Documents docSet) {
		// TODO Auto-generated method stub
		U = docSet.docs.size();
		TagNum = docSet.indexToTagMap.size();
		V = docSet.indexToTermMap.size();
		S = docSet.indexToVoteMap.size();

		// model parameters
		theta = new float[U][K];// U*K
		phi = new float[K][U][ExpertNum];// K*U*E
		psi = new float[K][TagNum];// K*T
		varphi = new float[K][ExpertNum][V];// K*E*V
		tau = new float[ExpertNum][S];// E*S

		// temporary count variables while sampling
		CUK = new int[U][K]; // U*K
		CUKsum = new int[U]; // U
		CKUE = new int[K][U][ExpertNum]; // K*U*E
		CKUEsum = new int[K][U]; // K*U
		CKT = new int[K][TagNum]; // K*T
		CKTsum = new int[K]; // K
		CKEV = new int[K][ExpertNum][V]; // K*E*V
		CKEVsum = new int[K][ExpertNum]; // K*E
		// CES = new int[ExpertNum][S]; // E*S
		// CESsum = new int[ExpertNum]; // E

		// initialize topic and expertise index
		Z = new int[U][][];// U*N*L topic label for each word
		E = new int[U][][];// U*N*L expertise label for each word
		for (int u = 0; u < docSet.docs.size(); u++) {
			Z[u] = new int[docSet.docs.get(u).docWords.length][];
			E[u] = new int[docSet.docs.get(u).docWords.length][];
			for (int n = 0; n < docSet.docs.get(u).docWords.length; n++) {
				if (docSet.docs.get(u).docWords[n] != null) {
					Z[u][n] = new int[docSet.docs.get(u).docWords[n].length];
					E[u][n] = new int[docSet.docs.get(u).docWords[n].length];
					int tag = docSet.docs.get(u).tags[n][0];
					int vote = docSet.docs.get(u).votes[n];
					for (int l = 0; l < docSet.docs.get(u).docWords[n].length; l++) {
						// randomly assign initial topic and expertise index
						int initTopic = (int) (Math.random() * K);// From 0 to K
																	// - 1
						int initExpert = (int) (Math.random() * ExpertNum);
						int term = docSet.docs.get(u).docWords[n][l];
						Z[u][n][l] = initTopic;
						E[u][n][l] = initExpert;
						CUK[u][initTopic]++;
						CUKsum[u]++;
						CKUE[initTopic][u][initExpert]++; // K*U*E
						CKUEsum[initTopic][u]++; // K*U
						CKT[initTopic][tag]++; // K*T
						CKTsum[initTopic]++; // K
						CKEV[initTopic][initExpert][term]++; // K*E*V
						CKEVsum[initTopic][initExpert]++; // K*E
						// CES[initExpert][vote]++; // E*S
						// CESsum[initExpert]++; // E
					}
				}
			}
		}

		initGMM(docSet, ExpertNum);
	}

	private void initGMM(Documents docSet, int expertNum) {
		// get data for GMM
		fgmm = new FGMM();

		for (int u = 0; u < docSet.docs.size(); u++) {
			for (int n = 0; n < docSet.docs.get(u).docWords.length; n++) {
				if (docSet.docs.get(u).docWords[n] != null)
					for (int l = 0; l < docSet.docs.get(u).docWords[n].length; l++) {
						fgmm.idmap
								.put(u + "_" + n + "_" + l, fgmm.idmap.size());
					}
			}
		}
		GMMData = new float[fgmm.idmap.size()][1];
		int[] clusterids = new int[fgmm.idmap.size()];
		int count = 0;
		for (int u = 0; u < docSet.docs.size(); u++) {
			for (int n = 0; n < docSet.docs.get(u).docWords.length; n++) {
				if (docSet.docs.get(u).docWords[n] != null) {
					float vote = Float.parseFloat(docSet.indexToVoteMap
							.get(docSet.docs.get(u).votes[n])) + 0f;
					// System.out.println(vote);
					for (int l = 0; l < docSet.docs.get(u).docWords[n].length; l++) {
						GMMData[count][0] = vote;
						clusterids[count] = E[u][n][l];
						count++;
					}
				}
			}
		}
		fgmm.init2(GMMData, expertNum, clusterids);
	}

	public void inferenceModel(Documents docSet, String minPostNum)
			throws IOException {
		// TODO Auto-generated method stub
		if (iterations < saveStep + beginSaveIters) {
			System.err
					.println("Error: the number of iterations should be larger than "
							+ (saveStep + beginSaveIters));
			System.exit(0);
		}
		for (int i = 0; i < iterations; i++) {
			if (i % 1 == 0) {
				System.out.print("\t");
				for (int k = 0; k < fgmm.ksize; k++)
					System.out.print(fgmm.clusterDataIndex.get(k).size() + " ");
				System.out.println();
				System.out.println("lambda:");
				System.out.print("\t");
				for (int k = 0; k < fgmm.ksize; k++)
					ComUtil.print(fgmm.p_lambda[k], " ", "\n\t");
				System.out.println("mu:");
				System.out.print("\t");
				for (int k = 0; k < fgmm.ksize; k++)
					ComUtil.print(fgmm.p_mu[k], " ", "\n\t");
			}
			System.out.println();
			System.out.println("Iteration " + i);
			if ((i >= beginSaveIters)
					&& (((i - beginSaveIters) % saveStep) == 0)) {
				// Saving the model
				System.out.println("Saving model at iteration " + i + " ... ");
				// Firstly update parameters
				updateEstimatedParameters();
				// Secondly print model variables
				saveIteratedModel(i, docSet, minPostNum);
			}

			if (i % 100 == 0) {
				if (!ModelComFunc.checkEqual(CUK, CUKsum, "CUK")
						|| !ModelComFunc.checkEqual(CKUE, CKUEsum, "CKUE")
						|| !ModelComFunc.checkEqual(CKT, CKTsum, "CKT")
						|| !ModelComFunc.checkEqual(CKEV, CKEVsum, "CKEV")
				// || !ModelComFunc.checkEqual(CES, CESsum, "CES")
				) {
					try {
						System.err.println("Model check equal error !");
						System.exit(0);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// Use Gibbs Sampling to update Z[][][] and E[][][]
			for (int u = 0; u < docSet.docs.size(); u++) {
				if (u % 100 == 0)
					System.out.println("\tNow u = " + u);
				for (int n = 0; n < docSet.docs.get(u).docWords.length; n++) {
					// System.out.println("\tNow u: " + u + "\tn: " + n);
					if (docSet.docs.get(u).docWords[n] == null)
						continue;
					for (int l = 0; l < docSet.docs.get(u).docWords[n].length; l++) {
						sampleTopicZandExpertE(docSet, u, n, l);
					}
				}
			}
		}
	}

	private void sampleTopicZandExpertE(Documents docSet, int u, int n, int l) {
		// TODO Auto-generated method stub
		int tag = docSet.docs.get(u).tags[n][0];
		int vote = docSet.docs.get(u).votes[n];

		// Remove old topic and expertise label for w_{u,n,l}
		int oldTopic = Z[u][n][l];
		int oldExpert = E[u][n][l];
		int term = docSet.docs.get(u).docWords[n][l];

		CUK[u][oldTopic]--;// U*K
		CUKsum[u]--;// U
		CKUE[oldTopic][u][oldExpert]--; // K*U*E
		CKUEsum[oldTopic][u]--; // K*U
		CKT[oldTopic][tag]--; // K*T
		CKTsum[oldTopic]--; // K
		CKEV[oldTopic][oldExpert][term]--; // K*E*V
		CKEVsum[oldTopic][oldExpert]--; // K*E
		// CES[oldExpert][vote]--; // E*S
		// CESsum[oldExpert]--; // E

		// compute probability based on Gibbs Updating Rule
		// sets store all the combinations of E and Z
		// then we can compute probability for all the combinations
		// of E and Z and sample e and z based on the probability
		ArrayList<int[]> sets = new ArrayList<int[]>();
		for (int i = 0; i < K; i++) {
			for (int j = 0; j < ExpertNum; j++) {
				int[] tmp = new int[2];
				tmp[0] = i;
				tmp[1] = j;
				sets.add(tmp);
			}
		}

		// get GMM data index
		int n_pos = fgmm.idmap.get(u + "_" + n + "_" + l);
		double[] probsGMM = fgmm.LearnProbs(GMMData, n_pos);

		double[] p = new double[sets.size()];
		for (int i = 0; i < sets.size(); i++) {
			int z = sets.get(i)[0];
			int e = sets.get(i)[1];
			p[i] = (CKEV[z][e][term] + gamma) / (CKEVsum[z][e] + gamma * V)
					* (CUK[u][z] + alpha) / (CUKsum[u] + alpha * K)
					* (CKUE[z][u][e] + beta)
					/ (CKUEsum[z][u] + beta * ExpertNum) * (CKT[z][tag] + eta)
					/ (CKTsum[z] + eta * TagNum);
			// * (CES[e][vote] + xi) / (CESsum[e] + xi * S);
			p[i] *= probsGMM[e];
		}

		int newNo = ComUtil.sample(p, p.length);

		// update new mu and lambda
		fgmm.UpdateProbs(GMMData, n_pos, sets.get(newNo)[1]);

		// Add new topic and expert label for w_{u, n, l}
		int newTopic = sets.get(newNo)[0];
		int newExpert = sets.get(newNo)[1];
		// System.out.println("now for (u,n,l) = " + u + "\t" + n + "\t" + l);
		// System.out.println("newTopic and new Expertise are: " + newTopic +
		// "\t" + newExpert);
		Z[u][n][l] = newTopic;
		E[u][n][l] = newExpert;
		CUK[u][newTopic]++;// U*K
		CUKsum[u]++;// U
		CKUE[newTopic][u][newExpert]++; // K*U*E
		CKUEsum[newTopic][u]++; // K*U
		CKT[newTopic][tag]++; // K*T
		CKTsum[newTopic]++; // K
		CKEV[newTopic][newExpert][term]++; // K*E*V
		CKEVsum[newTopic][newExpert]++; // K*E
		// CES[newExpert][vote]++; // E*S
		// CESsum[newExpert]++; // E
	}

	private void updateEstimatedParameters() {
		// TODO Auto-generated method stub

		for (int u = 0; u < U; u++) {
			for (int k = 0; k < K; k++) {
				theta[u][k] = (CUK[u][k] + alpha) / (CUKsum[u] + alpha * K);
			}
		}

		for (int k = 0; k < K; k++) {
			for (int u = 0; u < U; u++) {
				for (int e = 0; e < ExpertNum; e++) {
					phi[k][u][e] = (CKUE[k][u][e] + beta)
							/ (CKUEsum[k][u] + beta * ExpertNum);
				}
			}
		}

		for (int k = 0; k < K; k++) {
			for (int tag = 0; tag < TagNum; tag++) {
				psi[k][tag] = (CKT[k][tag] + eta) / (CKTsum[k] + eta * TagNum);
			}
		}

		for (int k = 0; k < K; k++) {
			for (int e = 0; e < ExpertNum; e++) {
				for (int term = 0; term < V; term++) {
					varphi[k][e][term] = (CKEV[k][e][term] + gamma)
							/ (CKEVsum[k][e] + gamma * V);
				}
			}
		}

		// for (int e = 0; e < ExpertNum; e++) {
		// for (int vote = 0; vote < S; vote++) {
		// tau[e][vote] = (CES[e][vote] + xi) / (CESsum[e] + xi * S);
		// }
		// }
	}

	public void saveIteratedModel(int iteration, Documents docSet,
			String minPostNum) throws IOException {
		// TODO Auto-generated method stub
		// model.params model.theta model.phi model.psi model.varphi model.tau
		String resPath = PathConfig.modelResPath + "USER" + minPostNum
				+ "/model_" + iteration;
		FileUtil.write2DArray(theta, resPath + ".theta");
		FileUtil.write3DArray(phi, resPath + ".phi");
		FileUtil.write2DArray(psi, resPath + ".psi");
		// FileUtil.write3DArray(varphi, resPath + ".varphi"); //Currently don't
		// store varphi which is too big
		FileUtil.write2DArray(tau, resPath + ".tau");

		// model.zassign

		// model.eassign

		int topNum = 20;
		// model.zeterms
		ArrayList<String> zetermsLines = new ArrayList<String>();
		for (int i = 0; i < K; i++) {
			for (int j = 0; j < ExpertNum; j++) {
				List<Integer> tWordsIndexArray = new ArrayList<Integer>();
				for (int w = 0; w < V; w++) {
					tWordsIndexArray.add(new Integer(w));
				}
				Collections.sort(tWordsIndexArray,
						new TEMModel1.TwordsComparable(varphi[i][j]));
				String line = "topic=" + i + "\texpert=" + j + "\t";
				for (int w = 0; w < topNum; w++) {
					line += docSet.indexToTermMap.get(tWordsIndexArray.get(w))
							+ "\t";
				}
				zetermsLines.add(line);
			}
		}
		FileUtil.writeLines(resPath + ".zeterms", zetermsLines);

		// model.evotes
		ArrayList<String> evotesLines = new ArrayList<String>();
		for (int i = 0; i < ExpertNum; i++) {
			List<Integer> tWordsIndexArray = new ArrayList<Integer>();
			for (int s = 0; s < S; s++) {
				tWordsIndexArray.add(new Integer(s));
			}
			Collections.sort(tWordsIndexArray, new TEMModel1.TwordsComparable(
					tau[i]));
			String line = "expert=" + i + "\t";
			for (int s = 0; s < topNum; s++) {
				line += docSet.indexToVoteMap.get(tWordsIndexArray.get(s))
						+ "\t";
			}
			evotesLines.add(line);
		}
		FileUtil.writeLines(resPath + ".evotes", evotesLines);

		// model.ztags
		ArrayList<String> ztagsLines = new ArrayList<String>();
		for (int i = 0; i < K; i++) {
			List<Integer> tWordsIndexArray = new ArrayList<Integer>();
			for (int t = 0; t < TagNum; t++) {
				tWordsIndexArray.add(new Integer(t));
			}
			Collections.sort(tWordsIndexArray, new TEMModel1.TwordsComparable(
					psi[i]));
			String line = "topic=" + i + "\t";
			for (int t = 0; t < topNum; t++) {
				line += docSet.indexToTagMap.get(tWordsIndexArray.get(t))
						+ "\t";
			}
			ztagsLines.add(line);
		}
		FileUtil.writeLines(resPath + ".ztags", ztagsLines);
	}

	public class TwordsComparable implements Comparator<Integer> {
		public float[] sortProb; // Store probability of each word in topic k

		public TwordsComparable(float[] sortProb) {
			this.sortProb = sortProb;
		}

		@Override
		public int compare(Integer o1, Integer o2) {
			// TODO Auto-generated method stub
			// Sort topic word index according to the probability of each word
			// in topic k
			if (sortProb[o1] > sortProb[o2])
				return -1;
			else if (sortProb[o1] < sortProb[o2])
				return 1;
			else
				return 0;
		}
	}
}
