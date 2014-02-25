package com.unsupervisedsentiment.analysis.core;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Config")
public class Config {
	private String inputDirectory;

	private String outputDirectory;
	
	private String SWNPath;

	private ArrayList<String> seedWords;

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
	public ArrayList<String> getSeedWords() {
		return seedWords;
	}

	public void setSeedWords(ArrayList<String> seedWords) {
		this.seedWords = seedWords;
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
}
