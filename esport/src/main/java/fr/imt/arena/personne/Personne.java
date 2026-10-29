package fr.imt.arena.personne;

// Classe mère des joueurs, coachs et arbitres
public abstract class Personne {

    private int id;
    private String nom;
    private String prenom;
    private String pseudo;
    private Genre genre;
    private String telephone;

    public Personne(int id, String nom, String prenom, String pseudo, Genre genre, String telephone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.pseudo = pseudo;
        this.genre = genre;
        this.telephone = telephone;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getPseudo() { return pseudo; }
    public Genre getGenre() { return genre; }
    public String getTelephone() { return telephone; }

    @Override
    public String toString() {
        return pseudo + " (" + prenom + " " + nom + ")";
    }
}
