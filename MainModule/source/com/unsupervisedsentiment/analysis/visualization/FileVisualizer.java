package com.unsupervisedsentiment.analysis.visualization;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import com.mxgraph.swing.mxGraphComponent;
import com.unsupervisedsentiment.analysis.model.FileVisualisationModel;

public class FileVisualizer extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FileVisualisationModel model;
	private VisualisationService visualisationService;

	public FileVisualizer(FileVisualisationModel model) {
		super();
		this.model = model;
		this.visualisationService = VisualisationService.getInstance();

		setTitle("Data analysis for " + model.getEvaluationMetadataResults().getFilename());
		JTabbedPane jtp = new JTabbedPane();
		getContentPane().add(jtp);
		JPanel jp1 = new JPanel();
		JPanel jp2 = new JPanel();
		JLabel label1 = new JLabel();
		label1.setText("Graph visualization");
		JLabel label2 = new JLabel();
		jp2.add(label2);
		jtp.addTab("Graph visualization", jp1);
		jtp.addTab("Polairty Distribution", jp2);

		initGraphVisualization(jp1);
		initPolarityDistributionTab(jp2);

		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setVisible(true);
	}

	private void initGraphVisualization(JPanel panel) {
		final mxGraphComponent graph = visualisationService.createVisualisationGraphFromTuples(model.getTuples());
		panel.add(graph);
	}

	private void initPolarityDistributionTab(JPanel panel) {
		final JFreeChart chart = visualisationService.createPolarityDistributionChart(model.getPolarityDistribution());
		final ChartPanel chartPanel = new ChartPanel(chart);
		panel.add(chartPanel);

	}
}
