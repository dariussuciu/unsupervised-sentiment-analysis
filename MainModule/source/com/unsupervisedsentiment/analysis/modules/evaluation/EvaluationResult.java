package com.unsupervisedsentiment.analysis.modules.evaluation;

public class EvaluationResult {
	private double precision;
	private double recall;
	private double Fscore;
	private double scoreCorectness;
	private int truePositive;
	private int falsePositive;
	private int falseNegative;

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}

	public double getFscore() {
		return Fscore;
	}

	public void setFscore(double fscore) {
		Fscore = fscore;
	}

	public double getScoreCorectness() {
		return scoreCorectness;
	}

	public void setScoreCorectness(double scoreCorectness) {
		this.scoreCorectness = scoreCorectness;
	}

	public int getTruePositive() {
		return truePositive;
	}

	public void setTruePositive(int truePositive) {
		this.truePositive = truePositive;
	}

	public int getFalsePositive() {
		return falsePositive;
	}

	public void setFalsePositive(int falsePositive) {
		this.falsePositive = falsePositive;
	}

	public int getFalseNegative() {
		return falseNegative;
	}

	public void setFalseNegative(int falseNegative) {
		this.falseNegative = falseNegative;
	}

}
