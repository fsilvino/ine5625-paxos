package idgenerator;

import java.rmi.RemoteException;
import common.Constants;
import common.Utils;

public class IDGeneratorImpl implements IDGenerator {

    private int currentId;

    protected IDGeneratorImpl() {
        this.currentId = 0;
    }

    @Override
    public synchronized int proposal_number() throws RemoteException {
        int n = ++this.currentId; 
        Utils.printFormat("Proposal number served: %d", n);
        return n;
    }

    public static void main(String[] args) {
        Utils.initSecurityManager();
        try {
            IDGenerator generator = new IDGeneratorImpl();
            Utils.bindObject(generator, Constants.IDGENERATOR_NAME);
            Utils.printFormat("%s bound", Constants.IDGENERATOR_NAME);
        } catch (Exception e) {
            Utils.printFormat("%s exception:", Constants.IDGENERATOR_NAME);
            e.printStackTrace();
        }
    }

}