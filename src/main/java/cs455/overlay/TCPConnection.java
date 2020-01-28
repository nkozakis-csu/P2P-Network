package cs455.overlay;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPConnection{

    private static Logger LOG = LogManager.getLogger(TCPConnection.class);

    private Socket sendSocket;
    private Socket recvSocket;
    private int recvNum = 0;
    private ServerSocket serverSocket;
    private DataOutputStream dataOut;
    private DataInputStream dataIn;
    private boolean terminate = false;
    private int port;
    private Thread recvThread;

    public TCPConnection(int port) {
        this.port = port;
        System.out.println("start it up baybe");
        Thread listenThread = new Thread(this::listenThread);
        recvThread = new Thread(this::recvThread);
        listenThread.start();
    }

    public void connect(String ip, int port) {
        try {
            System.out.println("Sending secret integer (76)");
            sendSocket = new Socket(ip, port);
            System.out.println("Connected to: "+sendSocket.getInetAddress());
            dataOut = new DataOutputStream(sendSocket.getOutputStream());
            dataOut.writeInt(75);
            dataOut.flush();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void recvThread() {
        System.out.println("Receiving");
        while(recvSocket != null && !terminate){
            try {
                System.out.println("server gonna read");
                int data = dataIn.readInt();
                LOG.warn("received: "+data);

            } catch (SocketException se){
                System.out.println(se.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void listenThread() {
        try {
            System.out.print("listening");
            serverSocket = new ServerSocket(port);
            while (!terminate) {
                    recvSocket = serverSocket.accept();
                    System.out.println("Server address: "+serverSocket.getInetAddress());
                    dataIn = new DataInputStream(recvSocket.getInputStream());
                    recvThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getPort(){
        return 50000;
    }

    public static void main(String[] args) throws InterruptedException {
        TCPConnection a = new TCPConnection(50000);
        TCPConnection b = new TCPConnection(50001);
        Thread.sleep(5000);
        b.connect("0.0.0.0", 50000);
    }

}
