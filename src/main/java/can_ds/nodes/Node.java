package can_ds.nodes;

import can_ds.interfaces.NodeInterface;
import can_ds.utils.RoutingData;
import can_ds.utils.UpdateData;
import can_ds.utils.Utils;
import can_ds.utils.Zone;

import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node implements NodeInterface {
    /**
     * Directory path to store files.
     */
    public static final String DATA_ITEMS_ROOT = "data";

    /**
     * Name.
     */
    private String name;

    /**
     * IP Address value.
     */
    private String ipAddress;

    /**
     * ID value.
     */
    private int nodeID;

    /**
     * Self stub.
     */
    private NodeInterface selfStub;

    /**
     * List of neighbors.
     */
    private Map<String, List<NodeInterface>> neighbors;

    /**
     *  Map of data times.
     */
    private Map<String, List<String>> dataItems;

    /**
     * Associated zone.
     */
    private Zone z;

    /**
     * Default constructor.
     */
    public Node() {
        this.neighbors = new HashMap<>();
        this.neighbors.put("left", new ArrayList<>());
        this.neighbors.put("bottom", new ArrayList<>());
        this.neighbors.put("right", new ArrayList<>());
        this.neighbors.put("top", new ArrayList<>());
        this.dataItems = new HashMap<>();
        this.z = null;

    }

    /**
     *  Returns Node information. (Remote interface)
     *
     * @return String - Node information
     */
    public String getInfo() {
        return this.toString();
    }

    /**
     * Returns zone of current node.
     *
     * @return Zone - Zone details
     */
    public Zone getZone() {
        return z;
    }

    /**
     * Sets name of node.
     *
     * @param name - Name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets IP address of node.
     *
     * @param ipAddress - IP address to be set
     */
    public void setIPAddr(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Sets unique ID of node.
     *
     * @param id - Node id to be set
     */
    public void setID(int id) {
        this.nodeID = id;
    }

    /**
     * Returns unique ID of node.
     *
     * @return int - ID of node
     */
    public int getID() {
        return this.nodeID;
    }

    /**
     * Returns all neighbors of a node as a list.
     *
     * @return List<NodeInterface>
     */
    public List<NodeInterface> getAllNeighborsList() {
        List<NodeInterface> allNeighbors = new ArrayList<>();

        for(List<NodeInterface> nodeList : this.neighbors.values()) {
            allNeighbors.addAll(nodeList);
        }

        return allNeighbors;
    }

    /**
     * Sets own node interface stub.
     *
     * @param selfStub - Remote interface to self
     */
    public void setSelfStub(NodeInterface selfStub) {
        this.selfStub = selfStub;
    }

    /**
     * Sets the zone managed by node.
     *
     * @param x - Starting x coordinate
     * @param y - Starting y coordinate
     * @param width - Width along x axis
     * @param height - Height along y axis
     */
    public void setZone(double x, double y, double width, double height) {
        this.z = new Zone(x, y, width, height);
    }

    /**
     * Sets zone managed by node.
     *
     * @param zone - Zone object
     */
    public void setZone(Zone zone) {
        this.z = zone;
    }

    /**
     * Returns the distance of current node zones from given point.
     *
     * @param x - x coordinate of destination
     * @param y - y coordinate of destination
     * @return double - distance to given point
     */
    public double distToPoint(double x, double y) {
        return this.z.zoneDistance(x, y);
    }

    /**
     * Forwards a message to a node nearest to given coordinates.
     *
     * @param r - All information required to route a request.
     *            Destination coordinates, operation, originating node stub.
     * @return 0 - Success
     *         1 - Failure
     */
    public int sendMessage(RoutingData r) {
        int retval = 1;
        String path = "";

        // Extract the operation to be performed
        String op = r.getOp();

        // Get points from routing data
        double px = r.getX();
        double py = r.getY();

        // Get caller node
        NodeInterface origNode = r.getOrigNode();
        boolean isPointInZone = this.z.isPointInZone(px, py);

        switch(op) {
            case "join":

                // Check if point is in our zone
                if (isPointInZone) {
                    ZoneData newZone = new ZoneData();

                    // Split zone coordinate space
                    int splitType = this.z.splitZone(newZone);

                    // Split zone neighbors
                    this.splitZoneNeighbors(newZone, r.getOrigNode(), splitType);

                    // Split zone dataitems
                    List<String> filesToRemove = this.splitZoneDataItems(newZone);

                    try {
                        r.getOrigNode().assignZone(newZone);
                    }
                    catch (RemoteException e) {
                        System.out.println("ERROR: " + e.getMessage());
                        e.printStackTrace();
                    }

                    // Delete files passed to new zone.
                    for (String fileName : filesToRemove) {
                        File file = new File(DATA_ITEMS_ROOT + fileName);
                        if (file.exists())
                            file.delete();
                    }
                }
                // Forward routing data to neighbor nearest to dest point
                else {
                    NodeInterface nearestNeighbor =
                            Utils.getNearestNeighbor(px, py, this.getAllNeighborsList());

                    try {
                        nearestNeighbor.sendMessage(r);
                    }
                    catch (RemoteException e) {
                        System.out.println("ERROR: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                break;

            case "insert":
                // Update path taken
                path = r.getPath();
                path += "peer-" + this.getID() + " ";
                r.setPath(path);

                boolean success_flag = true;

                // Check if point is in our zone
                if (isPointInZone) {
                    // Add filename to this nodes data items
                    String fileName = r.getFileName();
                    String point = px + "," + py;
                    List<String> tmpValue = this.dataItems.getOrDefault(point, (new ArrayList<>()));
                    tmpValue.add(r.getFileName());

                    byte[] fileData = null;
                    try {
                        fileData = origNode.downloadFile(fileName);
                    }
                    catch (RemoteException e) {
                        try { origNode.dispError("ERROR: " + e.getMessage()); }
                        catch (RemoteException e1) {
                            System.out.println("ERROR: " + e1.getMessage());
                        }
                        break;
                    }

                    if (fileData != null) {
                        File file = new File(DATA_ITEMS_ROOT + "/" + fileName);
                        try {
                            BufferedOutputStream out =
                                    new BufferedOutputStream(
                                            new FileOutputStream(file));
                            out.write(fileData, 0, fileData.length);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            try { origNode.dispError("ERROR: " + e.getMessage()); }
                            catch (RemoteException e1) {
                                System.out.println("ERROR: " + e1.getMessage());
                            }
                            break;
                        }

                        // Store filename in hash map
                        this.dataItems.put(point, tmpValue);

                        // Return path to be displayed at origNode
                        try {
                            origNode.dispPath(path);
                        } catch (RemoteException e) {
                            try { origNode.dispError("ERROR: " + e.getMessage()); }
                            catch (RemoteException e1) {
                                System.out.println("ERROR: " + e1.getMessage());
                            }
                            break;
                        }
                    }
                    else {
                        System.out.println("Failed to download file");
                    }
                }
                // Forward routing data to neighbor nearest to dest point
                else {
                    NodeInterface nearestNeighbor =
                            Utils.getNearestNeighbor(px, py, this.getAllNeighborsList());

                    try {
                        nearestNeighbor.sendMessage(r);
                    }
                    catch(Exception e) {
                        try { origNode.dispError("ERROR: " + e.getMessage()); }
                        catch (RemoteException e1) {
                            System.out.println("ERROR: " + e1.getMessage());
                        }
                        break;
                    }
                }
                break;

            case "search":
                // Update path taken
                path = r.getPath();
                path += "peer-" + this.getID() + " ";
                r.setPath(path);

                // Check if point is in our zone
                if (isPointInZone) {
                    // Check if file is part of data items
                    String point = px + "," + py;
                    List<String> tmpValue = this.dataItems.getOrDefault(point, (new ArrayList<>()));

                    if (tmpValue.contains(r.getFileName())) {
                        try {
                            origNode.dispPath(path);
                        }
                        catch (RemoteException e) {
                            try { origNode.dispError("ERROR: " + e.getMessage()); }
                            catch (RemoteException e1) {
                                System.out.println("ERROR: " + e1.getMessage());
                            }
                            break;
                        }
                    }
                    else {
                        try {
                            origNode.dispError("File not found!");
                        }
                        catch (RemoteException e) {
                            System.out.println("ERROR: " + e.getMessage());
                        }
                    }
                }
                // Forward routing data to neighbor nearest to dest point
                else {
                    NodeInterface nearestNeighbor =
                            Utils.getNearestNeighbor(px, py, this.getAllNeighborsList());

                    try {
                        nearestNeighbor.sendMessage(r);
                    }
                    catch(Exception e) {
                        try { origNode.dispError("ERROR: " + e.getMessage()); }
                        catch (RemoteException e1) {
                            System.out.println("ERROR: " + e1.getMessage());
                        }
                        break;
                    }
                }
        }

        return retval;
    }

    /**
     * Update neighbor list based on given update from node.
     *
     * @param updateInfo
     * @return 0 - Success
     *         1 - Failure
     */
    public int sendUpdate(UpdateData updateInfo) {
        Zone neighborZone = updateInfo.getZone();
        NodeInterface neighbor = updateInfo.getNodeStub();

        // Check if left neighbor
        if (this.z.isLeftNeighbor(neighborZone)) {
            List<NodeInterface> leftNeighbors = this.neighbors.getOrDefault("left", (new ArrayList<>()));
            if (!leftNeighbors.contains(neighbor)) {
                leftNeighbors.add(neighbor);
                this.neighbors.put("left", leftNeighbors);
            }
        }
        // Check if right neighbor
        else if (this.z.isRightNeighbor(neighborZone)) {
            List<NodeInterface> rightNeighbors =
                    this.neighbors.getOrDefault("right", (new ArrayList<>()));
            if (!rightNeighbors.contains(neighbor)) {
                rightNeighbors.add(neighbor);
                this.neighbors.put("right", rightNeighbors);
            }
        }
        // Check if bottom neighbor
        else if (this.z.isBottomNeighbor(neighborZone)) {
            List<NodeInterface> botNeighbors =
                    this.neighbors.getOrDefault("bottom", (new ArrayList<>()));
            if (!botNeighbors.contains(neighbor)) {
                botNeighbors.add(neighbor);
                this.neighbors.put("bottom", botNeighbors);
            }
        }
        // Check if top neighbor
        else if (this.z.isTopNeighbor(neighborZone)) {
            List<NodeInterface> topNeighbors =
                    this.neighbors.getOrDefault("top", (new ArrayList<>()));
            if (!topNeighbors.contains(neighbor)) {
                topNeighbors.add(neighbor);
                this.neighbors.put("top", topNeighbors);
            }
        }
        // Remove as neighbor if currently one
        else {
            for (List<NodeInterface> neighborNodes : neighbors.values()) {
                if (neighborNodes.contains(neighbor)) {
                    neighborNodes.remove(neighbor);
                }
            }
        }

        return 0;
    }

    /**
     * Sets new zone's neighbors.
     *
     * @param zone - Zone data to be updated
     */
    public void splitZoneNeighbors(ZoneData zone, NodeInterface origNode, int splitType) {
        Map<String, List<NodeInterface>> newNeighbors = new HashMap<>();

        UpdateData currUpdate = new UpdateData(this.z, this.selfStub);

        // Send update message to all neighbors of current node
        for (NodeInterface currNodeNeighbor : this.getAllNeighborsList()) {
            try {
                currNodeNeighbor.sendUpdate(currUpdate);
            }
            catch (RemoteException e) {
                System.out.println("ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }

        Zone tmp_zone = zone.getZone();

        // Vertical split
        if (splitType == 1) {
            // Set right neighbors of new node directly as that of current node
            List<NodeInterface> rightNeighbors = new ArrayList<>();
            rightNeighbors
                    .addAll(this.neighbors.getOrDefault("right", (new ArrayList<>())));
            newNeighbors.put("right", rightNeighbors);

            // Check top neighbors of current node
            List<NodeInterface> topNeighbors = new ArrayList<>();
            List<NodeInterface> tmp = new ArrayList<>(
                    this.neighbors.getOrDefault("top", (new ArrayList<>())));
            if (tmp.size() > 0) {
                for (NodeInterface topNode : this.neighbors.getOrDefault("top", (new ArrayList<>()))) {
                    try {
                        if (topNode.isNeighbor(tmp_zone, "bottom")) {
                            topNeighbors.add(topNode);
                        }

                        // Remove topNode if no longer a top neighbor
                        // Note: Checking for bottom here as the zone passed to isNeigbor should
                        //       be bottom zone to qualify topNode as a top neighbor
                        if (!topNode.isNeighbor(this.getZone(), "bottom")) {
                            tmp.remove(topNode);
                        }
                    } catch (RemoteException e) {
                        System.out.println("ERROR: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            newNeighbors.put("top", topNeighbors);
            this.neighbors.put("top", tmp);


            // Check bottom neighbors of current node
            List<NodeInterface> bottomNeighbors = new ArrayList<>();
            tmp = new ArrayList<>(this.neighbors.getOrDefault("bottom", (new ArrayList<>())));

            if (tmp.size() > 0) {
                for (NodeInterface botNode : this.neighbors.getOrDefault("bottom", (new ArrayList<>()))) {
                    try {
                        if (botNode.isNeighbor(tmp_zone, "top")) {
                            bottomNeighbors.add(botNode);
                        }

                        // Remove botNode if no longer a bottom neighbor
                        if (!botNode.isNeighbor(this.getZone(), "top")) {
                            tmp.remove(botNode);
                        }
                    } catch (RemoteException e) {
                        System.out.println("ERROR: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            newNeighbors.put("bottom", bottomNeighbors);
            this.neighbors.put("bottom", tmp);

            // Set left neighbor of new node
            List<NodeInterface> leftNeighbors = new ArrayList<>();
            leftNeighbors.add(selfStub);
            newNeighbors.put("left", leftNeighbors);

            // Remove all right neighbors of current node and add new node as right neighbor
            List<NodeInterface> currNeighbors = new ArrayList<>();
            currNeighbors.add(origNode);
            this.neighbors.put("right", currNeighbors);
        }
        // Horizontal split
        else {
            // Set top neighbors of new node directly as that of current node
            List<NodeInterface>  topNeighbors = new ArrayList<>();
            topNeighbors
                    .addAll(this.neighbors.getOrDefault("top", (new ArrayList<>())));
            newNeighbors.put("top", topNeighbors);

            // Check right neighbors of current node
            List<NodeInterface> rightNeighbors = new ArrayList<>();
            List<NodeInterface> tmp = new ArrayList<>(
                    this.neighbors.getOrDefault("right", (new ArrayList<>())));
            if (tmp.size() > 0) {
                for (NodeInterface rightNode : this.neighbors.getOrDefault("right", (new ArrayList<>()))) {
                    try {
                        if (rightNode.isNeighbor(tmp_zone, "left")) {
                            rightNeighbors.add(rightNode);
                        }

                        // Remove rightNode if no longer a right neighbor
                        // Note: Checking for left here as the zone passed to isNeigbor should
                        //       be left zone to qualify rightNode as a right neighbor
                        if (!rightNode.isNeighbor(this.getZone(), "left")) {
                            tmp.remove(rightNode);
                        }
                    } catch (RemoteException e) {
                        System.out.println("ERROR: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            newNeighbors.put("right", rightNeighbors);
            this.neighbors.put("right", tmp);

            // Check left neighbors of current node
            List<NodeInterface> leftNeighbors = new ArrayList<>();
            tmp = new ArrayList<>(this.neighbors.getOrDefault("left", (new ArrayList<>())));
            if (tmp.size() > 0) {
                for (NodeInterface leftNode : this.neighbors.getOrDefault("left", (new ArrayList<>()))) {
                    try {
                        if (leftNode.isNeighbor(tmp_zone, "right")) {
                            leftNeighbors.add(leftNode);

                            // Remove leftNode if no longer a left neighbor
                            if (!leftNode.isNeighbor(this.getZone(), "right")) {
                                tmp.remove(leftNode);
                            }
                        }
                    } catch (RemoteException e) {
                        System.out.println("ERROR: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            newNeighbors.put("left", leftNeighbors);
            this.neighbors.put("left", tmp);

            // Set bottom neighbor of new node
            List<NodeInterface> bottomNeighbors = new ArrayList<>();
            bottomNeighbors.add(this.selfStub);
            newNeighbors.put("bottom", bottomNeighbors);

            // Remove all top neighbors of current node and add new node as top neighbor
            List<NodeInterface> currNeighbors = new ArrayList<>();
            currNeighbors.add(origNode);
            this.neighbors.put("top", currNeighbors);
        }

        // Send update message to all neighbors of new node
        UpdateData newNodeUpdate = new UpdateData(zone.getZone(), origNode);
        for (List<NodeInterface> newNodeNeighbors : newNeighbors.values()) {
            for (NodeInterface newNodeNeighbor : newNodeNeighbors) {
                try {
                    newNodeNeighbor.sendUpdate(newNodeUpdate);
                }
                catch (RemoteException e) {
                    System.out.println("ERROR: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        zone.setNeighbors(newNeighbors);
    }

    /**
     * Splits zone data items and assigns to new node.
     *
     * @param zone - ZoneData for new node
     * @return List<> - List of filenames to remove from current node
     */
    public List<String> splitZoneDataItems(ZoneData zone) {
        Map<String, List<String>> newZoneDataItems = new HashMap<>();
        List<String> filesToRemove = new ArrayList<>();
        for (String key : this.dataItems.keySet()) {
            String[] keyParts = key.split(",");
            double x = Double.parseDouble(keyParts[0]);
            double y = Double.parseDouble(keyParts[1]);
            if (!this.z.isPointInZone(x, y)) {
                List<String> tmpFileNames = this.dataItems.get(key);
                // Add key, filename pair for new zone.
                newZoneDataItems.put(key, tmpFileNames);

                // Keep track of file names to physically remove from node.
                filesToRemove.addAll(tmpFileNames);

                // Remove key, filename pair as new zone will manage them.
                this.dataItems.remove(key);
            }
        }

        zone.setDataItems(newZoneDataItems);

        return filesToRemove;
    }

    /**
     * Returns if given zone is a neighbor along given position.
     *
     * @param zone - Zone information
     * @param position - Neighbor position
     * @return boolean
     */
    public boolean isNeighbor(Zone zone, String position) {
        boolean retval = false;
        switch(position) {
            // Check if given zone is top neighbor
            case "top":
                retval = this.z.isTopNeighbor(zone);
                break;

            // Check if given zone is bottom neighbor
            case "bottom":
                retval = this.z.isBottomNeighbor(zone);
                break;

            // Check if given zone is left neighbor
            case "left":
                retval = this.z.isLeftNeighbor(zone);
                break;

            // Check if given zone is right neighbor
            case "right":
                retval = this.z.isRightNeighbor(zone);
                break;

            // Check if given zone is any neighbor
            case "any":
                retval = (this.z.isTopNeighbor(zone) ||
                          this.z.isBottomNeighbor(zone) ||
                          this.z.isRightNeighbor(zone) ||
                          this.z.isLeftNeighbor(zone));
        }

        return retval;
    }

    /**
     * Assigns a zone and its neighbors to new node.
     *
     * @param zone - Zone information
     */
    public void assignZone(ZoneData zone) {
        this.setZone(zone.getZone());
        this.neighbors = zone.getNeighbors();
        this.dataItems = zone.getDataItems();
        NodeInterface nodeStub = zone.getDestStub();

        for (List<String> fileNames : this.dataItems.values()) {
            for (String fileName : fileNames) {
                try {
                    nodeStub.downloadFile(DATA_ITEMS_ROOT + "/" + fileName);
                }
                catch (RemoteException e) {
                    System.out.println("Failed to download file " + fileName);
                }
            }
        }
    }

    /**
     * Returns byte array of given file to caller.
     *
     * @param fileName - File to be downloaded
     * @return byte[]
     */
    public byte[] downloadFile(String fileName) {
        try {
            File file = new File(fileName);
            byte[] buffer = new byte[(int) file.length()];
            BufferedInputStream inp = new BufferedInputStream(new FileInputStream(fileName));
            inp.read(buffer, 0, buffer.length);
            inp.close();
            return buffer;
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }
    }

    /**
     * Display error message from a remote node.
     *
     * @param err - Error message
     */
    public void dispError(String err) {
        System.out.println(err);
    }

    /**
     * Displays path taken to reach destionation.
     *
     * @param path - Path to dest
     */
    public void dispPath(String path) {
        System.out.println("Path to destination: ");
        String[] peers = path.split(" ");
        String dest = "";
        for (int i = 0; i < peers.length; i++) {
            System.out.print(peers[i]);
            if (i != peers.length - 1)
                System.out.print(" => ");
            else
                dest = peers[i];
        }
        System.out.println();
        System.out.println("Peer " + dest + " stores the file");
    }

    /**
     * Returns list of all neighbors.
     *
     * @return String - List of neighbors
     */
    public String neighborsToString() {
        StringBuilder str;

        if (this.neighbors.isEmpty()) {
            str = new StringBuilder("[]");
        }
        else {
            str = new StringBuilder("[ ");
            for (NodeInterface node : this.getAllNeighborsList()) {
                try {
                    str.append("peer-")
                       .append(node.getID())
                       .append(" ");
                }
                catch (Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            str.append("]");
        }

        return str.toString();
    }

    /**
     * Returns string representation of data items stored by a node
     *
     * @return String
     */
    public String dataItemsToString() {
        StringBuilder str;
        if (this.dataItems.isEmpty()) {
            str = new StringBuilder("[ ]");
        }
        else {
            str = new StringBuilder("[ \n");
            for (Map.Entry<String, List<String>> dataItemEntry : this.dataItems.entrySet()) {
                str.append("(")
                        .append(dataItemEntry.getKey())
                        .append(") -> ");
                for (String fileName : dataItemEntry.getValue()) {
                    str.append(fileName)
                            .append(" ");
                }
                str.append("\n");
            }
            str.append("]");
        }

        return str.toString();
    }

    /**
     * Returns node information. (Local interface)
     *
     * @return String - Node information
     */
    public String toString() {
        String zoneInfo = (z == null) ? "N/A" : this.z.toString();

        return "+++++++++++++++++++++++\n" +
               "Name      : " + this.name + "\n" +
               "IPAddr    : " + this.ipAddress + "\n" +
               "Zone      : " + zoneInfo + "\n" +
               "Neighbors : " + this.neighborsToString() + "\n" +
               "Data Items: " + this.dataItemsToString() + "\n" +
               "+++++++++++++++++++++++\n";
    }

}
