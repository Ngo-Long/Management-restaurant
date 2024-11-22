package com.management.restaurant.controller;

import java.util.Objects;

import com.management.restaurant.domain.Restaurant;
import com.management.restaurant.domain.User;
import com.management.restaurant.service.UserService;
import com.management.restaurant.util.SecurityUtil;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;
import com.turkraft.springfilter.boot.Filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.management.restaurant.domain.DiningTable;
import com.management.restaurant.domain.response.ResultPaginationDTO;
import com.management.restaurant.util.annotation.ApiMessage;
import com.management.restaurant.util.error.InfoInvalidException;
import com.management.restaurant.service.DiningTableService;

/**
 * REST controller for managing dining tables.
 * This class accesses the {@link DiningTable} entity
 */
@RestController
@RequestMapping("/api/v1")
public class DiningTableController {

    private final Logger log = LoggerFactory.getLogger(DiningTableController.class);

    private final DiningTableService diningTableService;
    private final UserService userService;
    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public DiningTableController(
        DiningTableService diningTableService,
        UserService userService,
        FilterBuilder filterBuilder,
        FilterSpecificationConverter filterSpecificationConverter
    ) {
        this.diningTableService = diningTableService;
        this.userService = userService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    /**
     * {@code POST  /dining-tables} : Create a new dining table.
     *
     * @param table the dining table to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dining tables,
     *         or with status {@code 400 (Bad Request)} if the dining tables name already exists.
     * @throws InfoInvalidException if the dining table name already exists or if the input information is invalid.
     */
    @PostMapping("/dining-tables")
    @ApiMessage("Create a dining table")
    public ResponseEntity<DiningTable> createDiningTable(@Valid @RequestBody DiningTable table)
            throws InfoInvalidException {
        log.debug("REST request to save dining table : {}", table);

        boolean isExist = this.diningTableService.isNameAndRestaurantExist(table.getName(),table.getRestaurant());
        if (isExist) {
            throw new InfoInvalidException("Tên đã tồn tại, vui lòng sử dụng tên khác!");
        }

        DiningTable newDiningTable = this.diningTableService.createDiningTable(table);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDiningTable);
    }

    /**
     * {@code PUT  /dining-tables/:id} : Update an existing dining table.
     *
     * @param table the dining table details to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the updated dining table in the body,
     *         or with status {@code 400 (Bad Request)} if the dining table is invalid or if the name already exists.
     * @throws InfoInvalidException if the dining table does not exist or if the dining table name is already taken.
     */
    @PutMapping("/dining-tables")
    @ApiMessage("Update a dining table")
    public ResponseEntity<DiningTable> updateDiningTable(@Valid @RequestBody DiningTable table)
            throws InfoInvalidException {
        log.debug("REST request to update dining table : {}", table);

        DiningTable currentTable = this.diningTableService.fetchDiningTableById(table.getId());
        if (currentTable == null) {
            throw new InfoInvalidException("Bàn ăn không tồn tại!");
        }

        boolean isExist = this.diningTableService.isNameAndRestaurantExist(table.getName(),table.getRestaurant());
        if (isExist && !Objects.equals(currentTable.getName(), table.getName())) {
            throw new InfoInvalidException("Tên đã tồn tại, vui lòng sử dụng tên khác!");
        }

        DiningTable dataTable = this.diningTableService.updateDiningTable(table);
        return ResponseEntity.ok().body(dataTable);
    }

    /**
     * {@code DELETE  /dining-tables/:id} : delete the "id" dining table.
     *
     * @param id the id of the dining tables to delete.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     */
    @DeleteMapping("/dining-tables/{id}")
    @ApiMessage("Delete a dining table")
    public ResponseEntity<Void> deleteDiningTableById(@PathVariable("id") Long id)
        throws InfoInvalidException {
        log.debug("REST request to delete dining table: {}", id);

        DiningTable currentTable = this.diningTableService.fetchDiningTableById(id);
        if (currentTable == null) {
            throw new InfoInvalidException("Bàn ăn không tồn tại!");
        }

        this.diningTableService.deleteDiningTableById(id);
        return ResponseEntity.ok(null);
    }

    /**
     * {@code GET  /dining-tables/:id} : get the "id" dining table.
     *
     * @param id the id of the dining tables to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dining tables,
     *         or with status {@code 400 (Bad Request)}.
     */
    @GetMapping("/dining-tables/{id}")
    @ApiMessage("Get dining table by id")
    public ResponseEntity<DiningTable> fetchDiningTableById(@PathVariable("id") long id)
        throws InfoInvalidException {
        log.debug("REST request to get dining table : {}", id);

        DiningTable dataTable = this.diningTableService.fetchDiningTableById(id);
        if (dataTable == null) {
            throw new InfoInvalidException("Bàn ăn không tồn tại!");
        }

        return ResponseEntity.ok().body(dataTable);
    }

    /**
     * {@code GET  /dining-tables} : Fetch filter dining tables.
     *
     * @param pageable the pagination information.
     * @param spec the filtering criteria to apply to the dining table list.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dining tables in the body.
     */
    @GetMapping("/dining-tables")
    @ApiMessage("Fetch filter dining tables")
    public ResponseEntity<ResultPaginationDTO> fetchDiningTables
        (Pageable pageable, @Filter Specification<DiningTable> spec) {
        log.debug("REST request to get dining table filter");
        return ResponseEntity.ok(this.diningTableService.fetchDiningTables(spec, pageable));
    }

    /**
     * {@code GET /dining-tables/by-restaurant} : Retrieve users associated with the current
     * dining table's restaurant.
     *
     *  This endpoint fetches dining table specific to the restaurant associated with the
     *  currently authenticated table and applies additional filtering and pagination criteria.
     *
     * @param pageable the pagination information.
     * @param spec     the filtering criteria applied to the query
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and a body containing
     *  *         the paginated list of dining tables for the restaurant.
     */
    @GetMapping("/dining-tables/by-restaurant")
    @ApiMessage("Fetch filter dining tables")
    public ResponseEntity<ResultPaginationDTO> fetchDiningTablesByRestaurant(
        Pageable pageable, @Filter Specification<DiningTable> spec
    ) throws InfoInvalidException{
        log.debug("REST request to get dining tables by restaurant");

        // get user current
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userService.fetchUserByUsername(email);
        if (currentUser == null) {
            throw new InfoInvalidException("Không tìm thấy người dùng!");
        }

        // get restaurant
        Restaurant restaurant = currentUser.getRestaurant();
        if (restaurant == null) {
            throw new InfoInvalidException("Người dùng không thuộc nhà hàng nào!");
        }

        // Create a specification to filter by restaurant id
        Specification<DiningTable> restaurantSpec = (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("restaurant").get("id"), restaurant.getId());

        // Combine specifications if needed
        Specification<DiningTable> finalSpec = spec.and(restaurantSpec);

        return ResponseEntity.ok(this.diningTableService.fetchDiningTables(finalSpec, pageable));
    }
}
