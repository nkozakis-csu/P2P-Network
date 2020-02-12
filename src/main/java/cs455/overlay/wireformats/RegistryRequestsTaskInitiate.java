package cs455.overlay.wireformats;

import java.io.IOException;

public class RegistryRequestsTaskInitiate extends Message {

    private int numMessages;

    public RegistryRequestsTaskInitiate(int numMessages){
        super(Protocol.REGISTRY_REQUESTS_TASK_INITIATE);
        this.numMessages = numMessages;
        try {
            dout.writeInt(numMessages);
            dout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RegistryRequestsTaskInitiate(byte[] b){
        super(Protocol.REGISTRY_REQUESTS_TASK_INITIATE, b);
        try {
            this.numMessages = din.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNumMessages(){
        return numMessages;
    }
}
