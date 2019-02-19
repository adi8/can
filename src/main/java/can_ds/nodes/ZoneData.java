package can_ds.nodes;

import can_ds.interfaces.NodeInterface;
import can_ds.utils.Zone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZoneData implements Serializable {

    /**
     * Zone details of new zone.
     */
    private Zone zone;

    /**
     * List of neighbors of new zone.
     */
    private Map<String, List<NodeInterface>> neighbors;

    /**
     * List of data items of new zone.
     */
    private Map<String, List<String>> dataItems;

    /**
     * Remote stub of zone splitter.
     */
    private NodeInterface destStub;


    /**
     * Default constructor.
     */
    public ZoneData() {}

    /**
     * Creates a new ZoneData object.
     *
     * @param zone - Starting x coordinate
     * @param neighbors - Neighbors of a zone
     */
    public ZoneData(Zone zone, Map<String, List<NodeInterface>> neighbors) {
        this.zone = zone;
        this.neighbors = new HashMap<>();
        this.neighbors.put("left", new ArrayList<>());
        this.neighbors.put("bottom", new ArrayList<>());
        this.neighbors.put("right", new ArrayList<>());
        this.neighbors.put("top", new ArrayList<>());
        this.dataItems = new HashMap<>();
        this.destStub = null;
    }

    /**
     * Returns zone of new node.
     *
     * @return Zone
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Sets zone of new node.
     *
     * @param zone - Zone information
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get neighbors.
     *
     * @return List<NodeInteface>
     */
    public Map<String, List<NodeInterface>> getNeighbors() {
        return neighbors;
    }

    /**
     * Set neighbors.
     *
     * @param neighbors - Neighbors of zone
     */
    public void setNeighbors(Map<String, List<NodeInterface>> neighbors) {
        this.neighbors = neighbors;
    }

    /**
     * Returns data items managed.
     *
     * @return Map<String, List<String>>
     */
    public Map<String, List<String>> getDataItems() {
        return dataItems;
    }

    /**
     * Sets data items managed.
     *
     * @param dataItems
     */
    public void setDataItems(Map<String, List<String>> dataItems) {
        this.dataItems = dataItems;
    }

    /**
     * Returns remote stub of destination node.
     *
     * @return NodeInterface
     */
    public NodeInterface getDestStub() {
        return destStub;
    }

    /**
     * Sets remote stub of destination node.
     *
     * @param destStub
     */
    public void setDestStub(NodeInterface destStub) {
        this.destStub = destStub;
    }
}
