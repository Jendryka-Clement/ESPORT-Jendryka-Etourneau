package fr.imt.arena.tournoi;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDate;

// créé automatiquement à la fin d'un tournoi et sauvegardé dans la base H2
@Entity
public class RapportTournoi {

    @Id
    @GeneratedValue
    private Long id;

    private String nomTournoi;
    private LocalDate date;
    private String jeu;
    private int matchsPrevus;
    private int matchsJoues;
    private String vainqueur;
    private boolean reussi;

    // constructeur vide obligatoire pour JPA
    public RapportTournoi() {
    }

    public RapportTournoi(String nomTournoi, LocalDate date, String jeu, int matchsPrevus,
                          int matchsJoues, String vainqueur, boolean reussi) {
        this.nomTournoi = nomTournoi;
        this.date = date;
        this.jeu = jeu;
        this.matchsPrevus = matchsPrevus;
        this.matchsJoues = matchsJoues;
        this.vainqueur = vainqueur;
        this.reussi = reussi;
    }

    public Long getId() { return id; }
    public String getNomTournoi() { return nomTournoi; }
    public LocalDate getDate() { return date; }
    public String getJeu() { return jeu; }
    public int getMatchsPrevus() { return matchsPrevus; }
    public int getMatchsJoues() { return matchsJoues; }
    public String getVainqueur() { return vainqueur; }
    public boolean isReussi() { return reussi; }

    @Override
    public String toString() {
        String resultat = reussi ? "réussi" : "raté";
        return nomTournoi + " (" + jeu + ") : vainqueur " + vainqueur + ", "
                + matchsJoues + "/" + matchsPrevus + " matchs joués, " + resultat;
    }
}
