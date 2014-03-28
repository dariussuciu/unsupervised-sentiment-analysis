package com.unsupervisedsentiment.analysis.modules.doublepropagation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.classification.ISentimentScoreSource;
import com.unsupervisedsentiment.analysis.classification.SentiWordNetService;
import com.unsupervisedsentiment.analysis.core.constants.RelationEquivalency;
import com.unsupervisedsentiment.analysis.core.constants.relations.GenericRelation;
import com.unsupervisedsentiment.analysis.core.constants.relations.Pos_JJRel;
import com.unsupervisedsentiment.analysis.core.constants.relations.Pos_NNRel;
import com.unsupervisedsentiment.analysis.model.Dependency;
import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.Pair;
import com.unsupervisedsentiment.analysis.model.Triple;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.model.Word;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

public class Helpers {

	/**
	 * Gets the target edges based on the relation pos tag and the target pos
	 * tag
	 * 
	 * @param edges
	 * @param targetType
	 * @param relationType
	 * @param isSource
	 * @return
	 */
	public static List<SemanticGraphEdge> getTargetEdgesOnRelAndTarget(
			final Iterable<SemanticGraphEdge> edges,
			final GenericRelation targetType,
			final GenericRelation relationType, final boolean isSource) {
		final List<SemanticGraphEdge> targetEdges = new ArrayList<SemanticGraphEdge>();

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
	 * 
	 * @param edges
	 * @param targetType
	 * @param relationType
	 * @param isSource
	 * @return
	 */
	public static List<SemanticGraphEdge> getTargetEdgesOnRel(
			final Iterable<SemanticGraphEdge> edges,
			final GenericRelation relationType) {
		final List<SemanticGraphEdge> targetEdges = new ArrayList<SemanticGraphEdge>();

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
	 * 
	 * @param edges
	 * @param targetType
	 * @param relationType
	 * @param isSource
	 * @return
	 */
	public static List<SemanticGraphEdge> getTargetEdgesOnTarget(
			final Iterable<SemanticGraphEdge> edges,
			final GenericRelation targetType, final boolean isSource) {
		final List<SemanticGraphEdge> targetEdges = new ArrayList<SemanticGraphEdge>();

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

	public static Pair getPair(final String valueOpinion,
			final String posOpinion, final ElementType typeSource,
			final String valueTarget, final String posTarget,
			final ElementType typeTarget, final String relation,
			final Dependency dependency, final int sentenceIndex,
			final String sentence) {
		final Word opinion = new Word(posOpinion, valueOpinion, typeSource);
		final Word target = new Word(posTarget, valueTarget, typeTarget);

		return new Pair(opinion, target, dependency, TupleType.Pair, relation,
				sentenceIndex, sentence);
	}

	public static Triple getTriple(final String valueSource,
			final String posSource, final ElementType typeSource,
			final String valueTarget, final String posTarget,
			final ElementType typeTarget, final String valueH,
			final String posH, final String relationHOpinion,
			final String relationHTarget, final Dependency dependency,
			final int sentenceIndex, final String sentence) {
		final Word opinion = new Word(posSource, valueSource, typeSource);
		final Word target = new Word(posTarget, valueTarget, typeTarget);
		final Word H = new Word(posH, valueH, ElementType.NONE);

		return new Triple(opinion, target, H, dependency, TupleType.Triple,
				relationHOpinion, relationHTarget, sentenceIndex, sentence);
	}

	public static boolean checkEquivalentRelations(
			final GrammaticalRelation relationSourceH,
			final GrammaticalRelation relationTargetH) {
		// equivalency from Stanford, keep for know, until we know more about
		// how it decides when relations
		// are equivalent
		// if(relationSourceH.equals(relationTargetH))
		// return true;

		final String relSourceHName = relationSourceH.toString();
		final String relTargetHName = relationTargetH.toString();

		return compareRelations(relSourceHName, relTargetHName);
	}

	private static boolean compareRelations(final String rel1, final String rel2) {
		// if(rel1.equals(rel2))
		// return true;

		final RelationEquivalency relEquivalency1 = getRelationEquivalency(rel1);
		final RelationEquivalency relEquivalency2 = getRelationEquivalency(rel2);

		if (relEquivalency1.equals(relEquivalency2)
				&& !relEquivalency1.equals(RelationEquivalency.None))
			return true;

		return false;
	}

	private static RelationEquivalency getRelationEquivalency(
			final String relation) {
		if (relation.equals("s") || relation.equals("subj")
				|| relation.equals("obj") || relation.equals("dobj")
				|| relation.equals("nsubj"))
			return RelationEquivalency.Rule32;

		if (relation.equals("mod") || relation.equals("pmod"))
			return RelationEquivalency.Rule42;

		return RelationEquivalency.None;
	}

	public static Set<Tuple> extractTargets(final SemanticGraph semanticGraph,
			final Set<Word> words, final GenericRelation relationType,
			final GenericRelation targetPos, final ElementType targetType,
			final int semanticGraphIndex) {
		final Set<Tuple> targets = new HashSet<Tuple>();

		for (Word word : words) {
			final List<IndexedWord> vertexes = semanticGraph
					.getAllNodesByWordPattern(word.getPattern());
			for (IndexedWord vertex : vertexes) {
				// for outgoing edges
				final List<SemanticGraphEdge> outgoingTargetEdges = Helpers
						.getTargetEdgesOnRelAndTarget(
								semanticGraph.outgoingEdgeIterable(vertex),
								targetPos, relationType, false);
				for (SemanticGraphEdge edge : outgoingTargetEdges) {
					targets.add(Helpers.getPair(word.getValue(), word
							.getPosTag(), word.getType(), edge.getTarget()
							.word(), edge.getTarget().tag(), targetType, edge
							.getRelation().toString(),
							Dependency.DIRECT_DEPENDENCY, semanticGraphIndex,
							semanticGraph.toRecoveredSentenceString()));
				}

				// for incoming edges
				final List<SemanticGraphEdge> incomingTargetEdges = Helpers
						.getTargetEdgesOnRelAndTarget(
								semanticGraph.incomingEdgeIterable(vertex),
								targetPos, relationType, true);
				for (SemanticGraphEdge edge : incomingTargetEdges) {

					targets.add(Helpers.getPair(word.getValue(), word
							.getPosTag(), word.getType(), edge.getSource()
							.word(), edge.getSource().tag(), targetType, edge
							.getRelation().toString(),
							Dependency.DIRECT_DEPENDENCY, semanticGraphIndex,
							semanticGraph.toRecoveredSentenceString()));
				}
			}
		}

		return targets;
	}

	public static Set<Tuple> getTriplesRelativeToH(
			final SemanticGraph semanticGraph, final Word source,
			final SemanticGraphEdge edgeWithH, final IndexedWord H,
			final boolean isSource, final GenericRelation targetPos,
			final GenericRelation relationPos, final ElementType targetType,
			final int semanticGraphIndex) {
		final Set<Tuple> targets = new HashSet<Tuple>();
		// for incoming target edges
		final List<SemanticGraphEdge> incomingEdgesWithTargets = Helpers
				.getTargetEdgesOnRelAndTarget(
						semanticGraph.incomingEdgeIterable(H), targetPos,
						relationPos, !isSource);
		for (SemanticGraphEdge edgeWithTarget : incomingEdgesWithTargets) {

			final GrammaticalRelation relationHSource = edgeWithH.getRelation();
			final GrammaticalRelation relationHTarget = edgeWithTarget
					.getRelation();
			final IndexedWord target = edgeWithTarget.getSource();
			if (validateTriple(source, target, H))
				targets.add(createTriple(source, target, H, relationHSource,
						relationHTarget, targetType, semanticGraphIndex,
						semanticGraph.toRecoveredSentenceString()));
		}

		// for outgoing target edges
		final List<SemanticGraphEdge> outgoingEdgesWithTargets = Helpers
				.getTargetEdgesOnRelAndTarget(
						semanticGraph.outgoingEdgeIterable(H), targetPos,
						relationPos, isSource);
		for (SemanticGraphEdge edgeWithTarget : outgoingEdgesWithTargets) {

			final GrammaticalRelation relationHSource = edgeWithH.getRelation();
			final GrammaticalRelation relationHTarget = edgeWithTarget
					.getRelation();
			final IndexedWord target = edgeWithTarget.getTarget();
			if (validateTriple(source, target, H))
				targets.add(createTriple(source, target, H, relationHSource,
						relationHTarget, targetType, semanticGraphIndex,
						semanticGraph.toRecoveredSentenceString()));
		}
		return targets;
	}

	public static boolean validateTriple(final Word source,
			final IndexedWord target, final IndexedWord H) {
		final String sourceWord = source.getValue();
		final String targetWord = target.value();

		if (sourceWord.equals(targetWord))
			return false;

		return true;
	}

	public static Triple createTriple(final Word source,
			final IndexedWord target, final IndexedWord H,
			final GrammaticalRelation relationHSource,
			final GrammaticalRelation relationHTarget,
			final ElementType targetType, final int sentenceIndex,
			final String sentence) {
		final String relationHSourceString = relationHSource.toString();
		final String relationHTargetString = relationHTarget.toString();

		return createTriple(source, target, H, relationHSourceString,
				relationHTargetString, targetType, sentenceIndex, sentence);
	}

	public static Triple createTriple(final Word source,
			final IndexedWord target, final IndexedWord H,
			final String relationHSource, final String relationHTarget,
			final ElementType targetType, final int sentenceIndex,
			final String sentence) {

		return Helpers.getTriple(source.getValue(), source.getPosTag(),
				source.getType(), target.word(), target.tag(), targetType,
				H.word(), H.tag(), relationHSource, relationHTarget,
				Dependency.DIRECT_DEPENDENCY, sentenceIndex, sentence);
	}

	public static Set<Tuple> getTriplesRelativeToHOnEquivalency(
			final SemanticGraph semanticGraph, final Word source,
			final SemanticGraphEdge edgeWithH, final IndexedWord H,
			final boolean isSource, final GenericRelation targetPos,
			final ElementType targetType, final int semanticGraphIndex) {
		final Set<Tuple> targets = new HashSet<Tuple>();
		// for incoming target edges
		final List<SemanticGraphEdge> incomingEdgesWithTargets = Helpers
				.getTargetEdgesOnTarget(semanticGraph.incomingEdgeIterable(H),
						targetPos, !isSource);
		for (SemanticGraphEdge edgeWithTarget : incomingEdgesWithTargets) {

			final GrammaticalRelation relationHSource = edgeWithH.getRelation();
			final GrammaticalRelation relationHTarget = edgeWithTarget
					.getRelation();

			if (Helpers.checkEquivalentRelations(relationHSource,
					relationHTarget)) {
				final IndexedWord target = edgeWithTarget.getSource();
				if (validateTriple(source, target, H))
					targets.add(createTriple(source, target, H,
							relationHSource, relationHTarget, targetType,
							semanticGraphIndex,
							semanticGraph.toRecoveredSentenceString()));
			}
		}

		// for outgoing target edges
		final List<SemanticGraphEdge> outgoingEdgesWithTargets = Helpers
				.getTargetEdgesOnTarget(semanticGraph.outgoingEdgeIterable(H),
						targetPos, isSource);
		for (SemanticGraphEdge edgeWithTarget : outgoingEdgesWithTargets) {

			final GrammaticalRelation relationHSource = edgeWithH.getRelation();
			final GrammaticalRelation relationHTarget = edgeWithTarget
					.getRelation();

			if (Helpers.checkEquivalentRelations(relationHSource,
					relationHTarget)) {
				final IndexedWord target = edgeWithTarget.getTarget();
				if (validateTriple(source, target, H))
					targets.add(createTriple(source, target, H,
							relationHSource, relationHTarget, targetType,
							semanticGraphIndex,
							semanticGraph.toRecoveredSentenceString()));
			}
		}
		return targets;
	}

	public static Set<Tuple> getNewTuples(
			final Set<? extends Tuple> sourceTuples,
			final Set<? extends Tuple> existingTuples) {
		final Set<Tuple> newTuples = new HashSet<Tuple>();
		for (Tuple tuple : sourceTuples) {
			boolean duplicate = false;
			for (Tuple existingTuple : existingTuples) {
				if (existingTuple.equals(tuple)) {
					duplicate = true;
					break;
				}
			}

			if (!duplicate && !isInvalidTuple(tuple))
				newTuples.add(tuple);
		}
		return newTuples;
	}

	public static boolean existsObjectsForFile(final String directory,
			final String filename, final String type) {
		final String filePath = directory + "/" + filename + "-" + type;
		final File f = new File(filePath);
		return f.exists();
	}

	public static <T> void saveObjectsToFile(final List<T> objects,
			final String directory, final String filename, String type) {
		final String filePath = getStoredObjectFilename(directory, filename,
				type);
		try {
			// File f = new File(filename);
			// if (!f.exists())
			// f.createNewFile();
			final OutputStream file = new FileOutputStream(filePath);
			final OutputStream buffer = new BufferedOutputStream(file);
			final ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(objects);
			output.close();
		} catch (IOException ex) {
			System.out.println(ex);
		}
	}

	public static <T> List<T> getObjectsFromFile(final String directory,
			final String filename, final String type) {
		final String filePath = getStoredObjectFilename(directory, filename,
				type);
		try {
			final InputStream file = new FileInputStream(filePath);
			final InputStream buffer = new BufferedInputStream(file);
			final ObjectInput input = new ObjectInputStream(buffer);

			// deserialize the List
			@SuppressWarnings("unchecked")
			List<T> objects = (List<T>) input.readObject();
			input.close();
			// display its data
			return objects;
		} catch (ClassNotFoundException ex) {
			return null;
		} catch (IOException ex) {
			return null;
		}
	}

	private static String getStoredObjectFilename(final String directory,
			final String filename, final String type) {
		final String filePath = directory + "/" + filename + "-" + type;
		return filePath;
	}

	public static boolean isInvalidTuple(final Tuple tuple) {
		if (tuple.getSource().getValue().equals("-lrb-")
				|| tuple.getSource().getValue().equals("-rrb-"))
			return true;

		if (tuple.getTarget().getValue().equals("-lrb-")
				|| tuple.getTarget().getValue().equals("-rrb-"))
			return true;

		if (tuple.getTarget().getType().equals(ElementType.OPINION_WORD)) {
			ISentimentScoreSource swnService = SentiWordNetService
					.getInstance();
			Word target = tuple.getTarget();
			String[] equivalentPOS = getEquivalentPOS(target.getPosTag());
			Double score = swnService.extract(target.getValue(), equivalentPOS); 
			if (score == 0)
				return true;
		}

		return false;
	}

	public static String[] getEquivalentPOS(String posTag) {
		if (Pos_JJRel.getInstance().contains(posTag)) {
			return new String[] {
					SentiWordNetService.SWNPos.Adjective.toString(),
					SentiWordNetService.SWNPos.Adverb.toString() };
		}
		if (Pos_NNRel.getInstance().contains(posTag)){
			return new String[]{
				SentiWordNetService.SWNPos.Noun.toString()	
			};
		}
		if (posTag.toLowerCase().equals("vbp")){
			return new String[]{
				SentiWordNetService.SWNPos.Verb.toString()
			};
		}
		return new String[] {};
	}
}
