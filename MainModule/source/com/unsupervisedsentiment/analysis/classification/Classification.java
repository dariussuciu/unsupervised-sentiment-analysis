package com.unsupervisedsentiment.analysis.classification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.unsupervisedsentiment.analysis.classification.SentiWordNetService.SWNPos;
import com.unsupervisedsentiment.analysis.core.Initializer;
import com.unsupervisedsentiment.analysis.core.constants.relations.Dep_ConjRel;
import com.unsupervisedsentiment.analysis.core.constants.relations.Pos_NNRel;
import com.unsupervisedsentiment.analysis.model.SeedScoreModel;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.Helpers;

import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.util.Pair;


public class Classification {

    private static final boolean USE_ADDITION = Initializer.getConfig().isUseAdditionForModifierScore();

    public static double DEFAULT_SCORE = -100;

    private IPolarityLexion polarityLexicon;

    private ArrayList<SeedScoreModel> seeds;

    public Classification() {
        initReader();
    }

    public ArrayList<Tuple> assignScoresBasedOnSeeds(Set<Tuple> data, List<SemanticGraph> semanticGrapshs, boolean useSeedWordsFromFile) {

        Map<String, Pair<Double, Double>> knownModifiers = polarityLexicon.getModifiers();

        if (useSeedWordsFromFile) {
            seeds = polarityLexicon.getSeedWordsWithScores();
        } else {
            seeds = new ArrayList<>();
            List<Pair<String, Integer>> extractedSeedWords = polarityLexicon.getSeedWordsFromSemanticGraph(semanticGrapshs, knownModifiers);
            for (Pair wordPosition : extractedSeedWords) {
                String word = (String) wordPosition.first;
                int sentenceIndex = (int) wordPosition.second;
                if (word.contains(" ")) {
                    word = removePOSsFromWord(word);
                    String[] splittedWord = word.split(" ");
                    String actualWord = splittedWord[splittedWord.length - 1];
                    double actualWordScore = polarityLexicon.extract(actualWord, new String[]{SWNPos.Adjective.toString()});
                    double constructScore = actualWordScore;
                    String firstModifier = null;
                    for (int i = splittedWord.length - 2; i >= 0; i--) {
                        String modifier = Helpers.extractByRegexOneGroup(splittedWord[i], SentiWordNetService.WORD_AND_POS_REGEX);
                        if (firstModifier == null) {
                            firstModifier = modifier;
                        }
                        double modifierScore = 0;
                        if (knownModifiers.containsKey(modifier)) {
                            Pair<Double, Double> modif = knownModifiers.get(modifier);
                            modifierScore = actualWordScore >= 0 ? modif.first : modif.second;
                            if (modif.first == 0 && modif.second < 0) { //clearly a negative modifier
                                modifierScore = modif.second;
                            }
                        }
                        //should this be here?
                        /*if ((modifierScore - constructScore) > 0 && modifierScore > 0) {
                            constructScore = constructScore - constructScore * Math.abs(modifierScore);
                        } else {*/
                        double sign = actualWordScore < 0 ? -1 : 1;
                        if (modifier.equals("not")) {
                            sign = -1;
                        }
                        if (USE_ADDITION) {
                            constructScore = sign * (modifierScore + actualWordScore);
                            break;
                        } else {
                            constructScore = sign * (Math.abs(constructScore) + (1 - Math.abs(constructScore)) * modifierScore);
                        }
                        /*}*/

                        /*int sign = modifier.equals("not") ? -1 : 1;
                        constructScore += sign * modifierScore;*/
                    }
                    word = removePOSsFromWord(word);
                    SeedScoreModel modelModifier = new SeedScoreModel(firstModifier, constructScore, true);
                    modelModifier.setSentenceIndex(sentenceIndex);
                    seeds.add(modelModifier);
                    SeedScoreModel model = new SeedScoreModel(word, constructScore, true);
                    model.setModifier(firstModifier);
                    model.setSentenceIndex(sentenceIndex);
                    seeds.add(model);
                    actualWord = removePOSsFromWord(actualWord);
                    SeedScoreModel modelSolo = new SeedScoreModel(actualWord, constructScore, true);
                    modelSolo.setModifier(firstModifier);
                    modelSolo.setSentenceIndex(sentenceIndex);
                    seeds.add(modelSolo);
                } else {
                    double score = polarityLexicon.extract(word, new String[]{SWNPos.Adjective.toString()});
                    SeedScoreModel model = new SeedScoreModel(word, score, false);
                    seeds.add(model);
                }
            }
        }

        ArrayList<Tuple> initialTuples = PolarityAssigner
                .initTupleArrayList(data);

        ArrayList<Tuple> partiallyAssignedTuples = PolarityAssigner
                .assignScoresToSeeds(initialTuples, seeds);

        ArrayList<Tuple> fullyAssignedTuples = PolarityAssigner.assignScores(
                partiallyAssignedTuples, seeds);

        ArrayList<Tuple> fullyAssignedTuples2 = PolarityAssigner.assignScores(
                fullyAssignedTuples, seeds);

        //printResults(fullyAssignedTuples2);
        /*for (Tuple tuple : fullyAssignedTuples2) {
            if (tuple.getSource().getValue().contains("uneasy")) {
                System.out.println(tuple.getSource().getValue() + ": " + tuple.getSource().getScore() + " - " + tuple.getSentence());
            }
        }*/
        return fullyAssignedTuples2;
    }

    private String removePOSsFromWord(String word) {
        if (word.contains("-" + Pos_NNRel.NN.NN.toString())) {
            word = word.replaceAll("-" + Pos_NNRel.NN.NN.toString(), "");
        }
        if (word.contains("-" + Pos_NNRel.NN.NNP.toString())) {
            word = word.replaceAll("-" + Pos_NNRel.NN.NNP.toString(), "");
        }
        if (word.contains("-" + Pos_NNRel.NN.NNPS.toString())) {
            word = word.replaceAll("-" + Pos_NNRel.NN.NNPS.toString(), "");
        }
        if (word.contains("-" + Pos_NNRel.NN.NNS.toString())) {
            word = word.replaceAll("-" + Pos_NNRel.NN.NNS.toString(), "");
        }
        if (word.contains("-" + Dep_ConjRel.CONJ.conj.toString())) {
            word = word.replaceAll("-" + Dep_ConjRel.CONJ.conj.toString(), "");
        }
        if (word.contains("-" + Dep_ConjRel.CONJ.conj_and.toString())) {
            word = word.replaceAll("-" + Dep_ConjRel.CONJ.conj_and.toString(), "");
        }
        if (word.contains("-" + "CD")) {
            word = word.replaceAll("-" + "CD", "");
        }
        if (word.contains("-" + "SYM".toString())) {
            word = word.replaceAll("-" + "SYM", "");
        }
        if (word.contains("-" + "VB".toString())) {
            word = word.replaceAll("-" + "VB", "");
        }
        if (word.contains("-" + "IN".toString())) {
            word = word.replaceAll("-" + "IN", "");
        }
        if (word.contains("-" + "FW".toString())) {
            word = word.replaceAll("-" + "FW", "");
        }
        return word;
    }

    private List<String> getModifiersForWord(Map<String, Pair<Double, Double>> knownModifiers, String word) {
        List<String> modifiers = new ArrayList<>();

        return modifiers;
    }

    public double computeOverallScore(List<Tuple> data) {
        return PolaritySummarization.computeOverallScore(data);
    }

    public HashMap<Double, Integer> computeScoreDistribution(List<Tuple> data) {
        return PolaritySummarization.computeScoreDistribution(data);
    }

    public double getAverageScoreForTarget(String target, List<Tuple> data) {
        return PolaritySummarization.getAverageScoreForTarget(target, data);
    }

    private void printResults(ArrayList<Tuple> tuples) {
        System.out
                .println("-------------------------------------------------------------------------------------------------------");
        System.out.println("Score assignment:");

        for (Tuple tuple : tuples) {
            System.out.println("Source word/score :"
                    + tuple.getSource().getValue() + "/"
                    + tuple.getSource().getScore() + " "
                    + tuple.getSource().getSentiWordScore());
            if (tuple.getTarget() != null) {
                System.out.println("Target word/score :"
                        + tuple.getTarget().getValue() + "/"
                        + tuple.getTarget().getScore() + " "
                        + tuple.getTarget().getSentiWordScore());
            }
        }
        System.out.println(tuples.size());
    }

    private void initReader() {
        polarityLexicon = SentiWordNetService.getInstance();
    }

}
