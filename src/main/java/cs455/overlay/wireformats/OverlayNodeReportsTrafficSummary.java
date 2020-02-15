package cs455.overlay.wireformats;

import java.io.IOException;

public class OverlayNodeReportsTrafficSummary extends Message {
	
	public int id;
	public int numSent;
	public int numForwarded;
	public int sumSent;
	public int numReceived;
	public int sumReceived;
	
	public OverlayNodeReportsTrafficSummary(int id, int numSent, int numForwarded, int sumSent, int numReceived, int sumReceived){
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
			dout.writeInt(sumSent);
			dout.writeInt(numReceived);
			dout.writeInt(sumReceived);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public OverlayNodeReportsTrafficSummary(byte[] b){
		try {
			this.id = din.readInt();
			this.numSent = din.readInt();
			this.numForwarded = din.readInt();
			this.sumSent = din.readInt();
			this.numReceived = din.readInt();
			this.sumReceived = din.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String toString(){
		return String.format("Node %d | %d | %d | %d | %d | %d", id, numSent, numReceived, numForwarded, sumSent, sumReceived);
	}
}
