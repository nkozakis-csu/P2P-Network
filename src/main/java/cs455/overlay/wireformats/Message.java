package cs455.overlay.wireformats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Message {
	
	private static Logger log = LogManager.getLogger(Message.class);
	
	public Protocol protocol;
	public int message;
	
	public Message(Protocol protocol, int message){
		this.protocol = protocol;
		this.message = message;
	}
	
	public static void write(DataOutputStream out, Message m){
		try {
			out.writeInt(10);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Message read(DataInputStream in){
		try {
			return new Message(Protocol.OVERLAY_NODE_SENDS_REGISTRATION, in.readInt());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int getSourceID(){
		return 0;
	}
	
	public int getDestinationID() {
		return 0;
	}
}
