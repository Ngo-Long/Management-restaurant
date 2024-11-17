package com.management.restaurant.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;

import com.management.restaurant.domain.OrderDetail;
import com.management.restaurant.repository.OrderDetailRepository;
import com.management.restaurant.domain.response.ResultPaginationDTO;

/**
 * Service class for managing order details.
 */
@Service
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    public OrderDetailService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    public OrderDetail createOrderDetail(OrderDetail orderDetail) {
        return this.orderDetailRepository.save(orderDetail);
    }

    public OrderDetail updateOrderDetail(OrderDetail orderDetail) {
        OrderDetail orderDetailDB = this.getOrderDetailById(orderDetail.getId());
        if (orderDetailDB == null) {
            return null;
        }

        orderDetailDB.setQuantity(orderDetail.getQuantity());
        orderDetailDB.setPrice(orderDetail.getPrice());
        orderDetailDB.setStatus(orderDetail.getStatus());

        return this.orderDetailRepository.save(orderDetail);
    }

    public void deleteOrderDetailById(Long id) {
        this.orderDetailRepository.deleteById(id);
    }

    public OrderDetail getOrderDetailById(Long id) {
        Optional<OrderDetail> orderDetail = this.orderDetailRepository.findById(id);
        return orderDetail.orElse(null);
    }

    public ResultPaginationDTO fetchOrderDetails(Specification<OrderDetail> spec, Pageable pageable) {
        Page<OrderDetail> page = this.orderDetailRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(page.getContent());

        return rs;
    }
}
