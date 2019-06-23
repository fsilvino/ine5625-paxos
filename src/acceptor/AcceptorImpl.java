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

            Utils.print("Received prepare_request with major number.");

            Response response = new Response(this.acceptorName, proposal.getProposalNumber(), this.currentProposalNumber, this.currentValue);
            this.respondToProposer(proposal.getProposerName(), response);

            if (this.currentProposalNumber == Integer.MIN_VALUE) {
                this.currentValue = proposal.getValue();
            }

            this.currentProposalNumber = proposal.getProposalNumber();
        } else {
            Utils.print("Ignoring prepare_request from a proposal with minor number.");
        }
    }

    private void respondToProposer(String proposerName, Response response) {
        if (response.getResponseProposalNumber() == Integer.MIN_VALUE) {
            Utils.print("Responding to the proposer with [no previous]...");
        } else {
            Utils.print("Responding to the proposer with previous proposal that has received first...");
        }

        try {
            Proposer proposer = getProposer(proposerName);
            proposer.prepare_response(response);
        } catch (RemoteException e) {
            Utils.print("Failed to respond to the proposer!");
            e.printStackTrace();
        }
    }

    // Método para facilitar a obtenção do objeto remoto proposer pelo nome
    private Proposer getProposer(String proposerName) throws RemoteException {
        try {
            return (Proposer) Utils.getRemoteObject(proposerName);
        } catch (NotBoundException e) {
            e.printStackTrace();
            throw new RemoteException("Could not find the proposer: " + proposerName, e);
        }
    }

    // Método para facilitar a obtenção do objeto remoto learner pelo nome
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
            Utils.print("Received accept_request with a major number.");
            forwardAcceptToLearners(proposal);
        } else {
            Utils.print("Ignoring accept_request from a proposal with minor number.");
        }
    }

    private void forwardAcceptToLearners(Proposal proposal) throws RemoteException {

        new Thread() {

            @Override
            public void run() {

                Utils.print("Forwarding to the learners...");

                AcceptedProposal acceptedProposal = new AcceptedProposal(acceptorName, proposal.getProposalNumber(), proposal.getValue());

                try {
                    for (String learnerName: learners) {
                        Utils.print(String.format("Sending accept to the learner: %s...", learnerName));
                        Learner learner = getLearner(learnerName);
                        learner.accepted(acceptedProposal);
                    }
                } catch (RemoteException e) {
                    Utils.print("Failed to forward accept to the learners!");
                    e.printStackTrace();
                }

            }

        }.start();

    }

    public static void main(String[] args) {
        Utils.initSecurityManager();
        String acceptorName = "Acceptor";
        try {
            Document docConfigFile = Utils.readConfiguration(args);
            acceptorName = docConfigFile.getElementsByTagName("acceptorName").item(0).getTextContent();

            Acceptor acceptor = new AcceptorImpl(acceptorName, docConfigFile);
            Utils.bindObject(acceptor, acceptorName);

            Utils.print(acceptorName + " bound");
        } catch (Exception e) {
            Utils.print(acceptorName + " exception:");
            e.printStackTrace();
        }
    }

}
