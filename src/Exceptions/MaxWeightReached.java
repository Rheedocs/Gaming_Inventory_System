package Exceptions;

public class MaxWeightReached extends RuntimeException {
    public MaxWeightReached(String message){
        super(message);
    }
}
