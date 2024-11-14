package com.management.restaurant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.management.restaurant.domain.Restaurant;
import com.management.restaurant.domain.User;
import com.management.restaurant.domain.response.ResultPaginationDTO;
import com.management.restaurant.repository.RestaurantRepository;
import com.management.restaurant.repository.UserRepository;

@Service
public class RestaurantService {
	
	private final RestaurantRepository restaurantRepository;
	private final UserRepository userRepository;
	
	public RestaurantService(RestaurantRepository restaurantRepository,UserRepository userRepository) {
		this.restaurantRepository = restaurantRepository;
		this.userRepository = userRepository;
	}
	
    public Restaurant handleCreateRestaurant(Restaurant restaurant) {
        return this.restaurantRepository.save(restaurant);
    }
    
    public Restaurant handleUpdateRestaurant(Restaurant restaurant) {
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
    
    public Restaurant fetchRestaurantById(long id) {
        Optional<Restaurant> companyOptional = this.restaurantRepository.findById(id);
        if (companyOptional.isPresent()) {
            return companyOptional.get();
        }

        return null;
    }

    
    public List<Restaurant> fetchRestaurantByName(String name) {
        return this.restaurantRepository.findByName(name);
    }

    public void handleDeleteRestaurant(long id) {
        Optional<Restaurant> resOptional = this.restaurantRepository.findById(id);
        if (resOptional.isPresent()) {
        	Restaurant restaurant = resOptional.get();

            // fetch all user belong to this restaurant
            List<User> users = this.userRepository.findByRestaurant(restaurant);
            this.userRepository.deleteAll(users);
        }

        this.restaurantRepository.deleteById(id);
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

    public Optional<Restaurant> fetchRestaurantOptionalById(long id) {
        Optional<Restaurant> companyOptional = this.restaurantRepository.findById(id);
        if (companyOptional.isPresent()) {
            return companyOptional;
        }

        return null;
    }
   	
}
