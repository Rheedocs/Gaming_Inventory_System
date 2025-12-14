package ui;

import domain.*;
import java.util.Locale;

// Printer/formatter til Equipment output i konsollen.
// Ligger i ui-pakken fordi det her er præsentation (ikke domain-logik).
public class EquipmentPrinter {

    // Returnerer en teksttabel med alle equipped items i klassisk RPG-stil.
    // Samme look & feel som inventory-tabellen (ASCII-rammer og | kolonner).
    public static String format(Equipment eq) {
        StringBuilder sb = new StringBuilder();

        sb.append("====== EQUIPMENT OVERVIEW ======\n");

        sb.append("+----------+----------------------+------------+------------+--------+\n");
        sb.append("| Slot     | Name                 | Type       | Rarity     | Weight |\n");
        sb.append("+----------+----------------------+------------+------------+--------+\n");

        // MainHand
        appendRow(sb, "MainHand", eq.getMainHand());
        // OffHand
        appendRow(sb, "OffHand", eq.getOffHand());

        // Armour slots
        appendRow(sb, "Head", eq.getHead());
        appendRow(sb, "Chest", eq.getChest());
        appendRow(sb, "Legs", eq.getLegs());
        appendRow(sb, "Feet", eq.getFeet());

        sb.append("+----------+----------------------+------------+------------+--------+\n");

        return sb.toString();
    }

    // Hjælper til at printe én slot-linje, også når slot er tomt.
    private static void appendRow(StringBuilder sb, String slot, Item item) {

        if (item == null) {
            sb.append(String.format(Locale.ROOT,
                    "| %-8s | %-20s | %-10s | %-10s | %6s |\n",
                    slot, "-", "-", "-", "-"
            ));
            return;
        }

        String displayName = item.getName();

        // lille UI-hjælp: vis stack i navnet for consumables (hvis I en dag equipper dem)
        if (item instanceof Consumable c) {
            displayName += " x" + c.getStackSize();
        }

        // Locale.ROOT sikrer at decimaler altid bruger punktum (.) uanset OS-sprog
        sb.append(String.format(Locale.ROOT,
                "| %-8s | %-20s | %-10s | %-10s | %6.1f |\n",
                slot,
                displayName,
                item.getType(),
                item.getRarity(),
                item.getWeight()
        ));
    }
}
