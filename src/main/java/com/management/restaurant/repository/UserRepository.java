package com.management.restaurant.repository;

import java.util.List;

import com.management.restaurant.domain.User;
import com.management.restaurant.domain.Restaurant;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByEmail(String email);

    List<User> findByRestaurant(Restaurant restaurant);

    boolean existsByEmail(String email);

    User findByRefreshTokenAndEmail(String token, String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END " +
        "FROM User u WHERE u.id = :userId AND u.id = (" +
        "SELECT MIN(user.id) FROM User user WHERE user.restaurant.id = u.restaurant.id)")
    boolean isFirstUserOfRestaurant(@Param("userId") Long userId);
}
