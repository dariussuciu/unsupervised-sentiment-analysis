package com.unsupervisedsentiment.analysis.modules.doublepropagation;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.core.Initializer;
import com.unsupervisedsentiment.analysis.model.DoublePropagationData;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.IO.CacheService;
import com.unsupervisedsentiment.analysis.modules.IO.EvaluationModelsReportingService;
import com.unsupervisedsentiment.analysis.modules.standfordparser.NLPService;
import com.unsupervisedsentiment.analysis.modules.targetextraction.IOpinionWordExtractorService;
import com.unsupervisedsentiment.analysis.modules.targetextraction.ITargetExtractorService;
import com.unsupervisedsentiment.analysis.modules.targetextraction.OpinionWordExtractorService;
import com.unsupervisedsentiment.analysis.modules.targetextraction.TargetExtractorService;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;

public class DoublePropagationAlgorithm {

	private IOpinionWordExtractorService opinionWordExtractorService;
	private ITargetExtractorService targetExtractorService;
	private NLPService nlpService;
	private CacheService cacheService;

	private final DoublePropagationData data;
	private int numberOfIterations;

	private LinkedHashSet<Tuple> featuresIteration1;
	private LinkedHashSet<Tuple> opinionWordsIteration1;
	private LinkedHashSet<Tuple> featuresIteration2;
	private LinkedHashSet<Tuple> opinionWordsIteration2;

	public DoublePropagationAlgorithm(final DoublePropagationData data) {
		opinionWordExtractorService = OpinionWordExtractorService.getInstance();
		targetExtractorService = TargetExtractorService.getInstance();
		nlpService = NLPService.getInstance();
		cacheService = CacheService.getInstance();

		this.data = data;

		setNumberOfIterations(1);
	}

	public DoublePropagationData execute(final HashSet<Tuple> seedWords,
			EvaluationModelsReportingService reportingService) {
		initialize(seedWords);
		do {

			long elapsedTime = System.currentTimeMillis();
			executeStep();
			elapsedTime = System.currentTimeMillis() - elapsedTime;

			reportingService.addToDetailedReportingMaps(getResultTuplesForCurrentIteration(), getNumberOfIterations(),
					elapsedTime);

			setNumberOfIterations(getNumberOfIterations() + 1);
		} while (featuresIteration1.size() > 0 && opinionWordsIteration1.size() > 0);

		reportingService.outputDetailedReportMaps(data.getFilename());
        return data;
	}

	private void initialize(HashSet<Tuple> seedWords) {
		data.setExpandedOpinionWords(new LinkedHashSet<Tuple>());

		String storedSemanticGraphsDirectory = Initializer.getConfig().getStoredSemanticGraphsDirectory();

		List<SemanticGraph> cachedObjects = cacheService.getOrCreateSemanticGraphForFile(storedSemanticGraphsDirectory,
				data.getFilename(), data.getInput());

		data.setSentancesSemanticGraphs(cachedObjects);
		data.setExpandedOpinionWords(new LinkedHashSet<Tuple>(seedWords));
	}

	private void executeStep() {
		System.out.println("Iteration: " + getNumberOfIterations());
		resetIterationFeaturesAndOpinionWords();

		for (int i = 0; i < data.getSentancesSemanticGraphs().size(); i++) {
			final SemanticGraph semanticGraph = data.getSentancesSemanticGraphs().get(i);

            Set<Tuple> extractedFeatureTuples = targetExtractorService.extractTargetsUsingR1(semanticGraph,
                    data.getExpandedOpinionWords(), data.getFeatureTuples(), i);
            for (Tuple extractedFeatureTuple : extractedFeatureTuples) {
                String sentence = semanticGraph.toRecoveredSentenceString();
                String sentence2 = semanticGraph.toEnUncollapsedSentenceString();
                String a = sentence2;
                String wordBefore = null;
                String[] words = sentence.split(" ");
                if (words.length > 0) {
                    int opinionPosition = Helpers.getPositionOfWord(words, extractedFeatureTuple.getSource().getValue());
                    if (opinionPosition > 0) {
                        wordBefore = words[opinionPosition - 1];
                    }
                }
                extractedFeatureTuple.getSource().setWordBefore(wordBefore);
            }
			featuresIteration1.addAll(extractedFeatureTuples);

            Set<Tuple> extractedOpinionTuples = opinionWordExtractorService.extractOpinionWordsUsingR4(semanticGraph,
                    data.getExpandedOpinionWords(), data.getExpandedOpinionWordsTuples(), i);
            for (Tuple extractedOpinionTuple : extractedOpinionTuples) {
                String sentence = semanticGraph.toRecoveredSentenceString();
                String sentence2 = semanticGraph.toEnUncollapsedSentenceString();
                String a = sentence2;
                String wordBefore = null;
                String[] words = sentence.split(" ");
                if (words.length > 0) {
                    int opinionPosition = Helpers.getPositionOfWord(words, extractedOpinionTuple.getSource().getValue());
                    if (opinionPosition > 0) {
                        wordBefore = words[opinionPosition - 1];
                    }
                }
                extractedOpinionTuple.getSource().setWordBefore(wordBefore);
            }
			opinionWordsIteration1.addAll(extractedOpinionTuples);
        }

		data.getFeatureTuples().addAll(featuresIteration1);
		data.getExpandedOpinionWordsTuples().addAll(opinionWordsIteration1);

		for (int i = 0; i < data.getSentancesSemanticGraphs().size(); i++) {
			SemanticGraph semanticGraph = data.getSentancesSemanticGraphs().get(i);

            Set<Tuple> extractedFeatureTuples = targetExtractorService.extractTargetsUsingR3(semanticGraph, data.getFeatures(),
                    data.getFeatureTuples(), i);
            for (Tuple extractedOpinionTuple : extractedFeatureTuples) {
                String sentence = semanticGraph.toRecoveredSentenceString();
                String sentence2 = semanticGraph.toEnUncollapsedSentenceString();
                String a = sentence2;
                String wordBefore = null;
                String[] words = sentence.split(" ");
                if (words.length > 0) {
                    int opinionPosition = Helpers.getPositionOfWord(words, extractedOpinionTuple.getSource().getValue());
                    if (opinionPosition > 0) {
                        wordBefore = words[opinionPosition - 1];
                    }
                }
                extractedOpinionTuple.getSource().setWordBefore(wordBefore);
            }
			featuresIteration2.addAll(extractedFeatureTuples);

            Set<Tuple> extractedOpinionTuples = opinionWordExtractorService.extractOpinionWordsUsingR2(semanticGraph,
                    data.getFeatures(), data.getExpandedOpinionWordsTuples(), i);
            for (Tuple extractedOpinionTuple : extractedOpinionTuples) {
                String sentence = semanticGraph.toRecoveredSentenceString();
                String sentence2 = semanticGraph.toEnUncollapsedSentenceString();
                String a = sentence2;
                String wordBefore = null;
                String[] words = sentence.split(" ");
                if (words.length > 0) {
                    int opinionPosition = Helpers.getPositionOfWord(words, extractedOpinionTuple.getSource().getValue());
                    if (opinionPosition > 0) {
                        wordBefore = words[opinionPosition - 1];
                    }
                }
                extractedOpinionTuple.getSource().setWordBefore(wordBefore);
            }
            opinionWordsIteration2.addAll(extractedOpinionTuples);
		}

		featuresIteration1.addAll(featuresIteration2);
		opinionWordsIteration1.addAll(opinionWordsIteration2);
		data.getFeatureTuples().addAll(featuresIteration2);
		data.getExpandedOpinionWordsTuples().addAll(opinionWordsIteration2);
	}

	private void resetIterationFeaturesAndOpinionWords() {
		featuresIteration1 = new LinkedHashSet<Tuple>();
		opinionWordsIteration1 = new LinkedHashSet<Tuple>();
		featuresIteration2 = new LinkedHashSet<Tuple>();
		opinionWordsIteration2 = new LinkedHashSet<Tuple>();
	}

	private LinkedHashSet<Tuple> getResultTuplesForCurrentIteration() {
		LinkedHashSet<Tuple> resultTuples = new LinkedHashSet<Tuple>();

		resultTuples.addAll(data.getFeatureTuples());
		resultTuples.addAll(data.getExpandedOpinionWordsTuples());
		return resultTuples;
	}

	public int getNumberOfIterations() {
		return numberOfIterations;
	}

	private void setNumberOfIterations(int numberOfIterations) {
		this.numberOfIterations = numberOfIterations;
	}

	public DoublePropagationData getData() {
		return data;
	}

}
