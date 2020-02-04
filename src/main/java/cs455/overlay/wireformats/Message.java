package cs455.overlay.wireformats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Arrays;

public class Message {
	
	private static Logger LOG = LogManager.getLogger(Message.class);
	
	private Protocol protocol;
	private byte[] data;
	private Object subclass;
	protected ByteArrayOutputStream bout;
	protected DataOutputStream dout;
	protected ByteArrayInputStream bin;
	protected DataInputStream din;
	
	public Message(Protocol protocol){
		this.protocol = protocol;
		try {
			bout = new ByteArrayOutputStream();
			dout = new DataOutputStream(new BufferedOutputStream(bout));
			dout.writeByte(protocol.getID());
//			dout.writeLong(System.currentTimeMillis());
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Message getMessage(DataInputStream din) throws Exception{
		Protocol p = Protocol.getProtocol(din.readByte());
		int size = din.readInt();
		byte[] msg = new byte[size];
		din.readFully(msg, 0, size);
		switch(p){
			case OVERLAY_NODE_SENDS_REGISTRATION:
				return new OverlayNodeSendsRegistration(msg);
			case OVERLAY_NODE_SENDS_DEREGISTRATION:
				return null;
			default:
				throw new Exception("Unknown packet protocol received : "+p);
		}
	}
	
	public Message(byte[] b){
		bout = new ByteArrayOutputStream();
		dout = new DataOutputStream(new BufferedOutputStream(bout));
		bin = new ByteArrayInputStream(b);
		din = new DataInputStream(new BufferedInputStream(bin));
		try {
			byte p = din.readByte();
			this.protocol = Protocol.getProtocol(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public String toString(){
		return "Protocol: "+protocol.toString()+", Message: "+ Arrays.toString(this.getBytes());
	}
	
	public byte[] getBytes(){
		return bout.toByteArray();
	}
	
	public static void main(String[] args) {
		LOG.info("test");
		OverlayNodeSendsRegistration m = new OverlayNodeSendsRegistration("localhost", 50000);
		System.out.println(new String(m.getBytes()));
		Message received = new Message(m.getBytes());
		System.out.println(received.toString());
		
	}
}
