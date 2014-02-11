package com.unsupervisedsentiment.analysis.modules.doublepropagation;

import java.util.HashSet;
import java.util.List;

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
	private HashSet<Tuple> featuresIteration1;
	private HashSet<Tuple> opinionWordsIteration1;
	private HashSet<Tuple> featuresIteration2;
	private HashSet<Tuple> opinionWordsIteration2;
	private NLPService nlpService;

	public DoublePropagationAlgorithm(DoublePropagationData data) {
		opinionWordExtractorService = new OpinionWordExtractorService();
		targetExtractorService = new TargetExtractorService();
		this.data = data;
		nlpService = NLPService.getInstance();
	}

	private void initialize() {
		data.setExpandedOpinionWordDictionary(new HashSet<Tuple>());
		featuresIteration1 = new HashSet<Tuple>();
		opinionWordsIteration1 = new HashSet<Tuple>();
		featuresIteration2 = new HashSet<Tuple>();
		opinionWordsIteration2 = new HashSet<Tuple>();
		List<CoreMap> annotatedSentences = nlpService
				.getAnnotatedSentencesFromText(data.getInput());
		for (CoreMap sentence : annotatedSentences) {
			SemanticGraph graph = nlpService
					.getSemanticGraphFromSentence(sentence);
			InputDataMaker inputGenerator = new InputDataMaker(graph);
			data.getAllOpinionWords().add(inputGenerator.opinionWords);
			data.getAllTargets().add(inputGenerator.targets);
		}
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
		/*
		 * for(String sentence : data.getInputSentences()){
		 * featuresIteration1.addAll
		 * (targetExtractorService.ExtractTargetUsingR1(sentence));
		 * opinionWordsIteration1
		 * .addAll(opinionWordExtractorService.ExtractOpinionWordR2(sentence));
		 * }
		 */

		data.getFeaturesDictionary().addAll(featuresIteration1);
		data.getExpandedOpinionWordDictionary().addAll(opinionWordsIteration1);

		/*
		 * for(String sentence : data.getInputSentences()){
		 * featuresIteration2.addAll
		 * (targetExtractorService.ExtractTargetUsingR3(sentence));
		 * opinionWordsIteration2
		 * .addAll(opinionWordExtractorService.ExtractOpinionWordR4(sentence));
		 * }
		 */

		featuresIteration1.addAll(featuresIteration2);
		opinionWordsIteration1.addAll(opinionWordsIteration1);
		data.getFeaturesDictionary().addAll(featuresIteration2);
		data.getExpandedOpinionWordDictionary().addAll(opinionWordsIteration2);
	}
}
