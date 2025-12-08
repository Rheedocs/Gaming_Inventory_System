package service;

import domain.Player;
import domain.Inventory;
import domain.Item;
import domain.Armour;
import domain.Consumable;
import domain.Weapon;

import java.util.ArrayList;
import java.util.List;

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
            String type,
            String rarity,
            double weight,
            Integer damage,       // Weapon
            String handType,      // Weapon
            Integer defence,      // Armour
            String armourSlot,    // Armour
            String effectType,    // Consumable
            Integer stackSize     // Consumable
    ) {
        Item item;

        switch (type.toLowerCase()) {

            case "weapon" -> {
                Weapon w = new Weapon(name, "Weapon", rarity, weight);
                if (damage != null) w.setDamage(damage);        // kun hvis Menu har givet værdi
                if (handType != null) w.setHandtype(handType);
                item = w;
            }

            case "armour", "armor" -> {
                int def = (defence != null) ? defence : 0;      // fallback-standard
                String slot = (armourSlot != null) ? armourSlot : "Chest";
                item = new Armour(name, "Armour", rarity, weight, def, slot);
            }

            case "consumable" -> {
                Consumable c = new Consumable(name, "Consumable", rarity, weight);
                if (effectType != null) c.setEffectType(effectType);
                if (stackSize != null) c.setStackSize(stackSize);
                item = c;
            }

            default -> item = new Item(name, type, rarity, weight);   // ukendt type → almindeligt item
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

    public String useConsumable(String name) {
        Item item = inventory.findItemByName(name);

        if (item == null) {
            return "Could not use consumable. Item not found.";
        }

        if (!item.getType().equalsIgnoreCase("Consumable")) {
            return "Item is not a consumable.";
        }

        Consumable c = (Consumable) item;
        inventory.removeItem(item); // 1 brug = 1 item fjernes fra inventory

        String effect = c.getEffectType();
        if (effect != null && !effect.isBlank()) {
            return "Used consumable: " + c.getName() + " (" + effect + ")";
        } else {
            return "Used consumable: " + c.getName();
        }
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
    public List<Item> filterByType(String type) {
        List<Item> results = new ArrayList<>();

        for (Item i : inventory.getItems()) {
            if (i.getType().equalsIgnoreCase(type)) {
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

    public String buyInventorySlots(int amount) {
        if (amount <= 0) {
            return "Amount must be greater than 0.";
        }
        boolean ok = inventory.buyInventorySlots(amount);

        if (ok) {
            System.out.println("You have unlocked " + amount + " inventory slots");
            System.out.println(inventory.getUnlockedSlots() + "/" + inventory.getMaxSlots());
            return "Slots successfully unlocked";
        } else {
            return "You can't buy more slots. Maximum slot capicity reached";
        }
    }



    // filtrerer efter rarity
    public List<Item> filterByRarity(String rarity) {
        List<Item> results = new ArrayList<>();

        for (Item i : inventory.getItems()) {
            if (i.getRarity().equalsIgnoreCase(rarity)) {
                results.add(i);
            }
        }
        return results;
    }
}
