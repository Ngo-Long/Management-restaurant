package com.management.restaurant.domain;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.time.Instant;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;

import com.management.restaurant.domain.enumeration.GenderEnum;
import com.management.restaurant.util.SecurityUtil;

@Table(name = "users")
@Getter
@Setter
@Entity
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên người dùng không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    private String phone;
    
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;
    
    private String avatar;
    private String address; 
    private boolean active = true;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;
    
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
    
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

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
