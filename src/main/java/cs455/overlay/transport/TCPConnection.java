package cs455.overlay.transport;
import cs455.overlay.wireformats.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class TCPConnection{
    
    private static final Logger LOG = LogManager.getLogger(TCPConnection.class);

    private Socket sock;
    private int recvNum = 0;
    protected int distance;
    private DataOutputStream dataOut;
    private DataInputStream dataIn;
    private boolean terminate = false;
    private int port;
    private Thread recvThread;

    public TCPConnection(Socket socket, int distance) {
        this.sock = socket;
        this.distance = distance;
        try {
            this.dataIn = new DataInputStream(sock.getInputStream());
            this.dataOut = new DataOutputStream(sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void send(Message m){
        Message.write(dataOut, m);
    }
    
    public Message recvAnyMessages(){
        return Message.read(this.dataIn);
    }
    
    public DataInputStream getDataIN(){
        return dataIn;
    }
    
    public DataOutputStream getDataOut(){
        return dataOut;
    }

    public static void main(String[] args) throws InterruptedException {
        LOG.warn("test");
        LOG.debug("TESTING DEBUG");
        LOG.error("ERROR");
    }

}
