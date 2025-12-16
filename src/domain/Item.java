package domain;

import domain.enums.ItemType;
import domain.enums.Rarity;
import exceptions.NegativeValues;

// Grundlæggende item i systemet.
// Weapon, Armour og Consumable nedarver fra denne.
public class Item {

    private String name;
    private ItemType type;
    private Rarity rarity;
    private double weight;

    public Item(String name, ItemType type, Rarity rarity, double weight) {
        this.name = name;
        this.type = type;
        this.rarity = rarity;

        // Validerer data i domain-laget.
        // Domain må aldrig tillade ugyldige tilstande (fx negativ/0 vægt).
        // Derfor kastes en custom exception ved ugyldigt input.
        setWeight(weight); // validerer
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { // simpelt navneskift
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

    // Beskytter domain mod ugyldige værdier.
    // Fejlen håndteres videre oppe i service-laget.
    public void setWeight(double weight) {
        if (weight <= 0) {
            throw new NegativeValues("Weight must be > 0.");
        }
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
