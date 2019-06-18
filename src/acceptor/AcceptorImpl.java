package acceptor;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import common.Utils;

public class AcceptorImpl implements Acceptor {
	
	private ArrayList<String> learners;

	public AcceptorImpl(Document configFile) {
		learners = new ArrayList<>();
		NodeList learnersNodeList = configFile.getElementsByTagName("learner");
		for (int i = 0; i < learnersNodeList.getLength(); i++) {
			learners.add(learnersNodeList.item(i).getTextContent());
		}
	}
	
	@Override
	public void prepare_request(int n, int v) throws RemoteException {
		
	}

	@Override
	public void accept_request(int n, int v) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		Utils.initSecurityManager();
		String acceptorName = "Acceptor";
		try {
			Document docConfigFile = Utils.readConfiguration(args);
			Acceptor acceptor = new AcceptorImpl(docConfigFile);
			acceptorName = docConfigFile.getElementsByTagName("acceptorName").item(0).getTextContent();
			Utils.bindObject(acceptor, acceptorName);
			System.out.println(acceptorName + " bound");
		} catch (Exception e) {
			System.err.println(acceptorName + " exception:");
			e.printStackTrace();
		}
	}

}
