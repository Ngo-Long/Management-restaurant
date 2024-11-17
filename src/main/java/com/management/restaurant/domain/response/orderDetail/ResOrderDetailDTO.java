package com.management.restaurant.domain.response.orderDetail;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.management.restaurant.domain.enumeration.OrderDetailStatusEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResOrderDetailDTO {
    private Long id;
    private Long quantity;
    private Double price;
    private OrderDetailStatusEnum status;

    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;

    private OrderOD order;
    private ProductOD product;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderOD {
        private Long id;
        private String tableName;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductOD {
        private Long id;
        private String name;
    }
}
