package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class Registry extends Node implements Runnable{
	
	private static final Logger LOG = LogManager.getLogger(Registry.class);
	
	private HashMap<Integer, ConnectedNode> nodeMap;
	int numNodes = 0;
	
	public Registry(int id) {
		super(id);
		LOG.info("Registry Created with ID "+this.ID);
		type="Registry";
		nodeMap = new HashMap<>();
	}
	
	private void registerNode(OverlayNodeSendsRegistration o){
		nodeMap.put(numNodes, new ConnectedNode(numNodes, o.getSource(), o.ip, o.port));
		numNodes++;
		LOG.info("registry added node id: "+nodeMap.get(numNodes-1).ID+" with address "+o.ip+":"+o.port);
	}
	
	@Override
	void handleMessages(Message m) {
		switch(m.getProtocol()) {
			case OVERLAY_NODE_SENDS_REGISTRATION:
				registerNode((OverlayNodeSendsRegistration) m);
				break;
			case NODE_REPORTS_OVERLAY_SETUP_STATUS:
		}
	}
	
}
