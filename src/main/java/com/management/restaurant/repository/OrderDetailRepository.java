package com.management.restaurant.repository;

import com.management.restaurant.domain.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA repository for the {@link OrderDetail} entity.
 */
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>,
        JpaSpecificationExecutor<OrderDetail>{
}
