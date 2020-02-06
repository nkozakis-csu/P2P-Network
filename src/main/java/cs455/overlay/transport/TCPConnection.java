package cs455.overlay.transport;
import cs455.overlay.wireformats.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

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
    private BlockingQueue<Message> recvQueue;

    public TCPConnection(Socket socket, int distance, BlockingQueue<Message> queue) {
        this.sock = socket;
        this.distance = distance;
        this.recvQueue = queue;
        this.recvThread = new Thread(this::recvPopper);
        try {
            this.dataIn = new DataInputStream(sock.getInputStream());
            this.dataOut = new DataOutputStream(sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.recvThread.start();
    }
    
    public void send(Message m){
        try {
            LOG.debug("sending: "+new String(m.getBytes()));
            dataOut.write(m.getBytes());
            dataOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void recvPopper(){
        while (!terminate) {
            try {
                Message m = Message.getMessage(dataIn);
                if (m != null){
                    m.setSource(this);
                    recvQueue.add(m);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
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
