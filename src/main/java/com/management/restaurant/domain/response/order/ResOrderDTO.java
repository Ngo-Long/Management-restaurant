package com.management.restaurant.domain.response.order;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.management.restaurant.domain.enumeration.OrderOptionEnum;
import com.management.restaurant.domain.enumeration.OrderStatusEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResOrderDTO {
    private long id;
    private String note;
    private Double totalPrice;
    private OrderOptionEnum option;
    private OrderStatusEnum status;

    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;

    private UserOrder user;
    private DiningTableOrder diningTable;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserOrder {
        private long id;
        private String name;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiningTableOrder {
        private long id;
        private String name;
    }
}
