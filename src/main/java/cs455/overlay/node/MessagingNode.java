package cs455.overlay.node;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistrySendsNodeManifest;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class MessagingNode extends Node implements Runnable {
	
	private static final Logger LOG = LogManager.getLogger(MessagingNode.class);
	private TCPConnection registrySock;
	
	public MessagingNode(int id){
		super(id);
		this.type="Messaging Node";
		LOG.info(type+":"+this.ID+": messagingNode created");
	}
	
	@Override
	void handleMessages(Message m) {
		switch(m.getProtocol()){
			case REGISTRY_SENDS_NODE_MANIFEST:
				RegistrySendsNodeManifest manifest = (RegistrySendsNodeManifest) m;
				routingTable = manifest.getRoutingTable();
				this.connectToMessagingNodes();
		}
		LOG.debug(this.ID+": received message: "+m.toString());
	}
	
	public void connectToMessagingNodes() {
		try {
			for(Map.Entry<Integer, RoutingEntry> entry: routingTable.getEntrySet()) {
				RoutingEntry re = entry.getValue();
				Socket socket = new Socket(re.ip, re.port);
				TCPConnection con = new TCPConnection(socket, re.distance, recvQueue);
				re.addTCPConnection(con);
			}
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
