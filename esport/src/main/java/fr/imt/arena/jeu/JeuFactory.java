package fr.imt.arena.jeu;

// Pattern Fabrique : créer un jeu à partir de son nom,
// sans répéter le nombre de manches et de joueurs partout dans le code
public class JeuFactory {

    public static Jeu creer(String nom) {
        if (nom.equals("Rocket League")) {
            return new JeuEquipe("Rocket League", 5, 3);
        }
        if (nom.equals("Valorant")) {
            return new JeuEquipe("Valorant", 3, 5);
        }
        if (nom.equals("League of Legends")) {
            return new JeuEquipe("League of Legends", 3, 5);
        }
        if (nom.equals("Tekken")) {
            return new JeuSolo("Tekken", 3);
        }
        throw new IllegalArgumentException("Jeu inconnu : " + nom);
    }
}
