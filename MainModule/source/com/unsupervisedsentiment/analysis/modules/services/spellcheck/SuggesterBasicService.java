package com.unsupervisedsentiment.analysis.modules.services.spellcheck;

import java.util.ArrayList;

import com.softcorporation.suggester.BasicSuggester;
import com.softcorporation.suggester.Suggestion;
import com.softcorporation.suggester.dictionary.BasicDictionary;
import com.softcorporation.suggester.tools.SpellCheck;
import com.softcorporation.suggester.util.Constants;
import com.softcorporation.suggester.util.SpellCheckConfiguration;
import com.softcorporation.suggester.util.SuggesterException;
import com.unsupervisedsentiment.analysis.core.Config;
import com.unsupervisedsentiment.analysis.core.Initializer;

public class SuggesterBasicService {
	private BasicDictionary dictionary;
	private SpellCheckConfiguration configuration;
	private static  SuggesterBasicService instance;
	private SpellCheck spellCheck;
	private String misspeltWord;
	
	private SuggesterBasicService() {
		Config config = Initializer.getConfig();
		try {
		    // load English dictionary from jar file
		    BasicDictionary dictionary = new BasicDictionary(config.getSpellCheckerDictionaryLocation());

		    // load spellchecker configuration from file
		    SpellCheckConfiguration configuration = new SpellCheckConfiguration(config.getSpellCheckerConfigLocation());
		    BasicSuggester suggester = new BasicSuggester(configuration);
		    suggester.attach(dictionary);
		    spellCheck = new SpellCheck(configuration);
		    spellCheck.setSuggester(suggester);
	    
		} catch (SuggesterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static SuggesterBasicService getInstance() {
		if (instance == null)
		{
			instance = new SuggesterBasicService();
		}

		return instance;
	}
	
	public String correctText(String text) 
	{
		if(text == null)
			return null;
		//int nr=0;
	    try {
			spellCheck.setText(text, Constants.DOC_TYPE_TEXT, "en");
			spellCheck.check();
		    while (spellCheck.hasMisspelt())
		    {
		      misspeltWord = spellCheck.getMisspelt();

		      ArrayList<Suggestion> suggestions = spellCheck.getSuggestions();
              if(suggestions.size() > 0)
              {
            	  String selectedWord = suggestions.get(0).getWord();
            	  spellCheck.change(selectedWord);
    		      //System.out.println("Corrected: " + misspeltWord + " -> " + selectedWord + " : " + nr);
              }
		      spellCheck.checkNext();
		    }
		    
		} catch (SuggesterException e) {
			e.printStackTrace();
			return text;
		}
	    // get and display corrected text
	    text = spellCheck.getText();
	    return text;
	}
}