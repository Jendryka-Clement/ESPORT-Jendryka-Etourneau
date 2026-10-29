package fr.imt.arena.personne;

import fr.imt.arena.equipe.Equipe;

public class Coach extends Personne {

    private Equipe equipe;
    private int victoiresCarriere;

    public Coach(int id, String nom, String prenom, String pseudo, Genre genre, String telephone) {
        super(id, nom, prenom, pseudo, genre, telephone);
    }

    public Equipe getEquipe() { return equipe; }
    public int getVictoiresCarriere() { return victoiresCarriere; }

    public void setEquipe(Equipe equipe) { this.equipe = equipe; }

    public void ajouterVictoire() { victoiresCarriere++; }
}
