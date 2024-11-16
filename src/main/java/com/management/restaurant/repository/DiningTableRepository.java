package com.management.restaurant.repository;

import java.util.List;
import java.util.Optional;

import com.management.restaurant.domain.DiningTable;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA repository for the {@link DiningTable} entity.
 */
public interface DiningTableRepository extends JpaRepository<DiningTable, Long>,
        JpaSpecificationExecutor<DiningTable> {

    Boolean existsByName(String name);

    List<DiningTable> findByName(String name);

    List<DiningTable> findByLocation(String location);
}
