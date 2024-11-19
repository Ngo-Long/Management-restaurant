package com.management.restaurant.domain.response.user;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import com.management.restaurant.domain.enumeration.GenderEnum;

@Setter
@Getter
public class ResCreateUserDTO {

	private Long id;
	private String name;
	private String email;
	private Integer age;
    private GenderEnum gender;
	private String address;
    private Instant createdDate;
	private RestaurantUser restaurant;

	@Setter
	@Getter
	public static class RestaurantUser {
		private Long id;
		private String name;
	}
}
