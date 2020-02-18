package cs455.overlay.wireformats;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class OverlayNodeSendsDeregistration extends Message {
	public String ip;
	public int port;
	
	public OverlayNodeSendsDeregistration(String ip, int port){
		super(Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION);
		this.ip = ip;
		this.port = port;
		try {
			dout.writeInt(ip.length());
			dout.writeBytes(ip);
			dout.writeInt(port);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public OverlayNodeSendsDeregistration(byte[] b){
		super(Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION, b);
		try {
			int size = din.readInt();
			byte[] ipbytes = new byte[size];
			din.readFully(ipbytes,0,size);
			this.port = din.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
