package com.unsupervisedsentiment.analysis.modules.services.wordnet;

import java.util.ArrayList;
import java.util.HashSet;

import com.unsupervisedsentiment.analysis.core.Config;
import com.unsupervisedsentiment.analysis.core.Initializer;
import com.unsupervisedsentiment.analysis.visualization.VisualisationService;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;

public class WordNetService {
	private WordNetDatabase database;
	
	private static  WordNetService instance;
	
	private WordNetService() {
		Config config = Initializer.getConfig();
		System.setProperty("wordnet.database.dir", config.getWordNetLocation());
		database = WordNetDatabase.getFileInstance();
	}
	
	public static WordNetService getInstance() {
		if (instance == null)
		{
			instance = new WordNetService();
		}

		return instance;
	}
	
	public String searchByWord(String word) 
	{
		Synset[] synsets = database.getSynsets(word);
		for (Synset synset : synsets) {
		    return synset.getWordForms()[0];
		}
		return null;
	}
	
	public HashSet<String> getSynonyms(String word) 
	{
		HashSet<String> synonyms = new HashSet<String>();
		Synset[] synsets = database.getSynsets(word);
		for (Synset synset : synsets) {		
			for(String wordForm : synset.getWordForms())
			{
				synonyms.add(wordForm);
			}
		}
		return synonyms;
	}
	
	public HashSet<String> getAntonyms(String word) 
	{
		HashSet<String> antonyms = new HashSet<String>();
		Synset[] synsets = database.getSynsets(word);
		for (Synset synset : synsets) {
			WordSense[] wordSenses = synset.getAntonyms(word);
			
			for(WordSense wordSense : wordSenses)
			{
				String wordForm = wordSense.getWordForm();
				antonyms.add(wordForm);
			}
		}
		return antonyms;
	}
}
