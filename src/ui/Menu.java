package ui;

import domain.*;
import domain.enums.ArmourSlot;
import domain.enums.HandType;
import domain.enums.ItemType;
import domain.enums.Rarity;
import service.InventoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Konsolbaseret UI for inventory-systemet.
// Viser menuer, læser input og kalder service-laget.
public class Menu {


    private final Scanner input = new Scanner(System.in);

    private final Player player = new Player("Player1");
    private final InventoryService service = new InventoryService(player);

    public void start() {
        while (true) {
            ConsoleUI.header("MAIN MENU");
            ConsoleUI.option(1, "Inventory");
            ConsoleUI.option(2, "Equipment");
            ConsoleUI.option(3, "Use consumable");
            ConsoleUI.option(4, "Search / filter");
            ConsoleUI.option(5, "Buy slots");
            ConsoleUI.option(6, "Save inventory");
            ConsoleUI.option(7, "Load inventory");
            ConsoleUI.option(8, "Exit");
            ConsoleUI.footer();

            int choice = readMenuChoice(1, 8);

            switch (choice) {
                case 1 -> inventoryMenu();
                case 2 -> equipmentMenu();
                case 3 -> useConsumable();
                case 4 -> searchMenu();
                case 5 -> buySlotsMenu();
                case 6 -> saveInvToFile();
                case 7 -> loadInvFromFile();
                case 8 -> {
                    ConsoleUI.message("Exiting...");
                    return;
                }
            }
        }
    }

    // ---------- Sub menus ----------

    private void inventoryMenu() {
        while (true) {
            ConsoleUI.header("INVENTORY MENU");
            ConsoleUI.option(1, "Show inventory");
            ConsoleUI.option(2, "Add item");
            ConsoleUI.option(3, "Remove item");
            ConsoleUI.option(4, "Sort items");
            ConsoleUI.option(5, "Back");
            ConsoleUI.footer();

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
            ConsoleUI.header("EQUIPMENT MENU");
            ConsoleUI.option(1, "Show equipment");
            ConsoleUI.option(2, "Equip item");
            ConsoleUI.option(3, "Unequip item");
            ConsoleUI.option(4, "Back");
            ConsoleUI.footer();

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
            ConsoleUI.header("SEARCH MENU");
            ConsoleUI.option(1, "Search by exact name");
            ConsoleUI.option(2, "Search by name contains");
            ConsoleUI.option(3, "Filter by type");
            ConsoleUI.option(4, "Filter by weight range");
            ConsoleUI.option(5, "Filter by rarity");
            ConsoleUI.option(6, "Back");
            ConsoleUI.footer();

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
        System.out.print(InventoryPrinter.format(service.getInventory()));
    }

    private void showEquipment() {
        System.out.println();
        System.out.println("Player: " + player.getName());
        System.out.print(EquipmentPrinter.format(service.getPlayer().getEquipment()));
        pause();
    }

    private void addItem() {
        // UI læser input og sender det videre til service.
        // Domain/service håndterer validering (vægt, stackSize, osv.) og returnerer beskeder.

        System.out.print("Name: ");
        String name = input.nextLine().trim();

        while (name.isBlank()) {
            System.out.print("Name: ");
            name = input.nextLine().trim();
        }

        ItemType type = readEnum(ItemType.class, "Type (WEAPON/ARMOUR/CONSUMABLE): ");
        Rarity rarity = readEnum(Rarity.class, "Rarity (COMMON/UNCOMMON/RARE/EPIC): ");

        System.out.print("Weight: ");
        double weight = readDouble();

        Integer damage = null;
        HandType handType = null;
        Integer defence = null;
        ArmourSlot armourSlot = null;
        String effectType = null;
        Integer stackSize = null;

        if (type == ItemType.WEAPON) {
            System.out.print("Damage: ");
            damage = readInt();
            handType = readEnum(HandType.class, "HandType (ONE_HAND/OFF_HAND/TWO_HAND): ");
        }

        if (type == ItemType.ARMOUR) {
            System.out.print("Defence: ");
            defence = readInt();
            armourSlot = readEnum(ArmourSlot.class, "ArmourSlot (HEAD/CHEST/LEGS/FEET): ");
        }

        if (type == ItemType.CONSUMABLE) {
            System.out.print("EffectType (text): ");
            effectType = input.nextLine().trim();
            System.out.print("StackSize: ");
            stackSize = readInt();
        }

        String result = service.addItem(
                name, type, rarity, weight,
                damage, handType,
                defence, armourSlot,
                effectType, stackSize
        );

        System.out.println(result);
        pause();
    }

    private void removeItem() {
        if (service.isInventoryEmpty()) {
            ConsoleUI.message("Inventory is empty.");
            pause();
            return;
        }

        showInventory();
        System.out.print("Item to remove (or type 'exit' to go back): ");
        String name = input.nextLine().trim();

        if (name.equalsIgnoreCase("exit")) {
            return;
        }
        if (name.isBlank()) {
            return;
        }

        String result = service.removeItemByName(name);
        System.out.println(result);
        pause();
    }

    private void equipItem() {

        if (service.isInventoryEmpty()) {
            ConsoleUI.message("Inventory is empty.");
            pause();
            return;
        }

        // UI-valg: Vi viser kun items der kan equips (Weapon/Armour),
        // så brugeren ikke kan vælge ugyldige items som fx Consumables.
        List<Item> equipables = new ArrayList<>();
        for (Item i : service.getItems()) {
            if (i instanceof Weapon || i instanceof Armour) {
                equipables.add(i);
            }
        }

        if (equipables.isEmpty()) {
            ConsoleUI.message("No equippable items in inventory.");
            pause();
            return;
        }

        // Vis kun listen over equippable items (mere brugervenligt)
        printResults("EQUIPPABLE ITEMS", equipables);

        while (true) {
            System.out.print("Item you want to equip (or type 'exit' to go back): ");
            String name = input.nextLine().trim();

            if (name.equalsIgnoreCase("exit")) {
                return;
            }
            if (name.isBlank()) {
                continue;
            }

            Item item = service.findItemByName(name);

            if (item == null) {
                ConsoleUI.message("Item not found.");
                continue;
            }

            // Sikkerhedstjek (hvis brugeren skriver et navn på et ikke-equippable item)
            if (!(item instanceof Weapon || item instanceof Armour)) {
                ConsoleUI.message("Item cannot be equipped.");
                continue;
            }

            String result = service.equip(item);
            ConsoleUI.message(result);
            pause();
            return;
        }
    }

    private void unequipItem() {
        if (service.isEquipmentEmpty()) {
            ConsoleUI.message("Nothing is equipped.");
            pause();
            return;
        }

        while (true) {
            System.out.print("Slot to unequip (MainHand/OffHand/Head/Chest/Legs/Feet, or 'exit' to go back): ");
            String slot = input.nextLine().trim();

            if (slot.equalsIgnoreCase("exit")) {
                ConsoleUI.message("Returning to menu");
                return;
            }

            if (!isValidUnequipSlot(slot)) {
                ConsoleUI.message("Invalid input. Try again.");
                continue;
            }

            String result = service.unequip(slot);
            ConsoleUI.message(result);
            pause();

            if (service.isEquipmentEmpty()) {
                ConsoleUI.message("Nothing else is equipped.");
                pause();
                return;
            }
        }
    }

    private void useConsumable() {
        if (service.isInventoryEmpty()) {
            ConsoleUI.message("Inventory is empty.");
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

            // Succes-output opdelt i boks + linje
            if (result.toLowerCase().contains("used consumable")) {
                String[] parts = result.split("\\|");

                ConsoleUI.message(parts[0].trim());

                if (parts.length > 1) {
                    System.out.println(parts[1].trim());
                }
                pause();
                return;
            }

            // Fejlbesked
            ConsoleUI.message(result);
            pause();
        }
    }

    private void searchItem() {
        System.out.print("Name to search for: ");
        String name = input.nextLine().trim();

        Item item = service.findItemByName(name);
        List<Item> single = new ArrayList<>();
        if (item != null) {
            single.add(item);
        }

        printResults("RESULTS", single);

        if (item == null) {
            ConsoleUI.message("Tip: Exact search needs full name.");
        }
        pause();
    }

    private void sortItems() {
        if (service.isInventoryEmpty()) {
            ConsoleUI.message("Inventory is empty.");
            pause();
            return;
        }

        ConsoleUI.header("SORT MENU");
        ConsoleUI.option(1, "Name");
        ConsoleUI.option(2, "Weight");
        ConsoleUI.option(3, "Type");
        ConsoleUI.option(4, "Rarity");
        ConsoleUI.footer();

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
        String name = input.nextLine().trim();

        boolean ok = service.save(name);

        if (ok) {
            ConsoleUI.message("Inventory has been saved!");
        } else {
            ConsoleUI.message("Could not save inventory (invalid path or write error).");
        }
        pause();
    }

    private void loadInvFromFile() {
        System.out.print("Filename to load from: ");
        String name = input.nextLine().trim();

        boolean ok = service.load(name);

        if (ok) {
            ConsoleUI.message("Inventory has been loaded!");
        } else {
            ConsoleUI.message("Could not load inventory (file missing or invalid).");
        }
        pause();
    }

    public void buySlotsMenu() {
        while (true) {
            ConsoleUI.header("BUY SLOTS");
            System.out.println("Unlocked: " + service.getInventory().getUnlockedSlots()
                    + "/" + service.getInventory().getMaxSlots());
            System.out.println("How many slots do you want to buy? (or 0 to go back)");
            ConsoleUI.footer();

            int amount = readInt();

            if (amount == 0) {
                return; // tilbage til menu
            }

            boolean success = service.buyInventorySlots(amount);

            if (success) {
                ConsoleUI.message("Slots successfully unlocked.");
                pause();
                return;
            }

            ConsoleUI.message("Could not buy slots. Must be > 0 and within max slots.");
            pause();
        }
    }

    // ---------- Søg og filtrering hjælpemetoder ----------

    private void handleSearchByNameContains() {
        System.out.print("Text to search for: ");
        String text = input.nextLine().trim();
        List<Item> results = service.searchByNameContains(text);
        printResults("RESULTS", results);
        pause();
    }

    private void handleFilterByType() {
        ItemType type = readEnum(ItemType.class, "Type (WEAPON/ARMOUR/CONSUMABLE): ");
        List<Item> results = service.filterByType(type);
        printResults("RESULTS", results);
        pause();
    }

    private void handleFilterByWeightRange() {
        System.out.print("Min weight: ");
        double min = readDouble();
        System.out.print("Max weight: ");
        double max = readDouble();
        List<Item> results = service.filterByWeight(min, max);
        printResults("RESULTS", results);
        pause();
    }

    private void handleFilterByRarity() {
        Rarity rarity = readEnum(Rarity.class, "Rarity (COMMON/UNCOMMON/RARE/EPIC): ");
        List<Item> results = service.filterByRarity(rarity);
        printResults("RESULTS", results);
        pause();
    }

    private void printResults(String title, List<Item> items) {
        System.out.println(ItemTablePrinter.format(title, items));
    }

    // Hjælpemetoder til robust brugerinput.
    // UI håndterer kun input-fejl (NumberFormatException / IllegalArgumentException).
    // Domæne- og forretningsfejl håndteres i service- og domain-lag.

    // ---------- ui hjælpemetoder ----------

    // Læser et gyldigt menuvalg mellem min og max (bruges af både hoved- og undermenuer)
    @SuppressWarnings("SameParameterValue")
    private int readMenuChoice(int min, int max) {
        int choice;

        // læs et gyldigt menuvalg mellem min og max
        while (true) {
            System.out.print("Choose between (" + min + "-" + max + "): ");
            choice = readInt();

            if (choice >= min && choice <= max) {
                return choice;
            }

            ConsoleUI.message("Invalid choice. Please choose a number between " + min + " and " + max + ".");
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

    // Læser double robust:
    // - accepterer både "1.5" og "1,5"
    private double readDouble() {
        while (true) {
            String line = input.nextLine().trim();

            // Dansk komma-support
            line = line.replace(',', '.');

            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private <T extends Enum<T>> T readEnum(Class<T> enumClass, String prompt) {
        while (true) {
            System.out.print(prompt);
            String inputText = input.nextLine().trim();

            try {
                return Enum.valueOf(enumClass, inputText.toUpperCase());
            } catch (IllegalArgumentException e) {
                ConsoleUI.message("Invalid input. Try again.");
            }
        }
    }

    private boolean isValidUnequipSlot(String slot) {
        if (slot == null) return false;
        String s = slot.trim().toLowerCase();

        return s.equals("mainhand") || s.equals("offhand") ||
                s.equals("head") || s.equals("chest") ||
                s.equals("legs") || s.equals("feet");
    }

    private void pause() {
        System.out.println("Press Enter to continue...");
        input.nextLine();
    }
}
