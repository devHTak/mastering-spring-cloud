#### chap07. 고급 부하분산 및 서킷브레이커

- 마스터 스프링 클라우드 책에서는 서킷브레이커에 대해 리본이 구현되어 있지만 Resillience4j-circuitbreaker 를 구현하였습니다.
- 마이크로서비스 간에 커뮤니케이션을 고려할 때 관련된 시스템의 처리시간과 같은 문제가 있다.
  - 서비스 간의 여러 네트워크 구간에서 발생하는 지연 문제
    - 서비스의 느린 응답
    - 일시적인 서비스 사용 중단

##### Circuit Breaker

- 개요
  - 서킷 브레이커는 다량의 오류를 감지하면 서킷을 열어 새 호출을 받지 않는다.
  - 서킷 브레이커는 서킷이 열려 있을 때 빠른 실패 로직을 수행한다. 
    - 즉 이어지는 호출에서 시간 초과, 예외 발생 등 오류가 발생하지 않게, 폴백 메서드 호출을 리다디렉션을 한다. 
    - 폴백 메서드에서 다양한 비즈니스 로직을 적용하면 로컬 캐시의 데이터를 반환하거나 즉각적인 오류 메시지를 반환하는 등 최적화된 응답을 생성할 수 있다. 
    - 이로써 의존하는 서비스의 응답 중단 때문에 마이크로 서비스가 응답하지 못하게 되는 문제를 방지할 수 있는다.
  - 시간이 지나면 서킷 브레이커는 반열림 상태로 전환돼 새로운 호출을 허용하며, 이를 통해 문제를 일으킨 원인이 사라졌는지 확인한다. 
    - 서킷 브레이커는 새로운 오류를 감지하면서 서킷을 다시 열고 빠른 실패 로직을 다시 수행하며, 오류가 사라졌으면 서킷을 닫고 정상 작동 상태로 돌아간다.
    
- 상태
  - 일반적인 상태
    - CLOSED : 서킷브레이커가 닫혀 있는 상태로 서킷브레이커가 감싼 내부의 프로세스로 요청을 보내고 응답을 받을 수 있다.
    - OPEN : 서킷브레이커가 열려 있는 상태로 서킷브레이커는 내부의 프로세스로 요청을 보내지 않는다.
    - HALF_OPEN : 서킷브레이커가 열려 있는 상태지만 내부의 프로세스로 요청을 보내고 실패율을 측정해 상태를 CLOSED 혹은 OPEN 상태로 변경한다.
  - 요청의 성공 실패 metric을 수집하고 그에 따라 상태가 변한다.(상태변이, state transit)
    - CLOSED는 OPEN으로, OPEN은 HALF_OPEN으로, HALF_OPEN은 metric의 실패율에 따라 CLOSED, OPEN 두 상태로 선택하여 상태변이를 한다.
  - 특수상태는 강제 상태 변이를 하지 않으면 발생할 수 없다.
    - DISABLED : 서킷브레이커를 강제로 CLOSED한 상태이다. 하지만 실패율에 따른 상태변화도 없고 후술할 이벤트 발행도 발생하지 않는다.
    - FORCED_OPEN : 서킷브레이커를 강제로 OPEN한 상태이다. DISABLED와 동일하게 상태변화도 없고 이벤트 발행도 하지 않는다.
    - METRICS_ONLY : 서킷브레이커를 강제로 CLOSED한 상태이다. DISABLED과 동일하게 상태변화는 없지만 이벤트 발행을 하고 내부 프로세스의 metric 수집한다.

- 타입
  - 서킷브레이커가 요청에 대한 metric을 수집하는 데 해당 수집한 결과는 Sliding Window로 원형배열에 수집하고 방식은 두가지로 나뉘게 된다.
    - sliding window algorithm: 배열이나 리스트의 요소의 일정 범위의 값을 비교할때 사용하는 알고리즘.
  - Count-based sliding window (카운트 기반)
    - 카운트 기반은 n개의 원형배열로 구현된다. 원형 배열의 크기를 10으로 하면 10개의 측정 값을 유지하고 새로운 측정 값이 들어올때마다 가장 오래된 측정 값을 제거한 뒤 총 집계를 갱신한다.
  - Time-based sliding window (시간 기반)
    - 시간 기반도 동일하게 n개의 원형배열로 구현된다. 
    - n은 시간(초, epoch second)단위로 10으로 설정하면 1초씩 10개의 부분 집계 버킷가 생긴다. 동일하게 시간이 흐르면 가장 오래된 부분 집계 버킷이 제거되고 총 집계가 갱신된다.

- Resillience4j는 런타임에 다양한 방법으로 서킷 브레이커의 정보 공개
  - 서킷 브레이커의 현재 상태를 마이크로 서비스 액추에이터 상태 점검 엔드 포인트(/actuator/health)를 사용해 모니터링할 수 있다.
  - 서킷 브레이커는 상태 전이 등의 이벤트 액추에이터 엔드 포인트(/actuator/citcuitbreakerevents)를 게시한다.
  - 서킷 브레이커 스프링 부트의 매트릭스 시스템과 통합돼 있으며 이를 이용해 프로테우스와 같은 모니터링 도구에 메트릭을 게시할 수 있다.

- Resillience4j Circuitbreaker 설정 
  (추가적인 내용은 공식문서에서 확인 가능: https://resilience4j.readme.io/docs/circuitbreaker#create-and-configure-a-circuitbreaker)
  - name: 서킷브레이커의 이름
  - failureRateThreshold: 실패 비율의 임계치
  - slowCallRateThreshold: 느린 호출의 임계치
  - slowCallDurationThreshold: 느린 호출로 간주할 시간 값
  - slidingWindowType: 서킷브레이커의 타입을 지정한다. TIME_BASED, COUNT_BASED 중 택 1
  - slidingWindowSize: 시간은 단위 초, 카운트는 단위 요청 수
  - minimumNumberOfCalls: 총 집계가 유효해 지는 최소한의 요청 수. 이 값이 1000이라면 999번 실패해도 서킷브레이커는 상태변이가 일어나지 않는다.
  - waitDurationInOpenState: OPEN에서 HALF_OPEN으로 상태변이가 실행되는 최소 대기 시간
  - permittedNumberOfCallsInHalfOpenState: HALF_OPEN 상태에서 총 집계가 유효해지는 최소한의 요청 수. COUNT_BASED로 slidingWindowType이 고정되어 있다.
  - automaticTransition: true라면 waitDurationInOpenState로 지정한 시간이 지났을 때 새로운 요청이 들어오지 않아도 자동으로 HALF_OPEN으로 상태변이가 발생한다.
  - ignoreExceptions: 해당 값에 기재한 exception은 모두 실패로 집계하지 않는다.
  - recordExceptions: 해당 값에 기재한 exception은 모두 실패로 집계한다.

- 재시도 메커니즘
  - 일시적인 네티워크 결함과 같음 무작위로 드물게 발생하는 오류에 매우 유용하다. 
  - 재시도 메커니즘은 설정된 대기 시간을 사이에 두고, 실패한 요청에 여러번 다시 시도하는 것이다. 
  - 재시도 메커니즘을 사용하기 위한 주요 요건 중 하나는 재시도 대상 서비스의 멱등성이 있어야 한다는 것이다. 
    - 만약 재시도 메커니즘에 의해 2개의 주문이 생성되는 일이 발생하지 않아야 하기 때문이다.
  - Resilience4j는 서킷 브레이커와 같은 방식으로 재시도 관련된 이벤트 및 메트릭 정보를 공개하지만 상태 정보는 전혀 공개하지 않으며, 재시도 이벤트에 관한 정보는 Actuator 엔드 포인트에서 얻을 수 있다.
  - 설정
    - maxRetryAttempts: 첫번째 호출을 포함한 총 재시도 횟수
    - waitDuration: 재시도를 다시 수행하기 전의 대기 시간
    - retryExceptions: 재시도를 트러거하는 예외 목록

#### 구현

- Resllience4j 설정
  - YAML 파일로 설정하기
    ```yml
    resilience4j:
      circuitbreaker:
        user-service:
          board-service:
            ringBufferSizeInClosedState: 30
            ringBufferSizeInHalfOpenState: 30
            waitDurationInOpenState: 5000ms
            failureRateThreshold: 20
            registerHealthIndicator: false
    ```
    - ringBufferSizeInClosedState: Circuit이 닫혀있을 때(정상) Ring Buffer 사이즈, 기본값은 100
    - ringBufferSizeInHalfOpenState: half-open 상태일 때 RingBuffer 사이즈 기본값은 10
    - waitDurationInOpenState: half closed전에 circuitBreaker가 open 되기 전에 기다리는 기간
    - failureRateThreshold: Circuit 열지 말지 결정하는 실패 threshold 퍼센테이지

    - Circuit Breaker 생성
      ```java
      @Configuration
      public class CircuitBreakerConfig {

          private final String CIRCUIT_NAME = "board-service";

          @Bean
          public CircuitBreaker circuitBreaker(CircuitBreakerRegistry registry) {
              return registry.circuitBreaker(CIRCUIT_NAME);
          }
      }
      ```
  - Config 파일로 설정하기
    ```java
    @Configuration
    public class CircuitBreakerConfig {

        @Bean
        public Customizer<Resilience4JCircuitBreakerFactory> circuitBreakerConfig() {
            return resilience4JCircuitBreakerFactory -> {
              resilience4JCircuitBreakerFactory.configureDefault(id -> {
                  return new Resilience4JConfigBuilder(id)
                          .timeLimiterConfig(timeLimiterConfig())
                          .circuitBreakerConfig(customizerCircuitBreakerConfig())
                          .build();
              });
            };
        }

        private TimeLimiterConfig timeLimiterConfig() {
            return TimeLimiterConfig.custom()
                    // timeLimiter는 future supplier의 time limit을 정하는 API 기본 1초
                    .timeoutDuration(Duration.ofSeconds(4))
                    .build();
        }

        private io.github.resilience4j.circuitbreaker.CircuitBreakerConfig customizerCircuitBreakerConfig() {
            return io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                    // circuitBreaker 를 열지 결정하는 failure rate, 기본 50
                    .failureRateThreshold(4f)
                    // circuitBreaker를 open한 상태를 유지하는 지속 시간 이 기간 이후 half-open 상태 기본 60sec
                    .waitDurationInOpenState(Duration.ofMillis(1000))
                    // circuitBreaker 닫힐 때 통화 결과 기록하는 데 사용되는 슬라이딩 창의 유형 구성(카운트 또는 시간 기반)
                    .slidingWindowType(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                    // circuitBreaker 닫힐 때 호출 결과를 기록하는 데 사용되는 슬라이딩 창의 크기 구성
                    .slidingWindowSize(2)
                    .build();
        }
    }
    ```

- api 호출 부분에서 Circuit breaker 사용
  - 기존 feign client를 통해 불러오는 코드
    ```java
    List<PostResponse> posts = boardClient.getPostsByUserId(userId).getBody();
    ```
  - Circuit breaker 사용
    ```java
    private final Resilience4JCircuitBreakerFactory circuitBreakerFactory; // bean 에 등록한 CircuitBreakerFactory를 받아옴
    
    // ... 생략
    // METHOD
    Resilience4JCircuitBreaker circuitBreaker = circuitBreakerFactory.create("board-service");
    List<PostResponse> posts = circuitBreaker.run(
            () -> boardClient.getPostsByUserId(userId).getBody(),
            (throwable) -> new ArrayList<>()
    );
    ```
    - circuirBreker.run(실행 소스, 오류 발생 시);

#### 참조

- https://bottom-to-top.tistory.com/57
- https://cheese10yun.github.io/resilience4j-basic/
