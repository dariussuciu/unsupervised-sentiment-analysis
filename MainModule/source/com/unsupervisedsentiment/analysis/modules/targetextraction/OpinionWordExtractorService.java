package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.core.constants.relations.Dep_ConjRel;
import com.unsupervisedsentiment.analysis.core.constants.relations.Dep_MRRel;
import com.unsupervisedsentiment.analysis.core.constants.relations.Pos_JJRel;
import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.Helpers;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;

public class OpinionWordExtractorService implements IOpinionWordExtractorService {
	
	Helpers helpers = new Helpers();

	@Override
	public Set<Tuple> extractOpinionWordsUsingR2(SemanticGraph semanticGraph, Set<Word> targets,
			Set<Tuple> existingOpinionWords, int semanticGraphIndex) {
		Set<Tuple> foundTargets = new HashSet<Tuple>();

		foundTargets.addAll(extractOpinionWordsUsingR21(semanticGraph, targets, existingOpinionWords,
				ElementType.OPINION_WORD, semanticGraphIndex));
		foundTargets.addAll(extractOpinionWordsUsingR22(semanticGraph, targets, existingOpinionWords,
				ElementType.OPINION_WORD, semanticGraphIndex));
		return foundTargets;
	}

	@Override
	public Set<Tuple> extractOpinionWordsUsingR4(SemanticGraph semanticGraph, Set<Word> opinionWords,
			Set<Tuple> existingOpinionWords, int semanticGraphIndex) {
		Set<Tuple> foundTargets = new HashSet<Tuple>();

		foundTargets.addAll(extractOpinionWordsUsingR41(semanticGraph, opinionWords, existingOpinionWords,
				ElementType.OPINION_WORD, semanticGraphIndex));
		foundTargets.addAll(extractOpinionWordsUsingR42(semanticGraph, opinionWords, existingOpinionWords,
				ElementType.OPINION_WORD, semanticGraphIndex));
		return foundTargets;
	}

	public Set<Tuple> extractOpinionWordsUsingR21(SemanticGraph semanticGraph, Set<Word> opinionWords,
			Set<Tuple> existingOpinionWords, ElementType targetType, int semanticGraphIndex) {
		Set<Tuple> foundTargets = Helpers.extractTargets(semanticGraph, opinionWords, Dep_MRRel.getInstance(),
				Pos_JJRel.getInstance(), targetType, semanticGraphIndex);
		return helpers.getNewTuples(foundTargets, existingOpinionWords);
		// return Helpers.extractTargets(semanticGraph, opinionWords,
		// Dep_MRRel.getInstance(), Pos_JJRel.getInstance());
	}

	public Set<Tuple> extractOpinionWordsUsingR22(SemanticGraph semanticGraph, Set<Word> features,
			Set<Tuple> existingOpinionWords, ElementType targetType, int semanticGraphIndex) {
		Set<Tuple> targets = new HashSet<Tuple>();

		for (Word feature : features) {
			final List<IndexedWord> vertexes = semanticGraph.getAllNodesByWordPattern(feature.getPattern());
			for (IndexedWord vertex : vertexes) {
				// for outgoing edges
				List<SemanticGraphEdge> outgoingEdgesWithH = Helpers.getTargetEdgesOnRel(
						semanticGraph.outgoingEdgeIterable(vertex), Dep_MRRel.getInstance());
				for (SemanticGraphEdge edgeWithH : outgoingEdgesWithH) {
					Set<Tuple> foundTargets = Helpers.getTriplesRelativeToH(semanticGraph, feature, edgeWithH,
							edgeWithH.getTarget(), true, Pos_JJRel.getInstance(), Dep_MRRel.getInstance(), targetType, semanticGraphIndex);
					targets.addAll(helpers.getNewTuples(foundTargets, existingOpinionWords));
				}

				// for incoming edges
				List<SemanticGraphEdge> incomingEdgesWithH = helpers.getTargetEdgesOnRel(
						semanticGraph.incomingEdgeIterable(vertex), Dep_MRRel.getInstance());
				for (SemanticGraphEdge edgeWithH : incomingEdgesWithH) {
					Set<Tuple> foundTargets = Helpers.getTriplesRelativeToH(semanticGraph, feature, edgeWithH,
							edgeWithH.getSource(), false, Pos_JJRel.getInstance(), Dep_MRRel.getInstance(), targetType, semanticGraphIndex);
					targets.addAll(helpers.getNewTuples(foundTargets, existingOpinionWords));
				}
			}
		}

		return targets;
	}

	public Set<Tuple> extractOpinionWordsUsingR41(SemanticGraph semanticGraph, Set<Word> opinionWords,
			Set<Tuple> existingOpinionWords, ElementType targetType, int semanticGraphIndex) {

		Set<Tuple> foundTargets = Helpers.extractTargets(semanticGraph, opinionWords, Dep_ConjRel.getInstance(),
				Pos_JJRel.getInstance(), targetType, semanticGraphIndex);
		return helpers.getNewTuples(foundTargets, existingOpinionWords);
	}

	public Set<Tuple> extractOpinionWordsUsingR42(SemanticGraph semanticGraph, Set<Word> opinionWords,
			Set<Tuple> existingOpinionWords, ElementType targetType, int semanticGraphIndex) {
		Set<Tuple> targets = new HashSet<Tuple>();

		for (Word feature : opinionWords) {
			final List<IndexedWord> vertexes = semanticGraph.getAllNodesByWordPattern(feature.getPattern());
			for (IndexedWord vertex : vertexes) {
				// for outgoing edges
				Iterable<SemanticGraphEdge> outgoingEdgesWithH = semanticGraph.outgoingEdgeIterable(vertex);
				for (SemanticGraphEdge edgeWithH : outgoingEdgesWithH) {
					Set<Tuple> foundTargets = Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph, feature,
							edgeWithH, edgeWithH.getTarget(), true, Pos_JJRel.getInstance(), targetType, semanticGraphIndex);
					targets.addAll(helpers.getNewTuples(foundTargets, existingOpinionWords));
					// targets.addAll(Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph,
					// feature,
					// edgeWithH, edgeWithH.getTarget(), true,
					// Pos_JJRel.getInstance()));
				}

				// for incoming edges
				Iterable<SemanticGraphEdge> incomingEdgesWithH = semanticGraph.incomingEdgeIterable(vertex);
				for (SemanticGraphEdge edgeWithH : incomingEdgesWithH) {
					Set<Tuple> foundTargets = Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph, feature,
							edgeWithH, edgeWithH.getSource(), false, Pos_JJRel.getInstance(), targetType, semanticGraphIndex);
					targets.addAll(helpers.getNewTuples(foundTargets, existingOpinionWords));

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
