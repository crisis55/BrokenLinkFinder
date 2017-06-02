package broken;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

// Author: Lucas Galleguillos
// Filename: ApplicationUserInterface.java

class ApplicationUserInterface {
	private JFrame frame;
	private JPanel panel;
	private JLabel inputLabel;
	private JTextField inputField;
	private JButton scanButton;
	private JButton clearButton;
	private JButton saveButton;
	private JLabel statusLabel;
	final private JTextArea outputField;
	final private JScrollPane outputScroll;
	final private JFileChooser fileChooser;

	public ApplicationUserInterface() {
		frame = new JFrame("BrokenLinkFinder");
		panel = new JPanel();
		inputLabel = new JLabel("Webpage URL:");
		inputField = new JTextField();
		scanButton = new JButton("Scan"); // Starts scanning process.
		clearButton = new JButton("Clear"); // Clears fields that the user interacts with.
		saveButton = new JButton("Save"); // Saves output to text file.
		statusLabel = new JLabel("Status:");
		outputField = new JTextArea();
		outputScroll = new JScrollPane(outputField); // Allows for better user experience.
		fileChooser = new JFileChooser();
	}

	public void start() {
		editLabels();
		editFields();
		enableButtons();
		editPanel();
		editFrame();
	}

	private void editLabels() {
		statusLabel.setFont(new Font(statusLabel.getName(), Font.PLAIN, 10));
	}

	private void editFields() {
		inputField.setColumns(30);

		outputField.setRows(27);
		outputField.setColumns(38);
		outputField.setLineWrap(false);
		outputField.setEditable(false);
		outputField.setVisible(true);

		outputScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		outputScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	}

	private void enableButtons() {
		Dimension buttonDimension = new Dimension(50, 20);
		Insets buttonInsets = new Insets(0, 0, 0, 0);

		scanButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent scan) {
				String url = inputField.getText();
				LinkChecker check = new LinkChecker(url);
				check.scanPage();
				Status stat = check.getStatus();
				if (stat.equals(Status.SUCCESS)) {
					statusLabel.setText("Status: Scan successful.");
					TreeMap<Integer, String> brokenLinks = check.getBrokenLinks();
					outputField.setText("Scan Report for " + inputField.getText() + "\n");
					if (!brokenLinks.isEmpty()) {
						outputField.append("Result: " + brokenLinks.size() + " broken links found.\n");
						outputField.append("\n");
						for (Integer n: brokenLinks.keySet()) {
							outputField.append("Line " + n + ": " + brokenLinks.get(n) + "\n");
						}
					} else {
						outputField.append("Result: 0 broken links found.");
					}
				} else if (stat.equals(Status.SCAN_ERROR)) {
					statusLabel.setText("Status: Scan failed.");
				} else if (stat.equals(Status.BAD_URL_OR_CONNECTION)) {
					statusLabel.setText("Status: Bad URL or connection.");
				}
			}
		});
		scanButton.setPreferredSize(buttonDimension);
		scanButton.setMargin(buttonInsets);

		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent clear) {
				inputField.setText("");
				statusLabel.setText("Status: ");
				outputField.setText("");
			}
		});
		clearButton.setPreferredSize(buttonDimension);
		clearButton.setMargin(buttonInsets);

		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent save) {
				String[] outputLines = outputField.getText().split("\n");
				File file = saveFile();
				try {
					saveToFile(outputLines, file);
					statusLabel.setText("Status: File saved.");
				} catch (NullPointerException npe1) {
					statusLabel.setText("Status: File not saved.");
				}
			}
		});
		saveButton.setPreferredSize(buttonDimension);
		saveButton.setMargin(buttonInsets);
	}

	private File saveFile() {
		if (fileChooser.showSaveDialog(panel) == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}
		return null;
	}

	private void saveToFile(String[] a, File f) {
		try {
			FileWriter fileWriter = new FileWriter(f);
			for (String s: a) {
				fileWriter.write(s + "\n");
			}
			fileWriter.close();
		} catch (IOException ioe1) {
			f.mkdir();
		}
	}

	// Used multiple panels to achieve an organized user interface.
	private void editPanel() {
		panel.setPreferredSize(new Dimension(440, 500));
		panel.setBackground(Color.WHITE);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

		Border panelBorder = BorderFactory.createLineBorder(Color.BLACK);

		JPanel inputPanel = new JPanel();
		inputPanel.setPreferredSize(new Dimension(440, 30));
		inputPanel.setBackground(Color.WHITE);
		inputPanel.setBorder(panelBorder);
		inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));
		inputPanel.add(inputLabel);
		inputPanel.add(inputField);

		JPanel statusPanel = new JPanel();
		statusPanel.setPreferredSize(new Dimension(210, 30));
		statusPanel.setBackground(Color.WHITE);
		statusPanel.setBorder(panelBorder);
		statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		statusPanel.add(statusLabel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(230, 30));
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.setBorder(panelBorder);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
		buttonPanel.add(scanButton);
		buttonPanel.add(clearButton);
		buttonPanel.add(saveButton);

		JPanel outputPanel = new JPanel();
		outputPanel.setPreferredSize(new Dimension(440, 440));
		outputPanel.setBackground(Color.WHITE);
		outputPanel.setBorder(panelBorder);
		outputPanel.add(outputScroll);

		panel.add(inputPanel);
		panel.add(statusPanel);
		panel.add(buttonPanel);
		panel.add(outputPanel);
	}

	public void editFrame() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
}
