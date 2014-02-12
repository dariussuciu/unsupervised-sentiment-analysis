package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.Dependency;
import com.unsupervisedsentiment.analysis.model.Pair;
import com.unsupervisedsentiment.analysis.model.Triple;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.services.Helpers;
import com.unsupervisedsentiment.analysis.test.constants.relations.Dep_ConjRel;
import com.unsupervisedsentiment.analysis.test.constants.relations.Dep_MRRel;
import com.unsupervisedsentiment.analysis.test.constants.relations.GenericRelation;
import com.unsupervisedsentiment.analysis.test.constants.relations.Pos_NNRel;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

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
		return extractTargets(semanticGraph, opinionWords, Dep_MRRel.getInstance());
	}

	public Set<Triple> extractTargetUsingR12(SemanticGraph semanticGraph, Set<Word> opinionWords) {
		Set<Triple> targets = new HashSet<Triple>();

		for (Word opinionWord : opinionWords) {
			final List<IndexedWord> vertexes = semanticGraph.getAllNodesByWordPattern(opinionWord.getValue());
			for (IndexedWord vertex : vertexes) {
				// for outgoing edges
				List<SemanticGraphEdge> outgoingEdgesWithH = Helpers.getTargetEdges(
						semanticGraph.outgoingEdgeIterable(vertex), Dep_MRRel.getInstance());
				for (SemanticGraphEdge edgeWithH : outgoingEdgesWithH) {
					targets.addAll(getTriplesRelativeToH(semanticGraph, opinionWord, edgeWithH, edgeWithH.getTarget(),
							true));
				}

				// for incoming edges
				List<SemanticGraphEdge> incomingEdgesWithH = Helpers.getTargetEdges(
						semanticGraph.incomingEdgeIterable(vertex), Dep_MRRel.getInstance());
				for (SemanticGraphEdge edgeWithH : incomingEdgesWithH) {
					targets.addAll(getTriplesRelativeToH(semanticGraph, opinionWord, edgeWithH, edgeWithH.getSource(),
							false));
				}
			}
		}

		return targets;
	}

	public Set<Tuple> extractTargetUsingR31(SemanticGraph semanticGraph, Set<Word> targets) {
		return extractTargets(semanticGraph, targets, Dep_ConjRel.getInstance());
	}

	public Set<Triple> extractTargetUsingR32(SemanticGraph semanticGraph, Set<Word> features) {
		Set<Triple> targets = new HashSet<Triple>();

		for (Word feature : features) {
			final List<IndexedWord> vertexes = semanticGraph.getAllNodesByWordPattern(feature.getValue());
			for (IndexedWord vertex : vertexes) {
				// for outgoing edges
				List<SemanticGraphEdge> outgoingEdgesWithH = Helpers.getTargetEdges(
						semanticGraph.outgoingEdgeIterable(vertex), Dep_ConjRel.getInstance());
				for (SemanticGraphEdge edgeWithH : outgoingEdgesWithH) {
					String relationHSource = edgeWithH.getRelation().toString();
					String relationHTarget = edgeWithH.getRelation().toString();

					if (relationHSource.equals(relationHTarget))
						targets.addAll(getTriplesRelativeToH(semanticGraph, feature, edgeWithH, edgeWithH.getTarget(),
								true));
				}

				// for incoming edges
				//TODO: need a different function for Helpers.getTargetEdges(
				//semanticGraph.incomingEdgeIterable(vertex), Dep_ConjRel.getInstance());
				List<SemanticGraphEdge> incomingEdgesWithH = Helpers.getTargetEdges(
						semanticGraph.incomingEdgeIterable(vertex), Dep_ConjRel.getInstance());
				for (SemanticGraphEdge edgeWithH : incomingEdgesWithH) {
					String relationHSource = edgeWithH.getRelation().toString();
					String relationHTarget = edgeWithH.getRelation().toString();

					if (relationHSource.equals(relationHTarget))
						targets.addAll(getTriplesRelativeToH(semanticGraph, feature, edgeWithH, edgeWithH.getSource(),
								false));
				}
			}
		}

		return targets;
	}

	private Set<Triple> getTriplesRelativeToH(SemanticGraph semanticGraph, Word opinionWord,
			SemanticGraphEdge edgeWithH, IndexedWord H, boolean isSource) {
		Set<Triple> targets = new HashSet<Triple>();
		// for incoming target edges
		List<SemanticGraphEdge> incomingEdgesWithTargets = Helpers.getTargetEdges(
				semanticGraph.incomingEdgeIterable(H), Pos_NNRel.getInstance(), Dep_MRRel.getInstance(), !isSource);
		for (SemanticGraphEdge edgeWithTarget : incomingEdgesWithTargets) {

			String relationHSource = edgeWithH.getRelation().toString();
			String relationHTarget = edgeWithH.getRelation().toString();

			targets.add(createTriple(opinionWord, edgeWithTarget.getSource(), H, relationHSource, relationHTarget));
		}

		// for outgoing target edges
		List<SemanticGraphEdge> outgoingEdgesWithTargets = Helpers.getTargetEdges(
				semanticGraph.outgoingEdgeIterable(H), Pos_NNRel.getInstance(), Dep_MRRel.getInstance(), isSource);
		for (SemanticGraphEdge edgeWithTarget : outgoingEdgesWithTargets) {

			String relationHSource = edgeWithH.getRelation().toString();
			String relationHTarget = edgeWithH.getRelation().toString();

			targets.add(createTriple(opinionWord, edgeWithTarget.getTarget(), H, relationHSource, relationHTarget));
		}
		return targets;
	}

	private Set<Tuple> extractTargets(SemanticGraph semanticGraph, Set<Word> words, GenericRelation relationType) {
		Set<Tuple> targets = new HashSet<Tuple>();

		for (Word word : words) {
			final List<IndexedWord> vertexes = semanticGraph.getAllNodesByWordPattern(word.getValue());
			for (IndexedWord vertex : vertexes) {
				// for outgoing edges
				List<SemanticGraphEdge> outgoingTargetEdges = Helpers.getTargetEdges(
						semanticGraph.outgoingEdgeIterable(vertex), Pos_NNRel.getInstance(), relationType, false);
				for (SemanticGraphEdge edge : outgoingTargetEdges) {
					targets.add(Helpers.getPair(word.getValue(), word.getPosTag(), edge.getTarget().word(), edge
							.getTarget().tag(), edge.getRelation().toString(), Dependency.DIRECT_DEPENDENCY));
				}

				// for incoming edges
				List<SemanticGraphEdge> incomingTargetEdges = Helpers.getTargetEdges(
						semanticGraph.incomingEdgeIterable(vertex), Pos_NNRel.getInstance(), relationType, true);
				for (SemanticGraphEdge edge : incomingTargetEdges) {
					targets.add(Helpers.getPair(word.getValue(), word.getPosTag(), edge.getSource().word(), edge
							.getSource().tag(), edge.getRelation().toString(), Dependency.DIRECT_DEPENDENCY));
				}
			}
		}

		return targets;
	}

	private Triple createTriple(Word source, IndexedWord target, IndexedWord H, String relationHSource,
			String relationHTarget) {

		return Helpers.getTriple(source.getValue(), source.getPosTag(), target.word(), target.tag(), H.word(), H.tag(),
				relationHSource, relationHTarget, Dependency.DIRECT_DEPENDENCY);
	}
}
