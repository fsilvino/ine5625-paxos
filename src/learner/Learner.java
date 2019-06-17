package learner;

public interface Learner extends java.rmi.Remote {

	void accepted(int v) throws java.rmi.RemoteException;
	
}
