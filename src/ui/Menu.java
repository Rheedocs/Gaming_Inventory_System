package ui;

import domain.Item;
import domain.Player;
import service.InventoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {

    private final Scanner input = new Scanner(System.in);

    private InventoryService service; // oprettes i start()
    private Player player;            // den aktive spiller

    public void start() {

        System.out.print("Enter player name: ");
        String playerName = input.nextLine();

        player = new Player(playerName);
        service = new InventoryService(player);

        boolean running = true;

        while (running) {
            System.out.println("============ MENU ============");
            System.out.println("1.  Show inventory");
            System.out.println("2.  Show equipment");
            System.out.println("3.  Add item to inventory");
            System.out.println("4.  Remove item from inventory");
            System.out.println("5.  Equip item from inventory");
            System.out.println("6.  Unequip item");
            System.out.println("7.  Use consumable");
            System.out.println("8.  Search for an item");
            System.out.println("9.  Advanced search & filtering");
            System.out.println("10. Sort items in inventory");
            System.out.println("11. Save inventory");
            System.out.println("12. Load inventory from file");
            System.out.println("13. End");
            System.out.println("============ ---- ============");
            System.out.print("Choose between (1-13): ");

            int choice = readInt();

            switch (choice) {
                case 1 -> { showInventory(); pause(); }
                case 2 -> showEquipment();
                case 3 -> addItem();
                case 4 -> removeItem();
                case 5 -> equipItem();
                case 6 -> unequipItem();
                case 7 -> useConsumable();
                case 8 -> searchItem();
                case 9 -> advancedSearchMenu();
                case 10 -> sortItems();
                case 11 -> saveInvToFile();
                case 12 -> loadInvFromFile();
                case 13 -> running = false;
                default -> System.out.println("Choice does not fit between 1-13!");
            }
        }

        System.out.println("Menu has closed!");
    }

    // ---------- main actions ----------

    private void showInventory() {
        System.out.println();
        System.out.println("Player: " + player.getName());
        System.out.print(service.getInventoryOverview());
    }

    private void showEquipment() {
        System.out.println("\n=== EQUIPMENT ===");
        System.out.println(service.getPlayer().getEquipment().getOverview());
        pause();
    }

    private void addItem() {
        System.out.print("Name: ");
        String name = input.nextLine();

        String type;
        while (true) {
            System.out.print("Type (Weapon/Armour/Consumable): ");
            type = input.nextLine().trim();
            if (type.equalsIgnoreCase("Weapon") ||
                    type.equalsIgnoreCase("Armour") ||
                    type.equalsIgnoreCase("Consumable")) {
                break; // kun gyldige typer
            }
            System.out.println("Invalid type. Try again.");
        }

        String rarity;
        while (true) {
            System.out.print("Rarity (Common/Uncommon/Rare/Epic): ");
            rarity = input.nextLine().trim();

            if (rarity.equalsIgnoreCase("Common") ||
                    rarity.equalsIgnoreCase("Uncommon") ||
                    rarity.equalsIgnoreCase("Rare") ||
                    rarity.equalsIgnoreCase("Epic")) {

                rarity = capitalize(rarity);   // <- gør “common” til “Common”
                break;
            }

            System.out.println("Invalid rarity. Try again.");
        }


        double weight;
        while (true) {
            System.out.print("Weight (> 0): ");
            weight = readDouble();
            if (weight > 0) break;
            System.out.println("Weight must be greater than 0.");
        }

        // subtype-felter
        Integer damage = null;
        String handType = null;
        Integer defence = null;
        String armourSlot = null;
        String effectType = null;
        Integer stackSize = null;

        if (type.equalsIgnoreCase("Weapon")) {
            System.out.print("Damage: ");
            damage = readInt();

            while (true) {
                System.out.print("Hand type (OneHand/OffHand/TwoHand): ");
                handType = input.nextLine().trim();
                if (handType.equalsIgnoreCase("OneHand") ||
                        handType.equalsIgnoreCase("OffHand") ||
                        handType.equalsIgnoreCase("TwoHand")) {
                    break; // kendt håndtype
                }
                System.out.println("Invalid hand type. Try again.");
            }
        }

        if (type.equalsIgnoreCase("Armour")) {
            System.out.print("Defence: ");
            defence = readInt();

            System.out.print("Slot (Head/Chest/Legs/Feet): ");
            armourSlot = input.nextLine().trim();
        }

        if (type.equalsIgnoreCase("Consumable")) {
            System.out.print("Effect type: ");
            effectType = input.nextLine().trim();

            System.out.print("Stack size: ");
            stackSize = readInt();
        }

        String result = service.addItem(
                name, type, rarity, weight,
                damage, handType, defence, armourSlot,
                effectType, stackSize
        );

        System.out.println(result);
        pause();
    }

    private void removeItem() {
        if (service.isInventoryEmpty()) {
            System.out.println("Inventory is empty.");
            pause();
            return;
        }

        showInventory();
        System.out.print("Item you want to remove: ");
        String name = input.nextLine();

        String result = service.removeItemByName(name);
        System.out.println(result);
        pause();
    }

    private void equipItem() {
        if (service.isInventoryEmpty()) {
            System.out.println("Inventory is empty.");
            pause();
            return;
        }

        showInventory();
        System.out.print("Item you want to equip: ");
        String name = input.nextLine();

        Item item = service.findItemByName(name);
        if (item != null) {
            String result = service.equip(item);
            System.out.println(result);
        } else {
            System.out.println("Item does not exist!");
        }
        pause();
    }

    private void unequipItem() {
        System.out.print("Slot to unequip (MainHand/OffHand/Head/Chest/Legs/Feet): ");
        String slot = input.nextLine().trim();

        String result = service.unequip(slot);
        System.out.println(result);
        pause();
    }

    private void useConsumable() {
        if (service.isInventoryEmpty()) {
            System.out.println("Inventory is empty.");
            pause();
            return;
        }

        showInventory();
        System.out.print("Consumable to use: ");
        String name = input.nextLine();

        String result = service.useConsumable(name);
        System.out.println(result);
        pause();
    }

    private void searchItem() {
        System.out.print("Name to search for: ");
        String name = input.nextLine();

        Item item = service.findItemByName(name);
        List<Item> single = new ArrayList<>();
        if (item != null) {
            single.add(item);
        }
        printResults(single); // bruger samme tabel-layout som overview
        pause();
    }

    private void advancedSearchMenu() {

        while (true) { // forbliver i advanced search til bruger vælger back
            System.out.println("\n=== ADVANCED SEARCH ===");
            System.out.println("1. Search by name contains");
            System.out.println("2. Filter by type");
            System.out.println("3. Filter by weight range");
            System.out.println("4. Filter by rarity");
            System.out.println("5. Back");
            System.out.print("Choose: ");

            int choice = readInt();

            switch (choice) {

                case 1 -> {
                    System.out.print("Text to search for: ");
                    String text = input.nextLine();
                    List<Item> results = service.searchByNameContains(text);
                    printResults(results);
                    pause();
                }

                case 2 -> {
                    System.out.print("Type (Weapon/Armour/Consumable): ");
                    String type = input.nextLine();
                    List<Item> results = service.filterByType(type);
                    printResults(results);
                    pause();
                }

                case 3 -> {
                    System.out.print("Min weight: ");
                    double min = readDouble();
                    System.out.print("Max weight: ");
                    double max = readDouble();
                    List<Item> results = service.filterByWeight(min, max);
                    printResults(results);
                    pause();
                }

                case 4 -> {
                    System.out.print("Rarity (Common/Uncommon/Rare/Epic): ");
                    String rarity = input.nextLine();
                    List<Item> results = service.filterByRarity(rarity);
                    printResults(results);
                    pause();
                }

                case 5 -> {
                    return; // tilbage til hovedmenu
                }

                default -> {
                    System.out.println("Invalid choice.");
                    pause();
                }
            }
        }
    }

    private void sortItems() {
        if (service.isInventoryEmpty()) {
            System.out.println("Inventory is empty.");
            pause();
            return;
        }

        System.out.println("\nSort by:");
        System.out.println("1. Name");
        System.out.println("2. Weight");
        System.out.println("3. Type");
        System.out.println("4. Rarity");
        System.out.print("Choose: ");

        int choice = readInt();

        switch (choice) {
            case 1 -> service.sortByName();
            case 2 -> service.sortByWeight();
            case 3 -> service.sortByType();
            case 4 -> service.sortByRarity();
            default -> System.out.println("Invalid choice.");
        }

        showInventory(); // vis resultat efter sortering
        pause();
    }

    private void saveInvToFile() {
        System.out.print("Filename to save to (e.g. inventoryLog.txt): ");
        String name = input.nextLine();

        service.save(name);
        // fejl håndteres i FileHandler
        pause();
    }

    private void loadInvFromFile() {
        System.out.print("Filename to load from: ");
        String name = input.nextLine();

        boolean ok = service.load(name);

        if (ok) {
            System.out.println("Inventory has been loaded!");
        } else {
            System.out.println("Could not load inventory (file missing or invalid).");
        }
        pause();
    }

    // ---------- helpers ----------

    private int readInt() {
        while (true) {
            String line = input.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid integer: ");
            }
        }
    }

    private double readDouble() {
        while (true) {
            String line = input.nextLine();
            try {
                return Double.parseDouble(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private void pause() {
        System.out.println("Press Enter to continue...");
        input.nextLine();
    }

    private void printResults(List<Item> items) {
        if (items.isEmpty()) {
            System.out.println("No items found.");
            return;
        }

        System.out.println("\n====== SEARCH RESULTS ======");
        System.out.println("-------------------------------------------------------------");
        System.out.printf("%-3s %-18s %-12s %-12s %-8s%n",
                "No", "Name", "Type", "Rarity", "Weight");
        System.out.println("-------------------------------------------------------------");

        int index = 1;
        for (Item item : items) {
            System.out.printf(
                    "%-3d %-18s %-12s %-12s %-8.1f%n",
                    index++,
                    item.getName(),
                    item.getType(),
                    item.getRarity(),
                    item.getWeight()
            );
        }
        System.out.println("-------------------------------------------------------------");
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
