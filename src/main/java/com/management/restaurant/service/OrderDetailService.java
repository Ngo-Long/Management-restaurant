package com.management.restaurant.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;

import com.management.restaurant.domain.OrderDetail;
import com.management.restaurant.repository.OrderDetailRepository;
import com.management.restaurant.domain.response.ResultPaginationDTO;
import com.management.restaurant.domain.response.orderDetail.ResOrderDetailDTO;
import com.management.restaurant.domain.response.orderDetail.ResCreateOrderDetailDTO;
import com.management.restaurant.domain.response.orderDetail.ResUpdateOrderDetailDTO;

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

    /**
     * Convert a {@link OrderDetail} entity to a {@link ResOrderDetailDTO}.
     *
     * @param orderDetail the order detail entity to convert.
     * @return a {@link ResOrderDetailDTO} containing the order details.
     */
    public ResOrderDetailDTO convertToResOrderDetailDTO(OrderDetail orderDetail) {
        // response order detail DTO
        ResOrderDetailDTO res = new ResOrderDetailDTO();
        res.setId(orderDetail.getId());
        res.setQuantity(orderDetail.getQuantity());
        res.setPrice(orderDetail.getPrice());
        res.setStatus(orderDetail.getStatus());

        res.setCreatedBy(orderDetail.getCreatedBy());
        res.setCreatedDate(orderDetail.getCreatedDate());
        res.setLastModifiedBy(orderDetail.getLastModifiedBy());
        res.setLastModifiedDate(orderDetail.getLastModifiedDate());

        // response order detail - order DTO
        ResOrderDetailDTO.OrderOD order = new ResOrderDetailDTO.OrderOD();
        if (orderDetail.getOrder() != null) {
            order.setId(orderDetail.getOrder().getId());

            if (orderDetail.getOrder().getDiningTable() != null) {
                order.setTableName(orderDetail.getOrder().getDiningTable().getName());
            } else {
                order.setTableName("Mang về");
            }
            res.setOrder(order);
        }

        // response order detail - product DTO
        ResOrderDetailDTO.ProductOD product = new ResOrderDetailDTO.ProductOD();
        if (orderDetail.getProduct() != null) {
            product.setId(orderDetail.getProduct().getId());
            product.setName(orderDetail.getProduct().getName());
            res.setProduct(product);
        }

        return res;
    }

    /**
     * Convert a {@link OrderDetail} entity to a {@link ResCreateOrderDetailDTO} for order detail creation responses.
     *
     * @param orderDetail the order detail entity to convert.
     * @return a {@link ResCreateOrderDetailDTO} containing the order details for creation.
     */
    public ResCreateOrderDetailDTO convertToResCreateOrderDetailDTO(OrderDetail orderDetail) {
        // create response order detail DTO
        ResCreateOrderDetailDTO res = new ResCreateOrderDetailDTO();
        res.setId(orderDetail.getId());
        res.setQuantity(orderDetail.getQuantity());
        res.setPrice(orderDetail.getPrice());
        res.setStatus(orderDetail.getStatus());

        res.setCreatedBy(orderDetail.getCreatedBy());
        res.setCreatedDate(orderDetail.getCreatedDate());

        // create response order detail - order DTO
        ResCreateOrderDetailDTO.OrderOD orderOD = new ResCreateOrderDetailDTO.OrderOD();
        if (orderDetail.getOrder() != null) {
            orderOD.setId(orderDetail.getOrder().getId());
            res.setOrder(orderOD);
        }

        // create response order detail - product DTO
        ResCreateOrderDetailDTO.ProductOD productOD = new ResCreateOrderDetailDTO.ProductOD();
        if (orderDetail.getProduct() != null) {
            productOD.setId(orderDetail.getProduct().getId());
            res.setProduct(productOD);
        }

        return res;
    }

    /**
     * Convert a {@link OrderDetail} entity to a {@link ResUpdateOrderDetailDTO} for order detail update responses.
     *
     * @param orderDetail the order detail entity to convert.
     * @return a {@link ResUpdateOrderDetailDTO} containing the order details for updates.
     */
    public ResUpdateOrderDetailDTO convertToResUpdateOrderDetailDTO(OrderDetail orderDetail) {
        // update response order detail DTO
        ResUpdateOrderDetailDTO res = new ResUpdateOrderDetailDTO();
        res.setId(orderDetail.getId());
        res.setQuantity(orderDetail.getQuantity());
        res.setPrice(orderDetail.getPrice());
        res.setStatus(orderDetail.getStatus());

        res.setLastModifiedBy(orderDetail.getLastModifiedBy());
        res.setLastModifiedDate(orderDetail.getLastModifiedDate());

        // update response order detail - order DTO
        ResUpdateOrderDetailDTO.OrderOD order = new ResUpdateOrderDetailDTO.OrderOD();
        if (orderDetail.getOrder() != null) {
            order.setId(orderDetail.getOrder().getId());

            if (orderDetail.getOrder().getDiningTable() != null) {
                order.setTableName(orderDetail.getOrder().getDiningTable().getName());
            } else {
                order.setTableName("Mang về");
            }
            res.setOrder(order);
        }

        // update response order detail - product DTO
        ResUpdateOrderDetailDTO.ProductOD product = new ResUpdateOrderDetailDTO.ProductOD();
        if (orderDetail.getProduct() != null) {
            product.setId(orderDetail.getProduct().getId());
            product.setName(orderDetail.getProduct().getName());
            res.setProduct(product);
        }

        return res;
    }

    /**
     * Fetch a paginated list of order details based on the given search criteria and pagination information.
     *
     * @param spec the filtering criteria to apply to the order detail list.
     * @param pageable the pagination information.
     * @return a {@link ResultPaginationDTO} containing the list of order details and pagination metadata.
     */
    public ResultPaginationDTO fetchOrderDetailsDTO(Specification<OrderDetail> spec, Pageable pageable) {
        Page<OrderDetail> page = this.orderDetailRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        rs.setMeta(meta);

        // remove sensitive data
        List<ResOrderDetailDTO> dataList = page.getContent()
                .stream().map(this::convertToResOrderDetailDTO)
                .collect(Collectors.toList());

        rs.setResult(dataList);

        return rs;
    }

}
