package fr.imt.arena.personne;

import fr.imt.arena.jeu.Jeu;

// capacité "être qualifié pour un jeu" : c'est une interface et pas une classe mère,
// car ce n'est pas ce que l'objet EST mais ce qu'il SAIT faire
public interface Qualifiable {
    boolean estQualifiePour(Jeu jeu);
}
