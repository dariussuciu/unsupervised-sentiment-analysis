package com.unsupervisedsentiment.analysis.modules.standfordparser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.unsupervisedsentiment.analysis.classification.Classification;
import com.unsupervisedsentiment.analysis.model.Dependency;
import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.EvaluationModel;
import com.unsupervisedsentiment.analysis.model.Pair;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.Word;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;

public class NLPService {

	private static NLPService instance;
	private final StanfordCoreNLP coreNlp;

	public static NLPService getInstance() {
		if (instance == null)
			instance = new NLPService();
		return instance;
	}

	private NLPService() {
		final Properties props = new Properties();
		props.put("annotators", StanfordCoreNLP.STANFORD_TOKENIZE + "," + StanfordCoreNLP.STANFORD_SSPLIT + ","
				+ StanfordCoreNLP.STANFORD_POS + "," + StanfordCoreNLP.STANFORD_LEMMA + ","
				+ StanfordCoreNLP.STANFORD_PARSE);
		coreNlp = new StanfordCoreNLP(props);
	}

	public List<CoreMap> getAnnotatedSentencesFromText(final String text) {
		final String lemmatizedText = doLemmatization(text);
		return annotateSentences(lemmatizedText);
	}

	public List<Tuple> getTuplesFromSentence(final CoreMap sentence) {
		final List<Tuple> tuples = new ArrayList<Tuple>();
		final SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);

		final Set<SemanticGraphEdge> edgeSet = dependencies.getEdgeSet();

		for (final SemanticGraphEdge egi : edgeSet) {
			final Pair tuple = new Pair();
			tuple.setDependency(Dependency.DIRECT_DEPENDENCY);
			tuple.setSource(new Word(egi.getSource().get(TextAnnotation.class), egi.getSource().get(
					PartOfSpeechAnnotation.class), ElementType.NONE));
			tuple.setTarget(new Word(egi.getTarget().get(TextAnnotation.class), egi.getTarget().get(
					PartOfSpeechAnnotation.class), ElementType.NONE));
			tuple.setRelation(egi.getRelation().toString());
			tuples.add(tuple);
		}
		return tuples;
	}

	private List<CoreMap> annotateSentences(final String text) {
		final Annotation document = new Annotation(text);
		coreNlp.annotate(document);
		final List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		return sentences;
	}

	public List<EvaluationModel> getEvaluationModels(final List<SemanticGraph> semanticGraphs, boolean forScoring,
			ElementType elementType) {
		final List<EvaluationModel> evaluationModels = new ArrayList<EvaluationModel>();

		for (int i = 0; i < semanticGraphs.size(); i++) {
			final String sentence = semanticGraphs.get(i).toRecoveredSentenceString();

			Matcher matcher;

			if (elementType.equals(ElementType.OPINION_WORD)) {
				String patternForOpinionWords = forScoring ? "### (\\w*\\@.?\\d\\.?\\d*\\b)" : "### (\\w*\\b)";
				final Pattern OPINION_WORD_PATTERN = Pattern.compile(patternForOpinionWords);
				matcher = OPINION_WORD_PATTERN.matcher(sentence);
			} else {
				String patternForTargets = forScoring ? "% % % (\\w*\\@.?\\d\\.?\\d*\\b)" : "% % % (\\w*\\b)";

				final Pattern TARGET_PATTERN = Pattern.compile(patternForTargets);
				matcher = TARGET_PATTERN.matcher(sentence);
			}

			final String cleanSentence = sentence.replaceAll("(### )|(% % % )|(\\$ \\$ \\$ )|(\\@\\d\\.\\d*)", "");
			while (matcher.find()) {
				String element = matcher.group(1);
				double score = new Double(Classification.DEFAULT_SCORE);
				if (forScoring) {
					String numberString = element.substring(element.indexOf("@") + 1, element.length());
					score = Double.parseDouble(numberString);
					element = element.substring(0, element.indexOf("@"));
				}
				final EvaluationModel model = new EvaluationModel(element, cleanSentence, i);
				model.setOpinionWordScore(score);
				evaluationModels.add(model);
			}
		}

		return evaluationModels;
	}

	private String doLemmatization(final String documentText) {
		final List<String> lemmas = new LinkedList<String>();
		String result = "";
		// create an empty Annotation just with the given text
		final Annotation document = new Annotation(documentText);

		// run all Annotators on this text
		coreNlp.annotate(document);

		// Iterate over all of the sentences found
		final List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (final CoreMap sentence : sentences) {
			// Iterate over all tokens in a sentence
			for (final CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Retrieve and add the lemma for each word into the
				// list of lemmas
				lemmas.add(token.get(LemmaAnnotation.class));
				result += token.get(LemmaAnnotation.class) + " ";
			}
		}
		return result;
	}

	public List<SemanticGraph> createSemanticGraphsListForSentances(final String s) {
		final List<SemanticGraph> semanticGraphs = new ArrayList<SemanticGraph>();
		final List<CoreMap> annotatedSentences = getAnnotatedSentencesFromText(s);
		for (final CoreMap sentence : annotatedSentences) {
			final SemanticGraph graph = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
			semanticGraphs.add(graph);
		}
		return semanticGraphs;
	}

}
