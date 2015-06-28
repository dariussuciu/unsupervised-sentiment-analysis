package com.unsupervisedsentiment.analysis.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.classification.Classification;
import com.unsupervisedsentiment.analysis.model.DoublePropagationData;
import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.FileVisualisationModel;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.IO.EvaluationModelsReportingService;
import com.unsupervisedsentiment.analysis.modules.IO.InputService;
import com.unsupervisedsentiment.analysis.modules.IO.InputWrapper;
import com.unsupervisedsentiment.analysis.modules.IO.OutputService;
import com.unsupervisedsentiment.analysis.modules.IO.OutputWrapper;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.DoublePropagationAlgorithm;
import com.unsupervisedsentiment.analysis.modules.evaluation.EvaluationMetadata;
import com.unsupervisedsentiment.analysis.visualization.VisualisationService;

public class AlgorithmRunner {

	private Config config;
	private InputService inputService;
	private OutputService outputService;
	private VisualisationService visualisationService;

	private List<InputWrapper> inputFiles;
	private List<OutputWrapper> outputFiles;
	private List<EvaluationMetadata> metadataResults;
	private List<FileVisualisationModel> fileVisualisationModels;

	private HashSet<Tuple> seedWords;

	public AlgorithmRunner() {
		initialize();
	}

	public void runAlgorithm() {
		initialize();

		for (InputWrapper input : inputFiles) {
			computeFile(input);
		}
		outputService.writeToEvaluationMetadataCsv(metadataResults);
		outputService.writeOutput(outputFiles);
	}

	public List<FileVisualisationModel> getFileVisualisationModels() {
		return fileVisualisationModels;
	}

	private void computeFile(InputWrapper input) {
		System.out.println("-----------------------------------------");
		System.out.println("-----------------NEW FILE----------------");
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

		long elapsedTime = System.currentTimeMillis() - currentTime;
		System.out.println("Elapsed time: " + elapsedTime + " ms");

		Set<Tuple> featureTuples = algorithm.getData().getFeatureTuples();
		Set<Tuple> opinionWordTuples = algorithm.getData().getExpandedOpinionWordsTuples();

		LinkedHashSet<Tuple> resultTuples = new LinkedHashSet<Tuple>();
		resultTuples.addAll(featureTuples);
		resultTuples.addAll(opinionWordTuples);

		// this output should only be written once per file
		outputFiles.add(outputService.createOutputWrapperFromInput(input, resultTuples));
		EvaluationMetadata evaluationMetadataResults = reportingService.outputAndGetEvaluationMetadataResults(
				resultTuples, seedWords.size(), algorithm.getNumberOfIterations(), elapsedTime);
		metadataResults.add(evaluationMetadataResults);

		// Vlad's part
		Classification classification = new Classification();
		LinkedHashSet<Tuple> assignedResults = new LinkedHashSet<Tuple>(classification.assignScoresBasedOnSeeds(
				resultTuples, inputData.getSentancesSemanticGraphs(), false));

		reportingService.evaluateScoring(assignedResults);

		List<Tuple> opinionWordTuplesAL = new ArrayList<Tuple>(resultTuples);

		System.out.println("Total score for this document: " + classification.computeOverallScore(opinionWordTuplesAL));

		String targetString = "casino";
		System.out.println("Average score for target " + targetString + " is: "
				+ classification.getAverageScoreForTarget(targetString, opinionWordTuplesAL));

		HashMap<Double, Integer> distribution = classification.computeScoreDistribution(opinionWordTuplesAL);
		System.out.println(distribution.toString());

		// visualization
		fileVisualisationModels.add(visualisationService.createFileVisualisationModel(assignedResults, distribution,
				evaluationMetadataResults));
	}

	/*
	 * ***************** Init methods **********************
	 */
	private void initialize() {
		config = Initializer.getConfig();

		inputService = InputService.getInstance(config);
		outputService = OutputService.getInstance(config);
		visualisationService = VisualisationService.getInstance();

		inputFiles = inputService.getTextFromFile();
		outputFiles = new ArrayList<OutputWrapper>();
		metadataResults = new ArrayList<EvaluationMetadata>();
		fileVisualisationModels = new ArrayList<FileVisualisationModel>();
		seedWords = new HashSet<Tuple>();

		config.setSeedWords(inputService.getSeedWordsFromFile());

		initSeedWords();

		Initializer.setConfig(config);
	}

	private void initSeedWords() {
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
	}

}
