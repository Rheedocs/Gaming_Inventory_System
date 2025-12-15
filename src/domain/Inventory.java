package domain;

import exceptions.ItemNotFound;
import exceptions.MaxWeightReached;
import exceptions.NegativeValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Styrer spillerens inventory: vægtgrænser, slots, items, stacking og sortering.
public class Inventory {

    // kapacitet for inventory
    private double maxWeight = 50;
    private int maxSlots = 192;
    private int unlockedSlots = 32;

    // selve listen af items (kun Inventory må ændre denne direkte)
    private final ArrayList<Item> slots = new ArrayList<>();

    public Inventory() { }

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
        // Domain-validering: negativ maxWeight giver ikke mening
        if (maxWeight < 0) {
            throw new NegativeValues("Max weight cannot be negative.");
        }
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

    // Total vægt i inventory.
    // VIGTIGT: Consumables tæller vægt pr. enhed * stackSize (ellers kan man omgå maxWeight via stacking).
    public double getTotalWeight() {
        double sum = 0;

        for (Item item : slots) {
            if (item instanceof Consumable c) {
                sum += item.getWeight() * c.getStackSize();
            } else {
                sum += item.getWeight();
            }
        }

        return sum;
    }

    // bruges ved load fra fil – vi starter med tomt inventory
    public void clearItems() {
        slots.clear();
    }

    // --- core logik: add / remove / find ---

    // Forsøger at tilføje et item til inventory.
    // Domain er ansvarlig for at håndhæve vægt- og kapacitetsregler.
    // Ved overskridelse af maxWeight kastes en exception (fejlhåndtering via exceptions).
    public boolean addItem(Item item) {

        // stacking for consumables med samme navn (lægger stackSize sammen)
        // MEN: vi skal stadig respektere maxWeight (stacking er ikke “gratis”).
        if (item instanceof Consumable cNew) {

            // beregn hvad denne tilføjelse koster i vægt
            double addedWeight = cNew.getWeight() * cNew.getStackSize();

            // hvis vægtgrænse overskrides, må vi ikke stack
            if (getTotalWeight() + addedWeight > maxWeight) {
                // Max weight overskredet -> domain exception (ikke bare status)
                throw new MaxWeightReached("Max weight exceeded.");
            }

            for (Item existing : slots) {
                if (existing instanceof Consumable cOld &&
                        cOld.getName().equalsIgnoreCase(cNew.getName())) {

                    cOld.setStackSize(cOld.getStackSize() + cNew.getStackSize());
                    return true; // ingen ekstra slot ved stacking
                }
            }
        }

        // tjek for slots og maxWeight for nye items
        if (slots.size() >= unlockedSlots) {
            // Inventory er fuldt (slots).
            // Dette håndteres som en normal tilstand via return-værdi.
            return false;
        }

        // vægt-tjek (for consumables: weight * stackSize)
        double itemWeight = item.getWeight();
        if (item instanceof Consumable c) {
            itemWeight = item.getWeight() * c.getStackSize();
        }

        if (getTotalWeight() + itemWeight > maxWeight) {
            // Max weight overskredet -> domain exception
            throw new MaxWeightReached("Max weight exceeded.");
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

    // Finder et item ud fra navn.
    // Hvis item ikke findes, kastes ItemNotFound.
    // På den måde slipper service og UI for null-checks.
    public Item requireItemByName(String name) throws ItemNotFound {
        Item item = findItemByName(name);
        if (item == null) {
            throw new ItemNotFound("Item not found: " + name);
        }
        return item;
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

    // Sorterer inventory efter rarity ved brug af insertion sort og enum-rækkefølge
    public void sortByRarity() {
        int n = slots.size();

        for (int i = 1; i < n; i++) {
            Item current = slots.get(i);
            int currentRank = current.getRarity().ordinal();
            int j = i - 1;

            // flyt items med højere rarityRank opad i listen
            while (j >= 0 && slots.get(j).getRarity().ordinal() > currentRank) {
                slots.set(j + 1, slots.get(j));
                j--;
            }

            // indsæt current det rigtige sted
            slots.set(j + 1, current);
        }
    }

    // Låser flere slots op hvis amount er gyldig og der er plads til det
    public boolean buyInventorySlots(int amount) {

        // ugyldigt køb
        if (amount <= 0) {
            return false;
        }

        // kan ikke købe forbi max slots
        if (unlockedSlots + amount > maxSlots) {
            return false;
        }

        // opdater unlocked slots
        unlockedSlots += amount;
        return true;
    }

    @Override
    public String toString() {
        // Domain-toString bør være debug/neutral (ikke UI-tabel).
        return "Inventory{items=" + slots.size() +
                ", totalWeight=" + String.format("%.1f", getTotalWeight()) + "/" + String.format("%.1f", maxWeight) +
                ", unlockedSlots=" + unlockedSlots + "/" + maxSlots +
                "}";
    }
}
