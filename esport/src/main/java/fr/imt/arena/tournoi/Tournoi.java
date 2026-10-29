package fr.imt.arena.tournoi;

import fr.imt.arena.equipe.Equipe;
import fr.imt.arena.jeu.Jeu;
import fr.imt.arena.match.Match;
import fr.imt.arena.match.MatchListener;
import fr.imt.arena.match.StatutMatch;
import fr.imt.arena.match.StrategieVainqueur;
import fr.imt.arena.personne.Arbitre;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// Comparable : les tournois sont triés par date (utilisé par la PriorityQueue de l'Arena)
public class Tournoi implements Comparable<Tournoi> {

    private String nom;
    private Jeu jeu;
    private LocalDate date;
    private double budget;
    private int nbArenes;
    private List<Equipe> equipes = new ArrayList<>();
    private List<Arbitre> arbitres = new ArrayList<>();
    private List<Match> matchs = new ArrayList<>();
    private StatutTournoi statut;
    private Equipe vainqueur;
    private RapportTournoi rapport;

    public Tournoi(String nom, Jeu jeu, LocalDate date, double budget, int nbArenes) {
        if (nbArenes < 1 || nbArenes > 4) {
            throw new IllegalArgumentException("Il faut entre 1 et 4 arènes");
        }
        this.nom = nom;
        this.jeu = jeu;
        this.date = date;
        this.budget = budget;
        this.nbArenes = nbArenes;
        this.statut = StatutTournoi.PLANIFIE;
    }

    public void inscrire(Equipe equipe) {
        if (statut != StatutTournoi.PLANIFIE) {
            throw new IllegalStateException("Le tournoi a déjà commencé");
        }
        if (equipes.contains(equipe)) {
            throw new IllegalArgumentException(equipe.getNom() + " est déjà inscrite");
        }
        if (equipe.getJoueurs().size() < jeu.getNbJoueurs()) {
            throw new IllegalArgumentException(equipe.getNom() + " n'a pas assez de joueurs pour " + jeu.getNom());
        }
        equipes.add(equipe);
    }

    public void ajouterArbitre(Arbitre arbitre) {
        if (!arbitre.estQualifiePour(jeu)) {
            throw new IllegalArgumentException(arbitre.getPseudo() + " ne peut pas arbitrer " + jeu.getNom());
        }
        arbitres.add(arbitre);
    }

    // chaque équipe rencontre chaque autre une fois : n x (n - 1) / 2 matchs
    public static int nombreDeMatchs(int nbEquipes) {
        return nbEquipes * (nbEquipes - 1) / 2;
    }

    // joue tout le tournoi : vérifications, matchs, puis fin automatique
    public RapportTournoi simuler(Random random, StrategieVainqueur strategie, List<MatchListener> ecouteurs) {
        if (statut != StatutTournoi.PLANIFIE) {
            throw new IllegalStateException(nom + " a déjà été joué");
        }
        if (equipes.size() < 4) {
            throw new IllegalStateException(nom + " : il faut au moins 4 équipes");
        }
        if (arbitres.isEmpty()) {
            throw new IllegalStateException(nom + " : il faut au moins un arbitre");
        }
        statut = StatutTournoi.EN_COURS;

        // chaque équipe joue une fois contre chaque autre, les arbitres tournent
        int numero = 0;
        for (int i = 0; i < equipes.size(); i++) {
            for (int j = i + 1; j < equipes.size(); j++) {
                Arbitre arbitre = arbitres.get(numero % arbitres.size());
                Match match = new Match(equipes.get(i), equipes.get(j), jeu, arbitre);
                match.jouer(random, strategie);
                matchs.add(match);
                // on prévient les observateurs (pattern Observateur)
                for (MatchListener ecouteur : ecouteurs) {
                    ecouteur.matchTermine(match);
                }
                numero++;
            }
        }

        return terminer();
    }

    // fin automatique : points, victoires, matchs arbitrés, vainqueur, rapport
    private RapportTournoi terminer() {
        for (Match m : matchs) {
            if (m.getStatut() == StatutMatch.JOUE) {
                m.getVainqueur().ajouterVictoire();
                m.getArbitre().ajouterMatch();
            }
        }

        int maxVictoires = -1;
        for (Equipe e : equipes) {
            int victoires = compterVictoires(e);
            if (victoires > maxVictoires) {
                maxVictoires = victoires;
                vainqueur = e;
            }
        }

        rapport = new RapportTournoi(nom, date, jeu.getNom(), matchs.size(), getNbMatchsJoues(),
                vainqueur.getNom(), estReussi());
        statut = StatutTournoi.TERMINE;
        return rapport;
    }

    private int compterVictoires(Equipe equipe) {
        int n = 0;
        for (Match m : matchs) {
            if (m.getVainqueur() == equipe) {
                n++;
            }
        }
        return n;
    }

    public int getNbMatchsJoues() {
        int n = 0;
        for (Match m : matchs) {
            if (m.getStatut() == StatutMatch.JOUE) {
                n++;
            }
        }
        return n;
    }

    // réussi si au moins 70 % des matchs ont été joués
    public boolean estReussi() {
        return getNbMatchsJoues() >= 0.7 * matchs.size();
    }

    // matchs joués, regroupés par équipe gagnante (LinkedHashMap : garde l'ordre d'arrivée)
    public Map<Equipe, List<Match>> getMatchsParVainqueur() {
        Map<Equipe, List<Match>> resultat = new LinkedHashMap<>();
        for (Match m : matchs) {
            if (m.getStatut() == StatutMatch.JOUE) {
                if (!resultat.containsKey(m.getVainqueur())) {
                    resultat.put(m.getVainqueur(), new ArrayList<>());
                }
                resultat.get(m.getVainqueur()).add(m);
            }
        }
        return resultat;
    }

    public String getNom() { return nom; }
    public Jeu getJeu() { return jeu; }
    public LocalDate getDate() { return date; }
    public double getBudget() { return budget; }
    public int getNbArenes() { return nbArenes; }
    public StatutTournoi getStatut() { return statut; }
    public Equipe getVainqueur() { return vainqueur; }
    public RapportTournoi getRapport() { return rapport; }
    public List<Equipe> getEquipes() { return Collections.unmodifiableList(equipes); }
    public List<Arbitre> getArbitres() { return Collections.unmodifiableList(arbitres); }
    public List<Match> getMatchs() { return Collections.unmodifiableList(matchs); }

    @Override
    public int compareTo(Tournoi autre) {
        return this.date.compareTo(autre.date);
    }

    @Override
    public String toString() {
        return nom + " (" + jeu.getNom() + ", " + date + ", " + budget + " €)";
    }
}
