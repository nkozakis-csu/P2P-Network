package cs455.overlay.wireformats;

import java.io.IOException;

public class OverlayNodeReportsTaskFinished extends Message{
	
	public String ip;
	public int port;
	public int id;
	
	public OverlayNodeReportsTaskFinished(String ip, int port, int ID){
		super(Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED);
		this.ip = ip;
		this.port = port;
		this.id = ID;
		try {
			dout.writeInt(ip.length());
			dout.writeBytes(ip);
			dout.writeInt(port);
			dout.writeInt(ID);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public OverlayNodeReportsTaskFinished(byte[] b){
		super(Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED, b);
		try {
			int length = din.readInt();
			byte[] readIP = new byte[length];
			din.readFully(readIP, 0, length);
			this.ip = new String(readIP);
			this.port = din.readInt();
			this.id = din.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
