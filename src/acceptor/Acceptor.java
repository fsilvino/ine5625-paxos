package acceptor;

public interface Acceptor extends java.rmi.Remote {
	
	void prepare_request(int n, int v) throws java.rmi.RemoteException;

}
