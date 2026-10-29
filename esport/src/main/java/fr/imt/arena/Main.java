package fr.imt.arena;

import fr.imt.arena.equipe.Equipe;
import fr.imt.arena.jeu.Jeu;
import fr.imt.arena.jeu.JeuFactory;
import fr.imt.arena.match.FavoriseMieuxClasse;
import fr.imt.arena.match.JournalMatchs;
import fr.imt.arena.match.Match;
import fr.imt.arena.match.TirageEquitable;
import fr.imt.arena.personne.Arbitre;
import fr.imt.arena.personne.Coach;
import fr.imt.arena.personne.Genre;
import fr.imt.arena.personne.Joueur;
import fr.imt.arena.tournoi.RapportRepository;
import fr.imt.arena.tournoi.RapportTournoi;
import fr.imt.arena.tournoi.Tournoi;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    // on garde une référence pour que le réglage ne soit pas perdu
    private static final Logger LOG_HIBERNATE = Logger.getLogger("org.hibernate");

    public static void main(String[] args) {

        LOG_HIBERNATE.setLevel(Level.WARNING); // cache les messages d'information d'Hibernate

        // l'équipe la mieux classée est favorisée, graine 42
        Arena arena = new Arena(new FavoriseMieuxClasse(), 42);
        RapportRepository repository = new RapportRepository(); // base H2 : data/arena.mv.db
        arena.setRepository(repository);
        JournalMatchs journal = new JournalMatchs();
        arena.ajouterEcouteur(journal);

        // les jeux (créés par la fabrique)
        Jeu rocketLeague = JeuFactory.creer("Rocket League");
        Jeu valorant = JeuFactory.creer("Valorant");
        Jeu lol = JeuFactory.creer("League of Legends");
        Jeu tekken = JeuFactory.creer("Tekken");

        // les équipes (Solary n'a que 3 joueurs)
        List<Equipe> equipes = new ArrayList<>();
        equipes.add(creerEquipe("Karmine Corp", "KC", 5, 100));
        equipes.add(creerEquipe("Team Vitality", "VIT", 5, 200));
        equipes.add(creerEquipe("G2 Esports", "G2", 5, 300));
        equipes.add(creerEquipe("Team BDS", "BDS", 5, 400));
        equipes.add(creerEquipe("Gentle Mates", "M8", 5, 500));
        equipes.add(creerEquipe("Solary", "SLY", 3, 600));
        for (Equipe e : equipes) {
            arena.ajouterEquipe(e);
        }
        Equipe kc = arena.getEquipe("Karmine Corp");
        Equipe vitality = arena.getEquipe("Team Vitality");
        Equipe solary = arena.getEquipe("Solary");

        // les arbitres
        Arbitre tom = new Arbitre(1, "Leroy", "Tom", "Tom", Genre.HOMME, "0650", rocketLeague);
        tom.ajouterJeu(tekken);
        Arbitre zoe = new Arbitre(2, "Blanc", "Zoe", "Zoe", Genre.FEMME, "0651", valorant);
        Arbitre max = new Arbitre(3, "Petit", "Max", "Max", Genre.HOMME, "0652", lol);
        arena.ajouterArbitre(tom);
        arena.ajouterArbitre(zoe);
        arena.ajouterArbitre(max);

        System.out.println("Equipes : Karmine Corp, Team Vitality, G2 Esports, Team BDS, Gentle Mates (5 joueurs)");
        System.out.println("          Solary (3 joueurs)");
        System.out.println("Arbitres : Tom (Rocket League, Tekken), Zoe (Valorant), Max (League of Legends)");

        // ---------- les règles ----------
        System.out.println("\n--- Test des règles ---");

        System.out.print("1. Mettre KC_1 (déjà chez Karmine Corp) dans Solary : ");
        try {
            solary.ajouterJoueur(kc.getJoueurs().get(0));
            System.out.println("accepté");
        } catch (IllegalStateException e) {
            System.out.println("refusé");
        }

        System.out.print("2. Faire jouer Karmine Corp contre Karmine Corp : ");
        try {
            new Match(kc, kc, rocketLeague, tom);
            System.out.println("accepté");
        } catch (IllegalArgumentException e) {
            System.out.println("refusé");
        }

        System.out.print("3. Zoe arbitre un match de Rocket League (pas son jeu) : ");
        try {
            new Match(kc, vitality, rocketLeague, zoe);
            System.out.println("accepté");
        } catch (IllegalArgumentException e) {
            System.out.println("refusé");
        }

        Tournoi test = new Tournoi("Test", valorant, LocalDate.of(2026, 11, 1), 0, 1);
        System.out.print("4. Inscrire Solary (3 joueurs) à un tournoi Valorant (5 joueurs) : ");
        try {
            test.inscrire(solary);
            System.out.println("accepté");
        } catch (IllegalArgumentException e) {
            System.out.println("refusé");
        }

        System.out.print("5. Inscrire deux fois Karmine Corp au même tournoi : ");
        try {
            test.inscrire(kc);
            test.inscrire(kc);
            System.out.println("accepté");
        } catch (IllegalArgumentException e) {
            System.out.println("refusé");
        }

        // ---------- un match solo ----------
        System.out.println("\n--- Un match de Tekken (jeu solo : un joueur tiré au hasard par équipe) ---");
        Match matchSolo = new Match(kc, vitality, tekken, tom);
        matchSolo.jouer(new Random(42), new TirageEquitable());
        System.out.println(matchSolo);

        // ---------- planification ----------
        Tournoi lec = new Tournoi("LEC Showmatch", lol, LocalDate.of(2026, 12, 5), 3000, 1);
        Tournoi rlcs = new Tournoi("RLCS Paris", rocketLeague, LocalDate.of(2026, 11, 7), 1500, 2);
        Tournoi vct = new Tournoi("VCT Lille", valorant, LocalDate.of(2026, 11, 21), 2000, 2);
        inscrireEquipes(lec, equipes);
        inscrireEquipes(rlcs, equipes);
        inscrireEquipes(vct, equipes);
        lec.ajouterArbitre(max);
        rlcs.ajouterArbitre(tom);
        vct.ajouterArbitre(zoe);

        // planifiés dans le désordre : la PriorityQueue remet le plus proche en premier
        arena.planifier(lec);
        arena.planifier(rlcs);
        arena.planifier(vct);

        System.out.println("\n--- Tournois planifiés (le plus proche d'abord) ---");
        for (Tournoi t : arena.getTournoisPlanifies()) {
            System.out.println(t);
        }

        System.out.println("\nArbitres occupés (prévus dans un tournoi) : " + pseudos(arena.getArbitresOccupes())
                + " / disponibles : " + pseudos(arena.getArbitresDisponibles()));

        // ---------- simulation ----------
        System.out.println("\n--- Lancement du plus proche : " + rlcs.getNom() + " ---");
        System.out.println("Etat avant : " + rlcs.getStatut());
        System.out.println(rlcs.getEquipes().size() + " équipes -> "
                + Tournoi.nombreDeMatchs(rlcs.getEquipes().size()) + " matchs");
        RapportTournoi rapport = arena.lancerSuivant();
        System.out.println("Etat après : " + rlcs.getStatut());
        System.out.println("Rapport : " + rapport);

        System.out.println("\n--- Matchs de RLCS Paris par équipe gagnante ---");
        Map<Equipe, List<Match>> parVainqueur = rlcs.getMatchsParVainqueur();
        for (Equipe e : parVainqueur.keySet()) {
            System.out.println(e.getNom() + " : " + parVainqueur.get(e).size() + " victoire(s)");
            for (Match m : parVainqueur.get(e)) {
                System.out.println("   " + m);
            }
        }

        System.out.println("\n--- Les deux autres tournois ---");
        System.out.println("Rapport : " + arena.lancerSuivant());
        System.out.println("Rapport : " + arena.lancerSuivant());

        // ---------- listes ----------
        System.out.println("\n--- Tournois terminés (du plus gros budget au plus petit) ---");
        for (Tournoi t : arena.getTournoisTermines()) {
            System.out.println(t);
        }

        System.out.println("\n--- Classement ---");
        int rang = 1;
        for (Equipe e : arena.getClassement()) {
            System.out.println(rang + ". " + e.getNom() + " : " + e.getPoints() + " pts (coach "
                    + e.getCoach().getPseudo() + ", " + e.getCoach().getVictoiresCarriere() + " victoires)");
            rang++;
        }

        System.out.println("\n--- Matchs de Solary ---");
        for (Match m : arena.getMatchsEquipe(solary)) {
            System.out.println(m);
        }

        System.out.println("\n--- Joueurs ---");
        for (Equipe e : arena.getClassement()) {
            String ligne = e.getNom() + " : ";
            for (Joueur j : e.getJoueurs()) {
                ligne = ligne + j.getPseudo() + " (" + j.getStatut() + ")  ";
            }
            System.out.println(ligne);
        }

        System.out.println("\n--- Arbitres ---");
        System.out.println("Tom : " + tom.getMatchsArbitres() + " matchs");
        System.out.println("Zoe : " + zoe.getMatchsArbitres() + " matchs");
        System.out.println("Max : " + max.getMatchsArbitres() + " matchs");

        System.out.println("\n--- Journal (observateur) ---");
        System.out.println(journal.getMatchsJoues() + " matchs joués, " + journal.getIncidents() + " incidents");

        // ---------- base de données ----------
        // les rapports s'ajoutent à chaque lancement (supprimer le dossier data pour repartir de zéro)
        System.out.println("\n--- Rapports enregistrés dans la base H2 ---");
        for (RapportTournoi r : repository.trouverTous()) {
            System.out.println(r);
        }
        System.out.println("\n--- Rapports Valorant uniquement ---");
        for (RapportTournoi r : repository.trouverParJeu("Valorant")) {
            System.out.println(r);
        }

        repository.fermer();
        System.out.println("\nFin du programme.");
    }

    // crée une équipe avec un coach et des joueurs KC_1, KC_2...
    static Equipe creerEquipe(String nom, String tag, int nbJoueurs, int id) {
        Coach coach = new Coach(id, "Coach", tag, "Coach_" + tag, Genre.HOMME, "0600000000");
        Equipe equipe = new Equipe(nom, coach);
        for (int i = 1; i <= nbJoueurs; i++) {
            Joueur j = new Joueur(id + i, "Nom" + i, "Prenom" + i, tag + "_" + i, Genre.HOMME, "0700000000");
            equipe.ajouterJoueur(j);
        }
        return equipe;
    }

    // inscrit seulement les équipes qui ont assez de joueurs
    static void inscrireEquipes(Tournoi tournoi, List<Equipe> equipes) {
        for (Equipe e : equipes) {
            try {
                tournoi.inscrire(e);
            } catch (IllegalArgumentException ex) {
                // pas assez de joueurs : l'équipe ne participe pas
            }
        }
    }

    static String pseudos(List<Arbitre> arbitres) {
        String texte = "";
        for (Arbitre a : arbitres) {
            texte = texte + a.getPseudo() + " ";
        }
        if (texte.isEmpty()) {
            return "aucun";
        }
        return texte.trim();
    }
}
