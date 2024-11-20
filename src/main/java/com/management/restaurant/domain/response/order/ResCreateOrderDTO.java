package com.management.restaurant.domain.response.order;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.management.restaurant.domain.enumeration.OrderOptionEnum;
import com.management.restaurant.domain.enumeration.OrderStatusEnum;

@Getter
@Setter
public class ResCreateOrderDTO {
    private Long id;
    private String note;
    private Double totalPrice;
    private OrderOptionEnum option;
    private OrderStatusEnum status;

    private String createdBy;
    private Instant createdDate;

    private UserOrder user;
    private InvoiceOrder invoice;
    private DiningTableOrder diningTable;
    private List<OrderDetailOrder> orderDetails;

    @Setter
    @Getter
    public static class UserOrder {
        private Long id;
        private String name;
    }

    @Setter
    @Getter
    public static class InvoiceOrder {
        private Long id;
        private String status;
    }

    @Setter
    @Getter
    public static class DiningTableOrder {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    public static class OrderDetailOrder {
        private Long id;
        private Long productId;
        private String productName;
        private Integer quantity;
    }
}
