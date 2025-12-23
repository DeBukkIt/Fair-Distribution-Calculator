package eu.ajg.fairdistribution;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GuiFairDistribution extends JFrame {

	private static final long serialVersionUID = -1108375470708739666L;
	
	protected JTextField inputFieldOptionsFile;
    protected JTextField inputFieldWishesFile;
    protected JTextField inputFieldResultFile;
    protected JTextArea textAreaLog;

    protected String lastDirectoryVisitedPath;
    
    protected JButton generateButton;
    
    public GuiFairDistribution() {
        initComponents();
        captureSystemOutput();
    }

	private void captureSystemOutput() {
		OutputStreamRedirector osr = new OutputStreamRedirector();
		osr.setOutputStreamHandler((s) -> log(s));
		System.setOut(new PrintStream(osr));
	}

	private void initComponents() {
    	setTitle("Fair Student Distribution Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 400);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        JPanel leftPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        inputFieldOptionsFile = new JTextField();
        inputFieldWishesFile = new JTextField();
        inputFieldResultFile = new JTextField();

        inputFieldOptionsFile.setEditable(false);
        inputFieldWishesFile.setEditable(false);
        inputFieldResultFile.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        leftPanel.add(new JLabel("Options file:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        leftPanel.add(inputFieldOptionsFile, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        leftPanel.add(createSelectInputFileButton(inputFieldOptionsFile, true), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        leftPanel.add(new JLabel("Wishes file:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        leftPanel.add(inputFieldWishesFile, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        leftPanel.add(createSelectInputFileButton(inputFieldWishesFile, true), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        leftPanel.add(new JLabel("Result file:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        leftPanel.add(inputFieldResultFile, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        leftPanel.add(createSelectOutputFileButton(inputFieldResultFile), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;

        generateButton = new JButton("Generate");
        generateButton.addActionListener(e -> generate());
        leftPanel.add(generateButton, gbc);

        add(leftPanel, BorderLayout.CENTER);

        textAreaLog = new JTextArea();
        textAreaLog.setEditable(false);
        textAreaLog.setLineWrap(true);
        textAreaLog.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textAreaLog);
        scrollPane.setPreferredSize(new Dimension(300, 0));
        add(scrollPane, BorderLayout.EAST);
        
        generateButton.setEnabled(false);
	}

	private JButton createSelectInputFileButton(JTextField targetField, boolean csvOnly) {
        JButton button = new JButton("Select");
        button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(lastDirectoryVisitedPath);
            if (csvOnly) {
                chooser.setFileFilter(
                        new FileNameExtensionFilter("CSV files", "csv"));
            }
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                lastDirectoryVisitedPath = file.getParent();
                targetField.setText(file.getAbsolutePath());
            }
            
            generateButton.setEnabled(checkAllFilesSelected());

        });
        return button;
    }

    private JButton createSelectOutputFileButton(JTextField targetField) {
        JButton button = new JButton("Select");
        button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(lastDirectoryVisitedPath);
            chooser.setFileFilter(
                    new FileNameExtensionFilter("Text files", "txt"));
            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                lastDirectoryVisitedPath = file.getParent();
                targetField.setText(file.getAbsolutePath());
            }
            
            generateButton.setEnabled(checkAllFilesSelected());
            
        });
        return button;
    }
    
    private boolean checkAllFilesSelected() {
		return !(inputFieldOptionsFile.getText().isEmpty() || inputFieldWishesFile.getText().isEmpty()
				|| inputFieldResultFile.getText().isEmpty());
    }

    private void generate() {
		try {
			
			CSVInputReader inputReader = new CSVInputReader(inputFieldOptionsFile.getText(), inputFieldWishesFile.getText());

			FairDistribution fd = new FairDistribution();
			Map<Integer, List<String>> result = fd.distribute(inputReader.getOptions(),
					inputReader.getStudentsWithPriorities());

			ResultWriter resultWriter = new ResultWriter(inputFieldResultFile.getText());
			resultWriter.write(result, inputReader.getOptions(), inputReader.getStudentsWithPriorities());
		
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    private void log(String message) {
        textAreaLog.append(message + "\n");
    }
}
