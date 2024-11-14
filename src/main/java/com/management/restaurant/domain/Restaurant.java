package com.management.restaurant.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.management.restaurant.domain.enumeration.GenderEnum;
import com.management.restaurant.util.SecurityUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Table(name = "restaurants")
@Getter
@Setter
@Entity
public class Restaurant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên nhà hàng không được để trống")
    private String name;

    private String logo;
    private String address;
    
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
   
    private boolean active = true;
    
    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
    @JsonIgnore
    List<User> users;

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
