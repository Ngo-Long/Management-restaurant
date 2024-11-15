package com.management.restaurant.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.management.restaurant.util.SecurityUtil;
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
    
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    @JsonIgnore
    List<OrderDetail> orderDetails;
    
    @ManyToOne
    @JoinColumn(name = "dining_table_id")
    private DiningTable dining_table;
    
    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    @JsonIgnore
    private Invoice invoice; 
}
