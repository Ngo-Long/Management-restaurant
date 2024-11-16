package com.management.restaurant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.management.restaurant.domain.User;
import com.management.restaurant.domain.Restaurant;
import com.management.restaurant.domain.response.ResultPaginationDTO;

import com.management.restaurant.repository.UserRepository;
import com.management.restaurant.repository.RestaurantRepository;

/**
 * Service class for managing restaurants.
 */
@Service
public class RestaurantService {
	
	private final RestaurantRepository restaurantRepository;
	private final UserRepository userRepository;
	
	public RestaurantService(RestaurantRepository restaurantRepository,UserRepository userRepository) {
		this.restaurantRepository = restaurantRepository;
		this.userRepository = userRepository;
	}
	
    public Restaurant createRestaurant(Restaurant restaurant) {
        return this.restaurantRepository.save(restaurant);
    }
    
    public Restaurant updateRestaurant(Restaurant restaurant) {
    	Restaurant currentRestaurant = this.fetchRestaurantById(restaurant.getId());
        if (currentRestaurant == null) {
            return null;
        }

        currentRestaurant.setName(restaurant.getName());
        currentRestaurant.setLogo(restaurant.getLogo());
        currentRestaurant.setAddress(restaurant.getAddress());
        currentRestaurant.setDescription(restaurant.getDescription());
        currentRestaurant.setActive(restaurant.isActive());

        return this.restaurantRepository.save(currentRestaurant);
    }

    public void deleteRestaurant(long id) {
        Optional<Restaurant> restaurantOptional = this.restaurantRepository.findById(id);
        if (restaurantOptional.isPresent()) {
            Restaurant restaurant = restaurantOptional.get();

            // fetch all user belong to this restaurant
            List<User> users = this.userRepository.findByRestaurant(restaurant);
            this.userRepository.deleteAll(users);
        }

        this.restaurantRepository.deleteById(id);
    }

    public Boolean isNameExist(String name) {
        return this.restaurantRepository.existsByName(name);
    }
    
    public List<Restaurant> fetchRestaurantByName(String name) {
        return this.restaurantRepository.findByName(name);
    }

    public Restaurant fetchRestaurantById(long id) {
        Optional<Restaurant> companyOptional = this.restaurantRepository.findById(id);
        return companyOptional.orElse(null);
    }

    public ResultPaginationDTO handleFetchRestaurants(Specification<Restaurant> spec, Pageable pageable) {
        Page<Restaurant> pageRestaurant = this.restaurantRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageRestaurant.getTotalPages());
        meta.setTotal(pageRestaurant.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageRestaurant.getContent());

        return rs;
    }
   	
}
