package acceptor;

import common.Proposal;

public interface Acceptor extends java.rmi.Remote {

    void prepare_request(Proposal proposal) throws java.rmi.RemoteException;
    void accept_request(Proposal proposal) throws java.rmi.RemoteException;

}
