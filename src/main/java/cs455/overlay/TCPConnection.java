package cs455.overlay;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPConnection{
    
    private static final Logger LOG = LogManager.getLogger(TCPConnection.class);

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
        LOG.debug("start it up baybe");
        Thread listenThread = new Thread(this::listenThread);
        recvThread = new Thread(this::recvThread);
        listenThread.start();
    }

    public void connect(String ip, int port) {
        try {
            LOG.debug("Sending secret integer (76)");
            sendSocket = new Socket(ip, port);
            LOG.debug("Connected to: "+sendSocket.getInetAddress());
            dataOut = new DataOutputStream(sendSocket.getOutputStream());
            dataOut.writeInt(75);
            dataOut.flush();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void recvThread() {
        LOG.debug("Receiving");
        while(recvSocket != null && !terminate){
            try {
                LOG.debug("server gonna read");
                int data = dataIn.readInt();
                LOG.debug("received: "+data);

            } catch (SocketException se){
                LOG.debug(se.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void listenThread() {
        try {
            LOG.debug("listening");
            serverSocket = new ServerSocket(port);
            while (!terminate) {
                    recvSocket = serverSocket.accept();
                    LOG.debug("Server address: "+serverSocket.getInetAddress());
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
        LOG.warn("test");
        LOG.debug("TESTING DEBUG");
        LOG.error("ERROR");
        TCPConnection a = new TCPConnection(50000);
        TCPConnection b = new TCPConnection(50001);
        Thread.sleep(5000);
        b.connect("0.0.0.0", 50000);
    }

}
