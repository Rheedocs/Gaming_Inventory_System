package exceptions;

// Runtime exception hvis inventory rammer maxWeight.
public class MaxWeightReached extends RuntimeException {
    public MaxWeightReached(String message){
        super(message);
    }
}
