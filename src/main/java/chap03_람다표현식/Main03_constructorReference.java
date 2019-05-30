package chap03_람다표현식;

import classes.Apple;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class Main03_constructorReference {
    public static void main(String[] args) {
        // () -> Apple
        Supplier<Apple> defaultConstructor = Apple::new;
        Apple a1 = defaultConstructor.get();

        // (int) -> Apple
        Function<Integer, Apple> intConstructor = Apple::new;
        IntFunction<Apple> intConstructor2 = Apple::new; // 기본형 특화
        Apple a2, a3;
        a2 = intConstructor.apply(50);
        a3 = intConstructor2.apply(100);
    }
}
