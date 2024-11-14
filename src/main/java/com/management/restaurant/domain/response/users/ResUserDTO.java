package com.management.restaurant.domain.response.users;



import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

import com.management.restaurant.domain.enumeration.GenderEnum;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResUserDTO {
	private long id;
	private String email;
	private String fullName;
	private String phone;
	private String avatar;
	private String address;
	private boolean active;
    private GenderEnum gender;
    private Instant updatedAt;
    private Instant createdAt;
}
