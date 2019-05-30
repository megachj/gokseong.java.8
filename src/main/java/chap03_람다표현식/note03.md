## 함수형 인터페이스와 람다
> 함수형 인터페이스란, `정확히 하나의 추상 메서드`만 가진 인터페이스를 말한다.  
인터페이스이므로, 메서드의 파라미터 형식, 리턴 형식만 정의되었고, 동작은 정의되어 있지 않다.

> 람다 표현식은 `함수형 인터페이스의 인스턴스`를 나타낸다.  
즉, 함수형 인터페이스의 추상 메서드의 동작을 정의한 것이 람다이다.
* 실제 파라미터는 해당 인스턴스의 메서드를 실행시킬 때, 넘겨주는 것이다.  
람다 표현식의 파라미터는 형식만 나타내므로, 실제로 넘기는 파라미터 값이 아님에 주의하자.

### 문법
```
(parameter) -> expression
(parameter) -> { statements; }
```

### 자바 8 대표 함수형 인터페이스 종류
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

### 기본형 특화
제네릭에는 참조형만 사용이 가능해서, int, long, double 등 기본형을 사용하기 위해선
박싱된 Int, Long, Double 등으로 사용해야 한다. 문제는 박싱(기본->참조), 언박싱(참조->기본)으로 변환 과정에 비용이 소모된다.
`기본형 특화`는 `기본형 자체`로 함수형 인터페이스를 사용할 수 있게 한다.
* 스트림 처리할 때, 기본형 특화를 사용한 경우 속도 차이가 꽤 많이 나는 것을 확인할 수 있다.

### 활용: 실행 어라운드 패턴
예제를 보자.
1. 함수형 인터페이스가 정의
2. 해당 함수형 인터페이스를 구현한 객체를 파라미터로 받는 메서드(processFile) 구현
    * 이 메서드에서 파라미터로 받은 함수형 인터페이스 객체의 메서드를 실행
3. 메인 메서드에서 위 메서드 호출 시 람다로 함수형 인터페이스 인스턴스를 전달
    * 인터페이스의 동작이 정의되어 있음

### 자유 변수
람다 표현식에서 `자유 변수`(파라미터가 아닌 외부에 정의된 변수)를 활용할 수 있다.
* 인스턴스 변수, 정적 변수는 자유롭게 사용할 수 있다.
* 지역 변수는 `final`이거나, 할당문이 딱 한 번이어야 사용할 수 있다.

**자유 변수의 제약이 있는 이유?**  
클래스 변수는 클래스 영역(최상단 메모리), 인스턴스 변수는 힙에 저장, 지역 변수는 스택에 저장된다.  
만약 람다가 지역 변수를 직접 가리킨다고 가정하자.  
람다가 실행되는 스레드는 지역 변수가 실행되는 스레드와 다를 수 있다.
(parallelStream 사용시 해당 람다를 여러 스레드에서 실행함을 알 수 있다.)  
이 경우 지역 변수 스레드가 사라져버리면, 없는 변수를 접근하게 될 수 있다.  
그래서 직접 가리키지 않고, `자유 지역 변수`의 `복사본을 제공`하게 되고, `final 제약`이 필요하게 된다.

## 메서드 레퍼런스
> 메서드 레퍼런스란, 이미 정의된 메서드에 대한 동작을 참조하는 것을 말한다.
* 람다 표현식도 메서드의 동작을 정의하는 것이므로, 동일한 동작의 메서드가 존재한다면 모두 치환할 수 있다.

### 메서드 레퍼런스 만드는 방법
**1. static 메서드 레퍼런스**
```
// 람다
(args) -> ClassName.staticMethod(args);

// 메서드 레퍼런스
ClassName::ClassName.staticMethod(args);
```

**2. 인스턴스 메서드 레퍼런스**  
```
// 람다
// 첫 번째 파라미터 arg0의 타입은 ClassName 이어야 함
(arg0, restArgs) -> arg0.instanceMethod(restArgs);

// 메서드 레퍼런스
ClassName::instanceMethod(restArgs);
```

**3. 기존 객체의 메서드 레퍼런스**
```
// 람다
(args) -> someObj.instanceMethod(args);

// 메서드 레퍼런스
someObj::instanceMethod(args);
```

### 생성자 레퍼런스
`ClassName::new`처럼 클래스명과 new 키워드를 이용해 생성자 레퍼런스를 만들 수 있다.

Main03_constructorReference 예제를 보자.

## 람다식을 조합할 수 있는 메서드
### Comparator 조합
`reversed, thenComparing`을 제공한다.
```
// 정렬
inventory.sort(Comparator.comparing(Apple::getWeight));

// 역정렬
inventory.sort(Comparator.comparing(Apple::getWeight).reversed());

// Comparator 연결
inventory.sort(Comparator.comparing(Apple::getWeight)
                .reversed()
                .thenComparing(Apple::getCountry));
```

### Predicate 조합
`negate, and, or`을 제공한다.
```
Predicate<Apple> redApple = (a) -> a.getColor().equals("Red");

// negate
Predicate<Apple> notRedApple = redApple.negate();

// and, or
Predicate<Apple> redAndHeavyAppleOrGreen = 
    redApple.and(a -> a.getWeight() > 150)
            .or(a -> "green".equals(a.getColor()));
```
* 조합 시 왼쪽부터 차례대로이다. 즉, a.or(b).and(c) == (a || b) && C 이다.

### Function 조합
`andThen, compose`을 제공한다.
```
Function<Integer, Integer> f = x -> x + 1;
Function<Integer, Integer> g = x -> x * 2;
Function<Integer, Integer> h = f.andThen(g); // g(f(x))
Function<Integer, Integer> i = f.compose(g); // f(g(x))
```