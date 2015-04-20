package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.core.constants.relations.Dep_ConjRel;
import com.unsupervisedsentiment.analysis.core.constants.relations.Dep_MRRel;
import com.unsupervisedsentiment.analysis.core.constants.relations.Pos_JJRel;
import com.unsupervisedsentiment.analysis.core.constants.relations.Pos_NNRel;
import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.Helpers;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;

public class TargetExtractorService implements ITargetExtractorService {

	private static ITargetExtractorService instance;

	public static ITargetExtractorService getInstance() {
		if (instance == null)
			instance = new TargetExtractorService();

		return instance;
	}

	private TargetExtractorService() {
	}

	@Override
	public Set<Tuple> extractTargetsUsingR1(final SemanticGraph semanticGraph, final Set<Word> opinionWords,
			final Set<Tuple> existingFeatures, final int semanticGraphIndex) {
		Set<Tuple> foundTargets = new HashSet<Tuple>();

		foundTargets.addAll(extractTargetsUsingR11(semanticGraph, opinionWords, existingFeatures, ElementType.FEATURE,
				semanticGraphIndex));
		foundTargets.addAll(extractTargetsUsingR12(semanticGraph, opinionWords, existingFeatures, ElementType.FEATURE,
				semanticGraphIndex));
		return foundTargets;
	}

	@Override
	public Set<Tuple> extractTargetsUsingR3(final SemanticGraph semanticGraph, final Set<Word> features,
			final Set<Tuple> existingFeatures, final int semanticGraphIndex) {
		final Set<Tuple> foundTargets = new HashSet<Tuple>();
		// Removed due to better results without
		// foundTargets.addAll(extractTargetsUsingR31(semanticGraph, features,
		// existingFeatures, ElementType.FEATURE, semanticGraphIndex));
		// foundTargets.addAll(extractTargetsUsingR32(semanticGraph, features,
		// existingFeatures, ElementType.FEATURE, semanticGraphIndex));
		return foundTargets;
	}

	public Set<Tuple> extractTargetsUsingR11(SemanticGraph semanticGraph, Set<Word> opinionWords,
			Set<Tuple> existingFeatures, ElementType targetType, int semanticGraphIndex) {
		Set<Tuple> foundTargets = Helpers.extractTargets(semanticGraph, opinionWords, Dep_MRRel.getInstance(),
				Pos_JJRel.getInstance(), Pos_NNRel.getInstance(), targetType, semanticGraphIndex);
		return Helpers.getNewTuples(foundTargets, existingFeatures);
	}

	public Set<Tuple> extractTargetsUsingR12(final SemanticGraph semanticGraph, final Set<Word> opinionWords,
			final Set<Tuple> existingFeatures, final ElementType targetType, final int semanticGraphIndex) {
		Set<Tuple> targets = new HashSet<Tuple>();

		for (final Word opinionWord : opinionWords) {
			final List<IndexedWord> vertexes = semanticGraph.getAllNodesByWordPattern(opinionWord.getPattern());
			for (final IndexedWord vertex : vertexes) {
				// for outgoing edges
				final List<SemanticGraphEdge> outgoingEdgesWithH = Helpers.getTargetEdgesOnRel(
						semanticGraph.outgoingEdgeIterable(vertex), Dep_MRRel.getInstance());
				for (final SemanticGraphEdge edgeWithH : outgoingEdgesWithH) {
					final Set<Tuple> foundTargets = Helpers.getTriplesRelativeToH(semanticGraph, opinionWord,
							edgeWithH, edgeWithH.getTarget(), true, Pos_JJRel.getInstance(), Pos_NNRel.getInstance(),
							Dep_MRRel.getInstance(), targetType, semanticGraphIndex);
					targets.addAll(Helpers.getNewTuples(foundTargets, existingFeatures));
					// targets.addAll(Helpers.getTriplesRelativeToH(semanticGraph,
					// opinionWord, edgeWithH, edgeWithH.getTarget(),
					// true, Pos_NNRel.getInstance(), Dep_MRRel.getInstance()));
				}

				// for incoming edges
				final List<SemanticGraphEdge> incomingEdgesWithH = Helpers.getTargetEdgesOnRel(
						semanticGraph.incomingEdgeIterable(vertex), Dep_MRRel.getInstance());
				for (final SemanticGraphEdge edgeWithH : incomingEdgesWithH) {
					final Set<Tuple> foundTargets = Helpers.getTriplesRelativeToH(semanticGraph, opinionWord,
							edgeWithH, edgeWithH.getSource(), false, Pos_JJRel.getInstance(), Pos_NNRel.getInstance(),
							Dep_MRRel.getInstance(), targetType, semanticGraphIndex);
					targets.addAll(Helpers.getNewTuples(foundTargets, existingFeatures));

					// targets.addAll(Helpers.getTriplesRelativeToH(semanticGraph,
					// opinionWord, edgeWithH, edgeWithH.getSource(),
					// false, Pos_NNRel.getInstance(),
					// Dep_MRRel.getInstance()));
				}
			}
		}

		return targets;
	}

	public Set<Tuple> extractTargetsUsingR31(final SemanticGraph semanticGraph, final Set<Word> features,
			final Set<Tuple> existingFeatures, final ElementType targetType, final int semanticGraphIndex) {
		final Set<Tuple> foundTargets = Helpers.extractTargets(semanticGraph, features, Dep_ConjRel.getInstance(),
				Pos_NNRel.getInstance(), Pos_NNRel.getInstance(), targetType, semanticGraphIndex);
		return Helpers.getNewTuples(foundTargets, existingFeatures);
	}

	public Set<Tuple> extractTargetsUsingR32(final SemanticGraph semanticGraph, final Set<Word> features,
			final Set<Tuple> existingFeatures, final ElementType targetType, final int semanticGraphIndex) {
		final Set<Tuple> targets = new HashSet<Tuple>();

		for (final Word feature : features) {
			final List<IndexedWord> vertexes = semanticGraph.getAllNodesByWordPattern(feature.getPattern());
			for (final IndexedWord vertex : vertexes) {
				// for outgoing edges
				final Iterable<SemanticGraphEdge> outgoingEdgesWithH = semanticGraph.outgoingEdgeIterable(vertex);
				for (final SemanticGraphEdge edgeWithH : outgoingEdgesWithH) {
					final Set<Tuple> foundTargets = Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph, feature,
							edgeWithH, edgeWithH.getTarget(), true, Dep_ConjRel.getInstance(), Pos_NNRel.getInstance(),
							Pos_NNRel.getInstance(), targetType, semanticGraphIndex);
					targets.addAll(Helpers.getNewTuples(foundTargets, existingFeatures));

					// targets.addAll(Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph,
					// feature,
					// edgeWithH, edgeWithH.getTarget(), true,
					// Pos_NNRel.getInstance()));
				}

				// for incoming edges
				final Iterable<SemanticGraphEdge> incomingEdgesWithH = semanticGraph.incomingEdgeIterable(vertex);
				for (final SemanticGraphEdge edgeWithH : incomingEdgesWithH) {
					final Set<Tuple> foundTargets = Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph, feature,
							edgeWithH, edgeWithH.getSource(), false, Dep_ConjRel.getInstance(),
							Pos_NNRel.getInstance(), Pos_NNRel.getInstance(), targetType, semanticGraphIndex);
					targets.addAll(Helpers.getNewTuples(foundTargets, existingFeatures));

					// targets.addAll(Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph,
					// feature,
					// edgeWithH, edgeWithH.getSource(), false,
					// Pos_NNRel.getInstance()));
				}
			}
		}

		return targets;
	}
}
