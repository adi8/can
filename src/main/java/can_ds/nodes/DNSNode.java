package can_ds.nodes;

import can_ds.interfaces.DNSNodeInterface;
import can_ds.interfaces.NodeInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class DNSNode implements DNSNodeInterface {
    /**
     * DNS node's URL.
     */
    public static final String DNS_URL = "can://nodes/dns";

    /**
     * Max BS nodes to return
     */
    public static final int MAX_BS_NODES = 3;

    /**
     * Counter for node id's.
     */
    private int nextNodeId;

    /**
     * Count of nodes in network.
     */
    private int nodeCount;

    /**
     * Map of nodes in network.
     */
    private Map<Integer, NodeInterface> nodeMap;

    /**
     * Java RMI registry.
     */
    private Registry registry;

    /**
     * Bootstraps the DNS node.
     */
    public DNSNode() {
        this.nextNodeId = 0;
        this.nodeCount  = 0;
        this.nodeMap    = new HashMap<>();
        try {
            this.registry = LocateRegistry.getRegistry();
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Returns a list of bootstrap nodes.
     *
     * @return List<NodeInterface> - Bootstrap nodes list
     */
    public List<NodeInterface> getBSNodes() {
        List<NodeInterface> nodeStubs = new ArrayList<>();

        nodeStubs.addAll(this.nodeMap.values());
        Collections.shuffle(nodeStubs);

        if (this.nodeCount > 3) {
            nodeStubs = nodeStubs.subList(0, MAX_BS_NODES);
        }

        return nodeStubs;
    }

    /**
     * Registers a node with remote interface nodeStub and
     * returns a unique id for the node.
     *
     * @param nodeStub - Remote interface of registering node
     * @return int - Unique ID for node
     */
    public int register(NodeInterface nodeStub) {
        int curNodeID = this.nextNodeId++;

        this.nodeCount++;

        this.nodeMap.put(curNodeID, nodeStub);

        return curNodeID;
    }

    /**
     * De-registers a node with id.
     *
     * @param id - ID of a node
     */
    public void deregister(int id) {
        this.nodeMap.remove(id);
    }

    /**
     * Displays information of all nodes in overlay network.
     * Information that gets displayed:
     * Node count, IP Address, Peer ID
     *
     * @return String - Information string of all nodes in network.
     */
    public String dispNodeInfo() {
        if (this.nodeCount == 0) {
            return "No nodes in overlay n/w.";
        }
        else {
            String info = "Nodes: " + this.nodeCount + "\n";

            for (NodeInterface node : this.nodeMap.values()) {
                try {
                    info += node.getInfo();

                }
                catch (Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                    e.printStackTrace();
                }

            }

            return info;
        }

    }

    /**
     * Returns information for given id.
     *
     * @param id - Node ID
     * @return String - Node information
     */
    public String dispNodeInfo(int id) {
        String str = "No such peer exists!";
        if (this.nodeMap.containsKey(id)) {
            NodeInterface node = this.nodeMap.get(id);
            try {
                str = node.getInfo();
            }
            catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return str;
    }

}