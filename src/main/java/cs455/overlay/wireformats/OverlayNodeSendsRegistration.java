package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeSendsRegistration extends Message {
	
	String ip;
	int port;
	
	public OverlayNodeSendsRegistration(String ip, int port) {
		super(Protocol.OVERLAY_NODE_SENDS_REGISTRATION);
		try {
			dout.writeInt(ip.length());
			dout.writeBytes(ip);
			dout.writeInt(port);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public OverlayNodeSendsRegistration(byte[] b){
		super(Protocol.OVERLAY_NODE_SENDS_REGISTRATION);
		
		
	}
}
