package com.management.restaurant.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.io.Serializable;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Table(name = "permissions")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Permission extends AbstractAuditingEntity<Long> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên không được để trống!")
    private String name;

    @NotBlank(message = "API không được để trống!")
    private String apiPath;

    @NotBlank(message = "Phương thức không được để trống!")
    private String method;

    @NotBlank(message = "Module không được để trống!")
    private String module;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions")
    @JsonIgnore
    private List<Role> roles;

    public Permission(String name, String apiPath, String method, String module) {
        this.name = name;
        this.apiPath = apiPath;
        this.method = method;
        this.module = module;
    }
}
