package com.management.restaurant.repository;

import java.util.List;
import java.util.Optional;
import com.management.restaurant.domain.Order;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA repository for the {@link Order} entity.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {
    @Query(
        value = "SELECT * FROM orders o " +
                "WHERE o.dining_table_id = :diningTableId AND o.status = 'PENDING' " +
                "ORDER BY o.created_date DESC, o.id DESC LIMIT 1",
        nativeQuery = true
    )
    Optional<Order> findLatestPendingOrderByTableId(@Param("diningTableId") Long id);

    @Query(
        value = "SELECT * FROM orders o " +
            "WHERE o.dining_table_id IN :diningTableIds AND o.status = 'PENDING' " +
            "ORDER BY o.created_date DESC, o.id DESC",
        nativeQuery = true
    )
    List<Order> findPendingOrdersByTableIds(@Param("diningTableIds") List<Long> diningTableIds);
}
