package com.management.restaurant.domain.response.orderDetail;


import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import com.management.restaurant.domain.enumeration.OrderDetailStatusEnum;

@Getter
@Setter
public class ResCreateOrderDetailDTO {
    private long id;
    private long quantity;
    private Double price;
    private OrderDetailStatusEnum status;

    private String createdBy;
    private Instant createdDate;

    private OrderOD order;
    private ProductOD product;

    @Setter
    @Getter
    public static class OrderOD {
        private long id;
    }

    @Setter
    @Getter
    public static class ProductOD {
        private long id;
    }
}