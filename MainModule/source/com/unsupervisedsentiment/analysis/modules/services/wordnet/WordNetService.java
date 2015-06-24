package com.unsupervisedsentiment.analysis.modules.services.wordnet;

import com.unsupervisedsentiment.analysis.core.Config;
import com.unsupervisedsentiment.analysis.core.Initializer;
import com.unsupervisedsentiment.analysis.visualization.VisualisationService;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

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
}
