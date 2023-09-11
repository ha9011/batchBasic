package com.example.SpringBatchTutorial.core.domain.accounts;

import com.example.SpringBatchTutorial.core.domain.orders.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Long> {

}
