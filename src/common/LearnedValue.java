package common;

public class LearnedValue implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private int value;
	
	public LearnedValue(int value) {
		this.setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}
