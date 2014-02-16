package com.unsupervisedsentiment.analysis.core;

import java.util.HashSet;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.DoublePropagationData;
import com.unsupervisedsentiment.analysis.model.ElementType;
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
		// inputData.setInput(StanfordNLPTestConstants.SENTENCE_LIU);
		// inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST1);
		// inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST2);
	    // inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST3);
		// inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST4);
		// inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST5);
		// inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST6);

		inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST1 + " " + StanfordNLPTestConstants.SENTENCE_TEST2
				+ " " + StanfordNLPTestConstants.SENTENCE_TEST3
				+ " " + StanfordNLPTestConstants.SENTENCE_TEST4
				+ " " + StanfordNLPTestConstants.SENTENCE_TEST5
				+ " " + StanfordNLPTestConstants.SENTENCE_TEST6);
		DoublePropagationAlgorithm algorithm = new DoublePropagationAlgorithm(inputData);

		HashSet<Tuple> seed = new HashSet<Tuple>();

		Tuple test = new Tuple();

		test.setSource(new Word("JJ", "good", ElementType.OPINION_WORD));
		test.setTupleType(TupleType.Seed);
		seed.add(test);

		Tuple test2 = new Tuple();

		test2.setSource(new Word("JJ", "best", ElementType.OPINION_WORD));
		test2.setTupleType(TupleType.Seed);

		seed.add(test2);
		
		Tuple test3 = new Tuple();

		test3.setSource(new Word("JJ", "great", ElementType.OPINION_WORD));
		test3.setTupleType(TupleType.Seed);

		seed.add(test3);
		
		Tuple test4 = new Tuple();

		test4.setSource(new Word("JJ", "amazing", ElementType.OPINION_WORD));
		test4.setTupleType(TupleType.Seed);

		seed.add(test4);
		
		Tuple test5 = new Tuple();

		test5.setSource(new Word("JJ", "sexy", ElementType.OPINION_WORD));
		test5.setTupleType(TupleType.Seed);

		seed.add(test5);

		algorithm.execute(seed);

		System.out.println("-----------------------------------------");
		System.out.println("Features");
		PreetyPrintTuples(algorithm.getData().getFeatureTuples());
		System.out.println("-----------------------------------------");
		System.out.println("OpinionWords");
		PreetyPrintTuples(algorithm.getData().getExpandedOpinionWordsTuples());
	}

	private static void PreetyPrintTuples(Set<Tuple> tuples) {
		for (Tuple tuple : tuples) {
			if (tuple.getTupleType().equals(TupleType.Pair)) {
				Pair pair = (Pair) tuple;
				System.out.println("Pair:  " + tuple.getSource().getValue() + "("
						+ tuple.getSource().getPosTag() + ")" + " --(" + pair.getRelation() + ")--> "
						+ tuple.getTarget().getValue() + "(" + tuple.getTarget().getPosTag() + ")");
			} else if (tuple.getTupleType().equals(TupleType.Triple)) {
				Triple triple = (Triple) tuple;
				System.out.println("Triple:  " + tuple.getSource().getValue() + "("
						+ tuple.getSource().getPosTag() + ")" + " --(" + triple.getRelationHOpinion() + ")--> "
						+ triple.getH().getValue() + "(" + triple.getH().getPosTag() + ")" + " --("
						+ triple.getRelationHTarget() + ")--> " + tuple.getTarget().getValue() + "("
						+ tuple.getTarget().getPosTag() + ")");
			}
		}
	}
}
