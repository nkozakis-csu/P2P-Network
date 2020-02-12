package cs455.overlay.routing;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RoutingTable {

    private HashMap<Integer, RoutingEntry> routingTable;

    public RoutingTable(){
        routingTable = new HashMap<>();
    }

    public void add(RoutingEntry r){
        routingTable.put(r.id, r);
    }

    public RoutingEntry get(int id){
        return routingTable.get(id);
    }

    public int size(){
        return routingTable.size();
    }

    public Set<Map.Entry<Integer, RoutingEntry>> getEntrySet(){
        return routingTable.entrySet();
    }

    public Set<Integer> getDestinationIDs(){
        return routingTable.keySet();
    }

    public String toString(){
        return routingTable.toString();
    }
}
