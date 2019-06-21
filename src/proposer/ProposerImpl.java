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
		
		System.out.println("-----------------------------------------------------------------");
		System.out.println(String.format("Receiving request from client: %s", this.clientName));
		System.out.println(String.format("Proposal number: %d", n));
		System.out.println(String.format("Value: %d", v));
		System.out.println("-----------------------------------------------------------------");
		
		for	(String acceptorName: acceptors) {
			System.out.println(String.format("Sending to the acceptor: %s...", acceptorName));
			Acceptor acceptor = this.getAcceptor(acceptorName);
			acceptor.prepare_request(this.proposal);
		}
		
		System.out.println("-----------------------------------------------------------------");
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
			System.out.println(String.format("Received prepare_response from acceptor: %s", response.getAcceptorName()));
			
			this.acceptedProposals.add(response);
			
			if (this.acceptedProposals.size() >= (this.acceptors.size() / 2) + 1) {
				System.out.println("Received prepare_response from majority of acceptors. Sending accept...");
				sendAccept();
			}
		} else {
			System.out.println("Duplicated prepare_response received. Ignoring...");
		}
	}
	
	private void sendAccept() throws RemoteException {
		
		System.out.println("Finding the major proposal number from received prepare responses...");
		Response response = this.acceptedProposals.get(0);
		for	(Response r: this.acceptedProposals) {
			if (r.getResponseProposalNumber() > response.getResponseProposalNumber()) {
				response = r;
			}
		}
		
		if (response.getResponseProposalNumber() == Integer.MIN_VALUE) {
			System.out.println("No one proposal number found from prepare responses. Assuming original proposal...");
			response.setResponseProposalNumber(this.proposal.getProposalNumber());
			response.setValue(this.proposal.getValue());
		}
		
		Proposal proposalToAccept = new Proposal(this.proposerName,
												 response.getResponseProposalNumber(),
												 response.getValue());
		
		System.out.println("Sending accept_request to the acceptors.");
		System.out.println(String.format("Proposal number: %d", proposalToAccept.getProposalNumber()));
		System.out.println(String.format("Value: %d", proposalToAccept.getValue()));
		
		for	(String acceptorName: acceptors) {
			getAcceptor(acceptorName).accept_request(proposalToAccept);
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
			
			System.out.println(proposerName + " bound");
		} catch (Exception e) {
			System.err.println(proposerName + " exception:");
			e.printStackTrace();
		}
	}

	@Override
	public void learned(LearnedValue learnedValue) throws RemoteException {
		try {
			Client client = (Client) Utils.getRemoteObject(this.clientName);
			client.receive_result(learnedValue.getValue());
		} catch (NotBoundException e) {
			e.printStackTrace();
			throw new RemoteException("Cloud not find the client: " + this.clientName, e);
		}
	}

}
