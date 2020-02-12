package cs455.overlay.wireformats;

import java.io.IOException;

public class NodeReportsOverlaySetupStatus extends Message{

    private boolean status;
    private int id;

    public NodeReportsOverlaySetupStatus(int id, boolean status){
        super(Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS);
        this.status = status;
        try {
            dout.writeInt(id);
            dout.writeBoolean(status);
            dout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NodeReportsOverlaySetupStatus(byte[] b){
        super(Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS, b);
        try {
            this.id = din.readInt();
            this.status = din.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getStatus(){
        return status;
    }

    public int getId(){
        return id;
    }
}
