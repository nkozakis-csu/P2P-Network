package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.Protocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

abstract class Node {
	
	private static final Logger LOG = LogManager.getLogger(Node.class);
	
	private int ID;
	protected String type = "node";
	private boolean terminate = false;
	private int sendTracker = 0;
	private int receiveTracker = 0;
	private int relayTracker = 0;
	private int port = 0;
	
	protected ArrayList<TCPConnection> consIN = new ArrayList<>();
	protected HashMap<Integer, TCPConnection> routingTable = new HashMap<Integer, TCPConnection>(); //Routing ID, connection to other node
	private int routingTableSize;
	
	public Node(){
		LOG.info("NODE STARTED");
	}
	
	protected void listenThread() {
		try {
			LOG.debug("listening");
			ServerSocket serverSocket = new ServerSocket(0);
			port = serverSocket.getLocalPort();
			while (!terminate) {
				Socket recvSocket = serverSocket.accept();
				LOG.info(String.format("Server accepted connection from: %s", recvSocket.getLocalAddress()));
				consIN.add(new TCPConnection(recvSocket, 0));
				Message.write(consIN.get(0).getDataOut(), new Message(Protocol.OVERLAY_NODE_SENDS_REGISTRATION, 10));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void forwardMessage(Message m, int id){
		while(routingTable.size() == 0){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Message.write(routingTable.get(0).getDataOut(), m);
	}
	
	private void recvThread(){
		while (!terminate){
			if(this.consIN.size()>0){
				LOG.info("RECEIVED: "+consIN.get(0).recvAnyMessages().toString());
			}
			else{
				try{
					Thread.sleep(50);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
	}
	public int getPort(){
		return port;
	}
	
	public void start(){
		Thread listener = new Thread(this::listenThread);
		listener.start();
		Thread receiver = new Thread(this::recvThread);
		receiver.start();
	}
	
}
