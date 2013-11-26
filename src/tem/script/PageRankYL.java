package tem.script;

/**Standard PageRank Algorithm
 * @author yangliu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yangliuyx@gmail.com
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PageRankYL {
	private static final double LAMBDA = 0.5;
	private static final double THRESHOLD = 0.0000001;

	public static void main(String[] args) {
		System.out.println("lambda is " + LAMBDA);
		List<Double> PR0 = new ArrayList<Double>();
		//Both randomly initialise or set all 1 are OK
		//PR0 = getInitPR0(3);
		PR0.add(new Double(1));
		PR0.add(new Double(1));
		PR0.add(new Double(1));
		System.out.println("Initial state vector PR0 is:");
		printVec(PR0);
		System.out.println("Page Rank Update Matrix newPR:");
		printMatrix(getNewPR(LAMBDA));
		List<Double> pageRank = calPageRank(PR0, LAMBDA);
		System.out.println("Final PageRank is:");
		printVec(pageRank);
		System.out.println();
	}

	public static void printMatrix(List<List<Double>> m) {
		for (int i = 0; i < m.size(); i++) {
			for (int j = 0; j < m.get(i).size(); j++) {
				System.out.print(m.get(i).get(j) + ", ");
			}
			System.out.println();
		}
	}

	
	public static void printVec(List<Double> v) {
		for (int i = 0; i < v.size(); i++) {
			System.out.print(v.get(i) + ", ");
		}
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
	public static double calDistance(List<Double> q1, List<Double> q2) {
		double sum = 0;

		if (q1.size() != q2.size()) {
			return -1;
		}

		for (int i = 0; i < q1.size(); i++) {
			sum += Math.pow(q1.get(i).doubleValue() - q2.get(i).doubleValue(),
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
	public static List<Double> calPageRank(List<Double> PR0, double lambda) {

		List<List<Double>> newPR = getNewPR(lambda);
		List<Double> PR = null;
		while (true) {
			PR = vectorMulMatrix(PR0, newPR);
			double dis = calDistance(PR, PR0);//PR0 store PR vector after last iteration
			System.out.println("distance:" + dis);
			if (dis <= THRESHOLD) {
				System.out.println("PR0:");
				printVec(PR0);
				System.out.println("PR:");
				printVec(PR);
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
	public static List<List<Double>> getNewPR(double lambda) {

		int V = getM().size();
		List<List<Double>> add1 = numberMulMatrix(getM(), lambda);
		List<List<Double>> add2 = numberMulMatrix(getU(), (1 - lambda) / V);
		List<List<Double>> newPR = addMatrix(add1, add2);
		return newPR;
	}

	/**
	 * Compute the product of a vector and a matrix
	 * 
	 * @param v
	 *            a vector
	 * @param m
	 *            a matrix
	 * @return a new vector
	 */
	public static List<Double> vectorMulMatrix(List<Double> v, List<List<Double>> m
			) {
		
		if (m == null || v == null || m.size() <= 0
				|| m.get(0).size() != v.size()) {
			return null;
		}

		List<Double> list = new ArrayList<Double>();
		for(int i = 0; i < v.size(); i++){
			double sum = 0;
			for(int j = 0; j < m.size(); j++){
				sum += v.get(j) * m.get(j).get(i);
			}
			list.add(sum);
		}

		return list;
	}

	
	public static List<List<Double>> addMatrix(List<List<Double>> list1,
			List<List<Double>> list2) {
		List<List<Double>> list = new ArrayList<List<Double>>();
		if (list1.size() != list2.size() || list1.size() <= 0
				|| list2.size() <= 0) {
			return null;
		}
		for (int i = 0; i < list1.size(); i++) {
			list.add(new ArrayList<Double>());
			for (int j = 0; j < list1.get(i).size(); j++) {
				double temp = list1.get(i).get(j).doubleValue()
						+ list2.get(i).get(j).doubleValue();
				list.get(i).add(new Double(temp));
			}
		}
		return list;
	}

	
	public static List<List<Double>> numberMulMatrix(List<List<Double>> s,
			double a) {
		List<List<Double>> list = new ArrayList<List<Double>>();

		for (int i = 0; i < s.size(); i++) {
			list.add(new ArrayList<Double>());
			for (int j = 0; j < s.get(i).size(); j++) {
				double temp = a * s.get(i).get(j).doubleValue();
				list.get(i).add(new Double(temp));
			}
		}
		return list;
	}

	/**
	 * Initialise transition matrix M
	 * 
	 * @return M
	 */
	public static List<List<Double>> getM() {
		List<Double> row1 = new ArrayList<Double>();
		row1.add(new Double(0));
		row1.add(new Double(1));
		row1.add(new Double(0));
		List<Double> row2 = new ArrayList<Double>();
		row2.add(new Double(0.5));
		row2.add(new Double(0));
		row2.add(new Double(0.5));
		List<Double> row3 = new ArrayList<Double>();
		row3.add(new Double(0));
		row3.add(new Double(1));
		row3.add(new Double(0));
		
		List<List<Double>> M = new ArrayList<List<Double>>();
		M.add(row1);
		M.add(row2);
		M.add(row3);

		return M;
	}

	/**
	 * Initialise Matrix U
	 * 
	 * @return U
	 */
	public static List<List<Double>> getU() {
		List<Double> row1 = new ArrayList<Double>();
		row1.add(new Double(1));
		row1.add(new Double(1));
		row1.add(new Double(1));
		List<Double> row2 = new ArrayList<Double>();
		row2.add(new Double(1));
		row2.add(new Double(1));
		row2.add(new Double(1));
		List<Double> row3 = new ArrayList<Double>();
		row3.add(new Double(1));
		row3.add(new Double(1));
		row3.add(new Double(1));
		
		List<List<Double>> u = new ArrayList<List<Double>>();
		u.add(row1);
		u.add(row2);
		u.add(row3);

		return u;
	}
}
