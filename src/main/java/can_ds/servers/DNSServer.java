package can_ds.servers;

import can_ds.interfaces.DNSNodeInterface;
import can_ds.nodes.DNSNode;
import can_ds.utils.Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class DNSServer {
    public static void main(String[] args) {
        DNSNode dnsNode = new DNSNode();

        try {
            DNSNodeInterface dnsNodeStub =
                    (DNSNodeInterface) UnicastRemoteObject.exportObject(dnsNode, 0);
            Registry registry = null;
            try {
                registry = LocateRegistry.getRegistry();
                registry.bind(DNSNode.DNS_URL, dnsNodeStub);
            }
            catch (Exception e) {
                System.out.println("RMI registry server is not up. Try again!");
                System.exit(1);
            }

            String ipAddress = Utils.getAddress();
            String name = "dns";

            String startMsg = "DNS Node Ready\n" +
                              "+++++++++++++++++++++++\n" +
                              "IP Address: " + ipAddress + "\n" +
                              "Name      : " + name + "\n" +
                              "+++++++++++++++++++++++\n";
            System.out.println(startMsg);

            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);

            boolean exit_flag = false;
            while (!exit_flag) {
                System.out.print(name + " > ");

                String[] cmd = br.readLine().trim().split(" ");

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
                            System.out.println(dnsNode.dispNodeInfo());
                        }
                        break;

                    case "exit":
                        exit_flag = true;
                        break;

                    default:
                        System.out.println("ERROR: Command \"" +
                                           cmd_wo_args +
                                           "\" not recognized.");
                        break;
                }

            }

            System.out.println("\nSystem shutting down!");

            registry.unbind("can://nodes/dns");

            UnicastRemoteObject.unexportObject(dnsNode, true);
        }
        catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
