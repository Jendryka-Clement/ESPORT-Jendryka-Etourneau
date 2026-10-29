package fr.imt.arena.personne;

import fr.imt.arena.jeu.Jeu;

import java.util.HashSet;
import java.util.Set;

public class Arbitre extends Personne implements Qualifiable {

    private Set<Jeu> jeux = new HashSet<>(); // HashSet : pas de doublon
    private int matchsArbitres;

    public Arbitre(int id, String nom, String prenom, String pseudo, Genre genre, String telephone, Jeu jeu) {
        super(id, nom, prenom, pseudo, genre, telephone);
        jeux.add(jeu); // un arbitre a au moins un jeu
    }

    public void ajouterJeu(Jeu jeu) {
        jeux.add(jeu);
    }

    @Override
    public boolean estQualifiePour(Jeu jeu) {
        return jeux.contains(jeu);
    }

    public int getMatchsArbitres() { return matchsArbitres; }

    public void ajouterMatch() { matchsArbitres++; }
}
