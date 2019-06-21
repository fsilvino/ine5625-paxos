package learner;

import common.AcceptedProposal;

public interface Learner extends java.rmi.Remote {

	void accepted(AcceptedProposal acceptedProposal) throws java.rmi.RemoteException;
	
}
