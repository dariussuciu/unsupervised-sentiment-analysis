package com.unsupervisedsentiment.analysis.classification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.unsupervisedsentiment.analysis.classification.SentiWordNetService.SWNPos;
import com.unsupervisedsentiment.analysis.model.SeedScoreModel;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.Helpers;

public class Classification {

	/**
	 * Should make cases for modifiers like : very, especially, noticeably,
	 * clearly, undisputed negatia
	 */
	public static double DEFAULT_SCORE = -100;

	private ISentimentScoreSource sentimentScoreSource;

	public Classification() {
		initReader();
	}

	public void assignScores(final Set<Tuple> tuples) {
		HashMap<Word, Double> opinionScores = new HashMap<Word, Double>();

		for (Tuple tuple : tuples) {
			Word opinionWord = tuple.getOpinionWord();
			String opinionWordValue = opinionWord.getValue();
			double score = getScore(opinionWordValue,
					Helpers.getEquivalentPOS(opinionWord.getPosTag()));
			// either one
			opinionScores.put(opinionWord, score);
			tuple.getSource().setScore(score);
		}
	}

	public void assignSentiWordScores(final Set<Tuple> tuples) {
		HashMap<Word, Double> opinionScores = new HashMap<Word, Double>();

		for (Tuple tuple : tuples) {
			Word opinionWord = tuple.getOpinionWord();
			String opinionWordValue = opinionWord.getValue();
			String[] equivalentPOS = Helpers.getEquivalentPOS(opinionWord
					.getPosTag());
			if (equivalentPOS.length <= 0) {
				System.out.println(opinionWord.getPosTag());
			}
			double score = getScore(opinionWordValue, equivalentPOS);
			// either one
			opinionScores.put(opinionWord, score);
			tuple.getOpinionWord().setSentiWordScore(score);
		}
	}

	public ArrayList<Tuple> assignScoresBasedOnSeeds(Set<Tuple> data) {

		ArrayList<SeedScoreModel> seeds = sentimentScoreSource
				.getSeedWordsWithScores();

		ArrayList<Tuple> initialTuples = initTupleArrayList(data);

		ArrayList<Tuple> partiallyAssignedTuples = assignScoresToSeeds(
				initialTuples, seeds);

		// ArrayList<Tuple> fullyAssignedTuples =
		// assignScoresByPropagation(partiallyAssignedTuples);

		ArrayList<Tuple> fullyAssignedTuples = assignScores(
				partiallyAssignedTuples, seeds);

		// printResults(fullyAssignedTuples);
		return fullyAssignedTuples;
	}

	private ArrayList<Tuple> assignScores(ArrayList<Tuple> data,
			ArrayList<SeedScoreModel> seeds) {

		for (Tuple tuple : data) {
			if (isSeed(seeds, tuple.getSource().getValue())
					|| (tuple.getTarget() != null)
					&& isSeed(seeds, tuple.getTarget().getValue())) {
				if (tuple.getSource().getScore() != DEFAULT_SCORE) {
					propagateScore(data, tuple);
				}
			}
		}

		return data;
	}

	private void propagateScore(ArrayList<Tuple> data, Tuple assignedTuple) {
		for (Tuple tuple : data) {
			if (tuple.getSource().getValue().toLowerCase()
					.equals(assignedTuple.getSource().getValue().toLowerCase())
					|| (tuple.getTarget() != null
							&& assignedTuple.getTarget() != null && tuple
							.getTarget()
							.getValue()
							.toLowerCase()
							.equals(assignedTuple.getTarget().getValue()
									.toLowerCase()))
					|| (assignedTuple.getTarget() != null && tuple
							.getSource()
							.getValue()
							.toLowerCase()
							.equals(assignedTuple.getTarget().getValue()
									.toLowerCase()))
					|| (tuple.getTarget() != null && tuple
							.getTarget()
							.getValue()
							.toLowerCase()
							.equals(assignedTuple.getSource().getValue()
									.toLowerCase()))) {
				if (tuple.getSource().getScore() == DEFAULT_SCORE) {
					double score = assignedTuple.getSource().getScore();
					tuple.getSource().setScore(score);
					if (tuple.getTarget() != null)
						tuple.getTarget().setScore(score);
				}

			}
		}
	}

	private boolean isSeed(ArrayList<SeedScoreModel> seeds, String word) {
		for (SeedScoreModel model : seeds) {
			if (model.getSeed().toLowerCase().equals(word.toLowerCase()))
				return true;
		}
		return false;
	}

	private ArrayList<Tuple> assignScoresToSeeds(ArrayList<Tuple> tuples,
			ArrayList<SeedScoreModel> seeds) {
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
					}
				}
			}

		}
		return tuples;
	}

	/**
	 * Necessary because for some reason changes in a Set<E> do not persist!
	 * 
	 * @param data
	 * @return
	 */
	private ArrayList<Tuple> initTupleArrayList(final Set<Tuple> data) {
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
				Word firstTupleFeature = tuples.get(i).getSource();
				Word firstTupleOpinion = tuples.get(i).getOpinionWord();
				Word secondTupleFeature = tuples.get(j).getTarget();
				Word secondTupleOpinion = tuples.get(j).getSource();
				if (secondTupleFeature != null)
					if (firstTupleFeature.getValue().equals(
							secondTupleFeature.getValue())) {
						double score1 = firstTupleFeature.getScore();
						double score2 = secondTupleFeature.getScore();
						double score3 = secondTupleOpinion.getScore();

						if (score1 > DEFAULT_SCORE && score2 == DEFAULT_SCORE
								&& score3 == DEFAULT_SCORE) {
							secondTupleFeature.setScore(score1);
							secondTupleOpinion.setScore(score1);
						}
					}
				// if (firstWordOpinion.getValue().equals(secondWordOpinion)) {
				// double score1 = firstWordOpinion.getScore();
				// double score2 = secondWordOpinion.getScore();
				// double score3 = secondWordOpinion.getScore();
				//
				// if (score1 > DEFAULT_SCORE && score2 == DEFAULT_SCORE
				// && score3 == DEFAULT_SCORE) {
				// secondWordFeature.setScore(score1);
				// secondWordOpinion.setScore(score1);
				// }
				// }
			}
		}

		return tuples;
	}

	private void printResults(ArrayList<Tuple> tuples) {
		System.out
				.println("-------------------------------------------------------------------------------------------------------");
		System.out.println("Score assignment:");

		for (Tuple tuple : tuples) {
			System.out.println("Source word/score :"
					+ tuple.getSource().getValue() + "/"
					+ tuple.getSource().getScore() + " "
					+ tuple.getSource().getSentiWordScore());
			System.out.println("Target word/score :"
					+ tuple.getTarget().getValue() + "/"
					+ tuple.getTarget().getScore() + " "
					+ tuple.getTarget().getSentiWordScore());
		}
		System.out.println(tuples.size());
	}

	private double getScore(final String word, final String[] pos) {
		double score = sentimentScoreSource.extract(word, pos);
		return score;
	}

	private void initReader() {
		sentimentScoreSource = SentiWordNetService.getInstance();
	}

}
