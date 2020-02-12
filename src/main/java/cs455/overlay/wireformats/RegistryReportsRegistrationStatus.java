package cs455.overlay.wireformats;

import java.io.IOException;

public class RegistryReportsRegistrationStatus extends Message{

	public int id;
	public String msg;
	public int numNodes;

	public RegistryReportsRegistrationStatus(int id, int numNodes){
		super(Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS);
		this.id = id;
		this.numNodes = numNodes;
		if(id>=0){
			msg = "Registration request successful. Network has "+numNodes+" nodes. You are ID: "+id;
		}else{
			msg = "Registration failed";
		}
		try {
			dout.writeInt(id);
			dout.writeInt(numNodes);
			dout.writeInt(msg.length());
			dout.writeBytes(msg);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}


	
	}
	
	public RegistryReportsRegistrationStatus(byte[] b){
		super(Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS, b);
		try {
			id = din.readInt();
			numNodes = din.readInt();
			int length = din.readInt();
			byte[] message = new byte[length];
			din.readFully(message, 0, length);
			msg = new String(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return String.format("{RegistrationReportsRegistrationStatus: id=%d numNodes=%d msg=\"%s\"}", id, numNodes, msg);
	}
}
