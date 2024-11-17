package com.management.restaurant.domain.response.orderDetail;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

import com.management.restaurant.domain.enumeration.OrderDetailStatusEnum;

@Getter
@Setter
public class ResUpdateOrderDetailDTO {
    private long id;
    private long quantity;
    private Double price;
    private OrderDetailStatusEnum status;

    private String lastModifiedBy;
    private Instant lastModifiedDate;

    private OrderOD order;
    private ProductOD product;

    @Setter
    @Getter
    public static class OrderOD {
        private long id;
        private String tableName;
    }

    @Setter
    @Getter
    public static class ProductOD {
        private long id;
        private String name;
    }
}
