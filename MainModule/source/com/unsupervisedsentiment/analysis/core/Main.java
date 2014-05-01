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
import com.unsupervisedsentiment.analysis.model.ResultPrecRecall;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.IO.CacheService;
import com.unsupervisedsentiment.analysis.modules.IO.EvaluationModelsReportingService;
import com.unsupervisedsentiment.analysis.modules.IO.InputService;
import com.unsupervisedsentiment.analysis.modules.IO.InputWrapper;
import com.unsupervisedsentiment.analysis.modules.IO.OutputService;
import com.unsupervisedsentiment.analysis.modules.IO.OutputWrapper;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.DoublePropagationAlgorithm;
import com.unsupervisedsentiment.analysis.modules.evaluation.EvaluationMetadata;
import com.unsupervisedsentiment.analysis.modules.evaluation.EvaluationResult;
import com.unsupervisedsentiment.analysis.modules.evaluation.OpinionWordExtractionEvaluationService;
import com.unsupervisedsentiment.analysis.modules.evaluation.TargetExtractionEvaluationService;

public class Main {

	private static InputService inputService;
	private static Config config;
	private static OutputService outputService;
	private static CacheService cacheService;

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
			// TODO: not always JJ? what if adverbs
			Word word = new Word("JJ", seedString.trim(), ElementType.OPINION_WORD);
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

			inputData.setInput(input.getOriginalContent());
			DoublePropagationAlgorithm algorithm = new DoublePropagationAlgorithm(inputData);

			// the evaluation models are retrieved or created in the
			// reportingService
			EvaluationModelsReportingService reportingService = new EvaluationModelsReportingService(config, input);

			algorithm.execute(seedWords, reportingService);
			// alg is done
			long elapsedTime = System.currentTimeMillis() - currentTime;
			System.out.println("Elapsed time: " + elapsedTime + " ms");

			Set<Tuple> featureTuples = algorithm.getData().getFeatureTuples();
			Set<Tuple> opinionWordTuples = algorithm.getData().getExpandedOpinionWordsTuples();

			LinkedHashSet<Tuple> resultTuples = new LinkedHashSet<Tuple>();
			resultTuples.addAll(featureTuples);
			resultTuples.addAll(opinionWordTuples);

			// this output should only be written once per file
			outputFiles.add(outputService.createOutputWrapperFromInput(input, resultTuples));
			metadataResults.add(reportingService.outputAndGetEvaluationMetadataResults(resultTuples, seedWords.size(),
					algorithm.getNumberOfIterations(), elapsedTime));

			/*
			 * Vlad's part
			 */
			Classification classification = new Classification();
			classification.assignScoresBasedOnSeeds(featureTuples);
			// classification.assignSentiWordScores(featureTuples);

			classification = new Classification();
			classification.assignScoresBasedOnSeeds(opinionWordTuples);

			// List<EvaluationModel> scoreEvaluationModels = Helpers
			// .getEvaluationModels(storedEvaluationModelsDirectory,
			// input, true, ElementType.OPINION_WORD,
			// "OpinionWordEvaluationModel");
			//
			// ScoreEvaluationService.performEvaluation(scoreEvaluationModels,
			// combinedTuples);

			// List<Word> foundOpinionWords =
			// Helpers.ExtractElements(resultTuples, ElementType.OPINION_WORD);
			// for(Word foundOpinionWord : foundOpinionWords)
			// {
			// Tuple seed = new Tuple();
			// seed.setSource(foundOpinionWord);
			// seed.setTupleType(TupleType.Seed);
			// seed.setSentenceIndex(-1);
			// seed.setSentence(null);
			// seedWords.add(seed);
			// }
			List<Tuple> opinionWordTuplesAL = new ArrayList<Tuple>(featureTuples);
			// System.out.println("Total score for this document: "
			// + classification.computeOverallScore(opinionWordTuplesAL));

			// String targetString = "camera";
			// System.out.println("Average score for target "
			// + targetString
			// + " is: "
			// + classification.getAverageScoreForTarget(targetString,
			// opinionWordTuplesAL));

			HashMap<Double, Integer> distribution = classification.computeScoreDistribution(opinionWordTuplesAL);
			// classification.assignSentiWordScores(opinionWordTuples);
		}
		outputService.writeToEvaluationMetadataCsv(metadataResults);
		outputService.writeOutput(outputFiles);
	}

	private static void initialize() {
		config = Initializer.getConfig();

		inputService = InputService.getInstance(config);
		outputService = OutputService.getInstance(config);
		cacheService = CacheService.getInstance();

		inputFiles = inputService.getTextFromFile();
		outputFiles = new ArrayList<OutputWrapper>();
		metadataResults = new ArrayList<EvaluationMetadata>();
	}
}
