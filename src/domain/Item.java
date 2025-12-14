package domain;

import domain.enums.ItemType;
import domain.enums.Rarity;

// Grundlæggende item i systemet. Weapon, Armour og Consumable nedarver fra denne.
public class Item {

    private String name;
    private ItemType type;
    private Rarity rarity;
    private double weight;

    public Item(String name, ItemType type, Rarity rarity, double weight) {
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { // simpelt navne skift
        this.name = name;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Name: " + name +
                ", Type: " + type +
                ", Rarity: " + rarity +
                ", Weight: " + weight;
    }
}
