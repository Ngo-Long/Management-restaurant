package com.management.restaurant.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.turkraft.springfilter.boot.Filter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.management.restaurant.service.UserService;
import com.management.restaurant.service.OrderService;
import com.management.restaurant.service.DiningTableService;

import com.management.restaurant.util.SecurityUtil;
import com.management.restaurant.util.annotation.ApiMessage;
import com.management.restaurant.util.error.InfoInvalidException;

import com.management.restaurant.domain.User;
import com.management.restaurant.domain.Order;
import com.management.restaurant.domain.Restaurant;
import com.management.restaurant.domain.DiningTable;

import com.management.restaurant.domain.response.ResultPaginationDTO;
import com.management.restaurant.domain.response.order.ResOrderDTO;
import com.management.restaurant.domain.response.order.ResCreateOrderDTO;
import com.management.restaurant.domain.response.order.ResUpdateOrderDTO;

/**
 * REST controller for managing orders.
 * This class accesses the {@link com.management.restaurant.domain.Order} entity
 */
@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final UserService userService;
    private final OrderService orderService;
    private final DiningTableService diningTableService;

    public OrderController(
            OrderService orderService,
            UserService userService,
            DiningTableService diningTableService
    ) {
        this.userService = userService;
        this.orderService = orderService;
        this.diningTableService = diningTableService;
    }

    /**
     * {@code POST  /orders} : Create a new order.
     *
     * @param orderDTO the order to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orders
     * or with status {@code 400 (Bad Request)}.
     */
    @PostMapping("/orders")
    @ApiMessage("Create a order")
    public ResponseEntity<ResCreateOrderDTO> createOrder(@RequestBody ResCreateOrderDTO orderDTO) {
        log.debug("REST request to save order : {}", orderDTO);

        Order dataOrder = this.orderService.createOrder(orderDTO);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(this.orderService.convertToResCreateOrderDTO(dataOrder));
    }

    /**
     * {@code PUT  /orders/:id} : Update an existing order.
     *
     * @param orderDTO the order to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the updated order in the body
     * or with status {@code 400 (Bad Request)}.
     * @throws InfoInvalidException if the order does not exist
     */
    @PutMapping("/orders")
    @ApiMessage("Update a order")
    public ResponseEntity<ResUpdateOrderDTO> updateOrder(@RequestBody ResUpdateOrderDTO orderDTO)
        throws InfoInvalidException {
        log.debug("REST request to update order : {}", orderDTO);

        Order dataOrder = this.orderService.updateOrder(orderDTO);
        if (dataOrder == null) {
            throw new InfoInvalidException("Đơn hàng không tồn tại");
        }

        return ResponseEntity.ok(this.orderService.convertToResUpdateOrderDTO(dataOrder));
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
    public ResponseEntity<ResOrderDTO> getOrderById(@PathVariable Long id) throws InfoInvalidException {
        log.debug("REST request to get order : {}", id);

        Order dataOrder = this.orderService.fetchOrderById(id);
        if (dataOrder == null) {
            throw new InfoInvalidException("Đơn hàng không tồn tại!");
        }

        return ResponseEntity.ok(this.orderService.convertToResOrderDTO(dataOrder));
    }

    /**
     * {@code GET  /orders/by-table/:id} : get latest unpaid order by the "id" dining table.
     *
     * @param id the id of the dining table to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orders,
     * or with status {@code 400 (Bad Request)}.
     */
    @GetMapping("/orders/by-table/{id}")
    @ApiMessage("Get a latest unpaid order by dining table id")
    public ResponseEntity<ResOrderDTO> getOrderByTableId(@PathVariable Long id) throws InfoInvalidException {
        log.debug("REST request to get a latest unpaid order by dining table : {}", id);

        DiningTable diningTable = this.diningTableService.fetchDiningTableById(id);
        if (diningTable == null) {
            throw new InfoInvalidException("Bàn ăn không tồn tại!");
        }

        Order dataOrder = this.orderService.fetchUnpaidOrderByTableId(id);
        return ResponseEntity.ok(this.orderService.convertToResOrderDTO(dataOrder));
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
        return ResponseEntity.ok(this.orderService.fetchOrdersDTO(spec, pageable));
    }

    /**
     * {@code GET /orders/by-restaurant} : Retrieve users associated with the current
     *  order's restaurant.
     *
     *  This endpoint fetches dining table specific to the restaurant associated with the
     *  currently authenticated order and applies additional filtering and pagination criteria.
     *
     * @param pageable the pagination information.
     * @param spec     the filtering criteria applied to the query
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and a body containing
     *  *         the paginated list of orders for the restaurant.
     */
    @GetMapping("/orders/by-restaurant")
    @ApiMessage("Fetch filter orders by restaurant")
    public ResponseEntity<ResultPaginationDTO> getOrdersByRestaurant(
        Pageable pageable, @Filter Specification<Order> spec
    ) throws InfoInvalidException{
        log.debug("REST request to get orders by restaurant");

        // get user current
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userService.fetchUserByUsername(email);

        // get restaurant
        Restaurant restaurant = currentUser.getRestaurant();
        if (restaurant == null) throw new InfoInvalidException("Người dùng không thuộc nhà hàng nào!");

        // Create a specification to filter all order by dining table by restaurant
        Specification<Order> restaurantSpec = (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("diningTable").get("restaurant").get("id"), restaurant.getId());

        return ResponseEntity.ok(this.orderService.fetchOrdersDTO(restaurantSpec.and(spec), pageable));
    }
}
