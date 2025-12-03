package exceptions;

public class MaxWeightReached extends RuntimeException {
    public MaxWeightReached(String message){
        super(message);
    }
}
