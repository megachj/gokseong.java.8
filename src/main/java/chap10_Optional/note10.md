## Optional\<T\>
> 자료형 T `데이터`가 `있을 수도 있고, 없을 수도 있는 것`을 나타내는 클래스, `선택형` 클래스  

* null 을 대신해, 좀 더 깔끔한 코드를 작성할 수 있게 도와준다.
* Optional 을 사용하면 데이터가 비어있을 수도 있음을 `명시적`으로 나타낸다. 

```java
// Optional 클래스 정의
public final class Optional<T> {
    // ...
    
    /**
     * If non-null, the value; if null, indicates no value is present
     */
    private final T value;
    
    // ...
}
```
* final 클래스이므로 상속받을 수 없다.
* Serializable 인터페이스 구현이 되어있지 않다.  
도메인 모델에 Optional을 사용한다면 직렬화 모델을 사용하는 도구, 프레임워크에 문제 발생  
ex) hazelcast: 데이터를 저장하려면 해당 클래스가 직렬화되어야 한다.
    ```java
      // 직렬화 모델이 필요한 경우
      public class Person {
          private Car car; // 필드에 Optional 을 빼고
          
          // Optional 객체를 반환하는 메서를 이용해도 된다.
          public Optional<Car> getCarAsOptional() {
              return Optional.ofNullable(car);
          }
      }
    ```
    
### Optional 클래스 메서드

| 메서드 | 설명 |
| --- | --- |
| empty | 빈 Optional 인스턴스 반환 |
| filter | 값이 있으며 Predicate와 일치하면 Optional 반환<br>값이 없거나 Predicate와 다르면 빈 Optional 반환 |
| map | 값이 존재하면 제공된 매핑 함수를 적용함 |
| flatMap | 값이 존재하면 값을 포함한 Optional을 반환, 값이 없으면 빈 Optional을 반환 |
| get | 값이 있으면 데이터를 반환, 없으면 NoSuchElementException 발생 |
| ifPresent | 값이 있으면 Consumer 를 실행 |
| isPresent | 값 있으면 true, 없으면 false |
| of | 값 있으면 Optional 반환, 값이 null 이면 NPE 발생 |
| ofNullable | 값 있으면 Optional 반환, 없으면 빈 Optional 반환 |
| orElse | 값 있으면 반환, 없으면 디폴트값 반환 |
| orElseGet | 값 있으면 반환, 없으면 Supplier 에서 제공하는 값 반환 |
| orElseThrow | 값 있으면 반환, 없으면 Supplier 에서 생성한 예외 발생 |

### 실용 예제
#### 잠재적으로 null이 될 수 있는 대상을 Optional 로 감싸기
어떠한 자바 API에서는 값이 없거나 문제가 생겼을 경우 null을 반환한다.
```java
// 예제: Map의 get 메서드는 해당 키가 없으면 null을 반환
Map<String, Object> map;
Object value = map.get("key"); // null 반환될 가능성이 있다.

Optional<Object> value = Optional.ofNullable(map.get("key"));
```

#### 예외와 Optional
어떠한 자바 API에서는 값을 제공할 수 없을 때 null 대신 예외를 발생시킨다.
```java
// 예제: Integer.parseInt(String) 은 문자열을 정수로 바꾸지 못하면 예외를 발생시킨다.

// 유틸리티 클래스, 메서드를 만들면 매번 try, catch 문을 작성하는 것을 피할 수 있다.
public static Optional<Integer> stringToInt(String s) {
    try {
        return Optional.of(Integer.parseInt(s));
    } catch (NumberFormatException e) {
        return Optional.empty();
    }
}
```

### 기본형 Optional은 쓰면 안되는 이유
OptionalInt, OptionalLong, OptionalDouble 은 아래의 안 좋은 점이 있다.
* Optional의 최대 요소 수는 1개 이므로, 스트림에서 처럼 성능 향상이 없다.
* map, flatMap, filter 등을 지원하지 않는다.
* 스트림과 마찬가지로 일반 Optional과 혼용할 수 없다.