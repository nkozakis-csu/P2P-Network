package cs455.overlay.routing;

import cs455.overlay.transport.TCPConnection;

public class RoutingEntry {
    public int id;
    public int distance;
    public String ip;
    public int port;
    public TCPConnection con;

    public RoutingEntry(int id, int distance, String ip, int port){
        this.id =id;
        this.distance = distance;
        this.ip = ip;
        this.port = port;
    }

    public void addTCPConnection(TCPConnection con){
        this.con = con;
    }

    public String toString(){
        return String.format("{id=%3d distance=%d address=%s:%d}", id, distance, ip, port);
    }
}
