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

	private String evaluationMetadataFile;

	private String detailedEvaluationMetadataFile;

	private String numberOfSeeds;

	private String seedType;

	private String polarityThreshold;

	private String scoringThreshold;

	private String targetFrequencyThreshold;
	
	private boolean printEvaluationResultsToConsole;

    private boolean useDynamicThreshold;

    private String dynamicThresholdPercentage;

    private boolean useAdditionForModifierScore;

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

	@XmlElement(name = "SWNPath")
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

	public void setStoredSemanticGraphsDirectory(final String storedSemanticGraphsDirectory) {
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

	public void setEvaluationModelsDirectory(final String evaluationModelsDirectory) {
		this.evaluationModelsDirectory = evaluationModelsDirectory;
	}

	@XmlElement(name = "evaluationMetadataFile")
	public String getEvaluationMetadataFile() {
		return evaluationMetadataFile.trim();
	}

	public void setEvaluationMetadataFile(String evaluationMetadataFile) {
		this.evaluationMetadataFile = evaluationMetadataFile;
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
		return "Input Directory: " + inputDirectory + "\n" + "Output Directory: " + inputDirectory + "\n";
	}

	@XmlElement(name = "polarityThreshold")
	public String getPolarityThreshold() {
		return polarityThreshold.trim();
	}

	public void setPolarityThreshold(String polarityThreshold) {
		this.polarityThreshold = polarityThreshold;
	}

	@XmlElement(name = "scoringThreshold")
	public String getScoringThreshold() {
		return scoringThreshold.trim();
	}

	public void setScoringThreshold(String scoringThreshold) {
		this.scoringThreshold = scoringThreshold;
	}

	@XmlElement(name = "targetFrequencyThreshold")
	public String getTargetFrequencyThreshold() {
		return targetFrequencyThreshold.trim();
	}

	public void setTargetFrequencyThreshold(String targetFrequencyThreshold) {
		this.targetFrequencyThreshold = targetFrequencyThreshold;
	}

	@XmlElement(name = "detailedEvaluationMetadataFile")
	public String getDetailedEvaluationMetadataFile() {
		return detailedEvaluationMetadataFile.trim();
	}

	public void setDetailedEvaluationMetadataFile(String detailedEvaluationMetadataFile) {
		this.detailedEvaluationMetadataFile = detailedEvaluationMetadataFile;
	}

	@XmlElement(name = "printEvaluationResultsToConsole")
	public boolean getPrintEvaluationResultsToConsole() {
		return printEvaluationResultsToConsole;
	}

	public void setPrintEvaluationResultsToConsole(boolean evaluateInput) {
		this.printEvaluationResultsToConsole = evaluateInput;
	}

    @XmlElement(name = "useDynamicThreshold")
    public boolean getUseDynamicThreshold() {
        return useDynamicThreshold;
    }

    public void setUseDynamicThreshold(boolean useDynamicThreshold) {
        this.useDynamicThreshold = useDynamicThreshold;
    }

    @XmlElement(name = "dynamicThresholdPercentage")
    public String getDynamicThresholdPercentage() {
        return dynamicThresholdPercentage.trim();
    }

    public void setDynamicThresholdPercentage(String dynamicThresholdPercentage) {
        this.dynamicThresholdPercentage = dynamicThresholdPercentage;
    }

    @XmlElement(name = "useAdditionForModifierScore")
    public boolean isUseAdditionForModifierScore() {
        return useAdditionForModifierScore;
    }

    public void setUseAdditionForModifierScore(boolean useAdditionForModifierScore) {
        this.useAdditionForModifierScore = useAdditionForModifierScore;
    }
}
