package fr.imt.arena.match;

import fr.imt.arena.equipe.Equipe;

import java.util.Random;

// la mieux classée gagne toujours : résultat connu à l'avance, pratique pour les tests
public class ToujoursMieuxClasse implements StrategieVainqueur {

    @Override
    public Equipe choisir(Equipe equipe1, Equipe equipe2, Random random) {
        if (equipe2.compareTo(equipe1) < 0) {
            return equipe2;
        }
        return equipe1;
    }
}
