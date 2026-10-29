package fr.imt.arena.match;

import fr.imt.arena.equipe.Equipe;

import java.util.Random;

// Pattern Stratégie : plusieurs façons de choisir le vainqueur,
// on peut en changer sans toucher à la classe Match
public interface StrategieVainqueur {
    Equipe choisir(Equipe equipe1, Equipe equipe2, Random random);
}
