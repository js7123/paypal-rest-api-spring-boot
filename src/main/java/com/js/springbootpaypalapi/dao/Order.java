package com.js.springbootpaypalapi.dao;

import lombok.Data;
import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name= "orders")
@Data
public class Order {
    @Id
    @SequenceGenerator(name = "USER_SEQ", sequenceName = "USER_SEQ")
    @GeneratedValue(strategy = SEQUENCE, generator = "USER_SEQ")
    @Column(name = "ID")
    private Long id;

    @Column(name = "paypal_order_id")
    private String paypalOrderId;

    @Column(name = "paypal_order_status")
    private String paypalOrderStatus;
}
