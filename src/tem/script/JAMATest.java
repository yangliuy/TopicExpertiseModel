package tem.script;

/**Standard PageRank Algorithm
 * @author yangliu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yangliuyx@gmail.com
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Jama.Matrix;  

public class JAMATest {
	private static final double LAMBDA = 0.5;
	private static final double THRESHOLD = 0.0000001;

	public static void main(String[] args) {
		System.out.println("lambda is " + LAMBDA);
		//Both randomly initialise or set all 1 are OK
		//PR0 = getInitPR0(3);
		double[] PR0Array = new double[3];
		for(int i = 0; i < 3; i++){
			PR0Array[i] = 1;
		}
		
		Matrix  PR0 = new Matrix (PR0Array, 1);
		System.out.println("Initial state vector PR0 is:");
		PR0.print(3, 3);
		
		System.out.println("Page Rank Update Matrix newPR:");
		getNewPR(LAMBDA).print(3, 3);
		
		Matrix pageRank = calPageRank(PR0, LAMBDA);
		System.out.println("Final PageRank is:");
		pageRank.print(3, 3);
		System.out.println();
	}

	/**
	 * Randomly Initialise state vector PR0
	 * 
	 * @param n
	 *            dimension of vector PR0
	 * @return A random vector, each dimension is 0-5
	 */
	public static List<Double> getInitPR0(int n) {
		Random random = new Random();
		List<Double> q = new ArrayList<Double>();
		for (int i = 0; i < n; i++) {
			q.add(new Double(5 * random.nextDouble()));
		}
		return q;
	}

	/**
	 * Compute Euclidean Distance
	 * 
	 * @param q1
	 *           
	 * @param q2
	 *           
	 * @return distance
	 */
	public static double calDistance(Matrix q1, Matrix q2) {
		double sum = 0;

		if (q1.getColumnDimension() != q2.getColumnDimension() ) {
			return -1;
		}

		for (int i = 0; i < q1.getColumnDimension() ; i++) {
			sum += Math.pow(q1.get(0, i) - q2.get(0, i),
					2);
		}
		return Math.sqrt(sum);
	}

	/**
	 * compute pagerank
	 * 
	 * @param PR0
	 *            Initialise state vector
	 * @param lambda
	 *            lambda
	 * @return pagerank result
	 */
	public static Matrix calPageRank(Matrix PR0, double lambda) {

		Matrix newPR = getNewPR(lambda);
		Matrix PR;
		while (true) {
			PR = PR0.times(newPR);
			double dis = calDistance(PR, PR0);//PR0 store PR vector after last iteration
			System.out.println("distance:" + dis);
			if (dis <= THRESHOLD) {
				System.out.println("PR:");
				PR.print(3, 3);
				break;
			}
			PR0 = PR;
		}
		return PR;
	}

	/**
	 * compute NEWPR matrix
	 * 
	 * @param lambda
	 *            
	 * @return NEWPR matrix
	 */
	public static Matrix getNewPR(double lambda) {

		int V = getM().getColumnDimension();
		Matrix add1 = getM().times(lambda);
		Matrix add2 = getU().times((1 - lambda) / V);
		Matrix newPR = add1.plus(add2);
		return newPR;
	}

	/**
	 * Initialise transition matrix M
	 * 
	 * @return M
	 */
	public static Matrix getM() {
		double[][] m = new double[3][3];
		
		m[0][0] = 0;
		m[0][1] = 1;
		m[0][2] = 0;
		
		m[1][0] = 0.5;
		m[1][1] = 0;
		m[1][2] = 0.5;
		
		m[2][0] = 0;
		m[2][1] = 1;
		m[2][2] = 0;

		Matrix M = new Matrix(m);

		return M;
	}

	/**
	 * Initialise Matrix U
	 * 
	 * @return U
	 */
	public static Matrix getU() {
		
		double[][] u = new double[3][3];
		
		u[0][0] = 1;
		u[0][1] = 1;
		u[0][2] = 1;
		
		u[1][0] = 1;
		u[1][1] = 1;
		u[1][2] = 1;
		
		u[2][0] = 1;
		u[2][1] = 1;
		u[2][2] = 1;

		Matrix U = new Matrix(u);

		return U;
	}
}
