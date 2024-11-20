package com.management.restaurant.repository;

import com.management.restaurant.domain.Order;
import com.management.restaurant.domain.DiningTable;

import com.management.restaurant.domain.enumeration.OrderStatusEnum;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Order} entity.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {
    DiningTable findByDiningTableId(Long diningTableId);
    Optional<Order> findByIdAndStatus(Long id, OrderStatusEnum status);
}
