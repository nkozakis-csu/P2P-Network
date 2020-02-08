package cs455.overlay.wireformats;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import jdk.internal.misc.JavaSecurityAccess;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;

public class RegistrySendsNodeManifest extends Message{

    private RoutingTable routingTable;

    public RegistrySendsNodeManifest(){
        super(Protocol.REGISTRY_SENDS_NODE_MANIFEST);
    }

    public RegistrySendsNodeManifest(byte[] b){
        super(Protocol.REGISTRY_SENDS_NODE_MANIFEST, b);
        routingTable = new RoutingTable();
        try {
            int id = din.readInt();
            int distance = din.readInt();
            int length = din.readInt();
            byte[] ip = new byte[length];
            din.readFully(ip,0,length);
            int port = din.readInt();
            routingTable.add(new RoutingEntry(id, distance, new String(ip), port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    public void addToTable(int id, int distance, String ip, int port){
        try {
            dout.writeInt(id);
            dout.writeInt(distance);
            dout.writeInt(ip.length());
            dout.write(ip.getBytes());
            dout.writeInt(port);
            dout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
