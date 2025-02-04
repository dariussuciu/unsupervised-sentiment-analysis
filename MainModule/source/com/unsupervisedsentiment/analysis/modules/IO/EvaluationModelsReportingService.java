package com.unsupervisedsentiment.analysis.modules.IO;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.core.Config;
import com.unsupervisedsentiment.analysis.core.constants.Constants;
import com.unsupervisedsentiment.analysis.core.constants.relations.GeneralPosRelationContainer;
import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.EvaluationModel;
import com.unsupervisedsentiment.analysis.model.ResultPrecRecall;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.modules.evaluation.EvaluationMetadata;
import com.unsupervisedsentiment.analysis.modules.evaluation.EvaluationResult;
import com.unsupervisedsentiment.analysis.modules.evaluation.OpinionWordExtractionEvaluationService;
import com.unsupervisedsentiment.analysis.modules.evaluation.ScoreEvaluationService;
import com.unsupervisedsentiment.analysis.modules.evaluation.TargetExtractionEvaluationService;

/**
 * Service which covers detailed (per iteration) and metadata (only once)
 * reporting on evaluation.
 * 
 * @author Alex
 * 
 */
public class EvaluationModelsReportingService {

	private CacheService cacheService;
	private OutputService outputService;

	private final List<EvaluationModel> opinionWordEvaluationModels;
	private final List<EvaluationModel> targetEvaluationModels;
	private final List<EvaluationModel> scoreEvaluationModels;

	private Config config;
	private InputWrapper input;

	private List<LinkedHashMap<String, String>> detailedReportMaps = new ArrayList<LinkedHashMap<String, String>>();

	public EvaluationModelsReportingService(Config config, InputWrapper input) {

		this.config = config;
		this.input = input;
		cacheService = CacheService.getInstance();
		outputService = OutputService.getInstance(config);

		String storedEvaluationModelsDirectory = config.getEvaluationModelsDirectory();

		opinionWordEvaluationModels = cacheService.getStoredOrCreateNewEvaluationModel(storedEvaluationModelsDirectory,
				input, false, ElementType.OPINION_WORD, Constants.OPINION_WORD_EVAL_MODEL);
		targetEvaluationModels = cacheService.getStoredOrCreateNewEvaluationModel(storedEvaluationModelsDirectory,
				input, false, ElementType.FEATURE, Constants.TARGET_EVAL_MODEL);

		scoreEvaluationModels = cacheService.getStoredOrCreateNewEvaluationModel(storedEvaluationModelsDirectory,
				input, true, ElementType.OPINION_WORD, Constants.SCORE_EVAL_MODEL);
	}

	public EvaluationMetadata outputAndGetEvaluationMetadataResults(LinkedHashSet<Tuple> resultTuples,
			int numberOfSeedWords, int numberOfIterations, long elapsedTime) {
		OpinionWordExtractionEvaluationService extractionEvaluationService = new OpinionWordExtractionEvaluationService(
				opinionWordEvaluationModels, resultTuples);
		EvaluationResult extractionEvaluationResult = extractionEvaluationService.getResults();

		TargetExtractionEvaluationService targetEvaluationService = new TargetExtractionEvaluationService(
				targetEvaluationModels, resultTuples);
		EvaluationResult targetEvaluationResult = targetEvaluationService.getResults();

		if (config.getPrintEvaluationResultsToConsole()) {
			System.out.println("Precision : " + extractionEvaluationResult.getPrecision());
			System.out.println("Recall : " + extractionEvaluationResult.getRecall());
			System.out.println("-----------------------------------------");
			System.out.println("Target Extraction Precision : " + targetEvaluationResult.getPrecision());
			System.out.println("Target Extraction Recall : " + targetEvaluationResult.getRecall());
			System.out.println("-----------------------------------------");
		}

		EvaluationMetadata metadata = new EvaluationMetadata(Constants.sdf.format(new Date()), config.getSeedType(),
				input.getFilename(), String.valueOf(numberOfSeedWords), String.valueOf(numberOfIterations),
				String.valueOf(elapsedTime), new ResultPrecRecall(String.valueOf(extractionEvaluationResult
						.getPrecision()), String.valueOf(extractionEvaluationResult.getRecall())),
				new ResultPrecRecall(String.valueOf(targetEvaluationResult.getPrecision()), String
						.valueOf(targetEvaluationResult.getRecall())), config.getPolarityThreshold(),
				GeneralPosRelationContainer.getAllEnumElementsAsString());

		return metadata;
	};

	public void addToDetailedReportingMaps(LinkedHashSet<Tuple> resultTuples, int iterationNumber, long elapsedTime) {
		OpinionWordExtractionEvaluationService extractionEvaluationService = new OpinionWordExtractionEvaluationService(
				opinionWordEvaluationModels, resultTuples);
		TargetExtractionEvaluationService targetEvaluationService = new TargetExtractionEvaluationService(
				targetEvaluationModels, resultTuples);

		EvaluationResult extractionEvaluationResult = extractionEvaluationService.getResults();
		EvaluationResult targetEvaluationResult = targetEvaluationService.getResults();

		LinkedHashMap<String, String> detailedResultHashMap = createValueMapForDetailedReporting(iterationNumber,
				config.getSeedWords().size(), config.getTargetFrequencyThreshold(), config.getPolarityThreshold(),
				extractionEvaluationResult.getPrecision(), extractionEvaluationResult.getRecall(),
				targetEvaluationResult.getPrecision(), targetEvaluationResult.getRecall(),
				GeneralPosRelationContainer.getAllEnumElementsAsString(), extractionEvaluationResult,
				targetEvaluationResult, elapsedTime);

		detailedReportMaps.add(detailedResultHashMap);
	}

	/*
	 * Helpers
	 */

	private LinkedHashMap<String, String> createValueMapForDetailedReporting(int iterationNumber,
			int numberOfSeedWords, String targetFrequencyThreshold, String polarityThreshold,
			double opinionWordPrecision, double opinionWordRecall, double targetPrecision, double targetRecall,
			String allRelations, EvaluationResult extractionEvaluationResult, EvaluationResult targetEvaluationResult,
			long elapsedTime) {

		LinkedHashMap<String, String> detailedReportMap = new LinkedHashMap<String, String>();

		detailedReportMap.put("Iteration Number", String.valueOf(iterationNumber));
		detailedReportMap.put("Number of Seeds", String.valueOf(numberOfSeedWords));
		detailedReportMap.put("Target Frequency Threshold", targetFrequencyThreshold);
		detailedReportMap.put("Polarity Threshold", polarityThreshold);
		detailedReportMap.put("Opinion Word Precision", String.valueOf(opinionWordPrecision));
		detailedReportMap.put("Opinion Word Recall", String.valueOf(opinionWordRecall));
		detailedReportMap.put("Target Precision", String.valueOf(targetPrecision));
		detailedReportMap.put("Target Recall", String.valueOf(targetRecall));

		detailedReportMap.put("OW True Positive", String.valueOf(extractionEvaluationResult.getTruePositive()));
		detailedReportMap.put("OW False Negative", String.valueOf(extractionEvaluationResult.getFalseNegative()));
		detailedReportMap.put("OW False Positive", String.valueOf(extractionEvaluationResult.getFalsePositive()));

		detailedReportMap.put("Target True Positive", String.valueOf(targetEvaluationResult.getTruePositive()));
		detailedReportMap.put("Target False Negative", String.valueOf(targetEvaluationResult.getFalseNegative()));
		detailedReportMap.put("Target False Positive", String.valueOf(targetEvaluationResult.getFalsePositive()));

		detailedReportMap.put("Elapsed Time", String.valueOf(elapsedTime));

		return detailedReportMap;
	}

	public List<EvaluationModel> getOpinionWordEvaluationModels() {
		return opinionWordEvaluationModels;
	}

	public List<EvaluationModel> getTargetEvaluationModels() {
		return targetEvaluationModels;
	}

	public void outputDetailedReportMaps(String filename) {
		outputService.writeMapToDetailedReportsFile(filename, detailedReportMaps);
	}

	public void evaluateScoring(Set<Tuple> combinedTuples) {
		ScoreEvaluationService.performEvaluation(scoreEvaluationModels, combinedTuples);

	}

}
