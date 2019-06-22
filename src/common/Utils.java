package common;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

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
    
    public static Document readConfiguration(String[] args) throws Exception {
    	if (args.length != 1) {
    		throw new Exception("Invalid arguments.\r\n" +
    							"You must pass configuration file as argument.");
		}
    	
    	File configFile = new File(args[0]);
		if (!(configFile.exists() && configFile.isFile())) {
		    throw new Exception("Configuration file not found.");
		}
    	
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(configFile);
    }
    
    public static void readProcessList(Document configFile, String tagName, ArrayList<String> processList) {
    	NodeList nodeList = configFile.getElementsByTagName(tagName);
		for (int i = 0; i < nodeList.getLength(); i++) {
			processList.add(nodeList.item(i).getTextContent());
		}
    }

	public static void printFormat(String format, Object... args) {
		print(String.format(format, args));
	}
	
	public static void print(String msg) {
		SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss:SSS");
		System.out.println(String.format("%s %s", simple.format(new Date()), msg));
	}

}
