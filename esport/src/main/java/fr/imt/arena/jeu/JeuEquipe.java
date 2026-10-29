package fr.imt.arena.jeu;

// une équipe aligne entre 3 et 5 joueurs
public class JeuEquipe extends Jeu {

    private int nbJoueurs;

    public JeuEquipe(String nom, int nbManches, int nbJoueurs) {
        super(nom, nbManches);
        if (nbJoueurs < 3 || nbJoueurs > 5) {
            throw new IllegalArgumentException("Un jeu en équipe se joue de 3 à 5 joueurs");
        }
        this.nbJoueurs = nbJoueurs;
    }

    @Override
    public int getNbJoueurs() { return nbJoueurs; }
}
