package com.management.restaurant.domain.response.user;

import com.management.restaurant.domain.enumeration.GenderEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class ResRegisterUserDTO {

	private Long id;
	private String name;
	private String email;
    private String password;
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
