package fr.imt.arena;

import fr.imt.arena.equipe.Equipe;
import fr.imt.arena.match.Match;
import fr.imt.arena.match.MatchListener;
import fr.imt.arena.match.StrategieVainqueur;
import fr.imt.arena.personne.Arbitre;
import fr.imt.arena.tournoi.ComparateurBudget;
import fr.imt.arena.tournoi.RapportRepository;
import fr.imt.arena.tournoi.RapportTournoi;
import fr.imt.arena.tournoi.Tournoi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeSet;

// le gestionnaire : équipes, arbitres, tournois, et lancement des tournois
public class Arena {

    private Map<String, Equipe> equipes = new HashMap<>();             // accès direct par le nom
    private List<Arbitre> arbitres = new ArrayList<>();
    private PriorityQueue<Tournoi> planifies = new PriorityQueue<>();  // le plus proche en premier
    private List<Tournoi> termines = new ArrayList<>();
    private List<MatchListener> ecouteurs = new ArrayList<>();

    private StrategieVainqueur strategie;
    private Random random;
    private RapportRepository repository; // null = pas de sauvegarde (utile pour les tests)

    public Arena(StrategieVainqueur strategie, long graine) {
        this.strategie = strategie;
        this.random = new Random(graine); // graine fixe : résultat reproductible
    }

    public void setRepository(RapportRepository repository) {
        this.repository = repository;
    }

    // ---------- ajouts ----------

    public void ajouterEquipe(Equipe equipe) {
        if (equipes.containsKey(equipe.getNom())) {
            throw new IllegalArgumentException("L'équipe " + equipe.getNom() + " existe déjà");
        }
        equipes.put(equipe.getNom(), equipe);
    }

    public void ajouterArbitre(Arbitre arbitre) {
        arbitres.add(arbitre);
    }

    public void planifier(Tournoi tournoi) {
        planifies.add(tournoi);
    }

    public void ajouterEcouteur(MatchListener ecouteur) {
        ecouteurs.add(ecouteur);
    }

    // ---------- lancement ----------

    // lance le tournoi planifié le plus proche dans le temps, le joue en entier et renvoie son rapport
    public RapportTournoi lancerSuivant() {
        Tournoi tournoi = planifies.peek();
        if (tournoi == null) {
            throw new IllegalStateException("Aucun tournoi planifié");
        }
        RapportTournoi rapport = tournoi.simuler(random, strategie, ecouteurs); // vérifie 4 équipes et un arbitre
        planifies.poll();
        termines.add(tournoi);
        if (repository != null) {
            repository.sauver(rapport); // sauvegarde automatique en fin de tournoi
        }
        return rapport;
    }

    // ---------- listes ----------

    public Equipe getEquipe(String nom) {
        return equipes.get(nom);
    }

    // tournois planifiés, le plus proche d'abord
    public List<Tournoi> getTournoisPlanifies() {
        List<Tournoi> liste = new ArrayList<>(planifies);
        Collections.sort(liste); // une PriorityQueue n'est pas triée quand on la parcourt
        return liste;
    }

    // tournois terminés, du plus gros au plus petit budget
    public List<Tournoi> getTournoisTermines() {
        List<Tournoi> liste = new ArrayList<>(termines);
        liste.sort(new ComparateurBudget());
        return liste;
    }

    // classement : TreeSet trié par compareTo de Equipe (points puis nom), sans doublon
    public TreeSet<Equipe> getClassement() {
        return new TreeSet<>(equipes.values());
    }

    // un arbitre est occupé s'il est prévu dans un tournoi planifié
    public List<Arbitre> getArbitresOccupes() {
        List<Arbitre> occupes = new ArrayList<>();
        for (Tournoi t : planifies) {
            for (Arbitre a : t.getArbitres()) {
                if (!occupes.contains(a)) {
                    occupes.add(a);
                }
            }
        }
        return occupes;
    }

    public List<Arbitre> getArbitresDisponibles() {
        List<Arbitre> occupes = getArbitresOccupes();
        List<Arbitre> disponibles = new ArrayList<>();
        for (Arbitre a : arbitres) {
            if (!occupes.contains(a)) {
                disponibles.add(a);
            }
        }
        return disponibles;
    }

    // tous les matchs joués par une équipe dans les tournois terminés
    public List<Match> getMatchsEquipe(Equipe equipe) {
        List<Match> resultat = new ArrayList<>();
        for (Tournoi t : termines) {
            for (Match m : t.getMatchs()) {
                if (m.getEquipe1() == equipe || m.getEquipe2() == equipe) {
                    resultat.add(m);
                }
            }
        }
        return resultat;
    }
}
