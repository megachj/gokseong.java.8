package chap11_CompletableFuture.classes;

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

    public static void main(String[] args) {
        long start, invocationTime, duration;
        // 예제 1
        System.out.println("-------------------- 예제 1 --------------------");
        Shop shopExample1 = new Shop("shopExample1");
        start = System.nanoTime();
        Future<Double> futurePrice = shopExample1.getPriceAsync("my favorite product");
        invocationTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Invocation returned after " + invocationTime + " ms");

        // 제품 가격 계산하는 동안, 다른 작업 수행
        doSomethingElse();
        try {
            double price = futurePrice.get();
            System.out.printf("Price is %.2f%n",price);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Price returned after " + retrievalTime + " ms");
        System.out.println();

        // 예제 2: 비블록 코드 만들기
        Shop shopExample2 = new Shop("shopExample2", 20);
        System.out.println("-------------------- 예제 2 --------------------");

        /*
        System.out.println("--- Sync Serial Stream ---");
        start = System.nanoTime();
        System.out.println(shopExample2.findPricesSyncSerial("myPhone27S"));
        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in " + duration + " ms");
        */

        System.out.println("--- Sync Parallel Stream ---");
        start = System.nanoTime();
        System.out.println(shopExample2.findPricesSyncParallel("myPhone27S"));
        duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in " + duration + " ms");

        System.out.println("--- Sync CompletableFuture ---");
        start = System.nanoTime();
        System.out.println(shopExample2.findPricesSyncCompletableFuture("myPhone27S"));
        duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in " + duration + " ms");
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

    private String getMessage(String shopName, double price, Discount.Code code) {
        return String.format("%s:%.2f:%s", shopName, price, code);
    }

    private double calculatePrice(String product) {
        delay();
        Random random = new Random();
        return random.nextDouble() * product.charAt(0) + product.charAt(1);
    }

    private static void doSomethingElse() {
        System.out.println("doSomethingElse ThreadName: " + Thread.currentThread().getName());
    }

    private static void delay() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
