package can_ds.utils;

import can_ds.nodes.ZoneData;

import java.io.Serializable;

public class Zone implements Serializable {

    /**
     * Starting x coordinate.
     */
    private double x;

    /**
     * Starting y coordinate.
     */
    private double y;

    /**
     * Length along x.
     */
    private double width;

    /**
     * Height along y.
     */
    private double height;

    /**
     * X mid-point coordinate.
     */
    private double midX;

    /**
     * Y mid-point coordinate
     */
    private double midY;

    /**
     * Maximum width value.
     */
    public static final double WIDTH_MAX = 10.0;

    /**
     * Maximum height value.
     */
    public static final double HEIGHT_MAX = 10.0;

    /**
     * Default constructor.
     */
    public Zone() {}

    /**
     * Constructor that sets the attributes of a zone.
     *
     * @param x - Starting x coordinate
     * @param y - Starting y coordinate
     * @param width - Length along x
     * @param height - Length along y
     */
    public Zone(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.midX = this.x + (this.width / 2.0);
        this.midY = this.y + (this.height / 2.0);
    }

    /**
     * Set's starting x coordinate.
     *
     * @param x - Starting x coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Set's starting y coordinate.
     *
     * @param y - Starting y coordinate
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Returns x coordinate of zone.
     *
     * @return double - x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Returns y coordinate of zone.
     *
     * @return double - y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Returns width.
     *
     * @return double - width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns height.
     *
     * @return double - height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Set's width along x.
     *
     * @param width - Length along x
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * Set's height along y.
     *
     * @param height - Height along y.
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     *
     * @param px - x coordinate of point
     * @param py - y coordinate of point
     * @return boolean
     */
    public boolean isPointInZone(double px, double py) {
        if (this.x <= px && this.x + this.width > px) {
            if (this.y <= py && this.y + this.height > py) {
                return true;
            }
        }
        return false;
    }

    /**
     * Splits zone of this node to assign to a new node.
     *
     * @param zone - Zone data to be set coordinates
     * @return int - 1 - vertical split
     *               0 - horizontal split
     */
    public int splitZone(ZoneData zone) {
        int retval = 0;
        double newX = 0, newY = 0, newWidth = 0, newHeight = 0;

        // Split zone coordinates vertically
        if (this.width == this.height) {
            // Set zone values for new node
            newWidth = this.width / 2.0;
            newHeight = this.height;
            newX = this.x + (newWidth) ;
            newY = this.y;

            // Update current node's zone width
            this.setWidth(newWidth);
            retval = 1;
        }
        // Split zone coordinates horizontally
        else {
            // Set zone values for new node
            newWidth = this.width;
            newHeight = this.height / 2.0;
            newX = this.x;
            newY = this.y + (newHeight);

            // Update current zone height
            this.setHeight(newHeight);
        }

        zone.setZone(new Zone(newX, newY, newWidth, newHeight));

        return retval;
    }

    /**
     * Check if given zone is a neighbor.
     *
     * @param z - Zone
     * @return boolean
     */
    public boolean isNeighbor(Zone z) {
        if (isTopNeighbor(z) ||
            isBottomNeighbor(z) ||
            isLeftNeighbor(z) ||
            isRightNeighbor(z))
        {
            return true;
        }
        return false;
    }

    /**
     * Check if given zone is a top neighbor.
     *
     * @param z - Zone
     * @return boolean
     */
    public boolean isTopNeighbor(Zone z) {
        double x1 = this.x;
        double y1 = this.y;
        double w1 = this.width;
        double h1 = this.height;
        double x2 = z.getX();
        double y2 = z.getY();
        double w2 = z.getWidth();

        if (y1 + h1 == y2 &&
                ((x2 >= x1 && x2 < x1 + w1) ||
                        (x2 < x1 && x2 + w2 > x1))) {
            return true;
        }

        return false;
    }

    /**
     * Check if given zone is a bottom neighbor.
     *
     * @param z - Zone
     * @return boolean
     */
    public boolean isBottomNeighbor(Zone z) {
        double x1 = this.x;
        double y1 = this.y;
        double w1 = this.width;
        double x2 = z.getX();
        double y2 = z.getY();
        double w2 = z.getWidth();
        double h2 = z.getHeight();

        if (y2 + h2 == y1 &&
                ((x2 >= x1 && x2 < x1 + w1) ||
                        (x2 < x1 && x2 + w2 > x1))) {
            return true;
        }
        return false;
    }

    /**
     * Check if given zone is a left neighbor.
     *
     * @param z - Zone
     * @return boolean
     */
    public boolean isLeftNeighbor(Zone z) {
        double x1 = this.x;
        double y1 = this.y;
        double h1 = this.height;
        double x2 = z.getX();
        double y2 = z.getY();
        double w2 = z.getWidth();
        double h2 = z.getHeight();

        if (x2 + w2 == x1 &&
                ((y2 >= y1 && y2 < y1 + h1) ||
                        (y2 < y1 && y2 + h2 > y1))) {
            return true;
        }
        return false;
    }

    /**
     * Check if given zone is a right neighbor.
     *
     * @param z - Zone
     * @return boolean
     */
    public boolean isRightNeighbor(Zone z) {
        double x1 = this.x;
        double y1 = this.y;
        double w1 = this.width;
        double h1 = this.height;
        double x2 = z.getX();
        double y2 = z.getY();
        double h2 = z.getHeight();

        if (x1 + w1 == x2 &&
                ((y2 >= y1 && y2 < y1 + h1) ||
                        (y2 < y1 && y2 + h2 > y1))) {
            return true;
        }

        return false;
    }

    /**
     * Calculates distances of a point from zones mid-point.
     *
     * @param px - x coordinate of point
     * @param py - y coordinate of point
     * @return double - Distance from mid-point
     */
    public double zoneDistance(double px, double py) {
        return Math.sqrt(Math.pow(this.midX - px, 2) + Math.pow(this.midY - py, 2));
    }

    /**
     * Returns zones information: Starting and ending coordinates.
     *
     * @return String - Zone information
     */
    public String toString() {
        double xEnd = this.x + this.width;
        double yEnd = this.y + this.height;

        return String.format("X(%.2f - %.2f) ", this.x, xEnd) +
               String.format("Y(%.2f - %.2f)", this.y, yEnd);
    }
}
