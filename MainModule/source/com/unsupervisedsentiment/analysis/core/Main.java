package com.unsupervisedsentiment.analysis.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

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
import com.unsupervisedsentiment.analysis.modules.evaluation.EvaluationService;
import com.unsupervisedsentiment.analysis.modules.standfordparser.NLPService;

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
			if (Helpers.existsObjectsForFile(storedEvaluationModelsDirectory,
					input.getFilename(), "EvaluationModel")) {
				evaluationModels = Helpers
						.<EvaluationModel> getObjectsFromFile(
								storedEvaluationModelsDirectory,
								input.getFilename(), "EvaluationModel");
			} else {
				evaluationModels = NLPService.getInstance()
						.getEvaluationModels(input.getContent());
				Helpers.saveObjectsToFile(evaluationModels,
						storedEvaluationModelsDirectory, input.getFilename(),
						"EvaluationModel");
			}

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
			
			HashSet<Tuple> resultTuples = new HashSet<Tuple>();
			
			resultTuples = combinedTuples;
			
//			for(Tuple tuple : combinedTuples)
//			{
//				double count = 0;
//				for(Word feature : tuple.getElements(ElementType.FEATURE))
//				{
//					for(Tuple otherTuple : combinedTuples)
//					{
//						if(!otherTuple.equals(tuple))
//						{
//							for(Word otherFeature : otherTuple.getElements(ElementType.FEATURE))
//							{
//								if(feature.getValue().equals(otherFeature.getValue()))
//								{
//									count++;
//								}
//							}	
//						}
//					}
//				}
//				
//				if(count / tuple.getElements(ElementType.FEATURE).size() > 7)
//				{
//					resultTuples.add(tuple);
//				}
//			}

			OutputWrapper outputFile = new OutputWrapper();

			outputFile.setAuthor(input.getAuthor());
			outputFile.setFilename(input.getFilename());
			outputFile.setSource(input.getSource());
			outputFile.setTuples(resultTuples);
			outputFiles.add(outputFile);

			EvaluationService evaluationService = new EvaluationService(
					evaluationModels, resultTuples);
			EvaluationResult evaluationResult = evaluationService.getResults();
			System.out
					.println("Precision : " + evaluationResult.getPrecision());
			System.out.println("Recall : " + evaluationResult.getRecall());
			System.out.println("-----------------------------------------");
			
			
//			EvaluationService evaluationService = new EvaluationService(
//					evaluationModels, opinionWordTuples);
//			EvaluationResult evaluationResult = evaluationService.getResults();
//			System.out
//					.println("Precision : " + evaluationResult.getPrecision());
//			System.out.println("Recall : " + evaluationResult.getRecall());
//			System.out.println("-----------------------------------------");
//			
//			evaluationService = new EvaluationService(
//					evaluationModels, featureTuples);
//			evaluationResult = evaluationService.getResults();
//			System.out
//					.println("Precision : " + evaluationResult.getPrecision());
//			System.out.println("Recall : " + evaluationResult.getRecall());
//			System.out.println("-----------------------------------------");

			metadataResults.add(new EvaluationMetadata(Constants.sdf
					.format(new Date()), config.getSeedType(), input
					.getFilename(), String.valueOf(seedWords.size()), String
					.valueOf(algorithm.getNumberOfIterations()), String
					.valueOf(elapsedTime), String.valueOf(evaluationResult
					.getPrecision()), String.valueOf(evaluationResult
					.getRecall()), RelationsContainer
					.getAllEnumElementsAsString()));

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
