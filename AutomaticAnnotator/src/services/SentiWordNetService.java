package services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import model.Pair;

public class SentiWordNetService {

    private HashMap<Pair<String, Pair<Double, Double>>, String> modifierDictionary;

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

    private static SentiWordNetService instance;

    public static SentiWordNetService getInstance(String pathToSWN) {
        if (instance == null)
            instance = new SentiWordNetService(pathToSWN);

        return instance;
    }

    private SentiWordNetService(String pathToSWN) {
        init2(pathToSWN);
    }

    public void init2(String pathToSWN) {
        BufferedReader csv = null;
        try {

            HashMap<String, HashMap<Integer, Double>> allScoresHash = new HashMap<String, HashMap<Integer, Double>>();

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

                        if ((gloss.contains("intensifier") || gloss.contains("intensifiers")) && !key.equals("one#a") && !key.equals("well#r")) {
                            modifierDictionary.put(new Pair<>(key, new Pair<>(posScore, negScore)), gloss);
                        }
                    }
                }
            }

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
        Double total = new Double(0);
        if (_dict.get(word + pos[0]) != null)
            total += _dict.get(word + pos[0]) + total;
        return total;
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
