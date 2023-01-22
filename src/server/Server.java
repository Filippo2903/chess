package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private static Scanner inputPlayerOne, inputPlayerTwo;
    private static PrintWriter outputPlayerOne, outputPlayerTwo;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(4445);

            Socket clientSocketOne = serverSocket.accept();
            Socket clientSocketTwo = serverSocket.accept();

            PlayerColor colorPlayerOne, colorPlayerTwo;
            if ((int) (Math.random() * 2) == 0) {
                colorPlayerOne = PlayerColor.WHITE;
                colorPlayerTwo = PlayerColor.BLACK;
            } else {
                colorPlayerOne = PlayerColor.BLACK;
                colorPlayerTwo = PlayerColor.WHITE;
            }

            ClientHandler server = new ClientHandler(clientSocketOne, clientSocketTwo, colorPlayerOne, colorPlayerTwo);

        } finally {
            if (serverSocket == null)
                System.exit(-1);

            serverSocket.close();
            inputPlayerOne.close();
            inputPlayerTwo.close();
            outputPlayerOne.close();
            outputPlayerTwo.close();
        }
    }

    private static class ClientHandler {
        public ClientHandler(Socket clientPlayerOne, Socket clientPlayerTwo, PlayerColor colorPlayerOne, PlayerColor colorPlayerTwo) {
//            Scanner inputPlayerOne, inputPlayerTwo;
//            PrintWriter outputPlayerOne, outputPlayerTwo;
            try {
                inputPlayerOne = new Scanner(clientPlayerOne.getInputStream());
                outputPlayerOne = new PrintWriter(clientPlayerOne.getOutputStream(), true);

                inputPlayerTwo = new Scanner(clientPlayerTwo.getInputStream());
                outputPlayerTwo = new PrintWriter(clientPlayerTwo.getOutputStream(), true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            outputPlayerOne.println(colorPlayerOne);
            outputPlayerTwo.println(colorPlayerTwo);

//            boolean playing = true;

            if (colorPlayerTwo == PlayerColor.WHITE) {
                send(inputPlayerTwo, outputPlayerOne);
            }
            while (true) {
                send(inputPlayerOne, outputPlayerTwo);
                send(inputPlayerTwo, outputPlayerOne);
            }
        }

        public void send(Scanner sender, PrintWriter receiver) {
            String serializedMove;
            // Read from sender client
            serializedMove = sender.nextLine();
            System.out.println("Read " + serializedMove);

            Packet packet = null;
            try {
                packet = Packet.fromString(serializedMove);
            } catch (Exception e) {
                System.err.println("Error serializing the packet");
                System.exit(-1);
            }

            packet.from.y = 7 - packet.from.y;
            packet.to.y = 7 - packet.to.y;

            try {
                serializedMove = packet.serializeToString();
            } catch (IOException e) {
                System.err.println("Error serializing the packet");
                System.exit(-1);
            }

            // Send to other client
            receiver.println(serializedMove);
        }
    }
}

//public class Server {
//    private static class ServerThread {
//        private BufferedReader input = null;
//        private PrintWriter myOutput = null, enemyOutput = null;
//        private PlayerColor myColor;
//
//        public ServerThread(Socket mySocket, Socket enemySocket, PlayerColor color) {
//            try {
//                input = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
//                myOutput = new PrintWriter(mySocket.getOutputStream(), true);
//                enemyOutput = new PrintWriter(enemySocket.getOutputStream(), true);
//            }
//            catch (IOException e){
//                System.out.println("IO error in server thread");
//            }
//
//            this.myColor = color;
//
//            myOutput.println(color);
//            myOutput.flush();
//        }
//
//        public boolean waitMove() {
//            String serializedMove = null;
//
//            // Read from client
//            try {
//                serializedMove = input.readLine();
//                System.out.println("Read " + serializedMove);
//            } catch (IOException e) {
//                System.err.println("Error reading the line");
//            }
//
//            Packet packet = null;
//            try {
//                packet = Packet.fromString(serializedMove);
//            } catch (Exception e) {
//                System.err.println("Error serializing the packet");
//                System.exit(-1);
//            }
//
//            packet.from.y = 7 - packet.from.y;
//            packet.to.y = 7 - packet.to.y;
//
//            try {
//                serializedMove = packet.serializeToString();
//            } catch (IOException e) {
//                System.err.println("Error serializing the packet");
//                System.exit(-1);
//            }
//
//            // Send to enemy socket
//            enemyOutput.println(serializedMove);
//            enemyOutput.flush();
//            System.out.println("Sent data to " + (myColor == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE));
//
//            return packet.endGame;
//        }
//
//        public void end() {
//            try {
//                input.close();
//            } catch (IOException e) {
//                System.err.println("Error closing the input");
//            }
//            enemyOutput.close();
//        }
//    }
//
//    public static void main(String[] args){
//        Socket playerOne, playerTwo;
//
//        ServerSocket serverSocket = null;
//        System.out.println("Server Listening...");
//
//        ServerThread serverThreadPlayerOne = null;
//        ServerThread serverThreadPlayerTwo = null;
//
//        // Tenta l'accesso alla porta
//        try {
//            serverSocket = new ServerSocket(4445);
//        } catch (IOException e){
//            e.printStackTrace();
//            System.err.println("Server error");
//            System.exit(-1);
//        }
//
//        try {
//            PlayerColor colorPlayerOne = (int) (Math.random() * 2) == 0 ? PlayerColor.WHITE : PlayerColor.BLACK;
//            PlayerColor colorPlayerTwo = colorPlayerOne == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE;
//
//            // Aspetta le connessioni
//            playerOne = serverSocket.accept();
//            System.out.println("Connection Established with Player 1 (" + colorPlayerOne + ")");
//
//            playerTwo = serverSocket.accept();
//            System.out.println("Connection Established with Player 2 (" + colorPlayerTwo + ")");
//
//            // Crea un nuovo thread per il nuovo client
//            serverThreadPlayerOne = new ServerThread(playerOne, playerTwo, PlayerColor.WHITE); // colorPlayerOne
//
//            // Crea un nuovo thread per il nuovo client
//            serverThreadPlayerTwo = new ServerThread(playerTwo, playerOne, PlayerColor.BLACK); // colorPlayerTwo
//
//            boolean playing = true;
//            while (playing) {
//                playing = serverThreadPlayerOne.waitMove();
//                if (!playing) break;
//                playing = serverThreadPlayerTwo.waitMove();
////                if (colorPlayerOne == PlayerColor.WHITE) {
////                    playing = serverThreadPlayerOne.waitMove();
////
////                    if (!playing) break;
////
////                    playing = serverThreadPlayerTwo.waitMove();
////                }
////                else {
////                    playing = serverThreadPlayerTwo.waitMove();
////
////                    if (!playing) break;
////
////                    playing = serverThreadPlayerOne.waitMove();
////                }
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//            System.err.println("Connection Error");
//            System.exit(-1);
//        } finally {
//            assert serverThreadPlayerOne != null;
//            serverThreadPlayerOne.end();
//            assert serverThreadPlayerTwo != null;
//            serverThreadPlayerTwo.end();
//        }
//
//    }
//}
