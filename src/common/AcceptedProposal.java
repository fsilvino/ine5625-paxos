package common;

public class AcceptedProposal implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private String acceptorName;
	private int proposalNumber;
	private int value;
	
	public AcceptedProposal(String acceptorName, int proposalNumber, int value) {
		this.setAcceptorName(acceptorName);
		this.setProposalNumber(proposalNumber);
		this.setValue(value);
	}
	
	public String getAcceptorName() {
		return acceptorName;
	}
	
	public void setAcceptorName(String acceptorName) {
		this.acceptorName = acceptorName;
	}

	public int getProposalNumber() {
		return proposalNumber;
	}

	public void setProposalNumber(int proposalNumber) {
		this.proposalNumber = proposalNumber;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}
