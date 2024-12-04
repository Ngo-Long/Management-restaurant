package com.management.restaurant.domain.response.order;

public interface BaseOrderDetail {
    Long getId();
    Long getProductId();
    Integer getQuantity();
    Double getPrice();
}
