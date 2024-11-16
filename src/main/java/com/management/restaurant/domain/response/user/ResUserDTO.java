package com.management.restaurant.domain.response.user;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

import com.management.restaurant.domain.enumeration.GenderEnum;

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
    private Instant createdDate;
    private Instant lastModifiedDate;

	private RoleUser role;
	private RestaurantUser restaurant;

	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RoleUser {
		private long id;
		private String name;
	}

	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RestaurantUser {
		private long id;
		private String name;
	}
}
