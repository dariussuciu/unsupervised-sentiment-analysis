package com.unsupervisedsentiment.analysis.visualization;

import java.awt.Color;
import java.awt.GradientPaint;
import java.util.Comparator;
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

import com.unsupervisedsentiment.analysis.model.FileVisualisationModel;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.modules.evaluation.EvaluationMetadata;

public class VisualisationService {

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

	/* Comparator for map */
	class ValueComparator implements Comparator<Double> {

		Map<Double, Integer> base;

		public ValueComparator(Map<Double, Integer> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(Double a, Double b) {
			if (a <= b) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}
}
