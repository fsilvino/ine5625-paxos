package learner;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import common.Utils;

public class LearnerImpl implements Learner {
	
	private ArrayList<String> proposers;

	public LearnerImpl(Document configFile) {
		proposers = new ArrayList<>();
		NodeList proposersNodeList = configFile.getElementsByTagName("proposer");
		for (int i = 0; i < proposersNodeList.getLength(); i++) {
			proposers.add(proposersNodeList.item(i).getTextContent());
		}
	}
	
	@Override
	public void accepted(int v) throws RemoteException {
		
	}
	
	public static void main(String[] args) {
		Utils.initSecurityManager();
		String learnerName = "Learner";
		try {
			Document docConfigFile = Utils.readConfiguration(args);
			Learner learner = new LearnerImpl(docConfigFile);
			learnerName = docConfigFile.getElementsByTagName("learnerName").item(0).getTextContent();
			Utils.bindObject(learner, learnerName);
			System.out.println(learnerName + " bound");
		} catch (Exception e) {
			System.err.println(learnerName + " exception:");
			e.printStackTrace();
		}
	}

}
