package db;

import domain.Item;
import domain.ItemType;
import domain.Rarity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemRepository {

    // Save a single item to the DB
    public void saveItem(Item item) {
        String sql = "INSERT INTO items (name, type, rarity, weight) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, item.getName());
            ps.setString(2, item.getType().name());
            ps.setString(3, item.getRarity().name());
            ps.setDouble(4, item.getWeight());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("DB save error: " + e.getMessage());
        }
    }

    // Load all items from DB (as simple Item objects)
    public List<Item> loadAllItems() {
        List<Item> items = new ArrayList<>();

        String sql = "SELECT name, type, rarity, weight FROM items";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                ItemType type = ItemType.valueOf(rs.getString("type").toUpperCase());
                Rarity rarity = Rarity.valueOf(rs.getString("rarity").toUpperCase());
                double weight = rs.getDouble("weight");

                Item item = new Item(name, type, rarity, weight);
                items.add(item);
            }

        } catch (SQLException e) {
            System.err.println("DB load error: " + e.getMessage());
        }

        return items;
    }
}
