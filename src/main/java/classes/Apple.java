package classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Apple {
    private int weight;
    private String country;

    public Apple(int weight) {
        this.weight = weight;
        this.country = "UNKNOWN";
    }
}
