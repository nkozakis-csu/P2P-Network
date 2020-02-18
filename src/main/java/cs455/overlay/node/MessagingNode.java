package cs455.overlay.node;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class MessagingNode extends Node implements Runnable {
	
	private static final Logger LOG = LogManager.getLogger(MessagingNode.class);
	private TCPConnection registrySock;
	private String registryIP;
	private int registryPort;
	protected RoutingTable routingTable;
	private int numSent;
	private int numReceived;
	private int numForwarded;
	private long sumReceived;
	private long sumSent;
	
	public MessagingNode(String registryIP, int registryPort){
		super();
		this.type="Messaging Node";
		LOG.info(type+":"+this.ID+": messagingNode created");
		this.registryIP = registryIP;
		this.registryPort = registryPort;
		this.numReceived = 0;
		this.numSent = 0;
		this.numForwarded = 0;
		this.sumReceived = 0;
		this.sumSent = 0;
	}
	
	@Override
	void handleMessages(Message m) {
		switch(m.getProtocol()) {
			case REGISTRY_REPORTS_REGISTRATION_STATUS:
				RegistryReportsRegistrationStatus status = (RegistryReportsRegistrationStatus) m;
				this.ID = status.id;
				System.out.println(status.msg);
				break;
			case REGISTRY_SENDS_NODE_MANIFEST:
				RegistrySendsNodeManifest manifest = (RegistrySendsNodeManifest) m;
				this.assignedIDs = manifest.assignedIDs;
				this.assignedIDs.remove(Integer.valueOf(this.ID));
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
				if (data.destination == this.ID){
					this.numReceived++;
					this.sumReceived += data.payload;
				}else {
					this.numForwarded++;
					sendData(data);
				}
				break;
			case REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
				registrySock.send(new OverlayNodeReportsTrafficSummary(this.ID,this.numSent, this.numForwarded, this.sumSent, this.numReceived, this.sumReceived));
				break;
		}
		LOG.debug(this.ID+": received message: "+m.toString());
	}
	
	public void sendData(OverlayNodeSendsData m){
		int sendID;
		LOG.debug(this.ID+" end destination= "+m.destination);
		ArrayList<Integer> keys = routingTable.getKeys();
		if (keys.contains(m.destination)){
			sendID = m.destination;
		}else{
			sendID = this.ID;
			for (Integer dest : routingTable.getKeys()) {
				if (m.destination > dest) {
					sendID = dest;
				} else {
					break;
				}
			}
			if (sendID == this.ID){
				sendID = keys.get(keys.size()-1);
			}
		}
		LOG.debug(this.ID+" sending directly to "+sendID);
		routingTable.get(sendID).con.send(m);
	}
	
	public void initiateTask(int numMessages){
		LOG.info(this.type+":"+this.ID+": SENDING "+numMessages+" messages");
		Random rand = new Random();
		int destination;
		while(numMessages > 0){
			destination = assignedIDs.get(rand.nextInt(assignedIDs.size()));
			numMessages--;
			OverlayNodeSendsData m = new OverlayNodeSendsData(this.ID, destination);
			sendData(m);
			this.sumSent+=m.payload;
			this.numSent++;
		}
		registrySock.send(new OverlayNodeReportsTaskFinished(this.listenAddress, this.listenPort, this.ID));
		LOG.info("Finished Task");
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
			registrySock.send(new OverlayNodeSendsRegistration(this.listenAddress, this.getPort()));
			LOG.info(this.ID+": sending registration");
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void run(){
		super.run();
		this.connectToRegistry(this.registryIP, this.registryPort);
	}
	
	public void start(){
		Thread t = new Thread(this);
		t.start();
	}

	public void printDiagnostics(){
		System.out.println("ID: "+this.ID+"\nNum sent: "+this.numSent+"\nNum Received: "+this.numReceived+"\nNum Relayed: "+this.numForwarded);
	}

	public void exitOverlay(){
		super.end();
		for(Map.Entry<Integer, RoutingEntry> entry: routingTable.getEntrySet()){
			entry.getValue().con.end();
		}
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
				System.out.println("\n----Commands----\nprint-counters-and-diagnostic\nexit-overlay");
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
