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
	
	public Message(){
		bout = new ByteArrayOutputStream();
		dout = new DataOutputStream(new BufferedOutputStream(bout));
	}
	
	public Message(Protocol protocol){
//		Message();
		this.protocol = protocol;
		try {
			dout.writeByte(protocol.getID());
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
	
	public Message(Protocol p, byte[] b){
		super();
		data = b;
		protocol = p;
	}
	
	public Message(byte[] b){
		super();
		bin = new ByteArrayInputStream(b);
		din = new DataInputStream(new BufferedInputStream(bin));
		try {
			this.protocol = Protocol.getProtocol(din.readByte());
			int size = din.readInt();
			this.data = new byte[size];
			din.readFully(data, 0, size);
			
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
		Message m = new Message();
		try {
			m.dout.writeInt(10);
			m.dout.writeInt(20);
			m.dout.flush();
			LOG.info(Arrays.toString(m.bout.toByteArray()));
		} catch (IOException e) {
			e.printStackTrace();
		}
//		LOG.info("test");
//		OverlayNodeSendsRegistration m = new OverlayNodeSendsRegistration("localhost", 50000);
//		System.out.println(new String(m.getBytes()));
//		Message received = new Message(m.getBytes());
//		System.out.println(received.toString());
		
	}
}
