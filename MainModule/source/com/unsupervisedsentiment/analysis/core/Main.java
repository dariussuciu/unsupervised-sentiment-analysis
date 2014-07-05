package com.unsupervisedsentiment.analysis.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
import com.unsupervisedsentiment.analysis.modules.evaluation.OpinionWordExtractionEvaluationService;
import com.unsupervisedsentiment.analysis.modules.evaluation.ScoreEvaluationService;
import com.unsupervisedsentiment.analysis.modules.evaluation.TargetExtractionEvaluationService;

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
		
		for (InputWrapper input : inputFiles) {
			System.out.println("-----------------------------------------");
			System.out.println("-------------NEW FILE-----------");
			System.out.println("-----------------------------------------");
			long currentTime = System.currentTimeMillis();
			DoublePropagationData inputData = new DoublePropagationData();

			inputData.setFilename(input.getFilename());

			List<EvaluationModel> opinionWordEvaluationModels = null;
			List<EvaluationModel> targetEvaluationModels = null;

			String storedEvaluationModelsDirectory = Initializer.getConfig()
					.getEvaluationModelsDirectory();
			if (Helpers.existsObjectsForFile(storedEvaluationModelsDirectory,
					input.getFilename(), "OpinionWordEvaluationModel")) {
				opinionWordEvaluationModels = Helpers
						.getStoredEvaluationModels(
								storedEvaluationModelsDirectory, input, false,
								ElementType.OPINION_WORD,
								"OpinionWordEvaluationModel");
			} else {
				opinionWordEvaluationModels = Helpers.getEvaluationModels(
						storedEvaluationModelsDirectory, input, false,
						ElementType.OPINION_WORD, "OpinionWordEvaluationModel");
			}

			if (Helpers.existsObjectsForFile(storedEvaluationModelsDirectory,
					input.getFilename(), "TargetEvaluationModel")) {
				targetEvaluationModels = Helpers.getStoredEvaluationModels(
						storedEvaluationModelsDirectory, input, false,
						ElementType.FEATURE, "TargetEvaluationModel");
			} else {
				targetEvaluationModels = Helpers.getEvaluationModels(
						storedEvaluationModelsDirectory, input, false,
						ElementType.FEATURE, "TargetEvaluationModel");
			}

			inputData.setInput(input.getOriginalContent());
			DoublePropagationAlgorithm algorithm = new DoublePropagationAlgorithm(
					inputData);

			algorithm.execute(seedWords);
			long elapsedTime = System.currentTimeMillis() - currentTime;
			System.out.println("Elapsed time: " + elapsedTime + " ms");

			Set<Tuple> featureTuples = algorithm.getData().getFeatureTuples();

			Set<Tuple> opinionWordTuples = algorithm.getData()
					.getExpandedOpinionWordsTuples();
			
			LinkedHashSet<Tuple> combinedTuplesNoScores = new LinkedHashSet<Tuple>();
			combinedTuplesNoScores.addAll(featureTuples);
			combinedTuplesNoScores.addAll(opinionWordTuples);
			
			OutputWrapper outputFileBeforeScore = new OutputWrapper();

			outputFileBeforeScore.setAuthor(input.getAuthor());
			outputFileBeforeScore.setFilename(input.getFilename()+ "-BoforeScore");
			outputFileBeforeScore.setSource(input.getSource());
			outputFileBeforeScore.setTuples(combinedTuplesNoScores);
			outputFiles.add(outputFileBeforeScore);

			outputService.writeOutput(outputFiles);
			
			outputFiles.remove(outputFileBeforeScore);

			Classification classification = new Classification();
			Set<Tuple> assignedFeatureTuples = classification.assignScoresBasedOnSeeds(featureTuples);
			// classification.assignSentiWordScores(featureTuples);

			classification = new Classification();
			Set<Tuple> assignedOpinionWordTuples = classification.assignScoresBasedOnSeeds(opinionWordTuples);
			/*
			Classification classification2 = new Classification();
			classification2.assignScoresBasedOnSeeds(featureTuples);
			// classification.assignSentiWordScores(featureTuples);

			classification2 = new Classification();
			classification2.assignScoresBasedOnSeeds(opinionWordTuples);
*/			
			LinkedHashSet<Tuple> combinedTuples = new LinkedHashSet<Tuple>();
			combinedTuples.addAll(assignedFeatureTuples);
			combinedTuples.addAll(assignedOpinionWordTuples);

			LinkedHashSet<Tuple> resultTuples = new LinkedHashSet<Tuple>();

			resultTuples = combinedTuples;

			OutputWrapper outputFileAfterScore = new OutputWrapper();

			outputFileAfterScore.setAuthor(input.getAuthor());
			outputFileAfterScore.setFilename(input.getFilename()+ "-AfterScore");
			outputFileAfterScore.setSource(input.getSource());
			outputFileAfterScore.setTuples(combinedTuples);
			outputFiles.add(outputFileAfterScore);

			/*OpinionWordExtractionEvaluationService extractionEvaluationService = new OpinionWordExtractionEvaluationService(
					opinionWordEvaluationModels, resultTuples);
			EvaluationResult extractionEvaluationResult = extractionEvaluationService
					.getResults();
			System.out.println("Precision : "
					+ extractionEvaluationResult.getPrecision());
			System.out.println("Recall : "
					+ extractionEvaluationResult.getRecall());
			System.out.println("-----------------------------------------");

			TargetExtractionEvaluationService targetEvaluationService = new TargetExtractionEvaluationService(
					targetEvaluationModels, resultTuples);
			EvaluationResult targetEvaluationResult = targetEvaluationService
					.getResults();
			System.out.println("Target Extraction Precision : "
					+ targetEvaluationResult.getPrecision());
			System.out.println("Target Extraction Recall : "
					+ targetEvaluationResult.getRecall());
			System.out.println("-----------------------------------------");

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
							input, true, ElementType.OPINION_WORD,
							"ScoreEvaluationModel");
			
			ScoreEvaluationService.performEvaluation(scoreEvaluationModels,
					combinedTuples);
*/
			List<Word> foundOpinionWords = Helpers.ExtractElements(resultTuples, ElementType.OPINION_WORD);
			for(Word foundOpinionWord : foundOpinionWords)
			{
				Tuple seed = new Tuple();
				seed.setSource(foundOpinionWord);
				seed.setTupleType(TupleType.Seed);
				seed.setSentenceIndex(-1);
				seed.setSentence(null);
				seedWords.add(seed);
			}		
			List<Tuple> opinionWordTuplesAL = new ArrayList<Tuple>(
					assignedFeatureTuples);
			 System.out.println("Total score for this document: "
			 + classification.computeOverallScore(opinionWordTuplesAL));

			 String targetString = "camera";
			 System.out.println("Average score for target "
			 + targetString
			 + " is: "
			 + classification.getAverageScoreForTarget(targetString,
			 opinionWordTuplesAL));

			HashMap<Double, Integer> distribution = classification.computeScoreDistribution(opinionWordTuplesAL);
			System.out.println ("Score distribution for this corpus: " + distribution.toString());
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
}
