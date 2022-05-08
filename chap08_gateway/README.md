#### API Gateway

- MSA 환경에서 API Gateway 필요 이유
  - 유입되는 모든 요청/응답이 통하기 때문에 인증/보안을 적용하기 좋다.
  - URI에 따라 서비스 엔드포인트를 다르게 가져가는 동적 라우팅이 가능해진다. 
    - 예를 들면 도메인 변경없이 레거시 시스템을 신규 시스템으로 점진적으로 교체해 나가는 작업을 쉽게 진행할 수 있다.
  - 모든 트래픽이 통하기 때문에 모니터링 시스템 구성이 단순해진다.
  - 동적 라우팅이 가능하므로 신규 스팩을 서비스 일부에만 적용하거나 트래픽을 점진적으로 늘려나가는 테스트를 수행하기에 수월해진다.

#### Spring Cloud Gateway

- 기본 개념
  - Route(라우트)
    - 게이트웨이 기본 요소
    - 라우트, 목적지 URI, 조건자 목록과 필터의 목록을 식별하기 위한 고유ID로 구성
    - 라우트는 모든 조건자가 충족될 때 매칭된다.
  - Predicates(조건자)
    - 각 요청을 처리하기 전에 실행되는 로직
    - 헤더와 입력값 등 다양한 HTTP 요청이 정의된 기준에 맞는지를 찾는다.
    - java.util.function.Predicate\<T> Java 8 interace 기반
    - 입력타입은 스프링의 org.springframework.web.server.ServerWebExchange 기반
  - Filters(필터)
    - HTTP Request, HTTP Response를 수정할 수 있게 한다.
    - 다운스트림 요청을 보내기전이나 후에 수정할 수 있다. 라우트 필터는 특정 라우트에 한정된다.
    - org.springframework.web.server.GatewayFilter 구현

- 내장된 조건자와 필터
  - spring.cloud.gateway.routes 속성 아래 정의된 각 라우트에 predicates 속성 구성
    |이름|설명|예제|
    |---|---|---|
    |After Route|Date-time 입력값을 받아서 그 이후에 발생한 요청 매칭|After=2022-05-08T..|
    |Before Route|Date-time 입력값을 받아서 그 전에 발생한 요청 매칭|Before=2022-05-08T..|
    |Between Route|두 개의 date-time을 입력받아 두 날짜 사이에 발생한 요청을 매칭|Between=2022-05-05T..., 2022-05-08T...|
    |Cookie Route|쿠키 이름과 정규식을 입력받아 HTTP 요청의 헤더에서 쿠키를 찾고 그 값을 제공된 표현식과 비교|Cookie=SessionId, abc.|
    |Header Route|헤더 이름과 정규식을 입력값으로 받아 HTTP 요청의 헤더에서 특정 헤더를 찾고 그 값을 제공된 표현식과 비교|Header=X-Request-Id, \d+|
    |Host Route|. 구분자를 사용하는 호스트 이름 ANT 스타일 패턴을 입력받아 Host 헤더와 매칭|Host=\*\*.example.org|
    |Method Route|HTTP 메서드를 입력값으로 받아 비교|Method=GET|
    |Path Route|요청 컨텍스트 경로의 패턴을 입력값으로 받아 비교|Path=/account/{id}|
    |Query Route|두 개의 입력값 - 요청된 입력값과 선택적 regex를 받아 질의 입력값과 비교|Query=accountId, 1.|
    |RemoteAddr Route|IP 주소 목록을 192.168.0.1/16과 같은 CIDR 표현식으로 받아 요청의 원격 주소와 비교|RemoteAddr=192.168.0.1/16|
    
  - spring.cloud.gateway.routes 속성 아래 정의된 각 라우트에 filters 속성 구성
    |이름|설명|예제|
    |---|---|---|
    |Add RequestHeader|입력값에 제공된 이름과 값으로 HTTP 요청에 헤더 추가|AddRequestHeader=X-Response-ID, 123|
    |AddRequestParameter|입력값에 제공된 이름과 값으로 HTTP 요청에 질의 추가|AddRequestParameter=id, 123|
    |AddResponseHeader|입력값에 제공된 이름과 값으로 HTTP 응답에 헤더 추가|AddResponseHeader=X-Response-ID, 123|
    |Hystrix|히스트릭스 명령 이름의 입력값을 받는다|Hystrix=account-service|
    |PrefixPath|입력값에 정의된 접두사를 HTTP 요청 경로에 추가|PrefixPath=/api|
    |RequestRateLimiter|제공된 세 개의 입력값에 단일 사용자의 요청 처리 수 제한, 세 개의 입력값은 초당 최대 요청 수, 초당 최대 요청 처리 용량, 사용자 키를 반환하는 빈|RequestRateLimiter=10, 20, #{@userKeyResolver}|
    |RedirectTo|HTTP 상태 코드와 리다이렉트 경로를 입력값으로 받아 리다이렉트를 수행하기 위해 Location HTTP 헤더에 추가|RedirectTo=302, http://localhost:8092|
    |RemoveNonProxyHeaders|전달된요청에서 Keep-Alive, Proxy-Authentication 또는 Proxy-Authorization 등과 같은 몇가지 hop-by-hop 헤더 제거|-|
    |RemoveRequestHeader|헤더의 이름을 입력값으로 받아 HTTP 요청에서 그것을 제거|RemoveRequestHeader=X-Request-Foo|
    |RemoveResponseHeader|헤더의 이름을 입력값으로 받아 HTTP 응답에서 그것을 제거|RemoveResponseHeader=X-Response-ID|
    |RewritePath|Regex 입력과 대체값을 받아 요청 경로 재작성|RewritePath=/account(?<path>/*), /$\{path}|
    |SecureHeaders|몇가지 보안 헤더를 응답에 추가|-|
    |SetPath|경로 template 입력값을 사용하는 단일 입력값을 받아 요청 경로 변경|SetPath=/{segment}|
    |SetResponseHeader|이름과 값을 입력받아 HTTP 응답에 헤더를 추가|SetResponseHeader=X-Response-ID, 123|
    |SetStatus|유효한 HTTP 상태 입력값을 받아 응답에 설정|SetStatus=401|

#### Spring Cloud Gateway 설정

- filter 생성
  ```java
  @Component
  @Slf4j
  @Getter
  public class GlobalLoggingFilter extends AbstractGatewayFilterFactory<GlobalLoggingFilter.Config> {

      @Override
      public GatewayFilter apply(Config config) {
          return (exchange, chain) -> {
              if(config.isPreLogger())
                  log.info("Gateway Pre Logger: requestId - {}", exchange.getRequest().getId());

              return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                  if(config.isPostLogger())
                      log.info("Gateway Post Logger: response status - {}", exchange.getResponse().getStatusCode());
              }));
          };
      }

      @Getter @Setter
      @NoArgsConstructor @AllArgsConstructor
      public static class Config {
          private boolean preLogger;
          private boolean postLogger;
      }
  }
  ```
  - AbstractGatewayFilterFactory 를 상속받아 public GatewayFilter apply(Config config) 오버라이딩
    - Config 클래스를 생성하여 내부에서 사용할 수 있다.

- Route 및 Predicates, Filters 정의
  - YAML로 정의
    ```yml
    spring:
      cloud:
        gateway:
          default-filters:
            - name: GlobalFilter
              args:
                baseMessage: Spring Cloud Gateway GlobalFilter
                preLogger: true
                postLogger: true
          routes:
            - id: product-service
              uri: lb://product-service
              predicates:
                - Path=/product-service/**
                - RewritePath=/product-service/(?<segment>/*), /$\\{segment}
              filters:
                - name: ProductFilter
                  args:
                    baseMessage: Spring Cloud Gateway UserFilter
                    preLogger: true
                    postLogger: true
    ```
    - global filter, route 별 filter를 정해줄 수 있다.
  
  - JAVA로 정의
    ```yml
    @Configuration
    public class GatewayRouter {

        @Bean
        public RouteLocator route(RouteLocatorBuilder builder, GlobalLoggingFilter globalLoggingFilter) {
            return builder.routes()
                    .route(r -> {
                        return r.path("/product-service/**")
                                .filters(f-> f.filter(globalLoggingFilter.apply(new GlobalLoggingFilter.Config(true, true)))
                                        .rewritePath("/product-service/(?<segment>/*)", "/$\\{segment}" ))
                                .uri("lb://PRODUCT-SERVICE");
                    })
                    .build();
        }
    }
    ```
    - Filter를 파라미터로 받아 사용할 수 있다.
    - path, filter, rewritePath, uri(유레카 연동) 등 설정

- 서비스 디스커버리와 연동
  - uri에 IP가 아닌 lb://{service-name} 으로 사용하면 서비스 디스커버리와 연동하여 IP, Port 정보를 가져올 수 있다.
  - service-name은 서비스 디스커버리에 등록한 이름으로 사용하면 된다.
