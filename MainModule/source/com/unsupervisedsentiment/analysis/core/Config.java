package com.unsupervisedsentiment.analysis.core;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Config")
public class Config {
	private String inputDirectory;

	private String outputDirectory;

	private String SWNPath;

	private String storedSemanticGraphsDirectory;

	private List<String> seedWords;

	private String positiveSeedWordsFile;

	private String negativeSeedWordsFile;

	private String evaluationModelsDirectory;

	private String numberOfSeeds;

	private String seedType;

	@XmlElement(name = "inputDirectory")
	public String getInputDirectory() {
		return inputDirectory.trim();
	}

	public void setInputDirectory(final String inputDirectory) {
		this.inputDirectory = inputDirectory;
	}

	@XmlElement(name = "outputDirectory")
	public String getOutputDirectory() {
		return outputDirectory.trim();
	}

	public void setOutputDirectory(final String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	@XmlElement(name = "seedWord")
	@XmlElementWrapper(name = "seedWords")
	public List<String> getSeedWords() {
		return seedWords;
	}

	public void setSeedWords(final List<String> list) {
		this.seedWords = list;
	}

	@XmlElement(name = "SWNDirectory")
	public String getSWNPath() {
		return SWNPath.trim();
	}

	public void setSWNPath(final String sWNPath) {
		SWNPath = sWNPath;
	}

	@XmlElement(name = "storedSemanticGraphsDirectory")
	public String getStoredSemanticGraphsDirectory() {
		return storedSemanticGraphsDirectory.trim();
	}

	public void setStoredSemanticGraphsDirectory(
			final String storedSemanticGraphsDirectory) {
		this.storedSemanticGraphsDirectory = storedSemanticGraphsDirectory;
	}

	@XmlElement(name = "positiveSeedWordsFile")
	public String getPositiveSeedWordsFile() {
		return positiveSeedWordsFile.trim();
	}

	public void setPositiveSeedWordsFile(final String positiveSeedWordsFile) {
		this.positiveSeedWordsFile = positiveSeedWordsFile;
	}

	@XmlElement(name = "negativeSeedWordsFile")
	public String getNegativeSeedWordsFile() {
		return negativeSeedWordsFile.trim();
	}

	public void setNegativeSeedWordsFile(final String negativeSeedWordsFile) {
		this.negativeSeedWordsFile = negativeSeedWordsFile;
	}

	@XmlElement(name = "evaluationModelsDirectory")
	public String getEvaluationModelsDirectory() {
		return evaluationModelsDirectory.trim();
	}

	public void setEvaluationModelsDirectory(
			final String evaluationModelsDirectory) {
		this.evaluationModelsDirectory = evaluationModelsDirectory;
	}

	@XmlElement(name = "numberOfSeeds")
	public String getNumberOfSeeds() {
		return numberOfSeeds.trim();
	}

	public void setNumberOfSeeds(String numberOfSeeds) {
		this.numberOfSeeds = numberOfSeeds;
	}

	@XmlElement(name = "seedType")
	public String getSeedType() {
		return seedType.trim();
	}

	public void setSeedType(String seedType) {
		this.seedType = seedType;
	}

	@Override
	public String toString() {
		return "Input Directory: " + inputDirectory + "\n"
				+ "Output Directory: " + inputDirectory + "\n";
	}

}
