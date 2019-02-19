package can_ds.servers;

import can_ds.interfaces.DNSNodeInterface;
import can_ds.interfaces.NodeInterface;
import can_ds.nodes.DNSNode;
import can_ds.nodes.Node;
import can_ds.utils.RoutingData;
import can_ds.utils.Utils;
import can_ds.utils.Zone;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class NodeServer {
    private static final String PEER_PREFIX = "peer-";

    public static void main(String[] args) {
        try {
            Node node = new Node();

            NodeInterface nodeStub = (NodeInterface) UnicastRemoteObject.exportObject(node, 0);

            node.setSelfStub(nodeStub);

            String startMsg = "Node ready\n";
            System.out.println(startMsg);

            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);

            int peerID = -1;
            String peerName = PEER_PREFIX + "new";

            boolean exit_flag = false;
            while(!exit_flag) {
                System.out.print(peerName + " > ");

                String[] cmd = br.readLine().trim().split(" ");

                Registry registry;
                DNSNodeInterface dnsNodeStub;

                try {
                    registry = LocateRegistry.getRegistry("localhost", 4001);

                    // Get remote interface of dns node
                    dnsNodeStub = (DNSNodeInterface) registry.lookup(DNSNode.DNS_URL);
                }
                catch (Exception e) {
                    System.out.println("DNS Service not running. Please try again later");
                    continue;
                }

                String cmd_wo_args = cmd[0];

                switch(cmd_wo_args) {
                    case "view":
                        if (cmd.length > 1) {
                            try {
                                int id = Integer.parseInt(cmd[1]);
                                System.out.println(dnsNodeStub.dispNodeInfo(id));
                            }
                            catch (Exception e) {
                                System.out.println("ERROR: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                        else {
                            System.out.println(dnsNodeStub.dispNodeInfo());
                        }
                        break;

                    case "join":
                        if (node.getZone() == null) {
                            List<NodeInterface> bsNodeStubs = null;
                            try {
                                // Get random bootstrap nodes from dns node
                                bsNodeStubs = dnsNodeStub.getBSNodes();

                                // Register new node with the dns node
                                peerID = dnsNodeStub.register(nodeStub);
                            }
                            catch (RemoteException e) {
                                System.out.println("ERROR: DNS server seems to be down. Try again later!");
                                break;
                            }


                            // Set IP address, peerName and peerID of node
                            String peerAddress = Utils.getAddress();
                            peerName = PEER_PREFIX + peerID;

                            node.setIPAddr(peerAddress);
                            node.setName(peerName);
                            node.setID(peerID);

                            // Check if first node to join overlay
                            if (bsNodeStubs.isEmpty()) {
                                node.setZone(0.0, 0.0, Zone.WIDTH_MAX, Zone.HEIGHT_MAX);
                            } else {
                                // Select a random point in (0-10):(0-10) space
                                double randX = Math.random() * 10;
                                double randY = Math.random() * 10;

                                RoutingData r = new RoutingData(randX, randY, nodeStub, "join");

                                NodeInterface finalBSNodeStub = Utils.getNearestNeighbor(randX,
                                        randY,
                                        bsNodeStubs);
                                try {
                                    finalBSNodeStub.sendMessage(r);
                                } catch (RemoteException e) {
                                    System.out.println("ERROR: Failed to send join message");
                                }
                            }
                            System.out.println("Node has joined the can network");
                        }
                        else {
                            System.out.println("Node already on the network");
                        }

                        System.out.println(node.toString());

                        break;

                    case "insert":
                        if (node.getZone() != null) {
                            if (cmd.length == 3) {
                                File file = new File(cmd[2]);
                                if (file.exists()) {
                                    // Calculate x coordinate for keyword
                                    double x = Utils.calcXFromKeyword(cmd[1]);

                                    // Calculate y coordinate for keyword
                                    double y = Utils.calcYFromKeyword(cmd[1]);

                                    RoutingData insertData = new RoutingData(x, y, nodeStub, "insert");
                                    insertData.setFileName(cmd[2]);

                                    // Starting from self
                                    nodeStub.sendMessage(insertData);
                                }
                                else {
                                    System.out.printf("ERROR: File %s does not exist", cmd[2]);
                                }
                            } else {
                                System.out.println("Usage: insert <keyword> <abs_filename>");
                            }
                        }
                        else {
                            System.out.println("Node is not a part of the overlay network yet");
                        }
                        break;

                    case "exit":
                        if (peerID > -1) {
                            try {
                                dnsNodeStub.deregister(peerID);
                            }
                            catch (RemoteException e) {
                                System.out.println("ERROR: DNS server seems to be down");
                            }
                        }
                        exit_flag = true;
                        break;

                    default:
                        System.out.println("ERROR: Command \"" + cmd_wo_args + "\" not recognized.");
                        break;
                }
            }

            System.out.println("\nSystem shutting down!");

            UnicastRemoteObject.unexportObject(node, true);

        }
        catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();

        }
    }
}
