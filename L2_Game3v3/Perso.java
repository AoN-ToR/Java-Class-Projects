import java.util.HashMap;
import java.util.Random;

// Offence : attack, mod, ability, item
// Defence : hp, mod, ability, item

public class Perso {
    String team = "None";

    // char selection id
    String name;
    String pseudo;

    // {max, current}
    int[] hp = { 0, 0 };

    // {id att1, id att2, id att3, id att4}
    // int[] att_nb = {id, damage};
    // string[] att_name = {"id", "name", "desc"};
    // int[][] att_effects = {{id}, {chance, stat, effect_amount, target}
    int[] atts_id = new int[4];

    // 0-speed, 1-att, 2-defence, 3-dmg over time, 4-all, 5-cooldown, 6-protect
    double[] mod;
    double[] true_stat;

    int ability_id;
    int item_id;

    // 3 heals per combat, each heal restores 20 pv
    int heal = 3;
    Random random;

    // action to be performed this turn
    int action;
    Perso[] target = new Perso[3];
    int att_id;
    // helper: avoid applying per-target effects (like cooldown) multiple times
    // during one action
    boolean effectAppliedThisAction = false;

    // class-dependant variables
    String[] classes_name = { "Warrior", "Mage", "Ranger", "Healer", "Golem", "Rogue" };
    // Speed, Att, Def, Dmg over Time, All, Cooldown, Protect
    double[][] classes_base_stats = { { 300, 300, 300, 0, 100, 0, 0 },
            { 270, 300, 300, 0, 100, 0, 0 },
            { 285, 300, 300, 0, 100, 0, 0 },
            { 255, 300, 300, 0, 100, 0, 0 },
            { 240, 300, 300, 0, 100, 0, 0 },
            { 330, 300, 300, 0, 100, 0, 0 }, };
    int[] classes_base_hp = { 100, 75, 100, 80, 120, 85 };
    int[][] classes_attack_ids = { { 0, 1, 2, 3 }, { 4, 5, 6, 7 }, { 8, 9, 10, 11 }, { 12, 13, 14, 15 },
            { 16, 17, 18, 19 }, { 20, 21, 22, 23 } };

    public Perso(String pseudo, int char_id, int ability_id,
            int item_id, Random random) {

        this.random = random;

        // class pseudo is name if pseudo == None
        this.name = classes_name[char_id];
        if (!pseudo.equals("")) {
            this.pseudo = pseudo;
        } else {
            this.pseudo = classes_name[char_id];
        }

        // attacks based on class
        this.atts_id = classes_attack_ids[char_id];

        // stats based on class - set current stats to class stats
        this.mod = classes_base_stats[char_id];
        this.true_stat = classes_base_stats[char_id].clone();

        // hp based on class - set current hp to max hp
        this.hp[0] = classes_base_hp[char_id];
        this.hp[1] = classes_base_hp[char_id];

        this.ability_id = ability_id;
        // add stats boost from abilities
        if (ability_id == 0) {
            // Simple (double damage delt and taken)
            this.mod[4] += 100.0;
        } else if (ability_id == 1) {
            // Alert (increases speed)
            this.mod[0] += 50.0;
        } else if (ability_id == 3) {
            // Tough (increases hp)
            this.hp[0] += 20.0;
            this.hp[1] += 20.0;
        } else if (ability_id == 4) {
            // Huge Power (increases attack)
            this.mod[1] += 50.0;
        } else if (ability_id == 5) {
            // Rough Skin (increases defence)
            this.mod[2] += 50.0;
        } else {
            // Clear Body
        }

        this.item_id = item_id;
        // add stats boost from items
        if (item_id == 0 || item_id == 5 || item_id == 6) {
            // Leftovers, White herb, None
            // Have no stat changes
        } else if (item_id == 1) {
            // Expert Belt (increases attack)
            this.mod[1] += 50.0;
            this.true_stat[1] += 50.0;
        } else if (item_id == 2) {
            // Wind Materia (increases speed)
            this.mod[0] += 50.0;
            this.true_stat[0] += 50.0;
        } else if (item_id == 3) {
            // Cloak of Protection (increases defence)
            this.mod[2] += 50.0;
            this.true_stat[2] += 50.0;
        } else if (item_id == 4) {
            // Spike Necklace (increases all_mod)
            this.mod[4] += 50.0;
            this.true_stat[4] += 50.0;
        }
    }

    // ACTIONS (DONE)

    // takes target's hp array as parameter to modify it.
    public void healAction(Perso target) {
        this.heal -= 1;
        int max_hp = target.hp[0];
        System.out.println("[" + target.team + "] " + this.pseudo + " gives a potion to " + "[" + target.team + "] "
                + target.pseudo);
        // make sure current hp does not exceed max pv
        if (target.hp[1] <= 0) {
            System.out.println("[" + target.team + "] " + target.pseudo + " is already down.");
        } else if ((target.hp[1] + 20) > max_hp) {
            System.out.println(
                    "[" + target.team + "] " + target.pseudo + " was healed for " + (max_hp - target.hp[1]) + ".");
            target.hp[1] = max_hp;
        } else {
            System.out.println("[" + target.team + "] " + target.pseudo + " was healed for 20 hp.");
            target.hp[1] += 20;
        }
    }

    // reduces current target's hp by dmg amount
    public void damage(Perso target, double dmg) {
        if (dmg < 0 && target.hp[1] > 0) {
            target.hp[1] -= dmg;
            System.out.println(target.pseudo + " was healed");
            if (target.hp[1] > target.hp[0]) {
                target.hp[1] = target.hp[0];
            }
        } else if (target.hp[1] <= 0) {
            // target that has 0hp is attacked
            System.out.println(target.pseudo + "is already down.");

        } else if (target.mod[6] != 0) {
            // protect mod
            System.out.println(target.pseudo + " is protected from damage.");

        } else if (dmg == 0) {
            // nothing happens

        } else {
            // accounts for defence stats
            double target_defence = 300.0 / target.mod[2];
            double target_all = target.mod[4] / 100.0;
            double true_dmg = dmg * (target_defence) * (target_all);
            target.hp[1] -= (int) true_dmg;
            System.out.println(target.pseudo + " takes " + (int) (true_dmg) + " hp");
            // check if character is defeated
            if (target.hp[1] <= 0) {
                target.hp[1] = 0;
                System.out.println("[" + target.team + "] " + "Character " + target.pseudo + " has been defeated.");
            }
        }
    }

    public void effect(Perso target, int[] effects, double Attack_Dmg) {
        // % chance to apply effect
        if (random.nextInt(100) < effects[0]) {
            // apply effect

            // if cooldown: apply only once per action (multi-target attacks call effect
            // multiple times)
            if (effects[1] == 5) {
                if (!this.effectAppliedThisAction) {
                    this.effectAppliedThisAction = true;
                    this.mod[5] = 1;
                    // if white herb (one-time cooldown skip)
                    if (this.item_id == 5) {
                        this.mod[5] = 0;
                        System.out.println(this.pseudo + " consumued its white herb to skip the cooldown.");
                        this.item_id = 6;
                    }
                }

                // if reset stats
            } else if (effects[1] == 6) {
                target.mod[0] = target.true_stat[0];
                target.mod[1] = target.true_stat[1];
                target.mod[2] = target.true_stat[2];
                target.mod[3] = 0;
                target.mod[4] = 100.0;
                target.mod[5] = 0;
                target.mod[6] = 0;

                // if protect
            } else if (effects[1] == 7) {
                target.mod[6] = 1;
                this.mod[5] = 1;

            }
            // else modify the stat by the effect amount if target doesn't have clear body
            else if (Attack_Dmg == 0 || (effects[1] == 3)) {
                if (target.ability_id == 2 && (effects[1] != 5 && effects[1] != 7)) {
                    System.out.println(
                            "Clear body protects " + target.pseudo + " from stat changes and overtime damage.");
                    return;
                }
                HashMap<Integer, String> effect_name = new HashMap<>();
                effect_name.put(0, "speed");
                effect_name.put(1, "attack");
                effect_name.put(2, "defence");
                effect_name.put(3, "dmg over time");
                effect_name.put(4, "all");
                effect_name.put(5, "cooldown");
                effect_name.put(6, "protect");

                double temp = target.mod[effects[1]];
                target.mod[effects[1]] += effects[2];
                // if stat
                if (target.mod[effects[1]] < 0) {
                    target.mod[effects[1]] = 0;
                }
                String adj = (effects[2] == 50 || effects[2] == -50) ? "" : " sharply";
                String name = effect_name.get(effects[1]);
                if (effects[1] == 0 || effects[1] == 1 || effects[1] == 2) {
                    if (temp > target.mod[effects[1]]) {
                        System.out.println(target.pseudo + "'s " + name + " has decreased" + adj + ".");
                    } else {
                        System.out.println(target.pseudo + "'s " + name + " has increased" + adj + ".");
                    }
                }
            } else {
                if (target.ability_id == 2) {
                    System.out.println("Clear body protects " + target.pseudo + " from stat changes.");
                    return;
                }
                HashMap<Integer, String> effect_name = new HashMap<>();
                effect_name.put(0, "speed");
                effect_name.put(1, "attack");
                effect_name.put(2, "defence");

                double temp = this.mod[effects[1]];
                this.mod[effects[1]] += effects[2];
                String adj = (effects[2] == 50 || effects[2] == -50) ? "" : " sharply";
                String name = effect_name.get(effects[1]);
                if (effects[1] == 0 || effects[1] == 1 || effects[1] == 2) {
                    if (temp > this.mod[effects[1]]) {
                        System.out.println(this.pseudo + "'s " + name + " has decreased" + adj + ".");
                    } else {
                        System.out.println(this.pseudo + "'s " + name + " has increased" + adj + ".");
                    }
                }
            }
        }

    }

    // makes an attack on a target
    public void attack(Perso target, double Attack_Dmg, int[] att_effects, String att_name) {
        // inflicts attack damage to target
        if (!this.pseudo.equals(target.pseudo)) {
            System.out.println(this.pseudo + " used " + att_name + " on " + target.pseudo + ".");
        } else {
            System.out.println(this.pseudo + " used " + att_name + " on theirself.");
        }

        double att_mod = this.mod[1] / 300.0;
        double all_mod = this.mod[4] / 100.0;
        // accounts for offence stats
        double dmg = Attack_Dmg * (att_mod) * (all_mod);
        if (target.hp[1] <= 0) {
            System.out.println(target.pseudo + " is already down.");
        } else {
            damage(target, (int) dmg);
        }

        // applies effects
        int[] effects = att_effects;
        if (effects[0] != 0) {
            effect(target, effects, Attack_Dmg);
        }
    }

}
