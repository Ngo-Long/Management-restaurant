package com.management.restaurant.domain.response.user;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import com.management.restaurant.domain.enumeration.GenderEnum;

@Setter
@Getter
public class ResCreateUserDTO {

	private Long id;
	private String fullName;
	private String email;
	private String phone;
    private GenderEnum gender;
	private String avatar;
	private String address;
	private boolean active;
    private Instant createdDate;
	private RestaurantUser restaurant;

	@Setter
	@Getter
	public static class RestaurantUser {
		private Long id;
		private String name;
	}
}
