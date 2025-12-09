package domain;

// Repræsenterer et våben med skade og hvilken hånd-type (OneHand/OffHand/TwoHand).
public class Weapon extends Item {

    private int damage;
    private String handtype; // OneHand, OffHand, TwoHand

    public Weapon(String name, String type, String rarity, double weight) {
        super(name, type, rarity, weight);
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public String getHandtype() {
        return handtype;
    }

    public void setHandtype(String handtype) {
        this.handtype = handtype;
    }
}
