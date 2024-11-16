package com.management.restaurant.controller;

import java.util.Objects;

import jakarta.validation.Valid;

import com.turkraft.springfilter.boot.Filter;
import com.management.restaurant.domain.DiningTable;
import com.management.restaurant.domain.response.ResultPaginationDTO;

import com.management.restaurant.util.annotation.ApiMessage;
import com.management.restaurant.util.error.InfoInvalidException;
import com.management.restaurant.service.DiningTableService;

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

/**
 * REST controller for managing dining tables.
 * This class accesses the {@link com.management.restaurant.domain.DiningTable} entity
 */
@RestController
@RequestMapping("/api/v1")
public class DiningTableController {

    private final Logger log = LoggerFactory.getLogger(RestaurantController.class);

    private final DiningTableService diningTableService;

    public DiningTableController(DiningTableService diningTableService) {
        this.diningTableService = diningTableService;
    }

    /**
     * {@code POST  /dining-tables} : Create a new dining table.
     *
     * @param diningTable the dining table to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dining tables,
     *         or with status {@code 400 (Bad Request)} if the dining tables name already exists.
     * @throws InfoInvalidException if the dining table name already exists or if the input information is invalid.
     */
    @PostMapping("/dining-tables")
    @ApiMessage("Create a dining table")
    public ResponseEntity<DiningTable> createDiningTable(@Valid @RequestBody DiningTable diningTable)
            throws InfoInvalidException {
        log.debug("REST request to save dining table : {}", diningTable);

        if (this.diningTableService.isNameExist(diningTable.getName())) {
            throw new InfoInvalidException("Tên đã tồn tại, vui lòng sử dụng tên khác!");
        }

        DiningTable newDiningTable = this.diningTableService.createDiningTable(diningTable);
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

        boolean isNameExist = this.diningTableService.isNameExist(table.getName());
        if (isNameExist && !Objects.equals(currentTable.getName(), table.getName())) {
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
    public ResponseEntity<Void> deleteDiningTableById(@PathVariable("id") Long id) throws InfoInvalidException {
        log.debug("REST request to delete dining table: {}", id);

        DiningTable currentTable = this.diningTableService.fetchDiningTableById(id);
        if (currentTable == null) {
            throw new InfoInvalidException("Nhà hàng không tồn tại!");
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
    public ResponseEntity<DiningTable> fetchDiningTableById(@PathVariable("id") long id) throws InfoInvalidException {
        log.debug("REST request to get dining table : {}", id);

        DiningTable dataTable = this.diningTableService.fetchDiningTableById(id);
        if (dataTable == null) {
            throw new InfoInvalidException("Nhà hàng không tồn tại!");
        }

        return ResponseEntity.ok().body(dataTable);
    }

    /**
     * {@code GET  /dining-tables} : Fetch all dining tables.
     *
     * @param pageable the pagination information.
     * @param spec the filtering criteria to apply to the dining table list.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dining tables in the body.
     */
    @GetMapping("/dining-tables")
    @ApiMessage("Fetch all dining table")
    public ResponseEntity<ResultPaginationDTO> fetchDiningTables(
            Pageable pageable,
            @Filter Specification<DiningTable> spec
    ) {
        log.debug("REST request to get all dining table");
        return ResponseEntity.ok(this.diningTableService.fetchDiningTables(spec, pageable));
    }
}
