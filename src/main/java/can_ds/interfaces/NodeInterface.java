package can_ds.interfaces;

import can_ds.nodes.UpdateData;
import can_ds.nodes.ZoneData;
import can_ds.utils.RoutingData;
import can_ds.utils.Zone;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NodeInterface extends Remote {
    int getID() throws RemoteException;
    String getInfo() throws RemoteException;
    void assignZone(ZoneData z) throws RemoteException;
    int sendMessage(RoutingData r) throws RemoteException;
    int sendUpdate(UpdateData updateInfo) throws RemoteException;
    double distToPoint(double px, double py) throws RemoteException;
    boolean isNeighbor(Zone zone, String position) throws RemoteException;
    byte[] downloadFile(String fileName) throws RemoteException;
    void dispPath(String path) throws RemoteException;
    void dispError(String msg) throws RemoteException;
}
