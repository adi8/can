package can_ds.nodes;

import can_ds.interfaces.NodeInterface;
import can_ds.utils.Zone;

import java.io.Serializable;

public class UpdateData implements Serializable {
    /**
     * Zone of sender node.
     */
    public Zone zone;

    /**
     * Remote stub of sender node.
     */
    public NodeInterface nodeStub;

    /**
     * Constructor.
     *
     * @param zone - Zone details
     * @param nodeStub - Remote stub
     */
    public UpdateData(Zone zone, NodeInterface nodeStub) {
        this.zone = zone;
        this.nodeStub = nodeStub;
    }

    /**
     * Returns zone associated with sender of update message.
     *
     * @return Zone
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Sets zone assosiacted with sender of update message.
     *
     * @param zone - Zone details
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Returns remote stub associated with sender of update message.
     *
     * @return NodeInterface
     */
    public NodeInterface getNodeStub() {
        return nodeStub;
    }

    /**
     * Sets remote stub associated with sender of update message.
     *
     * @param nodeStub - Remote stub
     */
    public void setNodeStub(NodeInterface nodeStub) {
        this.nodeStub = nodeStub;
    }
}
