package com.management.restaurant.domain.response.users;

import java.time.Instant;

import com.management.restaurant.domain.enumeration.GenderEnum;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResUpdateUserDTO {
	private long id;
	private String email;
	private String fullName;
	private String phone;
	private String avatar;
	private String address;
	private boolean active;
    private GenderEnum gender;
    private Instant lastModifiedDate;
}
