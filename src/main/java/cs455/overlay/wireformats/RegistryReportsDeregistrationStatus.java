package cs455.overlay.wireformats;

import java.io.IOException;

public class RegistryReportsDeregistrationStatus extends Message{
	
	public boolean status;
	
	public RegistryReportsDeregistrationStatus(boolean status){
		super(Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS);
		this.status = status;
		try {
			dout.writeBoolean(status);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public RegistryReportsDeregistrationStatus(byte[] b){
		super(Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS, b);
		try {
			this.status = din.readBoolean();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String toString(){
		return String.format("{RegistryReportsDeregistrationStatus: status=%b}", status);
	}
}
