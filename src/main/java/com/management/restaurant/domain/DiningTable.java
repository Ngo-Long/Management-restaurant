package com.management.restaurant.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.io.Serializable;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.management.restaurant.domain.enumeration.TableEnum;

@Table(name = "dining_tables")
@Entity
@Setter
@Getter
public class DiningTable extends AbstractAuditingEntity<Long> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên bàn không được để trống!")
    private String name;

    @NotBlank(message = "Vị trí không được để trống!")
    private String location;
    
    private Integer seats;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private TableEnum status;

    private boolean active = true;
    
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
    
    @OneToMany(mappedBy = "diningTable", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Order> orders;

}
