#### chap10. 추가 컨피규레이션 및 디스커버리 기능

- Neflix eureka는 스프링 클라우드에서 서비스 디스커버리로, 스프링 컨피그프로젝트로 분산 컨피규레이션을 전담
- 통합하여 제공하는 몇가지 솔루션이 존재하는데, 스프링 클라우드에서 모두 지원한다.
  - 컨설: 동적이고 분산된 인프라에서 애플리케이션을 연결하고 구성하기 위해 설계된 고가용성과 분산 확녕을 지원하는 솔루션. 다소 복잡하지만 주요 기능은 모든 인프라에서 서비스를 발견하고 구성하는 것
  - 주키퍼: 자바로 개발된 분산된 계층의 키/값 저장소로서 분산 환경에서 컨피규레이션 정보와 네이밍, 분산 동기화를 유지하기 위해 설계됐다. 
  
#### 컨설 사용하기

- 컨설 에이전트 실행
  ```
  $ docker run -d --name consul -p 8500:8500 consul
  ```
  - consul 메인 인터페이스는 REST API를 제공한다. 
    - /V1/ 을 접두사로 사용
    - API를 직접 사용할 필요는 없고, 자바 라이브러리를 사용하면 된다(consul-api)
  - 에이전트, 이벤트, KV 저장소의 세가지 다른 컨설 기능 사용 가능
    - /agent, /event, /kv 의 종단점 그룹 제공
    
- 클라이언트 통합하기
  - dependency
    ```
    // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-consul-all
    implementation 'org.springframework.cloud:spring-cloud-starter-consul-all:2.2.8.RELEASE'
    ```
  - 설정
    ```yml
    spring:
      cloud:
        consul:
          host: 192.168.99.100
          port: 18500
    ```
    
- 서비스 디스커버리
  - @EnableDiscoveryClient
  - 설정
    - isntanceId 등록 방법 변경
      - spring.cloud.consul.discovery.instanceId: ${spring.application.name}:${vcap.application.instance_id:${spring.application.instance_id:${random.value}}}
  - 서비스 실행 검사
    - UI 컨설 대시보드
    - REST API 호출
      - GET /v1/agent/services: 에이전트에 등록된 서비스의 목록을 가져올 수 있다.
  - 서비스 상태 검사
    - 컨설은 /health 종단점을 호출해 모든 등록된 인스턴스의 서비스 상태를 점검
    - 추가 설정
      - 서비스 상태 검사 종단점 경로 재정의: spring.cloud.consul.discovery.healthCheckPath
      - 상태갱신주기 변경: spring.cloud.consul.discovery.healthCheckInterval
  - 존(zone)
    - 존 메커니즘은 호스트가 다른 곳에 위치할 때 되도록 같은 존에 등록된 인스턴스 사이에 커뮤니케이션 하도록 하는 데 유용
    - 애플리케이션의 기본 존은 spring.cloud.consul.discovery.instanceZone 속성을 사용해 구성할 수 있다.
    - 이것은 전달된 값을 사용해 spring.cloud.consul.discovery.defaultZoneMetadataName 속성에 구성된 태그를 설정한다.
      - 기본 메타데이터 태그 이름은 zone이다.
    - UI에서 Node 섹션에서 존으로 태그된 등록된 인스턴스의 전체 목록을 볼 수 있다.
  - 클라이언트 설정 사용자 정의
    - spring.cloud.consul.discovery 접두사 사용
      |속성|기본값|설명|
      |---|---|---|
      |enabled|false|애플리케이션에서 컨설 디스커버리 활성화 여부 설정|
      |failFast|true|true면 서비스 등록 중 예외를 던지고, 아니면 경고를 남긴다.|
      |hostname|-|컨설에 등록할 때 인스턴스의 호스트명 설정|
      |preferIpAddress|false|등록 중에 애플리케이션이 호스트명 대신 IP 주소를 보내도록 강제|
      |scheme|http|서비스가 HTTP 또는 HTTPS 프로토콜을 사용할지 설정|
      |serverListQueryTags|-|단일 태그로 서비스 목록을 필터링할 수 있게 한다.|
      |serviceName|-|spring.application.name 재정의|
      |tags|-|서비스 등록 태그와 사용할 값의 목록 설정|
  - 클러스터 구성
    - 클러스터에서 여러 노드가 함께 동작하도록 구성된 확장 가능한 운영급의 서비스 디스커버리 인프라 구성이 가능하다.
    - 도커 이미지를 사용한 컨설 클러스터 설치 및 구성
      - 다른 멤버 컨테이너에서 사용할 수 있도록 CONSUL_BIND_INTERFACE=eth0 설정 추가
      ```
      $ docker run -d --name consul-1 -p 8500:8500 -e CONSUL_BIND_INTERFACE=eth0 consul # 리더
      $ docker run -d --name consul-2 -p 8501:8500 consul agent --server -client=0.0.0.0 -join=172.17.0.2 # 리더역할과 join
      $ docker run -d --name consul-3 -p 8502:8500 consul agent --server -client=0.0.0.0 -join=172.17.0.2 # 리더역할과 join
      ```
      - 클러스터 목록 확인
        ```
        $ docker exec -t consul-1 consul members
        ```

- 분산 컨피규레이션
  - config library르 사용하는 애플리케이션은 부트스트랩할 때 컨설 키/값 저장소에서 컨피규레이션을 가져온다.
    - 기본으로 /config 폴더에 저장
  - bootstrap.yml 파일에 spring.application.name을 order-service로 설정하고 spring.profiles.activ가 zone1이라면 아래와 같은 순서로 속성을 찾는다.
    - config:/order-service,zone1/
    - config/order-service/
    - config/application,zone1,
    - config/application
  - 컨설에 속성 관리
    - 앱 대시보드 KEY/VALUE 색션을 통해 key,value를 등록할 수 있다.
  - 클라이언트 사용자 정의
    - spring.cloud.consul.config 접두어 사용
    |속성|설명|
    |---|---|
    |enabled|속성을 false로 하면 컨설 컨피그를 비활성화한다.|
    |fail-fast| 컨피규레이션 조회 과정에서 오류를 던지거나 연결 설정 오류인 경우 로그를 남길 지 여부에 대해 설정|
    |prefix|모든 컨피규레이션 값에 대해 기본 폴더 설정 기본값은 /config|
    |defaultContext|특별한 컨피규레이션이 없는 모든 애플리케이션에서 사용되는 폴더 이름 설정 기본값은 /application|
    |profileSeperator|프로파일은 애플리케이션 이름에 콤마를 사용해 분리|
    
  - 컨피규레이션 변경 모니터링
    - 컨피규레이션 적재를 위해서는 HTTP /refresh 종단점 요청
    - 각 속성을 변경하는 경우 컨설의 키 접두사를 감시하는 능력으로 갱신 이벤트가 자동으로 전달된다.
    - 새로운 컨피규레이션 데이터가 있다면 갱신 이벤트가 큐에 게신된다.
      - 모든 큐와 익스체인지는 프로젝트에 spring-cloud-starter-consul-all 의존성으로 포함된 스프링 클라우드 버스에 의해 애플리케이션이 시작할 때 생성된다.
      - 로그 출력 (Refresh keys changed: \[repository.customers\[1].name]
