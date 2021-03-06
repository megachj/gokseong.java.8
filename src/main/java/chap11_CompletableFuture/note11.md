## 멀티태스크 개념
### 동시성, 병렬성
* 동시성(concurrency): 한 코어에 여러 작업을 나눠 진행
* 병렬성(parallelism): 여러개 코어에 여러 작업을 나눠 진행

### 동기 API, 비동기 API
* 전통적인 동기 API는 호출한 메서드의 계산이 완료될 때까지 기다리고, 다음 동작을 수행한다.
호출자, 피호출자가 각각 다른 스레드더라도 완료될 때까지 기다리고, 이를 `블록 호출(blocking call)` 이라 한다.
* 비동기 API는 메서드가 즉시 반환되며, 호출 스레드와 다른 작업 스레드에 작업을 할당한다.
따라서 호출 스레드는 기다리지 않고 다음 작업을 하며, 이를 `비블록 호출(non-blocking call)`이라 한다.
작업 스레드의 계산 결과는 콜백 메서드를 호출해 전달하거나, 호출자가 '계산 결과를 기다리는 메서드'를 호출해
전달된다.

## Future
### 개념
> Future 인터페이스는 미래 어느 시점에 결과를 얻는 모델에 활용하는 인터페이스 (자바 5 부터 지원)

* Future 는 계산이 끝났을 때 결과에 접근할 수 있는 레퍼런스를 제공
* Future 는 저수준의 스레드에 비해 직관적으로 이해하기 쉬움

### 사용법
* FutureOldVersionExample 예제 소스 확인
* Future 를 이용하려면 작업을 Callable 객체 내부에 감싼 다음에 ExecutorService 에 제출해야 한다.

## CompletableFuture
### 나온 배경
Future 인터페이스를 위의 사용법대로 이용하기엔, 불편한 점이 많다.
그래서 자바8에서 아래와 같은 기능을 선언형으로 사용할 수 있는 `CompletableFuture 클래스`
(Future 구현체)를 제공한다. Stream 처럼 람다 표현식과 파이프라이닝을 활용할 수 있다. 
* 두 개의 비동기 계산 결과를 하나로 합친다.  
두 가지 계산 결과는 서로 독립적일 수도, 의존성을 가질 수도 있다.
* Future 집합이 실행하는 모든 태스크의 완료를 기다린다.
* Future 집합에서 가장 빨리 완료되는 태스크를 기다렸다가 결과를 얻는다. (예를 들면 다양한 방법으로 
같은 결과를 구하는 상황)
* 프로그램적으로 Future 를 완료시킨다. (비동기 동작에 수동으로 결과 제공)
* Future 완료 동작에 반응한다. (결과를 대기하며 블록되지 않고, 결과가 준비되었다는 알림을 받은 후 
Future 의 결과로 원하는 추가 동작 수행)

### 사용법
* Shop 예제 소스 확인

### 스레드 풀 크기 조절
자바 병렬 프로그래밍(브라이언 게츠)의 대략적인 CPU 활용 비율 계산 공식은 아래와 같다.
```
N_threads = N_cpu * U_cpu * (1 + W/C)

* N_cpu 는 Runtime.getRuntime().availableProcessors()가 반환하는 코어 수
* U_cpu 는 0과 1사이의 값을 갖는 CPU 활용 비율
* W/C는 대기시간과 계산시간의 비율
```
위 공식에서 작업의 성격에 따라 달라지는 부분이 W/C 이다.  
병렬 작업의 특성이 CPU 를 계산에 많이 쓰면 W/C 가 0에 수렴하고, CPU 가 대기하는데 많이 쓰면 W/C가 99에 수렴한다.
(수학적으론 무한대에 수렴이지만, C 가 최소 1이라 가정.)

Shop 예제를 보면, 병렬 작업의 특성이 상점의 응답을 기다리는 시간이 대부분(대략 99%라 생각) 이므로 W/C 는 99.
해당 장비가 N_cpu는 4이고, U_cpu 활용을 100% 하고 싶다면 1을 대입해준다.
그럼 최대 400개의 적정 스레드가 나온다.

예제에서 상점의 수가 T(<400)개 라면, T개 이상의 스레드는 의미가 없고, 또 스레드 수가 너무 많으면
서버가 크래시될 수 있으므로 하나의 Executor 에서 사용할 스레드 최대 개수는 100 이하로 설정하는 것이
바람직하다.

### 스트림 병렬화와 CompletableFuture 를 이용한 병렬화
병렬적으로 계산하는 작업은 자바 8에서 두 가지를 이용해 가능하다.
* 스트림 병렬화: stream() 을 parallelStream() 으로 변경
* CompletableFuture 이용: stream() 을 사용하는데, 스트림 파이프라인에서 CompletableFuture를 
이용해 계산하고, 다른 스트림 파이프라인에서 결과값을 합치기

여기서 CompletableFuture 이용 방식은 Executor 를 커스텀할 수 있다.
위의 스레드 풀 크기 조절 공식을 이용해, 해당 애플리케이션에 맞는 스레드 수를 계산한다면,
CompletableFuture 방식이 좀 더 성능을 최적화시킬 수 있다.

아래는 병렬 작업을 할 때 두 가지 병렬화 기법중 어떤 것을 사용할 것인지에 대한 참고용 기준이다.
위 공식의 W/C 비율을 보고 판단하면 된다.
* I/O가 포함되지 않은 계산 중심의 동작을 실행할 때는 스트림 인터페이스가 구현하기 간단하고 가장 효율적일 수 있다.
* 작업의 대부분이 I/O를 기다리는 작업이라면, CompletableFuture 에 커스텀 Executor 를 적정한 스레드 수로
사용하면 성능을 개선시킬 수 있다.  
특히 스트림의 게으른 특성 때문에 스트림에서 I/O를 실제로 언제 처리할지 예측이 어려운 문제도 있다.

## 클래스, 인터페이스 상세
### CompletableFuture
CompletableFuture\<T\> 는 Future\<T\> 와 CompletionStage\<T\> 인터페이스를 구현하고 있다.
```java
// @since 1.8
public class CompletableFuture<T> implements Future<T>, CompletionStage<T> {
    // ---------- static 메서드 ----------
    public static CompletableFuture<Void> allOf(CompletableFuture<?>... cfs);
}
```

### Future
Future 는 caller 스레드가 async 한 작업을 Executor 스레드에 위임하고, 결과값을 참조할 수 있는 인터페이스이다.
```java
// @since 1.5
public interface Future<V> {
    // task 를 취소하는 메소드
    // mayInterruptIfRunning 이 true 라면, 실행중인 상태일 때 인터럽트 가능
    // 이미 task 가 완료거나 다른 이유땜에 취소 실패하면 false
    boolean cancel(boolean mayInterruptIfRunning);
    
    // task 가 정상적으로 완료되기 전에 취소됐다면 true
    boolean isCancelled();
    
    // task 가 완료됐다면 true
    boolean isDone();
    
    // 결과 조회 메서드, 완료될 때까지 wait
    V get() throws InterruptedException, ExecutionException;
    
    // 결과 조회 메서드, 설정한 타임 아웃 시간까지 wait
    V get(long timeout, TimeUnit unit) 
        throws InterruptedException, ExecutionException, TimeoutException;
}
``` 

### CompletionStage
CompletionStage 는 `작업의 파이프라인을 구성`할 수 있게하는 인터페이스이다.
파이프라인에서 단계의 의존성 관계를 나타낼 수 있고, 각 단계에서 발생하는 에러를 
관리하고 전달할 수 있다.
```java
// @since 1.8
public interface CompletionStage<T> {
    
     
}
```

### Executor

### ExecutorService