package confuse;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MapExample {

    @Test
    public void mapNull() {
        Map<String, String> map = new HashMap<>();
        map.put("1", "a");
        map.put("2", "b");

        System.out.println(map.get(null));
        System.out.println(map.containsKey(null));

        map.put(null, "c");

        System.out.println(map.get(null));
        System.out.println(map.containsKey(null));
    }
}
