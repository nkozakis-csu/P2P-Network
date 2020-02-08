package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;

public class ConnectedNode {
	
	public int ID;
	public TCPConnection con;
	public String ip;
	public int port;
	public boolean status = false;
	
	public ConnectedNode(int id, TCPConnection con){
		this.ID = id;
		this.con = con;
	}
	
	public ConnectedNode(int id, TCPConnection con, String ip, int port){
		this(id, con);
		this.ip = ip;
		this.port = port;
	}
}
