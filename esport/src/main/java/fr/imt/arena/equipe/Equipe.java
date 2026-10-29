package fr.imt.arena.equipe;

import fr.imt.arena.personne.Coach;
import fr.imt.arena.personne.Joueur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Equipe implements Comparable<Equipe> {

    private String nom;
    private Coach coach;
    private List<Joueur> joueurs = new ArrayList<>();
    private int points;

    public Equipe(String nom, Coach coach) {
        if (coach.getEquipe() != null) {
            throw new IllegalStateException(coach.getPseudo() + " a déjà une équipe");
        }
        this.nom = nom;
        this.coach = coach;
        coach.setEquipe(this);
    }

    public String getNom() { return nom; }
    public Coach getCoach() { return coach; }
    public int getPoints() { return points; }

    // lecture seule : pour ajouter un joueur il faut passer par ajouterJoueur()
    public List<Joueur> getJoueurs() { return Collections.unmodifiableList(joueurs); }

    public void ajouterJoueur(Joueur joueur) {
        if (joueur.getEquipe() != null) {
            throw new IllegalStateException(joueur.getPseudo() + " est déjà dans " + joueur.getEquipe().getNom());
        }
        joueurs.add(joueur);
        joueur.setEquipe(this);
    }

    // une victoire = 3 points, et on prévient le coach et les joueurs
    public void ajouterVictoire() {
        points = points + 3;
        coach.ajouterVictoire();
        for (Joueur j : joueurs) {
            j.ajouterVictoire();
        }
    }

    // pour le classement : plus de points d'abord, sinon ordre alphabétique
    @Override
    public int compareTo(Equipe autre) {
        if (this.getPoints() != autre.getPoints()) {
            return autre.getPoints() - this.getPoints();
        }
        return this.nom.compareTo(autre.nom);
    }

    @Override
    public String toString() { return nom; }
}
