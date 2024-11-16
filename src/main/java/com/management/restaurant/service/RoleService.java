package com.management.restaurant.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.management.restaurant.domain.Role;
import com.management.restaurant.domain.Permission;
import com.management.restaurant.domain.response.ResultPaginationDTO;

import com.management.restaurant.repository.RoleRepository;
import com.management.restaurant.repository.PermissionRepository;

/**
 * Service class for managing roles.
 */
@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role createRole(Role dataRole) {
        // check permissions
        if (dataRole.getPermissions() != null) {
            List<Long> reqPermissions = dataRole.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            dataRole.setPermissions(dbPermissions);
        }

        return this.roleRepository.save(dataRole);
    }

    public Role updateRole(Role dataRole) {
        Role roleDB = this.fetchRoleById(dataRole.getId());

        // check permissions
        if (dataRole.getPermissions() != null) {
            List<Long> reqPermissions = dataRole.getPermissions()
                    .stream().map(Permission::getId)
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            dataRole.setPermissions(dbPermissions);
        }

        roleDB.setName(dataRole.getName());
        roleDB.setDescription(dataRole.getDescription());
        roleDB.setActive(dataRole.isActive());
        roleDB.setPermissions(dataRole.getPermissions());
        this.roleRepository.save(roleDB);

        return roleDB;
    }

    public void deleteRoleById(long id) {
        this.roleRepository.deleteById(id);
    }

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role fetchRoleById(long id) {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        return roleOptional.orElse(null);

    }

    public ResultPaginationDTO getRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageRole.getTotalPages());
        mt.setTotal(pageRole.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageRole.getContent());

        return rs;
    }
}
