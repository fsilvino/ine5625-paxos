package proposer;

public interface Proposer extends java.rmi.Remote {

	void request(int v) throws java.rmi.RemoteException;
	
}
