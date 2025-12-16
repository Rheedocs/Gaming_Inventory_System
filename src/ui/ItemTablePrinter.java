package ui;

import domain.Consumable;
import domain.Item;

import java.util.List;
import java.util.Locale;

// Printer en tabel for vilkårlige item-lister (search/filter/equip).
// Ligger i ui-pakken fordi det her er præsentation (ikke domain-logik).
public class ItemTablePrinter {

    public static String format(String title, List<Item> items) {
        StringBuilder sb = new StringBuilder();

        sb.append("====== ").append(title).append(" ======\n");

        sb.append("+----+----------------------+------------+------------+--------+\n");
        sb.append("| No | Name                 | Type       | Rarity     | Weight |\n");
        sb.append("+----+----------------------+------------+------------+--------+\n");

        if (items == null || items.isEmpty()) {
            sb.append("|    | No results                                              |\n");
            sb.append("+----+----------------------+------------+------------+--------+\n");
            return sb.toString();
        }

        int index = 1;
        for (Item item : items) {

            // Vis stack direkte i navnet for consumables
            String displayName = item.getName();
            if (item instanceof Consumable c) {
                displayName += " x" + c.getStackSize();
            }

            sb.append(String.format(Locale.ROOT,
                    "| %-2d | %-20s | %-10s | %-10s | %6.1f |\n",
                    index++,
                    displayName,
                    item.getType(),
                    item.getRarity(),
                    item.getWeight()
            ));
        }

        sb.append("+----+----------------------+------------+------------+--------+\n");
        return sb.toString();
    }
}
