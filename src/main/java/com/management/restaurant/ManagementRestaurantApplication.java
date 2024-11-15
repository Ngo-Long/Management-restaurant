package com.management.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
//@SpringBootApplication(exclude = {
//	org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
//	org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
//})
public class ManagementRestaurantApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManagementRestaurantApplication.class, args);
	}

}
