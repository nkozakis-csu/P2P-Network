package cs455.overlay.node;

import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

import javax.print.attribute.standard.Destination;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.*;

public class MessagingNode extends Node implements Runnable {
	
	private static final Logger LOG = LogManager.getLogger(MessagingNode.class);
	private TCPConnection registrySock;
	private String ip;
	private int port;
	private int numNodes;
	
	public MessagingNode(String ip, int port){
		super();
		this.type="Messaging Node";
		LOG.info(type+":"+this.ID+": messagingNode created");
		this.ip = ip;
		this.port = port;
	}
	
	@Override
	void handleMessages(Message m) {
		switch(m.getProtocol()){
			case REGISTRY_REPORTS_REGISTRATION_STATUS:
				RegistryReportsRegistrationStatus status = (RegistryReportsRegistrationStatus) m;
				numNodes = status.numNodes;
				this.ID = status.id;
				System.out.println(status.msg);
				break;
			case REGISTRY_SENDS_NODE_MANIFEST:
				RegistrySendsNodeManifest manifest = (RegistrySendsNodeManifest) m;
				routingTable = manifest.getRoutingTable();
				this.connectToMessagingNodes();
				break;
			case REGISTRY_REQUESTS_TASK_INITIATE:
				RegistryRequestsTaskInitiate task = (RegistryRequestsTaskInitiate) m;
				int numMessages = task.getNumMessages();
				initiateTask(numMessages);
				break;
			case OVERLAY_NODE_SENDS_DATA:
				OverlayNodeSendsData data = (OverlayNodeSendsData) m;
				if(data.destination == this.ID) handleData(data);
				else sendData(data);
		}
		LOG.debug(this.ID+": received message: "+m.toString());
	}

	public void handleData(OverlayNodeSendsData d){
		LOG.info("RECEIVED DATA:", d.payload);
	}

	public void initiateTask(int numMessages){
		LOG.info(this.type+":"+this.ID+": SENDING "+numMessages+" messages");
		Random rand = new Random();
		int destination;
		while(numMessages > 0){
			destination = rand.nextInt(numNodes);
			if(destination!=this.ID){
				numMessages--;
				sendData(new OverlayNodeSendsData(this.ID, destination));
			}
		}
	}
	
	public void connectToMessagingNodes() {
		try {
			for(Map.Entry<Integer, RoutingEntry> entry: routingTable.getEntrySet()) {
				RoutingEntry re = entry.getValue();
				Socket socket = new Socket(re.ip, re.port);
				TCPConnection con = new TCPConnection(socket, re.distance, recvQueue);
				re.addTCPConnection(con);
			}
			registrySock.send(new NodeReportsOverlaySetupStatus(this.ID, true));
		} catch(IOException e){
			e.printStackTrace();
			registrySock.send(new NodeReportsOverlaySetupStatus(this.ID, false));
			//todo: terminate node?
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
	@Override
	public void run(){
		super.run();
		this.connectToRegistry(this.ip, this.port);
	}
	
	public void start(){
		Thread t = new Thread(this);
		t.start();
	}

	public void printDiagnostics(){
		System.out.println("ID: "+this.ID);
	}

	public void exitOverlay(){
		this.terminate = true;
	}
	
	public static void main(String[] args) throws Exception {
		String ip = args[0];
		int port = Integer.parseInt(args[1]);
		MessagingNode node = new MessagingNode(ip, port);
		node.start();
		Scanner scanner = new Scanner(System.in);
		String command = scanner.nextLine();
		while (!command.equals("exit-overlay")){
			if (command.equals("print-counters-and-diagnostic")){
				 node.printDiagnostics();
			}else{
				System.out.println("Unknown command");
			}
			
			command = scanner.nextLine();
		}
		node.exitOverlay();
		
//		LOG.debug("STARTING");
//		System.out.println("TEST");
//		Registry r = new Registry();
//		r.start();
//		int port = r.getPort();
//		LOG.debug("PORT="+port);
//		MessagingNode a = new MessagingNode();
//		MessagingNode b = new MessagingNode();
//		a.start();
//		b.start();
//		a.connectToRegistry("localhost", port);
//		b.connectToRegistry("localhost", port);
		
	}
}
