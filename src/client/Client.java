package client;

public interface Client extends java.rmi.Remote {

	void receive_result(String proposerName, int v) throws java.rmi.RemoteException;
	
}
