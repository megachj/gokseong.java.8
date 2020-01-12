package chap11_CompletableFuture;

import java.util.concurrent.*;

public class FutureOldVersionExample {
    public static void main(String[] args) {
        System.out.println("main ThreadName: " + Thread.currentThread().getName());

        FutureOldVersionExample futureExample = new FutureOldVersionExample();
        futureExample.doFutureOldVersion(2000);
    }

    public void doFutureOldVersion(int millis) {
        System.out.println("doFutureOldVersion ThreadName: " + Thread.currentThread().getName());

        // 스레드 풀에 태스크를 제출하려면 ExecutorService 를 만들어야 함
        ExecutorService executor = Executors.newCachedThreadPool();

        // Callable 을 ExecutorService 로 제출
        Future<Integer> future = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return doSomeLongComputation(millis);
            }
        });

        // 비동기 작업 중 할 다른 작업 실행
        doSomethingElse();

        try {
            // 비동기 작업의 결과를 가져옴. 결과가 준비되어 있지 않으면 호출 스레드가 블록된다.
            // 하지만 최대 1초까지만 기다린다.
            int result = future.get(1, TimeUnit.SECONDS);
            System.out.println("비동기 작업 시간: " + result + " ms");
        } catch (ExecutionException ee) {
            System.out.println("계산 중 예외 발생");
        } catch (InterruptedException ie) {
            System.out.println("현재 스레드에서 대기 중 인터럽트 발생");
        } catch (TimeoutException te) {
            System.out.println("Future가 완료되기 전에 타임아웃 발생");
        }
    }

    private int doSomeLongComputation(int millis) {
        System.out.println("doSomeLongComputation ThreadName: " + Thread.currentThread().getName());
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return millis;
    }

    private void doSomethingElse() {
        System.out.println("doSomethingElse ThreadName: " + Thread.currentThread().getName());
    }
}
