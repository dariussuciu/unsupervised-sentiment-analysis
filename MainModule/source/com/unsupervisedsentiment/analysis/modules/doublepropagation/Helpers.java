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
import com.unsupervisedsentiment.analysis.core.Initializer;
import com.unsupervisedsentiment.analysis.core.constants.RelationEquivalency;
import com.unsupervisedsentiment.analysis.core.constants.relations.GenericRelation;
import com.unsupervisedsentiment.analysis.core.constants.relations.Pos_JJRel;
import com.unsupervisedsentiment.analysis.core.constants.relations.Pos_NNRel;
import com.unsupervisedsentiment.analysis.model.Dependency;
import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.EvaluationModel;
import com.unsupervisedsentiment.analysis.model.Pair;
import com.unsupervisedsentiment.analysis.model.Triple;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.IO.InputWrapper;
import com.unsupervisedsentiment.analysis.modules.standfordparser.NLPService;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

public class Helpers {

	/**
	 * Gets the target edges based on the relation pos tag, target pos and the
	 * source pos tag
	 * 
	 * @param edges
	 * @param targetType
	 * @param relationType
	 * @param isSource
	 * @return
	 */
	public static List<SemanticGraphEdge> getTargetEdgesOnEdge(
			final Iterable<SemanticGraphEdge> edges,
			final GenericRelation sourceType, final GenericRelation targetType,
			final GenericRelation relationType, final boolean isSource) {
		final List<SemanticGraphEdge> targetEdges = new ArrayList<SemanticGraphEdge>();

		for (SemanticGraphEdge edge : edges) {
			GrammaticalRelation relation = edge.getRelation();

			if (relationType.contains(relation.toString())) {
				if (!isSource && targetType.contains(edge.getTarget().tag())
						&& sourceType.contains(edge.getSource().tag())) {
					targetEdges.add(edge);
				}
				if (isSource && targetType.contains(edge.getSource().tag())
						&& sourceType.contains(edge.getTarget().tag())) {
					targetEdges.add(edge);
				}
			}
		}
		return targetEdges;
	}

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
	public static List<SemanticGraphEdge> getTargetEdgesOnEdgeAndSource(
			final Iterable<SemanticGraphEdge> edges, final Word source,
			final GenericRelation sourceType, final GenericRelation targetType,
			final GenericRelation relationType, final boolean isSource) {
		final List<SemanticGraphEdge> targetEdges = new ArrayList<SemanticGraphEdge>();

		for (SemanticGraphEdge edge : edges) {
			GrammaticalRelation relation = edge.getRelation();

			if (relationType.contains(relation.toString())) {
				if (isSource && targetType.contains(edge.getSource().tag())
						&& sourceType.contains(source.getPosTag())) {
					targetEdges.add(edge);
				}
				if (!isSource && targetType.contains(edge.getTarget().tag())
						&& sourceType.contains(source.getPosTag())) {
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
		opinion.setSentenceIndex(sentenceIndex);
		final Word target = new Word(posTarget, valueTarget, typeTarget);
		target.setSentenceIndex(sentenceIndex);

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
		opinion.setSentenceIndex(sentenceIndex);
		final Word target = new Word(posTarget, valueTarget, typeTarget);
		target.setSentenceIndex(sentenceIndex);
		final Word H = new Word(posH, valueH, ElementType.NONE);

		return new Triple(opinion, target, H, dependency, TupleType.Triple,
				relationHOpinion, relationHTarget, sentenceIndex, sentence);
	}

	public static boolean checkEquivalentRelations(
			final GrammaticalRelation relationSourceH,
			final GrammaticalRelation relationTargetH,
			final GenericRelation relationType) {
		// equivalency from Stanford, keep for know, until we know more about
		// how it decides when relations
		// are equivalent
		// if(relationSourceH.equals(relationTargetH))
		// return true;

		final String relSourceHName = relationSourceH.toString();
		final String relTargetHName = relationTargetH.toString();

		if (!relationType.contains(relationSourceH.toString())
				|| !relationType.contains(relationTargetH.toString()))
			return false;

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
			final GenericRelation sourcePos, final GenericRelation targetPos,
			final ElementType targetType, final int semanticGraphIndex) {
		final Set<Tuple> targets = new HashSet<Tuple>();

		for (Word word : words) {
			final List<IndexedWord> vertexes = semanticGraph
					.getAllNodesByWordPattern(word.getPattern());
			for (IndexedWord vertex : vertexes) {
				// for outgoing edges
				final List<SemanticGraphEdge> outgoingTargetEdges = Helpers
						.getTargetEdgesOnEdge(
								semanticGraph.outgoingEdgeIterable(vertex),
								sourcePos, targetPos, relationType, false);
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
						.getTargetEdgesOnEdge(
								semanticGraph.incomingEdgeIterable(vertex),
								sourcePos, targetPos, relationType, true);
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
			final boolean isSource, final GenericRelation sourcePos,
			final GenericRelation targetPos, final GenericRelation relationPos,
			final ElementType targetType, final int semanticGraphIndex) {
		final Set<Tuple> targets = new HashSet<Tuple>();
		// for incoming target edges
		final List<SemanticGraphEdge> incomingEdgesWithTargets = Helpers
				.getTargetEdgesOnEdgeAndSource(
						semanticGraph.incomingEdgeIterable(H), source,
						sourcePos, targetPos, relationPos, !isSource);
		for (SemanticGraphEdge edgeWithTarget : incomingEdgesWithTargets) {

			final GrammaticalRelation relationHSource = edgeWithH.getRelation();
			final GrammaticalRelation relationHTarget = edgeWithTarget
					.getRelation();
			final IndexedWord target = edgeWithTarget.getSource();
			if (validateTriple(source, target, H, sourcePos, targetPos))
				targets.add(createTriple(source, target, H, relationHSource,
						relationHTarget, targetType, semanticGraphIndex,
						semanticGraph.toRecoveredSentenceString()));
		}

		// for outgoing target edges
		final List<SemanticGraphEdge> outgoingEdgesWithTargets = Helpers
				.getTargetEdgesOnEdgeAndSource(
						semanticGraph.outgoingEdgeIterable(H), source,
						sourcePos, targetPos, relationPos, isSource);
		for (SemanticGraphEdge edgeWithTarget : outgoingEdgesWithTargets) {

			final GrammaticalRelation relationHSource = edgeWithH.getRelation();
			final GrammaticalRelation relationHTarget = edgeWithTarget
					.getRelation();
			final IndexedWord target = edgeWithTarget.getTarget();
			if (validateTriple(source, target, H, sourcePos, targetPos))
				targets.add(createTriple(source, target, H, relationHSource,
						relationHTarget, targetType, semanticGraphIndex,
						semanticGraph.toRecoveredSentenceString()));
		}
		return targets;
	}

	public static boolean validateTriple(final Word source,
			final IndexedWord target, final IndexedWord H,
			final GenericRelation sourcePos, final GenericRelation targetPos) {
		final String sourceWord = source.getValue();
		final String targetWord = target.value();
		final String hWord = H.value();

		if (sourceWord.equals(targetWord) || sourceWord.equals(hWord)
				|| target.equals(hWord))
			return false;

		if (!sourcePos.contains(source.getPosTag())
				|| !targetPos.contains(target.tag()))
			return false;
		

		if(Pos_NNRel.getInstance().contains(H.tag()) && Pos_NNRel.getInstance().contains(target.tag()))
		return false;
		
		if(Pos_NNRel.getInstance().contains(H.tag()) && Pos_NNRel.getInstance().contains(source.getPosTag()))
		return false;
		
		if(Pos_JJRel.getInstance().contains(H.tag()) && Pos_JJRel.getInstance().contains(target.tag()))
		return false;
		
		if(Pos_JJRel.getInstance().contains(H.tag()) && Pos_JJRel.getInstance().contains(source.getPosTag()))
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
			final boolean isSource, final GenericRelation relationPos,
			final GenericRelation sourcePos, final GenericRelation targetPos,
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
					relationHTarget, relationPos)) {
				final IndexedWord target = edgeWithTarget.getSource();
				if (targetPos.contains(target.tag())
						&& sourcePos.contains(source.getPosTag())) {
					if (validateTriple(source, target, H, sourcePos, targetPos))
						targets.add(createTriple(source, target, H,
								relationHSource, relationHTarget, targetType,
								semanticGraphIndex,
								semanticGraph.toRecoveredSentenceString()));
				}
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
					relationHTarget, relationPos)) {
				final IndexedWord target = edgeWithTarget.getTarget();
				if (targetPos.contains(target.tag())
						&& sourcePos.contains(source.getPosTag())) {
					if (validateTriple(source, target, H, sourcePos, targetPos))
						targets.add(createTriple(source, target, H,
								relationHSource, relationHTarget, targetType,
								semanticGraphIndex,
								semanticGraph.toRecoveredSentenceString()));
				}
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
			Double threshold = new Double(Initializer.getConfig()
					.getPolarityThreshold());
			if (score < threshold && score > -threshold)
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
		if (Pos_NNRel.getInstance().contains(posTag)) {
			return new String[] { SentiWordNetService.SWNPos.Noun.toString() };
		}

		if (posTag.toLowerCase().equals("vbp")) {
			return new String[] { SentiWordNetService.SWNPos.Verb.toString() };
		}
		return new String[] { SentiWordNetService.SWNPos.Verb.toString() };
	}

	public static List<EvaluationModel> getEvaluationModels(String directory,
			InputWrapper input, boolean forScoring, ElementType elementType, String storedFileName) {
		List<EvaluationModel> evaluationModels = new ArrayList<EvaluationModel>();
		final List<SemanticGraph> semanticGraphs = NLPService.getInstance().createSemanticGraphsListForSentances(input.getContent());
		evaluationModels = NLPService.getInstance().getEvaluationModels(
				semanticGraphs, forScoring, elementType);
		Helpers.saveObjectsToFile(evaluationModels, directory,
				input.getFilename(), storedFileName);
		return evaluationModels;
	}
	
	public static List<EvaluationModel> getStoredEvaluationModels(String directory,
			InputWrapper input, boolean forScoring, ElementType elementType, String storedFileName) {
		List<EvaluationModel> evaluationModels = new ArrayList<EvaluationModel>();
		evaluationModels = Helpers.<EvaluationModel> getObjectsFromFile(
				directory, input.getFilename(), storedFileName);
		return evaluationModels;
	}
	
	public static double normalizeScore(double score){
		if (score >= 10 && score < 100)
			return score / 100;
		if (score >= 100 && score < 1000)
			return score / 1000;
		if (score >=1000 && score < 10000)
			return score / 10000;
		if (score >= 10000 && score < 100000)
			return score / 100000;
		return score;
	}
	
	public static List<Word> ExtractElements(Set<Tuple> tuples, ElementType elementType) {
		List<Word> elements = new ArrayList<Word>();
		
		for(Tuple tuple : tuples) {
			if (tuple.getTupleType().equals(TupleType.Seed)
					|| tuple.getElements(elementType).size() <= 0)
				continue;
			
			for(Word foundElement : tuple.getElements(elementType))
			{
				int numberOfInstances = 1;
				boolean isDuplicate = false;
				for(Word existingElement : elements) {
					if(existingElement.equals(foundElement) && existingElement.getSentenceIndex() == foundElement.getSentenceIndex())
					{
						isDuplicate = true;
					}
				}
				for(Tuple otherTuple : tuples) {
					for(Word otherElement : otherTuple.getElements(elementType)) {
						if(otherElement.equals(foundElement))
						{
							numberOfInstances++;
						}
					}
				}
				
				if(!isDuplicate)
				{
					foundElement.setNumberOfInstances(numberOfInstances);
					elements.add(foundElement);
				}
				else 
				{
					//System.out.println("Duplicate: " + foundOpinionWord.getValue() + " - " + foundOpinionWord.getSentenceIndex() );
				}
			}
		}
		
		return elements;
	}
}
