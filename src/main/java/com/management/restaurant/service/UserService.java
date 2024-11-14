package com.management.restaurant.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.management.restaurant.domain.Restaurant;
import com.management.restaurant.domain.User;
import com.management.restaurant.domain.response.ResultPaginationDTO;
import com.management.restaurant.domain.response.users.ResCreateUserDTO;
import com.management.restaurant.domain.response.users.ResUpdateUserDTO;
import com.management.restaurant.domain.response.users.ResUserDTO;
import com.management.restaurant.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RestaurantService restaurantService;

    public UserService(UserRepository userRepository,RestaurantService restaurantService) {
        this.userRepository = userRepository;
        this.restaurantService = restaurantService;
    }

    public User create(User dataUser) {
        if (dataUser.getRestaurant() != null) {
        	Restaurant dataRestaurant = this.restaurantService.fetchRestaurantById(dataUser.getRestaurant().getId());            
            dataUser.setRestaurant(dataRestaurant != null ? dataRestaurant : null);
        }
        
        return this.userRepository.save(dataUser);
    }
    
    public User update(User dataUser) {
    	User currentUser = this.fetchUserById(dataUser.getId());
        if (currentUser == null) {
            return null;
        }
        
        currentUser.setFullName(dataUser.getFullName());
        currentUser.setPhone(dataUser.getPhone());
        currentUser.setGender(dataUser.getGender());
        currentUser.setAvatar(dataUser.getAvatar());
        currentUser.setAddress(dataUser.getAddress());
        currentUser.setActive(dataUser.isActive());
        
        return this.userRepository.save(currentUser);
    }

    public void delete(Long id) {
        this.userRepository.deleteById(id);
    }
    
    public Boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }
    
    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }

        return null;
    }
    
    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setFullName(user.getFullName());        
        res.setPhone(user.getPhone());
        res.setGender(user.getGender());
        res.setAvatar(user.getAvatar());
        res.setAddress(user.getAddress());
        res.setActive(user.isActive());
        res.setCreatedAt(user.getCreatedAt());
       
        return res;
    }
    
    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
    	ResUpdateUserDTO res = new ResUpdateUserDTO();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setFullName(user.getFullName());
        res.setPhone(user.getPhone());
        res.setGender(user.getGender());
        res.setAvatar(user.getAvatar());        
        res.setAddress(user.getAddress());
        res.setActive(user.isActive());
        res.setUpdatedAt(user.getUpdatedAt());
       
        return res;
    }
    
    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setFullName(user.getFullName());
        res.setPhone(user.getPhone());      
        res.setGender(user.getGender());
        res.setAvatar(user.getAvatar());
        res.setAddress(user.getAddress());
        res.setActive(user.isActive());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());        

        return res;
    }
    
    public ResultPaginationDTO handleFetchUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        rs.setMeta(meta);

        // remove sensitive data
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listUser);

        return rs;
    }

}
