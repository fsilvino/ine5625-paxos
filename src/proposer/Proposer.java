package proposer;

import common.LearnedValue;
import common.Response;

public interface Proposer extends java.rmi.Remote {

    void request(String clientName, int v) throws java.rmi.RemoteException;
    void prepare_response(Response response) throws java.rmi.RemoteException;
    void learned(LearnedValue learnedValue) throws java.rmi.RemoteException;

}
