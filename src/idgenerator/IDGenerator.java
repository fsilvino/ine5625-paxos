package idgenerator;

public interface IDGenerator extends java.rmi.Remote {

    int proposal_number() throws java.rmi.RemoteException;

}
