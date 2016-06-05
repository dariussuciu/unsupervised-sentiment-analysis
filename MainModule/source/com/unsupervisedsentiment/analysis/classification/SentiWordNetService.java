package com.unsupervisedsentiment.analysis.classification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unsupervisedsentiment.analysis.core.Config;
import com.unsupervisedsentiment.analysis.core.Initializer;
import com.unsupervisedsentiment.analysis.core.constants.relations.Pos_JJRel.JJ;
import com.unsupervisedsentiment.analysis.model.SeedScoreModel;
import com.unsupervisedsentiment.analysis.modules.IO.InputService;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.Helpers;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.util.Pair;

public class SentiWordNetService implements IPolarityLexion {

    public static final String WORD_AND_POS_REGEX = "(.*)\\-[A-Z]*";

    public enum SWNPos {
        Adjective("#a"), Verb("#v"), Noun("#n"), Adverb("#r");

        private String value;

        private SWNPos(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }

    private HashMap<String, Double> _dict;
    private HashMap<Pair<String, Pair<Double, Double>>, String> modifierDictionary;
    private Config config;

    private static IPolarityLexion instance;

    public static IPolarityLexion getInstance() {
        if (instance == null)
            instance = new SentiWordNetService();

        return instance;
    }

    private SentiWordNetService() {
        init2();
    }

    public void init() {
        config = Initializer.getConfig();
        final String pathToSWN = config.getSWNPath();

        _dict = new HashMap<String, Double>();
        try {
            final BufferedReader csv = new BufferedReader(new FileReader(
                    pathToSWN));
            String line = "";

            // headers...
            csv.readLine();
            while ((line = csv.readLine()) != null) {
                String[] data = line.split("\t");
                Double score = Double.parseDouble(data[2]) >= Double
                        .parseDouble(data[3]) ? Double.parseDouble(data[2])
                        : Double.parseDouble(data[3]);
                String[] words = data[4].split(" ");
                for (String w : words) {
                    String[] w_n = w.split("#");
                    w_n[0] += "#" + data[0];
                    if (_dict.containsKey(w_n[0])) {
                        _dict.put(w_n[0], score);
                    } else {
                        _dict.put(w_n[0], score);
                    }
                }
            }
            csv.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init2() {
        BufferedReader csv = null;
        try {

            HashMap<String, HashMap<Integer, Double>> allScoresHash = new HashMap<String, HashMap<Integer, Double>>();

            config = Initializer.getConfig();
            final String pathToSWN = config.getSWNPath();

            csv = new BufferedReader(new FileReader(pathToSWN));

            modifierDictionary = new HashMap<>();

            String line;
            while ((line = csv.readLine()) != null) {

                if (!line.trim().startsWith("#")) {

                    String[] data = line.split("\t");
                    String posTag = data[0];

                    String[] synsetList = data[4].split(" ");

                    String gloss = data[5];

                    double posScore = Double.parseDouble(data[2]);
                    double negScore = Double.parseDouble(data[3]);
                    Double score = posScore - negScore;

                    for (String rankedWord : synsetList) {

                        String word = rankedWord.split("#")[0];
                        int rank = Integer.parseInt(rankedWord.split("#")[1]);

                        String key = word + "#" + posTag;

                        if (!allScoresHash.containsKey(key)) {
                            allScoresHash.put(key, new HashMap<Integer, Double>());
                        }

                        allScoresHash.get(key).put(rank, score);
                        if (gloss.contains("intensifier") || gloss.contains("intensifiers")) {
                            if (key.contains("wonderful")) {
                                key = key.replace("#a", "");
                                key = key + "ly#a";
                            }
                            modifierDictionary.put(new Pair<>(key, new Pair<>(posScore, negScore)), gloss);
                        }
                    }
                }
            }

            modifierDictionary.put(new Pair<>("ugly", new Pair<>(0.0, -0.625)), "");
            modifierDictionary.put(new Pair<>("horrifying", new Pair<>(0.0, -0.625)), "");
            modifierDictionary.put(new Pair<>("atrocious", new Pair<>(0.0, -0.625)), "");
            modifierDictionary.put(new Pair<>("highly", new Pair<>(0.625, 0.0)), "");
            modifierDictionary.put(new Pair<>("extremely", new Pair<>(0.625, 0.0)), "");
            modifierDictionary.put(new Pair<>("excellent", new Pair<>(1.0, 0.0)), "");
            modifierDictionary.put(new Pair<>("great", new Pair<>(0.8, 0.0)), "");
            modifierDictionary.put(new Pair<>("good", new Pair<>(0.63, 0.0)), "");
            modifierDictionary.put(new Pair<>("useful", new Pair<>(0.3, 0.0)), "");
            modifierDictionary.put(new Pair<>("any", new Pair<>(0.0, -0.3)), "");
            modifierDictionary.put(new Pair<>("really", new Pair<>(0.4, 0.0)), "");
            modifierDictionary.put(new Pair<>("not", new Pair<>(0.0, 0.0)), "");

            _dict = new HashMap<String, Double>();
            for (Map.Entry<String, HashMap<Integer, Double>> entry : allScoresHash
                    .entrySet()) {
                String word = entry.getKey();
                Map<Integer, Double> synSetScoreMap = entry.getValue();

                double score = 0.0;
                double sum = 0.0;
                for (Map.Entry<Integer, Double> setScore : synSetScoreMap
                        .entrySet()) {
                    score += setScore.getValue() / (double) setScore.getKey();
                    sum += 1.0 / (double) setScore.getKey();
                }
                score = score / sum;

                _dict.put(word, score);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (csv != null) {
                try {
                    csv.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Possible pos parameters #n, #a, #r, #v
     *
     * @param word
     * @param pos
     * @return
     */
    public Double extract2(final String word, final String[] pos) {
        Double total = new Double(0);
        for (int i = 0; i < pos.length; i++) {
            if (_dict.get(word + pos[i]) != null)
                total = _dict.get(word + pos[i]) + total;
        }
        return total;

    }

    public Double extract(String word, String[] pos) {
        if (word.equals("comfortable")) //this is a SWN bug - most entries for comfortable have a negative score
            return 0.5;
        if (word.equals("great"))
            return 0.8;
        Double total = new Double(0);
        if (_dict.get(word + pos[0]) != null)
            total += _dict.get(word + pos[0]) + total;
        return total;
    }

    public ArrayList<SeedScoreModel> getSeedWordsWithScores() {
        final InputService inputService = InputService.getInstance(config);
        config.setSeedWords(inputService.getSeedWordsFromFile());
        ArrayList<SeedScoreModel> hash = new ArrayList<SeedScoreModel>();
        ArrayList<String> seedsTemp = (ArrayList<String>) config.getSeedWords();
        ArrayList<String> seeds = new ArrayList<String>();

        for (String seed : seedsTemp) {
            seeds.add(seed.trim());
        }

        for (String seedName : seeds) {
            double score = extract(seedName, new String[]{SWNPos.Adjective.toString()});
            hash.add(new SeedScoreModel(seedName, score, false));
        }
        return hash;
    }

    public List<Pair<String, Integer>> getSeedWordsFromSemanticGraph(List<SemanticGraph> graphs, Map<String, Pair<Double, Double>> knownModifiers) {
        List<Pair<String, Integer>> adjectives = new ArrayList<>();
        for (int sentenceIndex = 0; sentenceIndex < graphs.size(); sentenceIndex++) {
            List<String> wordsInSentence = new ArrayList<String>();

            Collection<IndexedWord> rootNodes = graphs.get(sentenceIndex).getRoots();
            for (IndexedWord root : rootNodes) {
                wordsInSentence.add(root.toString());
            }

            ArrayList<IndexedWord> nodesList = new ArrayList<IndexedWord>(graphs.get(sentenceIndex).vertexSet());
            for (IndexedWord word : nodesList) {
                wordsInSentence.add(word.toString());
            }

            for (int i = wordsInSentence.size() - 1; i >= 0; i--) {
                String word = wordsInSentence.get(i);
                //skip modifiers
                if (knownModifiers.containsKey(word)) {
                    continue;
                }
                if (i > 0) {
                    int j = i - 1;
                    String nextWord = Helpers.extractByRegexOneGroup(wordsInSentence.get(j), WORD_AND_POS_REGEX);
                    while ((knownModifiers.containsKey(nextWord) || nextWord.equals("not-RB") )&& j > 0) {
                        word = wordsInSentence.get(j) + " " + word;
                        j--;
                        nextWord = wordsInSentence.get(j);
                    }
                }
                //adjectives.add(word);
                boolean shouldAdd = false;
                if (word.contains("-" + JJ.JJ.toString())) {
                    word = word.replaceAll("-" + JJ.JJ.toString(), "").replaceAll("\\@.?\\d\\.?\\d*\\b", "");
                    shouldAdd = true;
                }
                if (word.contains("-" + JJ.JJS.toString())) {
                    word = word.replaceAll("-" + JJ.JJS.toString(), "").replaceAll("\\@.?\\d\\.?\\d*\\b", "");
                    shouldAdd = true;
                }
                if (word.contains("-" + JJ.JJR.toString())) {
                    word = word.replaceAll("-" + JJ.JJR.toString(), "").replaceAll("\\@.?\\d\\.?\\d*\\b", "");
                    shouldAdd = true;
                }
                if (word.contains("-" + JJ.RB.toString())) {
                    word = word.replaceAll("-" + JJ.RB.toString(), "").replaceAll("\\@.?\\d\\.?\\d*\\b", "");
                    shouldAdd = true;
                }
                if (shouldAdd) {
                    adjectives.add(new Pair(word, sentenceIndex));
                }
            }
        }
        return adjectives;
    }

    public Map<String, Pair<Double, Double>> getModifiers() {
        Map<String, Pair<Double, Double>> modifiers = new HashMap<>();
        for (Pair wordValue : modifierDictionary.keySet()) {
            if (wordValue.first.toString().contains("#")) {
                wordValue.first = wordValue.first.toString().split("#")[0];
            }
            modifiers.put((String) wordValue.first, new Pair<>((Double) ((Pair) wordValue.second).first, (Double) ((Pair) wordValue.second).second));
        }
        return modifiers;
    }

}
