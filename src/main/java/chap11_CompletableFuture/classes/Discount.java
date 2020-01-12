package chap11_CompletableFuture.classes;

import chap11_CompletableFuture.Main11;

public class Discount {
    public enum Code {
        NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);

        private final int percentage;

        Code(int percentage) {
            this.percentage = percentage;
        }
    }

    public static String applyDiscount(Quote quote) {
        return quote.getShopName() + " price is " +
                Discount.apply(quote.getPrice(), quote.getDiscountCode());
    }

    public static double apply(double price, Code code) {
        Main11.delay();
        return price * (100 - code.percentage) / 100;
    }
}
