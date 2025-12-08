package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Inventory {

    // kapacitet for inventory
    private double maxWeight = 50;
    private int maxSlots = 192;
    private int unlockedSlots = 32;

    // selve listen af items
    private final ArrayList<Item> slots = new ArrayList<>();

    public Inventory() {
    }

    public Inventory(double maxWeight, int maxSlots, int unlockedSlots) {
        this.maxWeight = maxWeight;
        this.maxSlots = maxSlots;
        this.unlockedSlots = unlockedSlots;
    }

    // --- settings / metadata ---

    public double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public void setMaxSlots(int maxSlots) {
        this.maxSlots = maxSlots;
    }

    public int getUnlockedSlots() {
        return unlockedSlots;
    }

    public void setUnlockedSlots(int unlockedSlots) {
        this.unlockedSlots = unlockedSlots;
    }

    public double getTotalWeight() {
        double sum = 0;
        for (Item item : slots) {
            sum += item.getWeight();
        }
        return sum;
    }

    // bruges ved load fra fil – vi starter med tomt inventory
    public void clearItems() {
        slots.clear();
    }

    // --- core logik: add / remove / find ---

    public boolean addItem(Item item) {

        // stacking for consumables med samme navn
        if (item instanceof Consumable cNew) {
            for (Item existing : slots) {
                if (existing instanceof Consumable cOld &&
                        cOld.getName().equalsIgnoreCase(cNew.getName())) {

                    // læg stacksize sammen i samme slot
                    cOld.setStackSize(cOld.getStackSize() + cNew.getStackSize());
                    return true; // ingen ekstra slot ved stacking
                }
            }
        }

        // slot + vægt check for helt nye items
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

    public boolean isEmpty() {
        return slots.isEmpty();
    }

    // UI får kun lov at læse listen, ikke ændre den direkte
    public List<Item> getItems() {
        return Collections.unmodifiableList(slots);
    }

    // --- sortering ---

    public void sortByName() {
        slots.sort(Comparator.comparing(Item::getName));
    }

    public void sortByWeight() {
        slots.sort(Comparator.comparing(Item::getWeight));
    }

    public void sortByType() {
        slots.sort(Comparator.comparing(Item::getType));
    }

    public void sortByRarity() {
        int n = slots.size();

        for (int i = 1; i < n; i++) {
            Item current = slots.get(i);
            int currentRank = rarityRank(current.getRarity());
            int j = i - 1;

            while (j >= 0 && rarityRank(slots.get(j).getRarity()) > currentRank) {
                slots.set(j + 1, slots.get(j));
                j--;
            }

            // indsæt current
            slots.set(j + 1, current);
        }
    }



    // --- tekstlig oversigt ---

    public String getDetailedOverview() {
        StringBuilder sb = new StringBuilder();

        sb.append("====== INVENTORY OVERVIEW ======\n");
        sb.append("-------------------------------------------------------------\n");
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

    public boolean buyInventorySlots(int amount) {
        if (amount <= 0) {
            System.out.println("Amount must be greater than 0.");
            return false;
        }

        if (unlockedSlots + amount > maxSlots) {
            return false;
        }

        unlockedSlots += amount;
        return true;
    }

    private int rarityRank(String rarity) {
        if (rarity == null) return 0;

        return switch (rarity.toLowerCase()) {
            case "common" -> 1;
            case "uncommon" -> 2;
            case "rare" -> 3;
            case "epic" -> 4;
            default -> 0;
        };
    }

    @Override
    public String toString() {
        return getDetailedOverview();
    }
}
