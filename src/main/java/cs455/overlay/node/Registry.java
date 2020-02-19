package cs455.overlay.node;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.wireformats.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

public class Registry extends Node implements Runnable{
	
	private static final Logger LOG = LogManager.getLogger(Registry.class);
	
	private ConnectedNode[] registeredNodes;
	private LinkedList<Integer> freeIDs;
	private int numReady = 0;
	private int numFinished = 0;
	private int numSummaries = 0;
	private int totalSent = 0;
	private int totalReceived = 0;
	private int totalForwarded = 0;
	private long totalSumSent = 0;
	private long totalSumReceived = 0;
	
	public Registry(int port) {
		super();
		System.out.println("Registry Created");
		this.listenPort = port;
		type="Registry";
		registeredNodes = new ConnectedNode[128];
		freeIDs = new LinkedList<>();
		for (int i = 0; i < 128; i++) {
			freeIDs.add(i);
		}
		Collections.shuffle(freeIDs);
	}
	
	private void registerNode(OverlayNodeSendsRegistration o){
		boolean fail = false;
//		System.out.println(o.getSource().getSourceIP()+ "__"+o.ip);
//		if (!(o.getSource().getSourceIP().equals(o.ip))){
//			System.out.println("Node IP mismatch error: "+o.getSource().getSourceIP()+" != "+o.ip);
//			fail = true;
//		}else{
		for(int i : assignedIDs){
			if( (registeredNodes[i].ip.equals(o.ip) && registeredNodes[i].port == o.port)){
				fail = true;
				System.out.println("Node already registered error: address="+o.ip+":"+o.port);
				o.getSource().end();
			}
		}
//		}
		if (!fail) {
			int id = freeIDs.remove(0);
			this.addSortedID(id);
			ConnectedNode conNode = new ConnectedNode(id, o.getSource(), o.ip, o.port);
			registeredNodes[id] = conNode;
			System.out.println("Registry added node id: " + conNode.ID + " with address " + o.ip + ":" + o.port);
			conNode.con.send(new RegistryReportsRegistrationStatus(id, assignedIDs.size()));
		}
	}

	public void addSortedID(int id){
		if (assignedIDs.size() ==0){
			assignedIDs.add(id);
		} else if ( assignedIDs.get(assignedIDs.size()-1) < id){
			assignedIDs.add(id);
		}else {
			for (int i = 0; i < assignedIDs.size(); i++) {
				if (assignedIDs.get(i) > id) {
					assignedIDs.add(i, id);
					break;
				}
			}
		}
	}

	private void deregisterNode(OverlayNodeSendsDeregistration m){
		for(int id: assignedIDs){
			if(registeredNodes[id].port == m.port && registeredNodes[id].ip.equals(m.ip)){
				this.freeIDs.addFirst(id);
				this.assignedIDs.remove(new Integer(id));
				this.registeredNodes[id] = null;
				System.out.println("deregistered node: "+id+" from ip: "+m.ip + ", port:"+m.port);
				break;
			}
		}
		m.getSource().send(new RegistryReportsDeregistrationStatus(true));

	}
	
	@Override
	void handleMessages(Message m) {
		LOG.debug(this.type+" handling message: "+m.toString());
		switch(m.getProtocol()) {
			case OVERLAY_NODE_SENDS_REGISTRATION:
				registerNode((OverlayNodeSendsRegistration) m);
				break;
			case OVERLAY_NODE_SENDS_DEREGISTRATION:
				deregisterNode((OverlayNodeSendsDeregistration) m);
				break;
			case NODE_REPORTS_OVERLAY_SETUP_STATUS:
                NodeReportsOverlaySetupStatus nodeStatus = (NodeReportsOverlaySetupStatus) m;
                boolean status = nodeStatus.getStatus();
                int id = nodeStatus.getId();
                if(!status){
					System.out.println("Node: "+id+" failed to setup overlay");
					//todo: something? remove node?
					freeIDs.addFirst(id);
					assignedIDs.remove(new Integer(id));
					registeredNodes[id] = null;
				}
                else{
                	registeredNodes[id].status = true;
                	numReady++;
                	if(numReady==assignedIDs.size())
                		System.out.println("All Messaging Nodes are setup. Overlay is ready to start");
				}
                break;
			case OVERLAY_NODE_REPORTS_TASK_FINISHED:
				OverlayNodeReportsTaskFinished finished = (OverlayNodeReportsTaskFinished) m;
				numFinished++;
				if (numFinished >= assignedIDs.size()){
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for(int i : assignedIDs){
						registeredNodes[i].con.send(new RegistryRequestsTrafficSummary());
					}
				}
				break;
			case OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
				OverlayNodeReportsTrafficSummary summary = (OverlayNodeReportsTrafficSummary) m;
				registeredNodes[summary.id].summary = summary;
				this.totalSent += summary.numSent;
				this.totalReceived += summary.numReceived;
				this.totalForwarded += summary.numForwarded;
				this.totalSumReceived += summary.sumReceived;
				this.totalSumSent += summary.sumSent;
				if (numSummaries == 0){
					System.out.println("Node ID  | sent | received | forwarded |     Sum Sent     |     Sum Received");
				}
				numSummaries++;
				System.out.println(summary.toString());
				if (numSummaries == assignedIDs.size()){
					System.out.println(String.format("\nTotal    | %4d | %8d | %9d | %16d | %16d", totalSent, totalReceived, totalForwarded, totalSumSent, totalSumReceived));
					this.totalSent =0;
					this.totalReceived =0;
					totalForwarded = 0;
					totalSumReceived = 0;
					totalSumSent = 0;
				}
				break;
			default:
				LOG.error("UNKNOWN PROTOCOL:"+ m.getProtocol());
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
		for (int i: assignedIDs) {
			System.out.printf("Node %3d | Listening on %s:%d\n", i, registeredNodes[i].ip, registeredNodes[i].port);
		}
		System.out.println("");
	}

	public void listRoutingTables(){
		System.out.println("Routing Tables");
		for(int i : assignedIDs){
			System.out.println("\nNode "+i);
			if (registeredNodes[i].routingTable == null){
				System.out.println("Routing tables not setup yet");
				break;
			}
			System.out.println(registeredNodes[i].routingTable.toString());
		}
		System.out.println("");

	}

	public void startNetwork(int numMessages){
		numSummaries = 0;
		numFinished = 0;
		RegistryRequestsTaskInitiate initiateCommand = new RegistryRequestsTaskInitiate(numMessages);
		for (int i : assignedIDs) {
			registeredNodes[i].con.send(initiateCommand);
		}

	}
	
	
	public static void main(String[] args) {
		int port = Integer.parseInt(args[0]);
		Registry registry = new Registry(port);
		registry.start();
		Scanner scanner = new Scanner(System.in);
		String command = scanner.nextLine();
		boolean setup = false;
		while (!command.equals("exit")){
			if (command.startsWith("setup-overlay")){
				String[] info = command.split(" ");
				if (info.length>1){
					int numRoutingEntries = Integer.parseInt(info[1]);
					registry.setupOverlay(numRoutingEntries);
				}else{
					registry.setupOverlay(3);
				}
				setup=true;
			}else if (command.equals("list-messaging-nodes")){
				registry.listMessagingNodes();
			}else if(command.equals("list-routing-tables")){
				registry.listRoutingTables();
			}else if(command.startsWith("start")){
				if (setup) {
					registry.listener.interrupt();
					String[] info = command.split(" ");
					if (info.length > 1) {
						registry.startNetwork(Integer.parseInt(info[1]));
					} else {
						registry.startNetwork(10);
					}
					System.out.println("Waiting 60 seconds before retrieving traffic summaries");
				} else {
					System.out.println("Cannot start before overlay is setup");
				}
			}else{
				System.out.println("\n----Commands----\nsetup-overlay [Routing Table Size]\nlist-messaging-nodes\nlist-routing-tables\nstart\n");
			}
			command = scanner.nextLine();
		}
		registry.end();

	}
	
}
