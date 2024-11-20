package com.management.restaurant.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import jakarta.persistence.*;
import com.management.restaurant.domain.enumeration.OrderDetailStatusEnum;

@Table(name = "order_detail")
@Entity
@Getter
@Setter
public class OrderDetail extends AbstractAuditingEntity<Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity = 1;
    private Double price;

    @Enumerated(EnumType.STRING)
    private OrderDetailStatusEnum status;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
