package com.management.restaurant.domain.response.order;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.management.restaurant.domain.enumeration.OrderOptionEnum;
import com.management.restaurant.domain.enumeration.OrderStatusEnum;

@Getter
@Setter
public class ResUpdateOrderDTO {
    private long id;
    private String note;
    private Double totalPrice;
    private OrderOptionEnum option;
    private OrderStatusEnum status;

    private String lastModifiedBy;
    private Instant lastModifiedDate;

    private UserOrder user;
    private DiningTableOrder diningTable;
    private List<OrderDetailOrder> orderDetails;

    @Setter
    @Getter
    public static class UserOrder {
        private long id;
        private String name;
    }

    @Setter
    @Getter
    public static class DiningTableOrder {
        private long id;
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
