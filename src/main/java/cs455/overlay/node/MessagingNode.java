package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

import java.io.IOException;
import java.net.Socket;

public class MessagingNode extends Node implements Runnable {
	
	private static final Logger LOG = LogManager.getLogger(MessagingNode.class);
	
	private TCPConnection registrySock;
	
	public MessagingNode(int id){
		super(id);
		this.type="Messaging Node";
		LOG.info(type+":"+this.ID+": messagingNode created");
	}
	
	public void connectToMessagingNode(String ip, int port, int id, int distance) {
		try {
			Socket socket = new Socket(ip, port);
			routingTable.put(id, new TCPConnection(socket, distance, recvQueue));
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void connectToRegistry(String ip, int port){
		try {
			Socket socket = new Socket(ip, port);
			registrySock = new TCPConnection(socket, 0, recvQueue);
			registrySock.send(new OverlayNodeSendsRegistration("localhost", this.getPort()));
			LOG.info(this.ID+": sending registration");
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		LOG.debug("STARTING");
		System.out.println("TEST");
		Registry r = new Registry(666);
		r.start();
		int port = r.getPort();
		LOG.debug("PORT="+port);
		MessagingNode a = new MessagingNode(0);
		MessagingNode b = new MessagingNode(1);
		a.start();
		b.start();
		a.connectToRegistry("localhost", port);
		b.connectToRegistry("localhost", port);
		
	}
}
