package chap03_람다표현식.classes;

public class StringComparator {
    public static int compareStaticMethod(String s1, String s2) {
        return s1.compareToIgnoreCase(s2);
    }

    public int compareInstanceMethod(String s1, String s2) {
        return s1.compareToIgnoreCase(s2);
    }
}
