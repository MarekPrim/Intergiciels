package carnet;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class IndividuImplRemote extends UnicastRemoteObject implements Individu {

	private static final long serialVersionUID = 1L;

	private Individu individu;
    protected IndividuImplRemote(String nom, int age) throws RemoteException {
        individu = new IndividuImpl(nom, age);
        System.out.println("Constructeur Ind remo");
    }

    @Override
    public String nom() throws RemoteException {
    	System.out.println("getnom Ind remo");
    	return individu.nom();
        
    }

    @Override
    public int age() throws RemoteException  {
    	System.out.println("getage Ind remo");
    	return individu.age();
    }

    @Override
    public void feter_anniversaire() throws RemoteException {
        System.out.println("IndividuImpl.feter_anniversaire");
        System.out.println("Joyeux anniversaire " + individu.nom() + "!");
        individu.feter_anniversaire();;
    }

    @Override
    public String toString()  {
    	System.out.println("tostring Ind remo");
        try {
			return "IndividuImplRemote [nom=" + individu.nom() + ", age=" + individu.age() + "]";
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
    }
    
}