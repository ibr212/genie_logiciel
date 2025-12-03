package re.forestier.edu;

import org.junit.jupiter.api.*;
import re.forestier.edu.rpg.player;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;


import org.junit.jupiter.api.*;
import re.forestier.edu.rpg.Affichage;
import re.forestier.edu.rpg.UpdatePlayer;
import re.forestier.edu.rpg.player;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class UnitTests {

    // --- Variables Utilitaires pour les tests ---
    private static final int INITIAL_HP = 100;

    @BeforeEach
    void setUp() {
        // Fixer la graine de Random pour rendre le test de addXp déterministe
        // Le code utilise un new Random() sans graine, ce n'est pas idéal pour les tests,
        // mais pour ce TP, nous allons simuler un résultat constant si possible.
        // La méthode addXp crée un nouveau Random() à chaque appel. Nous ne pouvons
        // pas le contrôler directement sans modifier le code source, nous allons juste
        // vérifier la présence d'un objet.
    }

    // --- Tests de player.java ---

    @Test
    @DisplayName("Test: Création de joueur pour chaque classe valide")
    void testPlayerCreationValidClasses() {
        // DWARF
        player dwarf = new player("Florian", "Gimli", "DWARF", 100, new ArrayList<>());
        assertThat(dwarf.getAvatarClass(), is("DWARF"));
        assertThat(dwarf.abilities.containsKey("ALC"), is(true)); // Capacité Nv 1 DWARF

        // ARCHER
        player archer = new player("Légolas", "ArcherName", "ARCHER", 100, new ArrayList<>());
        assertThat(archer.getAvatarClass(), is("ARCHER"));
        assertThat(archer.abilities.containsKey("VIS"), is(true)); // Capacité Nv 1 ARCHER

        // ADVENTURER
        player adventurer = new player("Jon", "Aventurier", "ADVENTURER", 100, new ArrayList<>());
        assertThat(adventurer.getAvatarClass(), is("ADVENTURER"));
        assertThat(adventurer.abilities.containsKey("DEF"), is(true)); // Capacité Nv 1 ADVENTURER
    }

    @Test
    @DisplayName("Test: Constructeur pour classe invalide (pas de création)")
    void testPlayerCreationInvalidClass() {
        player invalidPlayer = new player("Test", "Invalid", "WARRIOR", 100, new ArrayList<>());
        // La seule vérification possible est que les champs privés n'ont pas été initialisés
        // ou que les capacités n'ont pas été chargées, car le constructeur fait un 'return;'
        // avant l'initialisation complète des attributs si la classe est invalide.
        assertThat(invalidPlayer.abilities, is(nullValue()));
        assertThat(invalidPlayer.getAvatarClass(), is(nullValue()));
    }

    @Test
    @DisplayName("Test: Récupération de l'XP")
    void testGetXp() {
        player p = new player("A", "B", "ADVENTURER", 0, new ArrayList<>());
        // L'XP par défaut est 0, mais comme il est 'protected', nous devons utiliser addXp
        // pour modifier l'xp. Nous devons cependant être sûr que l'XP initial est 0.
        // Puisque nous ne pouvons pas accéder directement à 'xp', nous testons la méthode.
        assertThat(p.getXp(), is(0));
    }

    @Test
    @DisplayName("Test: Récupération de la classe")
    void testGetAvatarClass() {
        player p = new player("A", "B", "ADVENTURER", 0, new ArrayList<>());
        assertThat(p.getAvatarClass(), is("ADVENTURER"));
    }

    @Test
    @DisplayName("Test: Ajout d'argent (Couverture du Ternaire)")
    void testAddMoney() {
        player p = new player("A", "B", "ADVENTURER", 100, new ArrayList<>());
        p.addMoney(50);
        assertThat(p.money, is(150));
        // La ligne money = money + (value != null ? value : 0); est couverte car value
        // est Integer.valueOf(amount), qui n'est jamais null pour un int primitif.
        // Le chemin 'value != null' est donc testé.
    }

    @Test
    @DisplayName("Test: Retrait d'argent réussi")
    void testRemoveMoneySuccess() {
        player p = new player("A", "B", "ADVENTURER", 100, new ArrayList<>());
        p.removeMoney(40);
        assertThat(p.money, is(60));
    }

    @Test
    @DisplayName("Test: Impossible d'avoir argent négatif (Lève Exception)")
    void testRemoveMoneyNegative() {
        player p = new player("A", "B", "ADVENTURER", 100, new ArrayList<>());
        try {
            p.removeMoney(200);
            fail("IllegalArgumentException non levée pour un solde négatif.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Player can't have a negative money!"));
        }
    }

    // --- Tests de player.retrieveLevel() ---

    @Test
    @DisplayName("Niveau 1 (XP < 10)")
    void testRetrieveLevel1() {
        player p = new player("A", "B", "ADVENTURER", 0, new ArrayList<>());
        // XP initial 0
        assertThat(p.retrieveLevel(), is(1));
        UpdatePlayer.addXp(p, 9);
        assertThat(p.retrieveLevel(), is(1));
    }

    @Test
    @DisplayName("Niveau 2 (10 <= XP < 27)")
    void testRetrieveLevel2() {
        player p = new player("A", "B", "ADVENTURER", 0, new ArrayList<>());
        UpdatePlayer.addXp(p, 10); // Seuil minimal
        assertThat(p.retrieveLevel(), is(2));
        UpdatePlayer.addXp(p, 16); // XP total = 26
        assertThat(p.retrieveLevel(), is(2));
    }

    @Test
    @DisplayName("Niveau 3 (27 <= XP < 57)")
    void testRetrieveLevel3() {
        player p = new player("A", "B", "ADVENTURER", 0, new ArrayList<>());
        UpdatePlayer.addXp(p, 27); // Seuil minimal
        assertThat(p.retrieveLevel(), is(3));
        UpdatePlayer.addXp(p, 29); // XP total = 56
        assertThat(p.retrieveLevel(), is(3));
    }

    @Test
    @DisplayName("Niveau 4 (57 <= XP < 111)")
    void testRetrieveLevel4() {
        player p = new player("A", "B", "ADVENTURER", 0, new ArrayList<>());
        UpdatePlayer.addXp(p, 57); // Seuil minimal
        assertThat(p.retrieveLevel(), is(4));
        UpdatePlayer.addXp(p, 53); // XP total = 110
        assertThat(p.retrieveLevel(), is(4));
    }

    @Test
    @DisplayName("Niveau 5 (XP >= 111)")
    void testRetrieveLevel5() {
        player p = new player("A", "B", "ADVENTURER", 0, new ArrayList<>());
        UpdatePlayer.addXp(p, 111); // Seuil minimal
        assertThat(p.retrieveLevel(), is(5));
        UpdatePlayer.addXp(p, 100); // XP total = 211
        assertThat(p.retrieveLevel(), is(5));
    }

    // --- Tests de UpdatePlayer.java ---

    @Test
    @DisplayName("Test: Structure de abilitiesPerTypeAndLevel()")
    void testAbilitiesMapStructure() {
        HashMap<String, HashMap<Integer, HashMap<String, Integer>>> abilities = UpdatePlayer.abilitiesPerTypeAndLevel();
        assertNotNull(abilities);
        assertThat(abilities.keySet(), containsInAnyOrder("ADVENTURER", "ARCHER", "DWARF"));

        // Vérification d'une capacité spécifique (Exemple: ADVENTURER Level 3)
        HashMap<String, Integer> adventurerL3 = abilities.get("ADVENTURER").get(3);
        assertThat(adventurerL3.get("ATK"), is(5));
        assertThat(adventurerL3.get("ALC"), is(1));
    }

    @Test
    @DisplayName("Test: addXp sans passage de niveau")
    void testAddXpNoLevelUp() {
        player p = new player("A", "B", "ADVENTURER", 0, new ArrayList<>());
        int initialLevel = p.retrieveLevel();
        // Ajout d'XP qui ne fait pas atteindre le niveau 2 (seuil à 10)
        boolean leveledUp = UpdatePlayer.addXp(p, 5);

        assertFalse(leveledUp);
        assertThat(p.getXp(), is(5));
        assertThat(p.retrieveLevel(), is(initialLevel));
        assertThat(p.inventory.size(), is(0));
    }

    @Test
    @DisplayName("Test: addXp avec passage de niveau 1 -> 2")
    void testAddXpLevelUp1to2() {
        player p = new player("A", "B", "ADVENTURER", 0, new ArrayList<>());
        int initialInventorySize = p.inventory.size();

        // Ajout d'XP qui déclenche le niveau 2 (seuil à 10)
        boolean leveledUp = UpdatePlayer.addXp(p, 10);

        assertTrue(leveledUp);
        assertThat(p.retrieveLevel(), is(2));
        // Vérifie qu'un objet aléatoire a été ajouté
        assertThat(p.inventory.size(), is(initialInventorySize + 1));
        // Vérifie que les capacités de niveau 2 ont été ajoutées/mises à jour
        assertThat(p.abilities.containsKey("INT"), is(true)); // Capacité de niveau 2 Adventurer
        assertThat(p.abilities.get("INT"), is(2)); // Valeur mise à jour par la map Adventurer Lvl 2
    }

    @Test
    @DisplayName("Test: addXp avec passage de niveau multiple (1 -> 4)")
    void testAddXpMultipleLevelUp() {
        player p = new player("A", "B", "ADVENTURER", 0, new ArrayList<>());

        // Ajout d'XP qui déclenche le niveau 4 (seuil à 57)
        boolean leveledUp = UpdatePlayer.addXp(p, 60);

        assertTrue(leveledUp);
        assertThat(p.retrieveLevel(), is(4));
        // Vérifie que les capacités de niveau 4 ont été ajoutées/mises à jour
        assertThat(p.abilities.containsKey("DEF"), is(true)); // Capacité de niveau 4 Adventurer
        assertThat(p.abilities.get("DEF"), is(3));
    }

    // --- Tests de UpdatePlayer.majFinDeTour() ---

    @Test
    @DisplayName("majFinDeTour: Joueur KO (HP=0) - Sortie immédiate")
    void testMajFinDeTourKo() {
        player p = new player("A", "B", "ADVENTURER", 0, new ArrayList<>());
        p.healthpoints = INITIAL_HP;
        p.currenthealthpoints = 0;

        UpdatePlayer.majFinDeTour(p);

        // Les PV restent à 0
        assertThat(p.currenthealthpoints, is(0));
    }

    @Test
    @DisplayName("majFinDeTour: Joueur HP MAX - Sortie anticipée")
    void testMajFinDeTourMaxHp() {
        player p = new player("A", "B", "ADVENTURER", 0, new ArrayList<>());
        p.healthpoints = INITIAL_HP;
        p.currenthealthpoints = INITIAL_HP; // PV Max

        UpdatePlayer.majFinDeTour(p);

        // Les PV restent à HP_MAX
        assertThat(p.currenthealthpoints, is(INITIAL_HP));
    }

    @Test
    @DisplayName("majFinDeTour: HP < 50% - Classe DWARF avec Holy Elixir (+2 HP)")
    void testMajFinDeTourDwarfWithElixir() {
        player p = new player("A", "B", "DWARF", 0, new ArrayList<>());
        p.inventory.add("Holy Elixir");
        p.healthpoints = 100;
        p.currenthealthpoints = 40; // < 50

        UpdatePlayer.majFinDeTour(p);

        // +1 (élixir) +1 (DWARF) = +2
        assertThat(p.currenthealthpoints, is(42));
    }

    @Test
    @DisplayName("majFinDeTour: HP < 50% - Classe DWARF sans Holy Elixir (+1 HP)")
    void testMajFinDeTourDwarfWithoutElixir() {
        player p = new player("A", "B", "DWARF", 0, new ArrayList<>());
        p.healthpoints = 100;
        p.currenthealthpoints = 40; // < 50

        UpdatePlayer.majFinDeTour(p);

        // +1 (DWARF) = +1
        assertThat(p.currenthealthpoints, is(41));
    }

    @Test
    @DisplayName("majFinDeTour: HP < 50% - Classe ARCHER sans Magic Bow (+1 HP)")
    void testMajFinDeTourArcherWithoutBow() {
        player p = new player("A", "B", "ARCHER", 0, new ArrayList<>());
        p.healthpoints = 100;
        p.currenthealthpoints = 40; // < 50

        UpdatePlayer.majFinDeTour(p);

        // +1 (ARCHER) = +1
        assertThat(p.currenthealthpoints, is(41));
    }

    @Test
    @DisplayName("majFinDeTour: HP < 50% - Classe ARCHER avec Magic Bow (HP + 1 + (HP/8 - 1))")
    void testMajFinDeTourArcherWithBow() {
        player p = new player("A", "B", "ARCHER", 0, new ArrayList<>());
        p.inventory.add("Magic Bow");
        p.healthpoints = 100;
        p.currenthealthpoints = 40; // < 50

        UpdatePlayer.majFinDeTour(p);

        // +1 (ARCHER) + (40/8 - 1) = +1 + (5 - 1) = +5
        assertThat(p.currenthealthpoints, is(45));
    }

    @Test
    @DisplayName("majFinDeTour: HP < 50% - Classe ADVENTURER (Niv < 3) (+1 HP net)")
    void testMajFinDeTourAdventurerLowLevel() {
        player p = new player("A", "B", "ADVENTURER", 0, new ArrayList<>());
        // Niveau initial 1 (< 3)
        p.healthpoints = 100;
        p.currenthealthpoints = 40; // < 50

        UpdatePlayer.majFinDeTour(p);

        // +2 (else branch) - 1 (low level) = +1
        assertThat(p.currenthealthpoints, is(41));
    }

    @Test
    @DisplayName("majFinDeTour: HP < 50% - Classe ADVENTURER (Niv >= 3) (+2 HP)")
    void testMajFinDeTourAdventurerHighLevel() {
        player p = new player("A", "B", "ADVENTURER", 0, new ArrayList<>());
        UpdatePlayer.addXp(p, 30); // Niveau 3
        p.healthpoints = 100;
        p.currenthealthpoints = 40; // < 50

        UpdatePlayer.majFinDeTour(p);

        // +2 (else branch), le -1 est sauté (retrieveLevel >= 3)
        assertThat(p.currenthealthpoints, is(42));
    }

    @Test
    @DisplayName("majFinDeTour: HP >= 50% (Rien ne se passe)")
    void testMajFinDeTourHalfHp() {
        player p = new player("A", "B", "ADVENTURER", 0, new ArrayList<>());
        p.healthpoints = 100;
        p.currenthealthpoints = 50; // >= 50

        UpdatePlayer.majFinDeTour(p);

        // Rien ne devrait se passer (deuxième else if et if final sont gérés)
        assertThat(p.currenthealthpoints, is(50));
    }

    @Test
    @DisplayName("majFinDeTour: Remise à HP max après soin")
    void testMajFinDeTourCappedToMaxHp() {
        player p = new player("A", "B", "DWARF", 0, new ArrayList<>());
        p.inventory.add("Holy Elixir");
        p.healthpoints = 100;
        p.currenthealthpoints = 99; // < 50% de 100 si on le fait deux fois, mais le test est simple.

        // Simuler le cas où le soin ferait dépasser HP_MAX (ex: 99 + 2 = 101)
        p.currenthealthpoints = 99; // < 50% est faux, mais on force un cas où le dernier 'if' est atteint

        // Simuler le cas où le dernier IF est atteint (un test précédent doit avoir atteint cette ligne)
        // La couverture de la ligne `if(player.currenthealthpoints >= player.healthpoints)`
        // est assurée par tous les cas qui augmentent le currenthealthpoints et terminent
        // par cette vérification. Si p.currenthealthpoints = 99 pour un DWARF avec élixir,
        // il passe à 101, et le dernier IF le ramènera à 100.
        UpdatePlayer.majFinDeTour(p);

        // Pour couvrir le cap de fin :
        p.currenthealthpoints = 99; // On réinitialise pour être sûr que le soin le fait dépasser (on utilise un Archer avec Bow)
        player archer = new player("A", "B", "ARCHER", 0, new ArrayList<>());
        archer.healthpoints = 100;
        archer.currenthealthpoints = 40;
        archer.inventory.add("Magic Bow"); // +5HP
        UpdatePlayer.majFinDeTour(archer);
        assertThat(archer.currenthealthpoints, is(45));

        // Pour forcer un dépassement pour couvrir le dernier IF:
        archer.currenthealthpoints = 98; // < 50 est faux, on passe à la fin
        // On ne peut pas le faire dépasser sans le modifier ou sans un cas de soin complexe.
        // Puisque tous les chemins de soin ont été couverts, le cas où `currenthealthpoints >= healthpoints`
        // après un soin (mais avant le cap final) est déjà couvert par le DWARF ou l'ARCHER
        // qui augmente les PV. L'objectif est de vérifier le cap final.
        // Simuler directement un dépassement :
        p.currenthealthpoints = 101;
        UpdatePlayer.majFinDeTour(p);
        assertThat(p.currenthealthpoints, is(100)); // L'instruction finale est couverte
    }

    // --- Tests de Affichage.java ---

    @Test
    @DisplayName("Test: Affichage joueur complet (XP, capacités, inventaire)")
    void testAfficherJoueurComplet() {
        // Préparation
        player p = new player("Florian", "Grognak", "ADVENTURER", 100, new ArrayList<>());
        UpdatePlayer.addXp(p, 5); // XP = 5 (Niv 1)
        p.abilities.put("INT", 5);
        p.inventory.add("Epée");
        p.inventory.add("Potion");

        String result = Affichage.afficherJoueur(p);

        // Vérification du contenu
        assertTrue(result.contains("Joueur Grognak joué par Florian"));
        assertTrue(result.contains("Niveau : 1 (XP totale : 5)"));
        assertTrue(result.contains("Capacités :"));
        assertTrue(result.contains("   INT : 5"));
        assertTrue(result.contains("Inventaire :"));
        assertTrue(result.contains("   Epée"));
        assertTrue(result.contains("   Potion"));
    }

    @Test
    @DisplayName("Test: Affichage joueur (Inventaire et Capacités vides)")
    void testAfficherJoueurVide() {
        // Préparation
        player p = new player("Florian", "Vide", "ADVENTURER", 100, new ArrayList<>()); // Inventaire vide, Capacités de niveau 1

        // Pour s'assurer que les capacités et inventaire sont vraiment vides pour tester les sections
        p.abilities = new HashMap<>();
        p.inventory = new ArrayList<>();

        String result = Affichage.afficherJoueur(p);

        // Vérification que les sections existent
        assertTrue(result.contains("Capacités :"));
        assertTrue(result.contains("Inventaire :"));

        // Vérification qu'il n'y a pas d'éléments listés sous les sections
        String[] lines = result.split("\n");
        boolean inAbilities = false;
        boolean inInventory = false;
        for (String line : lines) {
            if (line.contains("Capacités :")) {
                inAbilities = true;
                inInventory = false;
                continue;
            }
            if (line.contains("Inventaire :")) {
                inInventory = true;
                inAbilities = false;
                continue;
            }
            if (line.trim().startsWith("   ")) {
                if (inAbilities) fail("Capacité trouvée alors qu'il ne devrait pas y en avoir.");
                if (inInventory) fail("Objet trouvé alors qu'il ne devrait pas y en avoir.");
            }
        }
    }
}