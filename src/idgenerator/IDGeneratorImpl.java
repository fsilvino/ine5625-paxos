package idgenerator;
import java.rmi.RemoteException;
import common.Constants;
import common.Utils;

public class IDGeneratorImpl implements IDGenerator {
	
	private int currentId;
	
	protected IDGeneratorImpl() {
		super();
		this.currentId = 0;
	}

	@Override
	public synchronized int proposal_number() throws RemoteException {
		int n = ++this.currentId; 
		System.out.println("Proposal number served: " + n);
		return n;
	}

	public static void main(String[] args) {
		Utils.initSecurityManager();
		try {
			IDGenerator generator = new IDGeneratorImpl();
			Utils.bindObject(generator, Constants.IDGENERATOR_NAME);
			System.out.println("IDGenerator bound");
		} catch (Exception e) {
			System.err.println("IDGenerator exception:");
			e.printStackTrace();
		}
	}

}