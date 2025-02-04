package com.unsupervisedsentiment.analysis.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Ignore;

import com.unsupervisedsentiment.analysis.core.constants.StanfordNLPTestConstants;
import com.unsupervisedsentiment.analysis.model.Dependency;
import com.unsupervisedsentiment.analysis.model.Pair;
import com.unsupervisedsentiment.analysis.model.Word;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
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

public class StanfordNLPTests extends TestCase {
	protected StanfordCoreNLP coreNlp;

	public StanfordNLPTests() {
		try {
			setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		Properties props = new Properties();
		/*
		 * What you supply as the second argument to the props.put function de-
		 * pends on what you want to do with it. Generally, you would want to be
		 * frugal and use only ones that you are going to need, as each one of
		 * them tend to make processing slower. Having said that, you would
		 * almost always need tokenize and ssplit (sentence splitting), so be
		 * sure to in- clude them. A list of possible values is available as
		 * static values from the StanfordCoreNLP class.
		 * StanfordCoreNLP.STANFORD_TOKENIZE; (ex)
		 */
		props.put("annotators", StanfordCoreNLP.STANFORD_TOKENIZE + "," + StanfordCoreNLP.STANFORD_SSPLIT + ","
				+ StanfordCoreNLP.STANFORD_POS + "," + StanfordCoreNLP.STANFORD_LEMMA + ","
				+ StanfordCoreNLP.STANFORD_PARSE);
		coreNlp = new StanfordCoreNLP(props);
	}

	/**
	 * Basically, splits the phrase in sentences.
	 */
	@Ignore
	public void testSentencesAnnotation() {
		String oneSentence = StanfordNLPTestConstants.SENTENCE_ONE;
		Annotation document = new Annotation(oneSentence);
		coreNlp.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		assertEquals(sentences.size(), 1);

		String twoSentence = StanfordNLPTestConstants.SENTENCE_TWO;
		document = new Annotation(twoSentence);
		coreNlp.annotate(document);
		sentences = document.get(SentencesAnnotation.class);
		assertEquals(sentences.size(), 2);

		int n = 20;
		String nSentence = StanfordNLPTestConstants.generateNSentencesString(n);
		document = new Annotation(nSentence);
		coreNlp.annotate(document);
		sentences = document.get(SentencesAnnotation.class);
		printSentences(sentences);
		assertEquals(sentences.size(), n);
	}

	/**
	 * Tokens = words + punctuations
	 */
	public void testTokensAnnotation() {
		System.out.println("TEST TOKENS ANNOTATION");
		String oneSentence = StanfordNLPTestConstants.SENTENCE_ONE;
		Annotation document = new Annotation(oneSentence);
		coreNlp.annotate(document);
		List<CoreLabel> tokens = document.get(TokensAnnotation.class);
		assertEquals(tokens.size(), 10);

		String twoSentence = StanfordNLPTestConstants.SENTENCE_TWO;
		document = new Annotation(twoSentence);
		coreNlp.annotate(document);
		tokens = document.get(TokensAnnotation.class);
		assertEquals(tokens.size(), 18);

		int n = 20;
		String nSentence = StanfordNLPTestConstants.generateNSentencesString(n);
		document = new Annotation(nSentence);
		coreNlp.annotate(document);
		tokens = document.get(TokensAnnotation.class);
		assertEquals(tokens.size(), 10 * n);

		StanfordCoreNLP.clearAnnotatorPool();
	}

	/**
	 * PartOfSpeechAnnotation
	 */
	public void testPartOfSpeechAnnotation() {
		System.err.println("TEST POS ANNOTATION");
		String text = StanfordNLPTestConstants.SENTENCE_TWO;
		Annotation doc = new Annotation(text);
		coreNlp.annotate(doc);
		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and
		// has values with custom types
		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		printSentences(sentences);

		StanfordCoreNLP.clearAnnotatorPool();
	}

	public void testPreProcessing() {
		System.err.println("TEST PRE PROCESSING");
		String text = StanfordNLPTestConstants.SENTENCE_TWO;
		String lemmatizedText = doLemmatization(text);
		List<CoreMap> annotatedSentences = annotateSentences(lemmatizedText);
		printSentences(annotatedSentences);

		List<HashMap<String, String>> word_posList = doPOSAnnotation(annotatedSentences);
		for (HashMap<String, String> map : word_posList) {
			System.out.println(map.toString());
		}

		StanfordCoreNLP.clearAnnotatorPool();
	}

	public void testLiuSentencePreProcessing() {
		System.err.println("LIU SENTENCE PRE PROCESSING");
		String text = StanfordNLPTestConstants.SENTENCE_LIU;
		String lemmatizedText = doLemmatization(text);
		List<CoreMap> annotatedSentences = annotateSentences(lemmatizedText);
		printSentences(annotatedSentences);

		List<HashMap<String, String>> word_posList = doPOSAnnotation(annotatedSentences);
		for (HashMap<String, String> map : word_posList) {
			System.out.println(map.toString());
		}

		StanfordCoreNLP.clearAnnotatorPool();
	}

	/*
	 * Helper functions
	 */

	private void printSentences(List<CoreMap> sentences) {
		System.err.println("PRINTING SENTENCES");
		for (CoreMap sentence : sentences) {
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
			SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);

			final Set<SemanticGraphEdge> edgeSet = dependencies.getEdgeSet();

			for (SemanticGraphEdge egi : edgeSet) {
				System.out.println("Source-Target:\n");
				System.out.println("The word '" + egi.getSource().toString() + "' is linked to '"
						+ egi.getTarget().toString() + "' with a relation of type '" + egi.getRelation().toString()
						+ "'\n");

				System.out.println("Gov-Dep:\n");
				System.out.println("The word '" + egi.getGovernor().toString() + "' is linked to '"
						+ egi.getDependent().toString() + "' with a relation of type '" + egi.getRelation().toString()
						+ "'\n");

				// (Governor = Source && Dependent = Target) for some reason
				// relation.toString() = short representation (see code in
				// GrammaticalRelation class)

				// TODO: Check EnglishGrammaticalRelations class for types

				Pair q = new Pair();
				q.setDependency(Dependency.DIRECT_DEPENDENCY);
				q.setSource(new Word(egi.getSource().toString()));
				q.setTarget(new Word(egi.getTarget().toString()));
				q.setRelation(egi.getRelation().toString());

				// should this be enough?
			}

			System.out.println("DEP:" + dependencies);
		}
	}

	private List<CoreMap> annotateSentences(String text) {
		Annotation document = new Annotation(text);
		coreNlp.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		return sentences;
	}

	private List<HashMap<String, String>> doPOSAnnotation(List<CoreMap> sentences) {
		List<HashMap<String, String>> word_posList = new ArrayList<HashMap<String, String>>();
		for (CoreMap sentence : sentences) {
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String word = token.get(TextAnnotation.class);
				String pos = token.get(PartOfSpeechAnnotation.class);
				System.out.println("word-pos :" + word + "-" + pos);

				HashMap<String, String> wordMap = new HashMap<String, String>();
				wordMap.put(word, pos);
				word_posList.add(wordMap);
			}
		}
		return word_posList;
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
}
