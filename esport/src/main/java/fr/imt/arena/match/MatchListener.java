package fr.imt.arena.match;

// Pattern Observateur : on prévient tous ceux qui écoutent quand un match se termine,
// sans que le match ou le tournoi ait besoin de les connaître
public interface MatchListener {
    void matchTermine(Match match);
}
