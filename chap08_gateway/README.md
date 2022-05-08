#### API Gateway

- 넷플릭스는 zuul을 통해 인증, 부하, 카나리 테스팅, 동적 라우팅 그리고 액티브/액티브 멀티 리전 트래픽 관리를 설계했다.
- Spring Cloud Gateway라는 새로운 프로젝트 진행
  - Spring Framework 5, Webflux(Project Reactor), Spring Boot 2.0 버전 기반

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
    |Cookie Route|쿠키 이름과 정규식을 입력받아 HTTP 요청의 헤더에서 쿠키를 찾고 그 값ㅇ르 제공된 표현식과 비교|Cookie=SessionId, abc.|
    |Header Route|헤더 이름과 정규식을 입력값으로 받아 HTTP 요청의 헤더에서 특정 헤더를 찾고 그 값을 제공된 표현식과 비교|Header=X-Request-Id, \d+|
    |Host Route|. 구분자를 사용하는 호스트 이름 ANT 스타일 패턴을 입력받아 Host 헤더와 매칭|Host=\*\*.example.org|
    |Method Route|HTTP 메서드를 입력값으로 받아 비교|Method=GET|
    |Path Route|요청 컨텍스트 경로의 패턴을 입력값으로 받아 비교|Path=/account/{id}|
    |Query Route|두 개의 입력값 - 요청된 입력값과 선택적 regex를 받아 질의 입력값과 비교|Query=accountId, 1.|
    |RemoteAddr Route|IP 주소 목록을 192.168.0.1/16과 같은 CIDR 표현식으로 받아 요청의 원격 주소와 비교|RemoteAddr=192.168.0.1/16|
    
  - spring.cloud.gateway.routes 속성 아래 정의된 각 라우트에 filters 속성 구성
