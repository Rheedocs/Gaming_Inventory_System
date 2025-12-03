package Inventory;

public class Weapon extends Item{
    private int damage;
    private String handtype;

    public Weapon (String name, String type, String rarity, double weight) {
        super(name, type, rarity, weight);
    }
    public void setDamage(int damage){
        this.damage = damage;
    }

    public int getDamage(){
        return damage;
    }
    public String getHandtype(){
        return handtype;
    }
    public void setHandtype(String handtype){
        this.handtype = handtype;
    }
}
