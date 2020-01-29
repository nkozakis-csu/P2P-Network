package cs455.overlay;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {
	
	public static void main(String[] args) {
		Logger log = LogManager.getLogger(Server.class);
		log.warn("warn");
		log.debug("server debug");
	}

}
