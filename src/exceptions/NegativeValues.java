package exceptions;

// Runtime exception til at beskytte mod negative værdier i input.
public class NegativeValues extends RuntimeException{
    public NegativeValues(String message){
        super(message);
    }
}
