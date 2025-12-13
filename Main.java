import java.util.Random;
import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {

        System.out.println("Welcome to the Java Program!");
        // start GUI (will also mirror System.out into the text box)
        GameScreen.init();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Random random = new Random();

        // Character Stat Section
        Attacks att = new Attacks();

        // Sample Character Stats
        String[] characters = { "Warrior", "Mage", "Ranger", "Healer", "Golem", "Rogue" };

        // Sample Players
        Player[] players = new Player[2];
        do { // Ensure unique player names
            players[0] = new Player(UI.askForString("Player 1's name:"));
            players[1] = new Player(UI.askForString("Player 2's name:"));
        } while (players[0].name.equals(players[1].name));

        Perso[] target_team = new Perso[6];

        // Team Selection
        for (int x = 0; x < 2; x++) {
            for (int i = 0; i < 3; i++) {
                int char_id = 0;

                do {
                    System.out.println();
                    // show descriptive buttons with class names
                    char_id = UI.askForChoiceIndex(
                            "Choose class for " + players[x].name + " - character " + (i + 1),
                            characters);
                    if (char_id < 1 || char_id > characters.length) {
                        System.out.println("number must be between 1 and " + characters.length + "\n");
                    }
                } while (char_id < 1 || char_id > characters.length);

                char_id -= 1; // adjust for 0-based index

                // Create a character in player's team based on chosen character

                // Choose a name
                String pseudo = "";
                while (pseudo == null || pseudo.equals("")) {
                    pseudo = JOptionPane.showInputDialog(null,
                            "You chose a " + characters[char_id] + ". Give them a unique name:");
                    if (pseudo == null)
                        pseudo = ""; // treat cancel as empty to re-prompt
                    for (int a = 0; a < ((x * 3) + i); a++) {
                        if (target_team[a].pseudo.equals(pseudo)) {
                            pseudo = "";
                        }
                    }
                }

                // Choose an ability (separate names and descriptions)
                String[] abilityNames = { "Simple", "Alert", "Clear Body", "Tough", "Huge Power", "Rough Skin" };
                String[] abilityDescs = { "takes and deal double damage", "Increases speed",
                        "Prevents any stat change", "Increases base hp",
                        "Increases attack stat", "Increases defence stat" };

                System.out.println();
                for (int j = 0; j < abilityNames.length; j++) {
                    System.out.println(abilityNames[j] + ": " + abilityDescs[j]);
                }
                System.out.println();

                int ability_id = 0;
                do {
                    ability_id = UI.askForChoiceIndex(
                            "Choose ability for " + pseudo + " (" + characters[char_id] + "):", abilityNames);
                    if (ability_id < 1 || ability_id > abilityNames.length) {
                        System.out.println("number must be between 1 and " + abilityNames.length + "\n");
                    }
                } while (ability_id < 1 || ability_id > abilityNames.length);
                ability_id -= 1;

                // Choose an item
                String[] itemNames = { "Leftovers", "Expert Belt", "Wind Materia", "Cloak of Protection",
                        "Spike Necklace", "White Herb" };
                String[] itemDescs = { "Restores 10hp / turn", "Increases attack",
                        "Increases speed", "Increases defence",
                        "Increases damage done and taken", "Cancels first cooldown" };

                System.out.println();
                for (int j = 0; j < itemNames.length; j++) {
                    System.out.println(itemNames[j] + ": " + itemDescs[j]);
                }
                System.out.println();

                int item_id = 0;
                do {
                    item_id = UI.askForChoiceIndex("Choose item for " + pseudo + " (" + characters[char_id] + "):",
                            itemNames);
                    if (item_id < 1 || item_id > itemNames.length) {
                        System.out.println("number must be between 1 and " + itemNames.length + "\n");
                    }
                } while (item_id < 1 || item_id > itemNames.length);
                item_id -= 1;

                target_team[i + x * 3] = new Perso(pseudo, char_id, ability_id, item_id, random);

                target_team[i + x * 3].team = players[x].name;
                players[x].team[i] = target_team[i + x * 3];

                GameScreen.clearLog();
            }
        }

        boolean p1 = true, p2 = true;
        int round = 1;
        while (p1 && p2) {
            GameScreen.clearLog();
            // Update the visual display with current teams and player names
            GameScreen.updateTeams(players[0].name, players[1].name, players[0].team, players[1].team);

            System.out.println("New Round Begins! (Round " + round + ")");

            // --- selection phase: every player chooses actions for their 3 characters ---
            for (int x = 0; x < 2; x++) {
                // Player x's turn (selection only)
                System.out.println();
                System.out.println(players[x].name + "'s turn");
                System.out.println();
                for (int i = 0; i < 3; i++) {
                    // Perso i
                    Perso p = players[x].team[i];
                    players[x].team[i].action = 0;

                    // Check if character can act
                    if (p.hp[1] <= 0) {
                        System.out.println(p.pseudo + " is defeated and cannot act. \n");
                        players[x].team[i].action = 3; // auto pass;
                    }
                    if (p.mod[5] > 0) {
                        System.out.println(p.pseudo + " recovers from their last attack. \n");
                        players[x].team[i].action = 3; // auto pass

                    }

                    // Choose action (selection only)
                    while (players[x].team[i].action != 1 && players[x].team[i].action != 2
                            && players[x].team[i].action != 3) {
                        String[] actionOptions = new String[] { "Attack", "Heal", "Pass" };
                        players[x].team[i].action = UI.askForChoiceIndex("Choose action for " + p.pseudo + ":",
                                actionOptions);
                        System.out.println(players[x].team[i].action + "\n");
                        // attack action -> choose which attack and select targets (but do not execute)
                        if (players[x].team[i].action == 1) {
                            int att_chosen = 0;
                            while (att_chosen < 1 || att_chosen > 4) {
                                String[] attNames = new String[4];
                                String[] attDescs = new String[4];
                                for (int k = 0; k < 4; k++) {
                                    int attid = players[x].team[i].atts_id[k];
                                    attNames[k] = att.name[attid];
                                    attDescs[k] = att.desc[attid];
                                    System.out.println((k + 1) + ": " + attNames[k] + " - " + attDescs[k]);
                                }
                                System.out.println();
                                att_chosen = UI.askForChoiceIndex("Choose attack for " + p.pseudo + ":", attNames);
                            }
                            att_chosen -= 1;

                            int att_id = players[x].team[i].atts_id[att_chosen];
                            players[x].team[i].att_id = att_id;

                            // att target selection (only assign targets)
                            TargetAtt(att.eff[att_id][3], target_team, players[x].team[i].action, players, x, i);

                            // heal action -> target selection only
                        } else if (players[x].team[i].action == 2 && p.heal > 0) {
                            TargetHeal(target_team, players[x].team[i].action, players, x, i);

                        } else if (players[x].team[i].action == 2 && p.heal <= 0) {
                            UI.showMessage(p.pseudo + " has no heals left!");
                            players[x].team[i].action = 0;
                        } else if (players[x].team[i].action == 3) {
                            System.out.println(p.pseudo + " passes the turn.");

                        }

                    }
                    GameScreen.clearLog();
                }
            }

            // --- action phase: after every character has selected their action ---
            Perso[] order = new Perso[6];
            for (int i = 0; i < order.length; i++) {
                order[i] = target_team[i];
            }

            // bubble sort characters by speed for action order (null-safe)
            for (int i = 0; i < order.length - 1; i++) {
                for (int j = i; j < order.length; j++) {
                    double speedJ = (order[j] != null) ? order[j].mod[0] : Double.NEGATIVE_INFINITY;
                    double speedI = (order[i] != null) ? order[i].mod[0] : Double.NEGATIVE_INFINITY;
                    if (speedJ > speedI) {
                        // swap
                        Perso temp = order[j];
                        order[j] = order[i];
                        order[i] = temp;
                    }
                }
            }

            System.out.println("Speed Tier: ");
            for (int i = 0; i < order.length; i++) {
                if (order[i] != null)
                    System.out.println(order[i].mod[0] + ": " + order[i].pseudo);
            }
            System.out.println();

            // reset per-action effect guard so effects that should apply once are allowed
            // again
            for (int i = 0; i < order.length; i++) {
                if (order[i] != null) {
                    order[i].effectAppliedThisAction = false;
                }
            }

            // action phase
            for (int cha = 0; cha < 6; cha++) {
                Perso p = order[cha];
                if (p == null)
                    continue;
                System.out.println("It's " + p.pseudo + "'s turn.");

                // apply damage over time if any
                if (p.mod[3] > 0 && p.hp[1] != 0) {
                    p.damage(p, p.mod[3]);
                    System.out.println(p.pseudo + " takes damage over time.");

                }
                // skips turn if defeated or recovering
                if (p.hp[1] <= 0) {
                    System.out.println(p.pseudo + " is defeated and cannot act.");

                } else if (p.mod[5] > 0) {
                    System.out.println(p.pseudo + " recovers from their last attack.");
                    p.mod[5] -= 1;
                    p.mod[6] = 0;

                    // actions
                } else {
                    if (p.action == 1) {
                        // attack action(s)
                        for (int j = 0; j < p.target.length; j++) {
                            if (p.target[j] != null) {
                                Perso target = p.target[j];
                                int att_id = p.att_id;
                                p.attack(target, att.dmg[att_id], att.eff[att_id],
                                        att.name[att_id]);
                            } else {
                                continue;
                            }
                        }

                    } else if (p.action == 2) {
                        // heal action already processed during selection
                        Perso target = p.target[0];
                        if (target != null) {
                            p.healAction(target);
                            System.out.println("Heal executed on: " + target.pseudo);
                        }
                    } else {
                        // pass action already processed during selection
                        System.out.println(p.pseudo + " passes their turn.");
                    }
                    if (p.item_id == 0 && p.hp[1] != p.hp[0]) {
                        p.hp[1] += 10;
                        System.out.println(p.pseudo + "'s leftovers healed it's hp a little.");
                        if (p.hp[1] >= p.hp[0]) {
                            p.hp[1] = p.hp[0];
                        }
                    }
                }

                GameScreen.updateTeams(players[0].name, players[1].name, players[0].team, players[1].team);

                UI.waitForOK("");

                for (int j = 0; j < p.target.length; j++) {
                    p.target[j] = null;
                }
            }

            // check if both players have at least one charcter above 0 hp (DONE)
            p1 = (players[0].team[0].hp[1] > 0 || players[0].team[1].hp[1] > 0 || players[0].team[2].hp[1] > 0);
            p2 = (players[1].team[0].hp[1] > 0 || players[1].team[1].hp[1] > 0 || players[1].team[2].hp[1] > 0);
            round += 1;

            System.out.println("\nEnd of Round " + round);
            UI.waitForOK("");

        }
        if (p1) {
            System.out.println("The win belongs to " + players[0].name + "!");
        } else {
            System.out.println("The win belongs to " + players[1].name + "!");
        }
    }

    // check if it's an int. If so, returns the int. Else, return 0
    public static int isInteger(String str) {
        if (str == null)
            return 0;
        try {
            int res = Integer.parseInt(str);
            return res;
        } catch (NumberFormatException e) {
            System.out.println("Answer must be a number");
            return 0;
        }
    }

    // target function for the attack action
    public static void TargetAtt(int ciblage, Perso[] target_team, int action, Player[] players, int x,
            int i) {
        // target selection
        if (ciblage == 0) {
            // ciblage au choix
            int target_id = 0;
            while (target_id < 1 || target_id > 6) {
                // build options with descriptive target names
                String[] targetOptions = new String[6];
                String[] targetbuttons = new String[6];
                for (int j = 0; j < 6; j++) {
                    Perso target = target_team[j];
                    targetOptions[j] = "[" + target.team + "] " + target.pseudo + " (HP: " + target.hp[1] + "/"
                            + target.hp[0] + ")";
                    System.out.println((j + 1) + ": " + targetOptions[j]);
                    targetbuttons[j] = target.pseudo;
                }
                target_id = UI.askForChoiceIndex("\nChoose a target to attack: \n", targetbuttons);

            }
            target_id -= 1;
            if (target_id < 0 || target_id >= 6) {
                // cancelled or invalid selection
                players[x].team[i].action = 0;
            } else if (target_team[target_id].hp[1] <= 0) {
                UI.showMessage(target_team[target_id].pseudo + " is already down.");
                target_id = 7;
                players[x].team[i].action = 0;
            } else {
                players[x].team[i].target[0] = target_team[target_id];
            }

        } else if (ciblage == 1) {
            // self-targeting attacks
            players[x].team[i].target[0] = players[x].team[i];
        } else {
            // all enemies
            int count = 0;
            for (int j = 0; j < 6; j++) {
                Perso target = target_team[j];
                if (!target.team.equals(players[x].team[i].team)) {
                    players[x].team[i].target[count] = target;
                    count += 1;
                }
            }
        }
    }

    // target function for the heal fonction
    public static void TargetHeal(Perso[] target_team, int action, Player[] players, int x, int i) {
        // target selection
        int target_id = 0;
        while ((target_id < 1 || target_id > 6)) {
            String[] targetOptions = new String[6];
            String[] targetbuttons = new String[6];
            for (int j = 0; j < 6; j++) {
                Perso target = target_team[j];
                targetOptions[j] = "[" + target.team + "] " + target.pseudo + " (HP: " + target.hp[1] + "/"
                        + target.hp[0] + ")";
                targetbuttons[j] = target.pseudo;
                System.out.println((j + 1) + ": " + targetOptions[j]);
            }
            target_id = UI.askForChoiceIndex("\nChoose a target to heal: \n", targetbuttons);

        }
        target_id -= 1;
        if (target_id < 0 || target_id >= 6) {
            players[x].team[i].action = 0;
        } else if (target_team[target_id].hp[1] <= 0) {
            UI.showMessage(target_team[target_id].pseudo + " is already down.");
            target_id = 7;
            players[x].team[i].action = 0;
        } else {
            players[x].team[i].target[0] = target_team[target_id];
        }
    }
}
