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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;

import com.management.restaurant.util.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.management.restaurant.domain.enumeration.TableEnum;

@Table(name = "dining_tables")
@Entity
@Getter
@Setter
public class DiningTable implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Tên bàn không được để trống!")
    private String name;

    @NotBlank(message = "Vị trí không được để trống!")
    private String location;
    
    private int seats;

    @Enumerated(EnumType.STRING)
    private TableEnum status;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private Instant startDate;
    private Instant endDate;
    private boolean active;
    
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
    
    @OneToMany(mappedBy = "dining_table", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Order> orders;

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
