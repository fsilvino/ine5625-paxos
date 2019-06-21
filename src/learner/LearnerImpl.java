package learner;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import common.AcceptedProposal;
import common.LearnedValue;
import common.Utils;
import proposer.Proposer;

public class LearnerImpl implements Learner {
	
	private String learnerName;
	private ArrayList<String> proposers;
	private ArrayList<String> acceptors;
	private ArrayList<AcceptedProposal> acceptedProposals;

	public LearnerImpl(String learnerName, Document configFile) {
		this.proposers = new ArrayList<>();
		this.acceptors = new ArrayList<>();
		this.acceptedProposals = new ArrayList<>();
		this.learnerName = learnerName;
		
		// Lê as listas de processos
		Utils.readProcessList(configFile, "proposer", proposers);
		Utils.readProcessList(configFile, "acceptor", acceptors);
	}
	
	private AcceptedProposal findAcceptedProposal(AcceptedProposal acceptedProposal) {
		AcceptedProposal ap = null;
		
		for (AcceptedProposal a: this.acceptedProposals) {
			if (a.getAcceptorName().equals(acceptedProposal.getAcceptorName()) &&
				a.getProposalNumber() == acceptedProposal.getProposalNumber()) {
				ap = a;
				break;
			}
		}
		
		return ap;
	}
	
	@Override
	public void accepted(AcceptedProposal acceptedProposal) throws RemoteException {
		if (findAcceptedProposal(acceptedProposal) == null) {
			System.out.println(String.format("Received accept from acceptor: %s", acceptedProposal.getAcceptorName()));
			
			this.acceptedProposals.add(acceptedProposal);
			
			LearnedValue learnedValue = getLearnedValue();
			if (learnedValue != null) {
				sendLearnedValue(learnedValue);
			}
		} else {
			System.out.println("Duplicated accepted received. Ignoring...");
		}
	}
	
	private LearnedValue getLearnedValue() {
		HashMap<Integer, Integer> acceptedValues = new HashMap<>();
		
		for (AcceptedProposal acceptedProposal: this.acceptedProposals) {
			
			int v = acceptedProposal.getValue();
			int count = 0;
			
			if (acceptedValues.containsKey(v)) {
				count = acceptedValues.get(v);
			}
			
			count += 1;
			
			if (count >= this.acceptors.size() / 2 + 1) {
				return new LearnedValue(v);
			}
			
			acceptedValues.put(v, count);
		}
		
		return null;
	}

	private void sendLearnedValue(LearnedValue learnedValue) throws java.rmi.RemoteException {
		
		System.out.println("Received a value from a majority of the acceptors. Send learned value to the proposers...");
		System.out.println(String.format("Learned value: %d", learnedValue.getValue()));
		
		for (String proposerName: this.proposers) {
			try {
				Proposer proposer = (Proposer) Utils.getRemoteObject(proposerName);
				proposer.learned(learnedValue);
			} catch (NotBoundException e) {
				e.printStackTrace();
				throw new RemoteException("Could not find the proposer: " + proposerName, e);
			}
		}
		
	}
	
	public static void main(String[] args) {
		Utils.initSecurityManager();
		String learnerName = "Learner";
		try {
			// Lê o arquivo de configuração
			Document docConfigFile = Utils.readConfiguration(args);
			learnerName = docConfigFile.getElementsByTagName("learnerName").item(0).getTextContent();
			
			// Cria o learner e associa ele ao registro RMI
			Learner learner = new LearnerImpl(learnerName, docConfigFile);
			Utils.bindObject(learner, learnerName);
			
			System.out.println(learnerName + " bound");
		} catch (Exception e) {
			System.err.println(learnerName + " exception:");
			e.printStackTrace();
		}
	}

}
