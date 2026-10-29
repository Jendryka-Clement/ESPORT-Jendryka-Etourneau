package fr.imt.arena;

import fr.imt.arena.equipe.Equipe;
import fr.imt.arena.jeu.Jeu;
import fr.imt.arena.jeu.JeuEquipe;
import fr.imt.arena.jeu.JeuFactory;
import fr.imt.arena.match.Match;
import fr.imt.arena.match.ToujoursMieuxClasse;
import fr.imt.arena.personne.Arbitre;
import fr.imt.arena.personne.Coach;
import fr.imt.arena.personne.Genre;
import fr.imt.arena.personne.Joueur;
import fr.imt.arena.personne.StatutJoueur;
import fr.imt.arena.tournoi.RapportTournoi;
import fr.imt.arena.tournoi.StatutTournoi;
import fr.imt.arena.tournoi.Tournoi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArenaTest {

    private Jeu rocketLeague;
    private Arbitre arbitre;
    private List<Equipe> equipes;
    private Arena arena;
    private int prochainId;

    // avant chaque test : 4 équipes de 3 joueurs, un arbitre, une arena sans base de données
    @BeforeEach
    void preparer() {
        prochainId = 1;
        rocketLeague = JeuFactory.creer("Rocket League");
        arbitre = new Arbitre(prochainId++, "Nom", "Prenom", "Arbitre", Genre.FEMME, "06", rocketLeague);
        equipes = new ArrayList<>();
        equipes.add(creerEquipe("Alpha"));
        equipes.add(creerEquipe("Bravo"));
        equipes.add(creerEquipe("Charlie"));
        equipes.add(creerEquipe("Delta"));
        // la mieux classée gagne toujours : résultat connu à l'avance
        arena = new Arena(new ToujoursMieuxClasse(), 1);
    }

    private Equipe creerEquipe(String nom) {
        Equipe equipe = new Equipe(nom, new Coach(prochainId++, "Nom", "Prenom", "Coach" + nom, Genre.HOMME, "06"));
        for (int i = 1; i <= 3; i++) {
            equipe.ajouterJoueur(new Joueur(prochainId++, "Nom", "Prenom", nom + i, Genre.HOMME, "07"));
        }
        return equipe;
    }

    private Tournoi creerTournoi(String nom, List<Equipe> lesEquipes, int jour) {
        Tournoi tournoi = new Tournoi(nom, rocketLeague, LocalDate.of(2026, 11, jour), 1000, 2);
        for (Equipe e : lesEquipes) {
            tournoi.inscrire(e);
        }
        tournoi.ajouterArbitre(arbitre);
        return tournoi;
    }

    // test paramétré : nombre de matchs d'un tournoi (6 équipes : 15 matchs, comme dans le sujet)
    @ParameterizedTest
    @CsvSource({
            "4, 6",
            "5, 10",
            "6, 15"
    })
    void nombreDeMatchs(int nbEquipes, int matchsAttendus) {
        assertEquals(matchsAttendus, Tournoi.nombreDeMatchs(nbEquipes));
    }

    // test paramétré + exception : un jeu en équipe se joue de 3 à 5 joueurs
    @ParameterizedTest
    @ValueSource(ints = {0, 2, 6})
    void jeuEquipeMauvaisNombreDeJoueurs(int nbJoueurs) {
        assertThrows(IllegalArgumentException.class, () -> new JeuEquipe("Jeu test", 3, nbJoueurs));
    }

    // test d'exception : un joueur ne peut pas être dans deux équipes
    @Test
    void joueurDansDeuxEquipesRefuse() {
        Joueur joueur = equipes.get(0).getJoueurs().get(0);
        assertThrows(IllegalStateException.class, () -> equipes.get(1).ajouterJoueur(joueur));
    }

    // test d'exception : une équipe ne joue pas contre elle-même
    @Test
    void matchContreSoiMemeRefuse() {
        Equipe alpha = equipes.get(0);
        assertThrows(IllegalArgumentException.class, () -> new Match(alpha, alpha, rocketLeague, arbitre));
    }

    // test d'exception : un tournoi à 3 équipes ne démarre pas
    @Test
    void tournoiTroisEquipesRefuse() {
        Tournoi tournoi = creerTournoi("Petit", equipes.subList(0, 3), 1);
        arena.planifier(tournoi);
        assertThrows(IllegalStateException.class, () -> arena.lancerSuivant());
        assertEquals(StatutTournoi.PLANIFIE, tournoi.getStatut());
    }

    // simulation complète : le tournoi se termine et les points correspondent aux matchs joués
    @Test
    void simulationComplete() {
        Tournoi tournoi = creerTournoi("Coupe", equipes, 1);
        arena.planifier(tournoi);

        RapportTournoi rapport = arena.lancerSuivant();

        assertEquals(StatutTournoi.TERMINE, tournoi.getStatut());
        assertEquals(6, rapport.getMatchsPrevus());
        int totalPoints = 0;
        for (Equipe e : equipes) {
            totalPoints = totalPoints + e.getPoints();
        }
        assertEquals(3 * rapport.getMatchsJoues(), totalPoints);
        assertEquals(rapport.getMatchsJoues(), arbitre.getMatchsArbitres());
    }

    // le tournoi lancé est toujours le plus proche dans le temps (PriorityQueue)
    @Test
    void lancerLePlusProche() {
        Tournoi decembre = creerTournoi("Decembre", equipes, 30);
        Tournoi novembre = new Tournoi("Novembre", rocketLeague, LocalDate.of(2026, 11, 2), 500, 1);
        arena.planifier(decembre);
        arena.planifier(novembre);
        assertEquals("Novembre", arena.getTournoisPlanifies().get(0).getNom());
    }

    // un joueur passe pro à la 3e victoire de son équipe
    @Test
    void passagePro() {
        Equipe alpha = equipes.get(0);
        alpha.ajouterVictoire();
        alpha.ajouterVictoire();
        assertEquals(StatutJoueur.AMATEUR, alpha.getJoueurs().get(0).getStatut());
        alpha.ajouterVictoire();
        assertEquals(StatutJoueur.PRO, alpha.getJoueurs().get(0).getStatut());
    }
}
