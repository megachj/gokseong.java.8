package chap02_동작파라미터;

import chap30_enum정의.classes.Person;

public class Main02 {
    public static void main(String[] args) {
        System.out.println("Main02 start");

        Person person = new Person("choe", 19);
        System.out.println(person.getName());
    }
}
