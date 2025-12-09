package exceptions;

// Checked exception til når et item ikke findes, hvis man vil bruge exceptions i stedet for booleans.
public class ItemNotFound extends Exception{
    public ItemNotFound(String message){
        super(message);
    }
}
