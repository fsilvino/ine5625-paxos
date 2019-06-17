package common;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import idgenerator.IDGenerator;

public class Utils {
	
	private Utils() {
		
	}
	
    public static void initSecurityManager() {
    	if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
	}
    
    @SuppressWarnings("unchecked")
	public static <IRemote extends java.rmi.Remote> void bindObject(IRemote obj, String objName) throws RemoteException {
    	IRemote stub = (IRemote) UnicastRemoteObject.exportObject(obj, 0);
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind(objName, stub);
    }
    
    @SuppressWarnings("unchecked")
	public static <IRemote extends java.rmi.Remote> IRemote getRemoteObject(String objName) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry();
        return (IRemote) registry.lookup(objName);
    }
    
    public static int getProposalNumber() throws RemoteException, NotBoundException {
		IDGenerator generator = Utils.getRemoteObject(Constants.IDGENERATOR_NAME);
		return generator.proposal_number();
    }

}
