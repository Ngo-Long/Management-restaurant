package com.management.restaurant.repository;

import java.util.List;
import java.util.Optional;

import com.management.restaurant.domain.Permission;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA repository for the {@link Permission} entity.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

    boolean existsByName(String name);

    List<Permission> findByIdIn(List<Long> id);

    Optional<Permission> findByModuleAndApiPathAndMethod(String module, String apiPath, String method);
}
