package tem.main;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import tem.com.MatrixUtil;

public class ModelComFunc {

	public static void writeData(float[] array, ArrayList<String> strings,
			ArrayList<Integer> rankList, BufferedWriter writer, String prefix) {
		PrintWriter writer2 = new PrintWriter(writer);
		for (int row = 0; row < rankList.size(); row++) {
			writer2.printf("%s\t%s\t%f\n", prefix,
					strings.get(rankList.get(row)), array[rankList.get(row)]);
			// writer2.printf(prefix + "\t",
			// strings.get(rankList.get(row)) + "\t" + array[rankList.get(row)]
			// + "\n");
		}
	}

	// public static void writeData(ArrayList<Integer>[] cNP2, BufferedWriter
	// writer) {
	// PrintWriter writer2 = new PrintWriter(writer);
	// writer2 = new PrintWriter(writer);
	// for (int i = 0; i < cNP2.length; i++) {
	// // writer2.printf("%d-th topic:\n", i);
	// for (int j = 0; j < cNP2[i].size(); j++) {
	// // writer2.printf("%s,\t", Doc.getNps().get(cNP2[i].get(j)));
	// }
	// writer2.print("\n\n");
	// }
	// }

	public static void writeData(float[] pi, BufferedWriter writer) {
		PrintWriter writer2 = new PrintWriter(writer);
		for (int row = 0; row < pi.length; row++) {
			writer2.printf("\t%f", pi[row]);
		}
	}

	public static void writeData(int[][] phi2, PrintWriter writer2) {
		for (int row = 0; row < phi2.length; row++) {
			// writer2.printf("%d", row);
			for (int col = 0; col < phi2[row].length; col++) {
				writer2.printf("%d\t", phi2[row][col]);
				// writer2.printf(phi2[row][col] + "\t");
			}
			writer2.print("\n");
		}
	}

	public static void writeData(float[][] array, BufferedWriter writer) {
		PrintWriter writer2 = new PrintWriter(writer);
		for (int row = 0; row < array.length; row++) {
			// writer2.printf("%d\t", row);
			for (int col = 0; col < array[row].length; col++) {
				writer2.printf("%f\t", array[row][col]);
				// writer2.printf(array[row][col] + "\t");
			}
			writer2.print("\n");
		}
	}

	public static void writeData(double[][] vph2, BufferedWriter writer) {
		PrintWriter writer2 = new PrintWriter(writer);
		for (int row = 0; row < vph2.length; row++) {
			// writer2.printf("%d", row);
			for (int col = 0; col < vph2[row].length; col++) {
				writer2.printf("\t%f", vph2[row][col]);
				// writer2.printf("\t" + vph2[row][col]);
			}
			writer2.print("\n");
		}
	}

	public static void writeData(float[][][] a, BufferedWriter writer) {
		PrintWriter writer2 = new PrintWriter(writer);
		for (int i = 0; i < a.length; i++)
			for (int row = 0; row < a[i].length; row++) {
				writer2.printf("%d\t%d", i, row);
				for (int col = 0; col < a[i][row].length; col++) {
					writer2.printf("\t%f", a[i][row][col]);
				}
				writer2.print("\n");
			}
	}

	public static void writeData(float[][][][] data, BufferedWriter writer) {
		PrintWriter writer2 = new PrintWriter(writer);
		for (int d = 0; d < data.length; d++)
			for (int a = 0; a < data[d].length; a++)
				for (int s = 0; s < data[d][a].length; s++) {
					writer2.printf("%d\t%d\t%d", d, a, s);
					for (int w = 0; w < data[d][a][s].length; w++) {
						writer2.printf("\t%f", data[d][a][s][w]);
					}
					writer2.print("\n");
				}
	}

	public static void writeData(ArrayList<ArrayList<Integer>> rankLists,
			ArrayList<ArrayList<Float>> probs, ArrayList<String> uniWordMap,
			ArrayList<String> names, BufferedWriter writer, String string)
			throws Exception {
		// string: "\t"
		// names.get(0) names.get(1) ...
		// w11:probs11 w12:probs12 ...
		// w21:probs21 w21:probs22 ...
		// rankLists.get(0) rankLists.get(1)
		int maxsize = rankLists.get(0).size();
		for (int i = 0; i < rankLists.size(); i++) {
			// get max size
			if (rankLists.get(i).size() > maxsize)
				maxsize = rankLists.get(i).size();
		}
		for (int i = 0; i < names.size(); i++) {
			writer.write(names.get(i) + string + string);
		}
		writer.write("\n");

		for (int j = 0; j < maxsize; j++) {
			for (int i = 0; i < rankLists.size(); i++) {
				if (rankLists.get(i).size() > j && probs.get(i).size() > j) {
					writer.write(uniWordMap.get(rankLists.get(i).get(j))
							+ string + probs.get(i).get(j) + string);
				} else
					writer.write("null" + string + "0" + string);
			}
			writer.write("\n");
		}
	}

	public static boolean checkEqual(int[][][][] a, int[][][] b, String string) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				for (int k = 0; k < a[i][j].length; k++) {
					if (IsLessThanZero(a[i][j][k]))
						return false;
				}
			}
		}
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b[i].length; j++) {
				if (IsLessThanZero(b[i][j]))
					return false;
			}
		}
		for (int k = 0; k < a.length; k++) {
			for (int i = 0; i < a[k].length; i++) {
				for (int j = 0; j < a[k][i].length; j++) {
					double c = MatrixUtil.sumRow(a[k][i], j);
					if (c != b[k][i][j]) {
						System.out.println(string + "\t" + c + "\t" + b[i]);
						return false;
					}
				}
			}
		}
		return true;
	}

	public static boolean checkEqual(int[][][] a, int[][] b, String string) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				if (IsLessThanZero(a[i][j]))
					return false;
			}
		}
		for (int i = 0; i < b.length; i++) {
			if (IsLessThanZero(b[i]))
				return false;
		}
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				double c = MatrixUtil.sumRow(a[i], j);
				if (c != b[i][j]) {
					System.out.println(string + "\t" + c + "\t" + b[i]);
					return false;
				}
			}
		}
		return true;
	}

	static boolean checkEqual(int[][] a, int[] b, String string) {

		for (int i = 0; i < a.length; i++) {
			if (IsLessThanZero(a[i]))
				return false;
		}
		if (IsLessThanZero(b))
			return false;
		for (int i = 0; i < a.length; i++) {
			double c = MatrixUtil.sumRow(a, i);
			if (c != b[i]) {
				System.out.println(string + "\t" + c + "\t" + b[i]);
				return false;
			}
		}
		return true;
	}

	private boolean checkEqual(double a, double b, String string) {
		if (a < 0 || b < 0)
			return false;
		if (a != b) {
			System.out.println(string + "\t" + a + "\t" + b);
			return false;
		} else {
			return true;
		}
	}

	public static boolean checkEqual(int[] a, int b, String string) {
		if (IsLessThanZero(a) || b < 0)
			return false;
		double c = MatrixUtil.sum(a);
		if (c != b) {
			System.out.println(string + "\t" + c + "\t" + b);
			return false;
		}
		return true;
	}

	private static boolean IsLessThanZero(int[] b) {
		for (int i = 0; i < b.length; i++) {
			if (b[i] < 0)
				return true;
		}
		return false;
	}

	private static boolean IsLessThanZero(double[] b) {
		for (int i = 0; i < b.length; i++) {
			if (b[i] < 0)
				return true;
		}
		return false;
	}

	protected static double checkDoubleOverflow(double probs, int pos,
			int[] countP) {
		if (probs < 0) {
			System.err.println(probs + "\t" + pos);
			for (int i = 0; i < countP.length; i++)
				System.err.print(countP[i] + " ");
			throw new IndexOutOfBoundsException("p is negative!!");
		}
		if (probs > 1e+150d) {
			// System.out.println("p is too large for double type (> 2e+150d).");
			countP[pos]++;
			return (probs / 1e+150);
		}
		if (probs < 1e-150d) {
			// System.out.println("p is too small for double type (< 5e-150d).");
			countP[pos]--;
			return (probs * 1e+150);
		}
		return probs;
	}

	static void reAssignP(double[] p, int[] countP) {
		// p and countP should be the same length
		int maxV = countP[0];
		for (int i = 0; i < countP.length; i++) {
			// System.out.print(p[i] + ":" + countP[i] + "\t");
			if (countP[i] > maxV) {
				maxV = countP[i];
			}
		}
		// System.out.println("\t max:" + maxV);
		for (int i = 0; i < p.length; i++) {
			p[i] *= Math.pow(1e+150, countP[i] - maxV);
			// System.out.print(p[i] + "\t");
		}
		// System.out.println();
	}
}
