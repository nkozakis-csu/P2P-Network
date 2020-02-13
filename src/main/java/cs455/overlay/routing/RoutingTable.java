package cs455.overlay.routing;

import javafx.collections.transformation.SortedList;

import java.util.*;

public class RoutingTable {

    private HashMap<Integer, RoutingEntry> routingTable;
    private ArrayList<Integer> keys;

    public RoutingTable(){
        routingTable = new HashMap<>();
        keys = new ArrayList<>();
    }

    public void add(RoutingEntry r){
        routingTable.put(r.id, r);
        keys.add(r.id);
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

    public ArrayList<Integer> getKeys(){
        return keys;
    }

    public String toString(){
        return routingTable.toString();
    }
}
