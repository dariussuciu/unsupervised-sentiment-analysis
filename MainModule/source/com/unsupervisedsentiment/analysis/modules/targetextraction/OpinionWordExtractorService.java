package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.Triple;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.services.Helpers;
import com.unsupervisedsentiment.analysis.test.constants.relations.Dep_ConjRel;
import com.unsupervisedsentiment.analysis.test.constants.relations.Dep_MRRel;
import com.unsupervisedsentiment.analysis.test.constants.relations.Pos_JJRel;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;

public class OpinionWordExtractorService implements
		IOpinionWordExtractorService {

	@Override
	public Set<Tuple> extractOpinionWordsUsingR2(SemanticGraph semanticGraph, Set<Word> targets, Set<Tuple> existingOpinionWords) {
		Set<Tuple> foundTargets = new HashSet<Tuple>();

		foundTargets.addAll(extractOpinionWordsUsingR21(semanticGraph, targets, existingOpinionWords));
		foundTargets.addAll(extractOpinionWordsUsingR22(semanticGraph, targets, existingOpinionWords));
		return foundTargets;
	}

	@Override
	public Set<Tuple> extractOpinionWordsUsingR4(SemanticGraph semanticGraph, Set<Word> opinionWords, Set<Tuple> existingOpinionWords) {
		Set<Tuple> foundTargets = new HashSet<Tuple>();

		foundTargets.addAll(extractOpinionWordsUsingR41(semanticGraph, opinionWords, existingOpinionWords));
	    foundTargets.addAll(extractOpinionWordsUsingR42(semanticGraph, opinionWords, existingOpinionWords));
		return foundTargets;
	}

	public Set<Tuple> extractOpinionWordsUsingR21(SemanticGraph semanticGraph, Set<Word> opinionWords, Set<Tuple> existingOpinionWords) {
		Set<Tuple> foundTargets = Helpers.extractTargets(semanticGraph, opinionWords, Dep_MRRel.getInstance(), Pos_JJRel.getInstance());
		return Helpers.getNewTuples(foundTargets, existingOpinionWords);
		//return Helpers.extractTargets(semanticGraph, opinionWords, Dep_MRRel.getInstance(), Pos_JJRel.getInstance());
	}

	public Set<Tuple> extractOpinionWordsUsingR22(SemanticGraph semanticGraph, Set<Word> opinionWords, Set<Tuple> existingOpinionWords) {
		Set<Tuple> targets = new HashSet<Tuple>();

		for (Word opinionWord : opinionWords) {
			final List<IndexedWord> vertexes = semanticGraph.getAllNodesByWordPattern(opinionWord.getValue());
			for (IndexedWord vertex : vertexes) {
				// for outgoing edges
				List<SemanticGraphEdge> outgoingEdgesWithH = Helpers.getTargetEdgesOnRel(
						semanticGraph.outgoingEdgeIterable(vertex), Dep_MRRel.getInstance());
				for (SemanticGraphEdge edgeWithH : outgoingEdgesWithH) {
					Set<Tuple> foundTargets = Helpers.getTriplesRelativeToH(semanticGraph, opinionWord, edgeWithH, edgeWithH.getTarget(),
							true, Pos_JJRel.getInstance(), Dep_MRRel.getInstance());
					targets.addAll(Helpers.getNewTuples(foundTargets, existingOpinionWords));
				}

				// for incoming edges
				List<SemanticGraphEdge> incomingEdgesWithH = Helpers.getTargetEdgesOnRel(
						semanticGraph.incomingEdgeIterable(vertex), Dep_MRRel.getInstance());
				for (SemanticGraphEdge edgeWithH : incomingEdgesWithH) {
					Set<Tuple> foundTargets = Helpers.getTriplesRelativeToH(semanticGraph, opinionWord, edgeWithH, edgeWithH.getSource(),
							false, Pos_JJRel.getInstance(), Dep_MRRel.getInstance());
					targets.addAll(Helpers.getNewTuples(foundTargets, existingOpinionWords));
				}
			}
		}

		return targets;
	}

	public Set<Tuple> extractOpinionWordsUsingR41(SemanticGraph semanticGraph, Set<Word> opinionWords, Set<Tuple> existingOpinionWords) {
		
		Set<Tuple> foundTargets = Helpers.extractTargets(semanticGraph, opinionWords, Dep_ConjRel.getInstance(), Pos_JJRel.getInstance());
		return Helpers.getNewTuples(foundTargets, existingOpinionWords);
	}

	public Set<Tuple> extractOpinionWordsUsingR42(SemanticGraph semanticGraph, Set<Word> opinionWords, Set<Tuple> existingOpinionWords) {
		Set<Tuple> targets = new HashSet<Tuple>();

		for (Word feature : opinionWords) {
			final List<IndexedWord> vertexes = semanticGraph.getAllNodesByWordPattern(feature.getValue());
			for (IndexedWord vertex : vertexes) {
				// for outgoing edges
				Iterable<SemanticGraphEdge> outgoingEdgesWithH = semanticGraph.outgoingEdgeIterable(vertex);
				for (SemanticGraphEdge edgeWithH : outgoingEdgesWithH) {
					Set<Tuple> foundTargets = Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph, feature,
							edgeWithH, edgeWithH.getTarget(), true, Pos_JJRel.getInstance());
					targets.addAll(Helpers.getNewTuples(foundTargets, existingOpinionWords));
					//targets.addAll(Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph, feature,
					//		edgeWithH, edgeWithH.getTarget(), true, Pos_JJRel.getInstance()));
				}

				// for incoming edges
				Iterable<SemanticGraphEdge> incomingEdgesWithH = semanticGraph.incomingEdgeIterable(vertex);
				for (SemanticGraphEdge edgeWithH : incomingEdgesWithH) {
					Set<Tuple> foundTargets = Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph, feature,
							edgeWithH, edgeWithH.getSource(), false, Pos_JJRel.getInstance());
					targets.addAll(Helpers.getNewTuples(foundTargets, existingOpinionWords));
					
					//targets.addAll(Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph, feature,
					//		edgeWithH, edgeWithH.getSource(), false, Pos_JJRel.getInstance()));
				}
			}
		}

		return targets;
	}

}
