package com.unsupervisedsentiment.analysis.modules.doublepropagation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.DoublePropagationData;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.modules.targetextraction.IOpinionWordExtractorService;
import com.unsupervisedsentiment.analysis.modules.targetextraction.ITargetExtractorService;
import com.unsupervisedsentiment.analysis.modules.targetextraction.OpinionWordExtractorService;
import com.unsupervisedsentiment.analysis.modules.targetextraction.TargetExtractorService;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.services.InputDataMaker;
import com.unsupervisedsentiment.analysis.modules.standfordparser.NLPService;

import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.util.CoreMap;

public class DoublePropagationAlgorithm {

	private IOpinionWordExtractorService opinionWordExtractorService;
	private ITargetExtractorService targetExtractorService;
	private DoublePropagationData data;
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

	private void initialize() {
		data.setExpandedOpinionWords(new HashSet<Tuple>());
		data.setSentancesSemanticGraphs(nlpService
				.createSemanticGraphsListForSentances(data.getInput()));
	}

	public DoublePropagationData execute() {
		initialize();
		do {
			executeStep();
		} while (featuresIteration1.size() > 0
				&& opinionWordsIteration1.size() > 0);

		return data;
	}

	private void executeStep() {
		resetIterationFeaturesAndOpinionWords();

		for (SemanticGraph semanticGraph : data.getSentancesSemanticGraphs()) {
			featuresIteration1.addAll(targetExtractorService
					.extractTargetUsingR1(semanticGraph));
			opinionWordsIteration1.addAll(opinionWordExtractorService
					.extractOpinionWordR4(semanticGraph));
		}

		data.getFeatures().addAll(featuresIteration1);
		data.getExpandedOpinionWords().addAll(opinionWordsIteration1);

		for (SemanticGraph semanticGraph : data.getSentancesSemanticGraphs()) {
			featuresIteration2.addAll(targetExtractorService
					.extractTargetUsingR3(semanticGraph));
			opinionWordsIteration2.addAll(opinionWordExtractorService
					.extractOpinionWordR2(semanticGraph));
		}

		featuresIteration1.addAll(featuresIteration2);
		opinionWordsIteration1.addAll(opinionWordsIteration2);
		data.getFeatures().addAll(featuresIteration2);
		data.getExpandedOpinionWords().addAll(opinionWordsIteration2);
	}

	void resetIterationFeaturesAndOpinionWords() {
		featuresIteration1 = new HashSet<Tuple>();
		opinionWordsIteration1 = new HashSet<Tuple>();
		featuresIteration2 = new HashSet<Tuple>();
		opinionWordsIteration2 = new HashSet<Tuple>();
	}
}
