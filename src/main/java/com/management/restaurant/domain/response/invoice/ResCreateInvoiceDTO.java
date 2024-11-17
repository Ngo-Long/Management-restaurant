package com.management.restaurant.domain.response.invoice;


import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import com.management.restaurant.domain.enumeration.PaymentMethodEnum;
import com.management.restaurant.domain.enumeration.PaymentStatusEnum;

@Getter
@Setter
public class ResCreateInvoiceDTO {
    private Long id;
    private Double totalAmount;
    private Double customerAmount;
    private Double returnAmount;
    private PaymentMethodEnum method;
    private PaymentStatusEnum status;

    private String createdBy;
    private Instant createdDate;

    private UserInvoice user;
    private OrderInvoice order;

    @Setter
    @Getter
    public static class UserInvoice {
        private Long id;
    }

    @Setter
    @Getter
    public static class OrderInvoice {
        private Long id;
    }
}
