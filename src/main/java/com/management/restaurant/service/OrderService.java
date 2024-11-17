package com.management.restaurant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;

import com.management.restaurant.domain.Order;
import com.management.restaurant.repository.OrderRepository;
import com.management.restaurant.domain.response.ResultPaginationDTO;

/**
 * Service class for managing orders.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Order order) {
        return this.orderRepository.save(order);
    }

    public Order updateOrder(Order order) {
        Order currentOrder = this.fetchOrderById(order.getId());
        if (currentOrder == null) {
            return null;
        }

        currentOrder.setNote(order.getNote());
        currentOrder.setTotalPrice(order.getTotalPrice());
        currentOrder.setOption(order.getOption());
        currentOrder.setStatus(order.getStatus());

        return this.orderRepository.save(currentOrder);
    }

    public void deleteOrderById(Long id) {
        this.orderRepository.deleteById(id);
    }

    public Order fetchOrderById(Long id) {
        Optional<Order> order = this.orderRepository.findById(id);
        return order.orElse(null);
    }

    public ResultPaginationDTO fetchOrders(Specification<Order> spec, Pageable pageable) {
        Page<Order> pageOrder = this.orderRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageOrder.getTotalPages());
        meta.setTotal(pageOrder.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageOrder.getContent());

        return rs;
    }
}
