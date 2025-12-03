package MenuSystem;

import Inventory.Inventory;
import Inventory.Item;

import java.util.ArrayList;
import java.util.Scanner;

public class Menu {

    private Scanner input = new Scanner(System.in);
    private Inventory inventory = new Inventory();

    public void start(){
        boolean running = true;

        while (running){
            System.out.println("============ MENU ============");
            System.out.println("1. Show Inventory");
            System.out.println("2. Add item to inventory");
            System.out.println("3. remove item from inventory");
            System.out.println("4. Equip Item from inventory");
            System.out.println("5. Unequip item from inventory");
            System.out.println("6. Use consumable");
            System.out.println("7. Search for an item in inventory");
            System.out.println("8. Sort items in inventory");
            System.out.println("9. Save inventory");
            System.out.println("10. Load inventory from file");
            System.out.println("11. End");
            System.out.println("============ ---- ============");
            System.out.print("Choose between (1-11)");

            int choice = readInt();

            switch(choice){
                case 1 -> showInventory();
                case 2 -> addItem();
                case 3 -> removeItem();
                case 4 -> equipItem();
                case 5 -> unequipItem();
                case 6 -> useConsumable();
                case 7 -> searchItem();
                case 8 -> sortItems();
                case 9 -> saveInvFromFile();
                case 10 -> LoadInvFromFile();
                case 11 -> running = false;
                default -> System.out.println("Choice does not fit between 1-11!");
            }
        }
        System.out.println("Menu has closed!");
    }

    private void showInventory(){
        System.out.println("" + inventory);
    }

    private void addItem() {
        System.out.println("Name: ");
        String name = input.nextLine();

        System.out.println("Type: ");
        String type = input.nextLine();

        System.out.println("Rarity: ");
        String rarity = input.nextLine();

        System.out.println("Weight: ");
        double weight = readDouble();

        Item item = new Item(name, type, rarity, weight);
            if (inventory.addItem(item)) {
            System.out.println("Item has been added to the inventory!");
        } else {
        System.out.println("Item could not be added, Inventory is too full or item exceeds maximum weight for player!");
        }
    }

    private void removeItem () {
        showInventory();

        System.out.println("Write the item that needs to be removed. ");
        String name = input.nextLine();

        Item item = inventory.findItemByName(name);
        if (item != null && inventory.removeItem(item)) {
            System.out.println("Item has been removed from Inventory!");
        } else {
            System.out.println("Choice is Incorrect!");
        }
    }

    private void equipItem () {
        showInventory();
        System.out.println("Item you want to equip: ");
        String name = input.nextLine();

        Item item = inventory.findItemByName(name);
        if (item != null) {
            equipItem = item; // Store equipped item
            System.out.println("Equipped: " + item.getName());
        } else {
            System.out.println("Item does not exist!");
        }
    }

    private void unequipItem () {
        if (equipItem != null) {
            System.out.println("Unequipped: " + equipItem.getName());
            equipItem = null;
        } else {
            System.out.println("There is no item(s) equipped!");
        }
    }

        private void useConsumable(){
            showInventory();
            System.out.println("Name of the Consumable: ");
            String name = input.nextLine();

            Item item = inventory.findItemByName(name);
            if (item != null && inventory.removeItem(item)) {
                System.out.println("Uses the Consumable " + item.getName());
            } else {
                System.out.println("Item not found!");
            }
            }

        private void searchItem(){
            System.out.println("Input name of Item: ");
            String name = input.nextLine();

            Item item = inventory.findItemByName(name);
            if (item != null) {
                System.out.println("Fundet: " + item);
            } else {
                System.out.println("Item not found!");
            }
        }
        private void sortItems() {
            System.out.println("Sort after:");
            System.out.println("1. Name");
            System.out.println("2. Weight");
            System.out.println("Choose:");

            int choice = readInt();
            switch (choice) {
                case 1 -> inventory.sortByName();
                case 2 -> inventory.sortByWeight();
                default -> {
                    System.out.println("Choice is incorrect!");
                    return;
                }
            }
            System.out.println("Inventory has been sorted!");
        }
        private void saveInvFromFile() {
            System.out.println("Name of the file: ");
            String name = input.nextLine();

            inventory.saveToFile(name);
            System.out.println("Inventory has been saved!");
        }

        private void LoadInvFromFile() {
            System.out.println("Name of the file: ");
            String name = input.nextLine();

            inventory.loadFromFile(name);
            System.out.println("Inventory has been loaded!");
        }




        }
    }
}