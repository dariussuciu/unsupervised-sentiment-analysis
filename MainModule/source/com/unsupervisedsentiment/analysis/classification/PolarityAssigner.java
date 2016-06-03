package com.unsupervisedsentiment.analysis.classification;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.SeedScoreModel;
import com.unsupervisedsentiment.analysis.model.Tuple;

import edu.stanford.nlp.util.Pair;

public class PolarityAssigner {

    /**
     * Necessary because for some reason changes in a Set<E> do not persist!
     *
     * @param data
     * @return
     */
    public static ArrayList<Tuple> initTupleArrayList(final Set<Tuple> data) {
        ArrayList<Tuple> tuples = new ArrayList<Tuple>();
        for (Tuple tuple : data) {
            if (tuple != null) {
                double score = SentiWordNetService.getInstance().extract(tuple.getSource().getValue(), new String[]{SentiWordNetService.SWNPos.Adjective.toString(), SentiWordNetService.SWNPos.Adverb.toString()});
                tuple.getSource().setScore(score);
                if (tuple.getTarget() != null) {
                    tuple.getTarget().setScore(score);
                }
                tuples.add(tuple);
            }
        }
        return tuples;
    }

    public static ArrayList<Tuple> assignScoresToSeeds(ArrayList<Tuple> tuples,
                                                       ArrayList<SeedScoreModel> seeds) {

        Map<String, Pair<Double, Double>> knownModifiers = SentiWordNetService.getInstance().getModifiers();
        for (Tuple tuple : tuples) {
            for (SeedScoreModel model : seeds) {
                String seed = model.getSeed().trim();
                String source = tuple.getSource().getValue();

                if (tuple.getTarget() != null) {
                    String target = tuple.getTarget().getValue();

                    if (seed.equals(target.trim())) {
                        double score = model.getScore();
                        tuple.getSource().setScore(score);
                        tuple.getTarget().setScore(score);
                        tuple.getSource().setHasModifier(model.hasModifier());
                        tuple.getTarget().setHasModifier(model.hasModifier());
                    }
                }
                if (seed.equals(source.trim())) {
                    double score = model.getScore();
                    if (tuple.getSource().getWordBefore() != null && model.getModifier() != null && tuple.getSource().getWordBefore().equals(model.getModifier())) {
                        tuple.getSource().setScore(score);
                        tuple.getSource().setHasModifier(true);
                        if (tuple.getTarget() != null) {
                            tuple.getTarget().setScore(score);
                            tuple.getTarget().setHasModifier(true);
                        }
                    } else if (tuple.getSource().getWordBefore() != null && !knownModifiers.containsKey(tuple.getSource().getWordBefore()) && !model.hasModifier()) {
                        tuple.getSource().setScore(score);
                        if (tuple.getTarget() != null) {
                            tuple.getTarget().setScore(score);
                        }
                    } else if (tuple.getSource().getWordBefore() == null && !model.hasModifier()) {
                        tuple.getSource().setScore(score);
                        if (tuple.getTarget() != null) {
                            tuple.getTarget().setScore(score);
                        }
                    }
                }
            }

        }
        return tuples;
    }

    public static ArrayList<Tuple> assignScores(ArrayList<Tuple> data,
                                                ArrayList<SeedScoreModel> seeds) {

        for (Tuple tuple : data) {
            if (isSeed(seeds, tuple.getSource().getValue())
                    || (tuple.getTarget() != null)
                    && isSeed(seeds, tuple.getTarget().getValue())) {
                if (tuple.getSource().getScore() != Classification.DEFAULT_SCORE) {
                    ScorePropagator.propagateScore(data, tuple);
                }
            }
        }

        return data;
    }

    private static boolean isSeed(ArrayList<SeedScoreModel> seeds, String word) {
        for (SeedScoreModel model : seeds) {
            if (model.getSeed().toLowerCase().equals(word.toLowerCase()))
                return true;
        }
        return false;
    }
}
