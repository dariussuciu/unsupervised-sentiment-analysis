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
import com.unsupervisedsentiment.analysis.modules.evaluation.OpinionWordExtractionEvaluationService;
import com.unsupervisedsentiment.analysis.modules.evaluation.ScoreEvaluationService;
import com.unsupervisedsentiment.analysis.modules.evaluation.TargetExtractionEvaluationService;
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
			if(Helpers.existsObjectsForFile(storedEvaluationModelsDirectory, input.getFilename(), "OpinionWordEvaluationModel"))
			{
				opinionWordEvaluationModels = Helpers.getStoredEvaluationModels(
						storedEvaluationModelsDirectory, input, false, ElementType.OPINION_WORD, "OpinionWordEvaluationModel");
			}
			else {
				opinionWordEvaluationModels = Helpers.getEvaluationModels(
						storedEvaluationModelsDirectory, input, false, ElementType.OPINION_WORD, "OpinionWordEvaluationModel");
			}
			
			if(Helpers.existsObjectsForFile(storedEvaluationModelsDirectory, input.getFilename(), "TargetEvaluationModel"))
			{
				targetEvaluationModels = Helpers.getStoredEvaluationModels(
						storedEvaluationModelsDirectory, input, false, ElementType.FEATURE, "TargetEvaluationModel");
			}
			else {
				targetEvaluationModels = Helpers.getEvaluationModels(
						storedEvaluationModelsDirectory, input, false, ElementType.FEATURE, "TargetEvaluationModel");
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

			LinkedHashSet<Tuple> combinedTuples = new LinkedHashSet<Tuple>();

			Classification classification = new Classification();
			classification.assignScoresBasedOnSeeds(featureTuples);
			// classification.assignSentiWordScores(featureTuples);

			classification = new Classification();
			classification.assignScoresBasedOnSeeds(opinionWordTuples);
			List<Tuple> opinionWordTuplesAL = new ArrayList<Tuple>(opinionWordTuples);
			System.out.println("Total score for this document: "
					+ classification.computeOverallScore(opinionWordTuplesAL));

			String targetString = "camera";
			System.out.println("Average score for target "
					+ targetString
					+ " is: "
					+ classification.getAverageScoreForTarget(targetString,
							opinionWordTuplesAL));
			// classification.assignSentiWordScores(opinionWordTuples);

			combinedTuples.addAll(featureTuples);
			combinedTuples.addAll(opinionWordTuples);

			LinkedHashSet<Tuple> resultTuples = new LinkedHashSet<Tuple>();

			resultTuples = combinedTuples;

			OutputWrapper outputFile = new OutputWrapper();

			outputFile.setAuthor(input.getAuthor());
			outputFile.setFilename(input.getFilename());
			outputFile.setSource(input.getSource());
			outputFile.setTuples(resultTuples);
			outputFiles.add(outputFile);

			OpinionWordExtractionEvaluationService extractionEvaluationService = new OpinionWordExtractionEvaluationService(
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

			//List<EvaluationModel> scoreEvaluationModels = Helpers
			//		.getEvaluationModels(storedEvaluationModelsDirectory,
			//				input, true, ElementType.OPINION_WORD, "OpinionWordEvaluationModel");
			//ScoreEvaluationService.performEvaluation(scoreEvaluationModels,
			//		combinedTuples);

//			List<Word> foundOpinionWords = Helpers.ExtractElements(resultTuples, ElementType.OPINION_WORD);
//			for(Word foundOpinionWord : foundOpinionWords)
//			{
//				Tuple seed = new Tuple();
//				seed.setSource(foundOpinionWord);
//				seed.setTupleType(TupleType.Seed);
//				seed.setSentenceIndex(-1);
//				seed.setSentence(null);
//				seedWords.add(seed);
//			}		
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
