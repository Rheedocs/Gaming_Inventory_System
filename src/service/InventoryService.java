package service;

import domain.*;

import java.util.ArrayList;
import java.util.List;

// Service-lag mellem UI (Menu) og domain.
// Samler logik for inventory, equipment, søgning, sortering og filhåndtering.
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

    public String getInventoryOverview() {
        return inventory.getDetailedOverview();
    }

    public boolean isInventoryEmpty() {
        return inventory.isEmpty();
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
                : "Item could not be added. Inventory is full or weight limit exceeded.";
    }

    public String removeItemByName(String name) {
        Item item = inventory.findItemByName(name);
        if (item != null && inventory.removeItem(item)) {
            return "Item has been removed from the inventory!";
        } else {
            return "Item could not be removed. Item not found.";
        }
    }

    // Bruger et consumable. Håndterer stackSize, så kun sidste item fjerner hele objektet.
    public String useConsumable(String name) {

        // find itemet i inventory
        Item item = inventory.findItemByName(name);

        if (item == null) {
            return "Could not use consumable. Item not found.";
        }

        // tjek om det faktisk er et consumable
        if (item.getType() != ItemType.CONSUMABLE) {
            return "Item is not a consumable.";
        }

        Consumable c = (Consumable) item;
        int currentStack = c.getStackSize();

        // hvis der er flere end 1 i stacken → reducer stackSize
        if (currentStack > 1) {
            c.setStackSize(currentStack - 1);
        } else {
            // sidste item i stacken → fjern helt fra inventory
            inventory.removeItem(c);
        }

        // feedback til brugeren
        String effect = c.getEffectType();
        String message = "Used consumable: " + c.getName();

        if (effect != null && !effect.isBlank()) {
            message += " (" + effect + ")";
        }

        // hvis der stadig er nogen tilbage i stack
        if (c.getStackSize() > 0) {
            message += " | Remaining in stack: " + c.getStackSize();
        }

        return message;
    }

    // sender equip-ønske videre til Player.Equipment
    public String equip(Item item) {
        if (item instanceof Weapon w) {
            boolean ok = player.getEquipment().equipWeapon(w);
            return ok ? "Equipped weapon: " + w.getName()
                    : "Cannot equip weapon. Hands full.";
        }

        if (item instanceof Armour a) {
            boolean ok = player.getEquipment().equipArmour(a);
            return ok ? "Equipped armour: " + a.getName()
                    : "Cannot equip armour. Invalid slot.";
        }

        return "Item cannot be equipped.";
    }

    public String unequip(String slot) {
        Item removed = player.getEquipment().unequip(slot);
        if (removed != null) {
            return "Unequipped " + removed.getName() + " from " + slot;
        } else {
            return "Nothing equipped in " + slot + ".";
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

    public void save(String path) {
        InventoryFileHandler.save(inventory, path); // filformat håndteres ét sted
    }

    public boolean load(String path) {
        return InventoryFileHandler.load(inventory, path);
    }

    // finder alle items som matcher del af navn (case-insensitive)
    public List<Item> searchByNameContains(String text) {
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

    // filtrerer efter min/max vægt
    public List<Item> filterByWeight(double min, double max) {
        List<Item> results = new ArrayList<>();

        for (Item i : inventory.getItems()) {
            double w = i.getWeight();
            if (w >= min && w <= max) {
                results.add(i);
            }
        }
        return results;
    }

    // filtrerer efter rarity
    public List<Item> filterByRarity(Rarity rarity) {
        List<Item> results = new ArrayList<>();

        for (Item i : inventory.getItems()) {
            if (i.getRarity() == rarity) {
                results.add(i);
            }
        }
        return results;
    }

    public boolean buyInventorySlots(int amount) {
        return player.getInventory().buyInventorySlots(amount);
    }

    public boolean isEquipmentEmpty() {
        return player.getEquipment().isEmpty();
    }
}
