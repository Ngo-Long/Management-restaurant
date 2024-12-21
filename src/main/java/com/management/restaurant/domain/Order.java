package com.management.restaurant.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.management.restaurant.domain.enumeration.OrderOptionEnum;
import com.management.restaurant.domain.enumeration.OrderStatusEnum;


@Table(name = "orders")
@Entity
@Getter
@Setter
public class Order extends AbstractAuditingEntity<Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	private String note;
	private Double totalPrice = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "`option`")
    private OrderOptionEnum option;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "dining_table_id")
    private DiningTable diningTable;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<OrderDetail> orderDetails = new HashSet<>();

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    @JsonIgnore
    private Invoice invoice;
}
