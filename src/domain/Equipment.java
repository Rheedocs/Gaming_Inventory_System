package domain;

import domain.enums.ArmourSlot;
import domain.enums.HandType;

import java.util.ArrayList;
import java.util.List;

// Håndterer hvad spilleren har equippet i hænder og rustnings-slots.
public class Equipment {

    private Weapon mainHand;
    private Weapon offHand;

    private Armour head;
    private Armour chest;
    private Armour legs;
    private Armour feet;

    // Returnerer alle items der blev erstattet/ryddet ud (0-2 stk).
    // Bruges af service til at putte dem tilbage i inventory.
    public List<Item> equipWeapon(Weapon weapon) {

        List<Item> replaced = new ArrayList<>();

        // TWO_HAND: kræver begge hænder -> alt i hænderne ryger ud
        if (weapon.getHandType() == HandType.TWO_HAND) {

            if (mainHand != null) replaced.add(mainHand);
            if (offHand != null) replaced.add(offHand);

            mainHand = weapon;
            offHand = null;
            return replaced;
        }

        // ONE_HAND / OFF_HAND:
        // 1) hvis main er tom -> put i main
        if (mainHand == null) {
            mainHand = weapon;
            return replaced;
        }

        // 2) hvis off er tom -> put i off
        if (offHand == null) {
            offHand = weapon;
            return replaced;
        }

        // 3) fallback: erstat mainhand
        replaced.add(mainHand);
        mainHand = weapon;
        return replaced;
    }

    // Returnerer evt. erstattet armour (eller null)
    public Item equipArmour(Armour a) {

        ArmourSlot slot = a.getSlot();
        Item replaced = null;

        switch (slot) {
            case HEAD -> { replaced = head; head = a; }
            case CHEST -> { replaced = chest; chest = a; }
            case LEGS -> { replaced = legs; legs = a; }
            case FEET -> { replaced = feet; feet = a; }
        }

        return replaced;
    }

    // Fjerner item fra valgt slot og returnerer det (UI må gerne sende tekst)
    public Item unequip(String slot) {
        Item removed = null;

        switch (slot.toLowerCase()) {
            case "mainhand" -> { removed = mainHand; mainHand = null; }
            case "offhand" -> { removed = offHand; offHand = null; }
            case "head" -> { removed = head; head = null; }
            case "chest" -> { removed = chest; chest = null; }
            case "legs" -> { removed = legs; legs = null; }
            case "feet" -> { removed = feet; feet = null; }
        }
        return removed; // null hvis slot ukendt eller tomt
    }

    // Bruges kun som rollback hvis inventory ikke kan tage imod (full/weight limit).
    // Vi sætter direkte tilbage i samme slot, uden at køre normal equip-regler.
    public void restoreToSlot(String slot, Item item) {
        if (slot == null || item == null) return;

        switch (slot.toLowerCase()) {
            case "mainhand" -> { if (item instanceof Weapon w) mainHand = w; }
            case "offhand" -> { if (item instanceof Weapon w) offHand = w; }
            case "head" -> { if (item instanceof Armour a) head = a; }
            case "chest" -> { if (item instanceof Armour a) chest = a; }
            case "legs" -> { if (item instanceof Armour a) legs = a; }
            case "feet" -> { if (item instanceof Armour a) feet = a; }
        }
    }

    public boolean isEmpty() {
        return mainHand == null &&
                offHand == null &&
                head == null &&
                chest == null &&
                legs == null &&
                feet == null;
    }

    // --- getters (bruges af UI til at vise equipment i tabelformat) ---
    public Weapon getMainHand() { return mainHand; }
    public Weapon getOffHand() { return offHand; }

    public Armour getHead() { return head; }
    public Armour getChest() { return chest; }
    public Armour getLegs() { return legs; }
    public Armour getFeet() { return feet; }
}
