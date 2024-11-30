package com.management.restaurant.repository;

import com.management.restaurant.domain.Order;
import com.management.restaurant.domain.OrderDetail;

import com.management.restaurant.domain.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link OrderDetail} entity.
 */
@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>,
        JpaSpecificationExecutor<OrderDetail>{
    @Query("SELECT SUM(d.quantity * d.price) FROM OrderDetail d WHERE d.order.id = :orderId")
    Double calculateTotalPrice(@Param("orderId") Long orderId);

    @Query("SELECT d FROM OrderDetail d WHERE d.order.id = :orderId")
    List<OrderDetail> findByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT d FROM OrderDetail d WHERE d.order.diningTable.restaurant.id = :restaurantId")
    List<OrderDetail> findByRestaurant(@Param("restaurantId") Long restaurantId);
}
