package chap10_Optional;

import chap10_Optional.classes.Car;
import chap10_Optional.classes.Insurance;
import chap10_Optional.classes.Person;

import java.util.Optional;

public class Main10 {
    public static void main(String[] args) {
        Insurance insurance = new Insurance("현대해상");
        Car car = new Car(Optional.of(insurance));
        Person person = new Person(Optional.of(car), 28);

        System.out.println(getCarInsuranceName(Optional.of(person)));
        System.out.println(getCarInsuranceName(Optional.of(person), 30));
    }

    public static String getCarInsuranceName(Optional<Person> person) {
        return person.flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                .orElse("Unknown");
    }

    public static String getCarInsuranceName(Optional<Person> person, int minAge) {
        return person.filter(p -> p.getAge() >= minAge)
                .flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                .orElse("Unknown");
    }
}
