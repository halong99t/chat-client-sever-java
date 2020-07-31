import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.ServerSocket;

public class Server {
    private int port;
    public static ArrayList<Socket> lSockets;

    public Server(int port) {
        this.port = port;
    }

    private void excuted() throws IOException {
        ServerSocket sever = new ServerSocket(port);
        WriteServer write = new WriteServer();
        write.start();
        System.out.println("Server is waiting !!");
        while (true) {  
            Socket socket = sever.accept();
            System.out.println("has connected to new friend");
            Server.lSockets.add(socket);
            ReadServer read = new ReadServer(socket);
            read.start();
        }

    }
    public static void main(String[] args) throws IOException {
        Server.lSockets = new ArrayList<>();
        Server server = new Server(15797);
        server.excuted();
    }

}

class ReadServer extends Thread {
    private final Socket serSocket;

    public ReadServer(final Socket serSocket) {
        this.serSocket = serSocket;
    }

    @Override
    public void run() {
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(serSocket.getInputStream());
            while (true) {
                String sms = dis.readUTF();
                if(sms.contains("out")){
                    Server.lSockets.remove(serSocket);
                    System.out.println("disconnect to " + serSocket);
                    dis.close();
                    serSocket.close();
                    continue;
                }
                for (Socket item : Server.lSockets) {
                    if (item.getPort() != serSocket.getPort()) {
                        DataOutputStream dos = new DataOutputStream(item.getOutputStream());
                        dos.writeUTF(sms);
                    } 
                }
                System.out.println(sms);
            }
        } catch (final Exception e) {
            try {
                dis.close();
                serSocket.close();
            } catch (final IOException ex) {
                System.out.println("Disconected !!");
            }
        }

        super.run();
    }
}

class WriteServer extends Thread {

    @Override
    public void run() {
        DataOutputStream dos = null;
        Scanner sc = new Scanner(System.in);
        while (true) {
            String sms = sc.nextLine();
            try {
                for (Socket item : Server.lSockets) {
                    dos = new DataOutputStream(item.getOutputStream());
                    dos.writeUTF("Server : "+sms);
                }
            } catch (Exception e) {
                
            }
        }

    }
}
