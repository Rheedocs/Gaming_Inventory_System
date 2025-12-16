package ui;

import domain.*;
import java.util.Locale;

// Printer/formatter til Inventory output i konsollen.
// Ligger i ui-pakken fordi det her er præsentation (ikke domain-logik).
public class InventoryPrinter {

    // Returnerer en teksttabel med alle items og samlet status (vægt, slots).
    // Tabellen er bevidst lavet i klassisk text-adventure / RPG-stil
    // med lodrette skillelinjer, så kolonnerne er tydelige i konsollen.
    public static String format(Inventory inventory) {
        StringBuilder sb = new StringBuilder();

        sb.append("====== INVENTORY OVERVIEW ======\n");

        // Fast ASCII-ramme for tabellen
        // Gør output lettere at læse og mere stabilt end kun mellemrum
        sb.append("+----+----------------------+------------+------------+--------+\n");
        sb.append("| No | Name                 | Type       | Rarity     | Weight |\n");
        sb.append("+----+----------------------+------------+------------+--------+\n");

        if (inventory.getItems().isEmpty()) {
            // Vis tom inventory på en pæn måde i tabellen
            sb.append("|    | Inventory is empty                                      |\n");
        } else {
            int index = 1;

            for (Item item : inventory.getItems()) {

                // lille UI-hjælp: vis stack direkte i navnet for consumables
                // fx "Healing Potion x5"
                String displayName = item.getName();
                if (item instanceof Consumable c) {
                    displayName += " x" + c.getStackSize();
                }

                // Hver række formatteres med faste bredder,
                // så alle kolonner flugter lodret i konsollen
                // Locale.ROOT sikrer at decimaler altid bruger punktum (.) uanset OS-sprog
                sb.append(String.format(Locale.ROOT,
                        "| %-2d | %-20s | %-10s | %-10s | %6.1f |\n",
                        index++,
                        displayName,
                        item.getType(),
                        item.getRarity(),
                        item.getWeight()
                ));
            }
        }

        // Afsluttende ramme
        sb.append("+----+----------------------+------------+------------+--------+\n");

        // Samlet inventory-status (antal items, vægt og slots)
        // Locale.ROOT sikrer punktum (.) i total weight også
        sb.append(String.format(Locale.ROOT,
                "Items: %d | Total weight: %.1f / %.1f | Unlocked slots: %d / %d%n",
                inventory.getItems().size(),
                inventory.getTotalWeight(),
                inventory.getMaxWeight(),
                inventory.getUnlockedSlots(),
                inventory.getMaxSlots()
        ));
        return sb.toString();
    }
}
