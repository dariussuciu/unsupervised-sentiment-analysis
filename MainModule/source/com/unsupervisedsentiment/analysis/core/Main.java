package com.unsupervisedsentiment.analysis.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.classification.Classification;
import com.unsupervisedsentiment.analysis.core.constants.StanfordNLPTestConstants;
import com.unsupervisedsentiment.analysis.model.DoublePropagationData;
import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.EvaluationModel;
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
import com.unsupervisedsentiment.analysis.modules.standfordparser.NLPService;

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


			inputData.setFilename(input.getFilename());
			
			//!!!!!!!! FOR EVALUTAION ONLY !!!!!!!
			List<EvaluationModel> evaluationModels = NLPService.getInstance().getEvaluationModels(input.getContent());
			outputService.WriteEvaluationModels(input.getFilename(), evaluationModels);
			
			inputData.setInput(input.getContent());
			DoublePropagationAlgorithm algorithm = new DoublePropagationAlgorithm(
					inputData);

			HashSet<Tuple> seedWords = new HashSet<Tuple>();

			config.setSeedWords(inputService.getSeedWordsFromFile());
			
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

			HashSet<Tuple> featureTuples = algorithm.getData()
					.getFeatureTuples();
			
			HashSet<Tuple> opinionWordTuples = algorithm.getData()
					.getExpandedOpinionWordsTuples();

			HashSet<Tuple> combinedTuples = new HashSet<Tuple>();
			
			Classification classification = new Classification();
			classification.assignScoresBasedOnSeeds(featureTuples);
			classification.assignSentiWordScores(featureTuples);
			
			classification = new Classification();
			classification.assignScoresBasedOnSeeds(opinionWordTuples);
			classification.assignSentiWordScores(opinionWordTuples);
			
			combinedTuples.addAll(featureTuples);
			combinedTuples.addAll(opinionWordTuples);
			
			OutputWrapper outputFile = new OutputWrapper();
			
			outputFile.setAuthor(input.getAuthor());
			outputFile.setFilename(input.getFilename());
			outputFile.setSource(input.getSource());
			outputFile.setTuples(combinedTuples);
			outputFiles.add(outputFile);
		}
		
		outputService.writeOutput(outputFiles);
		
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
