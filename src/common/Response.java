package common;

public class Response implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	private String acceptorName;
	private int requestedProposalNumber;
	private int responseProposalNumber;
	private int value;
	
	public Response() {
		
	}
	
	public Response(String acceptorName, int requestedProposalNumber, int responseProposalNumber, int value) {
		this.setAcceptorName(acceptorName);
		this.setRequestedProposalNumber(requestedProposalNumber);
		this.setResponseProposalNumber(responseProposalNumber);
		this.setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getResponseProposalNumber() {
		return responseProposalNumber;
	}

	public void setResponseProposalNumber(int responseProposalNumber) {
		this.responseProposalNumber = responseProposalNumber;
	}

	public int getRequestedProposalNumber() {
		return requestedProposalNumber;
	}

	public void setRequestedProposalNumber(int requestedProposalNumber) {
		this.requestedProposalNumber = requestedProposalNumber;
	}

	public String getAcceptorName() {
		return acceptorName;
	}

	public void setAcceptorName(String acceptorName) {
		this.acceptorName = acceptorName;
	}
	
}