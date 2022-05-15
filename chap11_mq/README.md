#### Consumer vs Stream
- Kafka를 기준으로 작성하였습니다.
- Consumer

  <img width="697" alt="image" src="https://user-images.githubusercontent.com/42403023/168464268-f335a2b4-4af4-43aa-a221-3e2b8c78f17d.png">

  - 이미지 출처: https://www.baeldung.com/java-kafka-streams-vs-kafka-consumer
  - consumer는 토픽으로부터 받은 메시지를 어플리케이션에서 처리하도록 한다.
  - 아래 기능을 포함하여 상호작용할 수 있는 기본 구성 요소 제공
    - Publisher, Consumer의 책임 분리
    - 단일 처리
    - 일괄 처리 지원
    - stateless 하다. 클라이언트는 이전 상태, 개별 스트림 내에 각 record에 대해 상태를 갖고 있지 않다.
    - 병렬 처리를 사용하지 않는다.
    - 여러 cluster에 쓰기 가능하다.

- Stream

  ![image](https://user-images.githubusercontent.com/42403023/168464416-5f9c7cbb-785a-4cc1-a842-661289af134b.png)

  - 이미지 출처: https://www.baeldung.com/java-kafka-streams-vs-kafka-consumer
  - 토픽의 스트림 처리를 단순화한다
  - 클라이언트 라이브러리 위에 구축되어 데이터 병렬 처리, 분산 조정, 내결함성, 확장성 제공
  - 제한없는 연속적이고 실시간 처리한다
    - 단일 Producer, Consumer 구성
    - 복잡한 처리 수행
    - 일괄 처리를 지원하지 않는다
    - stateless, stateful 지원
    - 스레딩 및 병렬 처리
    - 단일 Kafka Cluster와 만 상호작용
    - 메시지 저장 및 전송을 위한 논리적 단위로서 스트림 파티션 및 작업

#### RabbitMQ

- RabbitMQ
  - RabbitMQ는 AMQP를 따르는 오픈소스 메세지 브로커인데, 메세지를 많은 사용자에게 전달하거나, 요청에 대한 처리 시간이 길 때, 해당 요청을 다른 API에게 위임하고 빠른 응답을 할 때 많이 사용한다.
  - MQ를 사용하여 애플리케이션 간 결합도를 낮출 수 있는 장점도 있다.

- Concept
  - Producer
    - 메세지를 생성하고 발송하는 주체
    - 이 메세지는 Queue에 저장이 되는데, 주의할 점은 Producer는 Queue에 직접 접근하지 않고, 항상 Exchange를 통해 접근
  - Consumer
    - 메세지를 수신하는 주체
    - Consumer는 Queue에 직접 접근하여 메세지를 가져옵니다.
  - Queue
    - Producer들이 발송한 메세지들이 Consumer가 소비하기 전까지 보관되는 장소
    - Queue는 이름으로 구분되는데, 같은 이름과 같은 설정으로 Queue를 생성하면 에러 없이 기존 Queue에 연결되지만, 같은 이름과 다른 설정으로 Queue를 생성하려고 시도하면 에러 발생.
  - Exchange
    - Producer들에게서 전달받은 메세지들을 어떤 Queue들에게 발송할지를 결정하는 객체
    - Exchange는 네 가지 타입이 있으며, 일종의 라우터 개념입니다.
  - Binding
    - Exchange에게 메세지를 라우팅 할 규칙을 지정하는 행위
    - 특정 조건에 맞는 메세지를 특정 큐에 전송하도록 설정할 수 있는데, 이는 해당 Exchange 타입에 맞게 설정되어야 한다. 
    - Exchange와 Queue는 m:n binding이 가능

- Exchange 종류
  - Direct
    - Routing key가 정확히 일치하는 Queue에 메세지 전송
      - 하나의 큐에 여러 개의 라우팅 키를 지정할 수 있으며, Direct Exchange에 여러 큐에 같은 라우팅 키를 지정하여 Fanout처럼 동작하게 할 수도 있다.
    - 특징: Unicast
  - Topic
    - Routing key 패턴이 일치하는 Queue에 메세지 전송
    - 특징: Multicast
  - Headers
    - \[key:value]로 이루어진 header 값을 기준으로 일치하는 Queue에 메세지 전송
    - 특징: Multicast
    - 모든 header가 일치할 때, 하나라도 일치할 때 메세지 전송 등의 옵션을 줄 수 있다
  - Fanout
    - 해당 Exchange에 등록된 모든 Queue에 메세지 전송
    - 특징: Broadcast
    
- Prefetch Count
  - Queue와 Consumer가 일대다에 경우 Queue는 기본적으로 Round-Robin 방식으로 메세지를 분배한다
  - Consumer가 2개인 상황에서 홀수번째 메세지는 처리 시간이 짧고, 짝수번째 메세지는 처리 시간이 매우 긴 경우, 계속해서 하나의 Consumer만 일을 하게 되는 상황이 발생할 수 있다
    - 예방하기 위해, prefetch count를 1로 설정해 두면, 하나의 메세지가 처리되기 전(Ack를 보내기 전)에는 새로운 메세지를 받지 않게 되므로, 작업을 분산시킬 수 있다


#### Spring Boot에서 RabbitMQ 사용

- 의존성 추가
  ```
  implementation 'org.springframework.boot:spring-boot-starter-amqp'
  testImplementation 'org.springframework.amqp:spring-rabbit-test'
  ```
  
- 설정
  ```
  spring:
    rabbitmq:
      host: 접속할 서버 IP 주소
      port: 접속할 포트 (default: 5672)
      username: RabbitMQ 유저 아이디
      password: RabbitMQ 유저 비밀번호
      virtual-host: virtual host를 사용하는 경우 virtual host 이름
  ```

- Consumer
  - 설정
    ```java
    @Configuration
    public class ConsumerConfiguration {
        private static final String queueName = "spring-boot";

        private static final String topicExchangeName = "spring-boot-exchange";

        @Bean
        public Queue queue() {
            return new Queue(queueName, false);
        }

        @Bean
        public TopicExchange exchange() {
            return new TopicExchange(topicExchangeName);
        }

        @Bean
        public Binding binding(Queue queue, TopicExchange exchange) {
            return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
        }

        @Bean
        public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                             MessageConverter messageConverter) {
            RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
            rabbitTemplate.setMessageConverter(messageConverter);
            return rabbitTemplate;
        }

        @Bean
        public MessageConverter messageConverter() {
            return new Jackson2JsonMessageConverter();
        }
    }
    ```
    - Queue: 지정된 이름으로 Queue를 등록합니다. 서로 다른 이름으로 여러개의 Queue를 등록 가능
    - Exchange: 위 코드에서는 TopicExchange를 사용해 주어진 패턴과 일치하는 Queue에 메시지를 전달
    - Binding: Exchange가 Queue에게 메시지를 전달하기 위한 룰. 빈으로 등록한 Queue와 Exchange를 바인딩하면서 Exchange에서 사용될 패턴 설정
    - RabbitTemplate: RabbitTemplate는 Spring boot에서 자동으로 빈 등록을 해주지만 받은 메시지 처리를 위한 messageConverter를 설정하기 위해 설정
  
  - Listener
    ```java
    @Service
    public class TestConsumer {

        private final ObjectMapper objectMapper;
        private final TestRepository repository;

        @Autowired
        public TestConsumer(ObjectMapper objectMapper, TestRepository repository) {
            this.objectMapper = objectMapper;
            this.repository = repository;
        }

        @RabbitListener(queues = "spring-boot")
        public void receiveMessage(String message) {
            TestRequest testRequest = objectMapper.convertValue(message, TestRequest.class);

            Test entity = new Test();
            entity.setMessage(testRequest.getMessage());

            repository.save(entity);
        }
    }
    ```

- Producer
  - 설정
    ```java
    @Configuration
    public class ProducerConfiguration {

        @Bean
        RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                      MessageConverter messageConverter) {
            RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
            rabbitTemplate.setMessageConverter(messageConverter);
            return rabbitTemplate;
        }

        @Bean
        MessageConverter messageConverter() {
            return new Jackson2JsonMessageConverter();
        }

    }
    ```
  - 전송
    ```java
    @Component
    public class TestProducer {

        private final RabbitTemplate rabbitTemplate;
        private final ObjectMapper objectMapper;

        @Autowired
        public TestProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
            this.rabbitTemplate = rabbitTemplate;
            this.objectMapper = objectMapper;
        }

        public TestResponse produceTest(TestRequest testRequest) {
            TestResponse response = new TestResponse();
            response.setMessage(testRequest.getMessage());

            try {
                rabbitTemplate.convertAndSend("spring-boot-exchange", "foo.bar.baz", objectMapper.writeValueAsString(testRequest));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }
    }
    ```
    

#### Kafka

- 예전 작성했던 글로 대체: https://github.com/devHTak/devhtak.github.io/blob/master/_posts/msa/2021-07-31-SpringCloud_07_MQ.md

#### 출처

- https://www.baeldung.com/java-kafka-streams-vs-kafka-consumer
- https://blog.dudaji.com/general/2020/05/25/rabbitmq.html
- https://velog.io/@hellozin/Spring-Boot%EC%99%80-RabbitMQ-%EC%B4%88%EA%B0%84%EB%8B%A8-%EC%84%A4%EB%AA%85%EC%84%9C
