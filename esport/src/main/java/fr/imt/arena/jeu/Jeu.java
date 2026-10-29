package fr.imt.arena.jeu;

public abstract class Jeu {

    private String nom;
    private int nbManches;

    public Jeu(String nom, int nbManches) {
        this.nom = nom;
        this.nbManches = nbManches;
    }

    public String getNom() { return nom; }
    public int getNbManches() { return nbManches; }

    // chaque type de jeu donne son nombre de joueurs par équipe
    public abstract int getNbJoueurs();

    @Override
    public String toString() { return nom; }
}
