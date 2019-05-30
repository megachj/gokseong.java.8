package chap03_람다표현식;

import chap03_람다표현식.classes.StringComparator;

import java.util.Arrays;
import java.util.List;

public class Main03_methodReference {
    public static void main(String[] args) {
        // 메서드 레퍼런스
        List<String> str;

        // 1. ClassName::staticMethod
        // (String s1, String s2) -> int
        str = Arrays.asList("a", "b", "A", "B");
        str.sort(StringComparator::compareStaticMethod); // StringComparator.compareStaticMethod(s1, s2);
        System.out.println(str);

        // 2. ClassName::instanceMethod, 여기서 ClassName 은 파라미터의 첫 번째 인자와 자료형이 같아야 한다.
        // (String s2) -> int
        // 파라미터 첫 번째 인자인 String s1 과 String::method 명의 ClassName 이 같다. 같기 때문에 String s1을 생략할 수 있고 따라서 제외된 것이다.
        str = Arrays.asList("a", "b", "A", "B");
        str.sort(String::compareToIgnoreCase); // s1.compareToIgnoreCase(s2);
        System.out.println(str);

        // 3. 객체::instanceMethod
        // (String s1, String s2) -> int
        StringComparator stringComparator = new StringComparator();
        str = Arrays.asList("a", "b", "A", "B");
        str.sort(stringComparator::compareInstanceMethod);
        System.out.println(str);
    }
}
