package cs455.overlay.wireformats;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import jdk.internal.misc.JavaSecurityAccess;
import sun.awt.image.ImageWatched;

import java.io.*;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class RegistrySendsNodeManifest extends Message{

    private RoutingTable routingTable;
    public LinkedList<Integer> assignedIDs;

    public RegistrySendsNodeManifest(RoutingTable rt, LinkedList<Integer> assignedIDs){
        super(Protocol.REGISTRY_SENDS_NODE_MANIFEST);
        this.assignedIDs = assignedIDs;
        this.routingTable = rt;
        try{
            dout.writeInt(assignedIDs.size());
            for(Integer i : assignedIDs){
                dout.writeByte(i);
            }
            dout.writeInt(rt.size());
            for(Map.Entry<Integer, RoutingEntry> entry : rt.getEntrySet()){
                serializeEntry(entry.getValue());
            }
            dout.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public RegistrySendsNodeManifest(byte[] b){
        super(Protocol.REGISTRY_SENDS_NODE_MANIFEST, b);
        routingTable = new RoutingTable();
        this.assignedIDs = new LinkedList<>();
        try {
            int numIDs = din.readInt();
            for(int i=0; i<numIDs; i++){
                assignedIDs.add((int)din.readByte());
            }
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

    private void serializeEntry(RoutingEntry entry) throws IOException {
        dout.writeInt(entry.id);
        dout.writeInt(entry.distance);
        dout.writeInt(entry.ip.length());
        dout.write(entry.ip.getBytes());
        dout.writeInt(entry.port);
    }

    @Override
    public String toString() {
        return this.routingTable.toString();
    }
    
    public static void main(String[] args) {
        RoutingTable rt = new RoutingTable();
        rt.add(new RoutingEntry(0,1,"test", 1));
        rt.add(new RoutingEntry(1,2,"test", 2));
        rt.add(new RoutingEntry(2,4,"test", 3));
        LinkedList<Integer> list = new LinkedList<>();
        list.add(0);
        list.add(1);
        list.add(2);
        RegistrySendsNodeManifest m = new RegistrySendsNodeManifest(rt, list);
        System.out.println("BYTES: "+new String(m.getBytes()));
        System.out.println(m.toString());
        DataInputStream din = new DataInputStream( new BufferedInputStream(new ByteArrayInputStream(m.getBytes())));
        try {
            RegistrySendsNodeManifest m2 = (RegistrySendsNodeManifest) Message.getMessage(din);
            System.out.println("GOT: "+m2.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
