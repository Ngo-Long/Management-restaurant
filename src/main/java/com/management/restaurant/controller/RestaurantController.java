package com.management.restaurant.controller;

import java.util.Objects;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.management.restaurant.domain.Restaurant;
import com.management.restaurant.domain.response.ResultPaginationDTO;

import com.management.restaurant.service.RestaurantService;
import com.management.restaurant.util.annotation.ApiMessage;
import com.management.restaurant.util.error.InfoInvalidException;

/**
 * REST controller for managing restaurants.
 * This class accesses the {@link com.management.restaurant.domain.Restaurant} entity
 */
@RestController
@RequestMapping("/api/v1")
public class RestaurantController {
		
	private final Logger log = LoggerFactory.getLogger(RestaurantController.class);
	
	private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    /**
     * {@code POST  /restaurants} : Create a new restaurant.
     *
     * @param restaurant the restaurant to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new restaurants,
     *         or with status {@code 400 (Bad Request)} if the restaurants name already exists.
     * @throws InfoInvalidException if the restaurant name already exists or if the input information is invalid.
     */
    @PostMapping("/restaurants")
    @ApiMessage("Create a restaurant")
    public ResponseEntity<Restaurant> createRestaurant(@Valid @RequestBody Restaurant restaurant)
        throws InfoInvalidException {
        log.debug("REST request to save Restaurant : {}", restaurant);

        boolean isNameExits = this.restaurantService.isNameExist(restaurant.getName());
        if (isNameExits) {
            throw new InfoInvalidException("Tên đã tồn tại, vui lòng sử dụng tên khác!");
        }

        Restaurant newRestaurant = this.restaurantService.createRestaurant(restaurant);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRestaurant);
    }

    /**
     * {@code PUT  /restaurants/:id} : Update an existing restaurant.
     *
     * @param restaurant the restaurant details to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the updated restaurant in the body,
     *         or with status {@code 400 (Bad Request)} if the restaurant is invalid or if the name already exists.
     * @throws InfoInvalidException if the restaurant does not exist or if the restaurant name is already taken.
     */
    @PutMapping("/restaurants")
    @ApiMessage("Update a restaurant")
    public ResponseEntity<Restaurant> updateRestaurant(@Valid @RequestBody Restaurant restaurant) throws InfoInvalidException {
    	log.debug("REST request to update Restaurant : {}", restaurant);

        Restaurant currentRestaurant = this.restaurantService.fetchRestaurantById(restaurant.getId());
        if (currentRestaurant == null) {
        	throw new InfoInvalidException("Nhà hàng không tồn tại!");
        }

        boolean isNameExist = this.restaurantService.isNameExist(restaurant.getName());
        if (isNameExist && !Objects.equals(currentRestaurant.getName(), restaurant.getName())) {
            throw new InfoInvalidException("Tên đã tồn tại, vui lòng sử dụng tên khác!");
        }
    	
        Restaurant dataRestaurant = this.restaurantService.updateRestaurant(restaurant);
        return ResponseEntity.ok().body(dataRestaurant);
    }

    /**
     * {@code DELETE  /restaurants/:id} : delete the "id" restaurant.
     *
     * @param id the id of the restaurants to delete.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     */
    @DeleteMapping("/restaurants/{id}")
    @ApiMessage("Delete a restaurant")
    public ResponseEntity<Void> deleteRestaurantById(@PathVariable("id") Long id) throws InfoInvalidException {
    	log.debug("REST request to delete Restaurant: {}", id);
    	
        Restaurant currentRestaurant = this.restaurantService.fetchRestaurantById(id);
        if (currentRestaurant == null) {
            throw new InfoInvalidException("Nhà hàng không tồn tại!");
        }

        this.restaurantService.deleteRestaurantById(id);
        return ResponseEntity.ok(null);
    }

    /**
     * {@code GET  /restaurants/:id} : get the "id" restaurant.
     *
     * @param id the id of the restaurants to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the restaurants,
     *         or with status {@code 400 (Bad Request)}.
     */
    @GetMapping("/restaurants/{id}")
    @ApiMessage("Get restaurant by id")
    public ResponseEntity<Restaurant> fetchRestaurantById(@PathVariable("id") long id) throws InfoInvalidException {
    	log.debug("REST request to get Restaurant : {}", id);
    	
        Restaurant dataRestaurant = this.restaurantService.fetchRestaurantById(id);
        if (dataRestaurant == null) {
            throw new InfoInvalidException("Nhà hàng không tồn tại!");
        }

        return ResponseEntity.ok().body(dataRestaurant);
    }

    /**
     * {@code GET  /restaurants} : Fetch all restaurants.
     *
     * @param pageable the pagination information.
     * @param spec the filtering criteria to apply to the restaurant list.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of restaurants in the body.
     */
    @GetMapping("/restaurants")
    @ApiMessage("Fetch all restaurants")
    public ResponseEntity<ResultPaginationDTO> fetchRestaurants(Pageable pageable, @Filter Specification<Restaurant> spec) {
    	log.debug("REST request to get all Restaurant");
        return ResponseEntity.ok(this.restaurantService.fetchRestaurants(spec, pageable));
    }

}
