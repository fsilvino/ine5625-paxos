package proposer;

import java.rmi.RemoteException;
import java.util.ArrayList;

import common.Constants;
import common.Utils;
import acceptor.Acceptor;

public class ProposerImpl implements Proposer {
	
	private ArrayList<Acceptor> acceptors;
	
	public ProposerImpl() {
		acceptors = new ArrayList<>();
	}

	@Override
	public void request(int v) throws RemoteException {
		int n;
		
		try {
			n = Utils.getProposalNumber();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException("Could not generate the proposal number", e);
		}
		
		for	(Acceptor acceptor: acceptors) {
			acceptor.prepare_request(n, v);
		}
	}
	
	@Override
	public void prepare_response(int n, int v) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		Utils.initSecurityManager();
		try {
			Proposer proposer = new ProposerImpl();
			Utils.bindObject(proposer, Constants.PROPOSER_NAME);
			System.out.println("Proposer bound");
		} catch (Exception e) {
			System.err.println("Proposer exception:");
			e.printStackTrace();
		}
	}

}
