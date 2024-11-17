package com.management.restaurant.domain.response.invoice;

import java.time.Instant;

import lombok.Setter;
import lombok.Getter;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.management.restaurant.domain.enumeration.PaymentMethodEnum;
import com.management.restaurant.domain.enumeration.PaymentStatusEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResInvoiceDTO {
    private Long id;
    private Double totalAmount;
    private Double customerAmount;
    private Double returnAmount;
    private PaymentMethodEnum method;
    private PaymentStatusEnum status;

    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;

    private UserInvoice user;
    private OrderInvoice order;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInvoice {
        private Long id;
        private String name;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderInvoice {
        private Long id;
        private String tableName;
    }
}
