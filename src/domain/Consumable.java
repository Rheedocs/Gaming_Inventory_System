package domain;

import domain.enums.ItemType;
import domain.enums.Rarity;
import exceptions.NegativeValues;

// Repræsenterer forbrugsitems (potions mv.) med effekt og stackSize.
public class Consumable extends Item {

    private String effectType;
    private int stackSize;

    public Consumable(String name, Rarity rarity, double weight, int stackSize) {
        super(name, ItemType.CONSUMABLE, rarity, weight);

        // Validerer data i domain-laget.
        // Stack size må ikke være mindre end 1.
        setStackSize(stackSize);
    }

    public String getEffectType() {
        return effectType;
    }

    public void setEffectType(String effectType) {
        this.effectType = effectType;
    }

    public int getStackSize() { // nuværende stack
        return stackSize;
    }

    // Beskytter domain mod ugyldige værdier.
    // Fejlen håndteres videre oppe i service-laget.
    public void setStackSize(int stackSize) {
        if (stackSize < 1) {
            throw new NegativeValues("Stack size must be at least 1.");
        }
        this.stackSize = stackSize;
    }

    @Override
    public String toString() {
        String effect = (effectType != null) ? effectType : "None";
        return "Name: " + getName() +
                ", Type: " + getType() +
                ", Rarity: " + getRarity() +
                ", Weight: " + getWeight() +
                ", Effect: " + effect +
                ", StackSize: " + stackSize;
    }
}
