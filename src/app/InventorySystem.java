package app;

import ui.Menu;

// Indgang til programmet. Starter UI-laget (Menu), som styrer resten af systemet.
public class InventorySystem {

    public static void main(String[] args) {
        new Menu().start();// starter hele systemet via UI-laget
    }
}
