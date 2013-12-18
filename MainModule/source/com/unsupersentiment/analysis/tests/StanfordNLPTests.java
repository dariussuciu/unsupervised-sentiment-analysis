package com.unsupersentiment.analysis.tests;

import java.util.List;
import java.util.Properties;

import com.unsupresentiment.analysis.test.constants.StanfordNLPTestConstants;

import junit.framework.TestCase;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
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

	public void testAnnotationSimple() {
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
}
