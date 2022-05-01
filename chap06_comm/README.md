#### Chap06. 마이크로서비스 간의 커뮤니케이션

##### 다양한 커뮤니케이션 스타일

- 동기식/비동기식 커뮤니케이션 프로토콜로 나눌 수 있다.
  - 비동기 커뮤니케이션의 핵심은 응답을 기다리는 동안 클라이언트의 스레드가 멈추지 않아도 되는 것
    - EX) AMQP

##### 스프링 클라우드를 사용한 동기식 통신

- RestTemplate
  - 클라이언트가 RESTful 웹서비스를 사용할 때 사용
  - @LoadBalanced 한정자 사용

- Ribbon
  - 클라이언트측 부하분산기로서 HTTP, TCP 클라이언트의 행동을 제어하는 간단한 인터페이스 제공
  - 리본은 서비스 디스커버리, 서킷브레이커와 같은 스프링 클라우드 구성 요소와 쉽게 통합

- Feign
  - 부하 분산, 서비스 디스커버리에서 데이터를 가져오기 위해 리본을 사용
  - @FeignClient를 사용해 인터페이스를 쉽게 선언할 수 있다.

##### 리본을 활용한 동기식 통신

- 시나리오
  - 주문 서비스(order-service)
  - 주문을 생성하는 데, 각 제품의 가격(product-service), 고객의 주문 이력(customer-service) 를 호출해 얻은 시스템 내의 카테고리를 고려하여 최종 가격을 계산한 후 계산된 가격 반환

- application.yml 설정
  ```yml
  server:
    port: 8090
    customer-service:
      ribbon:
        eureka:
          enabled: false
        list-of-servers: localhost:8091
    product-service:
      ribbon:
        eureka:
          enabled: false
        list-of-servers: localhost:8092
  ```
  
- OrderApplication.java
  ```java
  @SpringBootApplication
  @RibbonClients({
      @RibbonClient(name="customer-service"),
      @RibbonClient(name="product-service")
  })
  public class OrderApplication {
      
      @LoadBalanced
      @Bean
      public RestTemplate restTemplate() {
          return new RestTemplate();
      }
      
      public static void main(String[] args) {
          new SpringApplicationBuilder(OrderApplication.class).web(true).run(args);
      }
  }
  ```
  - @RibbonClients를 통해 application.yml에 구성된 이름 목록을 선언해 리본 클라이언트를 사용하도록 한다.
  - RestTemplate을 사용하기 위해 빈으로 등록한다

- 다른 서비스 호출하기
  ```java
  @Autowired private RestTemplate restTemplate;
  @Autowired private OrderRepository orderRepsitory;
  
  @PostMapping
  pubblic Order prepare(@RequestBody Order order) {
      int price = 0;
      
      Product[] products = template.postForObject("http://product-service/ids", order.getProductIds(), Product.class);
      Customer customer = template.getForObject("http://customer-service/withAccounts/{id}", Customer.class, order.getCustomerId());
      
      for(Product product: products) {
          price += product.getPrice();
      }
      
      final int priceDiscounted = priceDiscount(price, customer);
      Optional<Account> account = customer.getAccounts().stream().filter({ a -> 
                  return a.getBalance() > priceDiscounted;
              }).findFirst();
              
       if(account.ifPresent()) {
            order.setAccountId(account.get().getId());
            order.setStatus(OrderStatus.ACCEPTED);
            order.setPrice(priceDiscounted);
       } else {
            order.setStatus(OrderStatus.REJECTED);
       }
       
       return repository.add(order);
  }
  ```
  
##### 서비스 디스커버리와 함께 RestTemplate 사용하기

- application.yml 설정
  ```yml
  server:
    port: 8090
  spring:
    application:
      name: order-service
  eureka:
    client:
      fetch-register: true
      registry-with-eureka: ture
      service-url:
        default-zone: http://localhost:8761/eureka
  ```

- OrderService.java
  ```java
  @SpringBootApplication
  @EnabledDiscoveryClient
  public class OrderApplication {
      
      @LoadBalanced
      @Bean
      public RestTemplate restTemplate() {
          return new RestTemplate();
      }
      
      public static void main(String[] args) {
          new SpringApplicationBuilder(OrderApplication.class).web(true).run(args);
      }
  }
  ```
