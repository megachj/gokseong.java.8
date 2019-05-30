package chap03_람다표현식;

import classes.Apple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Main03_expand {
    public static void main(String[] args) {
        List<Apple> inventory = Arrays.asList(new Apple(100, "Korea"), new Apple(50, "Japan"), new Apple(100, "America"));

        // 1단계: Comparator 인터페이스 구현 클래스 정의
        List<Apple> copiedList = new ArrayList<>(inventory);
        copiedList.sort(new AppleComparator());
        System.out.println(copiedList);

        // 2단계: 익명 클래스
        copiedList = new ArrayList<>(inventory);
        copiedList.sort(new Comparator<Apple>() {
            @Override
            public int compare(Apple o1, Apple o2) {
                int weightCompare = Integer.compare(o1.getWeight(), o2.getWeight());
                if (weightCompare != 0)
                    return weightCompare;
                return o1.getCountry().compareToIgnoreCase(o2.getCountry());
            }
        });
        System.out.println(copiedList);

        // 3단계: 람다
        copiedList = new ArrayList<>(inventory);
        copiedList.sort((Apple a1, Apple a2) -> {
            int weightCompare = Integer.compare(a1.getWeight(), a2.getWeight());
            if (weightCompare != 0)
                return weightCompare;
            return a1.getCountry().compareToIgnoreCase(a2.getCountry());
        });
        System.out.println(copiedList);

        // 4단계: 메서드 레퍼런스
        copiedList = new ArrayList<>(inventory);
        copiedList.sort(Comparator.comparing(Apple::getWeight).thenComparing(Apple::getCountry));
        System.out.println(copiedList);
    }

    private static class AppleComparator implements Comparator<Apple> {
        @Override
        public int compare(Apple o1, Apple o2) {
            int weightCompare = Integer.compare(o1.getWeight(), o2.getWeight());
            if (weightCompare != 0)
                return weightCompare;
            return o1.getCountry().compareToIgnoreCase(o2.getCountry());
        }
    }
}
