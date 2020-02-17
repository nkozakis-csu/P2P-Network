package cs455.overlay.wireformats;

import java.io.IOException;

public class OverlayNodeReportsTrafficSummary extends Message {
	
	public int id;
	public int numSent;
	public int numForwarded;
	public long sumSent;
	public int numReceived;
	public long sumReceived;
	
	public OverlayNodeReportsTrafficSummary(int id, int numSent, int numForwarded, long sumSent, int numReceived, long sumReceived){
		super(Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY);
		this.id = id;
		this.numSent = numSent;
		this.numForwarded = numForwarded;
		this.sumSent = sumSent;
		this.numReceived = numReceived;
		this.sumReceived = sumReceived;
		try {
			dout.writeInt(id);
			dout.writeInt(numSent);
			dout.writeInt(numForwarded);
			dout.writeLong(sumSent);
			dout.writeInt(numReceived);
			dout.writeLong(sumReceived);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public OverlayNodeReportsTrafficSummary(byte[] b){
		super(Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY, b);
		try {
			this.id = din.readInt();
			this.numSent = din.readInt();
			this.numForwarded = din.readInt();
			this.sumSent = din.readLong();
			this.numReceived = din.readInt();
			this.sumReceived = din.readLong();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String toString(){
		return String.format("Node %3d | %d | %d | %d | %d | %d", id, numSent, numReceived, numForwarded, sumSent, sumReceived);
	}
	
}
