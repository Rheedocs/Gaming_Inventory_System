import Exceptions.MaxWeightReached;
import Exceptions.NegativeValues;
import Inventory.Item;
import PlayerBase.Player;

public class InventorySystem {
    public static void main(String[] args) {

        Player player = new Player("Per",27, 26);
        Item myitem = new Item("Sword", "Weapon", "Common", 1.5);
        System.out.println(player);
        System.out.println(myitem);


        try {
            Player p = new Player("Bronny", -20,34);
        }
        catch (NegativeValues e) {
            System.out.println("Error: " + e.getMessage());
        }
        catch (MaxWeightReached e){
            System.out.println("Error: " +  e.getMessage());
        }
    }
}
