package com.unsupervisedsentiment.analysis.modules.standfordparser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private StanfordCoreNLP coreNlp;

	public static NLPService getInstance() {
		if (instance == null)
			instance = new NLPService();
		return instance;
	}

	private NLPService() {
		Properties props = new Properties();
		props.put("annotators", StanfordCoreNLP.STANFORD_TOKENIZE + "," + StanfordCoreNLP.STANFORD_SSPLIT + ","
				+ StanfordCoreNLP.STANFORD_POS + "," + StanfordCoreNLP.STANFORD_LEMMA + ","
				+ StanfordCoreNLP.STANFORD_PARSE);
		coreNlp = new StanfordCoreNLP(props);
	}

	public List<CoreMap> getAnnotatedSentencesFromText(String text) {
		String lemmatizedText = doLemmatization(text);
		return annotateSentences(lemmatizedText);
	}

	public List<Tuple> getTuplesFromSentence(CoreMap sentence) {
		List<Tuple> tuples = new ArrayList<Tuple>();
		SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);

		final Set<SemanticGraphEdge> edgeSet = dependencies.getEdgeSet();

		for (SemanticGraphEdge egi : edgeSet) {
			Pair tuple = new Pair();
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

	private List<CoreMap> annotateSentences(String text) {
		Annotation document = new Annotation(text);
		coreNlp.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		return sentences;
	}
	
	public List<EvaluationModel> getEvaluationModels(String text) {
		List<SemanticGraph> semanticGraphs = createSemanticGraphsListForSentances(text);
		List<EvaluationModel> evaluationModels = new ArrayList<EvaluationModel>();
		
		for(int i=0; i<semanticGraphs.size(); i++)
		{
			String sentence = semanticGraphs.get(i).toRecoveredSentenceString();
			String cleanSentence = sentence.replaceAll("(### )|(% % % )|(\\$ \\$ \\$ )", "");
			Pattern MY_PATTERN = Pattern.compile("### (\\w*\\b)");
			Matcher m = MY_PATTERN.matcher(sentence);
				while (m.find()) {
				    String opinionWord = m.group(1);
					EvaluationModel model = new EvaluationModel(opinionWord, cleanSentence, i);
					evaluationModels.add(model);
				}
		}
		
		return evaluationModels;
	}

	private String doLemmatization(String documentText) {
		List<String> lemmas = new LinkedList<String>();
		String result = "";
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(documentText);

		// run all Annotators on this text
		coreNlp.annotate(document);

		// Iterate over all of the sentences found
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			// Iterate over all tokens in a sentence
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Retrieve and add the lemma for each word into the
				// list of lemmas
				lemmas.add(token.get(LemmaAnnotation.class));
				result += token.get(LemmaAnnotation.class) + " ";
			}
		}
		return result;
	}

	public List<SemanticGraph> createSemanticGraphsListForSentances(String s) {
		List<SemanticGraph> semanticGraphs = new ArrayList<SemanticGraph>();
		List<CoreMap> annotatedSentences = getAnnotatedSentencesFromText(s);
		for (CoreMap sentence : annotatedSentences) {
			SemanticGraph graph = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
			semanticGraphs.add(graph);
		}
		return semanticGraphs;
	}

}
