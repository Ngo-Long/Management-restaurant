package com.management.restaurant.domain;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.io.Serializable;

import com.management.restaurant.domain.enumeration.PaymentMethodEnum;
import com.management.restaurant.domain.enumeration.PaymentStatusEnum;

@Table(name = "invoices")
@Getter
@Setter
@Entity
public class Invoice extends AbstractAuditingEntity<Long> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double totalAmount;
    private Double customerPaid;
    private Double returnAmount;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum method;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum status;  
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;  
	
}
