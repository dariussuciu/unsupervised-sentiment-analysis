package com.unsupersentiment.analysis.tests;

import java.util.List;
import java.util.Properties;

import com.unsupresentiment.analysis.test.constants.StanfordNLPTestConstants;

import junit.framework.TestCase;
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
import edu.stanford.nlp.util.CoreMap;

public class StanfordNLPTests extends TestCase {
	protected StanfordCoreNLP snlp;

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
		props.put("annotators", StanfordCoreNLP.STANFORD_TOKENIZE + ","
				+ StanfordCoreNLP.STANFORD_SSPLIT + ","
				+ StanfordCoreNLP.STANFORD_POS + ","
				+ StanfordCoreNLP.STANFORD_LEMMA + ","
				+ StanfordCoreNLP.STANFORD_PARSE);
		snlp = new StanfordCoreNLP(props);
	}

	/**
	 * Basically, splits the phrase in sentences.
	 */
	public void testSentencesAnnotation() {
		String oneSentence = StanfordNLPTestConstants.SENTENCE_ONE;
		Annotation document = new Annotation(oneSentence);
		snlp.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		assertEquals(sentences.size(), 1);

		String twoSentence = StanfordNLPTestConstants.SENTENCE_TWO;
		document = new Annotation(twoSentence);
		snlp.annotate(document);
		sentences = document.get(SentencesAnnotation.class);
		assertEquals(sentences.size(), 2);

		int n = 20;
		String nSentence = StanfordNLPTestConstants.generateNSentencesString(n);
		document = new Annotation(nSentence);
		snlp.annotate(document);
		sentences = document.get(SentencesAnnotation.class);
		assertEquals(sentences.size(), n);
	}

	/**
	 * Tokens = words + punctuations
	 */
	public void testTokensAnnotation() {
		String oneSentence = StanfordNLPTestConstants.SENTENCE_ONE;
		Annotation document = new Annotation(oneSentence);
		snlp.annotate(document);
		List<CoreLabel> tokens = document.get(TokensAnnotation.class);
		assertEquals(tokens.size(), 10);

		String twoSentence = StanfordNLPTestConstants.SENTENCE_TWO;
		document = new Annotation(twoSentence);
		snlp.annotate(document);
		tokens = document.get(TokensAnnotation.class);
		assertEquals(tokens.size(), 18);

		int n = 20;
		String nSentence = StanfordNLPTestConstants.generateNSentencesString(n);
		document = new Annotation(nSentence);
		snlp.annotate(document);
		tokens = document.get(TokensAnnotation.class);
		assertEquals(tokens.size(), 10 * n);
	}

	/**
	 * PartOfSpeechAnnotation
	 */
	public void testPartOfSpeechAnnotation() {
		String text = StanfordNLPTestConstants.SENTENCE_TWO;
		Annotation doc = new Annotation(text);
		snlp.annotate(doc);
		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and
		// has values with custom types
		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				String word = token.get(TextAnnotation.class);
				String pos = token.get(PartOfSpeechAnnotation.class);
				System.out.println("word-pos :" + word + "-" + pos);
				String ne = token.get(NamedEntityTagAnnotation.class);
				System.out.println("NER:" + ne);
			}
			// this is the parse tree of the current sentence
			// Tree tree = sentence.get(TreeAnnotation.class);
			// this is the Stanford dependency graph of the current sentence
			SemanticGraph dependencies = sentence
					.get(CollapsedCCProcessedDependenciesAnnotation.class);
			System.out.println("DEP:" + dependencies);
		}
	}
}
