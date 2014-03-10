package com.unsupervisedsentiment.analysis.core;

import java.util.ArrayList;
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

	@XmlElement(name = "inputDirectory")
	public String getInputDirectory() {
		return inputDirectory.trim();
	}

	public void setInputDirectory(String inputDirectory) {
		this.inputDirectory = inputDirectory;
	}

	@XmlElement(name = "outputDirectory")
	public String getOutputDirectory() {
		return outputDirectory.trim();
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	@XmlElement(name = "seedWord")
	@XmlElementWrapper(name = "seedWords")
	public List<String> getSeedWords() {
		return seedWords;
	}

	public void setSeedWords(List<String> list) {
		this.seedWords = list;
	}

	@Override
	public String toString() {
		return "Input Directory: " + inputDirectory + "\n"
				+ "Output Directory: " + inputDirectory + "\n";
	}

	@XmlElement(name = "SWNDirectory")
	public String getSWNPath() {
		return SWNPath.trim();
	}

	public void setSWNPath(String sWNPath) {
		SWNPath = sWNPath;
	}

	@XmlElement(name = "storedSemanticGraphsDirectory")
	public String getStoredSemanticGraphsDirectory() {
		return storedSemanticGraphsDirectory.trim();
	}

	public void setStoredSemanticGraphsDirectory(
			String storedSemanticGraphsDirectory) {
		this.storedSemanticGraphsDirectory = storedSemanticGraphsDirectory;
	}
	
	@XmlElement(name = "positiveSeedWordsFile")
	public String getPositiveSeedWordsFile() {
		return positiveSeedWordsFile.trim();
	}

	public void setPositiveSeedWordsFile(String positiveSeedWordsFile) {
		this.positiveSeedWordsFile = positiveSeedWordsFile;
	}

	@XmlElement(name = "negativeSeedWordsFile")
	public String getNegativeSeedWordsFile() {
		return negativeSeedWordsFile.trim();
	}

	public void setNegativeSeedWordsFile(String negativeSeedWordsFile) {
		this.negativeSeedWordsFile = negativeSeedWordsFile;
	}
}
