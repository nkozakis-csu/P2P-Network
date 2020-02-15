package cs455.overlay.node;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.wireformats.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Registry extends Node implements Runnable{
	
	private static final Logger LOG = LogManager.getLogger(Registry.class);
	
	private ConnectedNode[] registeredNodes;
	private LinkedList<Integer> freeIDs;
	private int numReady = 0;
	
	public Registry(int port) {
		super();
		LOG.info("Registry Created with ID "+this.ID);
		this.port = port;
		type="Registry";
		registeredNodes = new ConnectedNode[128];
		freeIDs = new LinkedList<>();
		for (int i = 0; i < 128; i++) {
			freeIDs.add(i);
		}
		Collections.shuffle(freeIDs);
	}
	
	private void registerNode(OverlayNodeSendsRegistration o){
		int id = freeIDs.remove(0);
		assignedIDs.add(id);
		ConnectedNode conNode = new ConnectedNode(id, o.getSource(), o.ip, o.port);
		registeredNodes[id]=conNode;
		LOG.info("registry added node id: "+conNode.ID+" with address "+o.ip+":"+o.port);
		conNode.con.send(new RegistryReportsRegistrationStatus(id, assignedIDs.size()));
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
					assignedIDs.remove(id);
					registeredNodes[id] = null;
				}
                else{
                	registeredNodes[id].status = true;
                	numReady++;
                	if(numReady==assignedIDs.size())
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
		for(int i=0; i<assignedIDs.size(); i++){
			ConnectedNode node = registeredNodes[assignedIDs.get(i)];
			RoutingTable rt = new RoutingTable();
			for(int j=0; j<numRoutingEntries; j++){
				int hops = (int) Math.pow(2,j);
				int routeID = assignedIDs.get((i+hops)%assignedIDs.size());
				RoutingEntry entry = new RoutingEntry(routeID, hops, registeredNodes[routeID].ip, registeredNodes[routeID].port);
				rt.add(entry);
			}
			LOG.debug(rt.toString());
			node.routingTable = rt;
			node.con.send(new RegistrySendsNodeManifest(rt, assignedIDs));
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
