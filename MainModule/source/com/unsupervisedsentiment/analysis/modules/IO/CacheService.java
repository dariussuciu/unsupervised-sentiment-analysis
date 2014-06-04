package com.unsupervisedsentiment.analysis.modules.IO;

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
import java.util.List;

import com.unsupervisedsentiment.analysis.core.constants.Constants;
import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.EvaluationModel;
import com.unsupervisedsentiment.analysis.modules.standfordparser.NLPService;

import edu.stanford.nlp.semgraph.SemanticGraph;

public class CacheService {

	private static CacheService instance;
	private final NLPService nlpService;

	public static CacheService getInstance() {
		if (instance == null)
			instance = new CacheService();

		return instance;
	}

	private CacheService() {
		this.nlpService = NLPService.getInstance();
	};

	/**
	 * Retrieves or creates a new evaluation model for the element types and
	 * input.
	 * 
	 * @param storedEvaluationModelsDirectory
	 *            the directory of the evaluation models
	 * @param input
	 *            the input in case the model is not created
	 * @param forScoring
	 *            if it's for scoring or not
	 * @param elementType
	 *            the element type (see {@link ElementType})
	 * @param evaluationModelType
	 *            the evaluation model type (see {@link Constants})
	 * @return an evaluation model
	 */
	public List<EvaluationModel> getStoredOrCreateNewEvaluationModel(String storedEvaluationModelsDirectory,
			InputWrapper input, boolean forScoring, ElementType elementType, String evaluationModelType) {

		if (existsObjectsForFile(storedEvaluationModelsDirectory, input.getFilename(), evaluationModelType)) {
			return getStoredEvaluationModels(storedEvaluationModelsDirectory, input.getFilename(), forScoring,
					elementType, evaluationModelType);
		} else {
			return createNewEvaluationModel(storedEvaluationModelsDirectory, input, forScoring, elementType,
					evaluationModelType);
		}
	}

	/**
	 * Retrieves or creates semantic graphs for an input file.
	 * 
	 * @param storedSemanticGraphsDirectory
	 *            .
	 * @param filename
	 *            .
	 * @param semanticGraph
	 *            .
	 * @param input
	 *            .
	 * @return the cached or newly created semantic graphs
	 */
	public List<SemanticGraph> getOrCreateSemanticGraphForFile(String storedSemanticGraphsDirectory, String filename,
			String input) {
		List<SemanticGraph> semanticGraphs;

		if (existsObjectsForFile(storedSemanticGraphsDirectory, filename, Constants.SEMANTIC_GRAPH)) {
			semanticGraphs = this.<SemanticGraph> getObjectsFromFile(storedSemanticGraphsDirectory, filename,
					Constants.SEMANTIC_GRAPH);
		} else {
			semanticGraphs = nlpService.createSemanticGraphsListForSentances(input);
			saveObjectsToFile(semanticGraphs, storedSemanticGraphsDirectory, filename, Constants.SEMANTIC_GRAPH);
		}
		return semanticGraphs;
	}

	/**
	 * Checks whether or not a file exists
	 * 
	 * @param directory
	 *            the directory
	 * @param filename
	 *            the filename
	 * @param type
	 *            the type
	 * @return true if the file exists, false otherwise
	 */
	private boolean existsObjectsForFile(final String directory, final String filename, final String type) {
		final String filePath = directory + "/" + filename + "-" + type;
		final File f = new File(filePath);
		return f.exists();
	}

	private <T> void saveObjectsToFile(final List<T> objects, final String directory, final String filename, String type) {
		final String filePath = getStoredObjectFilename(directory, filename, type);
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

	private <T> List<T> getObjectsFromFile(final String directory, final String filename, final String type) {
		final String filePath = getStoredObjectFilename(directory, filename, type);
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

	private List<EvaluationModel> createNewEvaluationModel(String directory, InputWrapper input, boolean forScoring,
			ElementType elementType, String storedFileName) {
		List<EvaluationModel> evaluationModels = new ArrayList<EvaluationModel>();
		final List<SemanticGraph> semanticGraphs = NLPService.getInstance().createSemanticGraphsListForSentances(
				input.getContent());
		evaluationModels = NLPService.getInstance().getEvaluationModels(semanticGraphs, forScoring, elementType);
		saveObjectsToFile(evaluationModels, directory, input.getFilename(), storedFileName);
		return evaluationModels;
	}

	private List<EvaluationModel> getStoredEvaluationModels(String directory, String filename, boolean forScoring,
			ElementType elementType, String storedFileName) {
		List<EvaluationModel> evaluationModels = new ArrayList<EvaluationModel>();
		evaluationModels = this.<EvaluationModel> getObjectsFromFile(directory, filename, storedFileName);
		return evaluationModels;
	}

	private String getStoredObjectFilename(final String directory, final String filename, final String type) {
		final String filePath = directory + "/" + filename + "-" + type;
		return filePath;
	}

}
