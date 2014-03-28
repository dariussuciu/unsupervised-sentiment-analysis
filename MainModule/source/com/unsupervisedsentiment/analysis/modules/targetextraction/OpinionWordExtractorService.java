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

public class OpinionWordExtractorService implements
		IOpinionWordExtractorService {

	@Override
	public Set<Tuple> extractOpinionWordsUsingR2(
			final SemanticGraph semanticGraph, final Set<Word> targets,
			final Set<Tuple> existingOpinionWords, final int semanticGraphIndex) {
		final Set<Tuple> foundTargets = new HashSet<Tuple>();

		foundTargets.addAll(extractOpinionWordsUsingR21(semanticGraph, targets,
				existingOpinionWords, ElementType.OPINION_WORD,
				semanticGraphIndex));
		foundTargets.addAll(extractOpinionWordsUsingR22(semanticGraph, targets,
				existingOpinionWords, ElementType.OPINION_WORD,
				semanticGraphIndex));
		return foundTargets;
	}

	@Override
	public Set<Tuple> extractOpinionWordsUsingR4(
			final SemanticGraph semanticGraph, final Set<Word> opinionWords,
			final Set<Tuple> existingOpinionWords, int semanticGraphIndex) {
		final Set<Tuple> foundTargets = new HashSet<Tuple>();

		foundTargets.addAll(extractOpinionWordsUsingR41(semanticGraph,
				opinionWords, existingOpinionWords, ElementType.OPINION_WORD,
				semanticGraphIndex));
		foundTargets.addAll(extractOpinionWordsUsingR42(semanticGraph,
				opinionWords, existingOpinionWords, ElementType.OPINION_WORD,
				semanticGraphIndex));
		return foundTargets;
	}

	public Set<Tuple> extractOpinionWordsUsingR21(
			final SemanticGraph semanticGraph, final Set<Word> opinionWords,
			final Set<Tuple> existingOpinionWords,
			final ElementType targetType, final int semanticGraphIndex) {
		final Set<Tuple> foundTargets = Helpers.extractTargets(semanticGraph,
				opinionWords, Dep_MRRel.getInstance(), Pos_NNRel.getInstance(), Pos_JJRel.getInstance(),
				targetType, semanticGraphIndex);
		return Helpers.getNewTuples(foundTargets, existingOpinionWords);
		// return Helpers.extractTargets(semanticGraph, opinionWords,
		// Dep_MRRel.getInstance(), Pos_JJRel.getInstance());
	}

	public Set<Tuple> extractOpinionWordsUsingR22(
			final SemanticGraph semanticGraph, final Set<Word> features,
			final Set<Tuple> existingOpinionWords,
			final ElementType targetType, final int semanticGraphIndex) {
		final Set<Tuple> targets = new HashSet<Tuple>();

		for (final Word feature : features) {
			final List<IndexedWord> vertexes = semanticGraph
					.getAllNodesByWordPattern(feature.getPattern());
			for (final IndexedWord vertex : vertexes) {
				// for outgoing edges
				final List<SemanticGraphEdge> outgoingEdgesWithH = Helpers
						.getTargetEdgesOnRel(
								semanticGraph.outgoingEdgeIterable(vertex),
								Dep_MRRel.getInstance());
				for (final SemanticGraphEdge edgeWithH : outgoingEdgesWithH) {
					final Set<Tuple> foundTargets = Helpers
							.getTriplesRelativeToH(semanticGraph, feature,
									edgeWithH, edgeWithH.getTarget(), true,
									Pos_NNRel.getInstance(),
									Pos_JJRel.getInstance(),
									Dep_MRRel.getInstance(), targetType,
									semanticGraphIndex);
					targets.addAll(Helpers.getNewTuples(foundTargets,
							existingOpinionWords));
				}

				// for incoming edges
				final List<SemanticGraphEdge> incomingEdgesWithH = Helpers
						.getTargetEdgesOnRel(
								semanticGraph.incomingEdgeIterable(vertex),
								Dep_MRRel.getInstance());
				for (final SemanticGraphEdge edgeWithH : incomingEdgesWithH) {
					final Set<Tuple> foundTargets = Helpers
							.getTriplesRelativeToH(semanticGraph, feature,
									edgeWithH, edgeWithH.getSource(), false,
									Pos_NNRel.getInstance(),
									Pos_JJRel.getInstance(),
									Dep_MRRel.getInstance(), targetType,
									semanticGraphIndex);
					targets.addAll(Helpers.getNewTuples(foundTargets,
							existingOpinionWords));
				}
			}
		}

		return targets;
	}

	public Set<Tuple> extractOpinionWordsUsingR41(
			final SemanticGraph semanticGraph, final Set<Word> opinionWords,
			final Set<Tuple> existingOpinionWords,
			final ElementType targetType, final int semanticGraphIndex) {

		final Set<Tuple> foundTargets = Helpers.extractTargets(semanticGraph,
				opinionWords, Dep_ConjRel.getInstance(),
				Pos_JJRel.getInstance(), Pos_JJRel.getInstance(), targetType, semanticGraphIndex);
		return Helpers.getNewTuples(foundTargets, existingOpinionWords);
	}

	public Set<Tuple> extractOpinionWordsUsingR42(
			final SemanticGraph semanticGraph, final Set<Word> opinionWords,
			final Set<Tuple> existingOpinionWords,
			final ElementType targetType, final int semanticGraphIndex) {
		final Set<Tuple> targets = new HashSet<Tuple>();

		for (final Word feature : opinionWords) {
			final List<IndexedWord> vertexes = semanticGraph
					.getAllNodesByWordPattern(feature.getPattern());
			for (final IndexedWord vertex : vertexes) {
				// for outgoing edges
				final Iterable<SemanticGraphEdge> outgoingEdgesWithH = semanticGraph
						.outgoingEdgeIterable(vertex);
				for (final SemanticGraphEdge edgeWithH : outgoingEdgesWithH) {
					final Set<Tuple> foundTargets = Helpers
							.getTriplesRelativeToHOnEquivalency(semanticGraph,
									feature, edgeWithH, edgeWithH.getTarget(),
									true, Pos_JJRel.getInstance(), Pos_JJRel.getInstance(), targetType,
									semanticGraphIndex);
					targets.addAll(Helpers.getNewTuples(foundTargets,
							existingOpinionWords));
					// targets.addAll(Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph,
					// feature,
					// edgeWithH, edgeWithH.getTarget(), true,
					// Pos_JJRel.getInstance()));
				}

				// for incoming edges
				final Iterable<SemanticGraphEdge> incomingEdgesWithH = semanticGraph
						.incomingEdgeIterable(vertex);
				for (final SemanticGraphEdge edgeWithH : incomingEdgesWithH) {
					final Set<Tuple> foundTargets = Helpers
							.getTriplesRelativeToHOnEquivalency(semanticGraph,
									feature, edgeWithH, edgeWithH.getSource(),
									false, Pos_JJRel.getInstance(), Pos_JJRel.getInstance(), targetType,
									semanticGraphIndex);
					targets.addAll(Helpers.getNewTuples(foundTargets,
							existingOpinionWords));

					// targets.addAll(Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph,
					// feature,
					// edgeWithH, edgeWithH.getSource(), false,
					// Pos_JJRel.getInstance()));
				}
			}
		}

		return targets;
	}

}
