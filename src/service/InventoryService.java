package service;

import domain.*;
import domain.enums.ArmourSlot;
import domain.enums.HandType;
import domain.enums.ItemType;
import domain.enums.Rarity;
import exceptions.ItemNotFound;
import exceptions.MaxWeightReached;
import exceptions.NegativeValues;

import java.util.ArrayList;
import java.util.List;

// Service-lag mellem UI (Menu) og domain.
// Samler logik for inventory, equipment, søgning, sortering og filhåndtering.
//
// Domain kaster exceptions.
// Service fanger dem og oversætter til brugervenlige beskeder.
// UI står kun for input og output.
public class InventoryService {

    private final Player player;        // den aktive spiller
    private final Inventory inventory;  // genvej til spillerens inventory

    public InventoryService(Player player) {
        this.player = player;
        this.inventory = player.getInventory(); // samme inventory alle metoder bruger
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean isInventoryEmpty() {
        return inventory.isEmpty();
    }

    public boolean isEquipmentEmpty() {
        return player.getEquipment().isEmpty();
    }

    public List<Item> getItems() {
        return inventory.getItems();
    }

    public Item findItemByName(String name) {
        return inventory.findItemByName(name);
    }

    // Opretter det konkrete item (Weapon/Armour/Consumable) ud fra input fra Menu
    public String addItem(
            String name,
            ItemType type,
            Rarity rarity,
            double weight,
            Integer damage,        // Weapon
            HandType handType,     // Weapon
            Integer defence,       // Armour
            ArmourSlot armourSlot, // Armour
            String effectType,     // Consumable
            Integer stackSize      // Consumable
    ) {

        // Service-laget håndterer domain-fejl og omsætter dem til tekst.
        // UI skal ikke kende til exceptions.
        try {

        Item item;

        // Vælg konkret subtype ud fra type og giv de ekstra felter videre
        switch (type) {

            case WEAPON -> {
                // fallback-værdier hvis UI ikke har givet input
                int dmg = (damage != null) ? damage : 0;
                HandType ht = (handType != null) ? handType : HandType.ONE_HAND;

                item = new Weapon(name, rarity, weight, dmg, ht);
            }

            case ARMOUR -> {
                int def = (defence != null) ? defence : 0;
                ArmourSlot slot = (armourSlot != null) ? armourSlot : ArmourSlot.CHEST;

                item = new Armour(name, rarity, weight, def, slot);
            }

            case CONSUMABLE -> {
                int st = (stackSize != null) ? stackSize : 1;

                Consumable c = new Consumable(name, rarity, weight, st);
                if (effectType != null) c.setEffectType(effectType);
                item = c;
            }

            default -> item = new Item(name, type, rarity, weight);
        }


            // addItem håndterer selv stacking, slots og vægt
            boolean added = inventory.addItem(item);

            return added
                    ? "Item has been added to the inventory!"
                    : "Item could not be added. Inventory is full.";

        } catch (NegativeValues | MaxWeightReached e) {
            // Domain-fejl omsættes til brugerfeedback, så programmet ikke crasher.
            return "Item could not be added. " + e.getMessage();
        }
    }

    public void validateAddWeight(double weight, int stackSize)
            throws NegativeValues, MaxWeightReached {

        if (weight <= 0) {
            throw new NegativeValues("Weight must be greater than 0.");
        }

        if (stackSize <= 0) {
            throw new NegativeValues("Stack size must be greater than 0.");
        }

        double addedWeight = weight * stackSize;
        double totalAfter = inventory.getTotalWeight() + addedWeight;

        if (totalAfter > inventory.getMaxWeight()) {
            throw new MaxWeightReached("Item is too heavy for your inventory.");
        }
    }


    public String removeItemByName(String name) {
        try {
            // Finder item eller kaster ItemNotFound
            Item item = inventory.requireItemByName(name);

            // Fjerner hele item-objektet fra inventory (inkl. hele stacken for consumables)
            if (inventory.removeItem(item)) {
                return "Removed item: " + item.getName();
            }

            return "Item could not be removed.";

        } catch (ItemNotFound e) {
            return e.getMessage();
        }
    }

    public String removeItemByName(String name, int amount) {
        try {
            // Finder item eller kaster ItemNotFound
            Item item = inventory.requireItemByName(name);

            // Guard clause: amount skal være >= 1
            if (amount <= 0) {
                return "Amount must be at least 1.";
            }

            // Kun consumables har stackSize der kan reduceres
            if (item instanceof Consumable c) {
                int current = c.getStackSize();

                // Hvis man prøver at fjerne mere end der er -> fjern hele stacken
                if (amount >= current) {
                    inventory.removeItem(c);
                    return "Removed entire stack of " + c.getName() + " (x" + current + ").";
                }

                // Ellers reducer stackSize
                c.setStackSize(current - amount);
                return "Removed " + amount + " from " + c.getName()
                        + " | Remaining in stack: " + c.getStackSize();
            }

            // Ikke-consumable: "amount" giver ikke mening -> fjern hele itemet som normalt
            if (inventory.removeItem(item)) {
                return "Removed item: " + item.getName();
            }

            return "Item could not be removed.";

        } catch (ItemNotFound e) {
            return e.getMessage();
        }
    }

    // Bruger et consumable og håndterer stack-logik.
    // Tjekker med instanceof for at sikre korrekt type før cast.
    public String useConsumable(String name) {

        Item item;
        try {
            item = inventory.requireItemByName(name);
        } catch (ItemNotFound e) {
            return e.getMessage();
        }

        if (!(item instanceof Consumable c)) {
            return "Item is not a consumable.";
        }

        int currentStack = c.getStackSize();

        // (Valgfrit) ekstra guard, hvis data skulle være corrupt
        if (currentStack <= 0) {
            inventory.removeItem(c);
            return "Consumable stack was invalid (0). Item removed.";
        }

        int remaining;

        if (currentStack > 1) {
            remaining = currentStack - 1;
            c.setStackSize(remaining); // OK: stadig >= 1
        } else {
            // currentStack == 1 -> brug den sidste og fjern itemet fra inventory
            inventory.removeItem(c);
            remaining = 0;

            // VIGTIGT: IKKE c.setStackSize(0) pga domain-regel (>= 1)
        }

        String effect = c.getEffectType();
        String message = "Used consumable: " + c.getName();

        if (effect != null && !effect.isBlank()) {
            message += " (" + effect + ")";
        }

        message += " | Remaining in stack: " + remaining;
        return message;
    }

    // Equipper et item og fjerner det fra inventory hvis equip lykkes.
    // Flytter altså item fra Inventory -> Equipment.
    public String equip(Item item) {

        if (item instanceof Weapon w) {

            // Equipment returnerer 0-2 items der blev skubbet ud
            List<Item> replaced = player.getEquipment().equipWeapon(w);

            // Ny weapon flyttes fra inventory -> equipment
            inventory.removeItem(w);

            // Skubbede items flyttes tilbage til inventory
            for (Item r : replaced) {
                inventory.addItem(r);
            }

            return "Equipped weapon: " + w.getName();
        }

        if (item instanceof Armour a) {

            Item replaced = player.getEquipment().equipArmour(a);

            inventory.removeItem(a);

            if (replaced != null) {
                inventory.addItem(replaced);
            }

            return "Equipped armour: " + a.getName();
        }

        return "Item cannot be equipped.";
    }

    // Unequipper et item og lægger det tilbage i inventory.
    // Flytter altså item fra Equipment -> Inventory.
    // VIGTIGT: hvis inventory ikke kan tage imod (full/weight), så ruller vi tilbage.
    public String unequip(String slot) {

        Item removed = player.getEquipment().unequip(slot);

        if (removed == null) {
            return "Nothing equipped in " + slot + ".";
        }

        try {
            boolean added = inventory.addItem(removed);

            if (!added) {
                // rollback så vi ikke mister items (inventory full/slots)
                player.getEquipment().restoreToSlot(slot, removed);
                return "Cannot unequip " + removed.getName() + ". Inventory is full.";
            }

            return "Unequipped " + removed.getName() + " from " + slot;

        } catch (MaxWeightReached e) {
            // Domain exception fanget.
            // Rollback sikrer at item ikke forsvinder.
            player.getEquipment().restoreToSlot(slot, removed);
            return "Cannot unequip " + removed.getName() + ". " + e.getMessage();
        }
    }

    public void sortByName() {
        inventory.sortByName();
    }

    public void sortByWeight() {
        inventory.sortByWeight();
    }

    // ekstra sorteringsmuligheder (bonus men nice)
    public void sortByType() {
        inventory.sortByType();
    }

    public void sortByRarity() {
        inventory.sortByRarity();
    }

    public boolean save(String path) {
        return InventoryFileHandler.save(inventory, path);
    }

    public boolean load(String path) {
        return InventoryFileHandler.load(inventory, path);
    }

    // Finder alle items hvor navnet indeholder søgeteksten (case-insensitive).
    // Tomt eller blank input returnerer en tom liste.
    public List<Item> searchByNameContains(String text) {

        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        text = text.toLowerCase();
        List<Item> results = new ArrayList<>();

        for (Item i : inventory.getItems()) {
            if (i.getName().toLowerCase().contains(text)) {
                results.add(i);
            }
        }
        return results;
    }

    // filtrerer items efter type
    public List<Item> filterByType(ItemType type) {
        List<Item> results = new ArrayList<>();

        for (Item i : inventory.getItems()) {
            if (i.getType() == type) {
                results.add(i);
            }
        }
        return results;
    }

    // Returnerer alle items fra inventory med vægt mellem min og max
    public List<Item> filterByWeight(double min, double max) {
        List<Item> results = new ArrayList<>();

        for (Item i : inventory.getItems()) {
            if (i.getWeight() >= min && i.getWeight() <= max) {
                results.add(i);
            }
        }
        return results;
    }

    public List<Item> filterByRarity(Rarity rarity) {
        List<Item> results = new ArrayList<>();

        for (Item i : inventory.getItems()) {
            if (i.getRarity() == rarity) {
                results.add(i);
            }
        }
        return results;
    }

    // unlocker inventory slots
    public boolean unlockInventorySlots(int amount) {
        return player.getInventory().unlockInventorySlots(amount);
    }
}
