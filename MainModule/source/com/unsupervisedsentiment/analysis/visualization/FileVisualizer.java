package com.unsupervisedsentiment.analysis.visualization;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import com.unsupervisedsentiment.analysis.model.FileVisualisationModel;
import com.unsupervisedsentiment.analysis.model.Word;

public class FileVisualizer extends JFrame {
	private static final int MIN_OW_SIZE = 2;

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

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(100, 100, (int) dim.getWidth(), (int) dim.getHeight());
		setLocationRelativeTo(null);

		JTabbedPane jtp = new JTabbedPane();
		getContentPane().add(jtp);
		JPanel jp1 = new JPanel();
		JPanel jp2 = new JPanel();

		jp1.setLayout(new GridLayout(0, 3));
		JScrollPane scrollPanel = new JScrollPane(jp1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPanel.setPreferredSize(new Dimension(getWidth(), getHeight()));

		JLabel label1 = new JLabel();
		label1.setText("Graph visualization");
		JLabel label2 = new JLabel();
		jp2.add(label2);
		jtp.addTab("Graph visualization", scrollPanel);
		jtp.addTab("Polairty Distribution", jp2);

		initGraphVisualization(jp1);
		initPolarityDistributionTab(jp2);

		this.setVisible(true);
	}

	private void initGraphVisualization(JPanel panel) {

		Map<Word, Set<Word>> sortedAssociationMap = visualisationService.getSortedAssociationMap(model.getTuples());

		for (Entry<Word, Set<Word>> e : sortedAssociationMap.entrySet()) {
			if (e.getValue().size() >= MIN_OW_SIZE)
				panel.add(visualisationService.createVisualisationGraphFromTuples(e));
		}

	}

	private void initPolarityDistributionTab(JPanel panel) {
		final JFreeChart chart = visualisationService.createPolarityDistributionChart(model.getPolarityDistribution());
		final ChartPanel chartPanel = new ChartPanel(chart);
		panel.add(chartPanel);

	}
}
