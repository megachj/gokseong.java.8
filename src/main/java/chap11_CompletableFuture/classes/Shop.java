package chap11_CompletableFuture.classes;

import chap11_CompletableFuture.Main11;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Getter
public class Shop {

    private String shopName;

    private List<Shop> relatedShops;

    private Executor executor;

    public Shop(String shopName) {
        this.shopName = shopName;
    }

    public Shop(String shopName, int relatedShopsCount) {
        this.shopName = shopName;
        this.relatedShops = new ArrayList<>(relatedShopsCount);
        for (int i = 1; i <= relatedShopsCount; i++)
            relatedShops.add(new Shop("Store" + i));

        if (relatedShopsCount <= 0)
            return;

        this.executor = Executors.newFixedThreadPool(Math.min(relatedShopsCount, 100),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        // 프로그램 종료를 방해하지 않는 데몬 스레드를 사용
                        // 자바 프로그램 종료시 데몬 스레드는 같이 종료됨.
                        Thread t = new Thread(r);
                        t.setDaemon(true);
                        return t;
                    }
                });
    }

    public double getPrice(String product) {
        return calculatePrice(product);
    }

    public Future<Double> getPriceAsync(String product) {
        // 계산 결과를 포함할 CompletableFuture 를 생성
        CompletableFuture<Double> futurePrice = new CompletableFuture<>();

        // 다른 스레드에서 작업 수행
        new Thread(() -> {
            try {
                double price = calculatePrice(product);
                futurePrice.complete(price); // 계산이 완료되면, Future 에 값을 설정
            } catch (Exception e) {
                futurePrice.completeExceptionally(e);
            }
        }).start();

        // 계산 결과가 완료되길 기다리지 않고 Future 반환
        return futurePrice;
    }

    public Future<Double> getPriceAsync2(String product) {
        // getPriceAsync 메서드와 같이 에러 핸들링이 된다.
        return CompletableFuture.supplyAsync(() -> calculatePrice(product));
    }

    public List<String> findPricesSyncSerial(String product) {
        return relatedShops.stream()
                .map(shop -> String.format("%s price is %.2f", shop.getShopName(), shop.getPrice(product)))
                .collect(Collectors.toList());
    }

    public List<String> findPricesSyncParallel(String product) {
        return relatedShops.parallelStream()
                .map(shop -> String.format("%s price is %.2f", shop.getShopName(), shop.getPrice(product)))
                .collect(Collectors.toList());
    }

    public List<String> findPricesSyncCompletableFuture(String product) {
        // NOTE: 주의) 아래처럼 두 개의 스트림 처리 파이프라인(List<CompletableFuture<String>> -> List<String>)로 진행하지 않고,
        //  한 개의 스트림 처리 파이프라인(map 을 2개 써서 바로 List<String>)로 진행하면 스트림의 게으른 특성때문에 순차 계산이 일어나게 된다.
        List<CompletableFuture<String>> priceFutures =
                relatedShops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> String.format("%s price is %.2f", shop.getShopName(), shop.getPrice(product)), executor))
                .collect(Collectors.toList());

        return priceFutures.stream()
                .map(CompletableFuture::join) // 모든 비동기 동작이 끝나길 기다림
                .collect(Collectors.toList());
    }

    public List<String> findDiscountedPricesSerial(String product) {
        return relatedShops.stream()
                .map(shop -> shop.getPriceAppliedDiscount(product)) // delay 1sec
                .map(Quote::parse)
                .map(Discount::applyDiscount) // delay 1sec
                .collect(Collectors.toList());
    }

    public List<String> findDiscountedPricesCompletableFuture(String product) {
        List<CompletableFuture<String>> priceFutures =
                relatedShops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPriceAppliedDiscount(product), executor)) // CompletableFuture<String> 생성
                .map(future -> future.thenApply(Quote::parse)) //
                .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(
                        () -> Discount.applyDiscount(quote), executor)))
                .collect(Collectors.toList());

        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private String getPriceAppliedDiscount(String product) {
        double price = calculatePrice(product);
        Discount.Code code = Discount.Code.values()[new Random().nextInt(Discount.Code.values().length)];
        return String.format("%s:%.2f:%s", shopName, price, code);
    }

    private double calculatePrice(String product) {
        Main11.delay();
        Random random = new Random();
        return random.nextDouble() * product.charAt(0) + product.charAt(1);
    }
}
