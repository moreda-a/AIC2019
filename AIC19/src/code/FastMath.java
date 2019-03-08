package code;

import java.util.ArrayList;

public class FastMath {

	static ArrayList<Double> valArr = new ArrayList<Double>();
	static ArrayList<Double> minDiffArr = new ArrayList<Double>();
	static ArrayList<Double> maxArr = new ArrayList<Double>();
	static ArrayList<Double> coef = new ArrayList<Double>();

	public static void startNewEvaluationFunction() {
		valArr = new ArrayList<Double>();
		minDiffArr = new ArrayList<Double>();
		maxArr = new ArrayList<Double>();
		coef = new ArrayList<Double>();
	}

	public static void addFeature(double val, double mind, double maxv) {
		double cc = (double) 1 / (maxv + mind);
		if (coef.size() != 0)
			cc *= coef.get(coef.size() - 1) * minDiffArr.get(coef.size() - 1);
		coef.add(cc);
		valArr.add(val);
		minDiffArr.add(mind);
		maxArr.add(maxv);
	}

	public static double evaluationFunction() {
		double res = 0;
		for (int i = 0; i < valArr.size(); ++i)
			res += valArr.get(i) * coef.get(i);
		return res;
	}

}
