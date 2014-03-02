package com.unsupervisedsentiment.analysis.model;

public class SeedScoreModel {
	
	private String seed;
	private double score;
	public SeedScoreModel(String seed, double score) {
		super();
		this.seed = seed;
		this.score = score;
	}
	public String getSeed() {
		return seed;
	}
	public void setSeed(String seed) {
		this.seed = seed;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	
	
	

}
