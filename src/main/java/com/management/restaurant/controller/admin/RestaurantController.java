package com.management.restaurant.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.management.restaurant.domain.Restaurant;
import com.management.restaurant.domain.response.ResultPaginationDTO;
import com.management.restaurant.service.RestaurantService;
import com.management.restaurant.util.annotation.ApiMessage;
import com.management.restaurant.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class RestaurantController {
		
	private final Logger log = LoggerFactory.getLogger(RestaurantController.class);
	
	private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping("/admin/restaurants")
    @ApiMessage("Create a restaurant")
    public ResponseEntity<Restaurant> createNewRestaurant(@Valid @RequestBody Restaurant restaurant) {
    	log.debug("REST request to save Restaurant : {}", restaurant);
    	
        Restaurant newRestaurant = this.restaurantService.handleCreateRestaurant(restaurant);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRestaurant);
    }
    
    @PutMapping("/admin/restaurants")
    @ApiMessage("Update a restaurant")
    public ResponseEntity<Restaurant> updateRestaurant(@Valid @RequestBody Restaurant restaurant) throws IdInvalidException {
    	log.debug("REST request to update Restaurant : {}", restaurant);
    	
        if (this.restaurantService.fetchRestaurantById(restaurant.getId()) == null) {
        	throw new IdInvalidException("Nhà hàng không tồn tại!");
        }
    	
        Restaurant dataRestaurant = this.restaurantService.handleUpdateRestaurant(restaurant);
        return ResponseEntity.ok().body(dataRestaurant);
    }    

    @DeleteMapping("/admin/restaurants/{id}")
    @ApiMessage("Delete a restaurant")
    public ResponseEntity<String> deleteRestaurantById(@PathVariable("id") Long id) throws IdInvalidException {
    	log.debug("REST request to delete Restaurant: {}", id);
    	
        Restaurant currentRestaurant = this.restaurantService.fetchRestaurantById(id);
        if (currentRestaurant == null) {
            throw new IdInvalidException("Nhà hàng không tồn tại!");
        }

        this.restaurantService.handleDeleteRestaurant(id);
        return ResponseEntity.ok(null);
    }
    
    @GetMapping("/admin/restaurants/{id}")
    @ApiMessage("Get a restaurant")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable("id") long id) throws IdInvalidException {
    	log.debug("REST request to get Restaurant : {}", id);
    	
        Restaurant currentRestaurant = this.restaurantService.fetchRestaurantById(id);
        if (currentRestaurant == null) {
            throw new IdInvalidException("Restaurant with id = " + id + " not found!");
        }

        Restaurant dataRestaurant = this.restaurantService.fetchRestaurantById(id);
        return ResponseEntity.ok().body(dataRestaurant);
    }
    
    @GetMapping("/admin/restaurants")
    @ApiMessage("Fetch all restaurants")
    public ResponseEntity<ResultPaginationDTO> getRestaurants(Pageable pageable, @Filter Specification<Restaurant> spec) {
    	log.debug("REST request to get all Restaurant for an admin");
        return ResponseEntity.ok(this.restaurantService.handleFetchRestaurants(spec, pageable));
    }

}
