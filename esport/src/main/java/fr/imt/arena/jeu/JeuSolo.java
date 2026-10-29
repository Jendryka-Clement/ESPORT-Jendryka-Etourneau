package fr.imt.arena.jeu;

// une équipe aligne un seul joueur
public class JeuSolo extends Jeu {

    public JeuSolo(String nom, int nbManches) {
        super(nom, nbManches);
    }

    @Override
    public int getNbJoueurs() { return 1; }
}
