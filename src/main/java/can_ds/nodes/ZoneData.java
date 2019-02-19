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
}
