package domain;

import domain.enums.ArmourSlot;
import domain.enums.ItemType;
import domain.enums.Rarity;
import exceptions.NegativeValues;

// Repræsenterer rustning med defence-værdi og slot (Head/Chest/Legs/Feet).
public class Armour extends Item {

    private int defence;
    private ArmourSlot slot; // fx Head, Chest, Legs, Feet

    public Armour(String name, Rarity rarity, double weight, int defence, ArmourSlot slot) {
        super(name, ItemType.ARMOUR, rarity, weight);

        // Validerer data i domain-laget.
        setDefence(defence);

        this.slot = slot;
    }

    public int getDefence() {
        return defence;
    }

    // Beskytter domain mod ugyldige værdier.
    // Fejlen håndteres videre oppe i service-laget.
    public void setDefence(int defence) {
        if (defence < 0) {
            throw new NegativeValues("Defence cannot be negative.");
        }
        this.defence = defence;
    }

    public ArmourSlot getSlot() {
        return slot;
    }

    public void setSlot(ArmourSlot slot) {
        this.slot = slot;
    }

    @Override
    public String toString() {
        return "Name: " + getName() +
                ", Type: " + getType() +
                ", Rarity: " + getRarity() +
                ", Weight: " + getWeight() +
                ", Defence: " + defence +
                ", Slot: " + slot;
    }
}
