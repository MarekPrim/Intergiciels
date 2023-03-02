package carnet;

import java.io.Serializable;

public class IndividuImpl implements Individu, Serializable{

    private static final long serialVersionUID = 1L;
    private String nom;
    private int age;

    public IndividuImpl(String nom, int age) {
    	System.out.println("constructeur Ind");
        this.nom = nom;
        this.age = age;
    }

    @Override
    public String nom() {
    	System.out.println("getnom Ind");
        return this.nom;
    }

    @Override
    public int age() {
    	System.out.println("getage Ind ");
        return this.age;
    }

    @Override
    public void feter_anniversaire() {
        System.out.println("IndividuImpl.feter_anniversaire");
        System.out.println("Joyeux anniversaire " + this.nom + "!");
        this.age++;
    }

    @Override
    public String toString() {
    	System.out.println("tostring Ind");
        return "IndividuImpl [nom=" + nom + ", age=" + age + "]";
    }
    
}
