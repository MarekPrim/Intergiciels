package carnet;

import java.rmi.RemoteException;

interface CallbackOnCreation extends java.rmi.Remote {

    /** Un individu a été créé. */
    public void eventCreated(Individu i) throws RemoteException;
}
