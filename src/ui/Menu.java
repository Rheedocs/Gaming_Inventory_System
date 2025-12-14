package ui;

import domain.*;
import service.InventoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Konsol-baseret brugergrænseflade.
// Viser menu, læser input og kalder InventoryService.
public class Menu {

    private final Scanner input = new Scanner(System.in);

    private InventoryService service; // oprettes i start()
    private Player player;            // den aktive spiller

    // ---------- Initialisering ----------

    public void start() {
        initPlayerAndService();

        boolean running = true;

        while (running) {
            printMainMenu();
            int choice = readMenuChoice(1, 9);
            running = handleMainChoice(choice);
        }

        System.out.println("Menu has closed!");
    }

    private void initPlayerAndService() {
        // Opretter spiller og service-lag ud fra spillerens navn
        System.out.print("Enter player name: ");
        String playerName = input.nextLine();

        player = new Player(playerName);
        service = new InventoryService(player);
    }

    // ---------- Hovedmenu ----------

    private void printMainMenu() {
        System.out.println("============ MENU ============");
        System.out.println("1.  Inventory menu");
        System.out.println("2.  Equipment menu");
        System.out.println("3.  Search menu");
        System.out.println("4.  Use consumable");
        System.out.println("5.  Buy inventory slots");
        System.out.println("6.  Save inventory");
        System.out.println("7.  Load inventory from file");
        System.out.println("8.  Show inventory");
        System.out.println("9.  End");
        System.out.println("============ ---- ============");
    }

    private boolean handleMainChoice(int choice) {
        switch (choice) {
            case 1 -> inventoryMenu();
            case 2 -> equipmentMenu();
            case 3 -> searchMenu();
            case 4 -> useConsumable();
            case 5 -> buySlotsMenu();
            case 6 -> saveInvToFile();
            case 7 -> loadInvFromFile();
            case 8 -> { showInventory(); pause(); }
            case 9 -> { return false; }
            default -> {
                System.out.println("Invalid choice. Please enter a number between 1 and 9.");
                pause();
            }
        }
        return true;
    }

    // ---------- sub menus ----------

    private void inventoryMenu() {
        while (true) {
            System.out.println("\n=== INVENTORY MENU ===");
            System.out.println("1. Show inventory");
            System.out.println("2. Add item to inventory");
            System.out.println("3. Remove item from inventory");
            System.out.println("4. Sort items in inventory");
            System.out.println("5. Back");
            System.out.print("Choose an option (1-5): ");

            int choice = readMenuChoice(1, 5);

            switch (choice) {
                case 1 -> { showInventory(); pause(); }
                case 2 -> addItem();
                case 3 -> removeItem();
                case 4 -> sortItems();
                case 5 -> { return; }
            }
        }
    }

    private void equipmentMenu() {
        while (true) {
            System.out.println("\n=== EQUIPMENT MENU ===");
            System.out.println("1. Show equipment");
            System.out.println("2. Equip item from inventory");
            System.out.println("3. Unequip item");
            System.out.println("4. Back");
            System.out.print("Choose an option (1-4): ");

            int choice = readMenuChoice(1, 4);

            switch (choice) {
                case 1 -> showEquipment();
                case 2 -> equipItem();
                case 3 -> unequipItem();
                case 4 -> { return; }
            }
        }
    }

    private void searchMenu() {
        while (true) {
            System.out.println("\n=== SEARCH MENU ===");
            System.out.println("1. Search by exact name");
            System.out.println("2. Search by name contains");
            System.out.println("3. Filter by type");
            System.out.println("4. Filter by weight range");
            System.out.println("5. Filter by rarity");
            System.out.println("6. Back");
            System.out.print("Choose an option (1-6): ");

            int choice = readMenuChoice(1, 6);

            switch (choice) {
                case 1 -> searchItem();
                case 2 -> handleSearchByNameContains();
                case 3 -> handleFilterByType();
                case 4 -> handleFilterByWeightRange();
                case 5 -> handleFilterByRarity();
                case 6 -> { return; }
            }
        }
    }

    // ---------- Inventory-handlinger ----------

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
        if (service.getInventory().getTotalWeight() >= service.getInventory().getMaxWeight()) {
            System.out.println("Inventory is full (weight: " + service.getInventory().getTotalWeight()
                    + " / " + service.getInventory().getMaxWeight() + ").");
            pause();
            return;
        }

        System.out.print("Name: ");
        String name = input.nextLine().trim();

        while (name.isBlank()) {
            System.out.print("Name: ");
            name = input.nextLine().trim();
        }

        ItemType type = readEnum(ItemType.class, "Type (WEAPON/ARMOUR/CONSUMABLE): ");
        Rarity rarity = readEnum(Rarity.class, "Rarity (COMMON/UNCOMMON/RARE/EPIC): ");

        double weight;

        while (true) {
            System.out.print("Weight (must be > 0): ");
            weight = readDouble();

            if (weight <= 0) {
                System.out.println("Error: Weight must be greater then 0");
                continue;
            }

            double sum = weight + service.getInventory().getTotalWeight();
            if (sum > service.getInventory().getMaxWeight()) {
                System.out.println("Error: Cannot add item. Player is too heavy");
                continue;
            }

            break;
        }

        // subtype-felter (kun relevante felter fyldes ud afhængig af type)
        Integer damage = null;
        HandType handType = null;
        Integer defence = null;
        ArmourSlot armourSlot = null;
        String effectType = null;
        Integer stackSize = null;

        if (type == ItemType.WEAPON) {
            damage = readIntMin(0, "Damage: ");
            handType = readEnum(HandType.class, "Hand type (ONE_HAND/OFF_HAND/TWO_HAND): ");
        }

        if (type == ItemType.ARMOUR) {
            defence = readIntMin(0, "Defence: ");
            armourSlot = readEnum(ArmourSlot.class, "Slot (HEAD/CHEST/LEGS/FEET): ");
        }

        if (type == ItemType.CONSUMABLE) {
            System.out.print("Effect type: ");
            effectType = input.nextLine().trim();

            stackSize = readIntMin(1, "Stack size: ");
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

        while (true) {
            showInventory();
            System.out.print("Item you want to remove (or type 'exit' to go back): ");
            String name = input.nextLine().trim();

            if (name.equalsIgnoreCase("exit")) {
                return; // tilbage til hovedmenu
            }

            String result = service.removeItemByName(name);
            System.out.println(result);
            pause();

            if (result.toLowerCase().contains("not found")) {
                continue;
            }

            return;
        }
    }

    // Lader spilleren vælge et item fra inventory og equippe det (kun Weapon/Armour).
    private void equipItem() {
        if (service.isInventoryEmpty()) {
            System.out.println("Inventory is empty.");
            pause();
            return;
        }

        showInventory();

        while (true) {
            System.out.print("Item you want to equip (or type 'exit' to go back): ");
            String name = input.nextLine().trim();

            if (name.equalsIgnoreCase("exit")) {
                return; // go back to menu
            }
            if (name.isBlank()) {
                continue;
            }

            Item item = service.findItemByName(name);

            if (item == null) {
                System.out.println("Item not found.");
                continue;
            }

            // Kun Weapon og Armour må equips
            if (!(item instanceof Weapon || item instanceof Armour)) {
                System.out.println("This item cannot be equipped.");
                pause();
                continue;
            }

            String result = service.equip(item);
            System.out.println(result);
            pause();
            return;
        }
    }

    private void unequipItem() {
        if (service.isEquipmentEmpty()) {
            System.out.println("Nothing is equipped.");
            pause();
            return;
        }

        while (true) {
            System.out.print("Slot to unequip (MainHand/OffHand/Head/Chest/Legs/Feet, or 'exit' to go back): ");
            String slot = input.nextLine().trim();

            if (slot.equalsIgnoreCase("exit")) {
                System.out.println("Returning to menu");
                return;
            }

            if (!isValidUnequipSlot(slot)) {
                System.out.println("Invalid input. Try again.");
                continue;
            }

            String result = service.unequip(slot);
            System.out.println(result);
            pause();

            if (service.isEquipmentEmpty()) {
                System.out.println("Nothing else is equipped.");
                pause();
                return;
            }
        }
    }

    private void useConsumable() {
        if (service.isInventoryEmpty()) {
            System.out.println("Inventory is empty.");
            pause();
            return;
        }

        while (true) {
            showInventory();
            System.out.print("Consumable to use (or type 'exit' to go back): ");
            String name = input.nextLine().trim();

            if (name.equalsIgnoreCase("exit")) {
                return;
            }
            if (name.isBlank()) {
                continue;
            }

            String result = service.useConsumable(name);
            System.out.println(result);
            pause();

            if (result.toLowerCase().contains("not found")
                    || result.toLowerCase().contains("no consumable")) {
                continue;
            }

            return;
        }
    }

    private void searchItem() {
        System.out.print("Name to search for: ");
        String name = input.nextLine();

        Item item = service.findItemByName(name);
        List<Item> single = new ArrayList<>();
        if (item != null) {
            single.add(item);
        }

        printResults(single);
        pause();
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
        System.out.print("Choose an option (1-4): ");

        int choice = readMenuChoice(1, 4);

        switch (choice) {
            case 1 -> service.sortByName();
            case 2 -> service.sortByWeight();
            case 3 -> service.sortByType();
            case 4 -> service.sortByRarity();
        }

        showInventory();
        pause();
    }

    private void saveInvToFile() {
        System.out.print("Filename to save to (e.g. inventoryLog.txt): ");
        String name = input.nextLine();

        service.save(name);
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

    public void buySlotsMenu() {
        while (true) {
            System.out.println("=== BUY SLOTS ===");
            System.out.println("Unlocked: " + service.getInventory().getUnlockedSlots()
                    + "/" + service.getInventory().getMaxSlots());
            System.out.println("How many slots do you want to buy? (or 0 to go back)");

            int amount = readInt();

            if (amount == 0) {
                return; //  tilbage til menu
            }

            boolean success = service.buyInventorySlots(amount);

            if (success) {
                System.out.println("Slots successfully unlocked.");
                pause();
                return;
            }

            System.out.println("Could not buy slots. Amount must be > 0 and not exceed max slots.");
            pause();
        }
    }

    // ---------- Søg og filtrering hjælpemetoder ----------

    private void handleSearchByNameContains() {
        System.out.print("Text to search for: ");
        String text = input.nextLine();
        List<Item> results = service.searchByNameContains(text);
        printResults(results);
        pause();
    }

    private void handleFilterByType() {
        ItemType type = readEnum(ItemType.class, "Type (WEAPON/ARMOUR/CONSUMABLE): ");
        List<Item> results = service.filterByType(type);
        printResults(results);
        pause();
    }

    private void handleFilterByWeightRange() {
        System.out.print("Min weight: ");
        double min = readDouble();
        System.out.print("Max weight: ");
        double max = readDouble();
        List<Item> results = service.filterByWeight(min, max);
        printResults(results);
        pause();
    }

    private void handleFilterByRarity() {
        Rarity rarity = readEnum(Rarity.class, "Rarity (COMMON/UNCOMMON/RARE/EPIC): ");
        List<Item> results = service.filterByRarity(rarity);
        printResults(results);
        pause();
    }

    // ---------- ui hjælpemetoder ----------

    private int readMenuChoice(int min, int max) {
        int choice;

        // læs et gyldigt menuvalg mellem min og max
        while (true) {
            System.out.print("Choose between (" + min + "-" + max + "): ");
            choice = readInt();

            if (choice >= min && choice <= max) {
                return choice;
            }

            System.out.println("Invalid choice. Please choose a number between " + min + " and " + max + ".");
            pause();
        }
    }

    private int readInt() {
        while (true) {
            String line = input.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
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

    private int readIntMin(int min, String prompt) {
        while (true) {
            System.out.print(prompt);
            int value = readInt();
            if (value >= min) return value;
            System.out.println("Error: Value must be at least " + min);
        }
    }

    // Konverterer brugerinput til enum og håndterer ugyldigt input med try/catch
    private <T extends Enum<T>> T readEnum(Class<T> enumType, String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = input.nextLine().trim();

            try {
                String normalized = text
                        .toUpperCase()
                        .replace(' ', '_')
                        .replace('-', '_');

                return Enum.valueOf(enumType, normalized);

            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input. Try again.");
            }
        }
    }

    private boolean isValidUnequipSlot(String slot) {
        return slot.equalsIgnoreCase("MainHand")
                || slot.equalsIgnoreCase("OffHand")
                || slot.equalsIgnoreCase("Head")
                || slot.equalsIgnoreCase("Chest")
                || slot.equalsIgnoreCase("Legs")
                || slot.equalsIgnoreCase("Feet");
    }

    // Printer en tabel med items i samme layout som inventory overview.
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
}
