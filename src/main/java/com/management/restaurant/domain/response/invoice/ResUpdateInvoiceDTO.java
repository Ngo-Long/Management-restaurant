package com.management.restaurant.domain.response.invoice;

import java.time.Instant;

import lombok.Setter;
import lombok.Getter;

import com.management.restaurant.domain.enumeration.PaymentMethodEnum;
import com.management.restaurant.domain.enumeration.PaymentStatusEnum;

@Getter
@Setter
public class ResUpdateInvoiceDTO {
    private Long id;
    private Double totalAmount;
    private Double customerAmount;
    private Double returnAmount;
    private PaymentMethodEnum method;
    private PaymentStatusEnum status;

    private String lastModifiedBy;
    private Instant lastModifiedDate;

    private UserInvoice user;
    private OrderInvoice order;

    @Setter
    @Getter
    public static class UserInvoice {
        private Long id;
        private String name;
    }

    @Setter
    @Getter
    public static class OrderInvoice {
        private Long id;
        private String tableName;
    }
}
