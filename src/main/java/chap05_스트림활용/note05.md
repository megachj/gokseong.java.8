## 스트림 연산
여러 스트림 관련 메서드를 적절히 사용하여 데이터를 처리한다.
* 필터링, 슬라이싱: filter, distinct, limit, skip
* 매핑: map, flatMap
* 검색, 매칭: ~Match, Optional, find~
* 리듀싱: reduce

## 스트림 생성
### 값으로 생성
```java
Stream<String> stram = Stream.of("Java 8", "Lambdas", "In ", "Action");
Stream<String> emptyStream = Stream.empty();
```

### 배열로 생성
```java
int[] numbers = {2, 3, 5, 7, 11, 13};
IntStream = Arrays.stream(numbers);
```

### 파일로 생성
자바의 NIO API도 스트림 API를 활용할 수 있다.
* 예를 들어 Files.lines는 주어진 파일의 행 스트림을 문자열로 변환
```java
// 예) 파일에서 고유한 단어 수를 찾기
long uniqueWords = 0;
try(Stream<String> lines = 
    Files.lines(Paths.get("data.txt"), Charset.devaultCahrset())) {
    
    uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" ")))
                        .distinct()
                        .count();

} catch(IOException e){
    ...
}
```

### 함수로 무한 스트림 생성
스트림 API는 Stream.iterate, Stream.generate를 제공하고, 이를 이용해 스트림을 생성할 수 있다. 두 연산을 이용해 무한 스트림을 생성할 수 있다.
```java
// iterate
Stream.iterate(0, n -> n + 2)
        .limit(10)
        .forEach(System.out::println);

// 예) iterate 피보나치수열 집합
Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1], t[0]+t[1]})
        .limit(20)
        .forEach(t -> System.out.println(...));


// generate
Stream.generate(Math::random)
        .limit(5)
        .forEach(System.out::println);

// 예) generate 피보나치수열 집합
IntSupplier fib = new IntSupplier(){
    private int previous = 0;
    private int current = 1;
    
    public int getAsInt(){
        int oldPrevious = this.previous;
        int nextValue = this.previous + this.current;
        this.previous = this.current;
        this.current = nextValue;
        return oldPrevious;
    }
};
IntStream.generate(fib).limit(10).forEach(System.out::println);
```
* iterate: 초깃값과 람다를 인수로 받아 새로운 값을 생산한다.
    * 예제에서는 UnaryOperator\<T>를 사용했다.
* generate: Supplier\<T>를 인수로 받아 새로운 값을 생산한다.
    * 예제의 피보나치수열 집합처럼, 가변 상태 객체를 사용할 땐 주의해야 한다.
    * 스트림을 `병렬`로 처리할때 올바른 결과를 얻으려면 `불변 상태 기법`을 `유지`해야 한다.