package com.unsupervisedsentiment.analysis.modules.services.filtering.pageRank;

import java.util.LinkedHashSet;

import com.unsupervisedsentiment.analysis.model.DoublePropagationData;
import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.Word;

public class PageRankFilter {
	public static void calculateRank(DoublePropagationData data) {

		double numberOfWords = data.getFeatureTuples().size() + data.getExpandedOpinionWords().size();
		double dampeningFactor = 0.85;
		for(Word feature : data.getFeatures())
		{
			feature.setRankScore(1.0 / numberOfWords);
		}	
		
		for(Word opinionWord : data.getExpandedOpinionWords())
		{
			opinionWord.setRankScore(0);
		}
		
		double convergence = 0.0001;	
		Boolean hasConverged = true;
		do
		{
			hasConverged = true;
			LinkedHashSet<Tuple> currentFeatures = data.getFeatureTuples();
			LinkedHashSet<Tuple> currentOpinionWords = data.getExpandedOpinionWordsTuples();
			
			for(Word feature : data.getFeatures())
			{
				double nextRank = calculateRank(feature, numberOfWords, dampeningFactor, currentFeatures, currentOpinionWords);
				
				if(Math.abs(feature.getRankScore() - nextRank) > convergence)
				{
					hasConverged = false;
				}
				feature.setRankScore(nextRank);
			}	
			
			for(Word opinionWord : data.getExpandedOpinionWords())
			{
				double nextRank = calculateRank(opinionWord, numberOfWords, dampeningFactor, currentFeatures, currentOpinionWords);
				if(Math.abs(opinionWord.getRankScore() - nextRank) > convergence)
				{
					hasConverged = false;
				}
				opinionWord.setRankScore(nextRank);
			}
		}
		while(!hasConverged);
		
		for(Word feature : data.getFeatures())
		{
				System.out.println(feature.getValue() + " - " + feature.getRankScore());
		}
	}
	
	private static double calculateRank(Word target, double numberOfWords, double dampeningFactor, LinkedHashSet<Tuple> currentFeatures, LinkedHashSet<Tuple> currentOpinionWords) {
		double previousRankScore = target.getRankScore();
		double rankScore = 0;
		int numberOfLinks = 0;
		for(Tuple featureTuple : currentFeatures)
		{
			if(featureTuple.getSource().getValue().equals(target.getValue()))
			{
				Word linkedFeature = featureTuple.getTarget();
				if(linkedFeature != null)
				{
					numberOfLinks++;
					rankScore += linkedFeature.getRankScore();
				}
			}
		}
		
		for(Tuple opinionWordTuple : currentOpinionWords)
		{
			if(opinionWordTuple.getSource().getValue().equals(target.getValue()))
			{
				Word linkedOpinionWord = opinionWordTuple.getTarget();
				if(linkedOpinionWord != null)
				{
					numberOfLinks++;
					rankScore += linkedOpinionWord.getRankScore();
				}
			}
		}
		
		if(target.getType().equals(ElementType.FEATURE))
		{
			previousRankScore = (1 - dampeningFactor) / numberOfWords; 
		}
		else 
		{
			previousRankScore = 0;
		}
		
		if(numberOfLinks > 0)
		{
			previousRankScore +=  dampeningFactor * (rankScore / numberOfLinks); 
		}

		return previousRankScore;
	}
}
