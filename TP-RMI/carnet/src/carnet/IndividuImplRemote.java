package carnet;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class IndividuImplRemote extends UnicastRemoteObject implements Individu {

	private static final long serialVersionUID = 1L;

	private String nom;
    private int age;

    protected IndividuImplRemote(String nom, int age) throws RemoteException {
        this.nom = nom;
        this.age = age;
        System.out.println("Constructeur Ind remo");
    }

    @Override
    public String nom() throws RemoteException {
    	System.out.println("getnom Ind remo");
    	return this.nom;
        
    }

    @Override
    public int age() throws RemoteException  {
    	System.out.println("getage Ind remo");
        return this.age;
    }

    @Override
    public void feter_anniversaire() throws RemoteException {
        System.out.println("IndividuImpl.feter_anniversaire");
        System.out.println("Joyeux anniversaire " + this.nom + "!");
        this.age++;
    }

    @Override
    public String toString()  {
    	System.out.println("tostring Ind remo");
        return "IndividuImplRemote [nom=" + nom + ", age=" + age + "]";
    }
    
}