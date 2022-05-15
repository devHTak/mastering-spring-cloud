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

#### Spring Boot에서 RabbitMQ 사용

