package com.unsupervisedsentiment.analysis.model;

public class ResultPrecRecall {
	private final String precision;
	private final String recall;

	public ResultPrecRecall(String precision, String recall) {
		this.precision = precision;
		this.recall = recall;
	}

	public String getPrecision() {
		return precision;
	}

	public String getRecall() {
		return recall;
	}

}
