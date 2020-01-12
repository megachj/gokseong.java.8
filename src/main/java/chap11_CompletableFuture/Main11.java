package chap11_CompletableFuture;

import chap11_CompletableFuture.classes.Shop;

import java.util.concurrent.Future;
import java.util.function.Supplier;

public class Main11 {

    public static void main(String[] args) {

        // 예제 1: 비동기 API 호출
        System.out.println("-------------------- 예제 1 --------------------");
//        callAsyncFunc();

        // 예제 2: 동기 API 비블록 코드 만들기
        System.out.println("-------------------- 예제 2 --------------------");
        Shop shopExample2 = new Shop("shopExample2", 5);

//        System.out.println("--- Sync Serial Stream ---");
//        callSyncFunc(() -> shopExample2.findPricesSyncSerial("computer"));

//        System.out.println("--- Sync Parallel Stream ---");
//        callSyncFunc(() -> shopExample2.findPricesSyncParallel("computer"));

//        System.out.println("--- Sync CompletableFuture ---");
//        callSyncFunc(() -> shopExample2.findPricesSyncCompletableFuture("computer"));

        // 예제 3: 비동기 작업 파이프라인 만들기
        System.out.println("-------------------- 예제 3 --------------------");

//        System.out.println("--- Sync Serial Stream ---");
//        callSyncFunc(() -> shopExample2.findDiscountedPricesSerial("computer"));

        System.out.println("--- Sync CompletableFuture ---");
        callSyncFunc(() -> shopExample2.findDiscountedPricesCompletableFuture("computer"));
    }

    private static void callAsyncFunc() {
        Shop shopExample1 = new Shop("shopExample1");
        long start = System.nanoTime();
        Future<Double> futurePrice = shopExample1.getPriceAsync("computer");
        long invocationTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Invocation returned after " + invocationTime + " ms");

        // 제품 가격 계산하는 동안, 다른 작업 수행
        doSomethingElse();
        try {
            double price = futurePrice.get();
            System.out.printf("Price is %.2f%n", price);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Price returned after " + retrievalTime + " ms");
        System.out.println();
    }

    private static <T> void callSyncFunc(Supplier<T> supplier) {
        long start = System.nanoTime();
        T result = supplier.get();
        System.out.println(result);
        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in " + duration + " ms");
    }

    private static void doSomethingElse() {
        System.out.println("doSomethingElse ThreadName: " + Thread.currentThread().getName());
    }

    public static void delay() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
