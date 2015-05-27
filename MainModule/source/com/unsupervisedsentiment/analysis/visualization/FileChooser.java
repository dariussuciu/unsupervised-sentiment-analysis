package com.unsupervisedsentiment.analysis.visualization;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.unsupervisedsentiment.analysis.model.FileVisualisationModel;

public class FileChooser extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JList<FileVisualisationModel> fileList;

	public FileChooser(List<FileVisualisationModel> fileList) {
		// create the model and add elements
		DefaultListModel<FileVisualisationModel> listModel = new DefaultListModel<FileVisualisationModel>();
		for (FileVisualisationModel fileVisualisationModel : fileList) {
			listModel.addElement(fileVisualisationModel);
		}

		// create the list
		this.fileList = new JList<FileVisualisationModel>(listModel);
		// TODO: multiple selection - np, but later, too tired..
		this.fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.fileList.addMouseListener(new DoubleClickListener());

		add(new JScrollPane(this.fileList));

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("File Review List");
		this.setSize(200, 200);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private final class DoubleClickListener extends MouseAdapter {
		public void mouseClicked(MouseEvent evt) {
			if (evt.getClickCount() == 2) {
				// Double-click detected
				final FileVisualisationModel selectedFile = FileChooser.this.fileList.getSelectedValue();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						new FileVisualizer(selectedFile);
					}
				});
				System.out.println(selectedFile);
			}
		}
	}

}
