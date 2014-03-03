package com.unsupervisedsentiment.analysis.classification;

import java.util.ArrayList;

import com.unsupervisedsentiment.analysis.model.SeedScoreModel;

public interface ISentimentScoreSource {
	public Double extract(String word);

	public ArrayList<SeedScoreModel> getSeedWordsWithScores();
}
