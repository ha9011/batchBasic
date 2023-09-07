package com.example.SpringBatchTutorial.core.domain.accounts;

import com.example.SpringBatchTutorial.core.domain.orders.Orders;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@ToString
@Getter
public class Accounts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderItem;

    private Integer price;

    private Date orderDate;
    private Date accountDate;

    @Builder
    public Accounts(String orderItem, Integer price, Date orderDate) {
        this.orderItem = orderItem;
        this.price = price;
        this.orderDate = orderDate;
        this.accountDate = new Date();
    }

    public static Accounts create(Orders orders) {
        return Accounts.builder()
                .orderDate(orders.getOrderDate())
                .price(orders.getPrice())
                .orderItem(orders.getOrderItem())
                .build();
    }
}
