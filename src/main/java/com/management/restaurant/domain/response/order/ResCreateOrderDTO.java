package com.management.restaurant.domain.response.order;

import java.time.Instant;

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
    private DiningTableOrder diningTable;

    @Setter
    @Getter
    public static class UserOrder {
        private Long id;
        private String name;
    }

    @Setter
    @Getter
    public static class DiningTableOrder {
        private Long id;
        private String name;
    }


}
