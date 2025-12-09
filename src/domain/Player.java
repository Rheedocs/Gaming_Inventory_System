package domain;

// Repræsenterer spilleren med navn, inventory og udstyr (equipment).
public class Player {

    private String name;
    private Inventory inventory; // spillerens inventory
    private Equipment equipment = new Equipment();

    public Player(String name) {
        this.name = name;
        this.inventory = new Inventory(); // standard inventory-opsætning
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    @Override
    public String toString() {
        return "Player: " + name;
    }
}
