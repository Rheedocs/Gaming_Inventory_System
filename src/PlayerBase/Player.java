package PlayerBase;

import Exceptions.MaxWeightReached;
import Exceptions.NegativeValues;
import Inventory.Inventory;

public class Player {
    private String name;
    private double totalWeight;
    private int inventorySlots;

    private Inventory inventory;

    public Player(String name, double totalWeight, int inventorySlots) {
        this.name = name;
        this.inventory = new Inventory();
        syncFromInventory();

    }

    private void syncFromInventory() {
        this.totalWeight = inventory.getTotalWeight();
        this.inventorySlots = inventory.getUnlockedSlots();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) throws NegativeValues, MaxWeightReached {
        if (totalWeight >= 0 && totalWeight <= 50) {
            this.totalWeight = totalWeight;
        }
        else if (totalWeight < 0) {
            throw new NegativeValues("Weight cannot be negative");
        }
        else if (totalWeight > 50) {
            throw new MaxWeightReached("Maximum weight has been exceeded.");
        }
    }

    public int getInventorySlots() {
        return inventorySlots;
    }

    public void setInventorySlots(int inventorySlots) {
        this.inventorySlots = inventorySlots;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", TotalWeight: " + totalWeight + ", InventorySlots: " + inventorySlots;
    }
}
