#### 분산 로깅과 추적

- 마이크로 서비스에서는 새로운 차원으로 로그의 설계를 해야 한다.
  - 마이크로 서비스 아키텍처에서는 다수의 머신에서 동작하는 작고 독립적이고 수평적으로 확장되고 서로 통신하는 수많은 서비스가 있다.
  - 이를 보기 쉽게 하기 위해서는 이런 요청들을 연관 짓고 모든 로그를 단일의 중앙 저장소에 저장해야 한다.
  - 스프링 클라우드는 스프링 클라우드 슬루스(Spring Cloud Sleuth)라는 분산 추적 솔루션을 구현한 전용 라이브러리를 제공한다.

- 로깅과 추적의 차이
  - 추적은 프로그램의 데이터 흐름을 따라가는 것
    - 성능 병목 또는 에러가 발생한 시간을 찾아내기 위해 시스템 흐름을 추적
  - 로깅은 에러 보고와 탐지에 사용
    - 로깅은 추적과 대조적으로 항상 사용해야 한다.
    - 대규모 시스템을 설계할 때 훌륭하고 유연한 머신 간의 에러 보고를 보유하고 싶다면 중앙에 로그 데이터를 수집해야 한다.
    - 대표적인 솔루션은 ELK 스택(Elasticsearch + Logstash + Kibana)이다.
    - Spring Cloud에서는 Zipkin이 있다.

#### 마이크로 서비스를 위한 로깅의 모범 사례

- 로깅을 사용하는 가장 중요한 모범 사례중 하나는 모든 요청과 나가는 응답을 추적하는 것이다
- 로그를 보여주는 형식은 표준화하는 것이 좋다.
  - 통신에서 JSON 타입을 많이 사용하므로 JSON 구조를 보여주는 것이 좋다.
    ```
    17:11:53.712  INFO   Order received with id 1, customerId 5, productId 10.
    ```
  - 로그 엔트리가 어떤 로그 레벨로 로그를 남겨야하는지 결정하는 것도 중요하다.
    - TRACE: 매우 자세한 정보로서 개발을 위한 것.
    - DEBUG: 프로그램에서 발생하는 모든 것을 로그로 남긴다. 개발자가 디버깅 또는 문제 해결에 주로 사용
    - INFO: 가장 중요한 정보를 해당 레벨로 남긴다. 운영자, 고급 사용자도 쉽게 이해할 수 있어야 하며, 애플리케이션이 무엇을 하고 있는지 빠르게 찾을 수 있게 하기 위함이다.
    - WARN: 에러가 될만한 모든 이벤트를 해당 레벨로 남긴다. 해당 프로세스에 각별한 주의를 기울여야 한다.
    - ERROR: 보통 예외를 이 레벨로 남긴다.
    - FATAL: 애플리케이션을 중단시킬 수 있는 잠재성 있는 중대한 에러 이벤트를 해당 레벨로 남긴다.
  - 로그가 왜 배출되었는지 명시해야 한다.
    - Time, Hostname, AppName과 같은 모든 마이크로서비스에서 정규화해야 하는 중요한 특징

#### Spring Boot 를 사용한 로깅

- starter를 사용해 의존성을 포함하면 Logback이 애플리케이션에서 기본으로 사용된다.
  - application.yml 에 logging.* 속성을 사용
```yml
logging:
  file: logs/order.log
  level:
    com.netflix: DEBUG
    org.springframework.web.filter.CommonsRequestLoggingFilter: DEBUG
  pattern:
    console: "%d{HH:mm:ss.SSS} %-5level %msg%n"
    file: "%d{HH:mm:ss.SSS} %-5level %msg%n"
```
- 출력에는 밀리초 단위의 날짜와 시간, 로그 레벨, 프로세스 ID, 스레드 이름, 로그를 출력한 전체 클래스 이름, 메시지가 들어간다.
  - console: logging.pattern.console, file appender: logging.pattern.file
- 로그 콘솔 외에 파일로 남기기
  - logging.file
- 로그 레벨 설정
  - logging.level.* : 패키지, 클래스 단위로 재정의 가능

- 컨피규레이션으로 충분하지 않을 경우
  - 어펜더 또는 필터를 정의하려면 반드시 로그백(logback-spring.xml)이나 Log4j2(log4j2-spring.xml), Java Util Logging(logging.properties) 같은 로깅 시스템을 위한 컨피그레이션을 포함해야 한다.

#### ELK Stack

- ELK는 ElasticSearch, Logstash, Kibana 에 약자이다.
  - ElasticSearch : LogStash를 통해서 전송받은 데이터 분석 및 저장 기능을 담당한다.
    - 인기 있는 이유는 성능에 있다. 물론 확장성, 유연성, 그리고 저장된 데이터 검색하기 위한 REST, JSON기반 API 를 제공함으로써 쉽게 통합할 수 있다는 장점도 가지고 있다.
  - Logstash : 데이터를 처리하는 파이프라인으로, 로그를 수집하여 ElasticSearch에 전송한다.
    - 외부 소스로부터 이벤트를 추출하는 다양한 입력을 지원
    - 데이터를 수신하고 목적지에 전달하는 것 뿐만 아니라 파싱하고 변환할 수도 있다.
  - Kibana : ElasticSearch에 저장되어 있는 데이터를 시각화하고, 실시간으로 분석할 수 있다.
    - 검색 질의를 생성해 애플리케이션에 수집된 모든 로그를 쉽게 표현하고 필터링할 수 있다.
    - PDF, CSV 형식으로 내보내 리포트를 제공할 수도 있다.

- 도커를 활용한 설치
  - Elasticsearch
    ```
    $ docker run -d --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:6.1.1
    ```
  - Logstash
    - logstash.conf 컨피큐레이션 파일 정의
      ```
      input {
        tcp {
          port => 5000
          codec => json
        }
      }
      output {
        elasticsearch {
          hosts => ["http://192.169.99.100:9200"]
          index => "micro-%{appName}"
        }
      }
      ```
      - input으로 간단한 logstash-input-tcp TCP 플러그인 정의하는 데, 로깅 어펜더로 사용하는 LogstashTcpSocketAppender와 호환
        - 모든 로그는 JSON 타입으로 보낸다.
      - output은 elasticsearch가 되며 마이크로서비스는 엘라스틱서치에서 이름과 micro 접두사로 인덱스가 구성된다.
      ```
      $ docker run -d --name logstash -p 5000:5000 -v ~/logstash.conf:/config-dir/logstash.conf docker.elastic.co/logstash/logstash-oss:6.1.1 -f /config/dir/logstash.conf
      ```
  - Kibana
    ```
    $ docker run -d --name kibana -e "ELASTICSEARCH_URL=http://192.168.99.100:9200" -p 5601:5601 docker.elastic.co/kibana/kibana:6.1.1
    ```

##### ELK와 어플리케이션 통합하기

- 통합 방법은 다양하게 제공하고 있다.
- 로그백과 전용 어펜더 기반의 컨피규레이션 방법으로 진행 
  - logback-spring.xml 파일에 net.logstash.loback.appender.LogstashSockerAppender 선언
  - dependency 추가
    ```
    implementation group: 'net.logstash.logback', name: 'logstash-logback-encoder', version: '4.1'
    ```
  - LogstashTCPAppender 클래스를 사용한 어펜더 정의
    ```
    <appender name="STASH" class="net.logstash.logback.appender.LogstashTcpSockerAppender">
      <destinatoin>192.168.99.100:5000</destination>
      <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
        <proviers>
          <mdc />
          <context />
          <logLevel />
          <pattern> <pattern> {"appName": "order-service"} </pattern></pattern>
          <threadName />
          <message />
          <logstashMarkers />
          <stackTrace />
        </providers>
      </encoder>
    </appender>
    ```
  
- AMQP 등 메시지 브로커 사용
  - 래빗엠큐 인스턴스를 실행한 후, 로그스태시에 연결한 queue, exchange를 생성한다.
    - 예제로는 q_logstash, ex_logstash로 명함.
  - dependency 추가
    ```
    implementation group: 'org.springframework.boot', name: 'spring-boot-starter-amqp'
    ```
  - AmqpAppender 클래스를 사용한 어펜더 정의
    ```
    <appender name="AMQP" class="org.springframework.amqp.rabbit.logback.AmqpAppender">
      <layout>
        <pattern> {"time": "%date{ISO08601}", "thread": "%thread", "level": "%level", "class": "%logger{36}", "message": "%message}</pattern>
      </layout>
      <host>192.168.99.100</host>
      <port>5000</port>
      <username>guest</username>
      <password>guest</password>
      <applicationId>order-service</applicationId>
      <routingKeyPattern>order-service</routingKeyPattern>
      <declareExchange>true</declareExchange>
      <exchangeType>direct</exchangeType>
      <generatedId>true</generatedId>
      <charset>UTF-8</charset>
      <durable>true</durable>
      <deliveryMode>PERSISTENT</deliveryMode>
    </appender>
    ```
  - 로그스태시 input에 rabbitmq로 설정
    ```
    input {
      rabbitmq {
        host => "192.169.99.100"
        port => 5672
        durable => true
        exchange => "ex_logstash"
      }
    }
    output {
      elasticsearch {
        hosts => ["http://192.169.99.100:9200"]
      }
    }
    ```
