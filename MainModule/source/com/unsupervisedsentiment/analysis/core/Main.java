package com.unsupervisedsentiment.analysis.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;

import com.unsupervisedsentiment.analysis.classification.Classification;
import com.unsupervisedsentiment.analysis.core.constants.Constants;
import com.unsupervisedsentiment.analysis.core.constants.relations.RelationsContainer;
import com.unsupervisedsentiment.analysis.model.DoublePropagationData;
import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.EvaluationModel;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.IO.InputService;
import com.unsupervisedsentiment.analysis.modules.IO.InputWrapper;
import com.unsupervisedsentiment.analysis.modules.IO.OutputService;
import com.unsupervisedsentiment.analysis.modules.IO.OutputWrapper;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.DoublePropagationAlgorithm;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.Helpers;
import com.unsupervisedsentiment.analysis.modules.evaluation.EvaluationMetadata;
import com.unsupervisedsentiment.analysis.modules.evaluation.EvaluationResult;
import com.unsupervisedsentiment.analysis.modules.evaluation.ExtractionEvaluationService;
import com.unsupervisedsentiment.analysis.modules.evaluation.ScoreEvaluationService;

public class Main {

	private static InputService inputService;
	private static Config config;
	private static OutputService outputService;
	private static List<InputWrapper> inputFiles;
	private static List<OutputWrapper> outputFiles;
	private static List<EvaluationMetadata> metadataResults;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		initialize();
		for (InputWrapper input : inputFiles) {
			System.out.println("-----------------------------------------");
			System.out.println("-------------NEW FILE-----------");
			System.out.println("-----------------------------------------");
			long currentTime = System.currentTimeMillis();
			DoublePropagationData inputData = new DoublePropagationData();

			inputData.setFilename(input.getFilename());
			// !!!!!!!! FOR EVALUTAION ONLY !!!!!!!
			List<EvaluationModel> evaluationModels = null;

			// List<EvaluationModel> evaluationModels =
			// NLPService.getInstance().getEvaluationModels(input.getContent());
			// outputService.WriteEvaluationModels(input.getFilename(),
			// evaluationModels);

			String storedEvaluationModelsDirectory = Initializer.getConfig()
					.getEvaluationModelsDirectory();

			evaluationModels = Helpers.getEvaluationModels(
					storedEvaluationModelsDirectory, input, false);

			inputData.setInput(input.getOriginalContent());
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
				seed.setSentenceIndex(-1);
				seed.setSentence(null);
				seedWords.add(seed);
			}
			algorithm.execute(seedWords);
			long elapsedTime = System.currentTimeMillis() - currentTime;
			System.out.println("Elapsed time: " + elapsedTime + " ms");

			Set<Tuple> featureTuples = algorithm.getData().getFeatureTuples();

			Set<Tuple> opinionWordTuples = algorithm.getData()
					.getExpandedOpinionWordsTuples();

			LinkedHashSet<Tuple> combinedTuples = new LinkedHashSet<Tuple>();

			Classification classification = new Classification();
			classification.assignScoresBasedOnSeeds(featureTuples);
			classification.assignSentiWordScores(featureTuples);

			classification = new Classification();
			classification.assignScoresBasedOnSeeds(opinionWordTuples);
			classification.assignSentiWordScores(opinionWordTuples);

			combinedTuples.addAll(featureTuples);
			combinedTuples.addAll(opinionWordTuples);

			LinkedHashSet<Tuple> resultTuples = new LinkedHashSet<Tuple>();

			resultTuples = combinedTuples;

			// for(Tuple tuple : combinedTuples)
			// {
			// double count = 0;
			// for(Word feature : tuple.getElements(ElementType.FEATURE))
			// {
			// for(Tuple otherTuple : combinedTuples)
			// {
			// if(!otherTuple.equals(tuple))
			// {
			// for(Word otherFeature :
			// otherTuple.getElements(ElementType.FEATURE))
			// {
			// if(feature.getValue().equals(otherFeature.getValue()))
			// {
			// count++;
			// }
			// }
			// }
			// }
			// }
			//
			// if(count / tuple.getElements(ElementType.FEATURE).size() > 7)
			// {
			// resultTuples.add(tuple);
			// }
			// }

			OutputWrapper outputFile = new OutputWrapper();

			outputFile.setAuthor(input.getAuthor());
			outputFile.setFilename(input.getFilename());
			outputFile.setSource(input.getSource());
			outputFile.setTuples(resultTuples);
			outputFiles.add(outputFile);

			ExtractionEvaluationService extractionEvaluationService = new ExtractionEvaluationService(
					evaluationModels, resultTuples);
			EvaluationResult extractionEvaluationResult = extractionEvaluationService
					.getResults();
			System.out.println("Precision : "
					+ extractionEvaluationResult.getPrecision());
			System.out.println("Recall : "
					+ extractionEvaluationResult.getRecall());
			System.out.println("-----------------------------------------");

			// EvaluationService evaluationService = new EvaluationService(
			// evaluationModels, opinionWordTuples);
			// EvaluationResult evaluationResult =
			// evaluationService.getResults();
			// System.out
			// .println("Precision : " + evaluationResult.getPrecision());
			// System.out.println("Recall : " + evaluationResult.getRecall());
			// System.out.println("-----------------------------------------");
			//
			// evaluationService = new EvaluationService(
			// evaluationModels, featureTuples);
			// evaluationResult = evaluationService.getResults();
			// System.out
			// .println("Precision : " + evaluationResult.getPrecision());
			// System.out.println("Recall : " + evaluationResult.getRecall());
			// System.out.println("-----------------------------------------");

			metadataResults.add(new EvaluationMetadata(Constants.sdf
					.format(new Date()), config.getSeedType(), input
					.getFilename(), String.valueOf(seedWords.size()), String
					.valueOf(algorithm.getNumberOfIterations()), String
					.valueOf(elapsedTime), String
					.valueOf(extractionEvaluationResult.getPrecision()), String
					.valueOf(extractionEvaluationResult.getRecall()), config
					.getPolarityThreshold(), RelationsContainer
					.getAllEnumElementsAsString()));

			List<EvaluationModel> scoreEvaluationModels = Helpers
					.getEvaluationModels(storedEvaluationModelsDirectory,
							input, true);
			ScoreEvaluationService.performEvaluation(scoreEvaluationModels,
					combinedTuples);

		}
		outputService.writeToEvaluationMetadataCsv(metadataResults);
		outputService.writeOutput(outputFiles);
	}

	private static void initialize() {
		config = Initializer.getConfig();

		inputService = InputService.getInstance(config);
		outputService = OutputService.getInstance(config);
		inputFiles = inputService.getTextFromFile();
		outputFiles = new ArrayList<OutputWrapper>();
		metadataResults = new ArrayList<EvaluationMetadata>();
	}

	// private static void PreetyPrintTuples(Set<Tuple> tuples) {
	// for (Tuple tuple : tuples) {
	// if (tuple.getTupleType().equals(TupleType.Pair)) {
	// Pair pair = (Pair) tuple;
	// System.out.println("Pair:  " + tuple.getSource().getValue()
	// + "(" + tuple.getSource().getPosTag() + ")" + " --("
	// + pair.getRelation() + ")--> "
	// + tuple.getTarget().getValue() + "("
	// + tuple.getTarget().getPosTag() + ")");
	// } else if (tuple.getTupleType().equals(TupleType.Triple)) {
	// Triple triple = (Triple) tuple;
	// System.out.println("Triple:  " + tuple.getSource().getValue()
	// + "(" + tuple.getSource().getPosTag() + ")" + " --("
	// + triple.getRelationHOpinion() + ")--> "
	// + triple.getH().getValue() + "("
	// + triple.getH().getPosTag() + ")" + " --("
	// + triple.getRelationHTarget() + ")--> "
	// + tuple.getTarget().getValue() + "("
	// + tuple.getTarget().getPosTag() + ")");
	// }
	// }
	// }
}
