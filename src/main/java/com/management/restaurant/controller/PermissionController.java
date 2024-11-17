package com.management.restaurant.controller;

import java.util.Objects;
import jakarta.validation.Valid;

import com.turkraft.springfilter.boot.Filter;
import com.management.restaurant.service.PermissionService;

import com.management.restaurant.domain.Permission;
import com.management.restaurant.domain.response.ResultPaginationDTO;

import com.management.restaurant.util.annotation.ApiMessage;
import com.management.restaurant.util.error.InfoInvalidException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.web.bind.annotation.*;


/**
 * REST controller for managing permissions.
 * This class accesses the {@link Permission} entity
 */
@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final Logger log = LoggerFactory.getLogger(PermissionController.class);

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * {@code POST  /permissions} : Create a new permission.
     *
     * @param permission the permission to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and the created permission in the body,
     *         or with status {@code 400 (Bad Request)} if the permission name already exists or conflicts with an existing permission.
     * @throws InfoInvalidException if the permission name is already in use, or if the combination of module, API path,
     *         and method already exists.
     */
    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) throws InfoInvalidException {
        log.debug("REST request to save Permission : {}", permission);

        if (this.permissionService.isNameExist(permission.getName()))  {
            throw new InfoInvalidException("Tên đã tồn tại, vui lòng sử dụng tên khác!");
        }

        // Check module, apiPath and method
        Permission conflictingPermission = this.permissionService.findByModuleAndApiPathAndMethod(
                permission.getModule(),
                permission.getApiPath(),
                permission.getMethod()
        );

        if (conflictingPermission != null) {
            throw new InfoInvalidException("Quyền hạn đã tồn tại!");
        }

        Permission newPermission = this.permissionService.createPermission(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPermission);
    }

    /**
     * {@code PUT  /permissions} : Update an existing permission.
     *
     * @param permission the permission details to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the updated permission in the body,
     *         or with status {@code 400 (Bad Request)} if the permission is invalid, or conflicts with an existing permission.
     * @throws InfoInvalidException if the permission does not exist, the permission name is already in use,
     *         or if the combination of module, API path, and method conflicts with another permission.
     */
    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission) throws InfoInvalidException {
        log.debug("REST request to update Permission : {}", permission);

        Permission currentPermission = this.permissionService.fetchPermissionById(permission.getId());
        if (currentPermission == null) {
            throw new InfoInvalidException("Quyền hạn không tồn tại!");
        }

        boolean isNameExist = this.permissionService.isNameExist(permission.getName());
        if (isNameExist && !Objects.equals(currentPermission.getName(), permission.getName()))  {
            throw new InfoInvalidException("Tên đã tồn tại, vui lòng sử dụng tên khác!");
        }

        // Check module, apiPath and method
        Permission conflictingPermission = this.permissionService.findByModuleAndApiPathAndMethod(
                permission.getModule(),
                permission.getApiPath(),
                permission.getMethod()
        );

        if (conflictingPermission != null && !conflictingPermission.getId().equals(permission.getId())) {
            throw new InfoInvalidException("Quyền hạn đã tồn tại!");
        }

        // update permission
        Permission dataPermission = this.permissionService.updatePermission(permission);
        return ResponseEntity.ok().body(dataPermission);
    }

    /**
     * {@code DELETE  /permissions/:id} : delete the "id" permission.
     *
     * @param id the id of the permissions to delete.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     */
    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> deletePermissionById(@PathVariable("id") long id) throws InfoInvalidException {
        log.debug("REST request to delete Permission: {}", id);

        if (this.permissionService.fetchPermissionById(id) == null) {
            throw new InfoInvalidException("Quyền hạn không tồn tại!");
        }

        this.permissionService.deletePermissionById(id);
        return ResponseEntity.ok().body(null);
    }

    /**
     * {@code GET  /permissions} : Fetch filter permissions.
     *
     * @param pageable the pagination information.
     * @param spec the filtering criteria to apply to the permission list.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of permissions in the body.
     */
    @GetMapping("/permissions")
    @ApiMessage("Fetch filter permissions")
    public ResponseEntity<ResultPaginationDTO> getPermissions(@Filter Specification<Permission> spec, Pageable pageable) {
        log.debug("REST request to get permission filter");
        return ResponseEntity.ok(this.permissionService.fetchPermissions(spec, pageable));
    }

}
