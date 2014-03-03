package com.unsupervisedsentiment.analysis.modules.doublepropagation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		if (Helpers.existsStoredSemanticGraphsForFile(data.getFilename())) {
			data.setSentancesSemanticGraphs(Helpers
					.getSemanticGraphsFromFile(data.getFilename()));
		} else {
			List<SemanticGraph> semanticGraphsListForSentances = nlpService
					.createSemanticGraphsListForSentances(data.getInput());
			Helpers.saveSemanticGraphsToFile(semanticGraphsListForSentances,
					data.getFilename());
			data.setSentancesSemanticGraphs(semanticGraphsListForSentances);
		}
		data.setExpandedOpinionWords(seedWords);
	}

	public DoublePropagationData execute(HashSet<Tuple> seedWords) {
		initialize(seedWords);
		do {
			executeStep();
		} while (featuresIteration1.size() > 0
				&& opinionWordsIteration1.size() > 0);

		return data;
	}

	private void executeStep() {
		System.out.println("-------------Iteration Started-----------");
		resetIterationFeaturesAndOpinionWords();

		for (SemanticGraph semanticGraph : data.getSentancesSemanticGraphs()) {
			featuresIteration1.addAll(targetExtractorService
					.extractTargetsUsingR1(semanticGraph,
							data.getExpandedOpinionWords(),
							data.getFeatureTuples()));
			opinionWordsIteration1.addAll(opinionWordExtractorService
					.extractOpinionWordsUsingR4(semanticGraph,
							data.getExpandedOpinionWords(),
							data.getExpandedOpinionWordsTuples()));
		}

		data.getFeatureTuples().addAll(featuresIteration1);
		data.getExpandedOpinionWordsTuples().addAll(opinionWordsIteration1);

		for (SemanticGraph semanticGraph : data.getSentancesSemanticGraphs()) {
			featuresIteration2.addAll(targetExtractorService
					.extractTargetsUsingR3(semanticGraph, data.getFeatures(),
							data.getFeatureTuples()));
			opinionWordsIteration2.addAll(opinionWordExtractorService
					.extractOpinionWordsUsingR2(semanticGraph,
							data.getFeatures(),
							data.getExpandedOpinionWordsTuples()));
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
}
