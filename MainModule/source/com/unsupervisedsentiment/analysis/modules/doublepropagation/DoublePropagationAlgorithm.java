package com.unsupervisedsentiment.analysis.modules.doublepropagation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.core.Initializer;
import com.unsupervisedsentiment.analysis.model.DoublePropagationData;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.modules.standfordparser.NLPService;
import com.unsupervisedsentiment.analysis.modules.targetextraction.IOpinionWordExtractorService;
import com.unsupervisedsentiment.analysis.modules.targetextraction.ITargetExtractorService;
import com.unsupervisedsentiment.analysis.modules.targetextraction.OpinionWordExtractorService;
import com.unsupervisedsentiment.analysis.modules.targetextraction.TargetExtractorService;

import edu.stanford.nlp.semgraph.SemanticGraph;

public class DoublePropagationAlgorithm {

	private IOpinionWordExtractorService opinionWordExtractorService;
	private ITargetExtractorService targetExtractorService;
	private DoublePropagationData data;
	private int numberOfIterations;

	public DoublePropagationData getData() {
		return data;
	}

	private Set<Tuple> featuresIteration1;
	private Set<Tuple> opinionWordsIteration1;
	private Set<Tuple> featuresIteration2;
	private Set<Tuple> opinionWordsIteration2;
	private NLPService nlpService;

	public DoublePropagationAlgorithm(DoublePropagationData data) {
		opinionWordExtractorService = new OpinionWordExtractorService();
		targetExtractorService = new TargetExtractorService();
		this.data = data;
		nlpService = NLPService.getInstance();
	}

	private void initialize(HashSet<Tuple> seedWords) {
		data.setExpandedOpinionWords(new HashSet<Tuple>());
		String storedSemanticGraphsDirectory = Initializer.getConfig()
				.getStoredSemanticGraphsDirectory();
		if (Helpers.existsObjectsForFile(storedSemanticGraphsDirectory,
				data.getFilename(), "SemanticGraph")) {
			data.setSentancesSemanticGraphs(Helpers
					.<SemanticGraph> getObjectsFromFile(
							storedSemanticGraphsDirectory, data.getFilename(),
							"SemanticGraph"));
		} else {
			List<SemanticGraph> semanticGraphsListForSentances = nlpService
					.createSemanticGraphsListForSentances(data.getInput());
			Helpers.saveObjectsToFile(semanticGraphsListForSentances,
					storedSemanticGraphsDirectory, data.getFilename(),
					"SemanticGraph");
			data.setSentancesSemanticGraphs(semanticGraphsListForSentances);
		}
		data.setExpandedOpinionWords(new HashSet<Tuple>(seedWords));
	}

	public DoublePropagationData execute(final HashSet<Tuple> seedWords) {
		initialize(seedWords);
		do {
			executeStep();
		} while (featuresIteration1.size() > 0
				&& opinionWordsIteration1.size() > 0);

		return data;
	}

	private void executeStep() {
		System.out.println("Iteration: " + getNumberOfIterations());
		setNumberOfIterations(getNumberOfIterations() + 1);
		resetIterationFeaturesAndOpinionWords();

		for (int i = 0; i < data.getSentancesSemanticGraphs().size(); i++) {
			SemanticGraph semanticGraph = data.getSentancesSemanticGraphs()
					.get(i);
			featuresIteration1.addAll(targetExtractorService
					.extractTargetsUsingR1(semanticGraph,
							data.getExpandedOpinionWords(),
							data.getFeatureTuples(), i));
			opinionWordsIteration1.addAll(opinionWordExtractorService
					.extractOpinionWordsUsingR4(semanticGraph,
							data.getExpandedOpinionWords(),
							data.getExpandedOpinionWordsTuples(), i));
		}

		data.getFeatureTuples().addAll(featuresIteration1);
		data.getExpandedOpinionWordsTuples().addAll(opinionWordsIteration1);

		for (int i = 0; i < data.getSentancesSemanticGraphs().size(); i++) {
			SemanticGraph semanticGraph = data.getSentancesSemanticGraphs()
					.get(i);
			featuresIteration2.addAll(targetExtractorService
					.extractTargetsUsingR3(semanticGraph, data.getFeatures(),
							data.getFeatureTuples(), i));
			opinionWordsIteration2.addAll(opinionWordExtractorService
					.extractOpinionWordsUsingR2(semanticGraph,
							data.getFeatures(),
							data.getExpandedOpinionWordsTuples(), i));
		}

		featuresIteration1.addAll(featuresIteration2);
		opinionWordsIteration1.addAll(opinionWordsIteration2);
		data.getFeatureTuples().addAll(featuresIteration2);
		data.getExpandedOpinionWordsTuples().addAll(opinionWordsIteration2);
	}

	void resetIterationFeaturesAndOpinionWords() {
		featuresIteration1 = new HashSet<Tuple>();
		opinionWordsIteration1 = new HashSet<Tuple>();
		featuresIteration2 = new HashSet<Tuple>();
		opinionWordsIteration2 = new HashSet<Tuple>();
	}

	public int getNumberOfIterations() {
		return numberOfIterations;
	}

	private void setNumberOfIterations(int numberOfIterations) {
		this.numberOfIterations = numberOfIterations;
	}
}
