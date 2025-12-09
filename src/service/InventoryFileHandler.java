package service;

import domain.*;
import java.io.*;
import java.util.*;

// Håndterer gemning og indlæsning af inventory til/fra tekstfil i et simpelt custom format.
public class InventoryFileHandler {

    // Gemmer hele inventory-tilstanden til en tekstfil
    public static void save(Inventory inventory, String path) {
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
                    writer.println("Weapon;name=" + w.getName()
                            + ";rarity=" + w.getRarity()
                            + ";weight=" + w.getWeight()
                            + ";damage=" + w.getDamage()
                            + ";hand=" + w.getHandtype());
                } else if (item instanceof Armour a) {
                    writer.println("Armour;name=" + a.getName()
                            + ";rarity=" + a.getRarity()
                            + ";weight=" + a.getWeight()
                            + ";defence=" + a.getDefence()
                            + ";slot=" + a.getSlot());
                } else if (item instanceof Consumable c) {
                    writer.println("Consumable;name=" + c.getName()
                            + ";rarity=" + c.getRarity()
                            + ";weight=" + c.getWeight()
                            + ";effect=" + c.getEffectType()
                            + ";stack=" + c.getStackSize());
                }
            }

            System.out.println("Inventory saved to file: " + path);

        } catch (Exception e) {
            System.out.println("Error saving file: " + e.getMessage());
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
                String type = parts[0];

                Map<String, String> map = new HashMap<>();
                for (int i = 1; i < parts.length; i++) {
                    String[] kv = parts[i].split("=");
                    map.put(kv[0], kv[1]); // key/value fra fil
                }

                switch (type) {

                    case "Weapon" -> {
                        Weapon w = new Weapon(
                                map.get("name"),
                                "Weapon",
                                map.get("rarity"),
                                Double.parseDouble(map.get("weight"))
                        );
                        w.setDamage(Integer.parseInt(map.get("damage")));
                        w.setHandtype(map.get("hand"));
                        inventory.addItem(w);
                    }

                    case "Armour" -> {
                        Armour a = new Armour(
                                map.get("name"),
                                "Armour",
                                map.get("rarity"),
                                Double.parseDouble(map.get("weight")),
                                Integer.parseInt(map.get("defence")),
                                map.get("slot")
                        );
                        inventory.addItem(a);
                    }

                    case "Consumable" -> {
                        Consumable c = new Consumable(
                                map.get("name"),
                                "Consumable",
                                map.get("rarity"),
                                Double.parseDouble(map.get("weight"))
                        );
                        c.setEffectType(map.get("effect"));
                        c.setStackSize(Integer.parseInt(map.get("stack")));
                        inventory.addItem(c);
                    }
                }
            }

            return true;

        } catch (Exception e) {
            System.out.println("Error loading file: " + e.getMessage());
            return false;
        }
    }
}
