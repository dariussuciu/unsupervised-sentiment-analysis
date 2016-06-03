package com.unsupervisedsentiment.analysis.model;

public class SeedScoreModel {

	private String seed;
	private double score;
    private boolean hasModifier;
    private String modifier;

	public SeedScoreModel(String seed, double score, boolean hasModifier) {
		super();
		this.seed = seed;
		this.score = score;
        this.hasModifier = hasModifier;
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

    public boolean hasModifier() {
        return hasModifier;
    }

    public void setHasModifier(boolean hasModifier) {
        this.hasModifier = hasModifier;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }
}
