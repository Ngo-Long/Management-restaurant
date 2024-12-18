package com.management.restaurant.repository;

import com.management.restaurant.domain.Restaurant;
import com.management.restaurant.domain.DiningTable;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA repository for the {@link DiningTable} entity.
 */
@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Long>,
        JpaSpecificationExecutor<DiningTable> {
    Boolean existsByNameAndRestaurant(String name, Restaurant restaurant);
}
