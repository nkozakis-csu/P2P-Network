package cs455.overlay.wireformats;

public class OverlayNodeSendsDeregistration extends Message {
	
	public OverlayNodeSendsDeregistration(){
		super(Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION);
	}
	
	public OverlayNodeSendsDeregistration(byte[] b){
		super(Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION, b);
	}
}
