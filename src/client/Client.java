package client;

public interface Client extends java.rmi.Remote {

	void receive_result(int v) throws java.rmi.RemoteException;
	
}
