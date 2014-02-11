package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.Dependency;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.test.constants.relations.Dep_MRRel;
import com.unsupervisedsentiment.analysis.test.constants.relations.GenericRelation;
import com.unsupervisedsentiment.analysis.test.constants.relations.Pos_NNRel;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

public class TargetExtractorService implements ITargetExtractorService {

	@Override
	public Set<Tuple> extractTargetUsingR1(SemanticGraph semanticGraph, Set<Word> opinionWords)
	{
		Set<Tuple> foundTargets = new HashSet<Tuple>();
		
		foundTargets.addAll(extractTargetUsingR11(semanticGraph, opinionWords));
		foundTargets.addAll(extractTargetUsingR12(semanticGraph, opinionWords));
		return foundTargets;
	}

	@Override
	public Set<Tuple> extractTargetUsingR3(SemanticGraph semanticGraph, Set<Word> targets)
	{
		Set<Tuple> foundTargets = new HashSet<Tuple>();
		
		foundTargets.addAll(extractTargetUsingR31(semanticGraph, targets));
		foundTargets.addAll(extractTargetUsingR32(semanticGraph, targets));
		return foundTargets;
	}

	public Set<Tuple> extractTargetUsingR11(SemanticGraph semanticGraph, Set<Word> opinionWords)
	{
		Set<Tuple> targets = new HashSet<Tuple>();
		
		for(Word opinionWord : opinionWords)
		{
			final List<IndexedWord> vertexes = semanticGraph.getAllNodesByWordPattern(opinionWord.getValue());
			for(IndexedWord vertex : vertexes)
			{
				//for outgoing edges
				List<SemanticGraphEdge> outgoingTargetEdges = getTargetEdges(semanticGraph.outgoingEdgeIterable(vertex), Pos_NNRel.getInstance(), Dep_MRRel.getInstance());
				for (SemanticGraphEdge edge : outgoingTargetEdges)
				{
					targets.add(getTuple(opinionWord.getValue(), opinionWord.getPosTag(), edge.getTarget().word(), edge.getTarget().tag(), edge.getRelation().toString(), Dependency.DIRECT_DEPENDENCY));
				}
				
				//for incoming edges
				List<SemanticGraphEdge> incomingTargetEdges = getTargetEdges(semanticGraph.incomingEdgeIterable(vertex), Pos_NNRel.getInstance(), Dep_MRRel.getInstance());
				for (SemanticGraphEdge edge : incomingTargetEdges)
				{
					targets.add(getTuple(opinionWord.getValue(), opinionWord.getPosTag(), edge.getSource().word(), edge.getSource().tag(), edge.getRelation().toString(), Dependency.DIRECT_DEPENDENCY));
				}
			}
		}
		
		return targets;
	}

	public Set<Tuple> extractTargetUsingR12(SemanticGraph semanticGraph, Set<Word> opinionWords)
	{
		Set<Tuple> targets = new HashSet<Tuple>();
		
		for(Word opinionWord : opinionWords)
		{
			final List<IndexedWord> vertexes = semanticGraph.getAllNodesByWordPattern(opinionWord.getValue());
			for(IndexedWord vertex : vertexes)
			{
				//for outgoing edges
				List<SemanticGraphEdge> outgoingEdgesWithH = getTargetEdges(semanticGraph.outgoingEdgeIterable(vertex), Dep_MRRel.getInstance());
				for (SemanticGraphEdge edgeWithH : outgoingEdgesWithH)
				{
					IndexedWord H = edgeWithH.getTarget();
					
					//for incoming target edges
					List<SemanticGraphEdge> incomingEdgesWithTargets = getTargetEdges(semanticGraph.incomingEdgeIterable(H), Pos_NNRel.getInstance(), Dep_MRRel.getInstance());
					for(SemanticGraphEdge edgeWithTarget : incomingEdgesWithTargets)
					{
						targets.add(getTuple(opinionWord.getValue(), opinionWord.getPosTag(), edgeWithTarget.getSource().word(), edgeWithTarget.getSource().tag(), edgeWithTarget.getRelation().toString(), Dependency.DIRECT_DEPENDENCY));
					}
					
					//for outgoing target edges
					List<SemanticGraphEdge> outgoingEdgesWithTargets = getTargetEdges(semanticGraph.outgoingEdgeIterable(H), Pos_NNRel.getInstance(), Dep_MRRel.getInstance());
					for(SemanticGraphEdge edgeWithTarget : outgoingEdgesWithTargets)
					{
						targets.add(getTuple(opinionWord.getValue(), opinionWord.getPosTag(), edgeWithTarget.getTarget().word(), edgeWithTarget.getTarget().tag(), edgeWithTarget.getRelation().toString(), Dependency.DIRECT_DEPENDENCY));
					}
				}
				
				//for incoming edges
				List<SemanticGraphEdge> incomingEdgesWithH = getTargetEdges(semanticGraph.incomingEdgeIterable(vertex), Dep_MRRel.getInstance());
				for (SemanticGraphEdge edgeWithH : incomingEdgesWithH)
				{
					IndexedWord H = edgeWithH.getSource();
					//for incoming target edges
					List<SemanticGraphEdge> incomingEdgesWithTargets = getTargetEdges(semanticGraph.incomingEdgeIterable(H), Pos_NNRel.getInstance(), Dep_MRRel.getInstance());
					for(SemanticGraphEdge edgeWithTarget : incomingEdgesWithTargets)
					{
						targets.add(getTuple(opinionWord.getValue(), opinionWord.getPosTag(), edgeWithTarget.getSource().word(), edgeWithTarget.getSource().tag(), edgeWithTarget.getRelation().toString(), Dependency.DIRECT_DEPENDENCY));
					}
					
					//for outgoing target edges
					List<SemanticGraphEdge> outgoingEdgesWithTargets = getTargetEdges(semanticGraph.outgoingEdgeIterable(H), Pos_NNRel.getInstance(), Dep_MRRel.getInstance());
					for(SemanticGraphEdge edgeWithTarget : outgoingEdgesWithTargets)
					{
						targets.add(getTuple(opinionWord.getValue(), opinionWord.getPosTag(), edgeWithTarget.getTarget().word(), edgeWithTarget.getTarget().tag(), edgeWithTarget.getRelation().toString(), Dependency.DIRECT_DEPENDENCY));
					}
				}
			}
		}
		
		return targets;
	}

	private List<SemanticGraphEdge> getTargetEdges(Iterable<SemanticGraphEdge> edges, GenericRelation targetType, GenericRelation relationType)
	{
		List<SemanticGraphEdge> targetEdges = new ArrayList<SemanticGraphEdge>();
		
		for (SemanticGraphEdge edge : edges)
		{
			GrammaticalRelation relation = edge.getRelation();
			if(relationType.contains(relation.toString()))
			{
				if(targetType.contains(edge.getTarget().tag()))
				{
					targetEdges.add(edge);
				}
			}
		}
		return targetEdges;
	}
	
	private List<SemanticGraphEdge> getTargetEdges(Iterable<SemanticGraphEdge> edges, GenericRelation relationType)
	{
		List<SemanticGraphEdge> targetEdges = new ArrayList<SemanticGraphEdge>();
		
		for (SemanticGraphEdge edge : edges)
		{
			GrammaticalRelation relation = edge.getRelation();
			if(relationType.contains(relation.toString()))
			{
				targetEdges.add(edge);
			}
		}
		return targetEdges;
	}
	
	public Set<Tuple> extractTargetUsingR31(SemanticGraph semanticGraph, Set<Word> targets) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Tuple> extractTargetUsingR32(SemanticGraph semanticGraph, Set<Word> targets)
	{
		// TODO Auto-generated method stub
		return null;
	}
	private Tuple getTuple(String valueOpinion, String posOpinion, String valueTarget, String posTarget, String relation, Dependency dependency)
	{
		Word opinion = new Word(posOpinion,valueOpinion);
		Word target = new Word(posTarget, valueTarget);
		
		return new Tuple(opinion, target, dependency, relation);
	}
}
