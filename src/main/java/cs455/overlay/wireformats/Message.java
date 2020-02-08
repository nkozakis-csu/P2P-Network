package cs455.overlay.wireformats;

import cs455.overlay.transport.TCPConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Message {
	
	private static Logger LOG = LogManager.getLogger(Message.class);
	
	protected Protocol protocol;
	protected byte[] data;
	protected ByteArrayOutputStream bout;
	protected DataOutputStream dout; //for writing to data array
	protected ByteArrayInputStream bin;
	protected DataInputStream din; // for reading from transmitted bytearray
	
	protected TCPConnection source;
	
	public Message(){
		bout = new ByteArrayOutputStream();
		dout = new DataOutputStream(new BufferedOutputStream(bout));
	}
	
	public Message(Protocol protocol){
		this();
		this.protocol = protocol;
	}
	
	public Message(Protocol p, byte[] b){
		this();
		bin = new ByteArrayInputStream(b);
		din = new DataInputStream(new BufferedInputStream(bin));
		this.data = b;
		this.protocol = p;
	}
	
	public static Message getMessage(DataInputStream in) throws Exception{
		Protocol p = Protocol.getProtocol(in.readByte());
		int size = in.readInt();
		byte[] msg = new byte[size];
		in.readFully(msg, 0, size);
		switch(p){
			case OVERLAY_NODE_SENDS_REGISTRATION:
				return new OverlayNodeSendsRegistration(msg);
			case OVERLAY_NODE_SENDS_DEREGISTRATION:
				return null;
			default:
				throw new Exception("Unknown packet protocol received : "+p);
		}
	}
	
	public String toString(){
		return "Protocol: "+protocol.toString()+", Message: "+ Arrays.toString(this.getBytes());
	}
	
	public byte[] getBytes(){
		ByteBuffer packer = ByteBuffer.allocate(5+this.data.length); // 5 = protocol (1 byte) + data.length ( 4 byte integer)
		packer.put(this.protocol.getID());
		packer.putInt(this.data.length);
		packer.put(this.data);
		return packer.array();
	}
	
	public Protocol getProtocol() {
		return protocol;
	}
	
	public void setSource(TCPConnection con) {
		source = con;
	}
	
	public TCPConnection getSource(){
		return source;
	}
	
	public static void main(String[] args) {

		LOG.info("test");
		OverlayNodeSendsRegistration m = new OverlayNodeSendsRegistration("localhost", 50000);
		System.out.println(new String(m.getBytes()));
		DataInputStream d = new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(m.getBytes())));
		try {
			Message received = Message.getMessage(d);
			if (received != null){
				System.out.println(received.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
