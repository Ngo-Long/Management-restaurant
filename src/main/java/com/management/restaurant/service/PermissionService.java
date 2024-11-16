package com.management.restaurant.service;

import java.util.Optional;

import com.management.restaurant.domain.Permission;
import com.management.restaurant.domain.response.ResultPaginationDTO;
import com.management.restaurant.repository.PermissionRepository;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * Service class for managing permissions.
 */
@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission createPermission(Permission p) {
        return this.permissionRepository.save(p);
    }

    public Permission updatePermission(Permission permission) {
        Permission currentPermission = this.fetchPermissionById(permission.getId());
        if (currentPermission == null) {
            return null;
        }

        currentPermission.setName(permission.getName());
        currentPermission.setApiPath(permission.getApiPath());
        currentPermission.setMethod(permission.getMethod());
        currentPermission.setModule(permission.getModule());

        return this.permissionRepository.save(currentPermission);
    }

    public void deletePermissionById(long id) {
        // delete permission_role
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);

        Permission currentPermission = permissionOptional.get();
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));

        // delete permission
        this.permissionRepository.delete(currentPermission);
    }

    public Permission fetchPermissionById(long id) {
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        return permissionOptional.orElse(null);
    }

    public ResultPaginationDTO fetchPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pPermissions = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pPermissions.getTotalPages());
        mt.setTotal(pPermissions.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pPermissions.getContent());

        return rs;
    }

    public Boolean isNameExist(String name) {
        return this.permissionRepository.existsByName(name);
    }

    public Permission findByModuleAndApiPathAndMethod(String module, String apiPath, String method) {
        return permissionRepository.findByModuleAndApiPathAndMethod(module, apiPath, method)
                .orElse(null);
    }
}
