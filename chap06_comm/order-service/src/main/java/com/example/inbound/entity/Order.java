package com.example.inbound.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="ORDERS")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {

    @Id @GeneratedValue
    private Long id;

    private String orderId;

    private String productId;

    @Enumerated(value=EnumType.STRING)
    private OrderStatus orderStatus;

    private long price;
}
