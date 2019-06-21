package acceptor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import org.w3c.dom.Document;

import common.AcceptedProposal;
import common.Proposal;
import common.Response;
import common.Utils;
import learner.Learner;
import proposer.Proposer;

public class AcceptorImpl implements Acceptor {
	
	private ArrayList<String> learners;
	private String acceptorName;
	private int currentProposalNumber;
	private int currentValue;

	public AcceptorImpl(String acceptorName, Document configFile) {
		this.learners = new ArrayList<>();
		this.currentProposalNumber = Integer.MIN_VALUE;
		this.currentValue = Integer.MIN_VALUE;
		this.acceptorName = acceptorName;
		
		Utils.readProcessList(configFile, "learner", learners);
	}
	
	@Override
	public void prepare_request(Proposal proposal) throws RemoteException {
		if (proposal.getProposalNumber() > this.currentProposalNumber) {
			
			System.out.println("Received prepare_request with major number.");
			
			Response response = new Response(this.acceptorName, proposal.getProposalNumber(), this.currentProposalNumber, this.currentValue);
			this.respondToProposer(proposal.getProposerName(), response);
			
			if (this.currentProposalNumber == Integer.MIN_VALUE) {
				this.currentValue = proposal.getValue();
			}
			
			this.currentProposalNumber = proposal.getProposalNumber();
		} else {
			System.out.println("Ignoring prepare_request from a proposal with minor number.");
		}
	}
	
	private void respondToProposer(String proposerName, Response response) throws RemoteException {
		
		if (response.getResponseProposalNumber() == Integer.MIN_VALUE) {
			System.out.println("Responding to the proposer with [no previous]...");
		} else {
			System.out.println("Responding to the proposer with previous proposal that has received first...");
		}
		
		Proposer proposer = getProposer(proposerName);
		proposer.prepare_response(response);
	}
	
	// M�todo para facilitar a obten��o do objeto remoto proposer pelo nome
	private Proposer getProposer(String proposerName) throws RemoteException {
		try {
			return (Proposer) Utils.getRemoteObject(proposerName);
		} catch (NotBoundException e) {
			e.printStackTrace();
			throw new RemoteException("Could not find the proposer: " + proposerName, e);
		}
	}
	
	// M�todo para facilitar a obten��o do objeto remoto learner pelo nome
	private Learner getLearner(String learnerName) throws RemoteException {
		try {
			return (Learner) Utils.getRemoteObject(learnerName);
		} catch (NotBoundException e) {
			e.printStackTrace();
			throw new RemoteException("Could not find the learner.", e);
		}
	}

	@Override
	public void accept_request(Proposal proposal) throws RemoteException {
		if (proposal.getProposalNumber() >= this.currentProposalNumber) {
			
			System.out.println("Received accept_request with a major number. Forwarding to the learners...");
			
			AcceptedProposal acceptedProposal = new AcceptedProposal(this.acceptorName, proposal.getProposalNumber(), proposal.getValue());
			for (String learnerName: this.learners) {
				System.out.println(String.format("Sending accept to the learner: %s...", learnerName));
				Learner learner = this.getLearner(learnerName);
				learner.accepted(acceptedProposal);
			}
		} else {
			System.out.println("Ignoring accept_request from a proposal with minor number.");
		}
	}
	
	public static void main(String[] args) {
		Utils.initSecurityManager();
		String acceptorName = "Acceptor";
		try {
			Document docConfigFile = Utils.readConfiguration(args);
			acceptorName = docConfigFile.getElementsByTagName("acceptorName").item(0).getTextContent();
			
			Acceptor acceptor = new AcceptorImpl(acceptorName, docConfigFile);
			Utils.bindObject(acceptor, acceptorName);
			
			System.out.println(acceptorName + " bound");
		} catch (Exception e) {
			System.err.println(acceptorName + " exception:");
			e.printStackTrace();
		}
	}

}
