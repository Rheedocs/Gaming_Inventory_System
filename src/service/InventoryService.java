package service;

import domain.Inventory;
import domain.Item;

public class InventoryService {

    private final Inventory inventory;

    public InventoryService() {
        this.inventory = new Inventory();
    }

    public InventoryService(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String addItem(String name, String type, String rarity, double weight) {
        Item item = new Item(name, type, rarity, weight);
        boolean added = inventory.addItem(item);

        if (added) {
            return "Item has been added to the inventory!";
        } else  {
            return "Item could not be added. Inventory is full or weight limit exceeded.";
        }
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
        if (item != null && inventory.removeItem(item)) {
            return "Used consumable: " + item.getName();
        } else  {
            return "Could not use consumable. Item not found.";
        }
    }

    public void sortByName() {
        inventory.sortByName();
    }

    public void sortByWeight() {
        inventory.sortByWeight();
    }

    public void save(String path) {
        inventory.saveToFile(path);
    }

    public  void load(String path) {
        inventory.loadFromFile(path);
    }
}
