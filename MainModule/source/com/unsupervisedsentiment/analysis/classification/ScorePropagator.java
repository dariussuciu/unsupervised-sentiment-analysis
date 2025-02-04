package com.unsupervisedsentiment.analysis.classification;

import java.util.ArrayList;

import com.unsupervisedsentiment.analysis.model.Tuple;

public class ScorePropagator {

	public static void propagateScore(ArrayList<Tuple> data, Tuple assignedTuple) {
		for (Tuple tuple : data) {
			if (tuple.getSource().getValue().toLowerCase().equals(assignedTuple.getSource().getValue().toLowerCase())
					|| (tuple.getTarget() != null && assignedTuple.getTarget() != null && tuple.getTarget().getValue()
							.toLowerCase().equals(assignedTuple.getTarget().getValue().toLowerCase()))
					|| (assignedTuple.getTarget() != null && tuple.getSource().getValue().toLowerCase()
							.equals(assignedTuple.getTarget().getValue().toLowerCase()))
					|| (tuple.getTarget() != null && tuple.getTarget().getValue().toLowerCase()
							.equals(assignedTuple.getSource().getValue().toLowerCase()))) {
				if (tuple.getSource().getScore() == Classification.DEFAULT_SCORE) {
					double score = assignedTuple.getSource().getScore();
					tuple.getSource().setScore(score);
					if (tuple.getTarget() != null)
						tuple.getTarget().setScore(score);
				}
				// if the tuple already has a score, assign an average
				else {
					double score = (assignedTuple.getSource().getScore() + tuple.getSource().getScore()) / 2;
					tuple.getSource().setScore(score);
					if (tuple.getTarget() != null)
						tuple.getTarget().setScore(score);
				}

			}
		}
	}

}
