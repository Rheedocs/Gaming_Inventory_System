package domain;

// Repræsenterer rustning med defence-værdi og slot (Head/Chest/Legs/Feet).
public class Armour extends Item {

    private int defence;
    private ArmourSlot slot; // fx Head, Chest, Legs, Feet

    public Armour(String name, Rarity rarity, double weight, int defence, ArmourSlot slot) {
        super(name, ItemType.ARMOUR, rarity, weight);
        this.defence = defence;
        this.slot = slot;
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public ArmourSlot getSlot() {
        return slot;
    }

    public void setSlot(ArmourSlot slot) {
        this.slot = slot;
    }
}
