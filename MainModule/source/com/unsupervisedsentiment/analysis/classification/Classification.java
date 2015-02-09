package com.unsupervisedsentiment.analysis.classification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.SeedScoreModel;
import com.unsupervisedsentiment.analysis.model.Tuple;

public class Classification {

	/**
	 * Should make cases for modifiers like : very, especially, noticeably,
	 * clearly, undisputed negatia
	 */
	public static double DEFAULT_SCORE = -100;

	private IPolarityLexion polarityLexicon;

	public Classification() {
		initReader();
	}

	public ArrayList<Tuple> assignScoresBasedOnSeeds(Set<Tuple> data) {

		ArrayList<SeedScoreModel> seeds = polarityLexicon
				.getSeedWordsWithScores();

		ArrayList<Tuple> initialTuples = PolarityAssigner.initTupleArrayList(data);

		ArrayList<Tuple> partiallyAssignedTuples = PolarityAssigner.assignScoresToSeeds(
				initialTuples, seeds);

		ArrayList<Tuple> fullyAssignedTuples = PolarityAssigner.assignScores(
				partiallyAssignedTuples, seeds);
		
		ArrayList<Tuple> fullyAssignedTuples2 = PolarityAssigner.assignScores(
				fullyAssignedTuples, seeds);

		printResults(fullyAssignedTuples);
		return fullyAssignedTuples2;
	}
	
	public double computeOverallScore(List<Tuple> data){
		return PolaritySummarization.computeOverallScore(data);
	}
	
	public HashMap<Double,Integer> computeScoreDistribution(List<Tuple> data){
		return PolaritySummarization.computeScoreDistribution(data);
	}
	
	public double getAverageScoreForTarget(String target, List<Tuple> data){
		return PolaritySummarization.getAverageScoreForTarget(target, data);
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

	private void initReader() {
		polarityLexicon = SentiWordNetService.getInstance();
	}

}
