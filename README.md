# IMT Arena

Projet Java du TP de Conception Orientée Objet (IMT Nord-Europe, FISA ITR).

Le programme gère des tournois e-sport : des joueurs, des coachs, des arbitres, des jeux, des équipes,
des matchs et des tournois. Les matchs ne sont pas vraiment joués, le résultat est tiré au hasard.
À la fin de chaque tournoi, un rapport est sauvegardé dans une base H2 avec JPA.

Le dossier de conception est dans `CONCEPTION.pdf`. Le diagramme de classes modifiable est
`diagramme_classes.drawio` (à ouvrir sur app.diagrams.net).

## Lancer le programme

Il faut Java 17 (ou plus) et VS Code avec l'extension *Extension Pack for Java*.

1. Ouvrir le dossier `esport` (celui avec le `pom.xml`) dans VS Code.
2. Ouvrir `src/main/java/fr/imt/arena/Main.java`.
3. Cliquer sur **Run** au-dessus de `main`.

Sinon, en ligne de commande dans le dossier `esport` : `mvn compile exec:java`

Au premier lancement, Maven télécharge Hibernate, H2 et JUnit (il faut Internet).
La base est créée dans `esport/data/arena.mv.db`. Les rapports s'ajoutent à chaque lancement :
supprimer le dossier `data` pour repartir de zéro.

Si les accents s'affichent mal dans le terminal Windows : taper `chcp 65001` avant.

## Lancer les tests

- VS Code : ouvrir `src/test/java/fr/imt/arena/ArenaTest.java` et cliquer sur le ▶ à côté de la classe.
- Ligne de commande, dans le dossier `esport` : `mvn test`

Les tests n'utilisent pas la base de données.

## Ce que fait le Main

- Crée 4 jeux : Rocket League (3 joueurs), Valorant et League of Legends (5 joueurs), Tekken (solo).
- Crée 6 équipes : Karmine Corp, Team Vitality, G2 Esports, Team BDS, Gentle Mates (5 joueurs)
  et Solary (3 joueurs), et 3 arbitres qualifiés pour des jeux différents.
- Teste 5 règles et affiche « refusé » pour chacune : un joueur dans deux équipes, une équipe contre
  elle-même, un arbitre qui n'a pas le bon jeu, une équipe sans assez de joueurs, une équipe inscrite deux fois.
- Joue un match de Tekken : en jeu solo, un joueur de chaque équipe est tiré au hasard.
- Planifie 3 tournois dans le désordre et les affiche du plus proche au plus loin.
- Lance les tournois un par un, toujours le plus proche d'abord, et affiche leur rapport.
- Affiche les listes : matchs regroupés par vainqueur, tournois terminés par budget, classement,
  matchs d'une équipe, joueurs, arbitres, et le journal des matchs.
- Affiche les rapports relus depuis la base H2 : tous, puis ceux de Valorant.

Les pseudos des joueurs sont inventés (KC_1, G2_3...). Le hasard utilise une graine fixe (42) : on a
toujours le même résultat.

## Les classes

```
fr.imt.arena
├── personne   Personne, Joueur, Coach, Arbitre, Qualifiable, Genre, StatutJoueur
├── jeu        Jeu, JeuSolo, JeuEquipe, JeuFactory
├── equipe     Equipe
├── match      Match, StatutMatch, StrategieVainqueur, TirageEquitable, FavoriseMieuxClasse,
│              ToujoursMieuxClasse, MatchListener, JournalMatchs
├── tournoi    Tournoi, StatutTournoi, RapportTournoi, RapportRepository, ComparateurBudget
├── Arena      le gestionnaire (équipes, arbitres, tournois)
└── Main
```

- **Personne** : ce que joueurs, coachs et arbitres ont en commun.
- **Joueur** : est dans une seule équipe, devient pro après 3 victoires de son équipe.
- **Coach** : dirige une seule équipe, compte ses victoires.
- **Arbitre** : a les jeux qu'il peut arbitrer, compte ses matchs.
- **Jeu** : un nom et un nombre de manches. `JeuSolo` = 1 joueur, `JeuEquipe` = 3 à 5 joueurs.
- **Equipe** : un nom, un coach, des joueurs, des points (3 par victoire).
- **Match** : deux équipes, un jeu, un arbitre. 10 % de chance d'incident, sinon une stratégie choisit le vainqueur.
- **Tournoi** : chaque équipe joue contre toutes les autres. Il faut au moins 4 équipes et un arbitre.
  À la fin : points, victoires, matchs arbitrés, promotions et rapport. Réussi si 70 % des matchs sont joués.
- **RapportTournoi** : le résumé d'un tournoi terminé (`@Entity`, sauvegardé en base).
- **RapportRepository** : sauvegarde les rapports et les relit (tous, ou ceux d'un jeu).
- **Arena** : ajoute les équipes, arbitres et tournois, lance les tournois et donne les listes.

## Nos choix

**Héritage** : `Personne` et `Jeu` sont abstraites, on ne crée jamais une « personne » ou un « jeu »
tout seul. `getNbJoueurs()` est abstraite dans `Jeu` et chaque classe fille donne sa réponse
(polymorphisme). Pour un nouveau type de jeu, il suffit de créer une nouvelle classe fille.

**Interfaces** : `Qualifiable` (être qualifié pour un jeu) et `Comparable` (être classé) sont des
capacités, pas des natures : ce sont donc des interfaces et pas des classes mères.

**Les règles sont dans les classes** : ce sont les objets qui vérifient les règles et lancent une
exception en cas d'erreur. Les attributs sont `private` et les listes sont renvoyées en lecture seule,
pour obliger à passer par les méthodes qui vérifient.

**Collections**

| Besoin | Collection | Pourquoi |
|---|---|---|
| Tournois planifiés, le plus proche d'abord | `PriorityQueue<Tournoi>` | Donne toujours le tournoi le plus proche (Tournoi est `Comparable` par date) |
| Classement des équipes | `TreeSet<Equipe>` | Trié automatiquement (points puis nom) et sans doublon |
| Équipe à partir de son nom | `HashMap<String, Equipe>` | Accès direct par le nom |
| Jeux d'un arbitre | `HashSet<Jeu>` | Pas de doublon, `contains()` rapide |
| Matchs par équipe gagnante | `LinkedHashMap<Equipe, List<Match>>` | Regroupe par équipe et garde l'ordre |
| Tournois terminés par budget | `ArrayList` + `ComparateurBudget` | Un autre ordre que la date, donc un `Comparator` |
| Joueurs, équipes et matchs d'un tournoi | `ArrayList` | Peu d'éléments, on garde l'ordre |

**Patterns**

- **Stratégie** (`StrategieVainqueur`) : le vainqueur peut être choisi de 3 façons (au hasard,
  en favorisant la mieux classée, toujours la mieux classée pour les tests). On change de stratégie
  dans l'`Arena` sans toucher à `Match`.
- **Observateur** (`MatchListener`) : quand un match se termine, le tournoi prévient tous les
  écouteurs (ici le `JournalMatchs`) sans les connaître. On peut en ajouter d'autres sans modifier `Tournoi`.
- **Fabrique** (`JeuFactory`) : on crée un jeu avec son nom seulement, sans répéter son nombre de
  manches et de joueurs partout.

**Persistance (JPA / H2)**

- Seul `RapportTournoi` est sauvegardé. C'est une `@Entity` avec un `@Id` généré automatiquement.
- La configuration est dans `src/main/resources/META-INF/persistence.xml` (base `jdbc:h2:./data/arena`).
- L'`Arena` sauvegarde le rapport automatiquement à la fin de chaque tournoi.
- `RapportRepository` retrouve tous les rapports, ou ceux d'un jeu, avec une requête JPQL.

## Tests

`ArenaTest` contient 8 tests JUnit 5 (12 cas en comptant les tests paramétrés) :

| Test | Type | Ce qu'il vérifie |
|---|---|---|
| `nombreDeMatchs` | paramétré (`@CsvSource`) | 4 équipes = 6 matchs, 5 = 10, 6 = 15 |
| `jeuEquipeMauvaisNombreDeJoueurs` | paramétré (`@ValueSource`) + exception | un jeu en équipe refuse 0, 2 ou 6 joueurs |
| `joueurDansDeuxEquipesRefuse` | exception | un joueur ne peut pas être dans deux équipes |
| `matchContreSoiMemeRefuse` | exception | une équipe ne joue pas contre elle-même |
| `tournoiTroisEquipesRefuse` | exception | un tournoi à 3 équipes ne démarre pas |
| `simulationComplete` | simple | le tournoi se termine, les points correspondent aux matchs joués |
| `lancerLePlusProche` | simple | le premier tournoi planifié est le plus proche dans le temps |
| `passagePro` | simple | un joueur passe pro à la 3e victoire |

`@BeforeEach` crée 4 équipes, un arbitre et une `Arena` avant chaque test.
Les tests utilisent la stratégie `ToujoursMieuxClasse` pour avoir un résultat connu.

## Non réalisé

- **Simulation dans des threads** : Manque de temps

## Limites connues

- En cas d'égalité de victoires dans un tournoi, le vainqueur est la première équipe inscrite.
- Si un tournoi ne peut pas démarrer (moins de 4 équipes), il reste en tête des tournois planifiés et bloque les suivants.
- Les rapports s'accumulent dans la base à chaque lancement du programme.
- Hibernate peut afficher quelques lignes d'information au démarrage.
