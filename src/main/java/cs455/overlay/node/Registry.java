package cs455.overlay.node;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Registry extends Node implements Runnable{
	
	private static final Logger LOG = LogManager.getLogger(Registry.class);
	
	private ConnectedNode[] nodeMap;
	private int numNodes = 0;
	private int numRoutingEntries;
	
	public Registry(int port) {
		super();
		this.port = port;
		LOG.info("Registry Created with ID "+this.ID);
		type="Registry";
		nodeMap = new ConnectedNode[128];
	}
	
	private void registerNode(OverlayNodeSendsRegistration o){
		nodeMap[numNodes]= new ConnectedNode(numNodes, o.getSource(), o.ip, o.port);
		LOG.info("registry added node id: "+nodeMap[numNodes].ID+" with address "+o.ip+":"+o.port);
		o.getSource().send(new RegistryReportsRegistrationStatus(numNodes, numNodes+1));
		numNodes++;
	}
	
	@Override
	void handleMessages(Message m) {
		LOG.debug(this.type+" handling message: "+m.toString());
		switch(m.getProtocol()) {
			case OVERLAY_NODE_SENDS_REGISTRATION:
				registerNode((OverlayNodeSendsRegistration) m);
				break;
			case NODE_REPORTS_OVERLAY_SETUP_STATUS:
                NodeReportsOverlaySetupStatus status = (NodeReportsOverlaySetupStatus) m;
		}
	}
	@Override
	public void run(){
		super.run();
		
	
	}
	
	public void start(){
		Thread t = new Thread(this);
		t.start();
	}
	
	public void setupOverlay(int numRoutingEntries){
		this.numRoutingEntries = numRoutingEntries;
		LOG.debug("SETUP OVERLAY");
		for(int i=0; i<numNodes; i++){
			ConnectedNode node = nodeMap[i];
			RoutingTable rt = new RoutingTable();
			for(int j=0; j<numRoutingEntries; j++){
				int routeID = (int) ((i+Math.pow(2,j))%numNodes);
				RoutingEntry entry = new RoutingEntry(routeID, (int) Math.pow(2,j), nodeMap[routeID].ip, nodeMap[routeID].port);
				LOG.debug(entry.toString());
				rt.add(entry);
			}
			LOG.debug(rt.toString());
			node.routingTable = rt;
			node.con.send(new RegistrySendsNodeManifest(rt));
		}
	}
	
	public void listMessagingNodes(){
		System.out.println("Entries: ");
		for(int i=0; i<numNodes; i++){
			System.out.print(nodeMap[i].ID+",");
		}
		System.out.println("");
	}

	public void listRoutingTables(){

	}

	public void startNetwork(){

	}
	
	
	public static void main(String[] args) {
		int port = Integer.parseInt(args[0]);
		Registry registry = new Registry(port);
		registry.start();
		Scanner scanner = new Scanner(System.in);
		String command = scanner.nextLine();
		while (!command.equals("exit")){
			if (command.startsWith("setup-overlay")){
				String[] info = command.split(" ");
				if (info.length>1){
					LOG.info(Arrays.toString(info));
					int numRoutingEntries = Integer.parseInt(info[1]);
					registry.setupOverlay(numRoutingEntries);
				}else{
					System.out.println("Must add number of routing table entries after setup-overlay");
				}
			}else if (command.equals("list-messaging-nodes")){
				registry.listMessagingNodes();
			}else if(command.equals("list-routing-tables")){
				registry.listRoutingTables();
			}else if(command.startsWith("start")){
				registry.startNetwork();
			}else{
				System.out.println("Unknown command");
			}
			
			command = scanner.nextLine();
		}
		
	}
	
}
