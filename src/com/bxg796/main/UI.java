package com.bxg796.main;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
public class UI extends JFrame {

	public static final String TOGGLE_COMMAND = "togglegui";
	
	private static final long serialVersionUID = 8075642296734150448L;

	private JPanel textPanel;

	private JTextArea textArea;

	private JScrollPane scrollPane;

	private JPanel inputPanel;

	private JTextField textField;

	private String userInput;

	private Object holder;

	private BufferedReader user;

	private boolean guiMode;

	public UI(String frameTitle, Object holder) {
		this.holder = holder;
		user = new BufferedReader(new InputStreamReader(System.in));
		guiMode = false;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle(frameTitle);
		setVisible(false);

		textPanel = new JPanel(new FlowLayout());
		textPanel.setPreferredSize(new Dimension(1800, 800));

		textArea = new JTextArea(52, 160);

		textArea.setLineWrap(true);
		textArea.setEditable(false);

		scrollPane = new JScrollPane(textArea);

		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		textPanel.add(scrollPane);

		add(textPanel, BorderLayout.CENTER);

		inputPanel = new JPanel(new FlowLayout());

		textField = new JTextField(44);

		textField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (holder) {
					// Get the text in the textField
					userInput = textField.getText();

					// Clear the text field
					textField.setText(null);

					// Notify the holder that input as been entered to allow the code to continue
					holder.notify();
				}
			}
		});

		inputPanel.add(textField/* , BorderLayout.SOUTH */);

		add(inputPanel, BorderLayout.SOUTH);

		pack();

	}

	/**
	 * Print a message to the text area and moving to the next line.
	 * 
	 * @param message
	 *            The message to be printed to the text area.
	 */
	public void tell(String message) {
		System.out.println(message);
		textArea.append(message);
		textArea.append("\n");
	}

	public String readLine() throws InterruptedException, IOException {
		synchronized (holder) {
			// If the guiMode is true it looks for user input from the textField
			if (guiMode == true) {
				while (userInput == null || userInput.isEmpty()) {
					// Wait for the user to input something
					holder.wait();
				}
			} else {
				// Otherwise it looks for input from the console
				userInput = user.readLine();
			}

			if (userInput.equals(TOGGLE_COMMAND)) {
				toggleGUIMode();
				userInput = null;
				userInput = readLine();
			}

			String output = userInput;
			// Set the userInput to null to reset it
			userInput = null;
			return output;
		}
	}

	/**
	 * Toggle the guiMode variable and set the visibility of the UI to equal
	 * the guiMode.
	 */
	private void toggleGUIMode() {
		if (guiMode == true) {
			guiMode = false;
		} else {
			guiMode = true;
		}
		setVisible(guiMode);
	}

}

