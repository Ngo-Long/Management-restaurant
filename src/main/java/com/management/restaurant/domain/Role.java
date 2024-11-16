package com.management.restaurant.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.io.Serializable;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.management.restaurant.util.SecurityUtil;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.JoinTable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;


@Table(name = "roles")
@Entity
@Getter
@Setter
public class Role extends AbstractAuditingEntity<Long> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên không được để trống!")
    private String name;

    private String description;
    private boolean active;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    List<User> users;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "roles" })
    @JoinTable(name = "permission_role", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private List<Permission> permissions;
}
