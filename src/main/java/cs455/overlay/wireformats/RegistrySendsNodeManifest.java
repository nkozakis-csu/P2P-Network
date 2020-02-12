package cs455.overlay.wireformats;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import jdk.internal.misc.JavaSecurityAccess;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class RegistrySendsNodeManifest extends Message{

    private RoutingTable routingTable;

    public RegistrySendsNodeManifest(RoutingTable rt){
        super(Protocol.REGISTRY_SENDS_NODE_MANIFEST);
        try{
            dout.writeInt(rt.size());
        }catch(IOException e){
            e.printStackTrace();
        }
        for(Map.Entry<Integer, RoutingEntry> entry : rt.getEntrySet()){
            serializeEntry(entry.getValue());
        }
    }

    public RegistrySendsNodeManifest(byte[] b){
        super(Protocol.REGISTRY_SENDS_NODE_MANIFEST, b);
        routingTable = new RoutingTable();
        try {
            int numEntries = din.readInt();
            for(int i=0; i<numEntries; i++) {
                int id = din.readInt();
                int distance = din.readInt();
                int length = din.readInt();
                byte[] ip = new byte[length];
                din.readFully(ip, 0, length);
                int port = din.readInt();
                routingTable.add(new RoutingEntry(id, distance, new String(ip), port));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    public void serializeEntry(RoutingEntry entry){
        try {
            dout.writeInt(entry.id);
            dout.writeInt(entry.distance);
            dout.writeInt(entry.ip.length());
            dout.write(entry.ip.getBytes());
            dout.writeInt(entry.port);
            dout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return this.routingTable.toString();
    }
}
