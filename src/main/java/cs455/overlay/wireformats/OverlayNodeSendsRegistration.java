package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsRegistration extends Message {
	
	public String ip;
	public int port;
	
	public OverlayNodeSendsRegistration(String ip, int port) {
		super(Protocol.OVERLAY_NODE_SENDS_REGISTRATION);
		try {
			dout.writeInt(ip.length());
			dout.write(ip.getBytes());
			dout.writeInt(port);
			dout.flush();
			this.data = bout.toByteArray();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public OverlayNodeSendsRegistration(byte[] b){
		super(Protocol.OVERLAY_NODE_SENDS_REGISTRATION, b);
		try {
			int ipLength = din.readInt();
			byte[] ipBytes = new byte[ipLength];
			din.readFully(ipBytes, 0, ipLength);
			ip = new String(ipBytes);
			port = din.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "OverlayNodeSendsRegistration{" +
				"ip='" + ip + '\'' +
				", port=" + port +
				'}';
	}
}
