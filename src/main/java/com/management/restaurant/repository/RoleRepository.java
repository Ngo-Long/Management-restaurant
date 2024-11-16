package com.management.restaurant.repository;

import com.management.restaurant.domain.Role;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA repository for the {@link Role} entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    boolean existsByName(String name);

    Role findByName(String name);
}
