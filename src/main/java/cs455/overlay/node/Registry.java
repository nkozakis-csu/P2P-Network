package cs455.overlay.node;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.*;
import jdk.internal.jline.internal.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Registry extends Node implements Runnable{
	
	private static final Logger LOG = LogManager.getLogger(Registry.class);
	
	private ArrayList<ConnectedNode> registeredNodes;
	private ArrayDeque<Integer> freeIDs;
	private int numNodes = 0;
	private int numReady = 0;
	private int numRoutingEntries;
	
	public Registry(int port) {
		super();
		this.port = port;
		LOG.info("Registry Created with ID "+this.ID);
		type="Registry";
		registeredNodes = new ArrayList<>();
		freeIDs = new ArrayDeque<>();
		for (int i = 0; i < 128; i++) {
			freeIDs.add(i);
		}
	}
	
	private void registerNode(OverlayNodeSendsRegistration o){
		int id = freeIDs.remove();
		ConnectedNode conNode = new ConnectedNode(id, o.getSource(), o.ip, o.port);
		registeredNodes.add(conNode);
		LOG.info("registry added node id: "+conNode.ID+" with address "+o.ip+":"+o.port);
		conNode.con.send(new RegistryReportsRegistrationStatus(id, registeredNodes.size()));
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
                NodeReportsOverlaySetupStatus nodeStatus = (NodeReportsOverlaySetupStatus) m;
                boolean status = nodeStatus.getStatus();
                int id = nodeStatus.getId();
                if(!status){
					LOG.warn("Node: "+id+" failed to setup overlay");
					//todo: something? remove node?
					freeIDs.addFirst(id);
				}
                else{
                	registeredNodes.get(id).status = true;
                	numReady++;
                	if(numReady==numNodes)
                		System.out.println("All Messaging Nodes are setup. Overlay is ready to start");
				}
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
		for(int i=0; i<registeredNodes.size(); i++){
			ConnectedNode node = registeredNodes.get(i);
			node.ID = i;
			RoutingTable rt = new RoutingTable();
			for(int j=0; j<numRoutingEntries; j++){
				int routeID = (int) ((i+Math.pow(2,j))%numNodes);
				RoutingEntry entry = new RoutingEntry(routeID, (int) Math.pow(2,j), registeredNodes.get(routeID).ip, registeredNodes.get(routeID).port);
				rt.add(entry);
			}
			LOG.debug(rt.toString());
			node.routingTable = rt;
			node.con.send(new RegistrySendsNodeManifest(rt, numNodes, node.ID));
		}
	}
	
	public void listMessagingNodes(){
		System.out.println("Entries: ");
		for (ConnectedNode node: registeredNodes) {
			System.out.print(node.ID+", ");
		}
		System.out.println("");
	}

	public void listRoutingTables(){

	}

	public void startNetwork(int numMessages){
		RegistryRequestsTaskInitiate initiateCommand = new RegistryRequestsTaskInitiate(numMessages);
		for (ConnectedNode node: registeredNodes) {
			node.con.send(initiateCommand);
		}

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
					int numRoutingEntries = Integer.parseInt(info[1]);
					registry.setupOverlay(numRoutingEntries);
				}else{
					registry.setupOverlay(3);
				}
			}else if (command.equals("list-messaging-nodes")){
				registry.listMessagingNodes();
			}else if(command.equals("list-routing-tables")){
				registry.listRoutingTables();
			}else if(command.startsWith("start")){
				String[] info = command.split(" ");
				if (info.length>1){
					registry.startNetwork(Integer.parseInt(info[1]));
				}else{
					registry.startNetwork(10);
				}
			}else{
				System.out.println("\n----Commands----\nsetup-overlay [Routing Table Size]\nlist-messaging-nodes\nlist-routing-tables\nstart\n");
			}
			command = scanner.nextLine();
		}
		
	}
	
}
