package com.management.restaurant.repository;

import java.util.List;

import com.management.restaurant.domain.Product;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA repository for the {@link Product} entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>{

    Boolean existsByName(String name);

    List<Product> findByName(String name);
}
