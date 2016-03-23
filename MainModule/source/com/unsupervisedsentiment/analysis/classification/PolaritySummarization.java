package com.unsupervisedsentiment.analysis.classification;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.Helpers;

public class PolaritySummarization {
	
	public static double computeOverallScore(List<Tuple> data) {
		double totalScore = 0;
		for (Tuple tuple : data) {
			if (tuple.getSource().getScore() != Classification.DEFAULT_SCORE) {
				totalScore += tuple.getSource().getScore();
			}
		}
		double normalizedScore = totalScore > 0 ? Helpers.normalizeScore(totalScore) : (-1 * Helpers.normalizeScore((-1 * totalScore)));

		return normalizedScore;
	}
	
	public static HashMap<Double,Integer> computeScoreDistribution(List<Tuple> data){
		HashMap<Double,Integer> myMap = new HashMap<Double,Integer>();
		for (Tuple tuple : data){
			if (tuple.getSource().getScore() != Classification.DEFAULT_SCORE) {
				DecimalFormat df = new DecimalFormat("#.#");
				String formatted = df.format(tuple.getSource().getScore()).replace(",", ".");
				double value = Double.parseDouble(formatted);
				if(value == -0.0)//stupid parser
					value = 0.0;
				if (myMap.containsKey(value)){
					myMap.put(value, myMap.get(value) + 1);
				}
				else{
					myMap.put(value, 1);
				}
				
			}
			
		}
		return myMap;
	}

	public static double getAverageScoreForTarget(String target, List<Tuple> data) {
		double score = 0;
		int numberOfEntries = 0;
		for (Tuple tuple : data) {
			if (!tuple.getTupleType().equals(TupleType.Seed) && tuple.getTarget() != null) {
				String targetString = tuple.getTarget().getValue();
				if (targetString.toLowerCase()
							.equals(target.toLowerCase())){
					score += tuple.getTarget().getScore();
					numberOfEntries++;
				}
				
			}
		}
		double averageScore = score / numberOfEntries;
		return averageScore;
	}

}
