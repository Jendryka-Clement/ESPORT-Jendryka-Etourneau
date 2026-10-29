package fr.imt.arena.match;

// un observateur qui compte les matchs joués et les incidents
public class JournalMatchs implements MatchListener {

    private int matchsJoues;
    private int incidents;

    @Override
    public void matchTermine(Match match) {
        if (match.getStatut() == StatutMatch.ANNULE) {
            incidents++;
        } else {
            matchsJoues++;
        }
    }

    public int getMatchsJoues() { return matchsJoues; }
    public int getIncidents() { return incidents; }
}
