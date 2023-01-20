package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Server {
    private static class ServerThread extends Thread {
        private Scanner input = null;
        private PrintWriter output = null;
        private final Socket socket;

        public ServerThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            String line;

            try {
                input = new Scanner(socket.getInputStream());
                output = new PrintWriter(socket.getOutputStream());
            }
            catch (IOException e){
                System.out.println("IO error in server thread");
            }

            try {
                while (true) {
                    // Stdin
                    line = input.nextLine();
                    if (line.equals("QUIT"))
                        break;
                    output.println(line);
                    output.flush();
                    System.out.println("Response to Client  :  " + line);
                }
            }
            catch (NullPointerException e){
                line = this.getName(); //reused String line for getting thread name
                System.out.println("Client " + line + " Closed");
            }
            catch (NoSuchElementException e) {
                System.out.println("Client force-closed connection");
            }
            finally {
                try {
                    System.out.println("Connection Closing...");
                    input.close();
                    output.close();
                    socket.close();
                }
                catch (IOException ie){
                    System.out.println("Socket Close Error");
                }
            }
        }
    }

    public static void main(String[] args){
        Socket socket;
        ServerSocket serverSocket = null;
        System.out.println("Server Listening...");

        // Tenta l'accesso alla porta
        try {
            serverSocket = new ServerSocket(4445);
        }
        catch (IOException e){
            e.printStackTrace();
            System.err.println("Server error");
            System.exit(-1);
        }

        while (true) {
            try {
                // Accetta le connessioni
                socket = serverSocket.accept();
                System.out.println("Connection Established");

                // Crea un nuovo thread per il nuovo client
                ServerThread serverThread = new ServerThread(socket);
                serverThread.start();
            }
            catch (Exception e){
                e.printStackTrace();
                System.err.println("Connection Error");
                System.exit(-1);
            }
        }
    }
}
