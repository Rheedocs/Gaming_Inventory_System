package domain;

import domain.enums.ItemType;
import domain.enums.Rarity;

// Repræsenterer forbrugsitems (potions mv.) med effekt og stackSize.
public class Consumable extends Item {

    private String effectType;
    private int stackSize;

    public Consumable(String name, Rarity rarity, double weight, int stackSize) {
        super(name, ItemType.CONSUMABLE, rarity, weight);
        this.stackSize = stackSize;
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

    public void setStackSize(int stackSize) {
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
