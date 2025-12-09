package domain;

// Repræsenterer forbrugsitems (potions mv.) med effekt og stackSize.
public class Consumable extends Item {

    private String effectType;
    private int stackSize;

    public Consumable(String name, String type, String rarity, double weight) {
        super(name, type, rarity, weight);
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
}
