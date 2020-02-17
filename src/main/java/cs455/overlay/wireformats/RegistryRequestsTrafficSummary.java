package cs455.overlay.wireformats;

public class RegistryRequestsTrafficSummary extends Message {
	
	public RegistryRequestsTrafficSummary(){
		super(Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY);
		
	}
	
	public RegistryRequestsTrafficSummary(byte[] b){
		super(Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY, b);
	}
}
