package com.management.restaurant.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.management.restaurant.repository.*;
import com.management.restaurant.util.SecurityUtil;

import com.management.restaurant.domain.*;
import com.management.restaurant.domain.enumeration.TableEnum;
import com.management.restaurant.domain.enumeration.OrderOptionEnum;
import com.management.restaurant.domain.enumeration.OrderStatusEnum;

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
    private final UserService userService;
    private final DiningTableService diningTableService;

    public OrderService(
        OrderRepository orderRepository,
        UserService userService,
        DiningTableService diningTableService
    ) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.diningTableService = diningTableService;
    }

    public Order createOrder(ResCreateOrderDTO orderDTO) {
        // create new order
        Order order = new Order();

        // set table order
        DiningTable diningTable = this.diningTableService.fetchDiningTableById(
            orderDTO.getDiningTable().getId());
        setTableStatus(diningTable, orderDTO.getStatus());
        order.setDiningTable(diningTable);
        order.setOption(diningTable != null ? OrderOptionEnum.DINE_IN : OrderOptionEnum.TAKEAWAY);

        // set user order
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User user = this.userService.fetchUserByUsername(email);

        order.setUser(user);
        order.setNote(orderDTO.getNote());
        order.setStatus(orderDTO.getStatus());

        return this.orderRepository.save(order);
    }

    public Order updateOrder(ResUpdateOrderDTO orderDTO) {
        // update order
        Order currentOrder = this.fetchOrderById(orderDTO.getId());
        if (currentOrder == null)  return null;

        // set table order
        DiningTable diningTable = this.diningTableService.fetchDiningTableById(
            orderDTO.getDiningTable().getId()
        );
        currentOrder.setDiningTable(diningTable);
        setTableStatus(diningTable, orderDTO.getStatus());

        currentOrder.setNote(orderDTO.getNote());
        currentOrder.setStatus(orderDTO.getStatus());

        return this.orderRepository.save(currentOrder);
    }

    private void setTableStatus(DiningTable diningTable, OrderStatusEnum status) {
        if (diningTable == null) return;

        if (status == OrderStatusEnum.PENDING) {
            diningTable.setStatus(TableEnum.OCCUPIED);
        } else if (status == OrderStatusEnum.COMPLETED) {
            diningTable.setStatus(TableEnum.OCCUPIED);
        } else if (status == OrderStatusEnum.PAID) {
            diningTable.setStatus(TableEnum.AVAILABLE);
        }

        this.diningTableService.updateDiningTable(diningTable);
    }

    public void deleteOrderById(Long id) {
        Order currentOrder = this.fetchOrderById(id);
        if (currentOrder == null) return;

        DiningTable diningTable = this.diningTableService.fetchDiningTableById(
            currentOrder.getDiningTable().getId()
        );
        if (diningTable != null) {
            diningTable.setStatus(TableEnum.AVAILABLE);
            this.diningTableService.updateDiningTable(diningTable);
        };

        this.orderRepository.deleteById(id);
    }

    public Order fetchOrderById(Long id) {
        Optional<Order> order = this.orderRepository.findById(id);
        return order.orElse(null);
    }

    public Order fetchUnpaidOrderByTableId(Long id) {
        Optional<Order> dataOrder = this.orderRepository.findLatestUnpaidOrderByTableId(id);
        return dataOrder.orElse(null);
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
