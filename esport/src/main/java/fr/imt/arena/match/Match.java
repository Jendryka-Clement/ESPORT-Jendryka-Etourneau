package fr.imt.arena.match;

import fr.imt.arena.equipe.Equipe;
import fr.imt.arena.jeu.Jeu;
import fr.imt.arena.personne.Arbitre;
import fr.imt.arena.personne.Joueur;

import java.util.List;
import java.util.Random;

public class Match {

    private Equipe equipe1;
    private Equipe equipe2;
    private Jeu jeu;
    private Arbitre arbitre;
    private StatutMatch statut;
    private Equipe vainqueur;
    private Joueur joueur1; // seulement pour un jeu solo
    private Joueur joueur2;

    public Match(Equipe equipe1, Equipe equipe2, Jeu jeu, Arbitre arbitre) {
        if (equipe1 == equipe2) {
            throw new IllegalArgumentException("Une équipe ne peut pas jouer contre elle-même");
        }
        if (!arbitre.estQualifiePour(jeu)) {
            throw new IllegalArgumentException(arbitre.getPseudo() + " ne peut pas arbitrer " + jeu.getNom());
        }
        this.equipe1 = equipe1;
        this.equipe2 = equipe2;
        this.jeu = jeu;
        this.arbitre = arbitre;
        this.statut = StatutMatch.PREVU;
    }

    // 10 % de chance d'incident, sinon c'est la stratégie qui choisit le vainqueur
    public void jouer(Random random, StrategieVainqueur strategie) {
        if (jeu.getNbJoueurs() == 1) {
            joueur1 = tirerJoueur(equipe1, random);
            joueur2 = tirerJoueur(equipe2, random);
        }
        if (random.nextInt(100) < 10) {
            statut = StatutMatch.ANNULE;
        } else {
            vainqueur = strategie.choisir(equipe1, equipe2, random);
            statut = StatutMatch.JOUE;
        }
    }

    // en jeu solo, on désigne un joueur de l'équipe au hasard
    private Joueur tirerJoueur(Equipe equipe, Random random) {
        List<Joueur> joueurs = equipe.getJoueurs();
        return joueurs.get(random.nextInt(joueurs.size()));
    }

    public Equipe getEquipe1() { return equipe1; }
    public Equipe getEquipe2() { return equipe2; }
    public Jeu getJeu() { return jeu; }
    public Arbitre getArbitre() { return arbitre; }
    public StatutMatch getStatut() { return statut; }
    public Equipe getVainqueur() { return vainqueur; }

    @Override
    public String toString() {
        String texte = equipe1.getNom() + " - " + equipe2.getNom();
        if (joueur1 != null) {
            texte = equipe1.getNom() + " (" + joueur1.getPseudo() + ") - " + equipe2.getNom() + " (" + joueur2.getPseudo() + ")";
        }
        if (statut == StatutMatch.JOUE) {
            return texte + " : " + vainqueur.getNom() + " gagne";
        } else if (statut == StatutMatch.ANNULE) {
            return texte + " : annulé (problème technique)";
        } else {
            return texte + " : pas encore joué";
        }
    }
}
