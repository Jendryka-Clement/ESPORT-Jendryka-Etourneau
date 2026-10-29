package fr.imt.arena.personne;

import fr.imt.arena.equipe.Equipe;

public class Joueur extends Personne {

    private StatutJoueur statut;
    private Equipe equipe;
    private int victoires; // victoires de l'équipe depuis que le joueur y est

    public Joueur(int id, String nom, String prenom, String pseudo, Genre genre, String telephone) {
        super(id, nom, prenom, pseudo, genre, telephone);
        this.statut = StatutJoueur.AMATEUR;
    }

    public StatutJoueur getStatut() { return statut; }
    public Equipe getEquipe() { return equipe; }

    // appelé par Equipe.ajouterJoueur()
    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
        this.victoires = 0;
    }

    // le joueur passe pro après 3 victoires de son équipe
    public void ajouterVictoire() {
        victoires++;
        if (victoires >= 3) {
            statut = StatutJoueur.PRO;
        }
    }
}
