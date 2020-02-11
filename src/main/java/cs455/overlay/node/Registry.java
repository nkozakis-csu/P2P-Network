package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.NodeReportsOverlaySetupStatus;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Registry extends Node implements Runnable{
	
	private static final Logger LOG = LogManager.getLogger(Registry.class);
	
	private ConnectedNode[] nodeMap;
	private int numNodes = 0;
	
	public Registry(int port) {
		super();
		this.port = port;
		LOG.info("Registry Created with ID "+this.ID);
		type="Registry";
		nodeMap = new ConnectedNode[128];
	}
	
	private void registerNode(OverlayNodeSendsRegistration o){
		nodeMap[numNodes]= new ConnectedNode(numNodes, o.getSource(), o.ip, o.port);
		numNodes++;
		LOG.info("registry added node id: "+nodeMap[numNodes-1].ID+" with address "+o.ip+":"+o.port);
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
	
	public void setupOverlay(){
		LOG.debug("SETUP OVERLAY");
		for(int i=0; i<numNodes; i++){
			ConnectedNode node = nodeMap[i];
			
		}
	}
	
	public void listMessagingNodes(){
		System.out.println("Entries: ");
		for(int i=0; i<numNodes; i++){
			System.out.print(nodeMap[i].ID+",");
		}
		System.out.println("");
	}
	
	
	public static void main(String[] args) {
		int port = Integer.parseInt(args[0]);
		Registry registry = new Registry(port);
		registry.start();
		Scanner scanner = new Scanner(System.in);
		String command = scanner.nextLine();
		while (!command.equals("exit")){
			if (command.startsWith("setup-overlay")){
				registry.setupOverlay();
			}else if (command.equals("list-messaging-nodes")){
				registry.listMessagingNodes();
			}else if(command.equals("list-routing-tables")){
			
			}else if(command.startsWith("start")){
			
			}else{
				System.out.println("Unknown command");
			}
			
			command = scanner.nextLine();
		}
		
	}
	
}
