package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

abstract class Node{
	
	private static final Logger LOG = LogManager.getLogger(Node.class);
	
	protected int ID;
	protected String type = "node";
	protected boolean terminate = false;
	protected int port = 0;
	protected BlockingQueue<Message> recvQueue;
	protected LinkedList<Integer> assignedIDs;
	protected Thread listener;
	protected Thread receiver;

	protected ArrayList<TCPConnection> consIN = new ArrayList<>();
	
	public Node(){
		this.ID = -1;
		this.recvQueue = new LinkedBlockingQueue<>();
		this.assignedIDs = new LinkedList<>();
	}
	
	protected void listenThread(){
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			port = serverSocket.getLocalPort();
			synchronized (this){
				notifyAll();
			}
			LOG.info(this.type+": setting port="+port);
			while (!terminate) {
				Socket recvSocket = serverSocket.accept();
				LOG.debug(String.format(this.type + ": Server accepted connection from: %s", recvSocket.getLocalAddress()));
				consIN.add(new TCPConnection(recvSocket, 0, recvQueue));
			}
			System.out.println("ending Listen Thread");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	public void end(){
		this.terminate = true;
		this.listener.interrupt();
		this.receiver.interrupt();
		System.exit(0);
	}

	public void run(){
		LOG.info(this.type +":"+this.ID+": node started");
		listener = new Thread(this::listenThread);
		listener.start();
		receiver = new Thread(this::recvThread);
		receiver.start();
	}
	
}
