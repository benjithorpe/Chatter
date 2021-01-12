package chatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ChatterServer {

    ArrayList clientOutputStreams;
    ServerSocket serverSocket;

    public class ClientHandler implements Runnable {

        BufferedReader bufferedReader;
        Socket socket;

        public ClientHandler(Socket clientSocket) {
            try {
                socket = clientSocket;
                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = bufferedReader.readLine()) != null) {
                    System.out.println("read " + message);
                    tellEveryone(message);
                }
            } catch (IOException e) {
            }
        }
    }

    public static void main(String[] args) {
        ChatterServer chatterServer = new ChatterServer();
        chatterServer.start();
    }

    private void tellEveryone(String message) {
        Iterator it = clientOutputStreams.iterator();

        while (it.hasNext()) {
            try {
                PrintWriter printWriter = (PrintWriter) it.next();
                printWriter.println(message);
                printWriter.flush();
            } catch (Exception e) {
            }
        }
    }

    void start() {
        clientOutputStreams = new ArrayList();

        try {
            serverSocket = new ServerSocket(5000);

            while (true) {
                Socket socket = serverSocket.accept(); // waits and accepts incoming clients
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                clientOutputStreams.add(printWriter);

                Thread t = new Thread(new ClientHandler(socket));
                t.start();

                System.out.println("got a connection");
            }

        } catch (IOException e) {
        }
    }

    void stop() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
        }
    }

}
