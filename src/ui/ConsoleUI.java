package ui;

public class ConsoleUI {

    // Fast bredde, matcher menu- og feedbackbokse
    private static final int WIDTH = 62;

    private static String line() {
        return "+" + "-".repeat(WIDTH) + "+";
    }

    // Header med centreret titel
    public static void header(String title) {
        System.out.println(line());
        System.out.println("|" + center(title) + "|");
        System.out.println(line());
    }

    // Menu-option, venstrejusteret
    public static void option(int number, String text) {
        String s = String.format("    %d. %s", number, text);
        System.out.println("|" + padRight(s) + "|");
    }

    public static void footer() {
        System.out.println(line());
    }

    // Kort status-/informationsbesked i boks
    public static void message(String text) {
        System.out.println(line());
        System.out.println("|" + padRight(" " + text) + "|");
        System.out.println(line());
    }

    // ---------- Hjælpemetoder ----------

    private static String padRight(String s) {
        if (s.length() > ConsoleUI.WIDTH) {
            return s.substring(0, ConsoleUI.WIDTH);
        }
        return s + " ".repeat(ConsoleUI.WIDTH - s.length());
    }

    private static String center(String s) {
        if (s.length() > ConsoleUI.WIDTH) {
            return s.substring(0, ConsoleUI.WIDTH);
        }
        int left = (ConsoleUI.WIDTH - s.length()) / 2;
        int right = ConsoleUI.WIDTH - s.length() - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }
}
