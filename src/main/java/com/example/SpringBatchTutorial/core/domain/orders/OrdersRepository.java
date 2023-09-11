package com.example.SpringBatchTutorial.core.domain.orders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

}
