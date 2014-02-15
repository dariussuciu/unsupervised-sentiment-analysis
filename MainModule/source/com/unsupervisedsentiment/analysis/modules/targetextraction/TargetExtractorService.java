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
import com.unsupervisedsentiment.analysis.test.constants.relations.Pos_NNRel;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;

public class TargetExtractorService implements ITargetExtractorService {

	@Override
	public Set<Tuple> extractTargetsUsingR1(SemanticGraph semanticGraph, Set<Word> opinionWords, Set<Tuple> existingFeatures) {
		Set<Tuple> foundTargets = new HashSet<Tuple>();

		foundTargets.addAll(extractTargetsUsingR11(semanticGraph, opinionWords, existingFeatures));
		foundTargets.addAll(extractTargetsUsingR12(semanticGraph, opinionWords, existingFeatures));
		return foundTargets;
	}

	@Override
	public Set<Tuple> extractTargetsUsingR3(SemanticGraph semanticGraph, Set<Word> features, Set<Tuple> existingFeatures) {
		Set<Tuple> foundTargets = new HashSet<Tuple>();

		foundTargets.addAll(extractTargetsUsingR31(semanticGraph, features, existingFeatures));
	    foundTargets.addAll(extractTargetsUsingR32(semanticGraph, features, existingFeatures));
		return foundTargets;
	}

	public Set<Tuple> extractTargetsUsingR11(SemanticGraph semanticGraph, Set<Word> opinionWords, Set<Tuple> existingFeatures) {
		return Helpers.extractTargets(semanticGraph, opinionWords, Dep_MRRel.getInstance(), Pos_NNRel.getInstance());
	}

	public Set<Tuple> extractTargetsUsingR12(SemanticGraph semanticGraph, Set<Word> opinionWords, Set<Tuple> existingFeatures) {
		Set<Tuple> targets = new HashSet<Tuple>();

		for (Word opinionWord : opinionWords) {
			final List<IndexedWord> vertexes = semanticGraph.getAllNodesByWordPattern(opinionWord.getValue());
			for (IndexedWord vertex : vertexes) {
				// for outgoing edges
				List<SemanticGraphEdge> outgoingEdgesWithH = Helpers.getTargetEdgesOnRel(
						semanticGraph.outgoingEdgeIterable(vertex), Dep_MRRel.getInstance());
				for (SemanticGraphEdge edgeWithH : outgoingEdgesWithH) {	
					Set<Tuple> foundTargets = Helpers.getTriplesRelativeToH(semanticGraph, opinionWord, edgeWithH, edgeWithH.getTarget(),
							true, Pos_NNRel.getInstance(), Dep_MRRel.getInstance());
					targets.addAll(Helpers.getNewTuples(foundTargets, existingFeatures));
					//targets.addAll(Helpers.getTriplesRelativeToH(semanticGraph, opinionWord, edgeWithH, edgeWithH.getTarget(),
					//		true, Pos_NNRel.getInstance(), Dep_MRRel.getInstance()));
				}

				// for incoming edges
				List<SemanticGraphEdge> incomingEdgesWithH = Helpers.getTargetEdgesOnRel(
						semanticGraph.incomingEdgeIterable(vertex), Dep_MRRel.getInstance());
				for (SemanticGraphEdge edgeWithH : incomingEdgesWithH) {
					Set<Tuple> foundTargets = Helpers.getTriplesRelativeToH(semanticGraph, opinionWord, edgeWithH, edgeWithH.getSource(),
							false, Pos_NNRel.getInstance(), Dep_MRRel.getInstance());
					targets.addAll(Helpers.getNewTuples(foundTargets, existingFeatures));
					
					//targets.addAll(Helpers.getTriplesRelativeToH(semanticGraph, opinionWord, edgeWithH, edgeWithH.getSource(),
					//		false, Pos_NNRel.getInstance(), Dep_MRRel.getInstance()));
				}
			}
		}

		return targets;
	}

	public Set<Tuple> extractTargetsUsingR31(SemanticGraph semanticGraph, Set<Word> features, Set<Tuple> existingFeatures) {
		Set<Tuple> foundTargets = Helpers.extractTargets(semanticGraph, features, Dep_ConjRel.getInstance(), Pos_NNRel.getInstance());
		return Helpers.getNewTuples(foundTargets, existingFeatures);
	}

	public Set<Tuple> extractTargetsUsingR32(SemanticGraph semanticGraph, Set<Word> features, Set<Tuple> existingFeatures) {
		Set<Tuple> targets = new HashSet<Tuple>();

		for (Word feature : features) {
			final List<IndexedWord> vertexes = semanticGraph.getAllNodesByWordPattern(feature.getValue());
			for (IndexedWord vertex : vertexes) {
				// for outgoing edges
				Iterable<SemanticGraphEdge> outgoingEdgesWithH = semanticGraph.outgoingEdgeIterable(vertex);
				for (SemanticGraphEdge edgeWithH : outgoingEdgesWithH) {			
					Set<Tuple> foundTargets = Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph, feature,
							edgeWithH, edgeWithH.getTarget(), true, Pos_NNRel.getInstance());
					targets.addAll(Helpers.getNewTuples(foundTargets, existingFeatures));
					
					//targets.addAll(Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph, feature,
					//		edgeWithH, edgeWithH.getTarget(), true, Pos_NNRel.getInstance()));
				}

				// for incoming edges
				Iterable<SemanticGraphEdge> incomingEdgesWithH = semanticGraph.incomingEdgeIterable(vertex);
				for (SemanticGraphEdge edgeWithH : incomingEdgesWithH) {
					Set<Tuple> foundTargets = Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph, feature,
							edgeWithH, edgeWithH.getSource(), false, Pos_NNRel.getInstance());
					targets.addAll(Helpers.getNewTuples(foundTargets, existingFeatures));
					
					//targets.addAll(Helpers.getTriplesRelativeToHOnEquivalency(semanticGraph, feature,
					//		edgeWithH, edgeWithH.getSource(), false, Pos_NNRel.getInstance()));
				}
			}
		}

		return targets;
	}
}
