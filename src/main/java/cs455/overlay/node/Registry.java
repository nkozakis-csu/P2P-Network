package cs455.overlay.node;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Registry extends Node implements Runnable{
	
	private static final Logger LOG = LogManager.getLogger(Registry.class);
	
	public Registry(int id) {
		super(id);
		LOG.debug("Registry Created");
		type="Registry";
	}
}
