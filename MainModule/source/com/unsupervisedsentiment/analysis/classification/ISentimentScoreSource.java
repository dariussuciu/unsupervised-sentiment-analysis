package com.unsupervisedsentiment.analysis.classification;

import java.util.HashMap;

public interface ISentimentScoreSource {
	public Double extract(String word);
	public HashMap<String, Double> getSeedWordsWithScores();
}
