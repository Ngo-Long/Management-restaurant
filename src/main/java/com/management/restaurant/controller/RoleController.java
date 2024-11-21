package com.management.restaurant.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.management.restaurant.domain.Restaurant;
import com.management.restaurant.domain.User;
import com.management.restaurant.service.UserService;
import com.management.restaurant.util.SecurityUtil;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.web.bind.annotation.*;

import com.turkraft.springfilter.boot.Filter;

import com.management.restaurant.domain.Role;
import com.management.restaurant.domain.response.ResultPaginationDTO;
import com.management.restaurant.service.RoleService;

import com.management.restaurant.util.annotation.ApiMessage;
import com.management.restaurant.util.error.InfoInvalidException;

/**
 * REST controller for managing roles.
 * This class accesses the {@link Role} entity
 */
@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final Logger log = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;
    private final UserService userService;
    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public RoleController(
        RoleService roleService,
        UserService userService,
        FilterBuilder filterBuilder,
        FilterSpecificationConverter filterSpecificationConverter
    ) {
        this.roleService = roleService;
        this.userService = userService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    /**
     * {@code POST  /roles} : Create a new role.
     *
     * @param role the role to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new role, or with status {@code 400 (Bad Request)} if the role name already exists.
     * @throws InfoInvalidException if the role name already exists or if the input information is invalid.
     */
    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) throws InfoInvalidException {
        log.debug("REST request to save Role : {}", role);

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
        log.debug("REST request to delete Role: {}", id);

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
        log.debug("REST request to get Role : {}", id);

        Role dataRole = this.roleService.fetchRoleById(id);
        if (dataRole == null) {
            throw new InfoInvalidException("Chức vụ không tồn tại!");
        }

        return ResponseEntity.ok().body(dataRole);
    }

    /**
     * {@code GET /roles} : Retrieve all roles with optional filtering and pagination
     *
     * This endpoint is accessible to administrators only
     *
     * @param pageable the pagination information.
     * @param spec     the filtering criteria applied to the query
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and a body containing
     *         the paginated list of users.
     */
    @GetMapping("/roles")
    @ApiMessage("Fetch filter roles")
    public ResponseEntity<ResultPaginationDTO> getAllRoles(
            @Filter Specification<Role> spec, Pageable pageable) {
        log.debug("REST request to get filter role");
        return ResponseEntity.ok().body(this.roleService.fetchRoles(spec, pageable));
    }

    /**
     * {@code GET /roles/by-user} : Retrieve roles associated with the current role's user
     *
     *  This endpoint fetches roles specific to the user associated with the currently
     *  authenticated role and applies additional filtering and pagination criteria.
     *
     * @param pageable the pagination information.
     * @param spec     the filtering criteria applied to the query
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and a body containing
     *  *         the paginated list of roles for the restaurant.
     */
    @GetMapping("/roles/by-user")
    @ApiMessage("Fetch roles by user")
    public ResponseEntity<ResultPaginationDTO> getRolesByUser(
        @Filter Specification<Role> spec,  Pageable pageable) {
        log.debug("REST request to get roles by user");

        // get info user
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userService.fetchUserByUsername(email);

        // create spec user roles
        Specification<Role> userRolesSpec = (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.join("users").get("id"), currentUser.getId());

        return ResponseEntity.ok(this.roleService.fetchRoles(spec.and(userRolesSpec), pageable));
    }
}
