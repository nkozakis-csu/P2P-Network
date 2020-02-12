package cs455.overlay.wireformats;

public class NodeReportsOverlaySetupStatus extends Message{

    public NodeReportsOverlaySetupStatus(int status){
        super(Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS);
        this.data = bout.toByteArray();
    }

    public NodeReportsOverlaySetupStatus(byte[] b){
        super(Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS, b);
    }
}
