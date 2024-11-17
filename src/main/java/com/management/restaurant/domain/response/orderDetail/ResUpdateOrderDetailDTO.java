package com.management.restaurant.domain.response.orderDetail;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

import com.management.restaurant.domain.enumeration.OrderDetailStatusEnum;

@Getter
@Setter
public class ResUpdateOrderDetailDTO {
    private Long id;
    private Long quantity;
    private Double price;
    private OrderDetailStatusEnum status;

    private String lastModifiedBy;
    private Instant lastModifiedDate;

    private OrderOD order;
    private ProductOD product;

    @Setter
    @Getter
    public static class OrderOD {
        private Long id;
        private String tableName;
    }

    @Setter
    @Getter
    public static class ProductOD {
        private Long id;
        private String name;
    }
}
