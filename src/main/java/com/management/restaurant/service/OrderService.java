package com.management.restaurant.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.management.restaurant.domain.*;
import com.management.restaurant.domain.enumeration.OrderDetailStatusEnum;
import com.management.restaurant.domain.enumeration.OrderOptionEnum;
import com.management.restaurant.domain.enumeration.OrderStatusEnum;
import com.management.restaurant.repository.*;
import com.management.restaurant.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;

import com.management.restaurant.domain.response.order.ResOrderDTO;
import com.management.restaurant.domain.response.ResultPaginationDTO;
import com.management.restaurant.domain.response.order.ResCreateOrderDTO;
import com.management.restaurant.domain.response.order.ResUpdateOrderDTO;

/**
 * Service class for managing orders.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserService userService;
    private final ProductService productService;
    private final DiningTableService diningTableService;

    public OrderService(
        OrderRepository orderRepository,
        OrderDetailRepository orderDetailRepository,
        UserService userService,
        ProductService productService,
        DiningTableService diningTableService
    ) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userService = userService;
        this.productService = productService;
        this.diningTableService = diningTableService;
    }

    public Order createOrder(ResCreateOrderDTO orderDTO) {
        // create order
        Order order = new Order();
        if (orderDTO.getDiningTable() != null) {
            DiningTable diningTable = this.diningTableService.fetchDiningTableById(orderDTO.getDiningTable().getId());
            order.setDiningTable(diningTable);
            order.setOption(OrderOptionEnum.DINE_IN);
        } else {
            order.setOption(OrderOptionEnum.TAKEAWAY);
        }

        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User user = this.userService.fetchUserByUsername(email);

        order.setUser(user);
        order.setTotalPrice(0.0);
        order.setNote(orderDTO.getNote());
        order.setStatus(OrderStatusEnum.COMPLETED);
        this.orderRepository.save(order);

        // add product to order
        for (ResCreateOrderDTO.OrderDetailOrder detail : orderDTO.getOrderDetails()) {
            Product product = this.productService.fetchProductById(detail.getProductId());

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(detail.getQuantity());
            orderDetail.setPrice(product.getPrice());
            orderDetail.setStatus(OrderDetailStatusEnum.CONFIRMED);
            this.orderDetailRepository.save(orderDetail);
        }

        // update total price order
        updateTotalPrice(order.getId());

        return order;
    }

    private void updateTotalPrice(Long orderId) {
        Double totalPrice = orderDetailRepository.calculateTotalPrice(orderId);
        Order order = this.fetchOrderById(orderId);
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);
    }

    public Order updateOrder(ResUpdateOrderDTO orderDTO) {
        // update order
        Order currentOrder = this.fetchOrderById(orderDTO.getId());
        if (currentOrder == null) {
            return null;
        }

        if (orderDTO.getDiningTable() != null) {
            DiningTable diningTable = this.diningTableService.fetchDiningTableById(orderDTO.getDiningTable().getId());
            currentOrder.setDiningTable(diningTable);
        }

        currentOrder.setNote(orderDTO.getNote());
        currentOrder.setOption(orderDTO.getOption());
        currentOrder.setStatus(orderDTO.getStatus());
        this.orderRepository.save(currentOrder);

        // update product to order
        for (ResUpdateOrderDTO.OrderDetailOrder detail : orderDTO.getOrderDetails()) {
            Product product = this.productService.fetchProductById(detail.getProductId());

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(currentOrder);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(detail.getQuantity());
            orderDetail.setPrice(product.getPrice());
            orderDetail.setStatus(OrderDetailStatusEnum.CONFIRMED);
            this.orderDetailRepository.save(orderDetail);
        }

        // update total price order
        updateTotalPrice(currentOrder.getId());

        return currentOrder;
    }

    public void deleteOrderById(Long id) {
        this.orderRepository.deleteById(id);
    }

    public Order fetchOrderById(Long id) {
        Optional<Order> order = this.orderRepository.findById(id);
        return order.orElse(null);
    }

    /**
     * Convert a {@link Order} entity to a {@link ResOrderDTO}.
     *
     * @param order the order entity to convert.
     * @return a {@link ResOrderDTO} containing the order details.
     */
    public ResOrderDTO convertToResOrderDTO(Order order) {
        // response order DTO
        ResOrderDTO res = new ResOrderDTO();
        res.setId(order.getId());
        res.setNote(order.getNote());
        res.setTotalPrice(order.getTotalPrice());
        res.setOption(order.getOption());
        res.setStatus(order.getStatus());

        res.setCreatedBy(order.getCreatedBy());
        res.setCreatedDate(order.getCreatedDate());
        res.setLastModifiedBy(order.getLastModifiedBy());
        res.setLastModifiedDate(order.getLastModifiedDate());

        // response dining table - order DTO
        ResOrderDTO.DiningTableOrder tableOrder = new ResOrderDTO.DiningTableOrder();
        if (order.getDiningTable() != null) {
            tableOrder.setId(order.getDiningTable().getId());
            tableOrder.setName(order.getDiningTable().getName());
            res.setDiningTable(tableOrder);
        }

        // response user - order DTO
        ResOrderDTO.UserOrder userOrder = new ResOrderDTO.UserOrder();
        if (order.getUser() != null) {
            userOrder.setId(order.getUser().getId());
            userOrder.setName(order.getUser().getName());
            res.setUser(userOrder);
        }

        return res;
    }

    /**
     * Convert a {@link Order} entity to a {@link ResCreateOrderDTO} for order creation responses.
     *
     * @param order the order entity to convert.
     * @return a {@link ResCreateOrderDTO} containing the order details for creation.
     */
    public ResCreateOrderDTO convertToResCreateOrderDTO(Order order) {
        // create response order DTO
        ResCreateOrderDTO res = new ResCreateOrderDTO();
        res.setId(order.getId());
        res.setNote(order.getNote());
        res.setTotalPrice(order.getTotalPrice());
        res.setOption(order.getOption());
        res.setStatus(order.getStatus());

        res.setCreatedBy(order.getCreatedBy());
        res.setCreatedDate(order.getCreatedDate());

        // create response dining table - order DTO
        ResCreateOrderDTO.DiningTableOrder tableOrder = new ResCreateOrderDTO.DiningTableOrder();
        if (order.getDiningTable() != null) {
            tableOrder.setId(order.getDiningTable().getId());
            tableOrder.setName(order.getDiningTable().getName());
            res.setDiningTable(tableOrder);
        }

        // create response user - order DTO
        ResCreateOrderDTO.UserOrder userOrder = new ResCreateOrderDTO.UserOrder();
        if (order.getUser() != null) {
            userOrder.setId(order.getUser().getId());
            userOrder.setName(order.getUser().getName());
            res.setUser(userOrder);
        }

        return res;
    }

    /**
     * Convert a {@link Order} entity to a {@link ResUpdateOrderDTO} for order update responses.
     *
     * @param order the order entity to convert.
     * @return a {@link ResUpdateOrderDTO} containing the order details for updates.
     */
    public ResUpdateOrderDTO convertToResUpdateOrderDTO(Order order) {
        // update response order DTO
        ResUpdateOrderDTO res = new ResUpdateOrderDTO();
        res.setId(order.getId());
        res.setNote(order.getNote());
        res.setTotalPrice(order.getTotalPrice());
        res.setOption(order.getOption());
        res.setStatus(order.getStatus());

        res.setLastModifiedBy(order.getLastModifiedBy());
        res.setLastModifiedDate(order.getLastModifiedDate());

        // update response dining table - order DTO
        ResUpdateOrderDTO.DiningTableOrder tableOrder = new ResUpdateOrderDTO.DiningTableOrder();
        if (order.getDiningTable() != null) {
            tableOrder.setId(order.getDiningTable().getId());
            tableOrder.setName(order.getDiningTable().getName());
            res.setDiningTable(tableOrder);
        }

        // update response user - order DTO
        ResUpdateOrderDTO.UserOrder userOrder = new ResUpdateOrderDTO.UserOrder();
        if (order.getUser() != null) {
            userOrder.setId(order.getUser().getId());
            userOrder.setName(order.getUser().getName());
            res.setUser(userOrder);
        }

        return res;
    }

    /**
     * Fetch a paginated list of orders based on the given search criteria and pagination information.
     *
     * @param spec the filtering criteria to apply to the order list.
     * @param pageable the pagination information.
     * @return a {@link ResultPaginationDTO} containing the list of orders and pagination metadata.
     */
    public ResultPaginationDTO fetchOrdersDTO(Specification<Order> spec, Pageable pageable) {
        Page<Order> pageOrder = this.orderRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageOrder.getTotalPages());
        meta.setTotal(pageOrder.getTotalElements());

        rs.setMeta(meta);

        // remove sensitive data
        List<ResOrderDTO> listOrder = pageOrder.getContent()
                .stream().map(this::convertToResOrderDTO)
                .collect(Collectors.toList());

        rs.setResult(listOrder);

        return rs;
    }
}
