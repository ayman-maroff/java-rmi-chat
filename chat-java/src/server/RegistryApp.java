package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegistryApp {
    public static void main(String[] args)throws RemoteException {
        try {
            Registry reg = LocateRegistry.createRegistry(8088);
            reg.rebind("chat", new ChatServer());
            System.out.println("Chat RMI Server is running...");
        }  catch(Exception e){
            System.out.println("Server had problems starting");
        }
    }
}
