package common;

public class Proposal implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private String proposerName;
    private int proposalNumber;
    private int value;

    public Proposal() {

    }

    public Proposal(String proposerName, int proposalNumber, int value) {
        this.setProposerName(proposerName);
        this.setProposalNumber(proposalNumber);
        this.setValue(value);
    }

    public String getProposerName() {
        return proposerName;
    }

    public void setProposerName(String proposerName) {
        this.proposerName = proposerName;
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
