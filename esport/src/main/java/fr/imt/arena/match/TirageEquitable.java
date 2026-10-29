package fr.imt.arena.match;

import fr.imt.arena.equipe.Equipe;

import java.util.Random;

// une chance sur deux pour chaque équipe
public class TirageEquitable implements StrategieVainqueur {

    @Override
    public Equipe choisir(Equipe equipe1, Equipe equipe2, Random random) {
        if (random.nextBoolean()) {
            return equipe1;
        }
        return equipe2;
    }
}
