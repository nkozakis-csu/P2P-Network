package cs455.overlay.node;

import cs455.overlay.routing.RoutingTable;
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

abstract class Node{
	
	private static final Logger LOG = LogManager.getLogger(Node.class);
	
	protected int ID;
	protected String type = "node";
	protected boolean terminate = false;
	private int sendTracker = 0;
	private int receiveTracker = 0;
	private int relayTracker = 0;
	protected int port = 0;
	protected BlockingQueue<Message> recvQueue;
	
	protected ArrayList<TCPConnection> consIN = new ArrayList<>();
	protected RoutingTable routingTable;
	
	public Node(){
		this.ID = -1;
		this.recvQueue = new LinkedBlockingQueue<>();
	}
	
	protected void listenThread(){
		try {
			LOG.debug(this.type+": listening");
			ServerSocket serverSocket = new ServerSocket(port);
			port = serverSocket.getLocalPort();
			synchronized (this){
				notifyAll();
			}
			LOG.info(this.type+": setting port="+port);
			while (!terminate) {
				Socket recvSocket = serverSocket.accept();
				LOG.info(String.format(this.type + ": Server accepted connection from: %s", recvSocket.getLocalAddress()));
				consIN.add(new TCPConnection(recvSocket, 0, recvQueue));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void sendData(int index, Message m){
		routingTable.get(index).con.send(m);
	}
	
	public void forwardMessage(Message m, int id){
		while(routingTable.size() == 0){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		routingTable.get(0).con.send(m);
	}
	
	abstract void handleMessages(Message m);
	
	private void recvThread(){
		Message message;
		while (!terminate){
			 message = null;
			try {
				message = recvQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (message != null)
				this.handleMessages(message);
		}
	}
	
	public synchronized int getPort() throws InterruptedException {
		while (this.port == 0) {
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
	
}
