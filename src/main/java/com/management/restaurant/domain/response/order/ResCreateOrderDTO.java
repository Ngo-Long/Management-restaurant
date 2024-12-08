package com.management.restaurant.domain.response.order;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.time.Instant;

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
}
