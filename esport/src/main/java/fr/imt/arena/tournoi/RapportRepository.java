package fr.imt.arena.tournoi;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

// sauvegarde et relit les rapports de tournoi dans la base H2
public class RapportRepository {

    private EntityManagerFactory emf;

    public RapportRepository() {
        emf = Persistence.createEntityManagerFactory("arena");
    }

    public void sauver(RapportTournoi rapport) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(rapport);
        em.getTransaction().commit();
        em.close();
    }

    public List<RapportTournoi> trouverTous() {
        EntityManager em = emf.createEntityManager();
        List<RapportTournoi> rapports = em
                .createQuery("SELECT r FROM RapportTournoi r", RapportTournoi.class)
                .getResultList();
        em.close();
        return rapports;
    }

    public List<RapportTournoi> trouverParJeu(String jeu) {
        EntityManager em = emf.createEntityManager();
        List<RapportTournoi> rapports = em
                .createQuery("SELECT r FROM RapportTournoi r WHERE r.jeu = :jeu", RapportTournoi.class)
                .setParameter("jeu", jeu)
                .getResultList();
        em.close();
        return rapports;
    }

    public void fermer() {
        emf.close();
    }
}
