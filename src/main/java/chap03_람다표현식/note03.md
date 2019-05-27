## 람다
> 람다 표현식은 `함수형 인터페이스의 인스턴스`를 나타낸다.

함수형 인터페이스란, `정확히 하나의 추상 메서드`만 가진 인터페이스를 말한다.
* 하나의 메서드만 가진 인터페이스이므로 파라미터 형식, 리턴 형식에 맞는 동작을 적으면 인스턴스와 같다.
* 실제 파라미터는 해당 인스턴스의 메서드를 실행시킬 때, 넘겨주는 것이다.  
람다 표현식의 파라미터는 형식만 나타내므로, 실제로 넘기는 파라미터 값이 아님에 주의하자.

### 문법
```
(parameter) -> expression
(parameter) -> { statements; }
```

### 활용: 실행 어라운드 패턴
예제를 보자.

## 함수형 인터페이스
자바 8의 대표 함수형 인터페이스

| 인터페이스 | 디스크립터 | 기본형 특화 |
| --- | --- | --- |
| Predicate\<T\> | T -> boolean | Int, Long, Double |
| Consumer\<T\> | T -> void | Int, Long, Double |
| Function\<T, R\> | T -> R | Int, Long, Double |
| Supplier\<T\> | () -> T | Boolean, Int, Long, Double |
| UnaryOperator\<T\> | T -> T | Int, Long, Double |
| BinaryOperator\<T\> | (T, T) -> T | Int, Long, Double |
| BiPredicate\<L, R\> | (L, R) -> boolean | |
| BiConsumer\<T, U\> | (T, U) -> void | Int, Long, Double |
| BiFunction\<T, U, R\> | (T, U) -> R | Int, Long, Double |