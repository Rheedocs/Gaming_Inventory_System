package ui;

import domain.Item;
import service.InventoryService;

import java.util.Scanner;

public class Menu {

    private final Scanner input = new Scanner(System.in);
    private final InventoryService service = new InventoryService();
    private Item equippedItem;   // WIP “udstyrt” item

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("============ MENU ============");
            System.out.println("1. Show Inventory");
            System.out.println("2. Add item to inventory");
            System.out.println("3. Remove item from inventory");
            System.out.println("4. Equip item from inventory");
            System.out.println("5. Unequip item from inventory");
            System.out.println("6. Use consumable");
            System.out.println("7. Search for an item in inventory");
            System.out.println("8. Sort items in inventory");
            System.out.println("9. Save inventory");
            System.out.println("10. Load inventory from file");
            System.out.println("11. End");
            System.out.println("============ ---- ============");
            System.out.print("Choose between (1-11): ");

            int choice = readInt();

            switch (choice) {
                case 1 -> {              // vis inventory og tilbage til menu
                    showInventory();
                    pause();
                }
                case 2 -> addItem();
                case 3 -> removeItem();
                case 4 -> equipItem();
                case 5 -> unequipItem();
                case 6 -> useConsumable();
                case 7 -> searchItem();
                case 8 -> sortItems();
                case 9 -> saveInvToFile();
                case 10 -> loadInvFromFile();
                case 11 -> running = false;
                default -> System.out.println("Choice does not fit between 1-11!");
            }
        }
        System.out.println("Menu has closed!");
    }

    // ---------- helpers ----------

    private int readInt() {
        while (!input.hasNextInt()) {
            System.out.print("Not a number, try again: ");
            input.nextLine();
        }
        int value = input.nextInt();
        input.nextLine(); // spis newline
        return value;
    }

    private double readDouble() {
        while (!input.hasNextDouble()) {
            System.out.print("Not a number, try again: ");
            input.nextLine();
        }
        double value = input.nextDouble();
        input.nextLine();
        return value;
    }

    // ---------- menu actions ----------

    private void pause() {
        System.out.println("\nPress ENTER to return to the menu...");
        input.nextLine();
    }

    private void showInventory() {
        System.out.println();
        System.out.print(service.getInventory().getDetailedOverview());
    }


    private void addItem() {
        System.out.print("Name: ");
        String name = input.nextLine();

        // Type
        String type;
        while (true) {
            System.out.print("Type (Weapon/Armour/Consumable): ");
            type = input.nextLine().trim();
            if (type.equalsIgnoreCase("Weapon") ||
                    type.equalsIgnoreCase("Armour") ||
                    type.equalsIgnoreCase("Consumable")) {
                break;
            }
            System.out.println("Invalid type. Try again.");
        }

        // Rarity
        String rarity;
        while (true) {
            System.out.print("Rarity (Common/Uncommon/Rare/Epic): ");
            rarity = input.nextLine().trim();
            if (rarity.equalsIgnoreCase("Common") ||
                    rarity.equalsIgnoreCase("Uncommon") ||
                    rarity.equalsIgnoreCase("Rare") ||
                    rarity.equalsIgnoreCase("Epic")) {
                break;
            }
            System.out.println("Invalid rarity. Try again.");
        }

        // Weight
        double weight;
        while (true) {
            System.out.print("Weight (> 0): ");
            weight = readDouble();
            if (weight > 0) break;
            System.out.println("Weight must be greater than 0.");
        }

        String result = service.addItem(name, type, rarity, weight);
        System.out.println(result);
        pause();
    }


    private void removeItem() {
        showInventory();  // viser bare inventory

        System.out.print("Write the name of the item to remove: ");
        String name = input.nextLine();

        String result = service.removeItemByName(name);
        System.out.println(result);

        pause(); // nu giver teksten mening: tilbage til menuen
    }


    private void equipItem() {
        showInventory();
        System.out.print("Item you want to equip: ");
        String name = input.nextLine();

        Item item = service.findItemByName(name);
        if (item != null) {
            equippedItem = item;
            System.out.println("Equipped: " + item.getName());
        } else {
            System.out.println("Item does not exist!");
        }
        pause();
    }

    private void unequipItem() {
        if (equippedItem != null) {
            System.out.println("Unequipped: " + equippedItem.getName());
            equippedItem = null;
        } else {
            System.out.println("There is no item equipped!");
        }
        pause();
    }

    private void useConsumable() {
        showInventory();
        System.out.print("Name of the consumable: ");
        String name = input.nextLine();

        String result = service.useConsumable(name);
        System.out.println(result);

        pause();
    }

    private void searchItem() {
        System.out.print("Input name of item: ");
        String name = input.nextLine();

        Item item = service.findItemByName(name);
        if (item != null) {
            System.out.println("Found: " + item);
        } else {
            System.out.println("Item not found!");
        }
        pause();
    }

    private void sortItems() {
        System.out.println("Sort after:");
        System.out.println("1. Name");
        System.out.println("2. Weight");
        System.out.print("Choose: ");

        int choice = readInt();
        switch (choice) {
            case 1 -> service.sortByName();
            case 2 -> service.sortByWeight();
            default -> {
                System.out.println("Choice is incorrect!");
                return;
            }
        }
        System.out.println("Inventory has been sorted!");
        pause();
    }

    private void saveInvToFile() {
        System.out.print("Name of the file: ");
        String name = input.nextLine();

        service.save(name);
        System.out.println("Inventory has been saved!");
        pause();
    }

    private void loadInvFromFile() {
        System.out.print("Name of the file: ");
        String name = input.nextLine();

        boolean ok = service.load(name);

        if (ok) {
            System.out.println("Inventory has been loaded!");
        } else {
            System.out.println("Could not load inventory (file missing or invalid).");
        }

        pause();

    }
}
