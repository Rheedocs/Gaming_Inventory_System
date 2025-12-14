package service;

import domain.*;
import java.io.*;
import java.util.*;

// Håndterer gemning og indlæsning af inventory til/fra tekstfil i et simpelt custom format.
public class InventoryFileHandler {

    // Gemmer hele inventory-tilstanden til en tekstfil
    public static boolean save(Inventory inventory, String path) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {

            // --- metadata om inventory (settings) ---
            writer.println("# Inventory settings");
            writer.println("maxWeight=" + inventory.getMaxWeight());
            writer.println("maxSlots=" + inventory.getMaxSlots());
            writer.println("unlockedSlots=" + inventory.getUnlockedSlots());
            writer.println();

            // --- items (Weapon/Armour/Consumable) som semikolon-separerede linjer ---
            writer.println("# Items");
            for (Item item : inventory.getItems()) {

                if (item instanceof Weapon w) {
                    writer.println("WEAPON;name=" + w.getName()
                            + ";rarity=" + w.getRarity()
                            + ";weight=" + w.getWeight()
                            + ";damage=" + w.getDamage()
                            + ";hand=" + w.getHandType());
                } else if (item instanceof Armour a) {
                    writer.println("ARMOUR;name=" + a.getName()
                            + ";rarity=" + a.getRarity()
                            + ";weight=" + a.getWeight()
                            + ";defence=" + a.getDefence()
                            + ";slot=" + a.getSlot());
                } else if (item instanceof Consumable c) {
                    writer.println("CONSUMABLE;name=" + c.getName()
                            + ";rarity=" + c.getRarity()
                            + ";weight=" + c.getWeight()
                            + ";effect=" + c.getEffectType()
                            + ";stack=" + c.getStackSize());
                }
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    // Læser inventory-tilstand ind fra fil (overskriver nuværende indhold)
    public static boolean load(Inventory inventory, String path) {

        try (Scanner scanner = new Scanner(new File(path))) {

            inventory.clearItems(); // vi vil ikke blande gamle og nye data

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;      // ignorer tomme linjer og kommentarer
                }

                // --- metadata-linjer ---
                if (line.startsWith("maxWeight=")) {
                    inventory.setMaxWeight(Double.parseDouble(line.split("=")[1]));
                    continue;
                }
                if (line.startsWith("maxSlots=")) {
                    inventory.setMaxSlots(Integer.parseInt(line.split("=")[1]));
                    continue;
                }
                if (line.startsWith("unlockedSlots=")) {
                    inventory.setUnlockedSlots(Integer.parseInt(line.split("=")[1]));
                    continue;
                }

                // --- item-linjer ---
                String[] parts = line.split(";");
                ItemType type = ItemType.valueOf(normalizeEnum(parts[0]));

                Map<String, String> map = new HashMap<>();
                for (int i = 1; i < parts.length; i++) {
                    String[] kv = parts[i].split("=");
                    map.put(kv[0], kv[1]); // key/value fra fil
                }

                Rarity rarity = Rarity.valueOf(normalizeEnum(map.get("rarity")));

                switch (type) {
                    case WEAPON -> {
                        HandType handType = HandType.valueOf(normalizeEnum(map.getOrDefault("hand", "ONE_HAND")));
                        Weapon w = new Weapon(
                                map.get("name"),
                                rarity,
                                Double.parseDouble(map.get("weight")),
                                Integer.parseInt(map.get("damage")),
                                handType
                        );
                        inventory.addItem(w);
                    }
                    case ARMOUR -> {
                        ArmourSlot slot = ArmourSlot.valueOf(normalizeEnum(map.get("slot")));
                        Armour a = new Armour(
                                map.get("name"),
                                rarity,
                                Double.parseDouble(map.get("weight")),
                                Integer.parseInt(map.get("defence")),
                                slot
                        );
                        inventory.addItem(a);
                    }
                    // Consumable indeholder flere felter end konstruktøren,
                    // derfor sættes effectType og stackSize efter oprettelse
                    case CONSUMABLE -> {
                        Consumable c = new Consumable(
                                map.get("name"),
                                rarity,
                                Double.parseDouble(map.get("weight")),
                                Integer.parseInt(map.get("stack"))
                        );
                        c.setEffectType(map.get("effect"));
                        inventory.addItem(c);
                    }
                }
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    // Gør enum-parsing tolerant: "Common" -> "COMMON", "two hand" -> "TWO_HAND"
    private static String normalizeEnum(String text) {
        if (text == null) return "";
        return text.trim()
                .replace("-", "_")
                .replace(" ", "_")
                .toUpperCase();
    }
}
