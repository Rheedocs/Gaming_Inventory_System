package service;

import domain.*;
import domain.enums.ArmourSlot;
import domain.enums.HandType;
import domain.enums.ItemType;
import domain.enums.Rarity;

import exceptions.MaxWeightReached;
import exceptions.NegativeValues;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Håndterer gemning og indlæsning af inventory til/fra tekstfil i et simpelt custom format.
// DAL-lag: laver kun fil-I/O og parsing. UI kalder service, service kalder DAL.
public class InventoryFileHandler {

    // Gemmer hele inventory-tilstanden til en tekstfil
    public static boolean save(Inventory inventory, String path) {
        // DAL-ansvar: håndterer fil-I/O.
        // UI/service skal kun se success/fail (boolean).
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {

            // --- FASE 1: metadata om inventory (settings) ---
            writer.println("# Inventory settings");
            writer.println("maxWeight=" + inventory.getMaxWeight());
            writer.println("maxSlots=" + inventory.getMaxSlots());
            writer.println("unlockedSlots=" + inventory.getUnlockedSlots());
            writer.println();

            // --- FASE 2: items (Weapon/Armour/Consumable) ---
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
                    // Undgå at skrive "null" som effekt i filen (giver bøvl ved load)
                    String effect = (c.getEffectType() == null) ? "" : c.getEffectType();

                    writer.println("CONSUMABLE;name=" + c.getName()
                            + ";rarity=" + c.getRarity()
                            + ";weight=" + c.getWeight()
                            + ";effect=" + effect
                            + ";stack=" + c.getStackSize());
                }
            }

            return true;

        } catch (IOException e) {
            // fx invalid path / manglende rettigheder / write-fejl
            return false;
        }
    }

    // Læser inventory-tilstand ind fra fil (overskriver nuværende indhold)
    public static boolean load(Inventory inventory, String path) {

        // DAL-ansvar: filen kan mangle eller være defekt.
        // Vi returnerer false i stedet for at crashe.
        try (Scanner scanner = new Scanner(new File(path))) {

            // --- FASE 1: start clean ---
            inventory.clearItems();

            // --- FASE 2: læs filen linje for linje ---
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                // ignorer tomme linjer og kommentarer
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // --- FASE 3: metadata-linjer ---
                if (line.startsWith("maxWeight=")) {
                    inventory.setMaxWeight(Double.parseDouble(line.split("=", 2)[1]));
                    continue;
                }
                if (line.startsWith("maxSlots=")) {
                    inventory.setMaxSlots(Integer.parseInt(line.split("=", 2)[1]));
                    continue;
                }
                if (line.startsWith("unlockedSlots=")) {
                    inventory.setUnlockedSlots(Integer.parseInt(line.split("=", 2)[1]));
                    continue;
                }

                // --- FASE 4: item-linje ---
                // Format: TYPE;key=value;key=value;...
                String[] parts = line.split(";");
                if (parts.length == 0) continue;

                ItemType type = ItemType.valueOf(normalizeEnum(parts[0]));

                // --- FASE 5: parse key=value felter ---
                Map<String, String> map = parseKeyValuePairs(parts);

                Rarity rarity = Rarity.valueOf(normalizeEnum(map.get("rarity")));

                // --- FASE 6: opret item og tilføj til inventory ---
                // Domain kan kaste exceptions ved oprettelse eller addItem
                try {
                    switch (type) {
                        case WEAPON -> {
                            HandType handType =
                                    HandType.valueOf(normalizeEnum(map.getOrDefault("hand", "ONE_HAND")));
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
                            ArmourSlot slot =
                                    ArmourSlot.valueOf(normalizeEnum(map.get("slot")));
                            Armour a = new Armour(
                                    map.get("name"),
                                    rarity,
                                    Double.parseDouble(map.get("weight")),
                                    Integer.parseInt(map.get("defence")),
                                    slot
                            );
                            inventory.addItem(a);
                        }
                        case CONSUMABLE -> {
                            Consumable c = new Consumable(
                                    map.get("name"),
                                    rarity,
                                    Double.parseDouble(map.get("weight")),
                                    Integer.parseInt(map.get("stack"))
                            );

                            String effect = map.get("effect");
                            if (effect != null && !effect.isBlank()) {
                                c.setEffectType(effect);
                            }

                            inventory.addItem(c);
                        }
                    }
                } catch (NegativeValues | MaxWeightReached e) {
                    // Filindhold bryder domain-regler
                    return false;
                }
            }

            return true;

        } catch (FileNotFoundException e) {
            // Filen findes ikke / forkert path
            return false;
        } catch (IllegalArgumentException e) {
            // Defekt filformat eller enum-parsing
            return false;
        } catch (RuntimeException e) {
            // Sikkerhedsnet: uventede parsing-fejl
            return false;
        }
    }

    // Parser en item-linje af typen: TYPE;key=value;key=value;...
    // Returnerer key/value felterne som Map, så load() forbliver letlæselig.
    private static Map<String, String> parseKeyValuePairs(String[] parts) {
        Map<String, String> map = new HashMap<>();
        for (int i = 1; i < parts.length; i++) {

            String part = parts[i];
            int eq = part.indexOf('=');
            if (eq < 0) continue;

            String key = part.substring(0, eq).trim();
            String value = part.substring(eq + 1).trim();

            map.put(key, value);
        }
        return map;
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
