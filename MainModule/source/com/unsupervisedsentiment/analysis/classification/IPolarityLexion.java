package com.unsupervisedsentiment.analysis.classification;

import java.util.ArrayList;
import java.util.List;

import com.unsupervisedsentiment.analysis.model.SeedScoreModel;

import edu.stanford.nlp.semgraph.SemanticGraph;

public interface IPolarityLexion {
	public Double extract(String word, String pos[]);

	public ArrayList<SeedScoreModel> getSeedWordsWithScores();

	public List<String> getSeedWordsFromSemanticGraph(List<SemanticGraph> graphs);
}
