package com.management.restaurant.controller;

import java.util.Objects;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import com.management.restaurant.domain.Role;
import com.management.restaurant.domain.response.ResultPaginationDTO;
import com.management.restaurant.service.RoleService;

import com.management.restaurant.util.annotation.ApiMessage;
import com.management.restaurant.util.error.InfoInvalidException;


/**
 * REST controller for managing restaurants.
 * This class accesses the {@link com.management.restaurant.domain.Role} entity
 */
@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final Logger log = LoggerFactory.getLogger(RestaurantController.class);

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * {@code POST  /roles} : Create a new role.
     *
     * @param role the restaurant to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new role, or with status {@code 400 (Bad Request)} if the role name already exists.
     * @throws InfoInvalidException if the role name already exists or if the input information is invalid.
     */
    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) throws InfoInvalidException {
        log.debug("REST request to save Restaurant : {}", role);

        if (this.roleService.existByName(role.getName())) {
            throw new InfoInvalidException("Tên đã tồn tại, vui lòng sử dụng tên khác!");
        }

        Role newRole = this.roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRole);
    }

    /**
     * {@code PUT  /roles/:id} : Update an existing role.
     *
     * @param role the role details to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the updated role in the body,
     *         or with status {@code 400 (Bad Request)} if the role is invalid or if the name already exists.
     * @throws InfoInvalidException if the role does not exist or if the role name is already taken.
     */
    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody Role role) throws InfoInvalidException {
        Role currentRole = this.roleService.fetchRoleById(role.getId());
        if (currentRole == null) {
            throw new InfoInvalidException("Chức vụ không tồn tại!");
        }

        boolean isNameExist = this.roleService.existByName(role.getName());
        if (isNameExist && !Objects.equals(currentRole.getName(), role.getName())) {
            throw new InfoInvalidException("Tên đã tồn tại, vui lòng sử dụng tên khác!");
        }

        Role dataRole = this.roleService.updateRole(role);
        return ResponseEntity.ok().body(dataRole);
    }

    /**
     * {@code DELETE  /roles/:id} : delete the "id" role.
     *
     * @param id the id of the roles to delete.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     */
    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> deleteRoleById(@PathVariable("id") long id) throws InfoInvalidException {
        log.debug("REST request to delete Restaurant: {}", id);

        if (this.roleService.fetchRoleById(id) == null) {
            throw new InfoInvalidException("Chức vụ không tồn tại!");
        }

        this.roleService.deleteRoleById(id);
        return ResponseEntity.ok().body(null);
    }

    /**
     * {@code GET  /roles/:id} : get the "id" role.
     *
     * @param id the id of the roles to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the roles, or with status {@code 400 (Bad Request)}.
     */
    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") long id) throws InfoInvalidException {
        log.debug("REST request to get Restaurant : {}", id);

        Role dataRole = this.roleService.fetchRoleById(id);
        if (dataRole == null) {
            throw new InfoInvalidException("Chức vụ không tồn tại!");
        }

        return ResponseEntity.ok().body(dataRole);
    }

    /**
     * {@code GET  /admin/roles} : Fetch all roles.
     *
     * @param pageable the pagination information.
     * @param spec the filtering criteria to apply to the role list.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of roles in the body.
     */
    @GetMapping("/roles")
    @ApiMessage("Fetch roles")
    public ResponseEntity<ResultPaginationDTO> getRoles(
            @Filter Specification<Role> spec, Pageable pageable) {
        log.debug("REST request to get all Restaurant for an admin");
        return ResponseEntity.ok().body(this.roleService.getRoles(spec, pageable));
    }
}