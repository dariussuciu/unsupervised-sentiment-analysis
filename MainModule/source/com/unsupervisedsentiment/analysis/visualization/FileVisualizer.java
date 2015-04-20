package com.unsupervisedsentiment.analysis.visualization;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

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
		label2.setText("Polairty Distribution");
		jp1.add(label1);
		jp2.add(label2);
		jtp.addTab("Graph visualization", jp1);
		jtp.addTab("Polairty Distribution", jp2);

		initPolarityDistributionTab(jp2);

		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setVisible(true);
	}

	private void initPolarityDistributionTab(JPanel jp1) {
		final JFreeChart chart = visualisationService.createPolarityDistributionChart(model.getPolarityDistribution());
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(500, 270));
		jp1.add(chartPanel);

	}
}
