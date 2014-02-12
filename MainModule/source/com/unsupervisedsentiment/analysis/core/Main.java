package com.unsupervisedsentiment.analysis.core;

import java.util.HashSet;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.DoublePropagationData;
import com.unsupervisedsentiment.analysis.model.Pair;
import com.unsupervisedsentiment.analysis.model.Triple;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.DoublePropagationAlgorithm;
import com.unsupervisedsentiment.analysis.test.constants.*;
import com.unsupervisedsentiment.analysis.test.constants.relations.Pos_JJRel;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		boolean ok = Pos_JJRel.getInstance().contains("JJR");
		
		DoublePropagationData inputData = new DoublePropagationData();
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_LIU);
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST1);
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST2);
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST3);
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST4);
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST5);
		//inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST6);
		
		inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST1+ " " + StanfordNLPTestConstants.SENTENCE_TEST2);
		DoublePropagationAlgorithm algorithm = new DoublePropagationAlgorithm(inputData);
		
		HashSet<Tuple>  seed = new HashSet<Tuple>();
		
		Tuple test = new Tuple();
		
		test.setOpinion(new Word("JJ", "good"));
		test.setTupleType(TupleType.Seed);
		
		seed.add(test);
		
		Tuple test2 = new Tuple();
		
		test2.setOpinion(new Word("JJ", "best"));
		test2.setTupleType(TupleType.Seed);
		
		seed.add(test2);
		
		algorithm.execute(seed);	
		
		System.out.println("-----------------------------------------");
		System.out.println("Features");
		PreetyPrintTuples(algorithm.getData().getFeatureTuples());
		System.out.println("-----------------------------------------");
		System.out.println("OpinionWords");
		PreetyPrintTuples(algorithm.getData().getExpandedOpinionWordsTuples());
	}

	
	private static void PreetyPrintTuples(Set<Tuple> tuples) {
		for(Tuple tuple : tuples)
		{
			if(tuple.getTupleType().equals(TupleType.Pair))
			{
				Pair pair = (Pair) tuple;
				System.out.println("Pair:  " + tuple.getOpinion().getValue() + "{O}"
						+ "(" + tuple.getOpinion().getPosTag() + ")" 
						+ " --(" + pair.getRelation() + ")--> "
						+ tuple.getTarget().getValue() + "{T}"
						+ "(" + tuple.getTarget().getPosTag() + ")");
			}
			else if(tuple.getTupleType().equals(TupleType.Triple))
			{
				Triple triple = (Triple) tuple;
				System.out.println("Triple:  " + tuple.getOpinion().getValue() + "{O}"
						+ "(" + tuple.getOpinion().getPosTag() + ")" 
						+ " --(" + triple.getRelationHOpinion() + ")--> "
						+ triple.getH().getValue() + "{H}"
						+ "(" + triple.getH().getPosTag() + ")"
						+ " --(" + triple.getRelationHTarget() + ")--> "
						+ tuple.getTarget().getValue() + "{T}"
						+ "(" + tuple.getTarget().getPosTag() + ")");
			}
		}
	}
}
