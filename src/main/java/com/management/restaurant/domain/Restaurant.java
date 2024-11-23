package com.management.restaurant.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.io.Serializable;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Table(name = "restaurants")
@Getter
@Setter
@Entity
public class Restaurant extends AbstractAuditingEntity<Long> implements Serializable {

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

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Product> products;

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
    @JsonIgnore
    List<DiningTable> dining_tables;
}
