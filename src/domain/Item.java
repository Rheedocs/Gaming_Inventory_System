package domain;

import exceptions.ItemNotFound;

public class Item {

    private String name;
    private String type;
    private String rarity;
    private double weight;

    public Item(String name, String type, String rarity, double weight) {
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this.weight = weight;
    }
    public String getName(){return name;}

    public void setName(String name) throws ItemNotFound {this.name = name;}

    public String getType(){return type;}

    public void setType(String type){
        this.type = type;
    }

    public String getRarity(){return rarity;}

    public void setRarity(String rarity){
        this.rarity = rarity;
    }

    public double getWeight(){return weight;}

    public void setWeight(double weight){
        this.weight = weight;
    }

    @Override
    public String toString(){
        return "Name: " + name + ", Type: " + type + ", Rarity: " + rarity + ", Weight: " + weight;
    }

}
