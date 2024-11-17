package com.management.restaurant.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.turkraft.springfilter.boot.Filter;
import com.management.restaurant.service.OrderService;

import com.management.restaurant.domain.Order;
import com.management.restaurant.domain.response.ResultPaginationDTO;

import com.management.restaurant.util.annotation.ApiMessage;
import com.management.restaurant.util.error.InfoInvalidException;

/**
 * REST controller for managing orders.
 * This class accesses the {@link com.management.restaurant.domain.Order} entity
 */
@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * {@code POST  /orders} : Create a new order.
     *
     * @param order the order to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orders
     * or with status {@code 400 (Bad Request)}.
     */
    @PostMapping("/orders")
    @ApiMessage("Create a order")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        log.debug("REST request to save order : {}", order);

        Order dataOrder = this.orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(dataOrder);
    }

    /**
     * {@code PUT  /orders/:id} : Update an existing order.
     *
     * @param order the order to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the updated order in the body
     * or with status {@code 400 (Bad Request)}.
     * @throws InfoInvalidException if the order does not exist
     */
    @PutMapping("/orders")
    @ApiMessage("Update a order")
    public ResponseEntity<Order> updateOrder(@RequestBody Order order)
    throws InfoInvalidException {
        log.debug("REST request to update order : {}", order);

        Order dataOrder = this.orderService.updateOrder(order);
        if (dataOrder == null) {
            throw new InfoInvalidException("Đơn hàng không tồn tại");
        }

        return ResponseEntity.ok(dataOrder);
    }

    /**
     * {@code DELETE  /orders/:id} : delete the "id" order.
     *
     * @param id the id of the orders to delete.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}
     * or with status {@code 400 (Bad Request)}.
     */
    @DeleteMapping("/orders/{id}")
    @ApiMessage("Delete a order by id")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.debug("REST request to delete order by id : {}", id);

        this.orderService.deleteOrderById(id);
        return ResponseEntity.ok(null);
    }

    /**
     * {@code GET  /orders/:id} : get the "id" order.
     *
     * @param id the id of the orders to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orders,
     * or with status {@code 400 (Bad Request)}.
     */
    @GetMapping("/orders/{id}")
    @ApiMessage("Get a order by id")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) throws InfoInvalidException {
        log.debug("REST request to get order : {}", id);

        Order dataOrder = this.orderService.fetchOrderById(id);
        if (dataOrder == null) {
            throw new InfoInvalidException("Đơn hàng không tồn tại!");
        }

        return ResponseEntity.ok(dataOrder);
    }

    /**
     * {@code GET  /orders} : Fetch filter orders.
     *
     * @param pageable the pagination information.
     * @param spec the filtering criteria to apply to the order list.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orders in the body
     * or with status {@code 400 (Bad Request)}.
     */
    @GetMapping("/orders")
    @ApiMessage("Get filter orders")
    public ResponseEntity<ResultPaginationDTO> getOrders(Pageable pageable,@Filter Specification<Order> spec) {
        log.debug("REST request to get order filter");
        return ResponseEntity.ok(this.orderService.fetchOrders(spec, pageable));
    }
}
