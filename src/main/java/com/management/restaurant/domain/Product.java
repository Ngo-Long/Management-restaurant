package com.management.restaurant.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.management.restaurant.util.SecurityUtil;

@Table(name = "products")
@Entity
@Getter
@Setter
public class Product implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotNull
	@Size(message = "Tên sản phẩm không được để trống")
	private String name;
	
	@NotNull
	@DecimalMin(value = "0", inclusive = false, message = "Giá phải lớn hơn 0")
	private double price;
	
	@NotNull
	@Min(value = 1, message = "Số lượng cần lớn hơn hoặc bằng 1")
	private long quantity;
	
	private long sold;
	private String image;
	
	private String shortDesc;
	
	@Column(columnDefinition = "MEDIUMTEXT")
	private String detailDesc;	
	
	private boolean active;
	
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        this.updatedAt = Instant.now();
    }
}
