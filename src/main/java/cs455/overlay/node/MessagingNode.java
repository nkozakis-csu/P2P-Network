package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.Protocol;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.Socket;

public class MessagingNode extends Node {
	
	private static final Logger LOG = LogManager.getLogger(MessagingNode.class);
	
	private TCPConnection registrySock;
	
	public MessagingNode(){
		super();
		this.type="Messaging Node";
	}
	
	public void connectToMessagingNode(String ip, int port, int id, int distance) {
		try {
			Socket socket = new Socket(ip, port);
			routingTable.put(id, new TCPConnection(socket, distance));
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void connectToRegistry(String ip, int port){
		try {
			Socket socket = new Socket(ip, port);
			registrySock = new TCPConnection(socket, 0);
//			bytes[] registerMessage = Message(Protocols.register());
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		MessagingNode a = new MessagingNode();
		MessagingNode b = new MessagingNode();
		a.start();
		b.start();
		while(a.getPort() == 0){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		int portA = a.getPort();
		LOG.info("port="+portA);
		b.connectToMessagingNode("localhost", portA, 0, 0);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		b.forwardMessage(new Message(Protocol.OVERLAY_NODE_SENDS_REGISTRATION, 10), 0);
	}
}
