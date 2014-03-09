package com.unsupervisedsentiment.analysis.classification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.SeedScoreModel;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.Word;

public class Classification {

	private final double DEFAULT_SCORE = -100;

	private ISentimentScoreSource sentimentScoreSource;

	public Classification() {
		initReader();
	}

	public void assignScores(Set<Tuple> tuples) {
		HashMap<Word, Double> opinionScores = new HashMap<Word, Double>();

		for (Tuple tuple : tuples) {
			Word opinionWord = tuple.getOpinionWord();
			String opinionWordValue = opinionWord.getValue();
			double score = getScore(opinionWordValue);
			// either one
			opinionScores.put(opinionWord, score);
			tuple.getOpinionWord().setScore(score);
		}
	}

	public void assignScoresBasedOnSeeds(Set<Tuple> data) {

		ArrayList<SeedScoreModel> seeds = sentimentScoreSource
				.getSeedWordsWithScores();

		ArrayList<Tuple> initialTuples = initTupleArrayList(data);

		ArrayList<Tuple> partiallyAssignedTuples = assignScoresBasedOnSeeds(
				initialTuples, seeds);

		ArrayList<Tuple> fullyAssignedTuples = assignScoresByPropagation(partiallyAssignedTuples);

		printResults(fullyAssignedTuples);
	}

	private ArrayList<Tuple> assignScoresBasedOnSeeds(ArrayList<Tuple> tuples,
			ArrayList<SeedScoreModel> seeds) {
		HashSet<Tuple> set = new HashSet<Tuple>();
		for (Tuple tuple : tuples) {
			for (SeedScoreModel model : seeds) {
				String seed = model.getSeed().trim();
				String source = tuple.getSource().getValue();

				if (tuple.getTarget() != null) {
					String target = tuple.getTarget().getValue();

					if (seed.equals(source.trim())
							|| seed.equals(target.trim())) {
						double score = model.getScore();
						tuple.getSource().setScore(score);
						tuple.getTarget().setScore(score);
						set.add(tuple);
					}
//					if (set.contains(tuple)
//							&& (tuple.getSource().getScore() == DEFAULT_SCORE || tuple
//									.getTarget().getScore() == DEFAULT_SCORE)) {
//					//	set.add(tuple);
//					} else if (!set.contains(tuple)) {
//						set.add(tuple);
//					}
				}
			}

		}
		tuples = new ArrayList<Tuple>();
		for (Tuple tuple : set) {
			tuples.add(tuple);
		}
		return tuples;
	}

	/**
	 * Necessary because for some reason changes in a Set<E> do not persist!
	 * 
	 * @param data
	 * @return
	 */
	private ArrayList<Tuple> initTupleArrayList(Set<Tuple> data) {
		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
		for (Tuple tuple : data) {
			if (tuple != null) {
				if (tuple.getSource() != null)
					tuple.getSource().setScore(DEFAULT_SCORE);
				if (tuple.getTarget() != null)
					tuple.getTarget().setScore(DEFAULT_SCORE);
				tuples.add(tuple);
			}
		}
		return tuples;
	}

	private ArrayList<Tuple> assignScoresByPropagation(ArrayList<Tuple> tuples) {
		for (int i = 0; i < tuples.size() - 1; i++) {
			for (int j = 1; j < tuples.size(); j++) {
				Word firstWordFeature = tuples.get(i).getFeatureWord();
				Word firstWordOpinion = tuples.get(i).getOpinionWord();
				Word secondWordFeature = tuples.get(j).getFeatureWord();
				Word secondWordOpinion = tuples.get(j).getOpinionWord();

				if (firstWordFeature.getValue().equals(secondWordFeature.getValue())) {
					double score1 = firstWordFeature.getScore();
					double score2 = secondWordFeature.getScore();
					double score3 = secondWordOpinion.getScore();
					
					if (score1 > DEFAULT_SCORE && score2 == DEFAULT_SCORE && score3 == DEFAULT_SCORE) {
						secondWordFeature.setScore(score1);
						secondWordOpinion.setScore(score1);
					}
				}
				if (firstWordOpinion.getValue().equals(secondWordOpinion)){
					double score1 = firstWordOpinion.getScore();
					double score2 = secondWordOpinion.getScore();
					double score3 = secondWordOpinion.getScore();
					
					if (score1 > DEFAULT_SCORE && score2 == DEFAULT_SCORE && score3 == DEFAULT_SCORE) {
						secondWordFeature.setScore(score1);
						secondWordOpinion.setScore(score1);
					}
				}
			}
		}
		return tuples;
	}

	private void printResults(ArrayList<Tuple> tuples) {
		System.out
				.println("-------------------------------------------------------------------------------------------------------");
		System.out.println("Score assignment:");

		for (Tuple tuple : tuples) {
			System.out.println("Opinion word/score :"
					+ tuple.getOpinionWord().getValue() + "/"
					+ tuple.getOpinionWord().getScore());
			System.out.println("Feature word/score :"
					+ tuple.getFeatureWord().getValue() + "/"
					+ tuple.getFeatureWord().getScore());
		}
	}

	private double getScore(String word) {
		double score = sentimentScoreSource.extract(word);
		return score;
	}

	private void initReader() {
		sentimentScoreSource = new SentiWordNetService();
	}

}
