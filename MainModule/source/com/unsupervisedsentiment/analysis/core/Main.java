package com.unsupervisedsentiment.analysis.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.classification.Classification;
import com.unsupervisedsentiment.analysis.core.constants.StanfordNLPTestConstants;
import com.unsupervisedsentiment.analysis.model.DoublePropagationData;
import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.Pair;
import com.unsupervisedsentiment.analysis.model.Triple;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.IO.InputService;
import com.unsupervisedsentiment.analysis.modules.IO.InputWrapper;
import com.unsupervisedsentiment.analysis.modules.IO.OutputService;
import com.unsupervisedsentiment.analysis.modules.IO.OutputWrapper;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.DoublePropagationAlgorithm;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Config config = Initializer.getConfig();

		InputService inputService = InputService.getInstance(config);
		OutputService outputService = OutputService.getInstance(config);
		List<InputWrapper> inputFiles = inputService.getTextFromFile();
        List<OutputWrapper> outputFiles = new ArrayList<OutputWrapper>();
		for (InputWrapper input : inputFiles) {
			System.out.println("-----------------------------------------");
			System.out.println("-------------NEW FILE-----------");
			System.out.println("-----------------------------------------");
			long currentTime = System.currentTimeMillis();
			DoublePropagationData inputData = new DoublePropagationData();

			// inputData.setInput(StanfordNLPTestConstants.SENTENCE_TEST1 + " "
			// +
			// StanfordNLPTestConstants.SENTENCE_TEST2
			// + " " + StanfordNLPTestConstants.SENTENCE_TEST3 + " " +
			// StanfordNLPTestConstants.SENTENCE_TEST4 + " "
			// + StanfordNLPTestConstants.SENTENCE_TEST5 + " " +
			// StanfordNLPTestConstants.SENTENCE_TEST6);

			inputData.setInput(input.getContent());
			inputData.setFilename(input.getFilename());
			DoublePropagationAlgorithm algorithm = new DoublePropagationAlgorithm(
					inputData);

			HashSet<Tuple> seedWords = new HashSet<Tuple>();

			for (String seedString : config.getSeedWords()) {
				Tuple seed = new Tuple();
				Word word = new Word("JJ", seedString.trim(),
						ElementType.OPINION_WORD);
				seed.setSource(word);
				seed.setTupleType(TupleType.Seed);
				seedWords.add(seed);
			}

			algorithm.execute(seedWords);
			System.out.println("Elapsed time: "
					+ (System.currentTimeMillis() - currentTime) + " ms");

			// System.out.println("-----------------------------------------");
			// System.out.println("Features");
			// PreetyPrintTuples(algorithm.getData().getFeatureTuples());
			// System.out.println("-----------------------------------------");
			// System.out.println("OpinionWords");
			// PreetyPrintTuples(algorithm.getData().getExpandedOpinionWordsTuples());

			HashSet<Tuple> featureTuples = algorithm.getData()
					.getFeatureTuples();
			
			HashSet<Tuple> opinionWordTuples = algorithm.getData()
					.getExpandedOpinionWordsTuples();

			Classification classification = new Classification();
			classification.assignScores(featureTuples);
			OutputWrapper outputFile = new OutputWrapper();
			
			outputFile.setAuthor(input.getAuthor());
			outputFile.setFilename(input.getFilename());
			outputFile.setSource(input.getSource());
			outputFile.setTuples(featureTuples);
			outputFiles.add(outputFile);
			
			classification = new Classification();
			classification.assignScores(opinionWordTuples);
			outputFile.setTuples(opinionWordTuples);
			outputFiles.add(outputFile);
		}
		
		outputService.writeOutput(outputFiles);

		// long currentTime = System.currentTimeMillis();
		// algorithm.execute(seedWords);
		// System.out.println("Elapsed time: " + (System.currentTimeMillis() -
		// currentTime) + " ms");
		//
		// System.out.println("-----------------------------------------");
		// System.out.println("Features");
		// PreetyPrintTuples(algorithm.getData().getFeatureTuples());
		// System.out.println("-----------------------------------------");
		// System.out.println("OpinionWords");
		// PreetyPrintTuples(algorithm.getData().getExpandedOpinionWordsTuples());
		//
		// Set<Tuple> featureTuples = algorithm.getData().getFeatureTuples();
		//
		// Classification classification = new Classification();
		// classification.assignScoresBasedOnSeeds(featureTuples);
		// classification.assignScores(featureTuples);
	}

	private static void PreetyPrintTuples(Set<Tuple> tuples) {
		for (Tuple tuple : tuples) {
			if (tuple.getTupleType().equals(TupleType.Pair)) {
				Pair pair = (Pair) tuple;
				System.out.println("Pair:  " + tuple.getSource().getValue()
						+ "(" + tuple.getSource().getPosTag() + ")" + " --("
						+ pair.getRelation() + ")--> "
						+ tuple.getTarget().getValue() + "("
						+ tuple.getTarget().getPosTag() + ")");
			} else if (tuple.getTupleType().equals(TupleType.Triple)) {
				Triple triple = (Triple) tuple;
				System.out.println("Triple:  " + tuple.getSource().getValue()
						+ "(" + tuple.getSource().getPosTag() + ")" + " --("
						+ triple.getRelationHOpinion() + ")--> "
						+ triple.getH().getValue() + "("
						+ triple.getH().getPosTag() + ")" + " --("
						+ triple.getRelationHTarget() + ")--> "
						+ tuple.getTarget().getValue() + "("
						+ tuple.getTarget().getPosTag() + ")");
			}
		}
	}
}
