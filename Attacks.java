public class Attacks {
        String[] name = {
                        "Slash", "Shield Bash", "Power Strike", "Swords Danse", // Warrior
                        "Magic Missile", "Entangle", "Shield", "Arcane Blast", // Mage
                        "Arrow Shot", "Trap", "Eagle Eye", "Rapid Fire", // Ranger
                        "Light Heal", "Great Reset", "Great Heal", "Mace Strike", // Healer
                        "Heavy Slam", "Protect", "Iron Defence", "Ground Pound", // Golem
                        "Dagger Throw", "Poison Dart", "Neutralizing Gas", "Sneak Attack" // Rogue
        };

        String[] desc = {
                        // Warrior
                        "A quick slash dealing moderate damage.",
                        "A bash, potentially raising your defence.",
                        "A powerful strike dealing high damage (Cooldown: 1).",
                        "An incantation, raising your attack sharply.",
                        // Mage
                        "A basic magic attack dealing light damage.",
                        "A spell that lowers all opponents speed.",
                        "A protective shield prevents you from taking dmg for 1 turn (Cooldown: 1).",
                        "A blast of arcane energy dealing heavy damage (Cooldown: 1).",
                        // Ranger
                        "A precise arrow shot dealing moderate damage.",
                        "A trap that triggers when the target attacks, dealing damage over time.",
                        "A focused shot that may raise your attack while dealing light damage.",
                        "A rapid series of shots dealing moderate dmg to all enemies (Cooldown: 1).",
                        // Healer
                        "A light heal restoring a small amount of HP.",
                        "A spell that resets all stat changes and item/ability stat effect on the target.",
                        "A great heal restoring a large amount of HP (Cooldown: 1).",
                        "A flail strike dealing light damage.",
                        // Golem
                        "A heavy slam dealing moderate damage.",
                        "Prevents you from taking dmg for 1 turn (Cooldown: 1).",
                        "Raises your defense sharply (Cooldown: 1).",
                        "A ground pound dealing light damage to all enemies.",
                        // Rogue
                        "A quick dagger throw dealing light damage.",
                        "A poison dart that deals damage over time.",
                        "A gas that neutralizes all stat changes and item/ability stat effect on the target.",
                        "A quick attack, may raise your speed."
        };

        int[] dmg = {
                        20, 15, 30, 0, // Warrior
                        15, 0, 0, 30, // Mage
                        20, 0, 10, 15, // Ranger
                        -20, 0, -40, 15, // Healer
                        20, 0, 0, 10, // Golem
                        15, 5, 0, 10 }; // Rogue

        int[][] eff = {
                        // POUR CHAQUE LIGNE
                        // ID 0: % chance of effect occurring
                        // ID 1: 0- speed, 1- attack, 2- defence, 3- dmg over time, 4- all, 5- cooldown,
                        // 6- reset, 7- protect
                        // ID 2: valeur de l'effet
                        // ID 3: ciblage

                        // Warrior
                        { 0, 0, 0, 0 },
                        { 50, 2, 50, 0 },
                        { 100, 5, 0, 0 },
                        { 100, 1, 100, 1 },
                        // Mage
                        { 0, 0, 0, 0 },
                        { 100, 0, -50, 2 },
                        { 100, 7, 0, 1 },
                        { 100, 5, 0, 0 },
                        // Ranger
                        { 0, 0, 0, 0 },
                        { 100, 3, 10, 0 },
                        { 50, 1, 50, 0 },
                        { 100, 5, 0, 2 },
                        // Healer
                        { 0, 0, 0, 0 },
                        { 100, 6, 0, 0 },
                        { 100, 5, 0, 0 },
                        { 0, 0, 0, 0 },
                        // Golem
                        { 0, 0, 0, 0 },
                        { 100, 7, 0, 1 },
                        { 100, 2, 100, 1 },
                        { 0, 0, 0, 2 },
                        // Rogue
                        { 0, 0, 0, 0 },
                        { 100, 3, 10, 0 },
                        { 100, 6, 0, 0 },
                        { 50, 0, 50, 0 }
        };
}