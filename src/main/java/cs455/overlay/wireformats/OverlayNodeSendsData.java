package cs455.overlay.wireformats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Random;

public class OverlayNodeSendsData extends Message{
    
    Logger LOG = LogManager.getLogger(OverlayNodeSendsData.class);

    static Random rand = new Random();

    public int payload;
    public int source;
    public int destination;

    public OverlayNodeSendsData(int source, int destination){
        super(Protocol.OVERLAY_NODE_SENDS_DATA);
        this.payload = rand.nextInt();
        this.destination = destination;
        this.source = source;
        writeData();
    }

    public OverlayNodeSendsData(byte[] b){
        super(Protocol.OVERLAY_NODE_SENDS_DATA, b);
        LOG.debug("Process bytes"+new String(b));
        try {
            this.source = din.readInt();
            this.destination = din.readInt();
            this.payload = din.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void writeData(){
        try {
            dout.writeInt(source);
            dout.writeInt(destination);
            dout.writeInt(payload);
            dout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toString(){
        return String.format("{OverlayNodeSendsData: Source=%s Dest=%s Payload=%d}", source, destination, payload);
    }

}
