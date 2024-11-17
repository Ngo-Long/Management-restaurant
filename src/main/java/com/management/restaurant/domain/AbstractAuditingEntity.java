package com.management.restaurant.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.time.Instant;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.management.restaurant.util.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Base abstract class for entities which will hold definitions for created, last modified, created by,
 * last modified by attributes.
 */
@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate" }, allowGetters = true)
public abstract class AbstractAuditingEntity<T> implements Serializable {
	
	@Serial
    private static final long serialVersionUID = 1L;
	
	public abstract T getId();

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 50, updatable = false)
	private String createdBy;
    
    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private Instant createdDate;
    
    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;
    
    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;
    
    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().orElse("");
        this.createdDate = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.lastModifiedBy = SecurityUtil.getCurrentUserLogin().orElse("");
        this.lastModifiedDate = Instant.now();
    }
}
