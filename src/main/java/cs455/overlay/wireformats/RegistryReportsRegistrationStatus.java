package cs455.overlay.wireformats;

import java.io.IOException;

public class RegistryReportsRegistrationStatus extends Message{

	public int id;
	public String msg;
	public int numNodes;

	RegistryReportsRegistrationStatus(int id, int numNodes){
		super(Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS);
		this.id = id;
		this.numNodes = numNodes;
		try {
			dout.writeInt(id);
			dout.writeInt(numNodes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(id>=0){
			msg = "Registration request successful. Network has "+numNodes+" nodes.";
		}
	
	}
	
	RegistryReportsRegistrationStatus(byte[] b){
		super(Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS, b);
		try {
			id = din.readInt();
			numNodes = din.readInt();
			int length = din.readInt();
			byte[] message = new byte[length];
			din.readFully(message, 0, length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
