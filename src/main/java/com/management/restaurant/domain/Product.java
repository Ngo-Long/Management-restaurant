package com.management.restaurant.domain;

import java.io.Serializable;
import jakarta.persistence.*;
import com.management.restaurant.domain.enumeration.ProductCategoryEnum;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

@Table(name = "products")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends AbstractAuditingEntity<Long> implements Serializable {

	private static final long serialVersionUID = 1L;

    public Product(String name, Double sellingPrice, Double costPrice,
                   ProductCategoryEnum category, String unit,
                   Integer quantity, Restaurant restaurant) {
        this.name = name;
        this.sellingPrice = sellingPrice;
        this.costPrice = costPrice;
        this.category = category;
        this.unit = unit;
        this.quantity = quantity;
        this.restaurant = restaurant;
    }

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Size(message = "Tên sản phẩm không được để trống")
	private String name;

    @DecimalMin(value = "0", inclusive = true, message = "Giá phải lớn hơn hoặc bằng 0")
    private Double sellingPrice = 0.0;

    @DecimalMin(value = "0", inclusive = true, message = "Giá phải lớn hơn hoặc bằng 0")
    private Double costPrice = 0.0;

    @NotNull(message = "Tên loại không được để trống")
    private ProductCategoryEnum category;

    @Size(message = "Tên đơn vị không được để trống")
    private String unit;

    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private Integer quantity = 0;

	private Double sold;
	private String image;
	private String shortDesc;

	@Column(columnDefinition = "MEDIUMTEXT")
	private String detailDesc;

	private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}
