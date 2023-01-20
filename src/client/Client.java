package client;// A simple Client Server Protocol .. Client for Echo Server
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        InetAddress address = InetAddress.getLocalHost();
        Socket socket = null;
        String line;
        Scanner scanner = new Scanner(System.in);
        Scanner is = null;
        PrintWriter os = null;

        try {
            socket = new Socket(address, 4445);
            is = new Scanner(socket.getInputStream());
            os = new PrintWriter(socket.getOutputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
            System.err.print("IO Exception");
            System.exit(-1);
        }

        System.out.println("Client Address : " + address);
        System.out.println("Enter Data to echo Server ( Enter QUIT to end):");

        String response;
        try{
            while (true) {
                line = scanner.nextLine();
                os.println(line);
                os.flush();
                if (line.equals("QUIT"))
                    break;
                response = is.nextLine();
                System.out.println("Server Response : " + response);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Socket read Error");
        }
        finally {
            is.close();
            os.close();
            scanner.close();
            socket.close();
            System.out.println("Connection Closed");
        }
    }
}
