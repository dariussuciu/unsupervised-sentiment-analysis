package com.unsupervisedsentiment.analysis.classification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.unsupervisedsentiment.analysis.model.SeedScoreModel;

import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.util.Pair;

public interface IPolarityLexion {

    Double extract(String word, String pos[]);

	ArrayList<SeedScoreModel> getSeedWordsWithScores();
	
	List<Pair<String, Integer>> getSeedWordsFromSemanticGraph(List<SemanticGraph> graphs, Map<String, Pair<Double, Double>> knownModifiers);

    Map<String, Pair<Double, Double>> getModifiers();

}
