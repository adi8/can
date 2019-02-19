package can_ds.utils;

import can_ds.interfaces.NodeInterface;

import java.io.Serializable;

public class RoutingData implements Serializable {
    /**
     * X coordinate of point to search.
     */
    private double x;

    /**
     * Y coordinate of point to search.
     */
    private double y;

    /**
     * Remote interface of node that has initiated search.
     */
    private NodeInterface origNode;

    /**
     * Path for reaching point (x,y).
     */
    private String path;

    /**
     * Operation requested.
     */
    private String op;

    /**
     * Filename for insert and search op.
     */
    private String fileName;

    /**
     * Constructor for  RoutingData object.
     *
     * @param x - x coordinate of destination
     * @param y - y coordinate of destination
     * @param node - Remote interface of origin node
     * @param op - Operation requested
     */
    public RoutingData(double x, double y, NodeInterface node, String op) {
        this.x = x;
        this.y = y;
        this.origNode = node;
        this.op = op;
        this.path = "";
        this.fileName = "";
    }

    /**
     * Returns operation of this object.
     *
     * @return String - Operation requested.
     */
    public String getOp() {
        return op;
    }

    /**
     * Returns filename associated with insert and search op.
     *
     * @return String
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Returns x coordinate of destination.
     *
     * @return double - x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Returns y coordinate of destination.
     *
     * @return double - y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Returns node interface of originating node.
     *
     * @return NodeInterface - Remote object of origin node
     */
    public NodeInterface getOrigNode() {
        return origNode;
    }

    /**
     * Returns path taken.
     *
     * @return String
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets filename for insert and search operation.
     *
     * @param fileName - File name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Set path taken.
     *
     * @param path - Updated path
     */
    public void setPath(String path) {
        this.path = path;
    }
}
