package com.unsupervisedsentiment.analysis.modules.evaluation;

public class EvaluationResult {
	private double precision;
	private double recall;
	private double Fscore;
	private double scoreCorectness;
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

}
