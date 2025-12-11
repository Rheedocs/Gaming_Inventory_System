package domain;

// Repræsenterer et våben med skade og hvilken hånd-type (OneHand/OffHand/TwoHand).
public class Weapon extends Item {

    private int damage;
    private HandType handType; // OneHand, OffHand, TwoHand

    public Weapon(String name, Rarity rarity, double weight, int damage, HandType handType) {
        super(name, ItemType.WEAPON, rarity, weight);
        this.damage = damage;
        this.handType = handType;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public HandType getHandType() {
        return handType;
    }

    public void setHandType(HandType handType) {
        this.handType = handType;
    }
}
