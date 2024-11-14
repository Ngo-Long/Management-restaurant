package com.management.restaurant.repository;

import com.management.restaurant.domain.Restaurant;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA repository for the {@link Restaurant} entity.
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, JpaSpecificationExecutor<Restaurant> {
	List<Restaurant> findByName(String name);
}
