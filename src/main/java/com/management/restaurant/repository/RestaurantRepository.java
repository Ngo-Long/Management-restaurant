package com.management.restaurant.repository;

import java.util.List;

import com.management.restaurant.domain.Restaurant;

import com.management.restaurant.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA repository for the {@link Restaurant} entity.
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, JpaSpecificationExecutor<Restaurant> {

	Boolean existsByName(String name);

	List<Restaurant> findByName(String name);

    @Query("SELECT u.restaurant FROM User u WHERE u = :user")
    Restaurant findByUser(@Param("user") User user);
}
