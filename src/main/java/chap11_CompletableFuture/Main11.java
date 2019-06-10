package chap11_CompletableFuture;

import chap11_CompletableFuture.classes.IterationCalculator;

public class Main11 {
    public static void main(String[] args) {
        IterationCalculator iterationCalculator = new IterationCalculator(1000000);
        iterationCalculator.asyncFunction();

        iterationCalculator = new IterationCalculator(100000000);
        iterationCalculator.asyncFunction();

        iterationCalculator = new IterationCalculator(1000000000);
        iterationCalculator.asyncFunction();

        iterationCalculator = new IterationCalculator(100000000000L);
        iterationCalculator.asyncFunction();
    }
}
