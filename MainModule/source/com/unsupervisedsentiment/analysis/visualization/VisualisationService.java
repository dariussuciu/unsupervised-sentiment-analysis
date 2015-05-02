package com.unsupervisedsentiment.analysis.visualization;

import java.awt.Color;
import java.awt.GradientPaint;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxGraph;
import com.unsupervisedsentiment.analysis.model.FileVisualisationModel;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.evaluation.EvaluationMetadata;

public class VisualisationService {

	private static final int MAX_OW_PER_TARGET = 14;
	private static final int RADIUS = 175;

	private static final int I_OFFSET_X = RADIUS + 100;
	private static final int I_OFFSET_Y = RADIUS;

	private static final int OW_WIDTH = 50;
	private static final int OW_HEIGHT = 30;

	private static final int TAR_WIDTH = 60;
	private static final int TAR_HEIGHT = 40;

	private static VisualisationService instance;

	public static VisualisationService getInstance() {
		if (instance == null)
			instance = new VisualisationService();

		return instance;
	}

	private VisualisationService() {
	}

	public FileVisualisationModel createFileVisualisationModel(Set<Tuple> tuples,
			Map<Double, Integer> polarityDistribution, EvaluationMetadata evaluationMetadataResults) {
		return new FileVisualisationModel(tuples, polarityDistribution, evaluationMetadataResults);
	}

	public JFreeChart createPolarityDistributionChart(Map<Double, Integer> polarityDistributionMap) {

		final CategoryDataset dataset = createPolarityDistributionDataset(polarityDistributionMap);

		final JFreeChart chart = ChartFactory.createBarChart("Polarity distribution", // chart
																						// title
				"", // domain axis label
				"Value", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, // tooltips?
				false // URLs?
				);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

		// set the background color for the chart...
		chart.setBackgroundPaint(Color.white);

		// get a reference to the plot for further customisation...
		final CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(new Color(0xE0E0E0));
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		// set the range axis to display integers only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// disable bar outlines...
		final BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setDrawBarOutline(false);
		renderer.setMaximumBarWidth(.05);

		// set up gradient paints for series...
		final GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, Color.lightGray);
		final GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green, 0.0f, 0.0f, Color.lightGray);
		final GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red, 0.0f, 0.0f, Color.lightGray);
		renderer.setSeriesPaint(0, gp0);
		renderer.setSeriesPaint(1, gp1);
		renderer.setSeriesPaint(2, gp2);

		final CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
		// OPTIONAL CUSTOMISATION COMPLETED.

		return chart;
	}

	private DefaultCategoryDataset createPolarityDistributionDataset(Map<Double, Integer> polarityDistributionMap) {
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		final String polaritiesSeries = "Polarities";

		// first sort the map
		ValueComparator bvc = new ValueComparator(polarityDistributionMap);
		TreeMap<Double, Integer> sortedPolarityDistributionMap = new TreeMap<Double, Integer>(bvc);
		sortedPolarityDistributionMap.putAll(polarityDistributionMap);

		for (Entry<Double, Integer> entry : sortedPolarityDistributionMap.entrySet()) {
			dataset.addValue(entry.getValue(), polaritiesSeries, entry.getKey());
		}

		return dataset;
	}

	private class ValueComparator implements Comparator<Double> {

		public ValueComparator(Map<Double, Integer> base) {
		}

		public int compare(Double a, Double b) {
			return a <= b ? -1 : 1;
		}
	}

	public Map<Word, Set<Word>> getSortedAssociationMap(Set<Tuple> tuples) {
		Map<Word, Set<Word>> tupleMap = new HashMap<Word, Set<Word>>();

		for (Tuple tuple : tuples) {
			if (!tuple.getTupleType().equals(TupleType.Seed) && tuple.getTarget() != null) {
				Word target = tuple.getTarget();
				if (tupleMap.get(target) == null) {
					tupleMap.put(target, new HashSet<Word>());
				}
				Set<Word> oWs = tupleMap.get(target);
				oWs.add(tuple.getSource());
			}
		}

		WordMapComparator bvc = new WordMapComparator(tupleMap);
		TreeMap<Word, Set<Word>> sortedTupleMap = new TreeMap<Word, Set<Word>>(bvc);
		sortedTupleMap.putAll(tupleMap);

		return sortedTupleMap;
	}

	private class WordMapComparator implements Comparator<Word> {

		Map<Word, Set<Word>> base;

		public WordMapComparator(Map<Word, Set<Word>> tupleMap) {
			this.base = tupleMap;
		}

		public int compare(Word a, Word b) {
			int result = Integer.compare(base.get(b).size(), base.get(a).size());
			if (result == 0) {
				result = a.getValue().compareTo(b.getValue());
			}
			return result;
		}
	}

	public mxGraphComponent createVisualisationGraphFromTuples(Entry<Word, Set<Word>> e) {
		mxGraph graph = new mxGraph() {

			/** rather unnecessary but who knows **/
			// Ports are not used as terminals for edges, they are
			// only used to compute the graphical connection point
			public boolean isPort(Object cell) {
				mxGeometry geo = getCellGeometry(cell);

				return (geo != null) ? geo.isRelative() : false;
			}

			// Implements a tooltip that shows the actual
			// source and target of an edge
			public String getToolTipForCell(Object cell) {
				if (model.isEdge(cell)) {
					return convertValueToString(model.getTerminal(cell, true)) + " -> "
							+ convertValueToString(model.getTerminal(cell, false));
				}

				return super.getToolTipForCell(cell);
			}

			// Removes the folding icon and disables any folding
			public boolean isCellFoldable(Object cell, boolean collapse) {
				return false;
			}
		};

		Map<String, Object> style = graph.getStylesheet().getDefaultEdgeStyle();
		style.put(mxConstants.STYLE_EDGE, mxEdgeStyle.ElbowConnector);
		style.put(mxConstants.STYLE_ROUNDED, true);

		style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
		style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
		style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		style.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "#ffffff");

		graph.getModel().beginUpdate();
		graph.setCellsMovable(false);
		graph.setCellsResizable(false);

		Object parent = graph.getDefaultParent();

		try {

			mxCell v1 = (mxCell) graph.insertVertex(parent, null,
					e.getKey().getValue() + "\n(" + String.format("%.2f", e.getKey().getScore()) + ")", I_OFFSET_X,
					I_OFFSET_Y, TAR_WIDTH, TAR_HEIGHT);
			v1.setConnectable(false);

			float max = e.getValue().size() > MAX_OW_PER_TARGET ? MAX_OW_PER_TARGET : e.getValue().size();

			float theta = 360.0f / max;
			int i = 0;
			for (Word w : e.getValue()) {
				if (i > MAX_OW_PER_TARGET)
					break;

				double newX = Math.sin(theta * i) * RADIUS + I_OFFSET_X;
				double newY = Math.cos(theta * i) * RADIUS + I_OFFSET_Y;

				mxCell v2 = (mxCell) graph.insertVertex(parent, null, w.getValue(), newX, newY, OW_WIDTH, OW_HEIGHT,
						"shape=ellipse");
				v2.setConnectable(false);
				graph.insertEdge(parent, null, "", v1, v2);

				i++;
			}

		} finally {
			graph.getModel().endUpdate();
		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		return graphComponent;
	}
}
