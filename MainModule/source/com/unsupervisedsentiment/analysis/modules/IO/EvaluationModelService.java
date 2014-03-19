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
import java.util.List;

import com.unsupervisedsentiment.analysis.core.Initializer;
import com.unsupervisedsentiment.analysis.model.EvaluationModel;

import edu.stanford.nlp.semgraph.SemanticGraph;

public class EvaluationModelService {
	public static boolean existsEvaluationModelForFile(String filename) {
		String storedSemanticGraphsDirectory = Initializer.getConfig()
				.getStoredSemanticGraphsDirectory();

		String filePath = storedSemanticGraphsDirectory + "/" + filename
				+ "-SemanticGraph";
		File f = new File(filePath);
		return f.exists();
	}

	public static void saveEvaluationModelToFile(
			List<SemanticGraph> semanticGraphsListForSentances, String filename) {
		String filePath = getEvaluationModelFilename(filename);
		try {
			// File f = new File(filename);
			// if (!f.exists())
			// f.createNewFile();
			OutputStream file = new FileOutputStream(filePath);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(semanticGraphsListForSentances);
			output.close();
		} catch (IOException ex) {
			System.out.println(ex);
		}
	}

	public static List<EvaluationModel> getEvaluationModelFromFile(String filename) {
		String filePath = getEvaluationModelFilename(filename);
		try {
			InputStream file = new FileInputStream(filePath);
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream(buffer);

			// deserialize the List
			@SuppressWarnings("unchecked")
			List<EvaluationModel> evaluationModels = (List<EvaluationModel>) input
					.readObject();
			input.close();
			// display its data
			return evaluationModels;
		} catch (ClassNotFoundException ex) {
			return null;
		} catch (IOException ex) {
			return null;
		}
	}

	private static String getEvaluationModelFilename(String filename) {
		String storedSemanticGraphsDirectory = Initializer.getConfig()
				.getStoredSemanticGraphsDirectory();
		String filePath = storedSemanticGraphsDirectory + "/" + filename
				+ "-SemanticGraph";
		return filePath;
	}
}
