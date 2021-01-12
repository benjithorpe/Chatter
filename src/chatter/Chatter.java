package chatter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Chatter {

    JFrame frame = new JFrame("Chatter");
    JButton sendButton = new JButton("Send");
    JTextField outgoingMsg = new JTextField(20);
    JTextArea textArea = new JTextArea();
    JScrollPane scrollPane = new JScrollPane(textArea,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    BufferedReader bufferedReader; // reads from the server
    PrintWriter printWriter; // sends messages to clients
    Socket socket;

    public static void main(String[] args) {
        nimbusLAF();
        new Chatter().design();
    }

    void design() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(400, 500); //to be used after testing
        frame.setSize(400, 350);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // text field and send button panel
        JPanel sendPanel = new JPanel();
        sendPanel.add(outgoingMsg);
        sendPanel.add(sendButton);
        sendButton.addActionListener(new SendButtonListener());

        textArea.setEditable(false);
        frame.getContentPane().add(BorderLayout.SOUTH, sendPanel);
        frame.getContentPane().add(BorderLayout.CENTER, scrollPane);

        setUpNetworking();
        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();
        frame.setVisible(true);
    }

    // to be changed to: connectToServer()
    private void setUpNetworking() {
        try {
            socket = new Socket("127.0.0.1", 5_000);
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);

            printWriter = new PrintWriter(socket.getOutputStream());
            System.out.println("Connection Succssful!!");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public class SendButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            // removes trailing spaces then check if message is empty
//            if (!outgoingMsg.getText().trim().isEmpty()) {
            try {
//                textArea.append(outgoingMsg.getText() + "\n");
                printWriter.println(outgoingMsg.getText());
                printWriter.flush();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
//            }
            outgoingMsg.setText("");
            outgoingMsg.requestFocus();
        }
    }

    void sendToServer(String message) {
        // sends message to server
    }

    private static void nimbusLAF() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
        }
    }

    public class IncomingReader implements Runnable {

        @Override
        public void run() {
            String message;
            try {
                while ((message = bufferedReader.readLine()) != null) {
                    System.out.println("read " + message);
                    textArea.append(message + "\n");
                }
            } catch (IOException e) {
                System.out.println("Err " + e.getMessage());
            }
        }

    }
}
