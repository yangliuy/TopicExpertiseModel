package tem.main;

/**
 * Finite Gaussian Mixture Model
 * 
 * @author Minghui
 */

import java.util.ArrayList;
import java.util.HashMap;

import tem.com.ComUtil;
import tem.com.MathUtil;
import tem.com.MatrixUtil;

public class FGMM implements java.io.Serializable {
	public double[] mu0;
	public int k0;
	public double alpha0;
	public double[] beta0;
	public double alpha;

	public double[][] p_mu;
	public double[][] p_lambda;
	public double[][] avgx; // k * d: k clusters, d dimensions
	public double[][] derv; // k * d: \sum_i (x_i - avgx)^2
	public double[][] diff; // k * d: \sum_i (x_i - avgx)

	public int ksize;
	public int[] clusterID;
	public double[] cz;
	public ArrayList<ArrayList<Integer>> clusterDataIndex;

	public int vector_n;
	public int vector_d;
	public HashMap<String, Integer> idmap;

	// for TEM model
	public void init2(float[][] data, int inputK, int[] clusterids) {
		vector_d = data[0].length;
		vector_n = data.length;

		System.out.println("vector_d n " + vector_d + " " + vector_n);

		ksize = inputK;
		p_mu = new double[ksize][vector_d];
		p_lambda = new double[ksize][vector_d];
		// initialize parameters
		// mu0: mean of all the data
		mu0 = new double[vector_d];

		for (int i = 0; i < vector_d; i++)
			mu0[i] = MatrixUtil.sumCol(data, i) / vector_n;
		k0 = 1;
		alpha0 = vector_d;
		// mean of distance between a pair of data points
		beta0 = computeMeanDist(data);

		// random assign clusterID
		clusterID = clusterids.clone();
		clusterDataIndex = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < ksize; i++) {
			ArrayList<Integer> a = new ArrayList<Integer>();
			clusterDataIndex.add(a);
		}

		for (int i = 0; i < vector_n; i++) {
			int id = (int) (Math.floor(Math.random() * ksize));
			clusterID[i] = id;
			clusterDataIndex.get(id).add(i);
		}

		System.out.print("\t" + vector_n + ":\t");
		for (int i = 0; i < ksize; i++)
			System.out.print(clusterDataIndex.get(i).size() + " ");
		System.out.println();

		// update p_mu p_lambda
		System.out.print("init p_mu and p_lambda");
		avgx = new double[p_mu.length][vector_d];
		derv = new double[p_mu.length][vector_d];
		diff = new double[p_mu.length][vector_d];
		for (int i = 0; i < ksize; i++) {
			comAVGX(data, i);
			comDateMus(p_mu, p_lambda, data, clusterDataIndex, i);
		}
	}

	public double[] LearnProbs(float[][] data, int n) {
		double[] probs = new double[ksize];

		int currentz = clusterID[n];
		// decrease
		int toRemoveIndex = clusterDataIndex.get(currentz).indexOf(n);
		clusterDataIndex.get(currentz).remove(toRemoveIndex);

		// update mu lambda for currentz
		comAVGX(data, currentz, n, false);
		comDateMus(p_mu, p_lambda, data, clusterDataIndex, currentz);

		// computeProbs
		for (int k = 0; k < ksize; k++) {
			probs[k] = 1;
			for (int d = 0; d < vector_d; d++)
				probs[k] *= MathUtil.phi(data[n][d], p_mu[k][d],
						Math.sqrt(1 / p_lambda[k][d]));
			if (probs[k] < 1e-300) {
				probs[k] = 1e-300;
			}
		}

		return probs;
	}

	// // for learn new data
	// public void init(float[][] data, int inputK, double galpha) {
	// System.out.println("Building a FGMM");
	// vector_d = data[0].length;
	// vector_n = data.length;
	//
	// alpha = galpha;
	//
	// ksize = inputK;
	// p_mu = new double[ksize][vector_d];
	// p_lambda = new double[ksize][vector_d];
	// // initialize parameters
	// // mu0: mean of all the data
	// mu0 = new double[vector_d];
	//
	// for (int i = 0; i < vector_d; i++)
	// mu0[i] = MatrixUtil.sumCol(data, i) / vector_n;
	// k0 = 1;
	// alpha0 = vector_d;
	// // mean of distance between a pair of data points
	// beta0 = computeMeanDist(data);
	//
	// // random assign clusterID
	// clusterID = new int[vector_n];
	// clusterDataIndex = new ArrayList<ArrayList<Integer>>();
	// for (int i = 0; i < ksize; i++) {
	// ArrayList<Integer> a = new ArrayList<Integer>();
	// clusterDataIndex.add(a);
	// }
	//
	// for (int i = 0; i < vector_n; i++) {
	// int id = (int) (Math.floor(Math.random() * ksize));
	// clusterID[i] = id;
	// clusterDataIndex.get(id).add(i);
	// }
	//
	// System.out.println(vector_n);
	// for (int i = 0; i < ksize; i++)
	// System.out.print(clusterDataIndex.get(i).size() + " ");
	// System.out.println();
	//
	// // update p_mu p_lambda
	// avgx = new double[p_mu.length][vector_d];
	// for (int i = 0; i < ksize; i++) {
	// comAVGX(data, i);
	// comDateMus(p_mu, p_lambda, data, clusterDataIndex, i);
	// }
	// // updateMus(data, mu0, k0, alpha0, beta0);
	// }

	private void comAVGX(float[][] data, int index) {
		// compute avgx, derv, diff for the first time, increment update them
		// onwards
		int count = clusterDataIndex.get(index).size();
		// compute avgx[index]
		for (int i = 0; i < count; i++) {
			int id = clusterDataIndex.get(index).get(i);
			for (int d = 0; d < vector_d; d++) {
				avgx[index][d] += data[id][d];
			}
		}
		for (int d = 0; d < vector_d; d++)
			avgx[index][d] = avgx[index][d]
					/ clusterDataIndex.get(index).size();
		// derv, diff
		for (int i = 0; i < count; i++) {
			int id = clusterDataIndex.get(index).get(i);
			for (int d = 0; d < vector_d; d++) {
				derv[index][d] += Math.pow(data[id][d] - avgx[index][d], 2);
				diff[index][d] += data[id][d] - avgx[index][d];
			}
		}
		System.out.println("computing Derv and diff for cluster " + index);
	}

	private void comAVGX(float[][] data, int index, int n, boolean flag) {
		// flag = false: decrease, true: increase
		int count = clusterDataIndex.get(index).size();
		double[] deltax = new double[vector_d];
		// compute avgx[index]
		if (!flag) {
			for (int d = 0; d < vector_d; d++) {
				double tmp = avgx[index][d];
				avgx[index][d] = (tmp * (count + 1) - data[n][d]) / count;
				deltax[d] = tmp - avgx[index][d];
			}
			for (int d = 0; d < vector_d; d++) {
				derv[index][d] = derv[index][d] + diff[index][d] * 2
						* deltax[d] + Math.pow(deltax[d], 2) * (count + 1)
						- Math.pow(data[n][d] - avgx[index][d], 2);
				diff[index][d] = diff[index][d] + deltax[d] * (count + 1)
						- (data[n][d] - avgx[index][d]);
			}
		} else {
			for (int d = 0; d < vector_d; d++) {
				double tmp = avgx[index][d];
				avgx[index][d] = (tmp * (count - 1) + data[n][d]) / count;
				deltax[d] = tmp - avgx[index][d];
			}
			for (int d = 0; d < vector_d; d++) {
				derv[index][d] = derv[index][d] + diff[index][d] * 2
						* deltax[d] + Math.pow(deltax[d], 2) * (count - 1)
						+ Math.pow(data[n][d] - avgx[index][d], 2);
				diff[index][d] = diff[index][d] + deltax[d] * (count - 1)
						+ (data[n][d] - avgx[index][d]);
			}
		}

		//
		// double[] tmpderv = new double[vector_d];
		// double[] tmpdiff = new double[vector_d];
		// for (int i = 0; i < count; i++) {
		// int id = clusterDataIndex.get(index).get(i);
		// for (int d = 0; d < vector_d; d++) {
		// tmpderv[d] += Math.pow(data[id][d] - avgx[index][d], 2);
		// tmpdiff[d] += data[id][d] - avgx[index][d];
		// }
		// }
		// System.out.println(flag);
		// System.out.print("derv ");
		// for (int d = 0; d < vector_d; d++) {
		// System.out.print(tmpderv[d] + ":" + derv[index][d] + " ");
		// }
		// System.out.println();
		// System.out.print("diff ");
		// for (int d = 0; d < vector_d; d++) {
		// System.out.print(tmpdiff[d] + ":" + diff[index][d] + " ");
		// }
		// System.out.println();
		// System.out.print("deltax ");
		// for (int d = 0; d < vector_d; d++) {
		// System.out.print((deltax[d] * count) + ":"
		// + (data[n][d] - avgx[index][d]) + " ");
		// }
		// System.out.println();
		// System.out.print("deltaxsss ");
		// for (int d = 0; d < vector_d; d++) {
		// System.out.print((diff[index][d] * 2 * deltax[d]) + ":"
		// + (Math.pow(deltax[d], 2)) + ":"
		// + Math.pow(data[n][d] - avgx[index][d], 2) + " ");
		// }
		// System.out.println();
	}

	public void UpdateProbs(float[][] data, int n, int newz) {
		// increase
		clusterID[n] = newz;
		clusterDataIndex.get(newz).add(n);
		// update mu lambda for currentz
		comAVGX(data, newz, n, true);
		comDateMus(p_mu, p_lambda, data, clusterDataIndex, newz);
	}

	public FGMM() {
		System.out.println("\tBuilding a FGMM");
		idmap = new HashMap<String, Integer>();
	}

	private void comDateMus(double[][] p_mu, double[][] p_lambda,
			float[][] data, ArrayList<ArrayList<Integer>> clusterDataIndex,
			int index) {
		int count = clusterDataIndex.get(index).size();

		// update cluster index
		if (clusterDataIndex.get(index).size() < 1) {
			p_mu[index] = mu0.clone();
			for (int d = 0; d < vector_d; d++)
				p_lambda[index][d] = alpha0 / beta0[d];
		} else {
			// update p_mu
			for (int d = 0; d < vector_d; d++) {
				p_mu[index][d] = (k0 * mu0[d] + count * avgx[index][d])
						/ (k0 + count);
			}
			double alphan = alpha0 + count / 2;
			// betan = beta0 + 1/2*sum((data - repmat(avgx, count,1)).^2, 1) ...
			// + k0*count*(avgx - mu0).^2/2/(k0+count)
			double[] betan = beta0.clone();
			for (int d = 0; d < vector_d; d++)
				betan[d] += k0 * count * Math.pow(avgx[index][d] - mu0[d], 2)
						/ 2 / (k0 + count);
			// for (int n = 0; n < count; n++) {
			// int id = clusterDataIndex.get(index).get(n);
			// for (int d = 0; d < vector_d; d++)
			// betan[d] += Math.pow(data[id][d] - avgx[index][d], 2) / 2;
			// }
			// using the incremental updated derv and diff to compute betan
			for (int d = 0; d < vector_d; d++) {
				betan[d] += (derv[index][d] / 2);
			}
			// update lambda
			for (int d = 0; d < vector_d; d++)
				p_lambda[index][d] = alphan / betan[d];
		}
	}

	private double[] computeMeanDist(float[][] data) {
		System.out.println("computeMeanDist");
		double dist[] = new double[vector_d];
		int count = 0;
		int set[] = new int[100];

		MatrixUtil.randperm(set, vector_n, 100);

		for (int i = 0; i < set.length; i++) {
			for (int j = i + 1; j < set.length; j++) {
				count++;
				for (int d = 0; d < vector_d; d++)
					dist[d] += MatrixUtil.dist(data[i][d], data[j][d],
							"Euclidean");
			}
		}
		for (int d = 0; d < vector_d; d++) {
			dist[d] = dist[d] / count;
		}
		return dist;
	}

	public void learn2(float[][] data, int iteration) {
		for (int iter = 0; iter < iteration; iter++) {
			if (iter % 10 == 0) {
				System.out.print("Iteration " + iter + "\t");
				for (int i = 0; i < ksize; i++)
					System.out.print(clusterDataIndex.get(i).size() + " ");
				System.out.println();
				System.out.println("lambda:");
				for (int k = 0; k < ksize; k++)
					ComUtil.print(p_lambda[k], " ", "\n");
				System.out.println("mu:");
				for (int k = 0; k < ksize; k++)
					ComUtil.print(p_mu[k], " ", "\n");
			}

			// tranverse data
			for (int n = 0; n < data.length; n++) {
				double[] probs = new double[ksize];

				// decrease
				double[] probsGMM = LearnProbs(data, n);

				// computeProbs
				for (int k = 0; k < ksize; k++) {
					probs[k] = (clusterDataIndex.get(k).size() + alpha)
							/ (vector_n + ksize * alpha);
					for (int d = 0; d < vector_d; d++)
						probs[k] *= probsGMM[k];
				}

				int newz = ComUtil.sample(probs, ksize);
				// increase
				UpdateProbs(data, n, newz);
			}
		}
	}

	public void learn(float[][] data, int iteration) {
		for (int iter = 0; iter < iteration; iter++) {
			if (iter % 10 == 0) {
				System.out.print("Iteration " + iter + "\t");
				for (int i = 0; i < ksize; i++)
					System.out.print(clusterDataIndex.get(i).size() + " ");
				System.out.println();
				System.out.println("mu:");
				for (int k = 0; k < ksize; k++)
					ComUtil.print(p_mu[k], " ", "\n");
				System.out.println("lambda:");
				for (int k = 0; k < ksize; k++)
					ComUtil.print(p_lambda[k], " ", "\n");
			}

			// tranverse data
			for (int n = 0; n < data.length; n++) {
				double[] probs = new double[ksize];

				int currentz = clusterID[n];
				// decrease
				int toRemoveIndex = clusterDataIndex.get(currentz).indexOf(n);
				clusterDataIndex.get(currentz).remove(toRemoveIndex);
				// update mu lambda for currentz
				comAVGX(data, currentz);
				comDateMus(p_mu, p_lambda, data, clusterDataIndex, currentz);

				// computeProbs
				for (int k = 0; k < ksize; k++) {
					probs[k] = (clusterDataIndex.get(k).size() + alpha)
							/ (vector_n + ksize * alpha);
					for (int d = 0; d < vector_d; d++)
						probs[k] *= MathUtil.phi(data[n][d], p_mu[k][d],
								Math.sqrt(1 / p_lambda[k][d]));
				}

				int newz = ComUtil.sample(probs, ksize);
				// increase
				clusterID[n] = newz;
				clusterDataIndex.get(newz).add(n);
				// update mu lambda for currentz
				comAVGX(data, newz);
				comDateMus(p_mu, p_lambda, data, clusterDataIndex, newz);
			}
		}
	}
}
