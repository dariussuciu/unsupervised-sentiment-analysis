package com.unsupervisedsentiment.analysis.modules.standfordparser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.Dependency;
import com.unsupervisedsentiment.analysis.model.Pair;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.services.InputDataMaker;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
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
		SemanticGraph dependencies = getSemanticGraphFromSentence(sentence);

		final Set<SemanticGraphEdge> edgeSet = dependencies.getEdgeSet();

		for (SemanticGraphEdge egi : edgeSet) {
			Pair tuple = new Pair();
			tuple.setDependency(Dependency.DIRECT_DEPENDENCY);
			tuple.setOpinion(new Word(egi.getSource().get(TextAnnotation.class), egi.getSource().get(
					PartOfSpeechAnnotation.class)));
			tuple.setTarget(new Word(egi.getTarget().get(TextAnnotation.class), egi.getTarget().get(
					PartOfSpeechAnnotation.class)));
			tuple.setRelation(egi.getRelation().toString());
			tuples.add(tuple);
		}
		return tuples;
	}

	public SemanticGraph getSemanticGraphFromSentence(CoreMap sentence) {

		// traversing the words in the current sentence
		// a CoreLabel is a CoreMap with additional token-specific methods
		for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
			// this is the text of the token
			String word = token.get(TextAnnotation.class);
			String pos = token.get(PartOfSpeechAnnotation.class);
			System.out.println("word-pos :" + word + "-" + pos);
			String ne = token.get(NamedEntityTagAnnotation.class);

			// http://nlp.stanford.edu/software/corenlp.shtml for more info
			// on NER
			System.out.println("NER:" + ne);
		}
		// this is the parse tree of the current sentence
		// Tree tree = sentence.get(TreeAnnotation.class);
		// this is the Stanford dependency graph of the current sentence
		return sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
	}

	private List<CoreMap> annotateSentences(String text) {
		Annotation document = new Annotation(text);
		coreNlp.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		return sentences;
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
			SemanticGraph graph = getSemanticGraphFromSentence(sentence);
			semanticGraphs.add(graph);
		}
		return semanticGraphs;
	}

}
