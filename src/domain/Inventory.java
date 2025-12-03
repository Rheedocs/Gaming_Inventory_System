package domain;

import java.io.*;
import java.util.*;

public class Inventory {

    private double maxWeight = 50;
    private int maxSlots = 192;
    private int unlockedSlots = 32;

    private final ArrayList<Item> slots = new ArrayList<>();

    public Inventory() {
    }

    public Inventory(double maxWeight, int maxSlots, int unlockedSlots) {
        this.maxWeight = maxWeight;
        this.maxSlots = maxSlots;
        this.unlockedSlots = unlockedSlots;
    }

    public double getTotalWeight() {
        double sum = 0;
        for (Item item : slots) {
            sum += item.getWeight();
        }
        return sum;
    }

    public boolean addItem(Item item) {
        if (slots.size() >= unlockedSlots) {
            return false;
        }
        if (getTotalWeight() + item.getWeight() > maxWeight) {
            return false;
        }
        slots.add(item);
        return true;
    }

    public boolean removeItem(Item item) {
        return slots.remove(item);
    }

    public Item findItemByName(String name) {
        for (Item item : slots) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    public void sortByName() {
        slots.sort(Comparator.comparing(Item::getName));
    }

    public void sortByWeight() {
        slots.sort(Comparator.comparing(Item::getWeight));
    }

    public void saveToFile(String path) {
        // TODO: filhåndtering
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {

            for (Item item : slots) {

                if (item instanceof Weapon w) {
                    writer.println("Weapon;name=" + w.getName() +
                            ";rarity=" + w.getRarity() +
                            ";weight=" + w.getWeight() +
                            ";damage=" + w.getDamage() +
                            ";hand=" + w.getHandtype());
                } else if (item instanceof Armour a) {
                    writer.println("Armour;name=" + a.getName() +
                            ";rarity=" + a.getRarity() +
                            ";weight=" + a.getWeight() +
                            ";defence=" + a.getDefence() +
                            ";slot=" + a.getSlot());
                } else if (item instanceof Consumable c) {
                    writer.println("Consumable;name=" + c.getName() +
                            ";rarity=" + c.getRarity() +
                            ";weight=" + c.getWeight() +
                            ";effect=" + c.getEffectType() +
                            ";stack=" + c.stackSize());
                }
            }
            System.out.println("Inventory saved to file: " + path);

        } catch (Exception e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    public boolean loadFromFile(String path) {
        try (Scanner scanner = new Scanner(new File(path))) {

            slots.clear();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(";");

                String type = parts[0];
                Map<String, String> map = new HashMap<>();

                for (int i = 1; i < parts.length; i++) {
                    String[] kv = parts[i].split("=");
                    map.put(kv[0], kv[1]);
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
                        slots.add(w);
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
                        slots.add(a);
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
                        slots.add(c);
                    }
                }
            }

            return true;  // success

        } catch (Exception e) {
            return false; // fail
        }
    }

    public int getUnlockedSlots () {
            return unlockedSlots;
    }

    public void unlockSlots ( int amount){
        if (unlockedSlots + amount <= maxSlots) {
            unlockedSlots += amount;
        }
    }

    public int getMaxSlots () {
        return maxSlots;
    }

    public void setMaxSlots ( int maxSlots){
        this.maxSlots = maxSlots;
    }

    public double getMaxWeight () {
        return maxWeight;
    }

    // Lille helper, hvis vi får brug for den flere steder
    public boolean isEmpty() {
        return slots.isEmpty();
    }

    // Sikker read-only adgang til listen (UI må gerne kigge, men ikke ændre)
    public List<Item> getItems () {
        return Collections.unmodifiableList(slots);
    }

    public String getDetailedOverview() {
        StringBuilder sb = new StringBuilder();

        sb.append("====== INVENTORY OVERVIEW ======\n");
        sb.append("-------------------------------------------------------------\n");
        // Header skrives KUN én gang, før vi looper
        sb.append(String.format(
                "%-3s %-18s %-12s %-12s %-8s%n",
                "No", "Name", "Type", "Rarity", "Weight"
        ));
        sb.append("-------------------------------------------------------------\n");

        if (slots.isEmpty()) {
            sb.append("Inventory is empty.\n");
        } else {
            int index = 1;
            for (Item item : slots) {
                sb.append(String.format(
                        "%-3d %-18s %-12s %-12s %-8.1f%n",
                        index++,
                        item.getName(),
                        item.getType(),
                        item.getRarity(),
                        item.getWeight()
                ));
            }
        }

        sb.append("-------------------------------------------------------------\n");
        sb.append(String.format(
                "Items: %d | Total weight: %.1f / %.1f | Unlocked slots: %d / %d%n",
                slots.size(),
                getTotalWeight(),
                maxWeight,
                unlockedSlots,
                maxSlots
        ));

        return sb.toString();
    }

    @Override
    public String toString () {
        return getDetailedOverview();
    }
}


