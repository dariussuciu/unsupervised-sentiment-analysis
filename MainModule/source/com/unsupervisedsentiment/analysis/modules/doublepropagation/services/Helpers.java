package com.unsupervisedsentiment.analysis.modules.doublepropagation.services;

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
import com.unsupervisedsentiment.analysis.test.constants.RelationEquivalency;
import com.unsupervisedsentiment.analysis.test.constants.relations.Dep_MRRel;
import com.unsupervisedsentiment.analysis.test.constants.relations.GenericRelation;
import com.unsupervisedsentiment.analysis.test.constants.relations.Pos_NNRel;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

public class Helpers {
	
	/**
	 * Gets the target edges based on the relation pos tag and the target pos tag
	 * @param edges
	 * @param targetType
	 * @param relationType
	 * @param isSource
	 * @return
	 */
	public static List<SemanticGraphEdge> getTargetEdgesOnRelAndTarget(Iterable<SemanticGraphEdge> edges, GenericRelation targetType,
			GenericRelation relationType, boolean isSource) {
		List<SemanticGraphEdge> targetEdges = new ArrayList<SemanticGraphEdge>();

		for (SemanticGraphEdge edge : edges) {
			GrammaticalRelation relation = edge.getRelation();
			if (relationType.contains(relation.toString())) {
				if (!isSource && targetType.contains(edge.getTarget().tag())) {
					targetEdges.add(edge);
				}
				if (isSource && targetType.contains(edge.getSource().tag())) {
					targetEdges.add(edge);
				}
			}
		}
		return targetEdges;
	}
	/**
	 * Gets the target edges based only on the relation pos tag
	 * @param edges
	 * @param targetType
	 * @param relationType
	 * @param isSource
	 * @return
	 */
	public static List<SemanticGraphEdge> getTargetEdgesOnRel(Iterable<SemanticGraphEdge> edges, GenericRelation relationType) {
		List<SemanticGraphEdge> targetEdges = new ArrayList<SemanticGraphEdge>();

		for (SemanticGraphEdge edge : edges) {
			GrammaticalRelation relation = edge.getRelation();
			if (relationType.contains(relation.toString())) {
				targetEdges.add(edge);
			}
		}
		return targetEdges;
	}
	
	/**
	 * Gets the target edges based only on the target pos tag
	 * @param edges
	 * @param targetType
	 * @param relationType
	 * @param isSource
	 * @return
	 */
	public static List<SemanticGraphEdge> getTargetEdgesOnTarget(Iterable<SemanticGraphEdge> edges,
			GenericRelation targetType, boolean isSource) {
		List<SemanticGraphEdge> targetEdges = new ArrayList<SemanticGraphEdge>();

		for (SemanticGraphEdge edge : edges) {
			if (!isSource && targetType.contains(edge.getTarget().tag())) {
				targetEdges.add(edge);
			}
			if (isSource && targetType.contains(edge.getSource().tag())) {
				targetEdges.add(edge);
			}
		}
		return targetEdges;
	}

	public static Pair getPair(String valueOpinion, String posOpinion, String valueTarget, String posTarget,
			String relation, Dependency dependency) {
		Word opinion = new Word(posOpinion, valueOpinion);
		Word target = new Word(posTarget, valueTarget);

		return new Pair(opinion, target, dependency, TupleType.Pair, relation);
	}

	public static Triple getTriple(String valueOpinion, String posOpinion, String valueTarget, String posTarget,
			String valueH, String posH, String relationHOpinion, String relationHTarget, Dependency dependency) {
		Word opinion = new Word(posOpinion, valueOpinion);
		Word target = new Word(posTarget, valueTarget);
		Word H = new Word(posH, valueH);

		return new Triple(opinion, target, H, dependency, TupleType.Triple, relationHOpinion, relationHTarget);
	}
	
	public static boolean checkEquivalentRelations(GrammaticalRelation relationSourceH, GrammaticalRelation relationTargetH)
	{
		//equivalency from Stanford, keep for know, until we know more about how it decides when relations
		// are equivalent
		if(relationSourceH.equals(relationTargetH))
			return true;
		
		String relSourceHName = relationSourceH.toString();
		String relTargetHName = relationTargetH.toString();
		
		return compareRelations(relSourceHName, relTargetHName);
	}
	
	private static boolean compareRelations(String rel1, String rel2)
	{	
		if(rel1.equals(rel2))
			return true;
		
		RelationEquivalency relEquivalency1 = getRelationEquivalency(rel1);
		RelationEquivalency relEquivalency2 = getRelationEquivalency(rel2);
	
		if(relEquivalency1.equals(relEquivalency2) && !relEquivalency1.equals(RelationEquivalency.None))
			return true;
		
		return false;
	}
	
	private static RelationEquivalency getRelationEquivalency(String relation)
	{
		if(relation.equals("s") || relation.equals("subj") || relation.equals("obj") || relation.equals("dobj") || relation.equals("nsubj"))
			return RelationEquivalency.Rule32;
		
		if(relation.equals("mod") || relation.equals("pmod"))
			return RelationEquivalency.Rule42;
		
		return RelationEquivalency.None;
	}
	
	public static Set<Tuple> extractTargets(SemanticGraph semanticGraph, Set<Word> words, GenericRelation relationType, GenericRelation targetType) {
		Set<Tuple> targets = new HashSet<Tuple>();

		for (Word word : words) {
			final List<IndexedWord> vertexes = semanticGraph.getAllNodesByWordPattern(word.getValue());
			for (IndexedWord vertex : vertexes) {
				// for outgoing edges
				List<SemanticGraphEdge> outgoingTargetEdges = Helpers.getTargetEdgesOnRelAndTarget(
						semanticGraph.outgoingEdgeIterable(vertex), targetType, relationType, false);
				for (SemanticGraphEdge edge : outgoingTargetEdges) {
					targets.add(Helpers.getPair(word.getValue(), word.getPosTag(), edge.getTarget().word(), edge
							.getTarget().tag(), edge.getRelation().toString(), Dependency.DIRECT_DEPENDENCY));
				}

				// for incoming edges
				List<SemanticGraphEdge> incomingTargetEdges = Helpers.getTargetEdgesOnRelAndTarget(
						semanticGraph.incomingEdgeIterable(vertex), targetType, relationType, true);
				for (SemanticGraphEdge edge : incomingTargetEdges) {
					
						targets.add(Helpers.getPair(word.getValue(), word.getPosTag(), edge.getSource().word(), edge
								.getSource().tag(), edge.getRelation().toString(), Dependency.DIRECT_DEPENDENCY));
				}
			}
		}

		return targets;
	}

	public static Set<Triple> getTriplesRelativeToH(SemanticGraph semanticGraph, Word source,
			SemanticGraphEdge edgeWithH, IndexedWord H, boolean isSource, GenericRelation targetPos, GenericRelation relationPos) {
		Set<Triple> targets = new HashSet<Triple>();
		// for incoming target edges
		List<SemanticGraphEdge> incomingEdgesWithTargets = Helpers.getTargetEdgesOnRelAndTarget(
				semanticGraph.incomingEdgeIterable(H), targetPos, relationPos, !isSource);
		for (SemanticGraphEdge edgeWithTarget : incomingEdgesWithTargets) {

			GrammaticalRelation relationHSource = edgeWithH.getRelation();
			GrammaticalRelation relationHTarget = edgeWithTarget .getRelation();
			IndexedWord target = edgeWithTarget.getSource();
			if(validateTriple(source, target, H))
				targets.add(createTriple(source, target, H, relationHSource, relationHTarget));
		}

		// for outgoing target edges
		List<SemanticGraphEdge> outgoingEdgesWithTargets = Helpers.getTargetEdgesOnRelAndTarget(
				semanticGraph.outgoingEdgeIterable(H), targetPos, relationPos, isSource);
		for (SemanticGraphEdge edgeWithTarget : outgoingEdgesWithTargets) {

			GrammaticalRelation relationHSource = edgeWithH.getRelation();
			GrammaticalRelation relationHTarget = edgeWithTarget .getRelation();
			IndexedWord target = edgeWithTarget.getTarget();
			if(validateTriple(source, target, H))
				targets.add(createTriple(source, target, H, relationHSource, relationHTarget));
		}
		return targets;
	}
	
	
	public static boolean validateTriple(Word source, IndexedWord target, IndexedWord H)
	{
		String sourceWord = source.getValue();
		String targetWord = target.value();
		String sourcePosTag = source.getPosTag();
		String targetPosTag = target.tag();
		
		if(sourceWord.equals(targetWord) && sourcePosTag.equals(targetPosTag))
			return false;
		
		return true;
	}
	
	public static Triple createTriple(Word source, IndexedWord target, IndexedWord H, GrammaticalRelation relationHSource,
			GrammaticalRelation relationHTarget) {
		
		String relationHSourceString = relationHSource.toString();
		String relationHTargetString = relationHTarget.toString();
		
		return createTriple(source, target, H, relationHSourceString, relationHTargetString);
	}
	
	public static Triple createTriple(Word source, IndexedWord target, IndexedWord H, String relationHSource,
			String relationHTarget) {
			
		return Helpers.getTriple(source.getValue(), source.getPosTag(), target.word(), target.tag(), H.word(), H.tag(),
				relationHSource, relationHTarget, Dependency.DIRECT_DEPENDENCY);
	}
	
	
	public static Set<Triple> getTriplesRelativeToHOnEquivalency(SemanticGraph semanticGraph, Word source,
			SemanticGraphEdge edgeWithH, IndexedWord H, boolean isSource, GenericRelation targetPos) {
		Set<Triple> targets = new HashSet<Triple>();
		// for incoming target edges
		List<SemanticGraphEdge> incomingEdgesWithTargets = Helpers.getTargetEdgesOnTarget(
				semanticGraph.incomingEdgeIterable(H), targetPos, !isSource);
		for (SemanticGraphEdge edgeWithTarget : incomingEdgesWithTargets) {

			GrammaticalRelation relationHSource = edgeWithH.getRelation();
			GrammaticalRelation relationHTarget = edgeWithTarget .getRelation();

			if(Helpers.checkEquivalentRelations(relationHSource, relationHTarget))
			{
				IndexedWord target = edgeWithTarget.getSource();
				if(validateTriple(source, target, H))
					targets.add(createTriple(source, target, H, relationHSource, relationHTarget));
			}
		}

		// for outgoing target edges
		List<SemanticGraphEdge> outgoingEdgesWithTargets = Helpers.getTargetEdgesOnTarget(
				semanticGraph.outgoingEdgeIterable(H), targetPos, isSource);
		for (SemanticGraphEdge edgeWithTarget : outgoingEdgesWithTargets) {

			GrammaticalRelation relationHSource = edgeWithH.getRelation();
			GrammaticalRelation relationHTarget = edgeWithTarget .getRelation();

			if(Helpers.checkEquivalentRelations(relationHSource, relationHTarget))
			{
				IndexedWord target = edgeWithTarget.getTarget();
				if(validateTriple(source, target, H))
					targets.add(createTriple(source, target, H, relationHSource, relationHTarget));
			}
		}
		return targets;
	}
}
