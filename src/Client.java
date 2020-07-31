import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private final InetAddress host;
    private final int port;

    public Client(final InetAddress host, final int port) {
        this.port = port;
        this.host = host;
    }

    private void excuted() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Your Name :");
        String name = sc.nextLine();
        final Socket client = new Socket(host, port);
        final ReadClient read = new ReadClient(client);
        read.start();
        final WriteClient write = new WriteClient(client,name);
        write.start();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client(InetAddress.getLocalHost(), 15797);
        client.excuted();
    }
}

class ReadClient extends Thread {
    private final Socket cliSocket;

    public ReadClient(final Socket cliSocket) {
        this.cliSocket = cliSocket;
    }

    @Override
    public void run() {
        DataInputStream ds = null;
        try {
            ds = new DataInputStream(cliSocket.getInputStream());
            while (true) {
                final String sms = ds.readUTF();
                System.out.println(sms);
            }
        } catch (final Exception e) {
            try {
                ds.close();
                cliSocket.close();
            } catch (final IOException ex) {
                System.out.println("Disconnected");
            }
        }

        super.run();
    }
}

class WriteClient extends Thread{
    private final Socket cliSocket;
    private String name;

    public WriteClient(final Socket cliSocket,String name){
        this.cliSocket = cliSocket;
        this.name = name; 
    }

    @Override
    public void run() {
        DataOutputStream dos =null;
        Scanner sc = null;

        try {
            dos = new DataOutputStream(cliSocket.getOutputStream());
            sc = new Scanner(System.in);
            while(true){
                final String sms = sc.nextLine();
                dos.writeUTF(name+" : "+sms);
                if(sms.contains("out")){
                    System.out.print("Type any key to exit");
                    dos.close();
                    cliSocket.close();    
                }
            }
        } catch (final Exception e) {
            try {
                dos.close();
                cliSocket.close();
            } catch (final IOException ex) {
                System.out.println("Disconnected");
            }
        }
        super.run();
    }

}
