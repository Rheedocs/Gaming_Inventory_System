package db;

import domain.Item;

public class SimpleDbTest {
    public static void main(String[] args) {

        ItemRepository repo = new ItemRepository();

        // Save a test item
        repo.saveItem(new Item("Test Sword2", "Weapon", "Common", 3.5));

        // Load all items
        var items = repo.loadAllItems();
        System.out.println("Items in DB:");
        for (Item i : items) {
            System.out.println(i);
        }
    }
}
