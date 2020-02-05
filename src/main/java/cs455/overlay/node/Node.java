package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.Protocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

abstract class Node implements Runnable {
	
	private static final Logger LOG = LogManager.getLogger(Node.class);
	
	protected int ID;
	protected String type = "node";
	private boolean terminate = false;
	private int sendTracker = 0;
	private int receiveTracker = 0;
	private int relayTracker = 0;
	private int port = 0;
	protected BlockingQueue<Message> recvQueue;
	
	protected ArrayList<TCPConnection> consIN = new ArrayList<>();
	protected HashMap<Integer, TCPConnection> routingTable = new HashMap<Integer, TCPConnection>(); //Routing ID, connection to other node
	private int routingTableSize;
	
	public Node(int id){
		this.ID = id;
		this.recvQueue = new LinkedBlockingQueue<>();
	}
	
	protected void listenThread() {
		try {
			LOG.debug(this.ID+": listening");
			ServerSocket serverSocket = new ServerSocket(0);
			port = serverSocket.getLocalPort();
			synchronized (this){
				notifyAll();
			}
			LOG.info(this.ID+": setting port="+port);
			while (!terminate) {
				Socket recvSocket = serverSocket.accept();
				LOG.info(String.format(this.ID + ": Server accepted connection from: %s", recvSocket.getLocalAddress()));
				consIN.add(new TCPConnection(recvSocket, 0, recvQueue));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void send(int index, Message m){
		routingTable.get(index).send(m);
	}
	
	public void forwardMessage(Message m, int id){
		while(routingTable.size() == 0){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		routingTable.get(0).send(m);
	}
	
	private void recvThread(){
		while (!terminate){
			Message message = null;
			try {
				message = recvQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (message != null)
				LOG.info(this.ID + " RECEIVED: " +message.toString());
		}
	}
	
	public synchronized int getPort() throws InterruptedException {
		while (this.port == 0) {
			LOG.debug(this.ID+" wait because port=0");
//			Thread.sleep(200);
			wait();
		}
		return port;
	}
	
	public void run(){
		LOG.info(this.type +":"+this.ID+": node started");
		Thread listener = new Thread(this::listenThread);
		listener.start();
		Thread receiver = new Thread(this::recvThread);
		receiver.start();
	}
	
	public void start(){
		Thread t = new Thread(this);
		LOG.info("Starting node "+this.ID+" thread");
		t.start();
	}
	
}
