package com.management.restaurant.domain.response.user;

import java.time.Instant;

import com.management.restaurant.domain.enumeration.GenderEnum;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResUpdateUserDTO {
	private Long id;
	private String email;
	private String fullName;
	private String phone;
	private String avatar;
	private String address;
	private boolean active;
    private GenderEnum gender;
    private Instant lastModifiedDate;
	private RestaurantUser restaurant;

	@Setter
	@Getter
	public static class RestaurantUser {
		private Long id;
		private String name;
	}
}
