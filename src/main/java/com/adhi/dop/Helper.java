package com.adhi.dop;

public abstract class Helper {

	public static double calcRelValue(double val, double low, double high) {
		return ((val - low) / (high - low));
	}

	public static double calcSimilarityIndex(double val1, double val2) {
		return (1 - Math.abs(val1 - val2));
	}

	public static double calcDistanceIndex(double val1, double val2) {
		return Math.abs(val1 - val2);
	}

}
