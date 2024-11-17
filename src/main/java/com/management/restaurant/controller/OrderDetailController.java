package com.management.restaurant.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.turkraft.springfilter.boot.Filter;
import com.management.restaurant.service.OrderDetailService;

import com.management.restaurant.domain.OrderDetail;
import com.management.restaurant.domain.response.ResultPaginationDTO;

import com.management.restaurant.util.annotation.ApiMessage;
import com.management.restaurant.util.error.InfoInvalidException;

/**
 * REST controller for managing order details.
 * This class accesses the {@link OrderDetail} entity
 */
@RestController
@RequestMapping("/api/v1")
public class OrderDetailController {

    private final Logger log = LoggerFactory.getLogger(OrderDetailController.class);

    private final OrderDetailService orderDetailService;

    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    /**
     * {@code POST  /order-details} : Create a new order detail.
     *
     * @param orderDetail the order detail to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new order details
     * or with status {@code 400 (Bad Request)}.
     */
    @PostMapping("/order-details")
    @ApiMessage("Create a order detail")
    public ResponseEntity<OrderDetail> createOrderDetail(@RequestBody OrderDetail orderDetail) {
        log.debug("REST request to save order detail : {}", orderDetail);

        OrderDetail dataOrderDetail = this.orderDetailService.createOrderDetail(orderDetail);
        return ResponseEntity.status(HttpStatus.CREATED).body(dataOrderDetail);
    }

    /**
     * {@code PUT  /order-details/:id} : Update an existing order detail.
     *
     * @param orderDetail the order details to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the updated order detail in the body
     * or with status {@code 400 (Bad Request)}.
     * @throws InfoInvalidException if the order detail does not exist
     */
    @PutMapping("/order-details")
    @ApiMessage("Update a order detail")
    public ResponseEntity<OrderDetail> updateOrderDetail(@RequestBody OrderDetail orderDetail)
            throws InfoInvalidException {
        log.debug("REST request to update order detail : {}", orderDetail);

        OrderDetail dataOrderDetail = this.orderDetailService.updateOrderDetail(orderDetail);
        if (dataOrderDetail == null) {
            throw new InfoInvalidException("Đơn hàng chi tiết không tồn tại");
        }

        return ResponseEntity.ok(dataOrderDetail);
    }

    /**
     * {@code DELETE  /order-details/:id} : delete the "id" order detail.
     *
     * @param id the id of the order details to delete.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}
     * or with status {@code 400 (Bad Request)}.
     */
    @DeleteMapping("/order-details/{id}")
    @ApiMessage("Delete a order detail by id")
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable Long id) {
        log.debug("REST request to delete order detail by id : {}", id);

        this.orderDetailService.deleteOrderDetailById(id);
        return ResponseEntity.ok(null);
    }

    /**
     * {@code GET  /order-details/:id} : get the "id" order detail.
     *
     * @param id the id of the order details to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orders,
     * or with status {@code 400 (Bad Request)}.
     */
    @GetMapping("/order-details/{id}")
    @ApiMessage("Get a order detail by id")
    public ResponseEntity<OrderDetail> getOrderDetailById(@PathVariable Long id) throws InfoInvalidException {
        log.debug("REST request to get order detail : {}", id);

        OrderDetail dataOrder = this.orderDetailService.getOrderDetailById(id);
        if (dataOrder == null) {
            throw new InfoInvalidException("Chi tiết đơn hàng không tồn tại!");
        }

        return ResponseEntity.ok(dataOrder);
    }

    /**
     * {@code GET  /order-details} : Fetch filter order details.
     *
     * @param pageable the pagination information.
     * @param spec the filtering criteria to apply to the order detail list.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of order details in the body
     * or with status {@code 400 (Bad Request)}.
     */
    @GetMapping("/order-details")
    @ApiMessage("Get filter order details")
    public ResponseEntity<ResultPaginationDTO> getOrderDetails(Pageable pageable,@Filter Specification<OrderDetail> spec) {
        log.debug("REST request to get order filter");
        return ResponseEntity.ok(this.orderDetailService.fetchOrderDetails(spec, pageable));
    }
}
