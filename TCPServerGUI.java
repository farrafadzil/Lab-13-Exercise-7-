package exercise6;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class TCPServerGUI extends JFrame {
    private JTextArea logTextArea;
    private JLabel requestCountLabel;

    private int port;
    private int requestCount;

    public TCPServerGUI(int port) {
        this.port = port;
        this.requestCount = 0;

        setTitle("Translation Server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        startServer();
    }

    private void initComponents() {
        // Create and configure components
        logTextArea = new JTextArea(10, 30);
        logTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        requestCountLabel = new JLabel("Requests received: 0");

        // Create and configure the layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(requestCountLabel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            logTextArea.append("Server started. Waiting for client connections...\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logTextArea.append("Client connected: " + clientSocket.getInetAddress() + "\n");

                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

                String text = reader.readLine();
                String targetLanguage = reader.readLine();

                Translator translator = new Translator();
                String translatedText = translator.translate(text, targetLanguage);

                writer.println(translatedText);

                reader.close();
                writer.close();
                clientSocket.close();

                logTextArea.append("Request processed:\n");
                logTextArea.append("   Original Text: " + text + "\n");
                logTextArea.append("   Target Language: " + targetLanguage + "\n");
                logTextArea.append("   Translated Text: " + translatedText + "\n\n");

                requestCount++;
                requestCountLabel.setText("Requests received: " + requestCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TCPServerGUI server = new TCPServerGUI(9999);
            }
        });
    }
}
