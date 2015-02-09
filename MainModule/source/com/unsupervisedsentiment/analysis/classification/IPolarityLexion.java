package com.unsupervisedsentiment.analysis.classification;

import java.util.ArrayList;

import com.unsupervisedsentiment.analysis.model.SeedScoreModel;

public interface IPolarityLexion {
	public Double extract(String word, String pos[]);

	public ArrayList<SeedScoreModel> getSeedWordsWithScores();
}
