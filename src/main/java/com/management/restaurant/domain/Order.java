package com.management.restaurant.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.io.Serializable;
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
	private double totalPrice;
	
    @Enumerated(EnumType.STRING)
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
    List<OrderDetail> orderDetails;
    
    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    @JsonIgnore
    private Invoice invoice;
}
