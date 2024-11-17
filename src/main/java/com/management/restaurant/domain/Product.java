package com.management.restaurant.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import jakarta.persistence.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

@Table(name = "products")
@Entity
@Getter
@Setter
public class Product extends AbstractAuditingEntity<Long> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Size(message = "Tên sản phẩm không được để trống")
	private String name;
	
	@NotNull
	@DecimalMin(value = "0", inclusive = false, message = "Giá phải lớn hơn 0")
	private Double price;
	
	@NotNull
	@Min(value = 1, message = "Số lượng cần lớn hơn hoặc bằng 1")
	private Integer quantity;
	
	private Double sold;
	private String image;
	
	private String shortDesc;
	
	@Column(columnDefinition = "MEDIUMTEXT")
	private String detailDesc;	
	
	private boolean active = true;
}
