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
import com.unsupervisedsentiment.analysis.test.constants.relations.Pos_NNRel;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;

public class TargetExtractorService implements ITargetExtractorService {

	@Override
	public Set<Tuple> extractTargetUsingR1(SemanticGraph semanticGraph, Set<Word> opinionWords) {
		Set<Tuple> foundTargets = new HashSet<Tuple>();

		foundTargets.addAll(extractTargetUsingR11(semanticGraph, opinionWords));
		foundTargets.addAll(extractTargetUsingR12(semanticGraph, opinionWords));
		return foundTargets;
	}

	@Override
	public Set<Tuple> extractTargetUsingR3(SemanticGraph semanticGraph, Set<Word> targets) {
		Set<Tuple> foundTargets = new HashSet<Tuple>();

		foundTargets.addAll(extractTargetUsingR31(semanticGraph, targets));
	    foundTargets.addAll(extractTargetUsingR32(semanticGraph, targets));
		return foundTargets;
	}

	public Set<Tuple> extractTargetUsingR11(SemanticGraph semanticGraph, Set<Word> opinionWords) {
		return Helpers.extractTargets(semanticGraph, opinionWords, Dep_MRRel.getInstance(), Pos_NNRel.getInstance());
	}

	public Set<Triple> extractTargetUsingR12(SemanticGraph semanticGraph, Set<Word> opinionWords) {
		Set<Triple> targets = new HashSet<Triple>();

		for (Word opinionWord : opinionWords) {
			final List<IndexedWord> vertexes = semanticGraph.getAllNodesByWordPattern(opinionWord.getValue());
			for (IndexedWord vertex : vertexes) {
				// for outgoing edges
				List<SemanticGraphEdge> outgoingEdgesWithH = Helpers.getTargetEdgesOnRel(
						semanticGraph.outgoingEdgeIterable(vertex), Dep_MRRel.getInstance());
				for (SemanticGraphEdge edgeWithH : outgoingEdgesWithH) {
					targets.addAll(Helpers.getTriplesRelativeToH(semanticGraph, opinionWord, edgeWithH, edgeWithH.getTarget(),
							true, Pos_NNRel.getInstance(), Dep_MRRel.getInstance()));
				}

				// for incoming edges
				List<SemanticGraphEdge> incomingEdgesWithH = Helpers.getTargetEdgesOnRel(
						semanticGraph.incomingEdgeIterable(vertex), Dep_MRRel.getInstance());
				for (SemanticGraphEdge edgeWithH : incomingEdgesWithH) {
					targets.addAll(Helpers.getTriplesRelativeToH(semanticGraph, opinionWord, edgeWithH, edgeWithH.getSource(),
							false, Pos_NNRel.getInstance(), Dep_MRRel.getInstance()));
				}
			}
		}

		return targets;
	}

	public Set<Tuple> extractTargetUsingR31(SemanticGraph semanticGraph, Set<Word> targets) {
		return Helpers.extractTargets(semanticGraph, targets, Dep_ConjRel.getInstance(), Pos_NNRel.getInstance());
	}

	public Set<Triple> extractTargetUsingR32(SemanticGraph semanticGraph, Set<Word> features) {
		Set<Triple> targets = new HashSet<Triple>();

		for (Word feature : features) {
			final List<IndexedWord> vertexes = semanticGraph.getAllNodesByWordPattern(feature.getValue());
			for (IndexedWord vertex : vertexes) {
				// for outgoing edges
				Iterable<SemanticGraphEdge> outgoingEdgesWithH = semanticGraph.outgoingEdgeIterable(vertex);
				for (SemanticGraphEdge edgeWithH : outgoingEdgesWithH) {
					targets.addAll(Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph, feature,
							edgeWithH, edgeWithH.getTarget(), true, Pos_NNRel.getInstance()));
				}

				// for incoming edges
				Iterable<SemanticGraphEdge> incomingEdgesWithH = semanticGraph.incomingEdgeIterable(vertex);
				for (SemanticGraphEdge edgeWithH : incomingEdgesWithH) {
					targets.addAll(Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph, feature,
							edgeWithH, edgeWithH.getSource(), false, Pos_NNRel.getInstance()));
				}
			}
		}

		return targets;
	}
}
