package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Random;

import org.w3c.dom.Document;

import common.Utils;
import proposer.Proposer;

public class ClientImpl implements Client {

    private String clientName;
    private String proposerName;
    private int timeToSleep;

    public ClientImpl(String clientName, Document configFile) {
        this.clientName = clientName;

        this.proposerName = configFile.getElementsByTagName("proposerName").item(0).getTextContent();
        this.timeToSleep = Integer.parseInt(configFile.getElementsByTagName("timeToSleep").item(0).getTextContent());

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(timeToSleep);
                    sendRequestToProposer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void sendRequestToProposer() {
        Random rnd = new Random();
        int v = rnd.nextInt(10000);
        try {
            Utils.printFormat("Sending value %d to proposer %s", v, this.proposerName);
            Proposer proposer = (Proposer) Utils.getRemoteObject(this.proposerName);
            proposer.request(this.clientName, v);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receive_result(String proposerName, int v) throws RemoteException {
        Utils.printFormat("Received value: %d from proposer: %s", v, proposerName);
    }

    public static void main(String[] args) {

        Utils.initSecurityManager();
        String clientName = "Client";
        try {
            Document docConfigFile = Utils.readConfiguration(args);
            clientName = docConfigFile.getElementsByTagName("clientName").item(0).getTextContent();

            Client client = new ClientImpl(clientName, docConfigFile);
            Utils.bindObject(client, clientName);

            Utils.print(clientName + " bound");
        } catch (Exception e) {
            Utils.print(clientName + " exception:");
            e.printStackTrace();
        }

    }

}