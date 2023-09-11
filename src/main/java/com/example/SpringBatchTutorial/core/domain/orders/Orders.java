package com.example.SpringBatchTutorial.core.domain.orders;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderItem;

    private Integer price;

    private Date orderDate;


    public Orders(String orderItem, Integer price, Date orderDate) {
        this.orderItem = orderItem;
        this.price = price;
        this.orderDate = orderDate;
    }
}
