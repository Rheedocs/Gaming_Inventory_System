package ui;

import domain.Item;
import domain.Player;
import domain.Armour;
import domain.Weapon;
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

    public void start() {

        // Opretter spiller og service-lag ud fra spillerens navn
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
            System.out.println("13. Buy inventory slots");
            System.out.println("14. End");
            System.out.println("============ ---- ============");

            int choice;

            // læs et gyldigt menuvalg mellem 1 og 14
            while (true) {
                System.out.print("Choose between (1-14): ");
                choice = readInt();

                if (choice >= 1 && choice <= 14) {
                    break; // valid → videre til switch
                }

                System.out.println("Invalid choice. Please choose a number between 1 and 14.");
                pause(); // så brugeren ser beskeden
            }

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
                case 13 -> buySlotsMenu();
                case 14 -> running = false;
                default -> {
                    System.out.println("Invalid choice. Please enter a number between 1 and 14.");
                    pause();
                }
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

        // vælg type (kun gyldige domænetyper)
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

        // vælg rarity og normaliser til stort begyndelsesbogstav
        String rarity;
        while (true) {
            System.out.print("Rarity (Common/Uncommon/Rare/Epic): ");
            rarity = input.nextLine().trim();

            if (rarity.equalsIgnoreCase("Common") ||
                    rarity.equalsIgnoreCase("Uncommon") ||
                    rarity.equalsIgnoreCase("Rare") ||
                    rarity.equalsIgnoreCase("Epic")) {

                rarity = capitalize(rarity);   // gør “common” til “Common”
                break;
            }

            System.out.println("Invalid rarity. Try again.");
        }

        // vægt skal være > 0
        double weight;
        while (true) {
            System.out.print("Weight (must be > 0): ");
            weight = readDouble();
            if (weight > 0) break;
            System.out.println("Weight must be greater than 0.");
        }

        // subtype-felter (kun relevante felter fyldes ud afhængig af type)
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

            // hvis item IKKE blev fundet, så lad loopen fortsætte
            if (result.toLowerCase().contains("not found")) {
                continue;
            }

            // ellers er vi færdige med at fjerne, tilbage til menu
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

            if (!slot.equalsIgnoreCase("MainHand")
                    && !slot.equalsIgnoreCase("OffHand")
                    && !slot.equalsIgnoreCase("Head")
                    && !slot.equalsIgnoreCase("Chest")
                    && !slot.equalsIgnoreCase("Legs")
                    && !slot.equalsIgnoreCase("Feet")) {

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

            String result = service.useConsumable(name);
            System.out.println(result);
            pause();

            // hvis beskeden fx er "not found" → prøv igen
            if (result.toLowerCase().contains("not found")
                    || result.toLowerCase().contains("no consumable")) {
                continue;
            }

            // ellers har vi brugt noget succesfuldt → tilbage til menu
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
        printResults(single); // bruger samme tabel-layout som overview
        pause();
    }

    // Under-menu til avanceret søgning og filtrering.
    // Bliver i menuen indtil brugeren vælger "Back".
    private void advancedSearchMenu() {

        while (true) {
            System.out.println("\n=== ADVANCED SEARCH ===");
            System.out.println("1. Search by name contains");
            System.out.println("2. Filter by type");
            System.out.println("3. Filter by weight range");
            System.out.println("4. Filter by rarity");
            System.out.println("5. Back");
            System.out.print("Choose an option (1-5): ");

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
                    System.out.println("Invalid choice. Please enter a number between 1 and 5.");
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
        System.out.print("Choose an option (1-4): ");

        int choice = readInt();

        switch (choice) {
            case 1 -> service.sortByName();
            case 2 -> service.sortByWeight();
            case 3 -> service.sortByType();
            case 4 -> service.sortByRarity();
            default -> {
                System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                pause();
            }
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

    public void buySlotsMenu() {
        while (true) {
            System.out.println("=== BUY SLOTS ===");
            System.out.println("Unlocked: " + service.getInventory().getUnlockedSlots()
                    + "/" + service.getInventory().getMaxSlots());
            System.out.println("How many slots do you want to buy? (or 0 to go back)");

            int amount = readInt();

            if (amount == 0) {
                return; // tilbage til menu
            }

            boolean success = service.buyInventorySlots(amount);

            if (success) {
                System.out.println("Slots successfully unlocked.");
                pause();
                return; // færdig, tilbage til menu
            } else {
                System.out.println("Could not buy slots. Amount must be > 0 and not exceed max slots.");
                pause();
                // og så kører while videre, så de kan prøve igen
            }
        }
    }

    // ---------- helpers ----------

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

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
