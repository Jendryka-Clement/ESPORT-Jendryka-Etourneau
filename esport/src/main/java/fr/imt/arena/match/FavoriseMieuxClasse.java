package fr.imt.arena.match;

import fr.imt.arena.equipe.Equipe;

import java.util.Random;

// l'équipe la mieux classée gagne 2 fois sur 3
public class FavoriseMieuxClasse implements StrategieVainqueur {

    @Override
    public Equipe choisir(Equipe equipe1, Equipe equipe2, Random random) {
        Equipe meilleure = equipe1;
        Equipe autre = equipe2;
        if (equipe2.compareTo(equipe1) < 0) {
            meilleure = equipe2;
            autre = equipe1;
        }
        if (random.nextInt(3) < 2) {
            return meilleure;
        }
        return autre;
    }
}
