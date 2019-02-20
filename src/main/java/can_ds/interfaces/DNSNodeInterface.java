package can_ds.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DNSNodeInterface extends Remote {
    int register(NodeInterface nodeStub) throws RemoteException;
    void deregister(int id) throws RemoteException;
    String dispNodeInfo() throws RemoteException;
    List<NodeInterface> getBSNodes() throws RemoteException;
    NodeInterface getNodeStub(int peerID) throws RemoteException;
    String dispNodeInfo(int id) throws RemoteException;
}