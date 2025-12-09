package domain;

// Håndterer hvad spilleren har equippet i hænder og rustnings-slots.
public class Equipment {

    private Weapon mainHand;
    private Weapon offHand;

    private Armour head;
    private Armour chest;
    private Armour legs;
    private Armour feet;

    // Forsøger at equippe et våben i korrekt hånd
    public boolean equipWeapon(Weapon w) {

        String hand = w.getHandtype();

        // TwoHand fylder begge hænder og rydder offhand
        if (hand.equalsIgnoreCase("TwoHand")) {
            mainHand = w;
            offHand = null;
            return true;
        }

        // OneHand prøver først mainHand, derefter OffHand
        if (hand.equalsIgnoreCase("OneHand")) {
            if (mainHand == null) {
                mainHand = w;
                return true;
            }
            if (offHand == null) {
                offHand = w;
                return true;
            }
            return false;
        }

        // OffHand kan kun ligge i offHand
        if (hand.equalsIgnoreCase("OffHand")) {
            if (offHand == null) {
                offHand = w;
                return true;
            }
            return false;
        }

        return false;
    }

    // Sætter rustning i korrekt slot baseret på tekst (head/chest/legs/feet)
    public boolean equipArmour(Armour a) {
        String s = a.getSlot();

        switch (s.toLowerCase()) {
            case "head" -> { head = a; return true; }
            case "chest" -> { chest = a; return true; }
            case "legs" -> { legs = a; return true; }
            case "feet" -> { feet = a; return true; }
            default -> { return false; }
        }
    }

    // Fjerner item fra valgt slot og returnerer det
    public Item unequip(String slot) {
        Item removed = null;

        switch (slot.toLowerCase()) {
            case "mainhand" -> {
                removed = mainHand;
                mainHand = null;
            }
            case "offhand" -> {
                removed = offHand;
                offHand = null;
            }
            case "head" -> {
                removed = head;
                head = null;
            }
            case "chest" -> {
                removed = chest;
                chest = null;
            }
            case "legs" -> {
                removed = legs;
                legs = null;
            }
            case "feet" -> {
                removed = feet;
                feet = null;
            }
        }
        return removed; // null hvis slot ukendt eller tomt
    }

    public String getOverview() {
        return
                "MainHand: " + (mainHand != null ? mainHand.getName() : "-") + "\n" +
                        "OffHand: " + (offHand != null ? offHand.getName() : "-") + "\n" +
                        "Head: " + (head != null ? head.getName() : "-") + "\n" +
                        "Chest: " + (chest != null ? chest.getName() : "-") + "\n" +
                        "Legs: " + (legs != null ? legs.getName() : "-") + "\n" +
                        "Feet: " + (feet != null ? feet.getName() : "-");
    }

    public boolean isEmpty() {
        return mainHand == null &&
                offHand == null &&
                head == null &&
                chest == null &&
                legs == null &&
                feet == null;
    }
}
