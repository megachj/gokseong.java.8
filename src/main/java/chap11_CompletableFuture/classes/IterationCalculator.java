package chap11_CompletableFuture.classes;

import java.util.concurrent.*;

public class IterationCalculator {

    private long iterationCount;

    public IterationCalculator(long iterationCount) {
        this.iterationCount = iterationCount;
    }

    public void asyncFunction() {
        ExecutorService executor = Executors.newCachedThreadPool();
        // 비동기 작업
        Future<Double> future = executor.submit(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                double result = 0;
                for (long i = 0; i < iterationCount; i++) {
                    result++;
                }
                return result;
            }
        });

        // 비동기 작업 중 할 다른 작업
        System.out.println("비동기 작업 중에 다른 작업 실행중!");
        try {
            // 비동기 작업의 결과를 가져옴. 결과가 준비되어 있지 않으면 호출 스레드가 블록된다.
            // 하지만 최대 1초까지만 기다린다.
            Double result = future.get(1, TimeUnit.SECONDS);
            System.out.println("계산 결과: " + result);
        } catch (ExecutionException ee) {
            // 계산 중 예외 발생
        } catch (InterruptedException ie) {
            // 현재 스레드에서 대기 중 인터럽트 발생
        } catch (TimeoutException te) {
            // Future가 완료되기 전에 타임아웃 발생
        }
    }
}
