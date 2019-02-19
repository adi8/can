package can_ds.utils;

import can_ds.interfaces.NodeInterface;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.List;

public class Utils {

    public static String getAddress() {
        String ipAddress = "";
        try {
            for (final Enumeration<NetworkInterface> interfaces
                 = NetworkInterface.getNetworkInterfaces();
                 interfaces.hasMoreElements();)
            {
                final NetworkInterface cur = interfaces.nextElement();

                if ( cur.isLoopback() )
                    continue;

                if (!(cur.getDisplayName().startsWith("w") || cur.getDisplayName().startsWith("e")))
                    continue;

                for (final InterfaceAddress addr : cur.getInterfaceAddresses()) {
                    final InetAddress inetAddr = addr.getAddress();

                    if (!(inetAddr instanceof Inet4Address))
                        continue;

                    ipAddress = inetAddr.getHostAddress();
                }

            }
        }
        catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
            e.printStackTrace();
        }

        return ipAddress;
    }

    public static NodeInterface getNearestNeighbor(double px,
                                                   double py,
                                                   List<NodeInterface> nodeStubs)
    {
        NodeInterface retNodeStub = null;
        double min = 9999;
        for (NodeInterface nodeStub : nodeStubs) {
            try {
                double dist = nodeStub.distToPoint(px, py);
                if (dist < min) {
                    min = dist;
                    retNodeStub = nodeStub;
                }
            }
            catch (RemoteException e) {
                System.out.println("ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return retNodeStub;
    }

    public static double calcXFromKeyword(String keyword) {
        int i = 1;
        double x = 0.0;
        for (i = 1; i < keyword.length(); i += 2) {
            x += Character.getNumericValue(keyword.charAt(i));
        }

        return x % 10;
    }

    public static double calcYFromKeyword(String keyword) {
        int i = 0;
        double y = 0.0;
        for (i = 0; i < keyword.length(); i += 2) {
            y += Character.getNumericValue(keyword.charAt(i));
        }

        return y % 10;
    }

}
