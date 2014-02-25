package com.unsupervisedsentiment.analysis.classification;

import java.util.HashMap;
import java.util.HashSet;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.Word;

public class Classification {

	private ISentimentScoreSource sentimentScoreSource;

	public Classification() {
		initReader();
	}

	public void assignScores(HashSet<Tuple> tuples) {
		HashMap<Word, Double> opinionScores = new HashMap<Word, Double>();

		for (Tuple tuple : tuples) {
			Word opinionWord = tuple.getOpinionWord();
			String opinionWordValue = opinionWord.getValue();
			double score = getScore(opinionWordValue);
			//either one
			opinionScores.put(opinionWord, score);
			tuple.getOpinionWord().setScore(score);
		}
	}

	public void assignScoresBasedOnSeeds(HashSet<Tuple> tuples) {
		HashMap<String, Double> seeds = sentimentScoreSource
				.getSeedWordsWithScores();

	}

	private double getScore(String word) {
		double score = sentimentScoreSource.extract(word);
		return score;
	}

	private void initReader() {
		sentimentScoreSource = new SentiWordNetService();
	}

}
