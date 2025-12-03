package domain;

public class Armour extends Item {

    private int defence;
    private String slot;

    public Armour(String name, String type, String rarity, double weight, int defence, String slot) {
        super(name, type, rarity, weight);
        this.defence = defence;
        this.slot = slot;
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }
}
