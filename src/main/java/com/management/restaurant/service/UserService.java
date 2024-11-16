package com.management.restaurant.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.management.restaurant.domain.User;
import com.management.restaurant.domain.Restaurant;
import com.management.restaurant.domain.response.users.ResUserDTO;
import com.management.restaurant.domain.response.ResultPaginationDTO;
import com.management.restaurant.domain.response.users.ResCreateUserDTO;
import com.management.restaurant.domain.response.users.ResUpdateUserDTO;

import com.management.restaurant.repository.UserRepository;

/**
 * Service class for managing users.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RestaurantService restaurantService;

    public UserService(UserRepository userRepository,RestaurantService restaurantService) {
        this.userRepository = userRepository;
        this.restaurantService = restaurantService;
    }

    public User createUser(User dataUser) {
        if (dataUser.getRestaurant() != null) {
        	Restaurant dataRestaurant = this.restaurantService.fetchRestaurantById(dataUser.getRestaurant().getId());            
            dataUser.setRestaurant(dataRestaurant != null ? dataRestaurant : null);
        }
        
        return this.userRepository.save(dataUser);
    }
    
    public User updateUser(User dataUser) {
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

    public void updateUserToken(String email, String token) {
        User currentUser = this.fetchUserByUsername(email);
        if (currentUser == null) {
            return;
        }

        currentUser.setRefreshToken(token);
        this.userRepository.save(currentUser);
    }

    public void deleteUser(Long id) {
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

    public User fetchUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    /**
     * Convert a {@link User} entity to a {@link ResUserDTO}.
     *
     * @param user the user entity to convert.
     * @return a {@link ResUserDTO} containing the user details.
     */
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
        res.setCreatedDate(user.getCreatedDate());
        res.setLastModifiedDate(user.getLastModifiedDate());

        return res;
    }

    /**
     * Convert a {@link User} entity to a {@link ResCreateUserDTO} for user creation responses.
     *
     * @param user the user entity to convert.
     * @return a {@link ResCreateUserDTO} containing the user details for creation.
     */
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
        res.setCreatedDate(user.getCreatedDate());
       
        return res;
    }

    /**
     * Convert a {@link User} entity to a {@link ResUpdateUserDTO} for user update responses.
     *
     * @param user the user entity to convert.
     * @return a {@link ResUpdateUserDTO} containing the user details for updates.
     */
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
        res.setLastModifiedDate(user.getLastModifiedDate());
       
        return res;
    }

    /**
     * Fetch a paginated list of users based on the given search criteria and pagination information.
     *
     * @param spec the filtering criteria to apply to the user list.
     * @param pageable the pagination information.
     * @return a {@link ResultPaginationDTO} containing the list of users and pagination metadata.
     */
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
