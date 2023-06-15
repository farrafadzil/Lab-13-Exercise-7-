package exercise6;

// TCPClientGUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class TCPClientGUI extends JFrame {
    private JTextField textInput;
    private JComboBox<String> languageComboBox;
    private JTextArea outputTextArea;
    private JButton translateButton;
    private JButton exitButton;
    private JLabel requestCountLabel;

    private String serverIP;
    private int serverPort;
    private int requestCount;

    public TCPClientGUI(String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.requestCount = 0;

        setTitle("Translation Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        // Create and configure components
        textInput = new JTextField(20);
        languageComboBox = new JComboBox<>(new String[]{"Bahasa Malaysia", "Arabic", "Korean"});
        outputTextArea = new JTextArea(10, 30);
        translateButton = new JButton("Translate");
        exitButton = new JButton("Exit");
        requestCountLabel = new JLabel("Requests made: 0");

        outputTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);

        // Create and configure the layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("English Text:"));
        inputPanel.add(textInput);
        inputPanel.add(new JLabel("Target Language:"));
        inputPanel.add(languageComboBox);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(translateButton);
        buttonPanel.add(exitButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Add action listeners to buttons
        translateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                translateButton.setEnabled(false);
                exitButton.setEnabled(false);
                performTranslation();
                translateButton.setEnabled(true);
                exitButton.setEnabled(true);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private void performTranslation() {
        try {
            Socket socket = new Socket(serverIP, serverPort);

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            String text = textInput.getText();
            String targetLanguage = (String) languageComboBox.getSelectedItem();

            writer.println(text);
            writer.println(targetLanguage);

            String translatedText = reader.readLine();

            outputTextArea.append("Original Text: " + text + "\n");
            outputTextArea.append("Target Language: " + targetLanguage + "\n");
            outputTextArea.append("Translated Text: " + translatedText + "\n\n");

            textInput.setText("");
            languageComboBox.setSelectedIndex(0);

            requestCount++;
            requestCountLabel.setText("Requests made: " + requestCount);

            reader.close();
            writer.close();
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TCPClientGUI client = new TCPClientGUI("localhost", 9999);
            }
        });
    }
}
