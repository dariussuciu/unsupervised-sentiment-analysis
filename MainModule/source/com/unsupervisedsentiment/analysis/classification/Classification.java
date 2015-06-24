package com.unsupervisedsentiment.analysis.classification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.classification.SentiWordNetService.SWNPos;
import com.unsupervisedsentiment.analysis.model.SeedScoreModel;
import com.unsupervisedsentiment.analysis.model.Tuple;

import edu.stanford.nlp.semgraph.SemanticGraph;

public class Classification {

	/**
	 * Should make cases for modifiers like : very, especially, noticeably,
	 * clearly, undisputed negatia
	 * 
	 * 
	 * 
	 * 
	 */
	public static double DEFAULT_SCORE = -100;

	private IPolarityLexion polarityLexicon;

	private ArrayList<SeedScoreModel> seeds;

	public Classification() {
		initReader();
	}

	public ArrayList<Tuple> assignScoresBasedOnSeeds(Set<Tuple> data, List<SemanticGraph> semanticGrapshs,
			boolean useSeedWordsFromFile) {

		if (useSeedWordsFromFile) {
			seeds = polarityLexicon.getSeedWordsWithScores();
		} else {
			seeds = new ArrayList<SeedScoreModel>();
			List<String> extractedSeedWords = polarityLexicon.getSeedWordsFromSemanticGraph(semanticGrapshs);
			// the extracted seed words do not have any polarities, so we have
			// to asign them
			for (String word : extractedSeedWords) {
				double score = polarityLexicon.extract(word, new String[] { SWNPos.Adjective.toString() });
				seeds.add(new SeedScoreModel(word, score));
			}
		}

		ArrayList<Tuple> initialTuples = PolarityAssigner.initTupleArrayList(data);

		ArrayList<Tuple> partiallyAssignedTuples = PolarityAssigner.assignScoresToSeeds(initialTuples, seeds);

		ArrayList<Tuple> fullyAssignedTuples = PolarityAssigner.assignScores(partiallyAssignedTuples, seeds);

		ArrayList<Tuple> fullyAssignedTuples2 = PolarityAssigner.assignScores(fullyAssignedTuples, seeds);

		//printResults(fullyAssignedTuples);
		return fullyAssignedTuples2;
	}

	public double computeOverallScore(List<Tuple> data) {
		return PolaritySummarization.computeOverallScore(data);
	}

	public HashMap<Double, Integer> computeScoreDistribution(List<Tuple> data) {
		return PolaritySummarization.computeScoreDistribution(data);
	}

	public double getAverageScoreForTarget(String target, List<Tuple> data) {
		return PolaritySummarization.getAverageScoreForTarget(target, data);
	}

	private void printResults(ArrayList<Tuple> tuples) {
		System.out
				.println("-------------------------------------------------------------------------------------------------------");
		System.out.println("Score assignment:");

		for (Tuple tuple : tuples) {
			System.out.println("Source word/score :" + tuple.getSource().getValue() + "/"
					+ tuple.getSource().getScore());
			if (tuple.getTarget() != null) {
				System.out.println("Target word/score :" + tuple.getTarget().getValue() + "/"
						+ tuple.getTarget().getScore());
			}
		}
		System.out.println(tuples.size());
	}

	private void initReader() {
		polarityLexicon = SentiWordNetService.getInstance();
	}

}
