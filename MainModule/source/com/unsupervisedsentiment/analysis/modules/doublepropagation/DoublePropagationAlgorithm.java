package com.unsupervisedsentiment.analysis.modules.doublepropagation;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.core.Initializer;
import com.unsupervisedsentiment.analysis.core.constants.relations.Dep_MRRel;
import com.unsupervisedsentiment.analysis.core.constants.relations.Pos_NNRel;
import com.unsupervisedsentiment.analysis.model.DoublePropagationData;
import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.IO.CacheService;
import com.unsupervisedsentiment.analysis.modules.IO.EvaluationModelsReportingService;
import com.unsupervisedsentiment.analysis.modules.services.filtering.pageRank.PageRankFilter;
import com.unsupervisedsentiment.analysis.modules.services.wordnet.WordNetService;
import com.unsupervisedsentiment.analysis.modules.standfordparser.NLPService;
import com.unsupervisedsentiment.analysis.modules.targetextraction.IOpinionWordExtractorService;
import com.unsupervisedsentiment.analysis.modules.targetextraction.ITargetExtractorService;
import com.unsupervisedsentiment.analysis.modules.targetextraction.OpinionWordExtractorService;
import com.unsupervisedsentiment.analysis.modules.targetextraction.TargetExtractorService;

import edu.smu.tspell.wordnet.SynsetType;
import edu.stanford.nlp.semgraph.SemanticGraph;

public class DoublePropagationAlgorithm {

	private IOpinionWordExtractorService opinionWordExtractorService;
	private ITargetExtractorService targetExtractorService;
	private NLPService nlpService;
	private CacheService cacheService;
	private WordNetService wordNetService;

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
		wordNetService = WordNetService.getInstance();

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

		PageRankFilter.calculateRank(data);
		
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
			
			SemanticGraph semanticGraph = data.getSentancesSemanticGraphs().get(i);
			//System.out.println(semanticGraph.toRecoveredSentenceString());
			//semanticGraph.prettyPrint();
			Set<Tuple> extractedFeaturesIteration1 = targetExtractorService.extractTargetsUsingR1(semanticGraph,
					data.getExpandedOpinionWords(), data.getFeatureTuples(), i);
			
			//Wordnet target filter
			Set<Tuple> filteredFeaturesIteration1 = new HashSet<Tuple>();

			for(Tuple extractedFeature : extractedFeaturesIteration1)
			{
				//wordNetService.getSynonims(extractedFeature.getTarget().getValue());
//				String result = wordNetService.searchByWord(extractedFeature.getTarget().getValue());
//				{
//					if(result != null)
//					{
						filteredFeaturesIteration1.add(extractedFeature);
//					}
//					else {
////						System.out.println(extractedFeature.getTarget().getValue());
//					}
//				}
			}
			
			featuresIteration1.addAll(filteredFeaturesIteration1);
			opinionWordsIteration1.addAll(opinionWordExtractorService.extractOpinionWordsUsingR4(semanticGraph,
					data.getExpandedOpinionWords(), data.getExpandedOpinionWordsTuples(), i));
		}

		data.getFeatureTuples().addAll(featuresIteration1);
		
		//Synonims and antonyms seed words
//		for(Tuple extractedOpinionWord : opinionWordsIteration1)
//		{
//			HashSet<String> extractedOpinionWordSynonyms = wordNetService.getSynonyms(extractedOpinionWord.getTarget().getValue());
//			HashSet<String> extractedOpinionWordAntonyms = wordNetService.getAntonyms(extractedOpinionWord.getTarget().getValue());
//			HashSet<Tuple> newSeedSynonymsWords = getSeedWords(extractedOpinionWordSynonyms);
//			HashSet<Tuple> newSeedAntonymsWords = getSeedWords(extractedOpinionWordAntonyms);
//			data.getExpandedOpinionWordsTuples().addAll(newSeedSynonymsWords);
//			data.getExpandedOpinionWordsTuples().addAll(newSeedAntonymsWords);
//		}
		
		data.getExpandedOpinionWordsTuples().addAll(opinionWordsIteration1);

		for (int i = 0; i < data.getSentancesSemanticGraphs().size(); i++) {
			SemanticGraph semanticGraph = data.getSentancesSemanticGraphs().get(i);
			
			Set<Tuple> extractedFeaturesIteration2 = targetExtractorService.extractTargetsUsingR3(semanticGraph, data.getFeatures(),
					data.getFeatureTuples(), i);
			
			//Wordnet target filter
			Set<Tuple> filteredFeaturesIteration2 = new HashSet<Tuple>();
			for(Tuple extractedFeature : extractedFeaturesIteration2)
			{
//				String result = wordNetService.searchByWord(extractedFeature.getTarget().getValue());
//				{
//					if(result != null)
//					{
						filteredFeaturesIteration2.add(extractedFeature);
//					}
//					else {
//						//System.out.println(extractedFeature.getTarget().getValue());
//					}
//				}
			}

			featuresIteration2.addAll(filteredFeaturesIteration2);
			
			//Synonims and antonyms seed words
//			for(Tuple extractedOpinionWord : opinionWordsIteration2)
//			{
//				HashSet<String> extractedOpinionWordSynonyms = wordNetService.getSynonyms(extractedOpinionWord.getTarget().getValue());
//				HashSet<String> extractedOpinionWordAntonyms = wordNetService.getAntonyms(extractedOpinionWord.getTarget().getValue());
//				HashSet<Tuple> newSeedSynonymsWords = getSeedWords(extractedOpinionWordSynonyms);
//				HashSet<Tuple> newSeedAntonymsWords = getSeedWords(extractedOpinionWordAntonyms);
//				data.getExpandedOpinionWordsTuples().addAll(newSeedSynonymsWords);
//				data.getExpandedOpinionWordsTuples().addAll(newSeedAntonymsWords);
//			}
			
			opinionWordsIteration2.addAll(opinionWordExtractorService.extractOpinionWordsUsingR2(semanticGraph,
					data.getFeatures(), data.getExpandedOpinionWordsTuples(), i));
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

	private HashSet<Tuple> getSeedWords(HashSet<String> words) {
		HashSet<Tuple> seedWords = new HashSet<Tuple>();
		for (String seedString : words) {
			Tuple seed = new Tuple();
			// TODO: not always JJ? what if adverbs
			Word word = new Word("JJ", seedString.trim(), ElementType.OPINION_WORD);
			seed.setSource(word);
			seed.setTupleType(TupleType.Seed);
			seed.setSentenceIndex(-1);
			seed.setSentence(null);
			seedWords.add(seed);
		}
		
		return seedWords;	
	}
}
