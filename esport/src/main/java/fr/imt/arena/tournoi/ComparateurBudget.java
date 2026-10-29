package fr.imt.arena.tournoi;

import java.util.Comparator;

// Comparator : trie les tournois du plus gros au plus petit budget
// (un autre ordre que l'ordre naturel par date de Tournoi)
public class ComparateurBudget implements Comparator<Tournoi> {

    @Override
    public int compare(Tournoi t1, Tournoi t2) {
        return Double.compare(t2.getBudget(), t1.getBudget());
    }
}
