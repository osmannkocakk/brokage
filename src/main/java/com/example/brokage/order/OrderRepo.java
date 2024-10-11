package com.example.brokage.order;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.brokage.order.model.Order;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdAndCreateDateGreaterThanAndCreateDateLessThan(Long customerId, Date startDate, Date endDate);

    //Pessimistic write lock to prevent lost updates
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ord FROM Order ord WHERE ord.id = :id")
    Optional<Order> findByIdForUpdate(Long id);


}
