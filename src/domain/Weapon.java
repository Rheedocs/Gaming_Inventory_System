package domain;

import domain.enums.HandType;
import domain.enums.ItemType;
import domain.enums.Rarity;
import exceptions.NegativeValues;

// Repræsenterer et våben med skade og hvilken hånd-type (OneHand/OffHand/TwoHand).
public class Weapon extends Item {

    private int damage;
    private HandType handType; // ONE_HAND, OFF_HAND, TWO_HAND

    public Weapon(String name, Rarity rarity, double weight, int damage, HandType handType) {
        super(name, ItemType.WEAPON, rarity, weight);

        // Validerer data i domain-laget.
        setDamage(damage);

        this.handType = handType;
    }

    public int getDamage() {
        return damage;
    }

    // Beskytter domain mod ugyldige værdier.
    // Fejlen håndteres videre oppe i service-laget.
    public void setDamage(int damage) {
        if (damage < 0) {
            throw new NegativeValues("Damage cannot be negative.");
        }
        this.damage = damage;
    }

    public HandType getHandType() {
        return handType;
    }

    public void setHandType(HandType handType) {
        this.handType = handType;
    }

    @Override
    public String toString() {
        return "Name: " + getName() +
                ", Type: " + getType() +
                ", Rarity: " + getRarity() +
                ", Weight: " + getWeight() +
                ", Damage: " + damage +
                ", HandType: " + handType;
    }
}