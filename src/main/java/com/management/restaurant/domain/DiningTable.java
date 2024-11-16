package com.management.restaurant.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    
    private int seats;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private TableEnum status;

    private boolean active = true;
    
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
    
    @OneToMany(mappedBy = "dining_table", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Order> orders;

}
