package cs455.overlay.wireformats;

import java.io.IOException;

public class RegistryReportsDeregistationStatus extends Message{
	
	public int id;
	public boolean status;
	
	public RegistryReportsDeregistationStatus(int id, boolean status){
		super(Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS);
		this.id = id;
		this.status = status;
		try {
			dout.writeInt(id);
			dout.writeBoolean(status);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public RegistryReportsDeregistationStatus(byte[] b){
		super(Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS, b);
		try {
			this.id = din.readInt();
			this.status = din.readBoolean();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String toString(){
		return String.format("{RegistryReportsDeregistrationStatus: id=%d, status=%b}", id, status);
	}
}
