package carnet;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** Interface Individu. */
public interface Individu extends Remote{
    /** Renvoie le nom de l'individu. 
     * @throws RemoteException */
    String nom() throws RemoteException;

    /** Renvoie l'age actuel de l'individu. 
     * @throws RemoteException */
    int age() throws RemoteException;

    /** Incrémente l'age actuel de l'individu et affiche à l'écran un message. 
     * @throws RemoteException */
    void feter_anniversaire () throws RemoteException;
}
