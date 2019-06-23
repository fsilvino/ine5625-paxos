package proposer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import org.w3c.dom.Document;

import common.LearnedValue;
import common.Proposal;
import common.Response;
import common.Utils;
import acceptor.Acceptor;
import client.Client;

public class ProposerImpl implements Proposer {

    private String proposerName;
    private String clientName;
    private Proposal proposal;
    private ArrayList<String> acceptors;
    private ArrayList<Response> acceptedProposals;

    public ProposerImpl(String proposerName, Document configFile) {
        this.acceptors = new ArrayList<>();
        this.acceptedProposals = new ArrayList<>();
        this.proposerName = proposerName;

        // lê a lista de acceptors do arquivo de configuração
        Utils.readProcessList(configFile, "acceptor", acceptors);
    }

    // Método que o cliente chama, deve passar o seu próprio nome
    // para poder receber o retorno.
    @Override
    public void request(String clientName, int v) throws RemoteException {
        int n;

        try {
            n = Utils.getProposalNumber();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Could not generate the proposal number", e);
        }

        this.clientName = clientName;
        this.proposal = new Proposal(this.proposerName, n, v);

        Utils.printFormat("Receiving request from client: %s", this.clientName);
        Utils.printFormat("Proposal number: %d", n);
        Utils.printFormat("Value: %d", v);

        sendPrepareRequestToAcceptors();
    }

    private void sendPrepareRequestToAcceptors() {
        try {

            for	(String acceptorName: acceptors) {
                Utils.printFormat("Sending to the acceptor: %s...", acceptorName);
                Acceptor acceptor = getAcceptor(acceptorName);
                acceptor.prepare_request(proposal);
            }

        } catch (Exception e) {
            Utils.print("Failed to send prepare_request to the acceptors!");
            e.printStackTrace();
        }
    }

    // Método para facilitar a obtenção do objeto remoto acceptor pelo nome
    private Acceptor getAcceptor(String acceptorName) throws RemoteException {
        try {
            return (Acceptor) Utils.getRemoteObject(acceptorName);
        } catch (NotBoundException e) {
            e.printStackTrace();
            throw new RemoteException("Could not find the acceptor.", e);
        }
    }

    private Response findResponse(Response response) {
        Response resp = null;

        for (Response r: this.acceptedProposals) {
            if (r.getAcceptorName().equals(response.getAcceptorName()) &&
                    r.getRequestedProposalNumber() == response.getRequestedProposalNumber()) {
                resp = r;
                break;
            }
        }

        return resp;
    }


    @Override
    public void prepare_response(Response response) throws RemoteException {
        if (this.findResponse(response) == null) {

            Utils.printFormat("Received prepare_response from acceptor: %s", response.getAcceptorName());

            this.acceptedProposals.add(response);

            if (this.acceptedProposals.size() >= (this.acceptors.size() / 2) + 1) {
                Utils.print("Received prepare_response from majority of acceptors. Sending accept...");
                sendAccept();
            }
        } else {
            Utils.print("Duplicated prepare_response received. Ignoring...");
        }
    }

    private void sendAccept() {

        try {

            Utils.print("Finding the major proposal number from received prepare responses...");

            Response response = acceptedProposals.get(0);
            for	(Response r: acceptedProposals) {
                if (r.getResponseProposalNumber() > response.getResponseProposalNumber()) {
                    response = r;
                }
            }

            if (response.getResponseProposalNumber() == Integer.MIN_VALUE) {
                Utils.print("No one proposal number found from prepare responses. Assuming original proposal...");
                response.setResponseProposalNumber(proposal.getProposalNumber());
                response.setValue(proposal.getValue());
            } else {
                Utils.print("The major proposal number was found.");
            }

            Proposal proposalToAccept = new Proposal(proposerName,
                                                     response.getResponseProposalNumber(),
                                                     response.getValue());

            Utils.print("Sending accept_request to the acceptors.");
            Utils.printFormat("Proposal number: %d", proposalToAccept.getProposalNumber());
            Utils.printFormat("Value: %d", proposalToAccept.getValue());

            for	(String acceptorName: acceptors) {
                getAcceptor(acceptorName).accept_request(proposalToAccept);
            }

        } catch (RemoteException e) {
            Utils.print("Failed to send accept to the acceptors!");
            e.printStackTrace();
        }

    }
    
    @Override
    public void learned(LearnedValue learnedValue) throws RemoteException {

        Utils.printFormat("Learned value received. Value: %d", learnedValue.getValue());

        try {
            Utils.printFormat("Sending to the client: %s...", clientName);
            Client client = (Client) Utils.getRemoteObject(clientName);
            client.receive_result(proposerName, learnedValue.getValue());
        } catch (Exception e) {
            Utils.printFormat("Could not send the value to the client %s!", clientName);
            e.printStackTrace();
        }

    }

    // Inicialização do objeto remoto
    public static void main(String[] args) {
        // Inicializa o Security Manager para o RMI
        Utils.initSecurityManager();

        String proposerName = "Proposer";
        try {
            // Lê o arquivo de configuração recebido por parâmetro na inicialização do programa
            Document docConfigFile = Utils.readConfiguration(args);

            // Cria o proposer e associa ele ao registro RMI
            proposerName = docConfigFile.getElementsByTagName("proposerName").item(0).getTextContent();

            Proposer proposer = new ProposerImpl(proposerName, docConfigFile);
            Utils.bindObject(proposer, proposerName);

            Utils.print(proposerName + " bound");
        } catch (Exception e) {
            Utils.print(proposerName + " exception:");
            e.printStackTrace();
        }
    }

}
