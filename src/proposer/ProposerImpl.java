package proposer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import common.Utils;
import acceptor.Acceptor;

public class ProposerImpl implements Proposer {
	
	private ArrayList<String> acceptors;
	
	public ProposerImpl(Document configFile) {
		acceptors = new ArrayList<>();
		NodeList acceptorsNodeList = configFile.getElementsByTagName("acceptor");
		for (int i = 0; i < acceptorsNodeList.getLength(); i++) {
			acceptors.add(acceptorsNodeList.item(i).getTextContent());
		}
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
		
		for	(String acceptorName: acceptors) {
			getAcceptor(acceptorName).prepare_request(n, v);
		}
	}
	
	private Acceptor getAcceptor(String acceptorName) throws RemoteException {
		try {
			return (Acceptor) Utils.getRemoteObject(acceptorName);
		} catch (NotBoundException e) {
			e.printStackTrace();
			throw new RemoteException("Could not find the acceptor.", e);
		}
	}
	
	@Override
	public void prepare_response(int n, int v) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		Utils.initSecurityManager();
		String proposerName = "Proposer";
		try {
			Document docConfigFile = Utils.readConfiguration(args);
			Proposer proposer = new ProposerImpl(docConfigFile);
			proposerName = docConfigFile.getElementsByTagName("proposerName").item(0).getTextContent();
			Utils.bindObject(proposer, proposerName);
			System.out.println(proposerName + " bound");
		} catch (Exception e) {
			System.err.println(proposerName + " exception:");
			e.printStackTrace();
		}
	}

}
